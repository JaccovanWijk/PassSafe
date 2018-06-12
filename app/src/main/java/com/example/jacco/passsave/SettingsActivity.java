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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements FirebaseHelper.CallBack{

    public String[] allQuestions = {"What is your fathers first name?", "What is your mothers first name?",
                                    "What was your first pets name?", "Which city/town were you born in?"};
    public ArrayList<Question> questions;
    public String username;
    public String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        questions = new ArrayList<>();

        readPassword();

        TextView usernameText = findViewById(R.id.username);
        usernameText.setText(username);

        FirebaseHelper helper = new FirebaseHelper(this, username);
        helper.getQuestions(this);
    }

    @Override
    public void gotQuestions(ArrayList<Question> questions) {
        this.questions = questions;

        EditText answerText = findViewById(R.id.answer);
        answerText.setText("");

        ArrayList<String> usedQuestions = new ArrayList<>();
        for (Question aQuestion : questions) {
            usedQuestions.add(aQuestion.getQuestion());
        }

        ArrayList<String> unusedQuestions = new ArrayList<>();
        for (String aQuestion : allQuestions) {
            if (!usedQuestions.contains(aQuestion)) {
                unusedQuestions.add(aQuestion);
            }
        }

        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unusedQuestions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
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
        Log.e("ERROR", "You're not supposed to be here!");
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
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.AccountSettings:
                Intent intent2 = new Intent(SettingsActivity.this, SettingsActivity.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changePasswordClicked(View view) {
        
    }

    public void addQuestionButtonClicked(View view) {
        Spinner spinner = findViewById(R.id.spinner);
        String question = spinner.getSelectedItem().toString();

        EditText answerText = findViewById(R.id.answer);
        String answer = answerText.getText().toString();

        Question newQuestion = new Question(question,answer);

        questions.add(newQuestion);

        FirebaseHelper helper = new FirebaseHelper(this, username);
        helper.addQuestion(newQuestion);

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        String text = "Question added!";
        Toast.makeText(context, text, duration).show();

        gotQuestions(questions);
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
