package com.its4u.beans;

import java.io.BufferedReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.its4u.models.ArgoIdentification;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
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
	
	private static HttpClient unsafeHttpClient;

    static {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            unsafeHttpClient = HttpClients.custom().setSSLContext(sslContext)
                    .setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public static HttpClient getClient() {
        return unsafeHttpClient;
    }
	
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
		
		URL url = new URL("http://openshift-gitops-server.openshift-gitops.svc.cluster.local:80/api/v1/session");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		System.out.println("Connection to "+url);
		conn.setDoOutput(true);
		
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");

		ArgoIdentification argoIdentification = new ArgoIdentification(argoPassword,argoUser);
		ObjectMapper mapper = new ObjectMapper();
		String jsonIdent = mapper.writeValueAsString(argoIdentification);
		System.out.println(jsonIdent);
		OutputStream os = conn.getOutputStream();
		os.write(jsonIdent.getBytes());
		os.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}

		conn.disconnect();
		
		
		return token;
	}
}
