package com.its4u.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;

import org.springframework.stereotype.Component;

@Component
@ViewScoped
public class PollView implements Serializable {
	
	private Stack<String> logPile = new Stack<String>();
	
	private String listLogs;
	
	private int number;
	
	@PostConstruct
    public void init()  {
		listLogs = "";
    }
 
	public void increment() {
		if (!logPile.isEmpty()) {
			String mystr = (String)logPile.pop();
			listLogs=listLogs+mystr;	
		}	  
    }
	
	public int getNumber() {
        return number;
    }

    public void log(String log) {    	
        
    	String logFormated = log+"<span \"text-color:green\">OK</span></br>";
    	logPile.push(logFormated);
    			
    }
  
	public String getListLogs() {
		
		return listLogs;
	}

	public void setListLogs(String listLogs) {
		this.listLogs = listLogs;
	}
    
    
}