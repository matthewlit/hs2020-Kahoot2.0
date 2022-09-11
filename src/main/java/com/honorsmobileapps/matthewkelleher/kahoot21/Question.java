package com.honorsmobileapps.matthewkelleher.kahoot21;

import java.io.Serializable;

public class Question implements Serializable {

    private String prompt;
    private String type;

    public Question() {
        prompt = "";
        type = "";
    }

    public Question(String type) {
        prompt = "";
        this.type = type;
    }

    public Question(String prompt, String type){
        this.prompt = prompt;
        this.type = type;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
