package com.example.jacco.passsave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuestionActivity extends AppCompatActivity {

    String[] questions = {"What is your fathers first name", "What is your mothers first name",
                          "What was your first pets name", "Which city/town are you born in",
                          "Coming soon..."};
    Boolean filledIn;
    String username;
    String answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Intent intent = getIntent();
        filledIn = (Boolean) intent.getSerializableExtra("boolean");
        username = (String) intent.getSerializableExtra("username");

        //TODO MAKE IT SELECT A FEW RANDOM QUESTIONS
        String question = questions[1];

        TextView questionText = findViewById(R.id.question);
        questionText.setText(question);

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
                Intent intent = new Intent(QuestionActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.AccountSettings:
                Intent intent2 = new Intent(QuestionActivity.this, SettingsActivity.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void submitClicked(View view) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        EditText answerText = findViewById(R.id.answer);
        answer = answerText.getText().toString();

        //TODO WAAROM PAKT HIJ CHILD NIET?
//        if (filledIn) {
//
//            database.child("users").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    // ...
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {}
//            });
//        }

        Intent intent = new Intent(QuestionActivity.this, PasswordActivity.class);
        startActivity(intent);
    }
}
