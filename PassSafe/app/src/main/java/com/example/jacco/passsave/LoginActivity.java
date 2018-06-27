package com.example.jacco.passsave;
/*
This activity is the log in screen. you can navigate to the registerscreen, ask for a new password,
or just log in. It saves the hashed password so it can be used to encrypt and decrypt data.
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileOutputStream;

/*
* TODO MAKE UP MORE QUESTIONS
* TODO https://codinginflow.com/tutorials/android/slide-animation-between-activities
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

        // Set FirebaseAuth reference
        mAuth = FirebaseAuth.getInstance();

        // Reading from SharedPreferences
        SharedPreferences settings = getSharedPreferences("usernames", MODE_PRIVATE);
        String value = settings.getString("username", "");
        EditText username = findViewById(R.id.username);
        username.setText(value);

    }

    // Create Register button at top
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.register_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Listen to loginbutton and check if input is correct. It checks with the firebase if the login
    is legal and goes to the accountsactivity if it is. Otherwise it gives an error.
    */
    public void loginClicked(View view) {
        EditText usernameText = findViewById(ids[0]);
        EditText passwordText = findViewById(ids[1]);

        username = usernameText.getText().toString();
        password = passwordText.getText().toString();

        // Check if nothing is left empty
        if (username.length() == 0 || password.length() == 0) {
            Log.d("error", "something is empty");
            messageUser("Not everything is filled in!");
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
                                    messageUser("E-Mail not verified!");
                                } else {
                                    // Store password in background
                                    storePassword();

                                    SharedPreferences settings = getSharedPreferences("usernames", MODE_PRIVATE);

                                    // Writing data to SharedPreferences
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("username", username);
                                    editor.commit();

                                    Intent intent = new Intent(LoginActivity.this, AccountsActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Sign out", "signInWithEmail:failure", task.getException());
                                messageUser("Wrong username/password!");
                            }
                        }
                    });
        }
    }

    /*
    Store encrypted password in a file.
     */
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

        SharedPreferences settings = getSharedPreferences("usernames", MODE_PRIVATE);

        // Reading from SharedPreferences
        String value = settings.getString("username", "");
        Log.d("username", value);

        EditText username = findViewById(R.id.username);
        username.setText(value);

        // Empty history of EditText
        EditText passwordText = findViewById(ids[1]);

        passwordText.setText("");
    }

    public void messageUser(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
