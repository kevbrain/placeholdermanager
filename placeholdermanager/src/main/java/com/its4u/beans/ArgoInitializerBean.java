package com.its4u.beans;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.its4u.models.ArgoAuthToken;
import com.mashape.unirest.http.HttpResponse;
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
	
	
	
		
	public void synchronise(String project) {
		
		pollView.log("Try to synchronise "+project);
		

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
	
	public String getToken() {
		pollView.log("Request a new Token access for ArgoCD");
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
