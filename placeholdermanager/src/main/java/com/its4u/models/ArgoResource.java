package com.its4u.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class ArgoResource implements Serializable{

	private String group;
	
	private String version;
	
	private String kind;
	
	private String namespace;
	
	private String name;
	
	private String status;
	
	private String message;
	
	private String hookPhase;
	
	private String syncPhase;
}
