package com.example.jacco.passsave;

/*
 Model class for Question object.
 */

public class Question {

    public Question() {
    }

    public String question;
    public String answer;

    public Question(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
