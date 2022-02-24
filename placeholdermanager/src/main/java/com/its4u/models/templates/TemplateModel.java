package com.its4u.models.templates;

public class TemplateModel {
	
	private String appName;
	
	private String envId;
	
	private String argoProj;
	
	private String gitOpsAppsRepo;
	
	private String namespace;
	
	// skopeo-copy
	
	private String appVersion;
	
	private String srcClusterSuffix;
	
	private String srcNamespace;
	
	private String destClusterSuffix;
	
	private String destNamespace;
	
	

	public TemplateModel() {
		super();
	}

	public TemplateModel(String appName, String envId, String argoProj, String gitOpsAppsRepo, String namespace) {
		super();
		this.appName = appName;
		this.envId = envId;
		this.argoProj = argoProj;
		this.gitOpsAppsRepo = gitOpsAppsRepo;
		this.namespace = namespace;
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

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getEnvId() {
		return envId;
	}

	public void setEnvId(String envId) {
		this.envId = envId;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getSrcClusterSuffix() {
		return srcClusterSuffix;
	}

	public void setSrcClusterSuffix(String srcClusterSuffix) {
		this.srcClusterSuffix = srcClusterSuffix;
	}

	public String getSrcNamespace() {
		return srcNamespace;
	}

	public void setSrcNamespace(String srcNamespace) {
		this.srcNamespace = srcNamespace;
	}

	public String getDestClusterSuffix() {
		return destClusterSuffix;
	}

	public void setDestClusterSuffix(String destClusterSuffix) {
		this.destClusterSuffix = destClusterSuffix;
	}

	public String getDestNamespace() {
		return destNamespace;
	}

	public void setDestNamespace(String destNamespace) {
		this.destNamespace = destNamespace;
	}
	
	
	

}
