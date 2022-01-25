package com.its4u.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class ArgoAuthToken implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String token;
		
}
