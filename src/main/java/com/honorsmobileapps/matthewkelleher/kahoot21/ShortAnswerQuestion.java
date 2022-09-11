package com.honorsmobileapps.matthewkelleher.kahoot21;

import java.io.Serializable;

public class ShortAnswerQuestion extends Question  implements Serializable {
    String answer;

    public ShortAnswerQuestion(){
        super();
        answer = "";
    }

    public ShortAnswerQuestion(String type){
        super(type);
        answer = "";
    }

    public ShortAnswerQuestion(String prompt, String answer, String type) {
        super(prompt, type);
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
