package com.its4u.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RmCommand;
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
import com.its4u.gitops.ListFilesDir;
import com.its4u.gitops.Parser;
import com.its4u.models.ArgoAppStatus;
import com.its4u.models.ArgoAuthToken;
import com.its4u.models.ArgoEnvironment;
import com.its4u.models.ArgoResource;
import com.its4u.models.Environments;
import com.its4u.models.PlaceHolderId;
import com.its4u.models.PlaceHolderSpec;
import com.its4u.models.PlaceHolders;
import com.its4u.models.Project;
import com.its4u.models.Versions;
import com.its4u.models.VersionsId;
import com.its4u.models.templates.TemplateModel;
import com.its4u.repositories.EnvironmentRepository;
import com.its4u.repositories.PlaceHoldersRepository;
import com.its4u.repositories.ProjectRepository;
import com.its4u.repositories.VersionRepository;
import com.its4u.services.ArgoService;
import com.its4u.services.EnvironmentService;
import com.its4u.services.ProjectService;
import com.its4u.utils.TemplateGenerator;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import freemarker.template.TemplateException;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectRepository repository;
	
	@Autowired
	private ArgoService argoService;
	
	@Autowired
	private PlaceHoldersRepository placeHolderRepository;
	
	@Autowired 
	private EnvironmentRepository environmentRepository;
	
	@Autowired
	private EnvironmentService environmentService;
	
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
	public String synchronize(Environments env) {
		
		ArgoEnvironment argoEnv = argoService.getArgoEnvByID(env.getArgoEnvId());
		String argoServer = argoEnv.getArgoServer();
		
		String responseArgo="";
		Unirest.setTimeouts(0, 0);
		try {
			HttpResponse<String> response = Unirest.post(argoServer+"/api/v1/applications/"+env.getProjectId()+"/sync")
			  .header("Authorization", "Bearer "+getToken(env.getArgoEnv()))
			  .body("{\r\n  \"dryRun\": false,\r\n \"prune\": true\r\n}")
			  .asString();		
			responseArgo = response.getBody();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseArgo;
	}
	
	@Override
	public String synchronizeClusterConfig(String env,String argoEnvid) {
						
		if (env.equalsIgnoreCase("prd")) env="prod"; 
		System.out.println("synchronize cluster config /api/v1/applications/cluster-configs-"+env+"/sync");
		ArgoEnvironment argoEnv = argoService.getArgoEnvByID(argoEnvid);
		String argoServer = argoEnv.getArgoServer();
		
		String responseArgo="";
		Unirest.setTimeouts(0, 0);
		try {
			HttpResponse<String> response = Unirest.post(argoServer+"/api/v1/applications/cluster-configs-"+env+"/sync")
			  .header("Authorization", "Bearer "+getToken(argoEnv))
			  //.body("{\r\n  \"dryRun\": false\r\n\r\n}")
			  .body("{\r\n  \"dryRun\": false,\r\n \"prune\": true\r\n}")
			  .asString();		
			responseArgo = response.getBody();
			System.out.println("Argo response :"+response.getStatus());
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseArgo;
	}
	
	
	@Override
	public String synchronize(String projectName,String env) {
						
		Project proj = findProject(projectName);
		enrichProject(proj);
		String envID = proj.getMapenvs().get(env);
		Environments envconcerned = null ;
		for (Environments envi: proj.getEnvironments()) {
			if (envi.getEnvironment().equalsIgnoreCase(envID)) {
				envconcerned = envi;
			}
		}
		ArgoEnvironment argoEnv = argoService.getArgoEnvByID(envconcerned.getArgoEnvId());
		String argoServer = argoEnv.getArgoServer();
		
		String responseArgo="";
		Unirest.setTimeouts(0, 0);
		try {
			HttpResponse<String> response = Unirest.post(argoServer+"/api/v1/applications/"+projectName+"/sync")
			  .header("Authorization", "Bearer "+getToken(envconcerned.getArgoEnv()))
			  .body("{\r\n  \"dryRun\": false\r\n\r\n}")
			  .asString();		
			responseArgo = response.getBody();
			System.out.println("Argo response :"+response.getStatus());
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseArgo;
	}
	
	@Override
	public String synchronize(String projectName,Environments env) {
		
		ArgoEnvironment argoEnv = argoService.getArgoEnvByID(env.getArgoEnvId());
		String argoServer = env.getArgoEnv().getArgoServer();
		System.out.println("synchronize cluster config /api/v1/applications/"+env.getEnvironment()+"/sync");
		String responseArgo="";
		Unirest.setTimeouts(0, 0);
		try {
			HttpResponse<String> response = Unirest.post(argoServer+"/api/v1/applications/"+env.getEnvironment()+"/sync")
			  .header("Authorization", "Bearer "+getToken(env.getArgoEnv()))
			  .body("{\r\n  \"dryRun\": false\r\n\r\n}")
			  .asString();		
			responseArgo = response.getBody();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(responseArgo);
		return responseArgo;
	}
	
	public String deleteArgoApplication(String projectName,String env) {
		Project proj = findProject(projectName);
		enrichProject(proj);
		String envID = proj.getMapenvs().get(env);
		Environments envconcerned = null ;
		for (Environments envi: proj.getEnvironments()) {
			if (envi.getEnvironment().equalsIgnoreCase(envID)) {
				envconcerned = envi;
			}
		}
		
		ArgoEnvironment argoEnv = argoService.getArgoEnvByID(envconcerned.getArgoEnvId());
		String argoServer = argoEnv.getArgoServer();
		
		String responseArgo="";
		Unirest.setTimeouts(0, 0);
		try {
			HttpResponse<String> response = Unirest.delete(argoServer+"/api/v1/applications/"+projectName+"?cascade=true")
			  .header("Authorization", "Bearer "+getToken(argoEnv))
			  .body("{\r\n  \"dryRun\": false\r\n\r\n}")
			  .asString();		
			responseArgo = response.getBody();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseArgo;
	}
	
	public String getToken(ArgoEnvironment argoenv) {		
	 
		String argoPassword = argoenv.getArgoPassword();
		String argoUser = argoenv.getArgoUser();
		String argoServer = argoenv.getArgoServer();
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
	public ArgoAppStatus statusAndHealth(String projectName,Environments env) {
		Unirest.setTimeouts(0, 0);
		ArgoAppStatus argoAppStatus = null;
		try {
			HttpResponse<JsonNode> response = Unirest.get("https://openshift-gitops-server-openshift-gitops.apps.ocp-lab.its4u.eu/api/v1/applications/"+env.getEnvironment()+"?refresh=true")
			  .header("Authorization", "Bearer "+getToken(env.getArgoEnv()))			  
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
	
	@Override
	public void updateGitOps(Environments env) {
		
		// clone gitaps
		Git gitApps = cloneGitApp(env.getProject());
		String gitAppsPath = GitController.getRepoPath(gitApps)+"/src/main/argo/";
		
		Git gitops = cloneGitOps(env);
		String gitOpsPath = GitController.getRepoPath(gitops)+"/cluster/";
		
		Path source = Paths.get(gitAppsPath);
		Path dest = Paths.get(gitOpsPath);
		try {
			Files.walkFileTree(source, new CopyDir(source, dest));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		// commit and push
		try {
			GitController.commitAndPushGitOpsApp(env,gitops);
		} catch (NoFilepatternException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}	
		
	}
	
	public void updateGitOpsApp(Environments env) {
		
		// clone gitops	
		Git gitOpsApps = cloneGitOpsApps(env);
		String pathWorkkingGitOpsAppsProject = GitController.getRepoPath(gitOpsApps)+"/"+env.getProjectId();		
		
		// clean GitOpsApps and recreate arborescence 
		System.out.println("clean gitOpsApp");
		try {
			FileUtils.deleteDirectory(new File(pathWorkkingGitOpsAppsProject));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
		}
		try {
			FileUtils.forceMkdir(new File(pathWorkkingGitOpsAppsProject+"/jkube/"+env.getEnvironment()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		Git gitApps = cloneGitApp(env.getProject());
		String pathWorkingGitAppProject = GitController.getRepoPath(gitApps);

		HashMap<String, String> keyValues = new HashMap<String, String>();
		for (PlaceHolders pl:env.getPlaceholders()) {
			keyValues.put(pl.getPlaceHolderId().getKey(),pl.getValue());
		}
		updateGitopsPerEnvironment(env.getProjectId(),pathWorkkingGitOpsAppsProject,pathWorkingGitAppProject,env.getEnvironment(),keyValues);
		
			
		// commit and push
		try {
			GitController.commitAndPushGitOpsApp(env,gitOpsApps);
		} catch (NoFilepatternException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}	
	}
	
	@Override
	public Git cloneGitOps(Environments env) {
		System.out.println("Clone GitOps from "+env.getArgoEnv().getGitOpsRepo());
		
		Git gitops = null;
		try {
			gitops = GitController.loadGitOps(env.getArgoEnv().getGitOpsRepo());
		} catch (IllegalStateException | GitAPIException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return gitops;
	}
	
	
	@Override
	public Git cloneGitOpsApps(Environments env) {
		
		Git gitOpsApps = null;
		try {
			gitOpsApps = GitController.loadGitOpsApps(env.getArgoEnv().getGitOpsAppsRepo());			
		} catch (IllegalStateException | GitAPIException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return gitOpsApps;
	}
	
	@Override
	public Git cloneGitApp(Project project) {
		Git gitApp = null;
		try {
			gitApp = GitController.loadGitApps(project.getProject_Id());			
			
		} catch (IllegalStateException | GitAPIException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return gitApp;
	}
	
	public void updateGitopsPerEnvironment(String projectId,String pathWorkkingGitOps,String pathWorkingGitApp,String keyenv, HashMap<String,String> placesH) {
		String pathWorkkingGitOpsEnv = pathWorkkingGitOps+"/jkube/"+keyenv;
		String pathWorkingGitAppEnv = pathWorkingGitApp+"/src/main/jkube/"+projectId+"-dev";
		try {
			System.out.println("Sync resource between "+pathWorkingGitAppEnv+ " and "+pathWorkkingGitOpsEnv);
			
			Path source = Paths.get(pathWorkingGitAppEnv);
			Path dest = Paths.get(pathWorkkingGitOpsEnv);
			Files.walkFileTree(source, new CopyDir(source, dest));	
			
			
			Files.walk(Paths.get(pathWorkkingGitOpsEnv))
	        .filter(Files::isRegularFile)
	        .forEach(path -> Parser.parser(path,placesH));
			
		
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to synchronize...");
		}
			
	}

	@Override
	public String applyConf(String projectName,String env) {
		System.out.println("Apply-conf for "+projectName+ " [env = "+env+"]");   
		// TODO Auto-generated method stub
		Project project= findProject(projectName);
		enrichProject(project);
		String envID = project.getMapenvs().get(env);
		System.out.println("envID = "+envID);
		Environments envconcerned = null ;
		for (Environments envi: project.getEnvironments()) {
			System.out.println("->"+envi.getEnvironment());
			if (envi.getEnvironment().equalsIgnoreCase(envID)) {
				
				envconcerned = envi;
				updateGitOpsApp(envconcerned);
			}
		}		
		
		if (envconcerned!=null) synchronizeClusterConfig("env",envconcerned.getArgoEnv().getArgoEnvId());
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return synchronize(projectName,envconcerned);
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
		versions.add(new Versions(new VersionsId(proj.getProject_Id(),version)));
		return createProject(proj);
		
	}
	
	
	
	@Override
	public List<Versions> getVersionsProjects() {
		return IterableUtils.toList(versionRepository.findAll());
	}

	@Override
	public String getDevVersion(String projectName) {
		String version="not found";
		Project project = findProject(projectName);
		enrichProject(project);
		String envDevID = project.getMapenvs().get("dev");
		try {
			version = project.getMapPlaceHoldersByEnv().get(envDevID).get("app-version").getValue();
		} catch (Exception e) {
			
		}
		
		return version;
	}
	

	@Override
	public void enrichProject(Project project) {
		
		HashMap<String,HashMap<String,PlaceHolderSpec>> envplaceHolders = new HashMap<String,HashMap<String,PlaceHolderSpec>>();
		HashMap<String,ArgoAppStatus> mapArgoStatusByEnv = new HashMap<String, ArgoAppStatus>();
		HashMap<String,String> envByProject = createMapEnvironment(project);
		HashMap<String,Boolean> envSelectedMap = new HashMap<String, Boolean>();
		for (Environments env:project.getEnvironments()) {	
			
			if (env.getArgoEnvId()!=null && !env.getArgoEnvId().isEmpty()) {
				ArgoEnvironment argoEnvironment = argoService.getArgoEnvByID(env.getArgoEnvId());
				if (argoEnvironment!=null)
					env.setArgoEnv(argoService.getArgoEnvByID(env.getArgoEnvId()));
			}
			envplaceHolders.put(env.getEnvironment(),createMapPlaceHoldersFromEnv(env));
			//ArgoAppStatus envstatus = statusAndHealth(project.getProject_Id(), env);
			ArgoAppStatus envstatus = new ArgoAppStatus("Unknow","Unknow");
			mapArgoStatusByEnv.put(env.getEnvironment(), envstatus);
			envSelectedMap.put(env.getEnvironment(),false);			
		}
		
		project.setMapenvs(envByProject);
		project.setMapPlaceHoldersByEnv(envplaceHolders);
		project.setMapappstatusByEnv(mapArgoStatusByEnv);
		project.setEnvSelectedMap(envSelectedMap);
		//System.out.println(mapArgoStatusByEnv);
	}
	
	@Override	
	public HashMap<String,String> createMapEnvironment(Project project) {
		HashMap<String,String> environmentMap = new HashMap<String,String>() ;
		for (Environments env:project.getEnvironments()) {
			String envsuffix = env.getEnvironment().substring(env.getEnvironment().length() - 3);
			environmentMap.put(envsuffix, env.getEnvironment());
		}		
		return environmentMap;
	}
	
	@Override
	public HashMap<String,PlaceHolderSpec> createMapPlaceHoldersFromEnv(Environments env) {
		HashMap<String,PlaceHolderSpec> keyvalue = new HashMap<String,PlaceHolderSpec>();
		for (PlaceHolders pl:env.getPlaceholders()) {
			keyvalue.put(pl.getPlaceHolderId().getKey(), new PlaceHolderSpec(pl.getValue(),pl.getType(),false));
		}
		return keyvalue;
	}
	
	@Override
	public void updateGitOpsOnlyArgoApplication(Environments env) {
		
		String projectid= env.getProjectId();		
		if (env.getArgoEnvId()!=null && !env.getArgoEnvId().isEmpty()) env.setArgoEnv(argoService.getArgoEnvByID(env.getArgoEnvId()));
		
		String nsName = env.getPlaceHoldersMap().get("ocp-namespace");
		
		TemplateModel tempMod = new TemplateModel(
				projectid,
				env.getEnvironment(), 
				env.getArgoEnv().getArgoProj(),
				env.getArgoEnv().getGitOpsAppsRepo(),
				nsName);
		
		TemplateGenerator templateGenerator;
		String newArgoApp = null;
		String newNamespace = null;
		
		try {
			templateGenerator = new TemplateGenerator();
			newArgoApp = templateGenerator.generateArgoApp(tempMod);
			newNamespace = templateGenerator.generateOcpNameSpace(tempMod);
		} catch (IOException | TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// publish new resources on gitops		
		// clone ocp-gitops
		Git gitops = cloneGitOps(env);		
		String path = GitController.getRepoPath(gitops);
		Path filePathArgoApp = Paths.get(path+"/cluster/applications/", "argoApp-"+projectid+".yaml");
		Path filePathNameSpace = Paths.get(path+"/cluster/namespaces/", "NS-"+nsName+".yml");
		try {
			Files.writeString(filePathArgoApp,newArgoApp);
			Files.writeString(filePathNameSpace,newNamespace);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			GitController.commitAndPushGitOps(env,gitops);
		} catch (GitAPIException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public List<String> listFileForClean(Git gitrepo,String type) {
		//Git gitapp = cloneGitApp(project);
		String pathapp = GitController.getRepoPath(gitrepo)+"/src/main/argo/"+type;
		// we browse the app git 
		List<String> pathfiletoDelete = new ArrayList<String>();
		
		Path pathtoBrowse = Paths.get(pathapp);
		try {
			Files.walkFileTree(pathtoBrowse, new ListFilesDir(pathtoBrowse, pathfiletoDelete));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pathfiletoDelete;
	}
	
	
	
	@Override
	public void deleteGitOpsArgo(Environments env)  {
			
		String envsuffix = env.getEnvironment().substring(env.getEnvironment().length() - 3);
		
		// clone ocp-gitops
	    env.setArgoEnv(argoService.getArgoEnvByID(env.getArgoEnvId()));
	    
	    Git gitops = cloneGitOps(env);	
		String pathops = GitController.getRepoPath(gitops);  
		Path filepath = null;
		
		if (envsuffix.equalsIgnoreCase("dev")) {
		
			    Git gitApp = cloneGitApp(env.getProject());    
			    
				List<String> fileAppToDelete = listFileForClean(gitApp,"applications");
				List<String> fileNamespacesToDelete = listFileForClean(gitApp,"namespaces");
				
				
				// delete application
				for (String filename:fileAppToDelete) {
					filepath = Paths.get(pathops+"/cluster/applications/"+filename);
					try {
						System.out.println("Delete cluster/applications/"+filename);
						Files.delete(filepath);
						gitops.rm().addFilepattern("cluster/applications/"+filename).call();				
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				// delete namespace items
				for (String filename:fileNamespacesToDelete) {
					filepath = Paths.get(pathops+"/cluster/namespaces/"+filename);
					try {
						System.out.println("Delete cluster/namespaces/"+filename);
						Files.delete(filepath);
						gitops.rm().addFilepattern("cluster/namespaces/"+filename).call();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		} else {
			
				// delete only argoApplication 
				filepath = Paths.get(pathops+"/cluster/applications/argoApp-"+env.getProjectId()+".yaml");
				try {
					System.out.println("Delete /cluster/applications/argoApp-"+env.getProjectId()+".yaml");
					Files.delete(filepath);
					gitops.rm().addFilepattern("cluster/applications/argoApp-"+env.getProjectId()+".yaml").call();	
				} catch (Exception e) {
					e.printStackTrace();
				}
				// and Namespace
				filepath = Paths.get(pathops+"/cluster/namespaces/NS-"+env.getEnvironment()+".yml");
				try {
					System.out.println("delete /cluster/namespaces/NS-"+env.getEnvironment()+".yml");
					Files.delete(filepath);
					gitops.rm().addFilepattern("cluster/namespaces/NS-"+env.getEnvironment()+".yml").call();	
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		
		// commit and push
		try {
			GitController.commitAndPushGitOps(env,gitops);
		} catch (NoFilepatternException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}	
			
	}
	

	@Override
	public void undeployGitOpsArgo(Environments env)  {
			
		// clone ocp-gitops
	    env.setArgoEnv(argoService.getArgoEnvByID(env.getArgoEnvId()));
	    Git gitApp = cloneGitApp(env.getProject());
	    Git gitops = cloneGitOps(env);	    
		String pathops = GitController.getRepoPath(gitops);

		
		List<String> fileAppToDelete = listFileForClean(gitApp,"applications");
		List<String> fileNamespacesToDelete = listFileForClean(gitApp,"namespaces");
		Path filepath = null;
		
		// delete application
		for (String filename:fileAppToDelete) {
			filepath = Paths.get(pathops+"/cluster/applications/"+filename);
			try {
				System.out.println("Delete cluster/applications/"+filename);
				Files.delete(filepath);
				gitops.rm().addFilepattern("cluster/applications/"+filename).call();				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// commit and push
		try {
			GitController.commitAndPushGitOps(env,gitops);
		} catch (NoFilepatternException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}	
			
	}
	
	@Override
	public void deleteGitOpsApps(Environments env)  {
			
		// clone ocp-gitops
		// clone gitops	
		
		Git gitopsapps = cloneGitOpsApps(env);
		String pathWorkkingGitOpsAppsProject =GitController.getRepoPath(gitopsapps)+"/"+env.getProjectId()+"/jkube/";		
		
		// clean GitOpsApps and recreate arborescence 
		System.out.println("clean gitOpsApp");
		try {
			File directorybase = new File(pathWorkkingGitOpsAppsProject);
			File[] contents = directorybase.listFiles();
			
			if (contents !=null) {
				for (File f: contents) {
					f.delete();
					System.out.println("Delete "+env.getProjectId()+"/"+f.getName());
					gitopsapps.rm().addFilepattern(env.getProjectId()+"/"+f.getName()).call();
				}
			}
			//directorybase.delete();
			gitopsapps.rm().addFilepattern(env.getProjectId()).call();
			
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// commit and push
		try {
			GitController.commitAndPushGitOpsApp(env,gitopsapps);
		} catch (NoFilepatternException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
						
	}

	@Override
	public Environments promote(Environments env) {
		
		System.out.println("Promote env "+env.getEnvironment());
				
		String envsuffix = env.getEnvironment().substring(env.getEnvironment().length() - 3);
		
		// Destination environment selection
		String destinationEnvironment=null;
		if (envsuffix.equalsIgnoreCase("dev")) {
			destinationEnvironment= "tst";
		}
		if (envsuffix.equalsIgnoreCase("tst")) {
			destinationEnvironment= "int";
		}
		if (envsuffix.equalsIgnoreCase("int")) {
			destinationEnvironment= "prd";
		}
		enrichProject(env.getProject());
				
		String  iddestinationEnvironment = env.getProject().getMapenvs().get(destinationEnvironment);
		Environments destinationEnv = null;
		
		if (iddestinationEnvironment==null) {
			// we create new Environment
			destinationEnv = new Environments();
			destinationEnv.setEnvironment(env.getProjectId()+"-"+destinationEnvironment);
			destinationEnv.setProject(env.getProject());
			destinationEnv.setProjectId(env.getProjectId());
			if (!destinationEnvironment.equalsIgnoreCase("prd")) {
				destinationEnv.setArgoEnvId("lab.its4u.eu-"+destinationEnvironment);
			} else {
				destinationEnv.setArgoEnvId("its4u.eu-prod");
			}
			environmentService.save(destinationEnv);		
			iddestinationEnvironment = env.getEnvironment();
			env.getProject().getMapenvs().put(destinationEnvironment, destinationEnv.getEnvironment());
		} else {
			destinationEnv = environmentService.getEnvById(iddestinationEnvironment);
		}
				
		destinationEnv.setArgoEnv(argoService.getArgoEnvByID(destinationEnv.getArgoEnvId()));
		
		destinationEnv = mergePlaceHolders(env, destinationEnv);
		if (!destinationEnvironment.equalsIgnoreCase("prd")) {
			destinationEnv.setArgoEnvId("lab.its4u.eu-"+destinationEnvironment);
		} else {
			destinationEnv.setArgoEnvId("its4u.eu-prod");
		}
		destinationEnv = environmentService.save(destinationEnv);
									
		return destinationEnv;
	}
	

	@Override
	public void skopeoCopy(TemplateModel skopeoModel) {
		
		try {
			TemplateGenerator templateGenerator = new TemplateGenerator();
			String payLoadSkopeoJson = templateGenerator.generateSkopeoCopyEvent(skopeoModel);
			System.out.println(payLoadSkopeoJson);
			
			String responseOpenShift="";
			Unirest.setTimeouts(0, 0);
			try {
				HttpResponse<String> response = Unirest.post("http://el-skopeo-copy-eventlistener-openshift-gitops.apps.ocp-lab.its4u.eu/")
				  .body(payLoadSkopeoJson)
				  .asString();		
				responseOpenShift = response.getBody();
				System.out.println("OpenShift response :"+response.getStatus());
			} catch (UnirestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public Environments mergePlaceHolders(Environments envSource,Environments envDest) {
		
		HashMap<String,PlaceHolderSpec> keyplaceHolderSource = envSource.getProject().getMapPlaceHoldersByEnv().get(envSource.getEnvironment());
		HashMap<String,PlaceHolderSpec> keyplaceHolderDest = envSource.getProject().getMapPlaceHoldersByEnv().get(envDest.getEnvironment());
		
		String envSourcesuffix = envSource.getEnvironment().substring(envSource.getEnvironment().length() - 3);
		String envDestsuffix = envDest.getEnvironment().substring(envDest.getEnvironment().length() - 3);
		
		List<PlaceHolders> plholDest = envDest.getPlaceholders();
		if (plholDest==null) {
			plholDest = new ArrayList<PlaceHolders>();
		}
		
		for (String keySource : keyplaceHolderSource.keySet()) {
			if (keyplaceHolderDest!=null && keyplaceHolderDest.get(keySource)!=null) {
				// key already exists
				if (keySource.equalsIgnoreCase("ocp.environment")) {
					//keyplaceHolderDest.put("ocp.environment", envDestsuffix);
				}
				if (keySource.equalsIgnoreCase("cluster-suffix")) {
					//keyplaceHolderDest.put("ocp.environment", envDestsuffix);
				}
			} else {
				PlaceHolderId plId = new PlaceHolderId(envDest.getEnvironment(), keySource);
				PlaceHolders pl = new PlaceHolders(plId,envDest,keyplaceHolderSource.get(keySource).getValue().replace(envSourcesuffix, envDestsuffix),keyplaceHolderSource.get(keySource).getType());
				plholDest.add(pl);
			}
			
		}
		envDest.setPlaceholders(plholDest);
		return envDest;
		
	}
	
	
	
}
