package com.its4u.services;

import com.its4u.models.Environments;

public interface EnvironmentService {
	
	Environments getEnvById(String envId);
	
	Environments save(Environments env);
	
	void delete(Environments env);

}
