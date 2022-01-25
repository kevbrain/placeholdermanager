package com.its4u.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class PlaceHolderId implements Serializable {
	
	@Column(name = "ENVIRONMENT",insertable = false, updatable = false)
	private String environment;
	
	@Column(name = "KEY")
	private String key;
	
	
	public PlaceHolderId() {
		super();
	}


	public PlaceHolderId(String environment, String key) {
		super();
		this.environment = environment;
		this.key = key;
	}
	
	

}
