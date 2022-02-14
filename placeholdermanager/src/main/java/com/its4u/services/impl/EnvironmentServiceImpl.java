package com.its4u.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.its4u.models.Environments;
import com.its4u.repositories.EnvironmentRepository;
import com.its4u.services.EnvironmentService;

@Service
public class EnvironmentServiceImpl implements EnvironmentService{

	@Autowired
	private EnvironmentRepository environmentRepository;
	
	@Override
	public Environments getEnvById(String envId) {
				
		return environmentRepository.findById(envId).get();
	}

	@Override
	public Environments save(Environments env) {
		return environmentRepository.save(env);
	}

}
