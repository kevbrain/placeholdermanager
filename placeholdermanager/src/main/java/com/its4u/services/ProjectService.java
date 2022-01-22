package com.its4u.services;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.its4u.models.ArgoAppStatus;
import com.its4u.models.Project;

public interface ProjectService {
	
	Project createProject(Project project);
	
	HashMap<String,Project> findAll();
	
	Project findProject(String projectName);
	
	String synchronize(String projectName);
	
	ArgoAppStatus statusAndHealth(String projectName);
	
	void updateGitOps(Project project);

	String cloneGitApp(Project project);
	
	String applyConf(String projectName);
	
}
