package com.example.schlouky;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    ArrayList<String> questions = new ArrayList<String>() {
        {
            add("hey slt comment ça ? Tu fais quoi aujourd'hui ? Oh cool trop bien j'adore ta vie");
            add("slt");
            add("yo");
        }
    };

    int currentQuestionIndex = 0;

    int questionNb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        questionNb = questions.size();

        Bundle bundle = new Bundle();
        bundle.putString("question", questions.get(currentQuestionIndex));

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.question_location, QuestionFragment.class, bundle)
                .commit();
    }

    public void StartSetupActivity(View view) {
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
    }
}