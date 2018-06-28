package com.example.jacco.passsave;
/*
This activity makes a new user. It registers the user for the Firebase authentication and it
creates a activationkey for the user.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.ArrayList;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    // Initialise variables
    public int[] ids = {R.id.username, R.id.password1, R.id.password2};
    private FirebaseAuth mAuth;
    private String username;
    private String password1;
    private String key;
    private Context context = this;
    String[] letters = {
            "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P",
            "Q","R","S","T","U","V","W","X","Y","Z","a","b","c","d","e","f",
            "g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v",
            "w","x","y","z","0","1","2","3","4","5","6","7","8","9","+","/",
            "!","@","#","$","%","&"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
    }

    /*
    Listen if the button is clicked, then load the input and check if it's correct. If it is correct
    call createUser().
     */
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
            messageUser("No e-mail is given!");
        }
        else if (password1.length() < 7) {
            Log.d("error", "Password too short");
            messageUser("Password is too short!");
        }
        else if (!password1.equals(password2)) {
            Log.d("error", "password1 != password2");
            messageUser("Passwords do not match!");
        }
        else {

            createUser();
        }
    }

    /*
    Upload the new user to firebase. Also add the key to database, and go back to the login screen.
     */
    public void createUser() {
        mAuth.createUserWithEmailAndPassword(username, password1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Created", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            key = createKey();

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Your activation key is: " + key + "\nThis key " +
                                    "is needed for changing" +
                                    " your password. \nNote this key and make sure not to lose it!")
                                    .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            sendEmail();

                                            byte[] bytesOldPassword = Base64.encode(password1.getBytes(),
                                                    Base64.DEFAULT);
                                            String encodedPassword = new String(bytesOldPassword);
                                            encodedPassword = encodedPassword.replace("\n",
                                                    "").replace("\r", "");

                                            FirebaseHelper helper = new FirebaseHelper(context);
                                            helper.addKey(key, encodedPassword);

                                            Intent intent = new Intent(RegisterActivity
                                                    .this, QuestionActivity.class);
                                            intent.putExtra("boolean", false);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in_left,
                                                    R.anim.slide_out_right);

                                            finish();
                                        }
                                    });
                            builder.show();

                            SharedPreferences settings = getSharedPreferences("usernames", MODE_PRIVATE);

                            // Writing data to SharedPreferences
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("username", username);
                            editor.commit();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Not created", "createUserWithEmail:failure",
                                    task.getException());
                            messageUser("E-Mail in use!");
                        }
                    }
                });
    }

    /*
    Create a random string of size 24.
     */
    public String createKey() {
        String key = "";
        for (int i = 0, n = 24; i < n; i++) {
            int idx = new Random().nextInt(letters.length);
            String random = letters[idx];
            key += random;
        }
        return key;
    }

    /*
    Send verification mail.
     */
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

        messageUser("A verification e-mail has been sent!");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    /*
    Make a Toast of the inputted message.
     */
    public void messageUser(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
