package com.example.jacco.passsave;

/**
 * Created by Jacco on 7-6-2018.
 */

public class Question {
    public String personalQuestion;
    public String answer;

    public Question(String personalQuestion, String answer) {
        this.personalQuestion = personalQuestion;
        this.answer = answer;
    }

    public String getPersonalQuestion() {
        return personalQuestion;
    }

    public void setPersonalQuestion(String personalQuestion) {
        this.personalQuestion = personalQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
