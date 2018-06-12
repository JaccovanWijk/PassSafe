package com.example.jacco.passsave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.FileInputStream;

public class PasswordActivity extends AppCompatActivity {

    public Account account;
    public String password;
    public TextView usernameText;
    public TextView passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");

        readPassword();

        usernameText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);

        System.out.println(account.getPassword());
        System.out.println(AES.decrypt(account.getPassword(), password) + "?????????????????????????????????");

        usernameText.setText(account.getUsername());
        passwordText.setText(AES.decrypt(account.getPassword(), password));
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
                usernameText.setText("This will be a username if you follow the legit way;).");
                passwordText.setText("**********");
                Intent intent = new Intent(PasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.AccountSettings:
                usernameText.setText("This will be a username if you follow the legit way;).");
                passwordText.setText("**********");
                Intent intent2 = new Intent(PasswordActivity.this, SettingsActivity.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void returnClicked(View view) {

        usernameText.setText("This will be a username if you follow the legit way;).");
        passwordText.setText("**********");

        Intent intent = new Intent(PasswordActivity.this, AccountsActivity.class);
        startActivity(intent);
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

            password = info[1];

            //string temp contains all the data of the file.
            fin.close();
        } catch(Exception e) {
            Log.e("error","Couldn't find file");
        }
    }
}
