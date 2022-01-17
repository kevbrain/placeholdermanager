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
	
	private List<String> listLogs = new ArrayList<String>();
	
	private int number;
	
	@PostConstruct
    public void init()  {
		listLogs = new ArrayList<String>();
    }
 
	public void increment() {
        number++;
    }
	
	public int getNumber() {
        return number;
    }

    public void log(String log) {    	
        
    	logPile.push(log+"[<p style=\"color:green\">OK</p>]");
    			
    }
  
	public List<String> getListLogs() {
		
		//for(int i = 0; i < 5; i++) {
			if (!logPile.isEmpty()) {
				String myint = (String)logPile.pop();
				listLogs.add(myint);	
			}				
		//}
		return listLogs;
	}

	public void setListLogs(List<String> listLogs) {
		this.listLogs = listLogs;
	}
    
    
}