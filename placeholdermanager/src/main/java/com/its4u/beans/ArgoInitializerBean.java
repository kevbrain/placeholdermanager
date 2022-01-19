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
		
			    
	    String command = "curl -kv -L -X POST -d '{\"password\":\"IYkn7CE8ULgFcMhpePRxuSqDwy216vZT\",\"username\":\"admin\"}' https://openshift-gitops-server-openshift-gitops.apps.ocp-lab.its4u.eu/api/v1/session";
	    System.out.println(command);
	    
	   
	    try
	    {
	    		ProcessBuilder pb = new ProcessBuilder(command.split(" "));
	    		pb.redirectErrorStream(true);
	    	    Process p = pb.start();
	    	    InputStream is = p.getInputStream();
	     	    	   	    
	    	    BufferedReader br = new BufferedReader(new InputStreamReader((is)));

	    		String output;
	    		System.out.println("Output from Server .... \n");
	    		while ((output = br.readLine()) != null) {
	    			System.out.println(output);
	    		}
	    	  
	    		System.out.println("**************** Methode 2 ***********************");
	    		Process process2 = Runtime.getRuntime().exec(command);
	    		InputStream is2 = process2.getInputStream();
	    		
	    		BufferedReader br2 = new BufferedReader(new InputStreamReader((is)));

	    		String output2;
	    		System.out.println("Output from Server .... \n");
	    		while ((output2 = br2.readLine()) != null) {
	    			System.out.println(output2);
	    		}
	    		
	    }
	    catch (Exception e)
	    {   System.out.print("error");
	        e.printStackTrace();
	    }
		
        
		String token="";
		

		
		
		return token;
	}
}
