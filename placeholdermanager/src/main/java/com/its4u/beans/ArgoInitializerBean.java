package com.its4u.beans;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sonarsource.scanner.api.internal.shaded.minimaljson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.its4u.models.ArgoAppStatus;
import com.its4u.models.ArgoAuthToken;
import com.its4u.models.ArgoResource;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import lombok.Data;

@Data
@Component
public class ArgoInitializerBean {
	
	@Autowired
	private PollView pollView;
	
	@Value("${argo.server}")
	private String argoServer;

	@Value("${argo.user}")
	private String argoUser;
	
	@Value("${argo.password}")
	private String argoPassword;
	
	private List<ArgoResource> argoResources;
	
		
	public void synchronise(String project) {
					
		Unirest.setTimeouts(0, 0);
		try {
			HttpResponse<String> response = Unirest.post("https://openshift-gitops-server-openshift-gitops.apps.ocp-lab.its4u.eu/api/v1/applications/"+project+"/sync")
			  .header("Authorization", "Bearer "+getToken())
			  .body("{\r\n  \"dryRun\": false\r\n\r\n}")
			  .asString();
			System.out.println(response.getStatus());
			System.out.println(response.getBody());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Project synchronized"));
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	public ArgoAppStatus statusAndHealth(String project) {
		Unirest.setTimeouts(0, 0);
		ArgoAppStatus argoAppStatus = null;
		try {
			HttpResponse<JsonNode> response = Unirest.get("https://openshift-gitops-server-openshift-gitops.apps.ocp-lab.its4u.eu/api/v1/applications/"+project+"?refresh=true")
			  .header("Authorization", "Bearer "+getToken())			  
			  .asJson();
											
			JsonNode princ = response.getBody();
			JSONObject jsonObject = princ.getObject();
			String sync = jsonObject.getJSONObject("status").getJSONObject("sync").getString("status");
			String healthy = jsonObject.getJSONObject("status").getJSONObject("health").getString("status");	
			argoAppStatus = new  ArgoAppStatus(sync, healthy);
			
			JSONArray  resources = jsonObject.getJSONObject("status").getJSONObject("operationState").getJSONObject("syncResult").getJSONArray("resources");
			ObjectMapper objectMapper = new ObjectMapper();
			
			argoResources = new ArrayList<ArgoResource>();
			for (Object resobj:resources) {
				JSONObject resJson = (JSONObject) resobj;				
				try {
					ArgoResource argoResource = objectMapper.readValue(resobj.toString(), ArgoResource.class);
					System.out.println(argoResource);
					argoResources.add(argoResource);
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
						
			
			
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return argoAppStatus;
	}
	
	public String getToken() {
		
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
	    {   System.out.print("error");
	        e.printStackTrace();
	    }
		      
		return token;
	}
	

}
