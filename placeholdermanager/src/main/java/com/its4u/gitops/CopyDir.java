package com.its4u.gitops;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;

public class CopyDir extends SimpleFileVisitor<Path> {
    private Path sourceDir;
    private Path targetDir;
 
    public CopyDir(Path sourceDir, Path targetDir) {
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
    }
 
    @Override
    public FileVisitResult visitFile(Path file,
            BasicFileAttributes attributes) {
 
        try {
            Path targetFile = targetDir.resolve(sourceDir.relativize(file));
            Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            System.err.println(ex);
        }
 
        return FileVisitResult.CONTINUE;
    }
 
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
            BasicFileAttributes attributes) {
        try {
            Path newDir = targetDir.resolve(sourceDir.relativize(dir));
            Files.createDirectory(newDir);
        } catch (IOException ex) {
            System.err.println(ex);
        }
 
        return FileVisitResult.CONTINUE;
    }
 
   
}

