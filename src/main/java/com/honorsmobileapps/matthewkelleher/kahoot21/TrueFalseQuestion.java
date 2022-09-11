package com.honorsmobileapps.matthewkelleher.kahoot21;

import java.io.Serializable;

public class TrueFalseQuestion extends Question implements Serializable {
    Boolean answer;

    public TrueFalseQuestion(){
        super();
        answer = true;
    }

    public TrueFalseQuestion(String type){
        super(type);
        answer = true;
    }

    public TrueFalseQuestion(String prompt, Boolean answer, String type) {
        super(prompt, type);
        this.answer = answer;
    }

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }
}
