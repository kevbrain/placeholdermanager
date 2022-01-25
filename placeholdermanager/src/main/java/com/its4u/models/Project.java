package com.its4u.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
	
	
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)	
    @JoinColumn(name="PROJECT_ID", referencedColumnName = "PROJECT_ID")  
	private List<Environments> environments;

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
	
	
	
	
	
}
