package com.example.jacco.passsave;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/*
TODO waarom werkt de firebasehelper niet voor wachtwoord veranderen? Nieuwe helper maken?
TODO Om email met key te vergelijken ook email in firebase database zetten?
TODO GAAT HIJ NA EEN DELEGATE NOG DOOR?
 */

/*
* TODO AUTHSTATE IN ONRESUME!!!!!
* TODO MAKE UP MORE QUESTIONS
* TODO SAVE  EMAIL AFTER LOGIN IN PREFERENCES
* TODO https://codinginflow.com/tutorials/android/slide-animation-between-activities
*/

public class LoginActivity extends AppCompatActivity {

    // initialise variables
    public int[] ids = {R.id.username, R.id.password};
    private FirebaseAuth mAuth;
    private String username;
    private String password;
    private String key;
    private String givenKey;
    private String givenUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
    }

    // add menu
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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
                                //TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                if (user.isEmailVerified()) {
                                    messageUser("E-Mail not verified!");
                                } else {
                                    // Store password in background
                                    storePassword();

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

    public void forgotPasswordClicked(View view) {

        //TODO CHECK IF USENAME AND KEY COMBINE IN STEAD OF ASKING FOR KEY FROM A USER
        //TODO https://stackoverflow.com/questions/41666044/how-to-get-userid-by-user-email-firebase-android

//        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            String userId = user.getUid();
//
//            DatabaseReference database = firebase.getReference(userId);
//
//            database.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    for (DataSnapshot userKey: dataSnapshot.child("Key").getChildren()) {
//                        key = userKey.getValue(String.class);
//                    }
//
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.key);
        final EditText emailInput = (EditText) promptsView
                .findViewById(R.id.email);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                givenKey = userInput.getText().toString();
                                givenUsername = emailInput.getText().toString();

                                checkKey();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    public void checkKey() {

        //TODO LOAD IN UID FROM E-MAIL, LOAD IN KEY AND COMPARE


        if(givenKey.equals(key)) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = "user@example.com";

            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("message", "Email sent.");
                                messageUser("Reset e-mail has been sent!");
                            }
                        }
                    });
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

    public void messageUser(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
