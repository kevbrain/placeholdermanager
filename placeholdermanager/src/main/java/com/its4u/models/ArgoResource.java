package com.its4u.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class ArgoResource implements Serializable{

	public String group;
	
	public String version;
	
	public String kind;
	
	public String namespace;
	
	public String name;
	
	public String status;
	
	public String message;
	
	public String hookPhase;
	
	public String syncPhase;
}
