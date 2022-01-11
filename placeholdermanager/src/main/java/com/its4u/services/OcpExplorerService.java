package com.its4u.services;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

public class OcpExplorerService {

	private KubernetesClient client;

	public OcpExplorerService(String token) {
		super();
		Config config = new ConfigBuilder().withMasterUrl("https://api.ocp-lab.its4u.eu:6443")
                .withDisableHostnameVerification(true)
                .withOauthToken(token)
                .withTrustCerts(false).build();

		this.client = new DefaultKubernetesClient(config);	
	}
	
	public void loadProject() {
		System.out.println("load projects ...");
		System.out.println(this.client);
		DeploymentList deployments = client.apps().deployments().inAnyNamespace().withLabel("its4u.placeholders/auto", "true").list();
		for (Deployment dep: deployments.getItems()) {
			System.out.println(dep.getMetadata().getName());
		}
	}
	
	
}
