package com.its4u.gitops;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.util.FileSystemUtils;

public class GitController {

	private static String gitAppsUrl = "https://github.com/kevbrain/";
	
	private static String gitOpsAppsDeployUrl = "https://github.com/kevbrain/ocp-gitops-apps-deploy.git";
	
	private static String pathWorkspace = "/git-workspace";
	
	private static Git gitOpsApp;
	
	private static Git gitApp;
			
	
	@SuppressWarnings("deprecation")
	public static String loadGitApps(String project) throws IllegalStateException, GitAPIException {

		
		String gitAppsUrlProject = gitAppsUrl+project+".git";
		UUID uuid = UUID.randomUUID();
		String path = pathWorkspace+"//"+project+"-"+uuid;
		
		File workingDirectory = null;
		workingDirectory = new File(path);
		workingDirectory.delete();
		workingDirectory.mkdirs();
						
		try {	
			gitApp = Git.cloneRepository()
					  .setURI(gitAppsUrlProject)
					  .setDirectory(workingDirectory)
					  .call();
		
		} catch (Exception e) {
			System.out.println("Git Clone exception");
			e.printStackTrace();
			gitApp = Git.init().setDirectory(workingDirectory).call();
		}	
		
		return path;
		
	}
	
	public static List<String> searchTagsGitApps() {
		List<String> tags = new ArrayList<String>();
		Map<String,Ref> tagsList = gitApp.getRepository().getTags();
		for (String tag:tagsList.keySet()) {
			System.out.println("--> "+tag);
			tags.add(tag);
		}
		return tags;
	}
	
	public static String loadGitOpsApps(String gitOpsAppsRepoUrl) throws IllegalStateException, GitAPIException {

		UUID uuid = UUID.randomUUID();
		String path = pathWorkspace+"//ocp-gitops-apps-deploy-"+uuid;
		
		File workingDirectory = null;
		workingDirectory = new File(path);
		workingDirectory.delete();
		workingDirectory.mkdirs();
						
		try {	
			gitOpsApp = Git.cloneRepository()
					  .setURI(gitOpsAppsRepoUrl)
					  .setDirectory(workingDirectory)
					  .call();
		} catch (Exception e) {
			System.out.println("Git Clone exception");
			e.printStackTrace();
			gitOpsApp = Git.init().setDirectory(workingDirectory).call();
		}	
		return path;
		
	}
	
	public static void commitAndPush() throws NoFilepatternException, GitAPIException, URISyntaxException {
		
		gitOpsApp.add().addFilepattern(".").call();
		
		// Now, we do the commit with a message

		RevCommit rev =	gitOpsApp.commit().setAuthor("ksc", "ksc@example.com").setMessage("Modified by ITS4U PlaceHolderControler").call();
		System.out.println("Commit ID = "+rev.getId().toString().substring(7, 47));
		System.out.println("Commit Time = "+rev.getCommitTime());
		
		RemoteAddCommand remoteAddCommand = gitOpsApp.remoteAdd();
	    remoteAddCommand.setName("origin");
	    remoteAddCommand.setUri(new URIish(gitOpsAppsDeployUrl));
	    remoteAddCommand.call();

	    // push to remote:
	    PushCommand pushCommand = gitOpsApp.push();
	    String user = System.getenv("git.user");
	    String password = System.getenv("git.password");
	    pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, password));
	    // you can add more settings here if needed
	    pushCommand.call();
	    System.out.println("Project modified and pushed");
	    
		// clean
		System.out.println("Clean workspace");
		FileSystemUtils.deleteRecursively(new File("pathWorkspace"));
		System.out.println("Workspace cleaned");
		
	}
	
}
