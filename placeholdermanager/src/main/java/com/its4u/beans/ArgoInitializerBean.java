package com.its4u.beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class ArgoInitializerBean {
	
	@Value("${argo.server}")
	private String argoServer;

	@Value("${argo.user}")
	private String argoUser;
	
	@Value("${argo.password}")
	private String argoPassword;
	
		
	public void testConnection() {
		System.out.println("argo server : "+argoServer);
		System.out.println("argo user : "+argoUser);
		System.out.println("argo password : "+argoPassword);
		
		String token = getToken();
		if (!token.isEmpty()) {
			System.out.println("Success , token = "+token);
		} else {
			System.out.println("Unable to connect ! ");
		}
				
	}
	
	public String getToken() {
		
			    
	    String command = "curl -k --location --request POST 'http://openshift-gitops-server.openshift-gitops.svc.cluster.local:80/api/v1/session' --header 'Content-Type: text/plain' --data '{  \"password\": \"IYkn7CE8ULgFcMhpePRxuSqDwy216vZT\", \"username\": \"admin\"}'";
	   
	   
	    try
	    {
	    	 Process process = Runtime.getRuntime().exec(command);   
	         BufferedReader reader =  new BufferedReader(new InputStreamReader(process.getInputStream()));
	            StringBuilder builder = new StringBuilder();
	            String line = null;
	            while ( (line = reader.readLine()) != null) {
	                    builder.append(line);
	                    builder.append(System.getProperty("line.separator"));
	            }
	            String result = builder.toString();
	            System.out.print(result);

	    }
	    catch (IOException e)
	    {   System.out.print("error");
	        e.printStackTrace();
	    }
		
        
		String token="";
		

		
		
		return token;
	}
}
