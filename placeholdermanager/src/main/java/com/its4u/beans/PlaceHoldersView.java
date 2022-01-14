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
	
	public void updateGitOps() {
		String pathWorkkingGitOps = null;
		// clone gitops
		try {
			pathWorkkingGitOps = GitController.loadGitOpsApps();
			System.out.println("Git project cloned");
		} catch (IllegalStateException | GitAPIException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		HashMap<String,HashMap<String,String>> placesH = new HashMap<String, HashMap<String, String>>();
		for (Environments env:selectedProject.getEnvironments()) {
			HashMap<String, String> keyValues = new HashMap<String, String>();
			for (PlaceHolders pl:env.getPlaceholders()) {
				keyValues.put(pl.getPlaceHolderId().getKey(),pl.getValue());
			}
			updateGitopsPerEnvironment(pathWorkkingGitOps,env.getEnvironment(),keyValues);
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
		
	public void updateGitopsPerEnvironment(String pathWorkkingGitOps,String keyenv, HashMap<String,String> placesH) {
		String pathWorkkingGitOpsEnv = pathWorkkingGitOps+"\\jkube\\"+selectedProject.getProject_Id()+"-"+keyenv;
		try {
			
			String pathWorkingGitApp = GitController.loadGitApps(selectedProject.getProject_Id())
					+"\\src\\main\\jkube\\"+keyenv;
			
			System.out.println("Sync resource between "+pathWorkingGitApp+ " and "+pathWorkkingGitOps);
			
			Path source = Paths.get(pathWorkingGitApp);
			Path dest = Paths.get(pathWorkkingGitOps);
			Files.walkFileTree(source, new CopyDir(source, dest));	
			System.out.println("Replace PlaceHolders by values");
			
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
	

}
