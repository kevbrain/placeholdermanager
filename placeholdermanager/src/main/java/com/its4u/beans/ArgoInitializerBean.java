package com.its4u.beans;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
				
		String command = "curl -kv -H 'Authorization: Bearer "+getToken()+"'"
				+" -X POST "
				+argoServer+"/api/v1/applications/"+project+"/sync";
		System.out.println(command);
		
		try
	    {
				Process process = Runtime.getRuntime().exec(command);						
	    		InputStream is = process.getInputStream();
	     	    	   	    
	    	    BufferedReader br = new BufferedReader(new InputStreamReader((is)));

	    		String readline;	    			    		
	    		while ((readline = br.readLine()) != null) {
	    			System.out.println(readline);
	    		}	    		
	    		process.destroy();
	    }
	    catch (Exception e)
	    {   System.out.print("error");
	        e.printStackTrace();
	    }
		
		System.out.println("UNIREST METHOD");
		try {
			doTrustToCertificates();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Unirest.setTimeouts(0, 0);
		try {
			HttpResponse<String> response = Unirest.post("https://openshift-gitops-server-openshift-gitops.apps.ocp-lab.its4u.eu/api/v1/applications/test-toto/sync")
			  .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NDI3NTgxMDMsImp0aSI6ImFhZDkwZWViLTVmN2EtNGQ0Mi04ZDIzLWFlMWE1NmU5MTI0OSIsImlhdCI6MTY0MjY3MTcwMywiaXNzIjoiYXJnb2NkIiwibmJmIjoxNjQyNjcxNzAzLCJzdWIiOiJhZG1pbjpsb2dpbiJ9.0OaFJz2iHtrVD5K5woMFHvMZqiM8gdsz8usOvIlmCuY")
			  .body("")
			  .asString();
			System.out.println(response.getStatus());
			System.out.println(response.getBody());
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
	
	 // trusting all certificate 
	 public void doTrustToCertificates() throws Exception {
	        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
	        TrustManager[] trustAllCerts = new TrustManager[]{
	                new TrustManager() {
	                    public X509Certificate[] getAcceptedIssuers() {
	                        return null;
	                    }

	                    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
	                        return;
	                    }

	                    public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
	                        return;
	                    }
	                }
	        };

	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        HostnameVerifier hv = new HostnameVerifier() {
	           
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					if (!arg0.equalsIgnoreCase(arg1.getPeerHost())) {
	                    System.out.println("Warning: URL host '" + arg0 + "' is different to SSLSession host '" + arg1.getPeerHost() + "'.");
	                }
	                return true;
				}
	        };
	        HttpsURLConnection.setDefaultHostnameVerifier(hv);
	    }
}
