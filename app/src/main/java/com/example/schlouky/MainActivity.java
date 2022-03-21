package com.example.schlouky;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseManager db = DatabaseManager.getInstance(this);
        db.setupDatabase();

        db.addPlayer("Stéphane", true);
        db.addPlayer("Robin", false);
        db.addPlayer("Célestin", true);
        db.addPlayer("Eva", true);
        db.addPlayer("Mathieu", true);

        db.addQuestion("%s doit mettre une grosse droite à %s", 2, 0);

        String test = db.getQuestion();
        Toast.makeText(this, test, Toast.LENGTH_LONG).show();
    }
}