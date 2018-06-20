package com.example.jacco.passsave;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.io.FileOutputStream;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements FirebaseHelper.CallBack{

    public String[] allQuestions = {"What is your fathers first name?", "What is your mothers first name?",
                                    "What was your first pets name?", "Which city/town were you born in?"};
    public ArrayList<Question> questions;
    public String username;
    public String password;
    public String oldPassword;
    public String newPassword1;
    public String newPassword2;
    public String key;
    public FirebaseUser user;
    public Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            username = user.getEmail();
        }

        questions = new ArrayList<>();

        readPassword();

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
    public void gotAccounts(ArrayList<Account> accounts) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        String text = "Something went wrong...";
        Toast.makeText(context, text, duration).show();

        // log error
        Log.e("ERROR", "You're not supposed to be here!");
    }
    @Override
    public void gotError(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        String text = "Something went wrong...";
        Toast.makeText(context, text, duration).show();

        // log error
        Log.e("ERROR", message);
    }
    @Override
    public void gotKey(String aKey) {
        //TODO DO SOMETHING WITH KEY
        key = aKey;

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompts2, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.key);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String inputKey = userInput.getText().toString();

                                checkKey(inputKey);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void checkKey(String inputKey) {

        EditText oldPasswordText = findViewById(R.id.oldPassword);
        EditText newPassword1Text = findViewById(R.id.newPassword1);
        EditText newPassword2Text = findViewById(R.id.newPassword2);

        System.out.println("[" + password + "],[" + AES.encrypt(inputKey,password) + "],[" + key + "]");

        if (key.equals(AES.encrypt(inputKey,password))) {

            //TODO WERKT NOG NIET
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

            storePassword(newPassword1);

            FirebaseHelper helper = new FirebaseHelper(context);
            helper.changePassword(password, newPassword1);
        }
        else {

            oldPasswordText.setText("");
            newPassword1Text.setText("");
            newPassword2Text.setText("");

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            String text = "Wrong Key!";
            Toast.makeText(context, text, duration).show();
        }
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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        oldPassword = oldPasswordText.getText().toString();
        newPassword1 = newPassword1Text.getText().toString();
        newPassword2 = newPassword2Text.getText().toString();

        byte[] bytesOldPassword = Base64.encode(oldPassword.getBytes(), Base64.DEFAULT);
        String encodedOldPassword = new String(bytesOldPassword);
        encodedOldPassword = encodedOldPassword.replace("\n", "").replace("\r", "");

        if(oldPassword.length() == 0 || newPassword1.length() == 0 || newPassword2.length() == 0) {
            Log.d("Error", "something is empty!");
        } else if(!password.equals(encodedOldPassword)) {
            System.out.println("[" + encodedOldPassword + "],[" + password + "]");
            Log.d("Error", "Wrong password!");
        } else if(!newPassword1.equals(newPassword2)) {
            Log.d("Error", "New passwords do not match!");
        } else if (newPassword1.length() < 7) {
            Log.d("Error", "New password is too small!");
        } else {

            FirebaseHelper helper = new FirebaseHelper(this);
            helper.getKey(this);
        }
    }

    public void addQuestionButtonClicked(View view) {
        Spinner spinner = findViewById(R.id.spinner);
        Object questionObject = spinner.getSelectedItem();

        if (questionObject != null) {

            String question = questionObject.toString();

            EditText answerText = findViewById(R.id.answer);
            String answer = AES.encrypt(answerText.getText().toString(), password);

            Question newQuestion = new Question(question, answer);

            questions.add(newQuestion);

            FirebaseHelper helper = new FirebaseHelper(this);
            helper.addQuestion(newQuestion);

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            String text = "Question added!";
            Toast.makeText(context, text, duration).show();

            gotQuestions(questions);
        } else {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            String text = "sfsdfs";
            Toast.makeText(context, text, duration).show();
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

    // Store encrypted password in a file
    public void storePassword(String newPassword) {
        String filename = "StorePass";

        byte[] bytesEncoded = Base64.encode(newPassword.getBytes(), Base64.DEFAULT);
        String fileContents = new String(bytesEncoded);
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
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
