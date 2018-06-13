package com.example.jacco.passsave;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.lang.reflect.Array;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Intent intent = getIntent();
        filledIn = (Boolean) intent.getSerializableExtra("boolean");
        account = (Account) intent.getSerializableExtra("account");

        foundQuestions = new ArrayList<>();

        readPassword();

        if (filledIn) {
            FirebaseHelper helper = new FirebaseHelper(this);
            helper.getQuestions(this);
        } else {

            ArrayList<String> unusedQuestions = new ArrayList<>();
            for (String aQuestion : allQuestions) {
                unusedQuestions.add(aQuestion);
            }

            Spinner spinner = findViewById(R.id.question);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unusedQuestions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(0);
        }

    }

    @Override
    public void gotQuestions(ArrayList<Question> questions) {

        foundQuestions = questions;

        ArrayList<String> usedQuestions = new ArrayList<>();
        for (Question aQuestion : questions) {
            usedQuestions.add(aQuestion.getQuestion());
        }

        Spinner spinner = findViewById(R.id.question);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, usedQuestions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        if (questions.size() == 0) {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            String text = "No questions added yet, please add them in the settings.";
            Toast.makeText(context, text, duration).show();

            Intent intent = new Intent(QuestionActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

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

    public void submitClicked(View view) {

        Spinner spinner = findViewById(R.id.question);
        String question = spinner.getSelectedItem().toString();

        EditText answerText = findViewById(R.id.answer);
        String givenAnswer = answerText.getText().toString();

        if (filledIn) {

            for (Question aQuestion : foundQuestions) {
                if (aQuestion.getQuestion().equals(question)) {
                    answer = aQuestion.getAnswer();
                }
            }

            if (givenAnswer.equals(answer)) {

                Intent intent = new Intent(QuestionActivity.this, PasswordActivity.class);
                intent.putExtra("account", account);
                startActivity(intent);

                finish();

            } else {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_LONG;
                String text = "Wrong answer!";
                Toast.makeText(context, text, duration).show();
            }

        } else {

            Question mainQuestion = new Question(question, givenAnswer);

            FirebaseHelper helper = new FirebaseHelper(this);
            helper.addQuestion(mainQuestion);

            Intent intent = new Intent(QuestionActivity.this, LoginActivity.class);
            startActivity(intent);

            finish();

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

            String password = info[0];

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
}
