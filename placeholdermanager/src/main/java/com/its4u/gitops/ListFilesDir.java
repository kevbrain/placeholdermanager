package com.its4u.gitops;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class ListFilesDir extends SimpleFileVisitor<Path> {
    private Path sourceDir;
    private List<String> listFilesName;
 
    public ListFilesDir(Path sourceDir, List<String> listFilesName) {
        this.sourceDir = sourceDir;
        this.listFilesName = listFilesName;
    }
 
    @Override
    public FileVisitResult visitFile(Path file,
            BasicFileAttributes attributes) {
 
        listFilesName.add(file.getFileName().toString());
 
        return FileVisitResult.CONTINUE;
    }
 
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
            BasicFileAttributes attributes) {
      
 
        return FileVisitResult.CONTINUE;
    }

	public List<String> getListFilesName() {
		return listFilesName;
	}

	public void setListFilesName(List<String> listFilesName) {
		this.listFilesName = listFilesName;
	}
    
    
 
   
}

