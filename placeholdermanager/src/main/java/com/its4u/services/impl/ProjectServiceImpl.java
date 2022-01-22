package com.its4u.services.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.collections4.IterableUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.its4u.models.ArgoAppStatus;
import com.its4u.models.ArgoAuthToken;
import com.its4u.models.ArgoResource;
import com.its4u.models.Environments;
import com.its4u.models.Project;

import com.its4u.repositories.ProjectRepository;
import com.its4u.services.ProjectService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import lombok.var;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectRepository repository;
	
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
		// TODO Auto-generated method stub
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
		
}
