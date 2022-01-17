package com.its4u.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.faces.view.ViewScoped;

import org.springframework.stereotype.Component;

@Component
@ViewScoped
public class PollView implements Serializable {
	
	private Stack<String> logPile = new Stack<String>();
	
	private List<String> listLogs;
	
	private int number;
 
	public void increment() {
        number++;
    }
	
	public int getNumber() {
        return number;
    }

    public void log(String log) {    	
        
    	logPile.push(log);
    }

  
	public List<String> getListLogs() {
		listLogs = new ArrayList<String>();
		for(int i = 0; i < 5; i++) {
			String myint = (String)logPile.pop();
			listLogs.add(myint);
		}
		return listLogs;
	}

	public void setListLogs(List<String> listLogs) {
		this.listLogs = listLogs;
	}
    
    
}