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
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Jacco on 6-6-2018.
 */

public class FirebaseHelper extends AES{

    public FirebaseHelper() {
    }

    public interface CallBack {
        void gotQuestions(ArrayList<Question> questions);
        void gotQuestionsError(String message);
        void gotAccounts(ArrayList<Account> accounts);
        void gotAccountsError(String message);
    }

    private Context context;
    private CallBack delegate;
    private DatabaseReference database;

    public FirebaseHelper(Context context, String username) {
        this.context = context;

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
//        firebase.setLogLevel(Logger.Level.DEBUG);
        database = firebase.getReference(username);

    }

    public void addQuestion(Question question) {

        DatabaseReference ref = database.child("Questions").push();
        ref.setValue(question);

    }

    public void getQuestions(CallBack activity) {

        delegate = activity;

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Question> questions = new ArrayList<>();

                for (DataSnapshot child : dataSnapshot.child("Questions").getChildren()) {
                    questions.add(child.getValue(Question.class));
                }

                delegate.gotQuestions(questions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                delegate.gotQuestionsError(databaseError.getMessage());
            }
        });
    }

    public void addAccount(Account account) {
        DatabaseReference ref = database.child("Accounts").push();
        ref.setValue(account);
    }

    public void getAccounts(CallBack activity) {

        delegate = activity;

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Account> accounts = new ArrayList<>();

                for (DataSnapshot child : dataSnapshot.child("Accounts").getChildren()) {
                    accounts.add(child.getValue(Account.class));
                }

                delegate.gotAccounts(accounts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                delegate.gotAccountsError(databaseError.getMessage());
            }
        });
    }

}
