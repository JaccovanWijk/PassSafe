package com.example.jacco.passsave;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;

public class NewAccountActivity extends AppCompatActivity implements FirebaseHelper.CallBack{

    int[] ids = {R.id.account, R.id.username, R.id.password};
    String username;
    String password;
    String[] letters = {
            "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P",
            "Q","R","S","T","U","V","W","X","Y","Z","a","b","c","d","e","f",
            "g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v",
            "w","x","y","z","0","1","2","3","4","5","6","7","8","9","+","/",
            "!","@","#","$","%","&"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        readPassword();
    }

    @Override
    public void gotQuestions(ArrayList<Question> questions) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        String text = "Something went wrong...";
        Toast.makeText(context, text, duration).show();

        // log error
        Log.e("ERROR", "You're not supposed to be here!!");
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
        Log.e("ERROR", "you're not supposed to be here!!");
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

    // add menu
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
                Intent intent = new Intent(NewAccountActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.AccountSettings:
                Intent intent2 = new Intent(NewAccountActivity.this, SettingsActivity.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void acceptButtonClicked(View view) {
        EditText accountText = findViewById(ids[0]);
        EditText usernameText = findViewById(ids[1]);
        EditText passwordText = findViewById(ids[2]);

        String account = accountText.getText().toString();
        String newPassword = passwordText.getText().toString();
        String newUsername = usernameText.getText().toString();

        //TODO HIJ ACCEPTEERT GEEN WACHTWOORDEN VAN < 8 IN DE ENCRIPTIE, WHY NOT EN HOE ZOU IK DIT KUNNEN ONTWIJKEN
        while (newPassword.length() < 8) {
            newPassword += "";
        }

        if (account.length() == 0 || newUsername.length() == 0 || newPassword.length() == 0) {
            Log.d("error", "Something is empty");
        } else {

            Account newAccount = new Account(account, newUsername, newPassword);

            newAccount.setAccount(account);
            newAccount.setUsername(newUsername);
            newAccount.setPassword(AES.encrypt(newPassword, password));

            FirebaseHelper helper = new FirebaseHelper(this, username);
            helper.addAccount(newAccount);

            Intent intent = new Intent(NewAccountActivity.this, AccountsActivity.class);
            startActivity(intent);
        }
    }


    public void randomPasswordClicked(View view) {
        EditText passwordText = findViewById(ids[2]);
        String randomPassword = "";

        for (int i = 0, n = 8; i < n; i++) {
            int idx = new Random().nextInt(letters.length);
            String random = letters[idx];
            randomPassword += random;
        }

        password = randomPassword;
        passwordText.setText(password);
    }

    public void visibilityClicked(View view) {

        EditText passwordText = findViewById(ids[2]);

        CheckBox checkBox = (CheckBox) view;
        Boolean checked = checkBox.isChecked();

        if (checked) {
            passwordText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    public void readPassword() {

        try {
            FileInputStream fin = openFileInput("StorePass");

            int c;
            String temp = "";
            while ((c = fin.read()) != -1) {
                temp += Character.toString((char) c);
            }

            String[] info = temp.split("\\s+");

            username = info[0];
            password = info[1];

            //string temp contains all the data of the file.
            fin.close();
        } catch(Exception e) {
            Log.e("error","Couldn't find file");
        }
    }

}
