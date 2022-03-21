package com.example.schlouky;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {

    private static DatabaseManager instance;

    //Database settings
    private static final String DATABASE_NAME = "SHLOUKY_QUESTIONS";
    private static final int DATABASE_VERSION = 1;

    //Table settings
    private static final String TABLE_NAME = "Questions";
    private static final String ID_FIELD = "id";
    private static final String QUESTION_FIELD = "question";
    private static final String PLAYER_COUNT_FIELD = "player_count";
    private static final String DRINKING_PLAYER_COUNT_FIELD = "drinking_player_count";

    //Players data
    private List<String> playerList;

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.getWritableDatabase();
        playerList = new ArrayList<>();
    }

    //singleton
    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDatabase) {
        setupDatabase();
    }

    public void setupDatabase() {
        SQLiteDatabase database = this.getWritableDatabase();

        //we first delete the table if it exists
        String dropRequest = String.format("DROP TABLE IF EXISTS %s;", TABLE_NAME);
        database.execSQL(dropRequest);

        //then we create it
        String createTableRequest = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s INT, %s INT);",
                TABLE_NAME, ID_FIELD, QUESTION_FIELD, PLAYER_COUNT_FIELD, DRINKING_PLAYER_COUNT_FIELD);
        database.execSQL(createTableRequest);
    }

    public void addQuestion(String question, int playerCount, int drinkingPlayerCount) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(QUESTION_FIELD, question);
        values.put(PLAYER_COUNT_FIELD, playerCount);
        values.put(DRINKING_PLAYER_COUNT_FIELD, drinkingPlayerCount);

        database.insert(TABLE_NAME, null, values);
    }

    public String getQuestion() {
        SQLiteDatabase database = this.getReadableDatabase();
        String request = String.format("SELECT * FROM %s;", TABLE_NAME);
        try (Cursor result = database.rawQuery(request, null)) {
            if (result.getCount() > 0) {
                result.moveToNext();
                String questionString = result.getString(1);

                //TODO: ajouter une vérification du nombre de joueurs
                return String.format(questionString, playerList.get(0), playerList.get(1));

                //while(result.moveToNext())
            }
        }
        return "";
    }

    public void addPlayer(String name, boolean isDrinking) {
        playerList.add(name);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}