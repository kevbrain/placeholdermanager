package com.its4u.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class VersionsId implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "PROJECT_ID")
	private String projectId;
	
	@Column(name = "VERSION")
	private String version;
	
	

	public VersionsId(String projectId, String version) {
		super();
		this.projectId = projectId;
		this.version = version;
	}



	public VersionsId() {
		super();
	}
	
	
	
	

}
