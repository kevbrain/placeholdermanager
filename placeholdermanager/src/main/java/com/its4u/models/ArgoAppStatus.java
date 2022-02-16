package com.its4u.models;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ArgoAppStatus implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String reconcileAt;
	
	private String sync;
	
	private String health;
	
	private List<ArgoResource> argoResources;
	
	private boolean synchrone;

	public ArgoAppStatus(String sync, String health) {
		super();
		this.sync = sync;
		this.health = health;
	}
	
	public boolean isSynchrone() {		
		if (sync!=null && sync.equalsIgnoreCase("synced")) return true;
		return false;
	}
	
	
}
