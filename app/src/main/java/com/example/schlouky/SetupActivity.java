package com.example.schlouky;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class SetupActivity extends AppCompatActivity {

    Button add;
    Button button_start;
    AlertDialog dialogr;
    LinearLayout layout;

    int maxPlayers = 5;
    int nbrPlayers = 0;
    ArrayList<Player> players;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setup);

        //nbrPlayers = 0;
        players = new ArrayList<Player>();
        //players.add(new Player("Perceval", true));

        // Création de la fenêtre de dialogue permettant d'ajouter des joueurs
        //buildDialog();

        // Récupération des éléments de la view setup
        // Le bouton pour ajouter des joueurs + assignation de sa fonction onClick
        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nbrPlayers == maxPlayers)
                {
                    // Si le nombre max de joueurs est atteint, impossible d'ajouter de nouveaux joueurs
                    String str = "Vous ne pouvez pas ajouter plus de " + maxPlayers + " joueurs.";
                    Toast.makeText(SetupActivity.this, str, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // Sinon afficher la fenêtre de dialogue pour ajouter un nouveau joueur
                    buildDialog();
                }

            }
        });

        // Le bouton pour commencer la partie (invisible initialement car il y a moins de 2 joueurs)
        button_start = findViewById(R.id.startGame);
        button_start.setVisibility(View.INVISIBLE); // A modifier plus tard quand on passera de l'écran de fin à l'écran setup et qu'il y aura dejà des joueurs
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartGameActivity(view);
            }
        });

        // La scroll view qui contiendra tous les joueurs
        layout = findViewById(R.id.container);

    }


    private void buildDialog() {
        // Récupération du layout dialog et de ses éléments
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        // Champs de texte pour entrer le non du joueur
        EditText name = view.findViewById(R.id.nameEdit);
        // Switch pour indiquer si le joueur est un buveur ou non
        Switch buveur = view.findViewById(R.id.buveur);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Enter name")
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        if(PlayerAlreadyExist(name.getText().toString()))
                        {
                            Toast.makeText(SetupActivity.this, "Ce nom est déjà pris par un autre joueur.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            // Ajout d'un nouveau joueur si appuit sur ok
                            AddCard(name.getText().toString(), buveur.isChecked());

                            // Si buveur a été décoché, on le coche par défaut pour la personne suivante
                            if(buveur.isChecked() == false) buveur.toggle();
                            //Dismiss once everything is OK.
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void AddCard(String name, boolean buveur) {
            // Il y a un joueur de plus
            nbrPlayers++;

            // Récupération du layout new_player_card et de ses éléments
            View view = getLayoutInflater().inflate(R.layout.new_player_card, null);

            // L'icone buveur + assignation
            ImageView buveurView = view.findViewById(R.id.buveur);
            // Temporaire, permet de changer l'image si non buveur
            if(buveur == false)
            {
                buveurView.setVisibility(View.INVISIBLE);
            }

            // Le nom du joueur + assignation
            TextView nameView = view.findViewById(R.id.name);
            nameView.setText(name);

            // Le bouton delete + assignation de sa fonction onClick
            ImageButton delete = view.findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Il y a un joueur en moins
                    nbrPlayers--;

                    // Suppression de la view card du joueur
                    layout.removeView(view);

                    // Désactivation du bouton pour commencer la partie si il y a moins de deux joueurs
                    if(nbrPlayers < 2) button_start.setVisibility(View.INVISIBLE);

                    // Suppression du joueur de la liste des joueurs
                    RemovePlayer(name);
                }
            });

            // Ajout de la view card du joueur
            layout.addView(view);

            // Ajout du joueur et de ses informations dans la base de données
            players.add(new Player(name, buveur));

            // Activation du bouton pour commencer la partie si il y a au moins deux joueurs
            if(nbrPlayers >= 2) button_start.setVisibility(View.VISIBLE);
    }

    public void StartGameActivity(View view) {
        // Création de l'intent pour appeler GameActivity
        Intent intent = new Intent(this, GameActivity.class);

        // Enregistrement des données (liste players)
        intent.putParcelableArrayListExtra("UniqueKey", players);

        // Démarrage de GameActivity
        startActivity(intent);
    }

    // Permet de supprimer un joueur de la liste des joueurs
    private void RemovePlayer(String name)
    {
        int indiceToRemove = 0;
        for (Player p : players)
        {
            if(p.name.equals(name))
            {
                String str = p.name + " est en PLS";
                Toast.makeText(SetupActivity.this, str, Toast.LENGTH_SHORT).show();
                players.remove(indiceToRemove);
                return;
            }
            indiceToRemove++;
        }
    }

    // Permet de ne pas avoir deux joueurs qui ont le même nom
    private boolean PlayerAlreadyExist(String name)
    {
        for (Player p : players)
        {
            if(p.name.equals(name)) return true;
        }
        return false;
    }
/*
    public class Player implements Parcelable
    {
        String name;
        Boolean buveur;

        Player(String name, Boolean buveur)
        {
            this.name = name;
            this.buveur = buveur;
        }

        protected Player(Parcel in) {
            name = in.readString();
            byte tmpBuveur = in.readByte();
            buveur = tmpBuveur == 0 ? null : tmpBuveur == 1;
        }

        public final Creator<Player> CREATOR = new Creator<Player>() {
            @Override
            public Player createFromParcel(Parcel in) {
                return new Player(in);
            }

            @Override
            public Player[] newArray(int size) {
                return new Player[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(name);
            parcel.writeByte((byte) (buveur == null ? 0 : buveur ? 1 : 2));
        }
    }*/
}
