package com.its4u.services.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.its4u.gitops.CopyDir;
import com.its4u.gitops.GitController;
import com.its4u.gitops.Parser;
import com.its4u.models.ArgoAppStatus;
import com.its4u.models.ArgoAuthToken;
import com.its4u.models.ArgoResource;
import com.its4u.models.Environments;
import com.its4u.models.PlaceHolderId;
import com.its4u.models.PlaceHolders;
import com.its4u.models.Project;
import com.its4u.models.Versions;
import com.its4u.repositories.EnvironmentRepository;
import com.its4u.repositories.PlaceHoldersRepository;
import com.its4u.repositories.ProjectRepository;
import com.its4u.repositories.VersionRepository;
import com.its4u.services.ProjectService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectRepository repository;
	
	@Autowired
	private PlaceHoldersRepository placeHolderRepository;
	
	@Autowired 
	private EnvironmentRepository environmentRepository;
	
	@Autowired
	private VersionRepository versionRepository;
	
	@Override
	public Project createProject(Project project) {
		
		return repository.save(project);
	}

	@Override
	public HashMap<String,Project> findAll() {
		HashMap<String,Project> projects = new HashMap<String, Project>();
		for (Project proj:repository.findAll()) {
			projects.put(proj.getProject_Id(), proj);
		}
		return projects;
	}
	
	@Override
	public Project findProject(String projectName) {
		return findAll().get(projectName);
	}

	@Override
	public String synchronize(String projectName) {
		
		String responseArgo="";
		Unirest.setTimeouts(0, 0);
		try {
			HttpResponse<String> response = Unirest.post("https://openshift-gitops-server-openshift-gitops.apps.ocp-lab.its4u.eu/api/v1/applications/"+projectName+"/sync")
			  .header("Authorization", "Bearer "+getToken())
			  .body("{\r\n  \"dryRun\": false\r\n\r\n}")
			  .asString();		
			responseArgo = response.getBody();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseArgo;
	}
	
	public String deleteArgoApplication(String projectName) {
		String responseArgo="";
		Unirest.setTimeouts(0, 0);
		try {
			HttpResponse<String> response = Unirest.delete("https://openshift-gitops-server-openshift-gitops.apps.ocp-lab.its4u.eu/api/v1/applications/"+projectName+"?cascade=true")
			  .header("Authorization", "Bearer "+getToken())
			  .body("{\r\n  \"dryRun\": false\r\n\r\n}")
			  .asString();		
			responseArgo = response.getBody();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseArgo;
	}
	
	public String getToken() {		
	
		String argoPassword = System.getenv("argo.password");
		String argoUser = System.getenv("argo.user");
		String argoServer = System.getenv("argo.server");
		String token="";
	    String command = "curl -k -H \"Accept: application/json\"  -X POST -d {\"password\":\""+argoPassword+"\",\"username\":\""+argoUser+"\"} "+argoServer+"/api/v1/session";
	    	   
	    try
	    {
	    		Process process = Runtime.getRuntime().exec(command);
	    		InputStream is = process.getInputStream();
	     	    	   	    
	    	    BufferedReader br = new BufferedReader(new InputStreamReader((is)));

	    		String readline;	    			    		
	    		while ((readline = br.readLine()) != null) {
	    			if (readline.startsWith("{\"token\":")) break;
	    		}	    		
	    		
	    		ObjectMapper objectMapper = new ObjectMapper();
	    		ArgoAuthToken argoAuthToken = objectMapper.readValue(readline, ArgoAuthToken.class);
	    		token = argoAuthToken.getToken();	
	    		process.destroy();
	    		
	    }
	    catch (Exception e)
	    {   
	        e.printStackTrace();
	    }
		      
		return token;
	}

	@Override
	public ArgoAppStatus statusAndHealth(String projectName) {
		Unirest.setTimeouts(0, 0);
		ArgoAppStatus argoAppStatus = null;
		try {
			HttpResponse<JsonNode> response = Unirest.get("https://openshift-gitops-server-openshift-gitops.apps.ocp-lab.its4u.eu/api/v1/applications/"+projectName+"?refresh=true")
			  .header("Authorization", "Bearer "+getToken())			  
			  .asJson();
						
			try {
					JsonNode princ = response.getBody();
					JSONObject jsonObject = princ.getObject();
					String sync = jsonObject.getJSONObject("status").getJSONObject("sync").getString("status");
					String healthy = jsonObject.getJSONObject("status").getJSONObject("health").getString("status");	
					argoAppStatus = new  ArgoAppStatus(sync, healthy);
					
					if (jsonObject.getJSONObject("status")!=null && jsonObject.getJSONObject("status").getString("reconciledAt")!=null) {
						argoAppStatus.setReconcileAt(jsonObject.getJSONObject("status").getString("reconciledAt"));
					}
					if (jsonObject.getJSONObject("status")!=null && jsonObject.getJSONObject("status").getJSONObject("operationState")!=null) {
						JSONArray  resources = jsonObject.getJSONObject("status").getJSONObject("operationState").getJSONObject("syncResult").getJSONArray("resources");
						ObjectMapper objectMapper = new ObjectMapper();
						List<ArgoResource> argoResources= new ArrayList<ArgoResource>();
						for (Object resobj:resources) {
							JSONObject resJson = (JSONObject) resobj;				
							try {
								ArgoResource argoResource = objectMapper.readValue(resobj.toString(), ArgoResource.class);
								argoResources.add(argoResource);
							} catch (JsonMappingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JsonProcessingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						argoAppStatus.setArgoResources(argoResources);
						//System.out.println(argoResources);
					}
			} catch (Exception e) {
				// nothing
			}
														
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return argoAppStatus;
		}
	}
	
	public void updateGitOps(Project project) {
		
		// clone gitops	
		String pathWorkkingGitOpsProject = cloneGitOps()+"/"+project.getProject_Id();					
		//String pathWorkingGitAppProject = cloneGitApp(project)+"/"+project.getProject_Id();
		String pathWorkingGitAppProject = cloneGitApp(project);
		
		//HashMap<String,HashMap<String,String>> placesH = new HashMap<String, HashMap<String, String>>();
		for (Environments env:project.getEnvironments()) {
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
	
	@Override
	public String cloneGitApp(Project project) {
		String pathWorkingGitApp = null;
		try {
			pathWorkingGitApp = GitController.loadGitApps(project.getProject_Id());			
			System.out.println("Git App project cloned on "+pathWorkingGitApp);
		} catch (IllegalStateException | GitAPIException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return pathWorkingGitApp;
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

	@Override
	public String applyConf(String projectName) {
		// TODO Auto-generated method stub
		Project project= findProject(projectName);
		updateGitOps(project);
		synchronize("cluster-configs");
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return synchronize(projectName);
	}

	@Override
	public void deleteProject(Project project) {
		System.out.println("Delete project "+project.getProject_Id());		
		repository.delete(project);
		
	}

	@Override
	public void deletePlaceHolder(PlaceHolders placeHolder) {
		
		Optional<PlaceHolders> placeHolderToDelete = placeHolderRepository.findById(placeHolder.getPlaceHolderId());		
		placeHolderRepository.delete(placeHolderToDelete.get());
		
	}
	
	public void deleteEnvironment(Environments env) {
		Optional<Environments> envToDelete = environmentRepository.findById(env.getEnvironment());
		environmentRepository.delete(envToDelete.get());
	}

	@Override
	public Project createVersion(String projectName, String version) {
		Project proj = findProject(projectName);
		List<Versions> versions;
		if (proj.getVersions()== null) {
			versions = new ArrayList<Versions>();
			proj.setVersions(versions);
		} else {
			versions = proj.getVersions();
		}
		versions.add(new Versions(version,proj.getProject_Id()));
		return createProject(proj);
		
	}
	
	@Override
	public List<Versions> getVersionsByProject(String projectId) {
		return versionRepository.findAllByProject(projectId);
	}
}
