package com.its4u.services;

import java.util.HashMap;
import java.util.List;

import com.its4u.models.ArgoEnvironment;

public interface ArgoService {
	
	List<ArgoEnvironment> loadAllArgoEnvs();

	ArgoEnvironment getArgoEnvByID(String argoEnvId);
	
	HashMap<String,ArgoEnvironment> findAll();
	
	ArgoEnvironment createArgoEnv(ArgoEnvironment argoEnvironment);
	
	void delete(String argoIdenv);

	void delete(ArgoEnvironment argoEnv);

}
