package com.example.jacco.passsave;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.util.ArrayList;

public class AccountsActivity extends AppCompatActivity  {

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        ListView listview = findViewById(R.id.listview);
        listview.setOnItemClickListener(new ListClickListener());
        listview.setOnItemLongClickListener(new ListLongClickListener());

        //TODO LOAD IN CORRECT LIST FROM FIREBASE
        ArrayList<String> list = new ArrayList<String>();
        list.add("Facebook");
        listview.setAdapter(new AccountsAdapter(this, list));
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
                //TODO DIT WERKT NOG NIET MET DE TERUGKNOP
                Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.AccountSettings:
                Intent intent2 = new Intent(AccountsActivity.this, SettingsActivity.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ListClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView accountText = view.findViewById(R.id.account);
            String account = accountText.getText().toString();

            Intent intent = new Intent(AccountsActivity.this, QuestionActivity.class);
            intent.putExtra("account", account);
            intent.putExtra("boolean", true);
            startActivity(intent);
        }
    }

    private class ListLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            return false;
        }
    }

    public void addButtonClicked(View view) {
        // go to next activity
        Intent intent = new Intent(AccountsActivity.this, NewAccountActivity.class);
        startActivity(intent);
    }

    public void readPassword() {

        try {
            FileInputStream fin = openFileInput("StorePass");

            int c;
            String temp = "";
            while ((c = fin.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }

            String[] info = temp.split("\\s+");

            username = info[0];

            //string temp contains all the data of the file.
            fin.close();
        } catch(Exception e) {
            Log.e("error","Couldn't find file");
        }
    }
}