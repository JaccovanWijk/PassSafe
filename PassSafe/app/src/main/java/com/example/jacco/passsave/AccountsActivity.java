package com.example.jacco.passsave;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.util.ArrayList;

public class AccountsActivity extends AppCompatActivity implements FirebaseHelper.CallBack {

    // Initialise variables
    public String password;
    public ArrayList<Account> accounts;
    public DatabaseReference myRef;
    public AccountsAdapter adapter;
    public ListView listView;
    public Account selectedAccount;
    public String deleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        readPassword();

        // Get user id
        String userId = "";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        // Set right reference
        myRef = FirebaseDatabase.getInstance().getReference(userId);

        accounts = new ArrayList<>();

        // Set listeners on listview
        listView = findViewById(R.id.listview);
        listView.setOnItemClickListener(new ListClickListener());
        listView.setOnItemLongClickListener(new ListLongClickListener());

        // Call FirebaseHelper to load in accounts
        FirebaseHelper helper = new FirebaseHelper(this);
        helper.getAccounts(this);
    }

    // Add menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.LogOut:
                Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            case R.id.AccountSettings:
                Intent intent2 = new Intent(AccountsActivity.this, SettingsActivity.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Listen for single clicks on listview an direct to next activity
    private class ListClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView accountText = view.findViewById(R.id.account);
            String account = accountText.getText().toString();

            // Find clicked account
            for (Account anAccount : accounts) {
                if(anAccount.getAccount().equals(account)) {
                    selectedAccount = anAccount;
                }
            }

            Intent intent = new Intent(AccountsActivity.this, QuestionActivity.class);
            intent.putExtra("account", selectedAccount);
            intent.putExtra("boolean", true);
            startActivity(intent);
        }
    }

    // Listen for long clicks
    private class ListLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            TextView accountText = view.findViewById(R.id.account);
            deleteAccount = accountText.getText().toString();

            areYouSure();

            return true;
        }
    }

    // Listen if addbutton is clicked and direct to next activity
    public void addButtonClicked(View view) {
        Intent intent = new Intent(AccountsActivity.this, NewAccountActivity.class);
        startActivity(intent);
    }

    // Update listview with updated info
    public void updateData() {
        adapter = new AccountsAdapter(this, accounts);
        listView.setAdapter(adapter);
    }

    // FirebaseHelper CallBack functions
    @Override
    public void gotQuestions(ArrayList<Question> questions) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        String text = "Something went wrong...";
        Toast.makeText(context, text, duration).show();

        // log error
        Log.e("ERROR", "You're not supposed to be here!");
    }
    @Override
    public void gotError(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        String text = "Something went wrong...";
        Toast.makeText(context, text, duration).show();

        // log error
        Log.e("ERROR", message);
    }
    // Receive accounts and update listview
    @Override
    public void gotAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;

        updateData();
    }
    @Override
    public void gotKey(String key) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        String text = "Something went wrong...";
        Toast.makeText(context, text, duration).show();

        // log error
        Log.e("ERROR", "You're not supposed to be here!");
    }

    // Create popupscreen to confirm deletion account
    public void areYouSure() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete "+ deleteAccount + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Delete item
                        deleteItem();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Don't delete
                    }
                });
        builder.show();
    }

    // Delete a account from Firebase
    public void deleteItem() {
        Query query = myRef.child("Accounts").orderByChild("account").equalTo(deleteAccount);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "onCancelled", databaseError.toException());
            }
        });
    }

    // Read password from saved file
    public void readPassword() {

        try {
            FileInputStream fin = openFileInput("StorePass");

            int c;
            String temp = "";
            while ((c = fin.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }

            String[] info = temp.split("\\s+");

            password = info[0];

            //string temp contains all the data of the file.
            fin.close();
        } catch(Exception e) {
            Log.e("error","Couldn't find file");
        }
    }

    // When returning to this activity without logging in go to loginactivity
    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // user isn't signed in
            Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
            startActivity(intent);

            finish();
        }
    }
}
