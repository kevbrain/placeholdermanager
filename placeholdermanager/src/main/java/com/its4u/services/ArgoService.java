package com.its4u.services;

import java.util.HashMap;
import java.util.List;

import com.its4u.models.ArgoEnvironment;

public interface ArgoService {
	
	List<ArgoEnvironment> loadAllArgoEnvs();
	
	HashMap<String,ArgoEnvironment> findAll();
	
	ArgoEnvironment createArgoEnv(ArgoEnvironment argoEnvironment);

}
