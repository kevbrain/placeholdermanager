package com.its4u.gitops;

import java.io.File;
import java.net.URISyntaxException;
import java.util.UUID;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;

public class GitController {

	private static String gitAppsUrl = "https://github.com/kevbrain/";
	
	private static String gitOpsAppsDeployUrl = "https://github.com/kevbrain/ocp-gitops-apps-deploy.git";
	
	private static String pathWorkspace = "c:/Tmp";
	
	private static Git gitOpsApp;
	
	private static Git gitApp;
			
	@Value("${git.user}")
	private static String gitUser;
	
	@Value("${git.password}")
	private static String gitPassword;
	
	public static String loadGitApps(String project) throws IllegalStateException, GitAPIException {

		
		gitAppsUrl = gitAppsUrl+project+".git";
		UUID uuid = UUID.randomUUID();
		String path = pathWorkspace+"//"+project+"-"+uuid;
		
		File workingDirectory = null;
		workingDirectory = new File(path);
		workingDirectory.delete();
		workingDirectory.mkdirs();
						
		try {	
			gitApp = Git.cloneRepository()
					  .setURI(gitAppsUrl)
					  .setDirectory(workingDirectory)
					  .call();
		} catch (Exception e) {

			gitApp = Git.init().setDirectory(workingDirectory).call();
		}	
		return path+"//"+project;
		
	}
	
	
	public static String loadGitOpsApps(String project) throws IllegalStateException, GitAPIException {

		UUID uuid = UUID.randomUUID();
		String path = pathWorkspace+"//ocp-gitops-apps-deploy-"+uuid;
		
		File workingDirectory = null;
		workingDirectory = new File(path);
		workingDirectory.delete();
		workingDirectory.mkdirs();
						
		try {	
			gitOpsApp = Git.cloneRepository()
					  .setURI(gitOpsAppsDeployUrl)
					  .setDirectory(workingDirectory)
					  .call();
		} catch (Exception e) {

			gitOpsApp = Git.init().setDirectory(workingDirectory).call();
		}	
		return path+"//"+project;
		
	}
	
	public static void commitAndPush() throws NoFilepatternException, GitAPIException, URISyntaxException {
		
		gitOpsApp.add().addFilepattern(".").call();
		
		// Now, we do the commit with a message
		
		RevCommit rev =	gitOpsApp.commit().setAuthor("ksc", "ksc@example.com").setMessage("Modified by ITS4U PlaceHolderControler").call();
	
		RemoteAddCommand remoteAddCommand = gitOpsApp.remoteAdd();
	    remoteAddCommand.setName("origin");
	    remoteAddCommand.setUri(new URIish(gitOpsAppsDeployUrl));
	    remoteAddCommand.call();

	    // push to remote:
	    PushCommand pushCommand = gitOpsApp.push();
	    pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitUser, gitPassword));
	    // you can add more settings here if needed
	    pushCommand.call();
	    
	    System.out.println("Project modified and pushed");
	}
	
}
