package com.example.jacco.passsave;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import java.io.FileOutputStream;

/*
* TODO AUTHSTATE
* TODO QUESTIONS ACCEPT EMPTY ANSWER
* TODO TITLE ABOVE ACCOUNTS
* TODO TRANSFER LOG INTO TOASTS
*/

public class LoginActivity extends AppCompatActivity {

    // initialise variables
    public int[] ids = {R.id.username, R.id.password};
    private FirebaseAuth mAuth;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    // Listen to registerbutton and direct to register activity if it's pressed
    public void registerClicked(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    // Listen to loginbutton and check if input is correct
    public void loginClicked(View view) {
        EditText usernameText = findViewById(ids[0]);
        EditText passwordText = findViewById(ids[1]);

        username = usernameText.getText().toString();
        password = passwordText.getText().toString();

        // Check if nothing is left empty
        if (username.length() == 0 || password.length() == 0) {
            Log.d("error", "something is empty");
        } else {
            // Check if input is correct
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Sign in", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                // Check if user verified his mail
                                if (!user.isEmailVerified()) {
                                    Context context = getApplicationContext();
                                    int duration = Toast.LENGTH_LONG;
                                    String text = "E-Mail not verified!";
                                    Toast.makeText(context, text, duration).show();
                                } else {
                                    // Store password in background
                                    storePassword();

                                    Intent intent = new Intent(LoginActivity.this, AccountsActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Sign out", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Wrong username/password.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Store encrypted password in a file
    public void storePassword() {
        String filename = "StorePass";

        byte[] bytesEncoded = Base64.encode(password.getBytes(), Base64.DEFAULT);
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

        // Empty history of EditText
        EditText usernameText = findViewById(ids[0]);
        EditText passwordText = findViewById(ids[1]);

        usernameText.setText("");
        passwordText.setText("");
    }
}
