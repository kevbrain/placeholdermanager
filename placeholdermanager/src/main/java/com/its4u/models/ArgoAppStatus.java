package com.its4u.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ArgoAppStatus implements Serializable {
	
	private String reconcileAt;
	
	private String sync;
	
	private String health;
	
	private List<ArgoResource> argoResources;

	public ArgoAppStatus(String sync, String health) {
		super();
		this.sync = sync;
		this.health = health;
	}
	
	
}
