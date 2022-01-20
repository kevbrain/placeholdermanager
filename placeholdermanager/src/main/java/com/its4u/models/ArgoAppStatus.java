package com.its4u.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class ArgoAppStatus implements Serializable {
	
	private String sync;
	
	private String health;

	public ArgoAppStatus(String sync, String health) {
		super();
		this.sync = sync;
		this.health = health;
	}
	
	
}
