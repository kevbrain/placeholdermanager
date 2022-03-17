package com.its4u.beans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.its4u.gitops.GitController;
import com.its4u.gitops.Parser;
import com.its4u.models.ArgoAppStatus;
import com.its4u.models.ArgoEnvironment;
import com.its4u.models.Environments;
import com.its4u.models.PlaceHolderId;
import com.its4u.models.PlaceHolderSpec;
import com.its4u.models.PlaceHolders;
import com.its4u.models.Project;
import com.its4u.models.Versions;
import com.its4u.models.templates.TemplateModel;
import com.its4u.services.ArgoService;
import com.its4u.services.EnvironmentService;
import com.its4u.services.OcpExplorerService;
import com.its4u.services.ProjectService;
import com.its4u.utils.TemplateGenerator;

import freemarker.template.TemplateException;
import lombok.Data;
@ViewScoped
@Data
@Component
public class PlaceHoldersView {

	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private ArgoService argoService;
	
	@Autowired
	private EnvironmentService environmentService;
	
	@Autowired
	private ArgoInitializerBean argoInitialier;
	
	@Autowired
	private PollView pollView;

	private OcpExplorerService ocp;
	
	private HashMap<String,Project> myProjects;
	
	private Project selectedProject;
	
	private String selectedProjectId;
	
	private Environments selectedEnvironment;
	
	private ArgoAppStatus appStatus;
	
	private Map<String,String> tags;
	
	private Map<String,String> versions;
	
	private HashMap<String,HashMap<String,String>> projectMap;
	
	private HashMap<String,HashMap<String,String>> envplaceHolders;
	
	private boolean applicationSelected;
	
	private int tabindex;
	
	
	

	
	@PostConstruct
    public void init()  {
		refresh();
    }
	
	public void refresh() {
		
		applicationSelected=false;
		myProjects = projectService.findAll();		
		for (Project proj:myProjects.values()) {			
			projectService.enrichProject(proj);			
			//proj.setAppstatus(appStatus);
			//proj.setSynchrone(appStatus.getSync().equalsIgnoreCase("synced"));
		}				
		argoInitialier.setNewEnv(false);
		//refreshStatus();
						
	}
	
	public void loadDetailApp(Project projet) {
		selectedProject=projet;
		applicationSelected=true;
		onSelectedProject(projet.getProject_Id());
		selectedProjectId=projet.getProject_Id();
	}
	
	public void loadDetailEnv(Project projet,String envId) {
		selectedProject=projet;
		applicationSelected=true;
		onSelectedProject(projet.getProject_Id());
		selectedProjectId=projet.getProject_Id();
		selectedEnvironment = environmentService.getEnvById(envId);
		
		projet.getEnvSelectedMap().put(envId, true);
		
		if (envId.equalsIgnoreCase("dev")) {
			tabindex=0;
		} else if (envId.equalsIgnoreCase("tst")) {
			tabindex=1;
		} else if (envId.equalsIgnoreCase("int")) {
			tabindex=2;
		}
		System.out.println("environement loaded = "+selectedEnvironment.getEnvironment());
	}

	public void closeArgoDetails() {
		argoInitialier.setNewEnv(false);
	}
	
		
	public void save(Environments env) {

		pollView.log("Save project on DataBase");
		projectService.createProject(selectedProject);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Project saved"));
		pollView.log("Update GitOps");
		
	}
	
