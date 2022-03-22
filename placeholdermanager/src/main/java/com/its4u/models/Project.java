package com.its4u.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;


@Entity
public class Project implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "PROJECT_ID")
	private String project_Id;
	
	@Column(name = "GIT_URL")
	private String gitUrl;
	
	@Column(name = "OWNER")
	private String owner;
	
	@Column(name = "TEAM")
	private String team;
	
	@Column(name = "VALUECHAIN")
	private String valueChain;
	
	@Column(name = "DESCRIPTION")
	private String description;
			
	@OneToMany( mappedBy = "project", cascade = { CascadeType.ALL },fetch = FetchType.EAGER,orphanRemoval = true)	
   	private List<Environments> environments;
	
	@OneToMany( mappedBy = "project", cascade = { CascadeType.ALL },fetch = FetchType.LAZY ,orphanRemoval = true)	
   	private List<Versions> versions;
	
	@Transient
	@JsonIgnore
	private HashMap<String,String> mapenvs;
	
	@Transient
	@JsonIgnore
	private HashMap<String,HashMap<String,PlaceHolderSpec>> mapPlaceHoldersByEnv;
	
	@Transient
	@JsonIgnore	
	private HashMap<String,ArgoAppStatus> mapappstatusByEnv;
	
	@Transient
	@JsonIgnore
	private Map<String,Boolean> envSelectedMap;
	
	@Transient
	@JsonIgnore
	private boolean synchrone;

	public Project() {
		super();
	}

	public Project(String projectId, String gitUrl, String owner) {
		super();
		this.project_Id = projectId;
		this.gitUrl = gitUrl;
		this.owner = owner;
	}

	
	public String toString() {
		return project_Id ;
	}

	public String getProject_Id() {
		return project_Id;
	}

	public void setProject_Id(String project_Id) {
		this.project_Id = project_Id;
	}

	public String getGitUrl() {
		return gitUrl!=null?gitUrl:"";
	}

	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getTeam() {
		return team!=null?team:"";
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getValueChain() {
		return valueChain!=null?valueChain:"";
	}

	public void setValueChain(String valueChain) {
		this.valueChain = valueChain;
	}

	public String getDescription() {
		return description!=null?description:"";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Environments> getEnvironments() {
		return environments;
	}

	public void setEnvironments(List<Environments> environments) {
		this.environments = environments;
	}

	public List<Versions> getVersions() {
		return versions;
	}

	public void setVersions(List<Versions> versions) {
		this.versions = versions;
	}

	public HashMap<String, String> getMapenvs() {
		return mapenvs;
	}

	public void setMapenvs(HashMap<String, String> mapenvs) {
		this.mapenvs = mapenvs;
	}

	public HashMap<String, HashMap<String, PlaceHolderSpec>> getMapPlaceHoldersByEnv() {
		return mapPlaceHoldersByEnv;
	}

	public void setMapPlaceHoldersByEnv(HashMap<String, HashMap<String, PlaceHolderSpec>> mapPlaceHoldersByEnv) {
		this.mapPlaceHoldersByEnv = mapPlaceHoldersByEnv;
	}

	public HashMap<String, ArgoAppStatus> getMapappstatusByEnv() {
		return mapappstatusByEnv;
	}

	public void setMapappstatusByEnv(HashMap<String, ArgoAppStatus> mapappstatusByEnv) {
		this.mapappstatusByEnv = mapappstatusByEnv;
	}

	public Map<String, Boolean> getEnvSelectedMap() {
		return envSelectedMap;
	}

	public void setEnvSelectedMap(Map<String, Boolean> envSelectedMap) {
		this.envSelectedMap = envSelectedMap;
	}

	public boolean isSynchrone() {
		return synchrone;
	}

	public void setSynchrone(boolean synchrone) {
		this.synchrone = synchrone;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	
	
}
