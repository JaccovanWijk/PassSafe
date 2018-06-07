package com.example.jacco.passsave;

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
        * TODO WAAROM PAKT HIJ CHILD NIET (FIREBASE)?
        * TODO WAAROM WIL HIJ NIET TERUG NAAR PASSWORDMODE(NEWACCOUNTACTIVITY)?
        */
        mAuth = FirebaseAuth.getInstance();

//        AES.setKey("123");
//        String encriptie = AES.encrypt("hoi");
//        String decriptie = AES.decrypt(encriptie);
//        Log.d("encyprion", encriptie);
//        Log.d("decyption", decriptie);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
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

        if (username.length() == 0 || password.length() == 0) {
            Log.d("error", "Something is empty");
        }
        else if (!checkInput(username, password)) {
            Log.d("error", "Not found in firebase");
        }
        else {
//

            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Sign in", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(LoginActivity.this, AccountsActivity.class);
                                startActivity(intent);
//                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Sign out", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
//                                updateUI(null);
                            }

                            // ...
                        }
                    });
        }
    }

    public boolean checkInput(String username, String password) {
        //TODO Check of het goed is in firebase
        return true;
    }
}
