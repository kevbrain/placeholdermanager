package com.its4u.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.its4u.models.Environments;
import com.its4u.models.Project;

import com.its4u.repositories.ProjectRepository;
import com.its4u.services.ProjectService;

import lombok.var;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectRepository repository;
	
	@Override
	public Project createProject(Project project) {
		
		return repository.save(project);
	}

	@Override
	public HashMap<String,Project> findAll() {
		HashMap<String,Project> projects = new HashMap<String, Project>();
		for (Project proj:repository.findAll()) {
			projects.put(proj.getProject_Id(), proj);
		}
		return projects;
	}
	
	@Override
	public Project findProject(String projectName) {
		return findAll().get(projectName);
	}
	
		
}
