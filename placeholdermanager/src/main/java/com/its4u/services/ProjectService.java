package com.its4u.services;

import java.util.HashMap;
import java.util.List;

import com.its4u.models.ArgoAppStatus;
import com.its4u.models.Environments;
import com.its4u.models.PlaceHolderSpec;
import com.its4u.models.PlaceHolders;
import com.its4u.models.Project;
import com.its4u.models.Versions;

public interface ProjectService {
	
	Project createProject(Project project);
	
	void deleteProject(Project project);
	
	HashMap<String,Project> findAll();
	
	Project findProject(String projectName);
	
	String synchronize(Environments env);
	
	String synchronize(String projectName,String env);
	
	String synchronize(String projectName,Environments env);
	
	ArgoAppStatus statusAndHealth(String projectName,Environments env);
	
	void updateGitOpsApp(Environments env);

	String cloneGitApp(Project project);
	
	String applyConf(String projectName,String env);
	
	void deletePlaceHolder(PlaceHolders placeHolder);
	
	String deleteArgoApplication(String projectName,String env);

	Project createVersion(String projectName, String version);

	List<Versions> getVersionsByProject(String projectId);

	String getDevVersion(String projectName);

	HashMap<String, String> createMapEnvironment(Project project);

	HashMap<String, PlaceHolderSpec> createMapPlaceHoldersFromEnv(Environments env);

	void enrichProject(Project project);

	String cloneGitOpsApps(Environments env);

	String cloneGitOps(Environments env);

	void promote(Environments env);

	void updateArgoApplication(Environments env);

	String synchronizeClusterConfig(String env, String argoEnvid);

	void delete(Environments env);
	
}
