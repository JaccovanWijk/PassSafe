package com.example.jacco.passsave;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity implements FirebaseHelper.CallBack{

    //TODO MAAK EEN SPINNER MET ALLE VRAGEN IN DE XML IPV 1 UITKIEZEN
    public String[] questions = {"What is your fathers first name", "What is your mothers first name",
                                 "What was your first pets name", "Which city/town were you born in"};
    public ArrayList<Question> foundQuestions;
    public String selectedQuestion;
    public Question mainQuestion;
    public Boolean filledIn;
    public String username;
    public String answer;
    public String account;
    private Random randomGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Intent intent = getIntent();
        filledIn = (Boolean) intent.getSerializableExtra("boolean");
        account = (String) intent.getSerializableExtra("account");

        readPassword();

        if (filledIn) {
            FirebaseHelper helper = new FirebaseHelper(this, username);
            helper.getQuestions(this);
        } else {
            int index = randomGenerator.nextInt(questions.length);
            selectedQuestion = questions[index];

            TextView questionText = findViewById(R.id.question);
            questionText.setText(selectedQuestion);
        }

    }

    @Override
    public void gotQuestions(ArrayList<Question> questions) {

        foundQuestions = questions;

        int index = randomGenerator.nextInt(questions.size());
        mainQuestion = questions.get(index);

        TextView questionText = findViewById(R.id.question);
        questionText.setText(mainQuestion.getQuestion());
        answer = mainQuestion.getAnswer();

    }
    @Override
    public void gotQuestionsError(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        String text = "Something went wrong...";
        Toast.makeText(context, text, duration).show();

        // log error
        Log.e("ERROR", message);
    }
    @Override
    public void gotAccounts(ArrayList<Account> accounts) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        String text = "Something went wrong...";
        Toast.makeText(context, text, duration).show();

        // log error
        Log.e("ERROR", "You're not supposed to be here!!");
    }
    @Override
    public void gotAccountsError(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        String text = "Something went wrong...";
        Toast.makeText(context, text, duration).show();

        // log error
        Log.e("ERROR", message);
    }

    // Add menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.LogOut:
                //TODO DIT WERKT NOG NIET MET DE TERUGKNOP
                Intent intent = new Intent(QuestionActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.AccountSettings:
                Intent intent2 = new Intent(QuestionActivity.this, SettingsActivity.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void submitClicked(View view) {

        EditText answerText = findViewById(R.id.answer);
        String givenAnswer = answerText.getText().toString();

        if (filledIn) {

            if (givenAnswer.equals(answer)) {

                Intent intent = new Intent(QuestionActivity.this, PasswordActivity.class);
                startActivity(intent);

            } else {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_LONG;
                String text = "Wrong answer!";
                Toast.makeText(context, text, duration).show();
            }

        } else {

            mainQuestion.setQuestion(selectedQuestion);
            mainQuestion.setAnswer(givenAnswer);

            FirebaseHelper helper = new FirebaseHelper(this, username);
            helper.addQuestion(mainQuestion);

            Intent intent = new Intent(QuestionActivity.this, LoginActivity.class);
            startActivity(intent);

        }
    }

    public void changeQuestionClicked(View view) {
        if (filledIn) {
            int index = randomGenerator.nextInt(foundQuestions.size());
            mainQuestion = foundQuestions.get(index);

            TextView questionText = findViewById(R.id.question);
            questionText.setText(mainQuestion.getQuestion());
            answer = mainQuestion.getAnswer();
        } else {
            int index = randomGenerator.nextInt(questions.length);
            selectedQuestion = questions[index];

            TextView questionText = findViewById(R.id.question);
            questionText.setText(selectedQuestion);
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

            username = info[0];

            //string temp contains all the data of the file.
            fin.close();
        } catch(Exception e) {
            Log.e("error","Couldn't find file");
        }
    }
}
