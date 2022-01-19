package com.its4u.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class ArgoIdentification implements Serializable{

	private String password;
	
	private String username;

	public ArgoIdentification(String password, String username) {
		super();
		this.password = password;
		this.username = username;
	}
	
	
}
