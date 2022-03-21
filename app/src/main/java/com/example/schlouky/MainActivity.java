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

        //TODO: changer le format des questions pour faciliter le remplacer des noms et nombres de gorgées
        //par exemple utiliser {player1} et {schloukCount}

        db.addQuestion("%s doit mettre une grosse droite à %s.", 2, 0);
        db.addQuestion("%s choisi la plus magnifique entre %s et %s. Le gagnant boit 2 gorgées.", 3, 2);
        db.addQuestion("%s ne doit pas oublier le petit bonhomme.", 1, 1);
        db.addQuestion("Duel de regards entre %s et %s. Le perdant boit 4 gorgées.", 2, 2);
        db.addQuestion("Duel de regards entre %s et %s.", 2, 0);

        Toast.makeText(this, db.getQuestion(3, 2), Toast.LENGTH_LONG).show();
        Toast.makeText(this, db.getQuestion(1, 3), Toast.LENGTH_LONG).show();
        Toast.makeText(this, db.getQuestion(2, 0), Toast.LENGTH_LONG).show();
        Toast.makeText(this, db.getQuestion(2, 2), Toast.LENGTH_LONG).show();
    }
}