package com.its4u.beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

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
		try {
			String token = getToken();
			if (!token.isEmpty()) {
				System.out.println("Success , token = "+token);
			} else {
				System.out.println("Unable to connect ! ");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getToken() throws IOException {
		
		String token="";
		Unirest.setTimeouts(0, 0);
		try {
			HttpResponse<String> response = Unirest.post(argoServer+"/api/v1/session")
			  .header("Content-Type", "text/plain")			  
			  .body("{\r\n  \"password\": \""+argoPassword+"\",\r\n  \"username\": \""+argoUser+"\"\r\n}")
			  .asString();
			System.out.println(response.getBody());
			token=response.getBody();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return token;
	}
}
