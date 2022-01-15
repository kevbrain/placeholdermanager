package com.its4u.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Data
@Entity
@Table(name = "ENVIRONMENTS")
public class Environments implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@Column(name = "ENVIRONMENT")
	private String environment;
	
	@Transient
	@Setter
	@Getter
	private String projectId;
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
	

	@OneToMany(cascade=CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinColumn(name="environment", referencedColumnName = "environment") 
	public List<PlaceHolders> placeholders;
	
	@JsonIgnore
	@Transient
	@Getter
	public List<PlaceHolders> clearPlaceholders;
	
	@JsonIgnore
	@Transient
	@Getter
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
