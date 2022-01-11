package com.its4u.beans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.its4u.gitops.CopyDir;
import com.its4u.gitops.GitController;
import com.its4u.gitops.Parser;
import com.its4u.models.Environments;
import com.its4u.models.PlaceHolderId;
import com.its4u.models.PlaceHolders;
import com.its4u.models.Project;
import com.its4u.services.OcpExplorerService;
import com.its4u.services.ProjectService;

import lombok.Data;
@ViewScoped
@Data
@Component
public class PlaceHoldersView {

	@Autowired
	private ProjectService projectService;

	private OcpExplorerService ocp;
	
	private HashMap<String,Project> myProjects;
	
	private Project selectedProject;
	
	private String selectedProjectId;
	
	@PostConstruct
    public void init()  {
		//ocp = new OcpExplorerService("sha256~MnctYrw8Vxm812WpkdY4a6LwwYpHZuSRfq-EaH1ZMw0");
		refresh();
    }
	
	public void refresh() {
		myProjects = projectService.findAll();
		
	}
	
	public void save() {
		System.out.println("Save project");
		projectService.createProject(selectedProject);
	}
	
	public void updateGitOps() {
		HashMap<String,HashMap<String,String>> placesH = new HashMap<String, HashMap<String, String>>();
		for (Environments env:selectedProject.getEnvironments()) {
			HashMap<String, String> keyValues = new HashMap<String, String>();
			for (PlaceHolders pl:env.getPlaceholders()) {
				keyValues.put(pl.getPlaceHolderId().getKey(),pl.getValue());
			}
			updateGitopsPerEnvironment(env.getEnvironment(),keyValues);
		}
			
		
		try {
			GitController.commitAndPush();
		} catch (NoFilepatternException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
		
	public void updateGitopsPerEnvironment(String keyenv, HashMap<String,String> placesH) {
		String pathWorkkingGitOps;
		try {
			pathWorkkingGitOps = GitController.loadGitOpsApps(selectedProject.getProject_Id())+"\\jkube\\"+keyenv;
			String pathWorkingGitApp = GitController.loadGitApps(selectedProject.getProject_Id())+"\\src\\main\\jkube\\"+keyenv;
			
			Path source = Paths.get(pathWorkingGitApp);
			Path dest = Paths.get(pathWorkkingGitOps);
			Files.walkFileTree(source, new CopyDir(source, dest));					
			Files.walk(Paths.get(pathWorkkingGitOps))
	        .filter(Files::isRegularFile)
	        .forEach(path -> Parser.parser(path,placesH));
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	public void onSelectedProject(String projectId) {
		
		System.out.println("Selected project : "+projectId);
		selectedProject=myProjects.get(projectId);
		
		
	}
	
	public void createProject() {
		//ocp.loadProject();
		String projectName = "boston";
		Project project = new Project(projectName, null, "Kevyn");
		List<Environments> envs =  new ArrayList<>();
		
		Environments dev = new Environments(project,projectName+"-dev");
		Environments tst = new Environments(project,projectName+"-tst");
		Environments inte = new Environments(project,projectName+"-int");
				
		dev.setPlaceholders(createplaceHolders(dev));
		tst.setPlaceholders(createplaceHolders(tst));
		inte.setPlaceholders(createplaceHolders(inte));
		
		envs.add(dev);
		envs.add(tst);
		envs.add(inte);
				
		project.setEnvironments(envs);
		projectService.createProject(project);
		
		/*
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(project);
			System.out.println(json);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	
	public List<PlaceHolders> createplaceHolders(Environments env) {
		List<PlaceHolders> placeHolders = new ArrayList<>();
		placeHolders.add(new PlaceHolders(new PlaceHolderId(env.getEnvironment(),"welcome.message"),env,"Hello",""));
		placeHolders.add(new PlaceHolders(new PlaceHolderId(env.getEnvironment(),"ocp.replicas"),env,"2",""));
		placeHolders.add(new PlaceHolders(new PlaceHolderId(env.getEnvironment(),"application.title"),env,"My application",""));
		placeHolders.add(new PlaceHolders(new PlaceHolderId(env.getEnvironment(),"database.password"),env,"systempassword","secret"));
		return placeHolders;
	}
}
