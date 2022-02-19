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
import org.eclipse.jgit.api.RemoteRemoveCommand;
import org.eclipse.jgit.api.RemoteSetUrlCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FileUtils;
import org.springframework.util.FileSystemUtils;

import com.its4u.models.Environments;

public class GitController {

	private static String gitAppsUrl = "https://github.com/kevbrain/";
	
	//private static String gitOpsAppsDeployUrl = "https://github.com/kevbrain/ocp-gitops-apps-deploy.git";
	
	private static String pathWorkspace = "/git-workspace";
	
	//private static Git gitOpsApp;
	
	//private static Git gitApp;
	
	//private static Git gitops;
			
	
	@SuppressWarnings("deprecation")
	public static Git loadGitApps(String project) throws IllegalStateException, GitAPIException {

		
		String gitAppsUrlProject = gitAppsUrl+project+".git";
		UUID uuid = UUID.randomUUID();
		String path = pathWorkspace+"//"+project+"-"+uuid;
		
		File workingDirectory = null;
		workingDirectory = new File(path);
		workingDirectory.delete();
		workingDirectory.mkdirs();
		
		Git gitRepo = null;
						
		try {	
			gitRepo = Git.cloneRepository()
					  .setURI(gitAppsUrlProject)
					  .setDirectory(workingDirectory)
					  .call();
		
		} catch (Exception e) {
			System.out.println("Git Clone exception");
			e.printStackTrace();
			gitRepo = Git.init().setDirectory(workingDirectory).call();
		}	
		
		return gitRepo;
		
	}
	
	public static String getRepoPath(Git gitrepo) {
		String gitRepoPath = gitrepo.getRepository().getDirectory().getPath().toString();
		return gitRepoPath.substring(0,gitRepoPath.length()-5);		
	}
	
	public static List<String> searchTagsGitApps(Git gitRepo) {
		List<String> tags = new ArrayList<String>();
		Map<String,Ref> tagsList = gitRepo.getRepository().getTags();
		for (String tag:tagsList.keySet()) {
			tags.add(tag);
		}
		return tags;
	}
	
	public static Git loadGitOpsApps(String gitOpsAppsRepoUrl) throws IllegalStateException, GitAPIException {

		UUID uuid = UUID.randomUUID();
		String path = pathWorkspace+"//ocp-gitops-apps-deploy-"+uuid;
		
		File workingDirectory = null;
		workingDirectory = new File(path);
		workingDirectory.delete();
		workingDirectory.mkdirs();
				
		Git gitRepo = null;
		try {	
			gitRepo = Git.cloneRepository()
					  .setURI(gitOpsAppsRepoUrl)
					  .setDirectory(workingDirectory)
					  .call();
		} catch (Exception e) {
			System.out.println("Git Clone exception");
			e.printStackTrace();
			gitRepo = Git.init().setDirectory(workingDirectory).call();
		}	
		return gitRepo;
		
	}
	
	public static Git loadGitOps(String gitOpsRepoUrl) throws IllegalStateException, GitAPIException {

		UUID uuid = UUID.randomUUID();
		String path = pathWorkspace+"//ocp-gitops-"+uuid;
		
		File workingDirectory = null;
		workingDirectory = new File(path);
		workingDirectory.delete();
		workingDirectory.mkdirs();
		
		Git gitRepo = null;
						
		try {	
			gitRepo = Git.cloneRepository()
					  .setURI(gitOpsRepoUrl)
					  .setDirectory(workingDirectory)
					  .call();
		} catch (Exception e) {
			System.out.println("Git Clone exception");
			e.printStackTrace();
			gitRepo = Git.init().setDirectory(workingDirectory).call();
		}	
		return gitRepo;
		
	}
	
public static void commitAndPushGitOps(Environments env,Git gitRepo) throws NoFilepatternException, GitAPIException, URISyntaxException {
		
		gitRepo.add().addFilepattern(".").call();
		
		// Now, we do the commit with a message
	
		RevCommit rev =	gitRepo.commit().setAuthor("ksc", "ksc@example.com").setMessage("Modified by ITS4U PlaceHolderControler").call();
		System.out.println("Commit ID = "+rev.getId().toString().substring(7, 47));
		System.out.println("Commit Time = "+rev.getCommitTime());
		
		
		

	    // push to remote:
	    PushCommand pushCommand = gitRepo.push();
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
	
	public static void commitAndPushGitOpsApp(Environments env,Git gitRepo) throws NoFilepatternException, GitAPIException, URISyntaxException {
		
		
		gitRepo.add().addFilepattern(".").call();
		
		// Now, we do the commit with a message

		RevCommit rev =	gitRepo.commit().setAuthor("ksc", "ksc@example.com").setMessage("Modified by ITS4U PlaceHolderControler").call();
		System.out.println("Commit ID = "+rev.getId().toString().substring(7, 47));
		System.out.println("Commit Time = "+rev.getCommitTime());
		
				
		RemoteAddCommand remoteAddCommand = gitRepo.remoteAdd();
	    remoteAddCommand.setName("origin");
	    remoteAddCommand.setUri(new URIish(env.getArgoEnv().getGitOpsAppsRepo()));
	    remoteAddCommand.call();

	    // push to remote:
	    PushCommand pushCommand = gitRepo.push();
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
