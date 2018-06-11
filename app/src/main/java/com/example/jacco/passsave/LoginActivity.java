package com.example.jacco.passsave;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {
    // TODO PROGRESSBAR FOR USER INTERFACE
    public int[] ids = {R.id.username, R.id.password};
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*
        * TODO WAAROM CRASHT HIJ NU? VGM NIKS AANGEPAST?
        * TODO WAAROM WIL HIJ NIET TERUG NAAR PASSWORDMODE(NEWACCOUNTACTIVITY)?
        */
        mAuth = FirebaseAuth.getInstance();

        String encriptie = AES.encrypt("hoi", "hoi");
        String decriptie = AES.decrypt(encriptie, "hoi");


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void registerClicked(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginClicked(View view) {
        EditText usernameText = findViewById(ids[0]);
        EditText passwordText = findViewById(ids[1]);

        username = usernameText.getText().toString();
        password = passwordText.getText().toString();

        Log.d("error","why");

        if(username.length() == 0 || password.length() == 0) {
            Log.d("error", "something is empty");
        }
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Sign in", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            storePassword();

                            Intent intent = new Intent(LoginActivity.this, AccountsActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Sign out", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        storePassword();

        Intent intent = new Intent(LoginActivity.this, AccountsActivity.class);
        startActivity(intent);
    }

    public void storePassword() {
        String filename = "StorePass";
        String fileContents = username + "\n" + AES.encrypt(password, "randomKey"); //TODO HASH PASSWORD better
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
