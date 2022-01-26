package com.its4u.beans;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.its4u.gitops.GitController;
import com.its4u.gitops.Parser;
import com.its4u.models.ArgoAppStatus;
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
	
	private HashMap<String,String> tags;
	
	
	@PostConstruct
    public void init()  {
		refresh();
    }
	
	public void refresh() {
		myProjects = projectService.findAll();
		
	}
	
	public void save() {

		pollView.log("Save project on DataBase");
		projectService.createProject(selectedProject);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Project saved"));
		
		pollView.log("Update GitOps");
		projectService.updateGitOps(selectedProject);
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
	}
	
	public void addNewPlaceHolder(Environments env,PlaceHolders pl,boolean secret) {
	
		env.getNewPlaceholders().remove(pl);
		if (secret) pl.setType("secret");
		env.getPlaceholders().add(pl);
		
		
	}
	
	public void onSelectedProject(String projectId) {
		
		selectedProject=myProjects.get(projectId);
		searchForNewPlaceHolders();
		appStatus = argoInitialier.statusAndHealth(selectedProject.getProject_Id());
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Project loaded"));
						
	}
	
	public void refreshStatusProject() {
		if (selectedProject !=null && selectedProjectId!=null) {
			appStatus = argoInitialier.statusAndHealth(selectedProject.getProject_Id());
		}
	}
	
	public void synchronise(String project) {
		save();
		argoInitialier.synchronise(project);
	}
	
	public void promote(Environments env) {
		System.out.println("¨Promote "+env.getEnvironment()+" to another environment");
	}
	
	

}
