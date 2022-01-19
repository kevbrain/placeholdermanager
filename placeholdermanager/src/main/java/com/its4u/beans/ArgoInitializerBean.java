package com.its4u.beans;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
		
		getToken();
		
				
	}
	
	public String getToken() {
		
			    
	    String command = "curl -kv --location --request POST 'http://openshift-gitops-server.openshift-gitops.svc.cluster.local:80/api/v1/session' --header 'Content-Type: text/plain' --data '{  \"password\": \"IYkn7CE8ULgFcMhpePRxuSqDwy216vZT\", \"username\": \"admin\"}'";
	    System.out.println(command);
	    
	   
	    try
	    {
	    		ProcessBuilder pb = new ProcessBuilder(command.split(" "));
	    		pb.redirectErrorStream(true);
	    	    Process p = pb.start();
	    	    InputStream is = p.getInputStream();
	    	    BufferedInputStream bis = new BufferedInputStream(is);
	    	    System.out.println(bis);
	    		
	    }
	    catch (Exception e)
	    {   System.out.print("error");
	        e.printStackTrace();
	    }
		
        
		String token="";
		

		
		
		return token;
	}
}
