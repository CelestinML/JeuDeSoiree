package com.example.schlouky;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SetupActivity extends AppCompatActivity {

    Button add;
    Button button_start;
    AlertDialog dialog;
    LinearLayout layout;

    int maxPlayers = 5;
    int nbrPlayers;
    ArrayList<Player> players = new ArrayList<Player>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        add = findViewById(R.id.add);
        button_start = findViewById(R.id.startGame);
        layout = findViewById(R.id.container);

        buildDialog();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nbrPlayers == maxPlayers)
                {
                    String str = "Vous ne pouvez pas ajouter plus de " + maxPlayers + " joueurs.";
                    Toast.makeText(SetupActivity.this, str, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    dialog.show();
                }

            }
        });

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartGameActivity(view);
            }
        });

        button_start.setVisibility(View.INVISIBLE);
        nbrPlayers = 0;

    }

    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        EditText name = view.findViewById(R.id.nameEdit);

        builder.setView(view);
        builder.setTitle("Enter name")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addCard(name.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        dialog = builder.create();
    }

    private void addCard(String name) {
            nbrPlayers++;

            View view = getLayoutInflater().inflate(R.layout.new_player_card, null);

            TextView nameView = view.findViewById(R.id.name);
            ImageButton delete = view.findViewById(R.id.delete);

            nameView.setText(name);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nbrPlayers--;
                    layout.removeView(view);
                    // J'ai pas encore remove le player de la liste
                    // players.remove(identifier le player à remove par son pseudo)
                    if(nbrPlayers < 2)
                    {
                        button_start.setVisibility(View.INVISIBLE);
                    }
                }
            });

            layout.addView(view);
            players.add(new Player(name, true));

            if(nbrPlayers >= 2)
            {
                button_start.setVisibility(View.VISIBLE);

            }
    }

    public void StartGameActivity(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private class Player
    {
        String name;
        Boolean buveur;

        Player(String name, Boolean buveur)
        {
            this.name = name;
            this.buveur = buveur;
        }
    }



    /*
    ImageView button_addPlayer;
    Button button_start;
    LinearLayout layout;

    //ArrayList<View> players_view = new ArrayList<View>();

    ArrayList<Player> players = new ArrayList<Player>();

    int maxPlayers = 15;
    int nbrPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // Assignations des view
        button_addPlayer = (ImageView) findViewById(R.id.button_addPlayer);
        button_start = (Button) findViewById(R.id.button_start);
        button_start.setVisibility(View.INVISIBLE);
        layout = (LinearLayout) findViewById(R.id.container);

        // Assignation de la fonction du bouton pour ajouter des joueurs
        button_addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlayer("coucou");
            }
        });

        // Assignation de la fonction du bouton pour lancer la partie
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartGameActivity(view);
            }
        });


    }

    private void addPlayer(String string) {

        // Permet de limiter le nombre de joueurs
        if(nbrPlayers == maxPlayers)
        {
            String str = "Vous ne pouvez pas ajouter plus de " + maxPlayers + " joueurs.";
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        }
        else
        {
            nbrPlayers++;
            String str = "Joueur ping" + nbrPlayers;

            // Toast Temporaire
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

            // Création de la view
            View newPlayer = getLayoutInflater().inflate(R.layout.new_player_card, null);

            ImageView photo = (ImageView) newPlayer.findViewById(R.id.photo);
            EditText pseudo = (EditText) newPlayer.findViewById(R.id.pseudo);
            ImageView buveur = (ImageView) newPlayer.findViewById(R.id.buveur);
            ImageView delete = (ImageView) findViewById(R.id.delete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //DeletePlayer(newPlayer);
                    layout.removeView(newPlayer);
                    Toast.makeText(SetupActivity.this, "Joueur supprimé", Toast.LENGTH_SHORT).show();
                }
            });

            // Ajout de la view dans le layout
            layout.addView(newPlayer);
            // Ajout du joueur dans la liste de joueurs
            players.add(new Player(pseudo.getText().toString(), true, newPlayer));
            //players_view.add(newPlayer);

            // Afficher le bouton pour commencer la partie si il y a au moins deux joueurs
            if(nbrPlayers >= 2)
            {
                button_start.setVisibility(View.VISIBLE);

            }

        }

    }


    public void DeletePlayer(View v)
    {
        //Toast.makeText(this, "Joueur supprimé", Toast.LENGTH_SHORT).show();
        /*layout.removeView(players_view.get(0));
        players_view.remove(0);
        nbrPlayers--;
        // Enlever le bouton pour commencer la partie si il y a moins de deux joueurs
        if(nbrPlayers < 2)
        {
            button_start.setVisibility(View.INVISIBLE);
        }

        // Test si je peux récupérer le nom du joueur en cliquant sur delete
        //String str = v.pa
    }

    public void StartGameActivity(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }


    private class Player
    {
        String name;
        Boolean buveur;
        View view;

        Player(String name, Boolean buveur, View view)
        {
            this.name = name;
            this.buveur = buveur;
            this.view = view;
        }
    }*/

}
