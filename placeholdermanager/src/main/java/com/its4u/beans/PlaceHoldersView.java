package com.its4u.beans;

import java.io.File;
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
import org.springframework.util.FileSystemUtils;

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
		refresh();
    }
	
	public void refresh() {
		myProjects = projectService.findAll();
		
	}
	
	public void save() {
		System.out.println("Save project");
		projectService.createProject(selectedProject);
		updateGitOps();
	}
	
	public void searchForNewPlaceHolders() {
		System.out.println("Search for new PlaceHolders...");
		String pathWorkingGitAppProject = cloneGitApp()+"/"+selectedProject.getProject_Id();
		
		
		for (Environments env:selectedProject.getEnvironments()) {
			HashMap<String,String> placeholders = new HashMap<String, String>();
			try {
				Files.walk(Paths.get(pathWorkingGitAppProject+"/src/main/jkube/"+env.getEnvironment()))
				.filter(Files::isRegularFile)
				.forEach(path -> placeholders.putAll(Parser.parserAllPlaceHolders(path)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HashMap<String, String> keyValues = new HashMap<String, String>();
			for (PlaceHolders pl:env.getPlaceholders()) {
				keyValues.put(pl.getPlaceHolderId().getKey(),pl.getValue());
			}
			List<PlaceHolders> newPlaceHolders = new ArrayList<>();
			for (String key:placeholders.keySet()) {
				if (keyValues.get(key)==null) {
					System.out.println("New PlaceHolder Dectected : ["+key+"]");					
					newPlaceHolders.add(new PlaceHolders(new PlaceHolderId(env.getEnvironment(),key),env,"",""));
				}
			}
			env.setNewPlaceholders(newPlaceHolders);
		}	
	}
	
	public String cloneGitApp() {
		String pathWorkingGitApp = null;
		try {
			pathWorkingGitApp = GitController.loadGitApps(selectedProject.getProject_Id());
			System.out.println("Git App project cloned on "+pathWorkingGitApp);
		} catch (IllegalStateException | GitAPIException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return pathWorkingGitApp;
	}
	
	public String cloneGitOps() {
		String pathWorkkingGitOps = null;
		try {
			pathWorkkingGitOps = GitController.loadGitOpsApps();
		} catch (IllegalStateException | GitAPIException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return pathWorkkingGitOps;
	}
	
	
	public void updateGitOps() {
		
		// clone gitops	
		String pathWorkkingGitOpsProject = cloneGitOps()+"/"+selectedProject.getProject_Id();					
		String pathWorkingGitAppProject = cloneGitApp()+"/"+selectedProject.getProject_Id();
		
		//HashMap<String,HashMap<String,String>> placesH = new HashMap<String, HashMap<String, String>>();
		for (Environments env:selectedProject.getEnvironments()) {
			HashMap<String, String> keyValues = new HashMap<String, String>();
			for (PlaceHolders pl:env.getPlaceholders()) {
				keyValues.put(pl.getPlaceHolderId().getKey(),pl.getValue());
			}
			updateGitopsPerEnvironment(pathWorkkingGitOpsProject,pathWorkingGitAppProject,env.getEnvironment(),keyValues);
		}
			
		// commit and push
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
		
	public void updateGitopsPerEnvironment(String pathWorkkingGitOps,String pathWorkingGitApp,String keyenv, HashMap<String,String> placesH) {
		String pathWorkkingGitOpsEnv = pathWorkkingGitOps+"/jkube/"+keyenv;
		String pathWorkingGitAppEnv = pathWorkingGitApp+"/src/main/jkube/"+keyenv;
		try {
			
			System.out.println("Sync resource between "+pathWorkingGitAppEnv+ " and "+pathWorkkingGitOpsEnv);
			
			Path source = Paths.get(pathWorkingGitAppEnv);
			Path dest = Paths.get(pathWorkkingGitOpsEnv);
			Files.walkFileTree(source, new CopyDir(source, dest));	
			System.out.println("Replace PlaceHolders by values");
			
			Files.walk(Paths.get(pathWorkkingGitOpsEnv))
	        .filter(Files::isRegularFile)
	        .forEach(path -> Parser.parser(path,placesH));
			
		
		} catch (Exception e) {
			System.out.println("Unable to synchronize...");
		}
			
	}
	
	public void onSelectedProject(String projectId) {
		
		System.out.println("Selected project : "+projectId);
		selectedProject=myProjects.get(projectId);
		searchForNewPlaceHolders();
				
	}
	

}
