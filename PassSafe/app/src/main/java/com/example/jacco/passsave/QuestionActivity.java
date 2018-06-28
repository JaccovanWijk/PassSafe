package com.example.jacco.passsave;
/*
This activity displays a question which can be answered. It has a filled-in mode and a not-filled-in
mode. Not-filled-in mode lets a user upload a question to firebase.Filled-in mode checks if the
given answer corresponds to the given answer when the user uploaded the question.
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity implements FirebaseHelper.CallBack{

    public String[] allQuestions = {"What is your fathers first name?", "What is your mothers first name?",
                                 "What was your first pets name?", "Which city/town were you born in?"};
    public ArrayList<Question> foundQuestions;
    public Boolean filledIn;
    public String username;
    public String answer;
    public Account account;
    public String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Intent intent = getIntent();
        filledIn = (Boolean) intent.getSerializableExtra("boolean");
        account = (Account) intent.getSerializableExtra("account");

        readPassword();
        foundQuestions = new ArrayList<>();

        if (filledIn) {
            // Load in Questions
            FirebaseHelper helper = new FirebaseHelper();
            helper.getQuestions(this);
        } else {
            // Pick a random question and display it
            int idx = new Random().nextInt(allQuestions.length);
            String random = allQuestions[idx];
            TextView textView = findViewById(R.id.question);
            textView.setText(random);
        }

    }

    /*
    Check questions that you received and display it.
     */
    @Override
    public void gotQuestions(ArrayList<Question> questions) {

        foundQuestions = questions;

        // Check which questions haven't been used
        ArrayList<String> usedQuestions = new ArrayList<>();
        for (Question aQuestion : questions) {
            usedQuestions.add(aQuestion.getQuestion());
        }

        TextView textView = findViewById(R.id.question);

        if (questions.size() == 0) {
            messageUser("Please set a new question!");

            Intent intent = new Intent(QuestionActivity.this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

        } else {

            // Pick a random filled in question
            int idx = new Random().nextInt(usedQuestions.size());
            String random = usedQuestions.get(idx);

            textView.setText(random);

        }


    }
    @Override
    public void gotAccounts(ArrayList<Account> accounts) {
        messageUser("Something went wrong...");

        // log error
        Log.e("ERROR", "You're not supposed to be here!!");
    }
    @Override
    public void gotKey(String key) {
        messageUser("Something went wrong...");

        // log error
        Log.e("ERROR", "You're not supposed to be here!");
    }
    @Override
    public void gotError(String message) {
        messageUser("Something went wrong...");

        // log error
        Log.e("ERROR", message);
    }

    /*
    Listen for submit button and check the given answer. Post it to Firebase if needed.
     */
    public void submitClicked(View view) {

        TextView textView = findViewById(R.id.question);
        String question = textView.getText().toString();
        EditText answerText = findViewById(R.id.answer);
        String givenAnswer = answerText.getText().toString();

        if (givenAnswer.length() == 0) {
            messageUser("Answer can't be empty!");
        } else {
            if (filledIn) {

                // Find answer for chosen question
                for (Question aQuestion : foundQuestions) {
                    if (aQuestion.getQuestion().equals(question)) {
                        answer = aQuestion.getAnswer();
                    }
                }

                givenAnswer = AES.encrypt(givenAnswer, password);

                //TODO SOMETHIMES HE POSTS THE PASSWORD AS ANSWER

                if (givenAnswer.equals(answer)) {

                    Intent intent = new Intent(QuestionActivity.this, PasswordActivity.class);
                    intent.putExtra("account", account);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

                    finish();

                } else {
                    messageUser("Wrong answer!");
                }

            } else {

                givenAnswer = givenAnswer.replace("\n", "");
                givenAnswer = AES.encrypt(givenAnswer, password);
                Question mainQuestion = new Question(question, givenAnswer);

                // Upload new question
                FirebaseHelper helper = new FirebaseHelper();
                helper.addQuestion(mainQuestion);

                Intent intent = new Intent(QuestionActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                finish();

            }
        }
    }

    public void readPassword() {

        try {
            FileInputStream fin = openFileInput("StorePass");

            int c;
            String temp = "";
            while ((c = fin.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }

            String[] info = temp.split("\\s+");

            password = info[0];

            //string temp contains all the data of the file.
            fin.close();
        } catch(Exception e) {
            Log.e("error","Couldn't find file");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // user isn't signed in
            Intent intent = new Intent(QuestionActivity.this, LoginActivity.class);
            startActivity(intent);

            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public void messageUser(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
