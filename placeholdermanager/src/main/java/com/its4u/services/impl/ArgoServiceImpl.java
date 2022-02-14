package com.its4u.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.its4u.models.ArgoEnvironment;
import com.its4u.repositories.ArgoEnvironmentRepository;
import com.its4u.services.ArgoService;

@Service
public class ArgoServiceImpl implements ArgoService {
	
	@Autowired
	private ArgoEnvironmentRepository argoRepository;

	@Override
	public ArgoEnvironment getArgoEnvByID(String argoEnvId) {
		return argoRepository.findById(argoEnvId).get();
	}
	
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

	@Override
	public void delete(String argoIdenv) {
		Optional<ArgoEnvironment> envToDelete = argoRepository.findById(argoIdenv);
		argoRepository.delete(envToDelete.get());
		
	}
	

	@Override
	public void delete(ArgoEnvironment argoEnv) {
		argoRepository.delete(argoEnv);
	}

}
