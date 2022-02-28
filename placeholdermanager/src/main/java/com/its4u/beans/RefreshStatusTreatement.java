package com.its4u.beans;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.its4u.models.ArgoAppStatus;
import com.its4u.models.Environments;
import com.its4u.models.Project;
import com.its4u.services.ProjectService;

public class RefreshStatusTreatement implements Runnable{

	
	private ProjectService projectService;
	
	private HashMap<String,Project> myProjects; 
			
	
	public RefreshStatusTreatement(HashMap<String, Project> myProjects,ProjectService projectService) {
		super();
		this.myProjects = myProjects;
		this.projectService = projectService;
	}


	@Override
	public void run() {
		for (Project proj:myProjects.values()) {
			
			for (Environments env:proj.getEnvironments()) {	
				try {
					
					ArgoAppStatus envstatus = projectService.statusAndHealth(proj.getProject_Id(), env);
					if (envstatus!=null) {
						
						proj.getMapappstatusByEnv().put(env.getEnvironment(), envstatus);
					} else {
						proj.getMapappstatusByEnv().put(env.getEnvironment(), new ArgoAppStatus("Unknow", "Unknow"));
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
