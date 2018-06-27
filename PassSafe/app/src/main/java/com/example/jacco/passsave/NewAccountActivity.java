package com.example.jacco.passsave;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;
//TODO ZET BUTTON GOED IN LANDSCAPE MODE
public class NewAccountActivity extends AppCompatActivity {

    int[] ids = {R.id.account, R.id.username, R.id.password};
    public String password;
    public String[] letters = {
                            "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P",
                            "Q","R","S","T","U","V","W","X","Y","Z","a","b","c","d","e","f",
                            "g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v",
                            "w","x","y","z","0","1","2","3","4","5","6","7","8","9","+","/",
                            "!","@","#","$","%","&"};
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        readPassword();
        mAuth = FirebaseAuth.getInstance();
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
                Intent intent = new Intent(NewAccountActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                mAuth.signOut();

                finish();
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

        //TODO CHECK OF HIJ ALLES WEL ACCEPTEERT

        if (account.length() == 0 || newUsername.length() == 0 || newPassword.length() == 0) {
            Log.d("error", "Something is empty");
            messageUser("Not everything is filled in!");
        } else if (newPassword.length() < 7) {
            messageUser("Password is too short!");
        } else {

            Account newAccount = new Account(account, newUsername, newPassword);

            newAccount.setAccount(account);
            newAccount.setUsername(AES.encrypt(newUsername,password));
            newAccount.setPassword(AES.encrypt(newPassword, password));

            FirebaseHelper helper = new FirebaseHelper(this);
            helper.addAccount(newAccount);

            Intent intent = new Intent(NewAccountActivity.this, AccountsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

            finish();
        }
    }

    public void randomPasswordClicked(View view) {
        //TODO WAAROM WERKT ENCRIPTIE NIET MET RANDOM WACHTWOORD
        EditText passwordText = findViewById(ids[2]);
        String randomPassword = "";

        for (int i = 0, n = 10; i < n; i++) {
            int idx = new Random().nextInt(letters.length);
            String random = letters[idx];
            randomPassword += random;
        }

        String aPassword = randomPassword;
        passwordText.setText(aPassword);
    }

    public void visibilityClicked(View view) {

        EditText passwordText = findViewById(ids[2]);

        CheckBox checkBox = (CheckBox) view;
        Boolean checked = checkBox.isChecked();

        if (checked) {
            passwordText.setTransformationMethod(SingleLineTransformationMethod.getInstance());
        } else {
            passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
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
            Intent intent = new Intent(NewAccountActivity.this, LoginActivity.class);
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
