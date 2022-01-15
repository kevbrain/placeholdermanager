package com.its4u.gitops;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	
	public static HashMap<String,String> parser(Path path,HashMap<String,String> placeholdersValues)  {
		System.out.println(path.getFileName());
		String read = null;
		boolean found = false;
		try {
			read = Files.readString(path);
			Pattern pattern = Pattern.compile("\\{\\{(.*)\\}\\}");
			Matcher matcher = pattern.matcher(read);
			while(matcher.find()){
				 found = true;
				 break;
			}
			
			if (found) {
				read = replacePlaceholder(read,placeholdersValues);
				Files.write(path, read.getBytes());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		return placeholdersValues;
	}
	
	public static HashMap<String,String> parserAllPlaceHolders(Path path)  {
		HashMap<String,String> placeholders = new HashMap<String, String>();
		System.out.println(path.getFileName());
		String read = null;
		boolean found = false;
		try {
			read = Files.readString(path); 
			Pattern pattern = Pattern.compile("\\{\\{(?:[^{}]|((g<0>)))+\\}\\}");
			//Pattern pattern = Pattern.compile("\\{\\{(.*)\\}\\}");
			Matcher matcher = pattern.matcher(read);
			while(matcher.find()){				
				 System.out.println("founded : "+matcher);
				 placeholders.put(matcher.toString(),matcher.toString());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		return placeholders;
	}
	
		
	public static String replacePlaceholder(String read,HashMap<String,String> placeholdersValues) {
										
		for (String key:placeholdersValues.keySet()) {
			read = read.replace("{{"+key+"}}", placeholdersValues.get(key));
		}
		return read;
	}

}
