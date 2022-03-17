package com.example.schlouky;

import androidx.appcompat.app.AppCompatActivity;

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

    float threshold = 300;
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
                                    currentQuestionIndex++;
                                    if(currentQuestionIndex == questionNb){
                                        getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                                                .setReorderingAllowed(true)
                                                .add(R.id.question_location, EndGameFragment.class, null)
                                                .commit();
                                    }
                                    else if (currentQuestionIndex < questionNb) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("question", questions.get(currentQuestionIndex));

                                        getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                                                .setReorderingAllowed(true)
                                                .add(R.id.question_location, QuestionFragment.class, bundle)
                                                .commit();
                                    }
                                    else {
                                        currentQuestionIndex = questionNb;
                                    }
                                }
                                else {
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
                                    }
                                    else {
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
}