package com.its4u.beans;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.its4u.models.ArgoAuthToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

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

		disableSSLVerification ();
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
	
    public static void disableSSLVerification() {

        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }

        } };

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier allHostsValid = new HostnameVerifier() {
        	
       		@Override
			public boolean verify(String arg0, SSLSession arg1) {
				// TODO Auto-generated method stub
				return true;
			}
        };      
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);           
    }
}
