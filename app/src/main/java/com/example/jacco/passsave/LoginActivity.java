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
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*
        * TODO IS DIT VEILIG GENOEG? NEEEEEE, versleutel ook vraag, antwoord en usernames
        * TODO AUTHSTATE
        * TODO ZORG DAT HIJ NA DE VRAAG BIJ QUESTIONACTIVITY PAS DE USER OPSLAAT ZODAT JE GEEN USER AANMAAKT ZONDER VRAAG!
        */
        mAuth = FirebaseAuth.getInstance();
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

        //TODO CHECK IF USERS EMAIL IS VERIFICATED

        if (username.length() == 0 || password.length() == 0) {
            Log.d("error", "something is empty");
        } else {
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Sign in", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (!user.isEmailVerified()) {
                                    Context context = getApplicationContext();
                                    int duration = Toast.LENGTH_LONG;
                                    String text = "E-Mail not verified!";
                                    Toast.makeText(context, text, duration).show();
                                } else {
                                    storePassword();

                                    Intent intent = new Intent(LoginActivity.this, AccountsActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Sign out", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void storePassword() {
        String filename = "StorePass";

        String fileContents = AES.encrypt(password, "randomKey"); //TODO HASH PASSWORD better
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
        EditText usernameText = findViewById(ids[0]);
        EditText passwordText = findViewById(ids[1]);

        usernameText.setText("");
        passwordText.setText("");
    }
}
