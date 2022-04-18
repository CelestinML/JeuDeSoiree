package com.example.schlouky;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;
import java.util.Random;
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

    int[] backgrounds = new int[]{R.drawable.background1, R.drawable.background2, R.drawable.background3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        RelativeLayout relativeLayout = findViewById(R.id.activity_game);
        relativeLayout.setBackground(getDrawable(backgrounds[new Random().nextInt(backgrounds.length)]));

        // Récupération de la liste des joueurs depuis SetupActivity"
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

        ////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////// Questions à 3 joueurs ////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////

        db.addQuestion("Moment confession :\n" +
                "{joueur3} dit qui est le plus gentil entre {joueur1} et {joueur2}.\n" +
                "L'élu.e de son coeur prendra {glou} schlouks pour honorer son amour.", 3, 2);
        db.addQuestion("{joueur3} donne un défi sportif à {joueur1} et {joueur2}.\n" +
                "Le perdant prendra {glou} schlouks.", 3, 2);
        db.addQuestion("{joueur3}, si tu devais monter un business, tu le ferais avec qui ? {joueur1} ou {joueur2} ?\n" +
                "Cette personne prendra {glou} schlouks pour les futurs millions qu'iel générera !", 3, 2);
        db.addQuestion("{joueur3}, si tu devais te faire conseiller une série par {joueur1} ou {joueur2}, tu choisirais qui ?\n" +
                "Cette personne prendra {glou} schlouks au nom de ses bons goûts !", 3, 2);
        db.addQuestion("{joueur3}, si tu devais peindre {joueur1} ou {joueur2} nu.e, tu choisirais qui ?\n" +
                "Aphrodite prendra {glou} schlouks.", 3, 2);


        ////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////// Questions à 2 joueurs ////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////

        db.addQuestion("Concours de pompes entre {joueur1} et {joueur2}.\n" +
                "Le perdant prend {glou} schlouks.", 2, 2);
        //PROBABLEMENT TROP LONG
        db.addQuestion("{joueur1} et {joueur2} se placent dos à dos.\n" +
                "Les autres joueurs leur poseront tour à tour une question comme par exemple \"Qui est le plus beau ?\"\n" +
                "Les deux joueurs dos à dos devront alors lever la main ou pointer l'autre personne du pouce au bout de 3 secondes pour dire qui correspondrait le mieux à la question.\n" +
                "S'ils sont d'accord entre eux, la personne ayant posé la question boit un schlouk. Sinon, ils prennent tous les deux un schlouk.", 2, 2);
        db.addQuestion("{joueur1} et {joueur2} inventent un check. Si iels ne s'en souviennent pas à la prochaine soirée, iels prendront un gage.\n" +
                "(Les autres, on vous fait confiance pour vous en souvenir)", 2, 0);
        db.addQuestion("{joueur1} et {joueur2}, si vous avez déjà couché ensembles (ou si vous aimeriez le faire), tous les autres prennent {glou} schlouks s'iels boivent.\n" +
                "Sinon, vous les prenez.", 2, 2);
        db.addQuestion("{joueur1} et {joueur2} si vous vous embrassez, tous les autres prennent {glou} schlouks.\n" +
                "Sinon, vous lez prenez.", 2, 2);
        db.addQuestion("{joueur1}, si tu arrives à deviner la taille du soutif, ou la taille du sexe, de {joueur2}, alors tu peux distribuer 5 gorgées. Sinon, tu les prends.", 2, 1);

        ////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////// Questions à 1 joueur /////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////

        db.addQuestion("{joueur1} fait un compliment sincère à tous les autres joueurs.\n" +
                "Si iel prend trop longtemps pour une personne, celle-ci pourra lui distribuer autant de schlouks qu'iel souhaite, ou pourra lui donner un gage.", 1, 0);
        db.addQuestion("{joueur1} est le nouveau roi du silence !\n" +
                "Une fois par tour, il pourra exiger le silence complet en criant \"Silence !\".\n" +
                "Le dernier joueur à se taire prendra {glou} schlouks ou un gage si iel ne veut pas boire.", 1, 0);
        db.addQuestion("{joueur1} est le nouveau mister freeze !\n" +
                "Une fois par tour, il pourra crier \"Freeze !\".\n" +
                "Le dernier joueur à arrêter de bouger prendra {glou} schlouks ou un gage si iel ne veut pas boire.", 1, 0);
        db.addQuestion("{joueur1} est le nouveau roi des pouces !\n" +
                "Une fois par tour, il pourra poser son pouce sur son front.\n" +
                "Le dernier à l'imiter prendra {glou} schlouks ou un gage si iel ne veut pas boire.", 1, 0);
        db.addQuestion("{joueur1} invente une nouvelle règle qui fonctionnera jusqu'à la fin de la partie !", 1, 0);
        db.addQuestion("{joueur1} raconte une anecdote, et tous les autres devront deviner si elle est vraie ou fausse.\n" +
                "Iel prendra 1 schlouk pour chaque personne ayant trouvé la bonne réponse.", 1, 1);
        db.addQuestion("{joueur1} lance un \"Qui pourrait ?\"\n" +
                "Par exemple, si iel demande \"Qui pourrait vomir ce soir ?\", vous voterez tous en même temps pour la personne à laquelle vous pensez.\n" +
                "Chaque personne prendra autant de schlouks que de doigts pointés vers elle si elle boit\n" +
                "Si cette personne ne boit pas, elle fera autant de squats que de doigts pointés vers elle.", 1, 0);
        db.addQuestion("{joueur1} dit quelque-chose qu'iel n'a jamais fait.\n" +
                "Ceux ou celles qui l'ont déjà fait prendront {glou} schlouks, s'iels boivent.", 1, 0);
        db.addQuestion("{joueur1} dit quelque-chose qu'iel a déjà fait.\n" +
                "Ceux ou celles qui ne l'ont jamais fait prendront {glou} schlouks, s'iels boivent.", 1, 0);

        ////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////// Questions à 0 joueurs ////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////

        db.addQuestion("Jeu à thème !\n" +
                "Les alcools français.\n" +
                "Le premier joueur qui n'a plus d'idées, ou qui est trop lent prend {glou} schlouks.", 0, 0);
        db.addQuestion("Jeu à thème !\n" +
                "Les films français.\n" +
                "Le premier joueur qui n'a plus d'idées, ou qui est trop lent prend {glou} schlouks.", 0, 0);
        db.addQuestion("Jeu à thème !\n" +
                "Les humoristes français.\n" +
                "Le premier joueur qui n'a plus d'idées, ou qui est trop lent prend {glou} schlouks.", 0, 0);
        db.addQuestion("Jeu à thème !\n" +
                "Les chanteurs français.\n" +
                "Le premier joueur qui n'a plus d'idées, ou qui est trop lent prend {glou} schlouks.", 0, 0);
        db.addQuestion("Jeu à thème !\n" +
                "Les chanteurs des années 2000.\n" +
                "Le premier joueur qui n'a plus d'idées, ou qui est trop lent prend {glou} schlouks.", 0, 0);
        db.addQuestion("Jeu à thème !\n" +
                "Les séries dont la fin est naze.\n" +
                "Le premier joueur qui n'a plus d'idées, ou qui est trop lent prend {glou} schlouks.", 0, 0);
        db.addQuestion("Jeu à thème !\n" +
                "Les séries surcotées.\n" +
                "Le premier joueur qui n'a plus d'idées, ou qui est trop lent prend {glou} schlouks.", 0, 0);
        db.addQuestion("Chaque joueur complimente tour à tour sincèrement la personne à sa gauche.\n" +
                "Si une personne prend trop de temps, la personne qu'iel devait complimenter peut lui donner un gage.", 0, 0);
        db.addQuestion("Tout le monde raconte tour à tour un de ses rêves.\n" +
                "Ceux qui disent la même chose trinquent ensembles à leurs bons goûts !", 0, 0);
        db.addQuestion("Tous ceux qui ont déjà fait leur coming out, buvez {glou} schlouks pour fêter ça !\n" +
                "Ceux pour qui c'est prévu, c'est le moment de briller, distribuez 10 gorgées !", 0, 0);

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
