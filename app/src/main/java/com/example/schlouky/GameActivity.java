package com.example.schlouky;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
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

    float threshold = 1;
    float x1, x2, y1, y2, dx, dy;
    String direction;

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

        findViewById(R.id.question_location).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case(MotionEvent.ACTION_DOWN): {
                        x1 = event.getX();
                        y1 = event.getY();
                        return true;
                    }
                    case(MotionEvent.ACTION_UP): {
                        x2 = event.getX();
                        y2 = event.getY();
                        dx = x2-x1;
                        dy = y2-y1;

                        // Use dx and dy to determine the direction of the move
                        if(Math.abs(dx) > Math.abs(dy)) {
                            if (Math.abs(dx) > threshold) {
                                if(dx<0) {
                                    //Next question
                                    currentQuestionIndex = Math.min(questionNb, currentQuestionIndex +1);
                                    if(currentQuestionIndex == questionNb){
                                        getSupportFragmentManager().beginTransaction()
                                                .setReorderingAllowed(true)
                                                .add(R.id.question_location, EndGameFragment.class, null)
                                                .commit();
                                    }
                                    else{
                                        Bundle bundle = new Bundle();
                                        bundle.putString("question", questions.get(currentQuestionIndex));

                                        getSupportFragmentManager().beginTransaction()
                                                .setReorderingAllowed(true)
                                                .add(R.id.question_location, QuestionFragment.class, bundle)
                                                .commit();
                                    }

                                }
                                else {
                                    //Previous question
                                    currentQuestionIndex = Math.max(0,currentQuestionIndex-1);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("question", questions.get(currentQuestionIndex));

                                    getSupportFragmentManager().beginTransaction()
                                            .setReorderingAllowed(true)
                                            .add(R.id.question_location, QuestionFragment.class, bundle)
                                            .commit();
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
}