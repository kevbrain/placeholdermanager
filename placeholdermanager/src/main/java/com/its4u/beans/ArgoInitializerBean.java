package com.its4u.beans;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.its4u.models.ArgoAppStatus;
import com.its4u.models.ArgoResource;
import com.its4u.models.Environments;
import com.its4u.services.ProjectService;

@Component
public class ArgoInitializerBean {
	
	@Autowired
	private PollView pollView;
	
	@Autowired
	private ProjectService service;
	
	@Value("${argo.server}")
	private String argoServer;

	@Value("${argo.user}")
	private String argoUser;
	
	@Value("${argo.password}")
	private String argoPassword;
	
	public List<ArgoResource> argoResources;
	
	private String reconciliateDate;

	
	public void synchronise(Environments env) {
					
		service.synchronize(env.getProjectId(),env);
				
	}
	
	public ArgoAppStatus statusAndHealth(String project) {

		return service.statusAndHealth(project);
	}
	

	public PollView getPollView() {
		return pollView;
	}

	public void setPollView(PollView pollView) {
		this.pollView = pollView;
	}

	public String getArgoServer() {
		return argoServer;
	}

	public void setArgoServer(String argoServer) {
		this.argoServer = argoServer;
	}

	public String getArgoUser() {
		return argoUser;
	}

	public void setArgoUser(String argoUser) {
		this.argoUser = argoUser;
	}

	public String getArgoPassword() {
		return argoPassword;
	}

	public void setArgoPassword(String argoPassword) {
		this.argoPassword = argoPassword;
	}

	public List<ArgoResource> getArgoResources() {
		return argoResources;
	}

	public void setArgoResources(List<ArgoResource> argoResources) {
		this.argoResources = argoResources;
	}

	public String getReconciliateDate() {
		return reconciliateDate;
	}

	public void setReconciliateDate(String reconciliateDate) {
		this.reconciliateDate = reconciliateDate;
	}
	
	
	
}