	public void deletePlaceHolder(PlaceHolders pl,Environments env) {
		
		projectService.deletePlaceHolder(pl);
		env.getPlaceholders().remove(pl);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "PlaceHolder "+pl.getPlaceHolderId().getKey()+" deleted"));
	}
	
	public void undeployEnv(Environments env) {
		
		String envsuffix = env.getEnvironment().substring(env.getEnvironment().length() - 3);
		projectService.deleteGitOpsApps(env);				
		projectService.synchronize(env);
		
		projectService.undeployGitOpsArgo(env);
		projectService.synchronizeClusterConfig(envsuffix, env.getArgoEnvId());
		pollView.log("Project undeloyed");
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Project undeployed"));
	}
	
	public void deleteProject(Project project) {
		System.out.println("Delete Project "+project.getProject_Id());
		for (Environments env:project.getEnvironments()) {
			System.out.println("Delete env "+env.getEnvironment());
			try {
				deleteEnvironment(env);
			} catch (Exception e) {
				
			}
		}
		projectService.deleteProject(project);
		//refresh();
	}
	
	
	public void deleteEnvironment(Environments env) {
								
		String envsuffix = env.getEnvironment().substring(env.getEnvironment().length() - 3);
		
		System.out.println("Delete GitOpsApps.....");
		projectService.deleteGitOpsApps(env);				
		projectService.synchronize(env);
		System.out.println("Wait 3s ....");
    	try {
    		TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("Delete GitOps.....");
    	projectService.deleteGitOpsArgo(env);
    	projectService.synchronizeClusterConfig(envsuffix, env.getArgoEnvId());
    	//System.out.println("Delete Argo Application.....");
    	//projectService.deleteArgoApplication(env.getProjectId(), envsuffix);
				
    	if (!envsuffix.equalsIgnoreCase("dev")) {
	    	env.getProject().getEnvironments().remove(env);
	    	projectService.createProject(env.getProject());
    	}
    	
		pollView.log("Environment "+env.getEnvironment()+" deleted");
		System.out.println("Environment "+env.getEnvironment()+" deleted");
		refresh();
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Project deleted"));
	}
	
	public void searchForNewPlaceHolders() {
		
		
		System.out.println("Start Git Clone "+selectedProject.getProject_Id()+" project...");
		
		Git gitapp = projectService.cloneGitApp(selectedProject);
		String pathWorkingGitAppProject = GitController.getRepoPath(gitapp);
		
		System.out.println("->" + pathWorkingGitAppProject);
		
			
		
		for (Environments env:selectedProject.getEnvironments()) {
			System.out.println("Search for new PlaceHolders.. in "+pathWorkingGitAppProject+"/src/main/jkube/"+env.getEnvironment());
			HashMap<String,String> placeholders = new HashMap<String, String>();
			try {
				Files.walk(Paths.get(pathWorkingGitAppProject+"/src/main/jkube/"+env.getEnvironment()))
				.filter(Files::isRegularFile)
				.forEach(path -> placeholders.putAll(Parser.parserAllPlaceHolders(path)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			HashMap<String, String> keyValues = new HashMap<String, String>();
			for (PlaceHolders pl:env.getPlaceholders()) {
				keyValues.put(pl.getPlaceHolderId().getKey(),pl.getValue());
			}
			List<PlaceHolders> newPlaceHolders = new ArrayList<>();
			for (String key:placeholders.keySet()) {
				if (keyValues.get(key)==null) {
					pollView.log("New PlaceHolder Dectected : ["+key+"]");
					System.out.println("New PlaceHolder Dectected : ["+key+"]");					
					newPlaceHolders.add(new PlaceHolders(new PlaceHolderId(env.getEnvironment(),key),env,"",""));
				}
			}
			env.setNewPlaceholders(newPlaceHolders);
		}
		
		this.tags = new HashMap<String, String>();
		for (String tag:GitController.searchTagsGitApps(gitapp)) {
			this.tags.put(tag,tag);
		}
		
		this.versions = new HashMap<String, String>();
		//List<Versions> vers = projectService.getVersionsByProject(selectedProject.getProject_Id());
		
		selectedProject = projectService.findProject(selectedProjectId);
		List<Versions> vers = selectedProject.getVersions();
		for (Versions ver:vers) {
			versions.put(ver.getVersionsid().getVersion(), ver.getVersionsid().getVersion());
		}
	}
	
	public void addNewPlaceHolder(Environments env,PlaceHolders pl,boolean secret) {
	
		env.getNewPlaceholders().remove(pl);
		if (secret) pl.setType("secret");
		env.getPlaceholders().add(pl);
		
		
	}
	
	public void onSelectedProject(String projectId) {
		
		selectedProject=myProjects.get(projectId);
		searchForNewPlaceHolders();				
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Project loaded"));
						
	}
	

	
	public void refreshStatusProject() {
		if (selectedProject !=null && selectedProjectId!=null) {
			//appStatus = argoInitialier.statusAndHealth(selectedProject.getProject_Id());
		}
	}
	
	public void synchronise(Environments env) {
		save(env);
		String envsuffix = env.getEnvironment().substring(env.getEnvironment().length() - 3);
		
		if (envsuffix.equalsIgnoreCase("dev")) {
			
			projectService.updateGitOps(env);
			projectService.synchronizeClusterConfig(envsuffix, env.getArgoEnvId());
			
			
		} else {
			
			projectService.updateGitOpsOnlyArgoApplication(env);
			projectService.synchronizeClusterConfig(envsuffix, env.getArgoEnvId());
			
					
		}
		int timeSleep=30;
		if (envsuffix.equalsIgnoreCase("prd")) timeSleep=60;
		
		try {
			System.out.println("Wait "+timeSleep+"s for (Cluster Upgrade ) ....");
    		TimeUnit.SECONDS.sleep(timeSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Update GitopsApps ....");
		projectService.updateGitOpsApp(env);
		System.out.println("Synchronize App ....");
		projectService.synchronize(env.getEnvironment(),env);	
		
	}
	
	public void promote(Project project,String envId) {
		
		
		Environments sourceenv = environmentService.getEnvById(project.getMapenvs().get(envId));

		Environments destinationEnv = projectService.promote(sourceenv);			
		projectService.enrichProject(project);
		
		// synchronise cluster ( destination env)
		String destEnvsuffix = destinationEnv.getEnvironment().substring(destinationEnv.getEnvironment().length() - 3);
		projectService.updateGitOpsOnlyArgoApplication(destinationEnv);
		projectService.synchronizeClusterConfig(destEnvsuffix, destinationEnv.getArgoEnvId());
		
		// prepare skopeoModel
		
		System.out.println("create skopeo model");
		System.out.println(sourceenv.getPlaceHoldersMap().get("ocp-namespace"));
		System.out.println(destinationEnv.getPlaceHoldersMap().get("ocp-namespace"));
		
		TemplateModel skopeoModel =  new TemplateModel();
		skopeoModel.setAppName(project.getProject_Id());
		skopeoModel.setSrcNamespace(sourceenv.getPlaceHoldersMap().get("ocp-namespace"));
		skopeoModel.setDestNamespace(destinationEnv.getPlaceHoldersMap().get("ocp-namespace"));
		skopeoModel.setSrcClusterSuffix(sourceenv.getPlaceHoldersMap().get("cluster-suffix"));
		skopeoModel.setDestClusterSuffix(destinationEnv.getPlaceHoldersMap().get("cluster-suffix"));
		skopeoModel.setAppVersion(sourceenv.getPlaceHoldersMap().get("app-version"));
		
		// promote image container
		projectService.skopeoCopy(skopeoModel);
		
		refresh(); 
		
	}
	
	
	public List<Project> getProjectList() {
		return new ArrayList(myProjects.values());
	}
	
	public void refreshStatus() {
		System.out.println("Refresh status function called");
		Runnable runnable = new RefreshStatusTreatement(myProjects,projectService);
		Thread thread = new Thread(runnable);
		thread.start();

	}
	
	

}
