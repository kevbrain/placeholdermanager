package com.its4u.rest.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.its4u.models.ArgoEnvironment;
import com.its4u.models.Project;
import com.its4u.services.ArgoService;
import com.its4u.services.ProjectService;

@RestController
public class PlaceHoldersController {

	@Autowired
	private ProjectService service;
	
	@Autowired
	private ArgoService argoService;
	
	@ResponseBody
	@PostMapping(value= "/createProject", consumes = "application/json", produces = "application/json")
	public Project createProject(@RequestBody Project project) {
		return service.createProject(project);
	}
	
	@RequestMapping(value = "/projects/list", produces = "application/json")
	public @ResponseBody Collection<Project> getAllProjects() {
		return service.findAll().values();
	}
	
	@PostMapping(value ="/argo-environment/delete/{envId}")
	public void deleteArgoEnv(@PathVariable("envId") String envId) {
		argoService.delete(envId);
	}
	
	@RequestMapping(value = "/argo-environment/list", produces = "application/json")
	public @ResponseBody Collection<ArgoEnvironment> getAllArogoEnvironment() {
		return argoService.findAll().values();
	}
	
	@RequestMapping(value = "/projects/get/{projectName}", produces = "application/json")
	public @ResponseBody Project getProject(@PathVariable("projectName") String projectName) {
		return service.findProject(projectName);
	}
	
	@RequestMapping(value = "/projects/checkDevVersion/{projectName}", produces = "application/json")
	public @ResponseBody String checkDevVersion(@PathVariable("projectName") String projectName) {
		return service.getDevVersion(projectName);
	}
	
	@RequestMapping(value = "/apply-conf/{projectName}/{env}")
	public @ResponseBody String applyconf(@PathVariable("projectName") String projectName,@PathVariable("env") String env) {
		return service.applyConf(projectName,env);
	}
	
	@RequestMapping(value = "/sync/{projectName}/{env}")
	public @ResponseBody String synchronize(@PathVariable("projectName") String projectName,@PathVariable("env") String env) {
		return service.synchronize(projectName,env);
	}
	
	@RequestMapping(value = "/projects/{projectName}/create-version/{version}")
	public @ResponseBody Project createVersion(@PathVariable("projectName") String projectName, @PathVariable("version") String version) {
		return service.createVersion(projectName,version);
	}
}
