package com.its4u.services.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.its4u.models.ArgoEnvironment;
import com.its4u.models.Project;
import com.its4u.repositories.ArgoEnvironmentRepository;
import com.its4u.services.ArgoService;

public class ArgoServiceImpl implements ArgoService {
	
	@Autowired
	private ArgoEnvironmentRepository argoRepository;

	@Override
	public List<ArgoEnvironment> loadAllArgoEnvs() {
		return IterableUtils.toList(argoRepository.findAll());
	}

	@Override
	public ArgoEnvironment createArgoEnv(ArgoEnvironment argoEnvironment) {
		return argoRepository.save(argoEnvironment);
	}

	@Override
	public HashMap<String, ArgoEnvironment> findAll() {
		HashMap<String,ArgoEnvironment> argoEnvs = new HashMap<String, ArgoEnvironment>();
		for (ArgoEnvironment argoenv:argoRepository.findAll()) {
			argoEnvs.put(argoenv.getArgoEnvId(), argoenv);
		}
		return argoEnvs;
	}

}
