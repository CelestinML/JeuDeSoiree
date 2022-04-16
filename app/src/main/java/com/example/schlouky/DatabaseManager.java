package com.example.schlouky;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
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
    public List<Player> players;
    private List<Player> playersFromCurrentQuestion;

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.getWritableDatabase();
        this.players = new ArrayList<>();
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
        setupDatabase(sqlDatabase);
    }

    public void setupDatabase() {
        setupDatabase(this.getWritableDatabase());
    }

    public void setupDatabase(SQLiteDatabase database) {
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

    public Question getRandomQuestion() {

        String rawQuestion = "";
        List<Player> randomPlayerList = new ArrayList<>();

        while (rawQuestion.equals("")) {
            randomPlayerList = getRandomPlayers();

            int playerCount = randomPlayerList.size();
            int drinkingPlayerCount = 0;
            for(int i = 0; i < playerCount; i++)
            {
                if(randomPlayerList.get(i).buveur)
                {
                    drinkingPlayerCount++;
                }
            }

            rawQuestion = getRawQuestion(playerCount, drinkingPlayerCount);
        }

        String filledQuestion = getFilledQuestion(rawQuestion, randomPlayerList);

        //mise a jour des chances des joueurs
        for(int i = 0; i < players.size(); i++)
        {
            Player p = players.get(i);
            if(randomPlayerList.contains(p)) { //si le joueur a été choisi a cette question
                p.chance = 0;
            }
            p.chance += (int)(Math.random() * 3);
        }

        Question res = new Question(filledQuestion, randomPlayerList);
        return res;
    }

    private List<Player> getRandomPlayers()
    {
        int playerCount = (int)Math.floor(Math.random() * (Math.min(players.size(), 3) + 1));

        List<Player> randomPlayerList = new ArrayList<>(players);
        Collections.sort(randomPlayerList, new PlayerChanceCompare()); //trie par chance
        randomPlayerList = randomPlayerList.subList(0, playerCount); //garde les plus chanceux
        Collections.sort(randomPlayerList, new PlayerDrinkingCompare()); //met les buveurs en début de liste

        return randomPlayerList;
    }

    private String getRawQuestion(int playerCount, int drinkingPlayerCount) {
        SQLiteDatabase database = this.getReadableDatabase();
        String request = String.format("SELECT %s, %s FROM %s WHERE %s = %d AND %s <= %d;",
                ID_FIELD, QUESTION_FIELD, TABLE_NAME, PLAYER_COUNT_FIELD, playerCount, DRINKING_PLAYER_COUNT_FIELD, drinkingPlayerCount);

        try (Cursor result = database.rawQuery(request, null)) {
            int questionsCount = result.getCount();
            if (questionsCount > 0) {
                //loops through all the query answers and fill the questions list
                List<String> questionsList = new ArrayList<>(questionsCount);
                while(result.moveToNext())
                {
                    questionsList.add(result.getString(1));
                }

                //selects a random question
                int randomIndex = (int)(Math.random() * questionsCount);
                return questionsList.get(randomIndex);
            }
        }
        return ""; //if no question was found
    }

    private String getFilledQuestion(String rawQuestion, List<Player> players)
    {
        String res = rawQuestion;
        if (players.size() > 0) res = res.replace("{joueur1}", players.get(0).name);
        if (players.size() > 1) res = res.replace("{joueur2}", players.get(1).name);
        if (players.size() > 2) res = res.replace("{joueur3}", players.get(2).name);
        if (players.size() > 3) res = res.replace("{joueur4}", players.get(3).name);
        res = res.replace("{glou}", String.valueOf((int)(1 + Math.random() * 4)));
        return res;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public List<Player> getPlayersInCurrentQuestion() {
        return playersFromCurrentQuestion;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

class Question implements Parcelable {
    public String text;
    public List<Player> players;

    public Question(String text, List<Player> players)
    {
        this.text = text;
        this.players = players;
    }

    protected Question(Parcel in) {
        text = in.readString();
        players = in.readArrayList(Player.class.getClassLoader());
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(text);
        parcel.writeList(players);
    }
}
