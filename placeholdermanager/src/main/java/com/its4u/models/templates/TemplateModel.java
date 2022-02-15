package com.its4u.models.templates;

public class TemplateModel {
	
	private String appName;
	
	private String argoProj;
	
	private String gitOpsAppsRepo;

	public TemplateModel(String appName, String argoProj, String gitOpsAppsRepo) {
		super();
		this.appName = appName;
		this.argoProj = argoProj;
		this.gitOpsAppsRepo = gitOpsAppsRepo;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getArgoProj() {
		return argoProj;
	}

	public void setArgoProj(String argoProj) {
		this.argoProj = argoProj;
	}

	public String getGitOpsAppsRepo() {
		return gitOpsAppsRepo;
	}

	public void setGitOpsAppsRepo(String gitOpsAppsRepo) {
		this.gitOpsAppsRepo = gitOpsAppsRepo;
	}
	
	

}
