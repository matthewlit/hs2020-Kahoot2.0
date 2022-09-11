package com.honorsmobileapps.matthewkelleher.kahoot21;

import java.io.Serializable;
import java.util.ArrayList;

public class MultipleChoiceQuestion extends Question implements Serializable {

    private String answer;
    private String wrongAnswerOne;
    private String wrongAnswerTwo;
    private String wrongAnswerThree;

    public MultipleChoiceQuestion(){
        super();
        wrongAnswerOne = "";
        wrongAnswerTwo = "";
        wrongAnswerThree = "";
        answer = "";
    }

    public MultipleChoiceQuestion(String type){
        super(type);
        wrongAnswerOne = "";
        wrongAnswerTwo = "";
        wrongAnswerThree = "";
        answer = "";
    }

    public MultipleChoiceQuestion(String prompt, String answer, String wrongAnswerOne, String wrongAnswerTwo, String wrongAnswerThree, String type) {
        super(prompt,type);
        this.answer = answer;
        this.wrongAnswerOne = wrongAnswerOne;
        this.wrongAnswerTwo = wrongAnswerTwo;
        this.wrongAnswerThree = wrongAnswerThree;

    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getWrongAnswerOne() {
        return wrongAnswerOne;
    }

    public void setWrongAnswerOne(String wrongAnswerOne) {
        this.wrongAnswerOne = wrongAnswerOne;
    }

    public String getWrongAnswerTwo() {
        return wrongAnswerTwo;
    }

    public void setWrongAnswerTwo(String wrongAnswerTwo) {
        this.wrongAnswerTwo = wrongAnswerTwo;
    }

    public String getWrongAnswerThree() {
        return wrongAnswerThree;
    }

    public void setWrongAnswerThree(String wrongAnswerThree) {
        this.wrongAnswerThree = wrongAnswerThree;
    }
}
