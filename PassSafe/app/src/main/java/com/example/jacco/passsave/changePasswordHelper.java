package com.example.jacco.passsave;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Jacco on 26-6-2018.
 */

public class changePasswordHelper {

    public changePasswordHelper () {
    }

    public interface CallBack {
        void changedPassword(String message);
        void gotQuestions(ArrayList<Question> questions);
        void gotKey(String key);
        void gotError(String message);
    }

    private Context context;
    private FirebaseUser user;
    private String userId;
    private CallBack delegate;
    private DatabaseReference database;

    public ArrayList<Account> accounts;
    public ArrayList<Account> newAccounts;
    public ArrayList<Question> questions;
    public ArrayList<Question> newQuestions;
    public String key;
    public String oldPassword;
    public String newPassword;

    public changePasswordHelper(Context context) {
        this.context = context;

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        database = firebase.getReference(userId);
    }

    public void changePassword(CallBack activity, String oldPassword, String newPassword) {

        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
        delegate = activity;
        getQuestionsToDecrypt();

    }

    // Load in questions
    public void getQuestionsToDecrypt() {

        database.addValueEventListener(new ValueEventListener() {
            // Load in questions and return them to callback activity
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questions = new ArrayList<>();

                for (DataSnapshot child : dataSnapshot.child("Questions").getChildren()) {
                    questions.add(child.getValue(Question.class));
                }

                getAccounts();
            }
            // Return error
            @Override
            public void onCancelled(DatabaseError databaseError) {
                delegate.gotError(databaseError.getMessage());
            }
        });
    }

    // Load in questions
    public void getQuestions(changePasswordHelper.CallBack activity) {

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

    // Load in accounts from firebase
    public void getAccounts() {

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                accounts = new ArrayList<>();

                for (DataSnapshot child : dataSnapshot.child("Accounts").getChildren()) {
                    accounts.add(child.getValue(Account.class));
                }

                System.out.println(accounts);

                getKeyToDecrypt();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                delegate.gotError(databaseError.getMessage());
            }
        });
    }

    // Load in key from firebase
    public void getKeyToDecrypt() {

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                key = "";

                for (DataSnapshot child : dataSnapshot.child("Key").getChildren()) {
                    key = child.getValue(String.class);
                }

                decryptData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                delegate.gotError(databaseError.getMessage());
            }
        });
    }

    public void decryptData() {

        newAccounts = new ArrayList<>();
        for (Account account : accounts) {

            String accountName = account.getAccount();
            String username = account.getUsername();
            String accountPassword = account.getPassword();

            username = AES.decrypt(username, oldPassword);
            accountPassword = AES.decrypt(accountPassword, oldPassword);

            if (username.equals("") && accountPassword.equals("")) {

                username = AES.encrypt(username, newPassword);
                accountPassword = AES.encrypt(accountPassword, newPassword);

                Account newAccount = new Account(accountName, username, accountPassword);
                newAccounts.add(newAccount);

            } else {
                delegate.gotError("Decrypting account went wrong!");
                return;
            }

        }

        newQuestions = new ArrayList<>();
        for (Question question : questions) {

            String askedQuestion = question.getQuestion();
            String answer = question.getAnswer();

            answer = AES.decrypt(answer, oldPassword);

            if (answer.equals("")) {

                answer = AES.encrypt(answer, newPassword);

                Question newQuestion = new Question(askedQuestion,answer);
                newQuestions.add(newQuestion);

            } else {
                delegate.gotError("Decrypting answer went wrong!");
                return;
            }
        }

        key = AES.decrypt(key, oldPassword);

        if (key.equals("")) {

            key = AES.encrypt(key, newPassword);

        } else {
            delegate.gotError("Decrypting key went wrong!");
            return;
        }

        database.removeValue();

        addAccounts();
    }

    public void addAccounts() {

        for (Account account : newAccounts) {
            DatabaseReference ref = database.child("Accounts").push();
            ref.setValue(account);
        }

        addQuestions();

    }

    public void addQuestions() {

        for (Question question : newQuestions) {
            DatabaseReference ref = database.child("Questions").push();
            ref.setValue(question);
        }

        addKey();

    }

    public void addKey() {
        DatabaseReference ref = database.child("Key").push();
        ref.setValue(key);

        delegate.changedPassword("Password is changed!");
    }
}
