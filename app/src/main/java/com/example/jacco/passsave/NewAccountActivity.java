package com.example.jacco.passsave;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class NewAccountActivity extends AppCompatActivity {

    int[] ids = {R.id.account, R.id.password};
    String account;
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
        EditText passwordText = findViewById(ids[1]);

        account = accountText.getText().toString();
        password = passwordText.getText().toString();

        if (account.length() == 0 || password.length() == 0) {
            Log.d("error", "Something is empty");
        }

        //TODO ADD ACOUNT TO FIREBASE

        Intent intent = new Intent(NewAccountActivity.this, AccountsActivity.class);
        startActivity(intent);
    }


    public void randomPasswordClicked(View view) {
        EditText passwordText = findViewById(ids[1]);
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

        EditText passwordText = findViewById(ids[1]);

        CheckBox checkBox = (CheckBox) view;
        Boolean checked = checkBox.isChecked();

        if (checked) {
            passwordText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

}
