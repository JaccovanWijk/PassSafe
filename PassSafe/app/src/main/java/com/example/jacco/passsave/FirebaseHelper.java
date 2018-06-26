package com.example.jacco.passsave;

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

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jacco on 6-6-2018.
 */

public class FirebaseHelper extends AES{

    public FirebaseHelper() {
    }

    public interface CallBack {
        void gotQuestions(ArrayList<Question> questions);
        void gotAccounts(ArrayList<Account> accounts);
        void gotKey(String key);
        void gotError(String message);
    }

    private Context context;
    private CallBack delegate;
    private DatabaseReference database;
    private DatabaseReference userDatabase;
    public FirebaseUser user;
    public String userId;
    public ArrayList<Account> accounts;
    public ArrayList<Question> questions;
    public String key;
    public String oldPassword;
    public String newPassword;

    public FirebaseHelper(Context context) {
        this.context = context;

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        database = firebase.getReference(userId);
        userDatabase = firebase.getReference("Users");

    }

    // Post question to firebase
    public void addQuestion(Question question) {

        DatabaseReference ref = database.child("Questions").push();
        ref.setValue(question);

    }

    // Load in questions
    public void getQuestions(CallBack activity) {

        delegate = activity;

        database.addValueEventListener(new ValueEventListener() {
            // Load in questions and return them to callback activity
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questions = new ArrayList<>();

                for (DataSnapshot child : dataSnapshot.child("Questions").getChildren()) {
                    questions.add(child.getValue(Question.class));
                }

                delegate.gotQuestions(questions);
            }
            // Return error
            @Override
            public void onCancelled(DatabaseError databaseError) {
                delegate.gotError(databaseError.getMessage());
            }
        });
    }

    // Post account to firebase
    public void addAccount(Account account) {
        DatabaseReference ref = database.child("Accounts").push();
        ref.setValue(account);
    }

    // Load in accounts from firebase and return them
    public void getAccounts(CallBack activity) {

        delegate = activity;

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                accounts = new ArrayList<>();

                for (DataSnapshot child : dataSnapshot.child("Accounts").getChildren()) {
                    accounts.add(child.getValue(Account.class));
                }

                delegate.gotAccounts(accounts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                delegate.gotError(databaseError.getMessage());
            }
        });
    }

    // Load in key from firebase and return it
    public void getKey(CallBack activity) {

        delegate = activity;

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                key = "";

                for (DataSnapshot child : dataSnapshot.child("Key").getChildren()) {
                    key = child.getValue(String.class);
                }

                delegate.gotKey(key);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                delegate.gotError(databaseError.getMessage());
            }
        });
    }

    public void addKey(String key, String password, String username) {
        // Upload key
        DatabaseReference ref = database.child("Key").push();
        ref.setValue(AES.encrypt(key,password));

        //TODO KIJK OF HIJ GEHASHT WEL EEN KOPJE KAN ZIJN
        byte[] encodedUsername = Base64.encode(username.getBytes(), Base64.DEFAULT);
        username = new String(encodedUsername);
        DatabaseReference ref2 = userDatabase.child(username).push();
        ref2.setValue(user.getUid());
    }
}
