package com.example.jacco.passsave;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

/*
 Helper for the Firebase realtime database.
 */

public class FirebaseHelper extends AES{

    public interface CallBack {
        void gotQuestions(ArrayList<Question> questions);
        void gotAccounts(ArrayList<Account> accounts);
        void gotKey(String key);
        void gotError(String message);
    }

    private CallBack delegate;
    private DatabaseReference database;
    public FirebaseUser user;
    public String userId;
    public ArrayList<Account> accounts;
    public ArrayList<Question> questions;

    public FirebaseHelper() {

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        // Set reference to userId
        database = firebase.getReference(userId);

    }

    // Post question to Firebase
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

    // Post account to Firebase
    public void addAccount(Account account) {
        DatabaseReference ref = database.child("Accounts").push();
        ref.setValue(account);
    }

    // Load in accounts from Firebase and return them
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

    // Load in key from Firebase and return it
    public void getKey(CallBack activity) {

        delegate = activity;

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = "";

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

    // Upload key to Firebase
    public void addKey(String key, String password) {
        DatabaseReference ref = database.child("Key").push();
        ref.setValue(AES.encrypt(key,password));
    }

    public void changePassword(String newPassword) {
        database.removeValue();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Edit", "User password updated.");
                        }
                    }
                });
    }
}
