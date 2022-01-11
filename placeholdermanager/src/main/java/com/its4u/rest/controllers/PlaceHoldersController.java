package com.its4u.rest.controllers;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.its4u.models.Project;
import com.its4u.services.ProjectService;

@RestController
public class PlaceHoldersController {

	@Autowired
	private ProjectService service;
	
	@ResponseBody
	@PostMapping(value= "/createProject", consumes = "application/json", produces = "application/json")
	public Project createProject(@RequestBody Project project) {
		
		System.out.println(project);
		
		return service.createProject(project);
	}
	
	@RequestMapping(value = "/projects/list", produces = "application/json")
	public @ResponseBody Collection<Project> getAllProjects() {
		return service.findAll().values();
	}
	
	@RequestMapping(value = "/projects/get/{projectName}", produces = "application/json")
	public @ResponseBody Project getProject(@PathVariable("projectName") String projectName) {
		return service.findProject(projectName);
	}
}
