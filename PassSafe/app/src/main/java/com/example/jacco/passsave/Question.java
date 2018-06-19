package com.example.jacco.passsave;

/**
 * Created by Jacco on 7-6-2018.
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
