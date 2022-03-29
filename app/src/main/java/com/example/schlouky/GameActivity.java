package com.example.schlouky;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.concurrent.ThreadLocalRandom;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    final int questionNb = 20;
    int playerNb = 3;
    private ArrayList<String> questions = new ArrayList<>();

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

        // Récupération de la liste des joueurs depuis SetupActivity"
        players = getIntent().getParcelableArrayListExtra("UniqueKey");
        /*for (Player p:players)
        {
            String str = "[" + p.name + "]" + "{" + p.buveur + "}";
            Toast.makeText(GameActivity.this, str, Toast.LENGTH_SHORT).show();
        }*/

        LoadQuestions();

        Bundle bundle = new Bundle();
        bundle.putString("question", questions.get(currentQuestionIndex));

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
                                                .add(R.id.question_location, EndGameFragment.class, null)
                                                .commit();
                                    } else if (currentQuestionIndex < questionNb) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("question", questions.get(currentQuestionIndex));

                                        getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                                                .setReorderingAllowed(true)
                                                .add(R.id.question_location, QuestionFragment.class, bundle)
                                                .commit();
                                    } else {
                                        currentQuestionIndex = questionNb;
                                    }
                                } else {
                                    //Previous question
                                    currentQuestionIndex--;
                                    if (currentQuestionIndex >= 0) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("question", questions.get(currentQuestionIndex));

                                        getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in_left, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_left)
                                                .setReorderingAllowed(true)
                                                .add(R.id.question_location, QuestionFragment.class, bundle)
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
        startActivity(intent);
    }

    private void LoadQuestions() {
        DatabaseManager db = DatabaseManager.getInstance(this);
        db.setupDatabase();

        /*db.addPlayer("Stéphane", true);
        db.addPlayer("Robin", false);
        db.addPlayer("Célestin", true);
        db.addPlayer("Eva", true);
        db.addPlayer("Mathieu", true);*/

        // Ajout des joueurs
        for (Player p:players)
        {
            db.addPlayer(p.name, p.buveur);
        }

        db.addQuestion("{joueur1} doit mettre une grosse droite à {joueur2}.", 2, 0);
        db.addQuestion("{joueur1} choisi la plus magnifique entre {joueur2} et {joueur3}. Le gagnant boit {glou} gorgées. Je rapelle que c'est a {joueur1} de choisir.", 3, 2);
        db.addQuestion("{joueur1} ne doit pas oublier le petit bonhomme.", 1, 1);
        db.addQuestion("Duel de regards entre {joueur1} et {joueur2}. Le perdant boit {glou} gorgées.", 2, 2);
        db.addQuestion("Duel de regards entre {joueur1} et {joueur2}.", 2, 0);

        for (int i = 0; i < questionNb; i++) {
            String currentQuestion = db.getQuestion(Random(1, playerNb),3);
            if (currentQuestion != "Aucune question trouvée avec ces paramètres.") {
                questions.add(currentQuestion);
            }
            else {
                i--;
            }
        }
    }

    private int Random(int inclMin, int inclMax) {
        return inclMin + (int)(Math.random() * ((inclMax - inclMin) + 1));
    }
}