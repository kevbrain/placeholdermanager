package com.its4u.beans;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

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
import com.its4u.services.ArgoService;
import com.its4u.services.EnvironmentService;
import com.its4u.services.OcpExplorerService;
import com.its4u.services.ProjectService;

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
						
	}
	
	public void loadDetailApp(Project projet) {
		selectedProject=projet;
		applicationSelected=true;
		onSelectedProject(projet.getProject_Id());
		selectedProjectId=projet.getProject_Id();
	}
	

	public void closeArgoDetails() {
		argoInitialier.setNewEnv(false);
	}
	
		
	public void save(Environments env) {

		pollView.log("Save project on DataBase");
		projectService.createProject(selectedProject);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Project saved"));
		
		pollView.log("Update GitOps");
		projectService.updateGitOps(env);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Git Synchronized"));
	}
	
	public void deletePlaceHolder(PlaceHolders pl,Environments env) {
		
		projectService.deletePlaceHolder(pl);
		env.getPlaceholders().remove(pl);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "PlaceHolder "+pl.getPlaceHolderId().getKey()+" deleted"));
	}
	
	public void deleteProject() {
		System.out.println("Delete project "+selectedProject.getProject_Id());
		projectService.deleteProject(selectedProject);
		selectedProject = null;
		pollView.log("Project deleted");
		refresh();
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Project deleted"));
	}
	
	public void searchForNewPlaceHolders() {
		
		
		pollView.log("Start Git Clone "+selectedProject.getProject_Id()+" project...");
		
		String pathWorkingGitAppProject = projectService.cloneGitApp(selectedProject)+"/"+selectedProject.getProject_Id();
			
		pollView.log("Search for new PlaceHolders...");
		for (Environments env:selectedProject.getEnvironments()) {
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
		for (String tag:GitController.searchTagsGitApps()) {
			this.tags.put(tag,tag);
		}
		
		this.versions = new HashMap<String, String>();
		List<Versions> vers = projectService.getVersionsByProject(selectedProject.getProject_Id());
		for (Versions ver:vers) {
			versions.put(ver.getVersion(), ver.getVersion());
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
		//appStatus = argoInitialier.statusAndHealth(selectedProject.getProject_Id());
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Project loaded"));
						
	}
	

	
	public void refreshStatusProject() {
		if (selectedProject !=null && selectedProjectId!=null) {
			//appStatus = argoInitialier.statusAndHealth(selectedProject.getProject_Id());
		}
	}
	
	public void synchronise(Environments env) {
		save(env);
		argoInitialier.synchronise(env);
	}
	
	public void promote(Environments env) {
		System.out.println("¨Promote "+env.getEnvironment()+" to another environment");
		String envsuffix = env.getEnvironment().substring(env.getEnvironment().length() - 3);
		System.out.println("Environment to promote = "+envsuffix);
		String destinationEnvironment=null;
		if (envsuffix.equalsIgnoreCase("dev")) {
			destinationEnvironment= "tst";
		}
		if (envsuffix.equalsIgnoreCase("tst")) {
			destinationEnvironment= "int";
		}
		String  iddestinationEnvironment = selectedProject.getMapenvs().get(destinationEnvironment);
		Environments destinationEnv = environmentService.getEnvById(iddestinationEnvironment);
		
		System.out.println("Destination Environment = "+destinationEnv.getEnvironment());
		
		destinationEnv = mergePlaceHolders(env, destinationEnv);
		environmentService.save(destinationEnv);
		
		refresh();
		
	}
	
	public Environments mergePlaceHolders(Environments envSource,Environments envDest) {
		HashMap<String,PlaceHolderSpec> keyplaceHolderSource = selectedProject.getMapPlaceHoldersByEnv().get(envSource.getEnvironment());
		HashMap<String,PlaceHolderSpec> keyplaceHolderDest = selectedProject.getMapPlaceHoldersByEnv().get(envDest.getEnvironment());
		
		List<PlaceHolders> plholDest = envDest.getPlaceholders();
		if (plholDest==null) {
			plholDest = new ArrayList<PlaceHolders>();
		}
		
		for (String keySource : keyplaceHolderSource.keySet()) {
			if (keyplaceHolderDest!=null && keyplaceHolderDest.get(keySource)!=null) {
				// key already exists
			} else {
				PlaceHolderId plId = new PlaceHolderId(envDest.getEnvironment(), keySource);
				PlaceHolders pl = new PlaceHolders(plId,envDest,keyplaceHolderSource.get(keySource).getValue(),keyplaceHolderSource.get(keySource).getType());
				plholDest.add(pl);
			}
		}
		envDest.setPlaceholders(plholDest);
		return envDest;
		
	}
	
	public List<Project> getProjectList() {
		return new ArrayList(myProjects.values());
	}

}
