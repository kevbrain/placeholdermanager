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
		
			    
	    String command = "curl -kv -L -X POST -d {\"password\":\"IYkn7CE8ULgFcMhpePRxuSqDwy216vZT\",\"username\":\"admin\"} https://openshift-gitops-server-openshift-gitops.apps.ocp-lab.its4u.eu/api/v1/session";
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
	    	  
	    		
	    		
	    		
	    }
	    catch (Exception e)
	    {   System.out.print("error");
	        e.printStackTrace();
	    }
		
	    System.out.println("**************** Methode 2 ***********************");
	    
	    Process process2;
		try {
			process2 = Runtime.getRuntime().exec(command);
			InputStream is2 = process2.getInputStream();
			
			
			System.out.println(process2.info().command());
			System.out.println(process2.info().arguments());
			
			for (String str: process2.info().arguments().get()) {
				System.out.println(str);
			}
			System.out.println(process2.info().commandLine());
			
			
			BufferedReader br2 = new BufferedReader(new InputStreamReader((is2)));

			String output2;
			System.out.println("Output from Server .... \n");
			while ((output2 = br2.readLine()) != null) {
				System.out.println(output2);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
		String token="";
		

		
		
		return token;
	}
}
