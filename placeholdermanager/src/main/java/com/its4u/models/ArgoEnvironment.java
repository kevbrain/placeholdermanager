package com.its4u.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "ARGOENV")
public class ArgoEnvironment implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ARGO_ENV_ID")
	private String argoEnvId;

	@Column(name = "ARGO_SERVER")
	private String argoServer;
	
	@Column(name = "ARGO_USER")
	private String argoUser;
		
	@Column(name = "ARGO_PASSWORD")
	private String argoPassword;
	
	@Column(name = "ARGO_PROJ")
	private String argoProj;
	
	@Column(name = "GITOPS_REPO")
	private String gitOpsRepo;
	
	@Column(name = "GITOPS_APPS_REPO")
	private String gitOpsAppsRepo;
	
	public String toString() {
		return argoEnvId ;
	}

	public String getArgoEnvId() {
		return argoEnvId;
	}

	public void setArgoEnvId(String argoEnvId) {
		this.argoEnvId = argoEnvId;
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

	public String getArgoProj() {
		return argoProj;
	}

	public void setArgoProj(String argoProj) {
		this.argoProj = argoProj;
	}

	public String getGitOpsRepo() {
		return gitOpsRepo;
	}

	public void setGitOpsRepo(String gitOpsRepo) {
		this.gitOpsRepo = gitOpsRepo;
	}

	public String getGitOpsAppsRepo() {
		return gitOpsAppsRepo;
	}

	public void setGitOpsAppsRepo(String gitOpsAppsRepo) {
		this.gitOpsAppsRepo = gitOpsAppsRepo;
	}
	
	
	
}
