package com.example.schlouky;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class SetupActivity extends AppCompatActivity {

    Button add;
    Button button_start;
    AlertDialog dialogr;
    LinearLayout layout;

    int maxPlayers = 5;
    int minPlayers = 3;
    int nbrPlayers = 0;
    ArrayList<Player> previousPlayers;
    ArrayList<Player> players = new ArrayList<>();

    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView targetImageView;
    String targetPlayerName;

    ArrayList<View> playerCards = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setup);

        if (getIntent().hasExtra("Players")) {
            previousPlayers = getIntent().getParcelableArrayListExtra("Players");
        }

        // Création de la fenêtre de dialogue permettant d'ajouter des joueurs
        //buildDialog();

        // Récupération des éléments de la view setup
        // Le bouton pour ajouter des joueurs + assignation de sa fonction onClick
        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nbrPlayers == maxPlayers) {
                    // Si le nombre max de joueurs est atteint, impossible d'ajouter de nouveaux joueurs
                    String str = "Vous ne pouvez pas ajouter plus de " + maxPlayers + " joueurs.";
                    Toast.makeText(SetupActivity.this, str, Toast.LENGTH_SHORT).show();
                } else {
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

    @Override
    protected void onStart() {
        super.onStart();
        if (previousPlayers != null) {
            for (Player player : previousPlayers) {
                AddCard(player);
            }
        }
        previousPlayers = null;
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
                        if (PlayerAlreadyExist(name.getText().toString())) {
                            Toast.makeText(SetupActivity.this, "Ce nom est déjà pris par un autre joueur.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Ajout d'un nouveau joueur si appuit sur ok
                            AddCard(name.getText().toString(), buveur.isChecked(), "");

                            // Si buveur a été décoché, on le coche par défaut pour la personne suivante
                            if (buveur.isChecked() == false) buveur.toggle();
                            //Dismiss once everything is OK.
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void AddCard(String name, boolean buveur, String photoPath) {
        // Il y a un joueur de plus
        nbrPlayers++;

        // Ajout du joueur et de ses informations dans la base de données
        Player newPlayer = new Player(name, buveur, photoPath);
        players.add(newPlayer);

        // Récupération du layout new_player_card et de ses éléments
        View view = getLayoutInflater().inflate(R.layout.new_player_card, null);

        ImageView photoView = view.findViewById(R.id.imageView);

        if (photoPath != "") {
            Bitmap loadedPhoto = loadPhoto(photoPath);

            if (loadedPhoto != null) {
                photoView.setImageBitmap(loadedPhoto);
            }
        }

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(photoView, name);
            }
        });

        // L'icone buveur + assignation
        ImageView buveurView = view.findViewById(R.id.buveur);
        // Temporaire, permet de changer l'image si non buveur
        if (buveur == false) {
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
                if(nbrPlayers < minPlayers) button_start.setVisibility(View.INVISIBLE);

                // Suppression du joueur de la liste des joueurs
                RemovePlayer(name);
            }
        });

        // Ajout de la view card du joueur
        layout.addView(view);

        playerCards.add(view);
            // La possibilité de cliquer sur la carte pour modifier le joueur
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vieww) {
                    ModifyPlayer(view);
                }
            });

            // Ajout de la view card du joueur
            layout.addView(view);

        // Activation du bouton pour commencer la partie si il y a au moins deux joueurs
        if (nbrPlayers >= minPlayers) button_start.setVisibility(View.VISIBLE);
    }
    private void AddCard(Player player) {
        AddCard(player.name, player.buveur, player.photoPath);
    }

    public void StartGameActivity(View view) {
        // Création de l'intent pour appeler GameActivity
        Intent intent = new Intent(this, GameActivity.class);

        // Enregistrement des données (liste players)
        intent.putParcelableArrayListExtra("Players", players);

        // Démarrage de GameActivity
        startActivity(intent);
    }

    // Permet de supprimer un joueur de la liste des joueurs
    private void RemovePlayer(String name) {
        int indiceToRemove = 0;
        for (Player p : players)
        {
            if(p.name.equals(name))
            {
                //String str = p.name + " est en PLS";
                //Toast.makeText(SetupActivity.this, str, Toast.LENGTH_SHORT).show();
                players.remove(indiceToRemove);
                return;
            }
            indiceToRemove++;
        }
    }

    // Permet de ne pas avoir deux joueurs qui ont le même nom
    private boolean PlayerAlreadyExist(String name) {
        for (Player p : players) {
            if (p.name.equals(name)) return true;
        }
        return false;
    }

    private void ModifyPlayer(View v) {
        // On récupère les informations actuelles
        // L'icone buveur
        ImageView buveurView = v.findViewById(R.id.buveur);

        // Le nom du joueur
        TextView nameView = v.findViewById(R.id.name);

        // Suppression du joueur de la liste des joueurs
        RemovePlayer(nameView.getText().toString());

        // Récupération du layout dialog et de ses éléments
        View viewdialog = getLayoutInflater().inflate(R.layout.dialog, null);

        // Champ de texte pour entrer le non du joueur
        EditText namedialog = viewdialog.findViewById(R.id.nameEdit);
        namedialog.setText(nameView.getText().toString());

        // Switch pour indiquer si le joueur est un buveur ou non
        Switch buveurdialog = viewdialog.findViewById(R.id.buveur);


        if(buveurView.getVisibility() == View.INVISIBLE) buveurdialog.toggle();

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(viewdialog)
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
                        if(PlayerAlreadyExist(namedialog.getText().toString()))
                        {
                            Toast.makeText(SetupActivity.this, "Ce nom est déjà pris par un autre joueur.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            // Assignation des nouvelles valeurs à la view du joueur déjà existante
                            nameView.setText(namedialog.getText());
                            // Temporaire, permet de changer l'image si non buveur
                            if(buveurdialog.isChecked() == false)
                            {
                                buveurView.setVisibility(View.INVISIBLE);
                            }
                            else buveurView.setVisibility(View.VISIBLE);

                            // Rajout du joueur modifié si appuit sur ok
                            players.add(new Player(namedialog.getText().toString(), buveurdialog.isChecked()));

                            // Si buveur a été décoché, on le coche par défaut pour la personne suivante
                            if(buveurdialog.isChecked() == false) buveurdialog.toggle();
                            //Dismiss once everything is OK.
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }
    
    @Override
    public void onBackPressed() { }

    private void dispatchTakePictureIntent(ImageView imgView, String playerName) {
        targetImageView = imgView;
        targetPlayerName = playerName;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String imagePath = getGalleryPath() + targetPlayerName + ".png";
            try (FileOutputStream out = new FileOutputStream(imagePath)) {

                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored

                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).name == targetPlayerName) {
                        players.get(i).photoPath = imagePath;
                    }
                }

                targetImageView.setImageBitmap(imageBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getGalleryPath() {
        return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";
    }

    private void DestroyAllCards() {
        for (int i = playerCards.size() - 1; i >= 0; i--) {
            layout.removeView(playerCards.get(i));
            playerCards.remove(i);
        }
    }

    private Bitmap loadPhoto(String path) {
        File imgFile = new  File(path);
        if(imgFile.exists()){
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        else {
            return null;
        }
}
