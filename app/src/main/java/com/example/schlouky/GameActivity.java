package com.example.schlouky;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    final int questionNb = 20;
    private List<Question> questions = new ArrayList<>();

    int currentQuestionIndex = 0;

    float threshold = 300;
    float x1, x2, y1, y2, dx, dy;

    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<String> dede;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        // Récupération de la liste des joueurs depuis SetupActivity
        players = getIntent().getParcelableArrayListExtra("Players");

        LoadQuestions();

        Bundle bundle = new Bundle();
        bundle.putParcelable("question", questions.get(currentQuestionIndex));

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.question_location, QuestionFragment.class, bundle)
                .commit();

        findViewById(R.id.question_location).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case (MotionEvent.ACTION_DOWN): {
                        x1 = event.getX();
                        y1 = event.getY();
                        return true;
                    }
                    case (MotionEvent.ACTION_UP): {
                        x2 = event.getX();
                        y2 = event.getY();
                        dx = x2 - x1;
                        dy = y2 - y1;

                        // Use dx and dy to determine the direction of the move
                        if (Math.abs(dx) > Math.abs(dy)) {
                            if (Math.abs(dx) > threshold) {
                                if (dx < 0) {
                                    //Next question
                                    currentQuestionIndex++;
                                    if (currentQuestionIndex == questionNb) {
                                        getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                                                .setReorderingAllowed(true)
                                                .replace(R.id.question_location, EndGameFragment.class, null)
                                                .commit();
                                    } else if (currentQuestionIndex < questionNb) {
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable("question", questions.get(currentQuestionIndex));

                                        getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                                                .setReorderingAllowed(true)
                                                .replace(R.id.question_location, QuestionFragment.class, bundle)
                                                .commit();
                                    } else {
                                        currentQuestionIndex = questionNb;
                                    }
                                } else {
                                    //Previous question
                                    currentQuestionIndex--;
                                    if (currentQuestionIndex >= 0) {
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable("question", questions.get(currentQuestionIndex));

                                        getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in_left, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_left)
                                                .setReorderingAllowed(true)
                                                .replace(R.id.question_location, QuestionFragment.class, bundle)
                                                .commit();
                                    } else {
                                        currentQuestionIndex = 0;
                                    }
                                }
                            }
                        }
                        return true;
                    }
                    default:
                        return false;
                }
            }
        });
    }

    public void StartSetupActivity(View view) {
        Intent intent = new Intent(this, SetupActivity.class);
        intent.putParcelableArrayListExtra("Players", players);
        startActivity(intent);
        finish();
    }

    private void LoadQuestions() {
        DatabaseManager db = DatabaseManager.getInstance(this);
        db.players = players;
        db.setupDatabase();

        //Questions à 3 joueurs
        db.addQuestion("Moment confession\n\n" +
                "{joueur3} dit qui est le plus gentil entre {joueur1} et {joueur2}.\n" +
                "L'élu de son coeur prendra {glou} schlouks.", 3, 2);

        //Questions à 2 joueurs
        db.addQuestion("Concours de pompes entre {joueur1} et {joueur2}.\n" +
                "Le perdant prend 3 schlouks.", 2, 2);

        //Questions à 1 joueur
        db.addQuestion("{joueur1} fait un compliment sincère à tous les autres joueurs.", 1, 0);

        //Questions à 0 joueurs
        db.addQuestion("Jeu à thème !\n\n" +
                "Les alcools français.\n" +
                "Le premier joueur qui n'a plus d'idées, ou qui est trop lent prend {glou} schlouks.", 0, 0);
        db.addQuestion("Jeu à thème !\n\n" +
                "Les films français.\n" +
                "Le premier joueur qui n'a plus d'idées, ou qui est trop lent prend {glou} schlouks.", 0, 0);
        db.addQuestion("Jeu à thème !\n\n" +
                "Les humoristes français.\n" +
                "Le premier joueur qui n'a plus d'idées, ou qui est trop lent prend {glou} schlouks.", 0, 0);
        db.addQuestion("Jeu à thème !\n\n" +
                "Les chanteurs français.\n" +
                "Le premier joueur qui n'a plus d'idées, ou qui est trop lent prend {glou} schlouks.", 0, 0);
        db.addQuestion("Jeu à thème !\n\n" +
                "Les chanteurs des années 2000.\n" +
                "Le premier joueur qui n'a plus d'idées, ou qui est trop lent prend {glou} schlouks.", 0, 0);
        db.addQuestion("Jeu à thème !\n\n" +
                "Les séries dont la fin est naze.\n" +
                "Le premier joueur qui n'a plus d'idées, ou qui est trop lent prend {glou} schlouks.", 0, 0);
        db.addQuestion("Jeu à thème !\n\n" +
                "Les séries surcotées.\n" +
                "Le premier joueur qui n'a plus d'idées, ou qui est trop lent prend {glou} schlouks.", 0, 0);

        for (int i = 0; i < players.size(); i++) {
            players.get(i).chance = (int) (Math.random() * 10);
        }

        for (int i = 0; i < questionNb; i++) {
            questions.add(db.getRandomQuestion());
        }
    }

    private int Random(int inclMin, int inclMax) {
        return inclMin + (int) (Math.random() * ((inclMax - inclMin) + 1));
    }

    @Override
    public void onBackPressed() {
        StartSetupActivity(null);
    }
}