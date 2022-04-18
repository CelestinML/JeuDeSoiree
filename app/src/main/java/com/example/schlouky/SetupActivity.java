package com.example.schlouky;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class SetupActivity extends AppCompatActivity {

    Button add;
    Button button_start;
    LinearLayout layout;

    int maxPlayers = 15;
    int minPlayers = 2;
    int nbrPlayers = 0;
    ArrayList<Player> previousPlayers;
    ArrayList<Player> players = new ArrayList<>();

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PERMISSIONS = 2;

    ImageView targetImageView;
    String targetPlayerName;
    File output;

    ArrayList<View> playerCards = new ArrayList<>();

    int[] backgrounds = new int[]{R.drawable.background1, R.drawable.background2, R.drawable.background3};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setup);

        RelativeLayout relativeLayout = findViewById(R.id.activity_setup);
        relativeLayout.setBackground(getDrawable(backgrounds[new Random().nextInt(backgrounds.length)]));

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

    private void getRandomBackground(){

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
                .setTitle("Entrez le nom")
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
                        if (PlayerAlreadyExist(name.getText().toString(), null)) {
                            Toast.makeText(SetupActivity.this, "Ce nom est déjà pris par un autre joueur.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Ajout d'un nouveau joueur si appuit sur ok
                            AddCard(name.getText().toString(), buveur.isChecked(), null, null);

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

    private void AddCard(String name, boolean buveur, Uri photoUri, String photoPath) {
        // Il y a un joueur de plus
        nbrPlayers++;

        // Ajout du joueur et de ses informations dans la base de données
        Player newPlayer = new Player(name, buveur, photoUri, photoPath);
        players.add(newPlayer);

        // Récupération du layout new_player_card et de ses éléments
        View view = getLayoutInflater().inflate(R.layout.new_player_card, null);

        ImageView photoView = view.findViewById(R.id.imageView);

        if (photoUri != null) {
            Bitmap loadedPhoto = loadPhoto(photoUri, photoPath);

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
                if (nbrPlayers < minPlayers) button_start.setVisibility(View.INVISIBLE);

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

        // Activation du bouton pour commencer la partie si il y a au moins deux joueurs
        if (nbrPlayers >= minPlayers) button_start.setVisibility(View.VISIBLE);
    }

    private void AddCard(Player player) {
        AddCard(player.name, player.buveur, player.photoUri, player.photoPath);
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
        for (Player p : players) {
            if (p.name.equals(name)) {
                //String str = p.name + " est en PLS";
                //Toast.makeText(SetupActivity.this, str, Toast.LENGTH_SHORT).show();
                players.remove(indiceToRemove);
                return;
            }
            indiceToRemove++;
        }
    }

    // Permet de ne pas avoir deux joueurs qui ont le même nom
    private boolean PlayerAlreadyExist(String name, String ignoredPlayer) {
        for (Player p : players) {
            if (ignoredPlayer != null) {
                if (p.name == ignoredPlayer) {
                    continue;
                }
            }
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

        // Récupération du layout dialog et de ses éléments
        View viewdialog = getLayoutInflater().inflate(R.layout.dialog, null);

        // Champ de texte pour entrer le non du joueur
        EditText namedialog = viewdialog.findViewById(R.id.nameEdit);
        namedialog.setText(nameView.getText().toString());

        // Switch pour indiquer si le joueur est un buveur ou non
        Switch buveurdialog = viewdialog.findViewById(R.id.buveur);


        if (buveurView.getVisibility() == View.INVISIBLE) buveurdialog.toggle();

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(viewdialog)
                .setTitle("Entrez le nom")
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
                        String playerOldName = nameView.getText().toString();
                        String playerNewName = namedialog.getText().toString();
                        if (PlayerAlreadyExist(playerNewName, playerOldName)) {
                            Toast.makeText(SetupActivity.this, "Ce nom est déjà pris par un autre joueur.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Assignation des nouvelles valeurs à la view du joueur déjà existante
                            nameView.setText(playerNewName);
                            // Temporaire, permet de changer l'image si non buveur
                            if (buveurdialog.isChecked() == false) {
                                buveurView.setVisibility(View.INVISIBLE);
                            } else buveurView.setVisibility(View.VISIBLE);

                            // Modification du joueur
                            for (int i = 0; i < players.size(); i++) {
                                if (players.get(i).name == playerOldName) {
                                    players.get(i).name = playerNewName;
                                    players.get(i).buveur = buveurdialog.isChecked();
                                    break;
                                }
                            }

                            //On actualise le listener de l'appareil photo avec le nouveau pseudo
                            ImageView photoView = v.findViewById(R.id.imageView);
                            photoView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dispatchTakePictureIntent(photoView, playerNewName);
                                }
                            });

                            // Si buveur a été décoché, on le coche par défaut pour la personne suivante
                            if (buveurdialog.isChecked() == false) buveurdialog.toggle();
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
    public void onBackPressed() {
    }

    private void dispatchTakePictureIntent(ImageView imgView, String playerName) {
        targetImageView = imgView;
        targetPlayerName = playerName;
        if (ContextCompat.checkSelfPermission(
                SetupActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Schlouky");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            output = new File(dir, targetPlayerName + ".jpeg");
            Uri uri;
            if (getApplicationContext().getApplicationInfo().targetSdkVersion >= 24) {
                uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", output);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(output);
            }

            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).name == targetPlayerName) {
                    players.get(i).photoUri = uri;
                    players.get(i).photoPath = output.getAbsolutePath();
                }
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (ActivityNotFoundException e) {
                // display error state to the user
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent(targetImageView, targetPlayerName);
            } else {
                Toast.makeText(this, "Impossible d'utiliser la fonctionnalité \"Photo\" sans votre permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = loadPhoto(Uri.fromFile(output), output.getPath());
            targetImageView.setImageBitmap(imageBitmap);
        }
    }

    private Bitmap loadPhoto(Uri uri, String path) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ExifInterface exif = null;
            try {
                File pictureFile = new File(path);
                exif = new ExifInterface(pictureFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            int orientation = ExifInterface.ORIENTATION_NORMAL;

            if (exif != null)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateBitmap(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateBitmap(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateBitmap(bitmap, 270);
                    break;
            }

            return bitmap;

        } catch (IOException e) {
            return null;
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
