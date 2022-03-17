package com.its4u.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;


@Data
@Entity
@Table(name = "VERSIONS")
public class Versions implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private VersionsId versionsid;
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name = "project_Id",insertable = false, updatable = false)
    private Project project;

	public Versions() {
		super();
	}

	public Versions(VersionsId versionsid) {
		super();
		this.versionsid = versionsid;
	}

	

	
}
