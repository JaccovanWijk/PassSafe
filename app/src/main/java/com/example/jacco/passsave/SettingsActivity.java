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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileInputStream;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements FirebaseHelper.CallBack{

    public String[] allQuestions = {"What is your fathers first name?", "What is your mothers first name?",
                                    "What was your first pets name?", "Which city/town were you born in?"};
    public ArrayList<Question> questions;
    public String username;
    public String password;
    public FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            username = user.getEmail();
        }

        questions = new ArrayList<>();

//        readPassword();

        TextView usernameText = findViewById(R.id.username);
        usernameText.setText(username);

        FirebaseHelper helper = new FirebaseHelper(this);
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

        EditText oldPasswordText = findViewById(R.id.oldPassword);
        EditText newPassword1Text = findViewById(R.id.newPassword1);
        EditText newPassword2Text = findViewById(R.id.newPassword2);

        String oldPassword = oldPasswordText.getText().toString();
        String newPassword1 = newPassword1Text.getText().toString();
        String newPassword2 = newPassword2Text.getText().toString();

        if(oldPassword.length() == 0 || newPassword1.length() == 0 || newPassword2.length() == 0) {
            Log.d("Error", "something is empty!");
        } else if(!oldPassword.equals(AES.decrypt(password, "randomKey"))) {
            Log.d("Error", "Wrong password!");
        } else if(!newPassword1.equals(newPassword2)) {
            Log.d("Error", "New passwords do not match!");
        } else {

            user.updatePassword(newPassword1)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Change", "User password updated.");
                            }
                        }
                    });

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            String text = "Changed password!";
            Toast.makeText(context, text, duration).show();

            oldPasswordText.setText("");
            newPassword1Text.setText("");
            newPassword2Text.setText("");
        }
    }

    public void addQuestionButtonClicked(View view) {
        Spinner spinner = findViewById(R.id.spinner);
        String question = spinner.getSelectedItem().toString();

        EditText answerText = findViewById(R.id.answer);
        String answer = answerText.getText().toString();

        Question newQuestion = new Question(question,answer);

        questions.add(newQuestion);

        FirebaseHelper helper = new FirebaseHelper(this);
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

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // user isn't signed in
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);

            finish();
        }
    }
}
