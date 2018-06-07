package com.example.jacco.passsave;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Jacco on 6-6-2018.
 */

public class FirebaseHelper extends AES{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public interface CallBack {
        void gotCategories(ArrayList<String> accounts);
        void gotCategoriesError(String message);
    }

    private Context context;
    private CallBack delegate;
    private DatabaseReference database;

    public FirebaseHelper(Context context) {
        this.context = context;
        // TODO accounts moet maybe een laag dieper de mapjes in?
//        database = FirebaseDatabase.getInstance().getReference();

        // set key for encrypting
//        setKey();

        mAuth = FirebaseAuth.getInstance();

        // upload main password
//        DatabaseReference ref = database.push();
//        ref.setValue(encrypt(password));
    }

    public void addAccount(String account, String password) {
        // TODO Add account under username
    }

    // TODO Add all the other functions


}
