package com.its4u.services;

import java.util.HashMap;
import java.util.List;

import com.its4u.models.ArgoAppStatus;
import com.its4u.models.Environments;
import com.its4u.models.PlaceHolders;
import com.its4u.models.Project;
import com.its4u.models.Versions;

public interface ProjectService {
	
	Project createProject(Project project);
	
	void deleteProject(Project project);
	
	HashMap<String,Project> findAll();
	
	Project findProject(String projectName);
	
	String synchronize(String projectName);
	
	ArgoAppStatus statusAndHealth(String projectName);
	
	void updateGitOps(Project project);

	String cloneGitApp(Project project);
	
	String applyConf(String projectName);
	
	void deletePlaceHolder(PlaceHolders placeHolder);
	
	String deleteArgoApplication(String projectName);

	Project createVersion(String projectName, String version);

	List<Versions> getVersionsByProject(String projectId);

	String getDevVersion(String projectName);

	HashMap<String, String> createMapEnvironment(Project project);

	HashMap<String, String> createMapPlaceHoldersFromEnv(Environments env);

	void enrichProject(Project project);
	
}
