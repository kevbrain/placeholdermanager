package com.its4u.services.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.its4u.models.Environments;
import com.its4u.repositories.EnvironmentRepository;
import com.its4u.services.EnvironmentService;

public class EnvironmentServiceImpl implements EnvironmentService{

	@Autowired
	private EnvironmentRepository environmentRepository;
	
	@Override
	public Environments getEnvById(String envId) {
				
		return environmentRepository.findById(envId).get();
	}

}
