package com.its4u.models;

import lombok.Data;

@Data
public class PlaceHolderSpec {
	
	private String value;
	
	private String type;
	
	private boolean snapshot=false;

	public PlaceHolderSpec(String value, String type, boolean snapshot) {
		super();
		this.value = value;
		this.type = type;
		this.snapshot = snapshot;
	}
	
	public boolean isSnapshot() {
		snapshot= false;
		if (value!=null &&  value.endsWith("-SNAPSHOT")) {
			snapshot = true;
		}
		return snapshot;
	}

}
