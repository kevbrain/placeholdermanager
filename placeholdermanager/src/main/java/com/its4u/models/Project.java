package com.its4u.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

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

@Data
@Entity
@Table(name = "PROJECT")
public class Project implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "PROJECT_ID")
	private String project_Id;
	
	@Column(name = "GIT_URL")
	private String gitUrl;
	
	@Column(name = "OWNER")
	private String owner;
	
	@JsonIgnore
	@Transient
	private HashMap<String,Environments> environmentMap;
	
		
	@OneToMany( mappedBy = "project", cascade = { CascadeType.ALL },fetch = FetchType.EAGER,orphanRemoval = true)	
   	private List<Environments> environments;
	
	@OneToMany( mappedBy = "project", cascade = { CascadeType.ALL },fetch = FetchType.LAZY ,orphanRemoval = true)	
   	private List<Versions> versions;

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
	
	public HashMap<String,Environments> getEnvironmentMap() {
		HashMap<String,Environments> envmap = new HashMap<String,Environments>() ;
		for (Environments env:environments) {
			String envsuffix = env.getEnvironment().substring(env.getEnvironment().length() - 3);
			envmap.put(envsuffix, env);
		}
		System.out.println(envmap);
		return envmap;
	}
	
	
	
}
