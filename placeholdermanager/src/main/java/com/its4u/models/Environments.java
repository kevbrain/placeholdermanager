package com.its4u.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "ENVIRONMENTS")
public class Environments implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@Column(name = "ENVIRONMENT")
	private String environment;
	
	@Column(name = "PROJECT_ID")
	private String projectId;
	
	@Column(name = "ARGO_ENV_ID")
	private String argo_env_Id;
	
	@Column(name = "RELEASE_TAG")
	private String releaseTag;
	
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
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name = "project_Id",insertable = false, updatable = false)
    private Project project;
		

	@OneToMany(mappedBy = "_environment", cascade =  { CascadeType.ALL }, fetch = FetchType.EAGER,orphanRemoval = true)	
	public List<PlaceHolders> placeholders;
	
	@JsonIgnore
	@Transient
	public List<PlaceHolders> clearPlaceholders;
	
	@JsonIgnore
	@Transient
	public List<PlaceHolders> secretsPlaceholders;
	
	@JsonIgnore
	@Transient
	public List<PlaceHolders> newPlaceholders;
	
	
	
		
	public Environments() {
		super();
	}
	
	public Environments(String environment) {
		super();	
		this.environment = environment;
	}


	public Environments(Project project, String environment) {
		super();
		this.project = project;
		this.environment = environment;
	}
	
	public void setProjectId(String projectId) {
		this.projectId=projectId;
		if (project!=null) this.project.setProject_Id(projectId);
	}
	
	public String getProjectId() {
		if (project!=null) return this.project.getProject_Id();
		return this.projectId;
	}
	
	public List<PlaceHolders> getSecretsPlaceholders() {
		List<PlaceHolders> secrets = new ArrayList<>();
		for (PlaceHolders pl: placeholders) {
			if (pl.getType()!=null && pl.getType().equalsIgnoreCase("secret")) {
				secrets.add(pl);
			}
		}
		return secrets;
	}
	
	public List<PlaceHolders> getClearPlaceholders() {
		List<PlaceHolders> clears = new ArrayList<>();
		for (PlaceHolders pl: placeholders) {
			if (pl.getType()==null || !pl.getType().equalsIgnoreCase("secret")) {
				clears.add(pl);
			}
		}
		return clears;
	}
	

	
	@Override
	public String toString() {
		return "Environments [environment=" + environment + "]";
	}

	
	
}
