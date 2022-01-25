package com.its4u.models;

import java.io.Serializable;


public class ArgoResource implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public String group;
	
	public String version;
	
	public String kind;
	
	public String namespace;
	
	public String name;
	
	public String status;
	
	public String message;
	
	public String hookPhase;
	
	public String syncPhase;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getHookPhase() {
		return hookPhase;
	}

	public void setHookPhase(String hookPhase) {
		this.hookPhase = hookPhase;
	}

	public String getSyncPhase() {
		return syncPhase;
	}

	public void setSyncPhase(String syncPhase) {
		this.syncPhase = syncPhase;
	}
	
	
}
