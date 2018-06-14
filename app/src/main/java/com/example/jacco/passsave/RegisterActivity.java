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

import java.io.FileOutputStream;

public class RegisterActivity extends AppCompatActivity {

    // Initialise variables
    public int[] ids = {R.id.username, R.id.password1, R.id.password2};
    private FirebaseAuth mAuth;
    private String username;
    private String password1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
    }

    public void registerClicked(View view) {
        EditText usernameText = findViewById(ids[0]);
        EditText password1Text = findViewById(ids[1]);
        EditText password2Text = findViewById(ids[2]);

        // Find inputted username and password
        username = usernameText.getText().toString();
        password1 = password1Text.getText().toString();
        String password2 = password2Text.getText().toString();

        // Check if input is correct
        if (username.length() == 0) {
            Log.d("error","Username is empty");
        }
        else if (password1.length() < 7) {
            Log.d("error", "Password too short");
        }
        else if (!password1.equals(password2)) {
            Log.d("error", "password1 != password2");
        }
        else {
            createUser();
        }
    }

    // Upload a new user to Firebase Authentication
    public void createUser() {
        mAuth.createUserWithEmailAndPassword(username, password1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Created", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            sendEmail();

                            Intent intent = new Intent(RegisterActivity.this, QuestionActivity.class);
                            intent.putExtra("boolean", false);
                            startActivity(intent);

                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Not created", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Send verification Email
    public void sendEmail() {

        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("E-Mail", "Email sent.");
                        }
                    }
                });

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        String text = "A verification e-mail has been sent";
        Toast.makeText(context, text, duration).show();
    }
}
