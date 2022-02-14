package com.its4u.beans;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.its4u.models.ArgoAppStatus;
import com.its4u.models.ArgoEnvironment;
import com.its4u.models.ArgoResource;
import com.its4u.models.Environments;
import com.its4u.services.ArgoService;
import com.its4u.services.ProjectService;

@Component
public class ArgoInitializerBean {
	
	@Autowired
	private PollView pollView;
	
	@Autowired
	private ProjectService service;
	
	@Autowired
	private ArgoService argoService;
	
	@Value("${argo.server}")
	private String argoServer;

	@Value("${argo.user}")
	private String argoUser;
	
	@Value("${argo.password}")
	private String argoPassword;
	
	public List<ArgoResource> argoResources;
	
	private String reconciliateDate;
	
	private ArgoEnvironment argoEnvironmentSelected;
	
	private List<ArgoEnvironment> availableArgoEnvironment;
	
	private HashMap<String,ArgoEnvironment> myArgoEnv;
	
	private String selectedArgoEnId;
	
	private Environments env;
	
	private boolean newEnv;

	@PostConstruct
    public void init()  {
		myArgoEnv = argoService.findAll();
		availableArgoEnvironment = argoService.loadAllArgoEnvs();		
    }
	
	public void save(ArgoEnvironment argoEnv,Environments env) {
			
		argoEnvironmentSelected = argoService.createArgoEnv(argoEnv);
		env.setArgoEnvId(argoEnv.getArgoEnvId());
		newEnv=false;
		init();
	}
	
	public void delete(ArgoEnvironment argoEnv,Environments env) {
		argoService.delete(argoEnv);
		env.setArgoEnv(null);
		init();
	}
	
	public void addNewEnv() {
		argoEnvironmentSelected = new ArgoEnvironment();
		argoEnvironmentSelected.setArgoEnvId("new env");
		newEnv=true;
	}
		
	public void onSelectedArgoEnvId(String argoEnvId,Environments env) {
		
		this.env=env;	
		selectedArgoEnId=argoEnvId;
		argoEnvironmentSelected = myArgoEnv.get(argoEnvId);
		env.setArgoEnvId(argoEnvId);
		selectedArgoEnId=argoEnvId;
	
				
	}
	
	public void synchronise(Environments env) {
					
		service.synchronize(env.getProjectId(),env);
				
	}
	
	public ArgoAppStatus statusAndHealth(String project) {

		return service.statusAndHealth(project);
	}
	

	public PollView getPollView() {
		return pollView;
	}

	public void setPollView(PollView pollView) {
		this.pollView = pollView;
	}

	public String getArgoServer() {
		return argoServer;
	}

	public void setArgoServer(String argoServer) {
		this.argoServer = argoServer;
	}

	public String getArgoUser() {
		return argoUser;
	}

	public void setArgoUser(String argoUser) {
		this.argoUser = argoUser;
	}

	public String getArgoPassword() {
		return argoPassword;
	}

	public void setArgoPassword(String argoPassword) {
		this.argoPassword = argoPassword;
	}

	public List<ArgoResource> getArgoResources() {
		return argoResources;
	}

	public void setArgoResources(List<ArgoResource> argoResources) {
		this.argoResources = argoResources;
	}

	public String getReconciliateDate() {
		return reconciliateDate;
	}

	public void setReconciliateDate(String reconciliateDate) {
		this.reconciliateDate = reconciliateDate;
	}

	public ArgoEnvironment getArgoEnvironmentSelected() {
		return argoEnvironmentSelected;
	}

	public void setArgoEnvironmentSelected(ArgoEnvironment argoEnvironmentSelected) {
		this.argoEnvironmentSelected = argoEnvironmentSelected;
	}

	public List<ArgoEnvironment> getAvailableArgoEnvironment() {
		return availableArgoEnvironment;
	}

	public void setAvailableArgoEnvironment(List<ArgoEnvironment> availableArgoEnvironment) {
		this.availableArgoEnvironment = availableArgoEnvironment;
	}

	public HashMap<String, ArgoEnvironment> getMyArgoEnv() {
		return myArgoEnv;
	}

	public void setMyArgoEnv(HashMap<String, ArgoEnvironment> myArgoEnv) {
		this.myArgoEnv = myArgoEnv;
	}

	public String getSelectedArgoEnId() {
		return selectedArgoEnId;
	}

	public void setSelectedArgoEnId(String selectedArgoEnId) {
		this.selectedArgoEnId = selectedArgoEnId;
	}

	public boolean isNewEnv() {
		return newEnv;
	}

	public void setNewEnv(boolean newEnv) {
		this.newEnv = newEnv;
	}
	
	
	
	
	
}
