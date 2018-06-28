package com.example.jacco.passsave;
/*

 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class SettingsActivity extends AppCompatActivity implements FirebaseHelper.CallBack{

    public String[] allQuestions = {"What is your fathers first name?", "What is your mothers first name?",
                                    "What was your first pets name?", "Which city/town were you born in?"};
    public ArrayList<Question> questions;
    public String password;
    public String newPassword1;
    public String inputKey;
    public FirebaseUser user;
    public Context context = this;
    public String givenPassword;
    public FirebaseAuth mAuth;
    String[] letters = {
            "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P",
            "Q","R","S","T","U","V","W","X","Y","Z","a","b","c","d","e","f",
            "g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v",
            "w","x","y","z","0","1","2","3","4","5","6","7","8","9","+","/",
            "!","@","#","$","%","&"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String username = "";
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            username = user.getEmail();
        }

        questions = new ArrayList<>();

        readPassword();
        mAuth = FirebaseAuth.getInstance();

        TextView usernameText = findViewById(R.id.username);
        usernameText.setText(username);

        FirebaseHelper helper = new FirebaseHelper();
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
        messageUser("Something went wrong!");

        // log error
        Log.e("ERROR", "You're not supposed to be here!");
    }
    @Override
    public void gotError(String message) {
        messageUser("Something went wrong!");

        // log error
        Log.e("ERROR", message);
    }
    @Override
    public void gotKey(String aKey) {
        String key = aKey;
        inputKey = AES.encrypt(inputKey, password);

        if (key.equals(inputKey)) {
            // Popup with final check
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Changing your password will delete all your data! Are you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Delete item
                            changePassword();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Cancel
                        }
                    });
            builder.show();
        } else {
            messageUser("Wrong Key!");
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

                mAuth.signOut();

                finish();
                return true;
            case R.id.AccountSettings:
                Intent intent2 = new Intent(SettingsActivity.this, SettingsActivity.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changePasswordClicked(View view) {

        // Load in input
        EditText oldPasswordText = findViewById(R.id.oldPassword);
        EditText newPassword1Text = findViewById(R.id.newPassword1);
        EditText newPassword2Text = findViewById(R.id.newPassword2);
        String oldPassword = oldPasswordText.getText().toString();
        newPassword1 = newPassword1Text.getText().toString();
        String newPassword2 = newPassword2Text.getText().toString();

        // Encrypt password to compare
        byte[] bytesOldPassword = Base64.encode(oldPassword.getBytes(), Base64.DEFAULT);
        String encodedOldPassword = new String(bytesOldPassword);
        encodedOldPassword = encodedOldPassword.replace("\n", "").replace("\r", "");

        // Check if input is correct
        if(oldPassword.length() == 0 || newPassword1.length() == 0 || newPassword2.length() == 0) {
            Log.d("Error", "something is empty!");
            messageUser("Not everything is filled in!");
        } else if(!password.equals(encodedOldPassword)) {
            Log.d("Error", "Wrong password!");
            messageUser("Wrong password!");
        } else if(!newPassword1.equals(newPassword2)) {
            Log.d("Error", "New passwords do not match!");
            messageUser("Passwords do not match!");
        } else if (newPassword1.length() < 7) {
            Log.d("Error", "New password is too small!");
            messageUser("New password is too small!");
        } else {

            // Create popup menu to ask for key
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.prompts2, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);
            final EditText userInput = (EditText) promptsView.findViewById(R.id.key);
            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    inputKey = userInput.getText().toString();

                                    loadKey();

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
    }

    public void loadKey() {
        FirebaseHelper helper = new FirebaseHelper();
        helper.getKey(this);
    }

    public void changePassword() {

        FirebaseHelper helper = new FirebaseHelper();
        helper.changePassword(newPassword1);

        String key = createKey();
        helper.addKey(key, newPassword1);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your new key is: " + key)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        messageUser("Please set a new question!");

                        Intent intent = new Intent(SettingsActivity.this, QuestionActivity.class);
                        intent.putExtra("boolean", false);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                        finish();
                    }
                });
        builder.show();

    }

    public String createKey() {
        String key = "";
        for (int i = 0, n = 24; i < n; i++) {
            int idx = new Random().nextInt(letters.length);
            String random = letters[idx];
            key += random;
        }
        return key;
    }

    public void addQuestionButtonClicked(View view) {
        Spinner spinner = findViewById(R.id.spinner);
        Object questionObject = spinner.getSelectedItem();

        if (questionObject != null) {

            String question = questionObject.toString();

            EditText answerText = findViewById(R.id.answer);
            String answer = AES.encrypt(answerText.getText().toString(), password);

            final Question newQuestion = new Question(question, answer);

            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.prompts_password, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.password);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    givenPassword = userInput.getText().toString();
                                    checkPassword(newQuestion);
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
        } else {
            messageUser("No questions left to answer!");
        }
    }

    public void checkPassword(Question newQuestion) {

        byte[] bytesEncoded = Base64.encode(givenPassword.getBytes(), Base64.DEFAULT);
        String encryptedPassword = new String(bytesEncoded);
        encryptedPassword = encryptedPassword.replace("\n", "");

        if (password.equals(encryptedPassword)) {
            questions.add(newQuestion);

            FirebaseHelper helper = new FirebaseHelper();
            helper.addQuestion(newQuestion);

            messageUser("Question added!");

            gotQuestions(questions);
        } else {
            messageUser("Wrong password!");
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

        readPassword();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // user isn't signed in
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
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
