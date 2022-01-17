package com.its4u.beans;

import java.io.Serializable;

import javax.faces.view.ViewScoped;

import org.springframework.stereotype.Component;

@Component
@ViewScoped
public class PollView implements Serializable {

    private int number;

    public void increment() {
        number++;
    }

    public int getNumber() {
        return number;
    }
}