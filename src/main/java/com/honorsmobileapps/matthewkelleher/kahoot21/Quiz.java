package com.honorsmobileapps.matthewkelleher.kahoot21;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Quiz implements Serializable {
    private String quizTitle;
    private String quizAuthor;
    private String id;
    private List<Question> questions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Quiz(){
        questions = new ArrayList<>();
        quizTitle = "";
        quizAuthor = "";
        id = "";
    }

    public Quiz(String mQuizTitle, String mQuizAuthor, String id, ArrayList<Question> questions) {
        this.quizTitle = mQuizTitle;
        this.quizAuthor = mQuizAuthor;
        this.id = id;
        this.questions = questions;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public void setQuizAuthor(String quizAuthor) {
        this.quizAuthor = quizAuthor;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions){
        this.questions = questions;
    }

    public String getQuizAuthor() {
        return quizAuthor;
    }

    public void addQuestion(Question q){
        questions.add(q);
    }

    public void removeQuestion(Question q){
        questions.remove(q);
    }
}
