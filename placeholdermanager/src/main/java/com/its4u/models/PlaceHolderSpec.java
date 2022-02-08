package com.its4u.models;

import lombok.Data;

@Data
public class PlaceHolderSpec {
	
	private String value;
	
	private boolean snapshot=false;

	public PlaceHolderSpec(String value, boolean snapshot) {
		super();
		this.value = value;
		this.snapshot = snapshot;
	}
	
	public boolean isSnapshot() {
		return value.endsWith("-SNAPSHOT");
	}

}
