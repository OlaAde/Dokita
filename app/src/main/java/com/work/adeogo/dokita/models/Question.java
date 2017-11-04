package com.work.adeogo.dokita.models;

/**
 * Created by Adeogo on 10/28/2017.
 */

public class Question {
    private String text;
    private String name;

    public Question(){}

    public Question(String text, String name){
        this.text = text;
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
