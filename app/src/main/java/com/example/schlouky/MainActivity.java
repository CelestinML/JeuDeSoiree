package com.example.schlouky;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    int[] backgrounds = new int[]{R.drawable.background1, R.drawable.background2, R.drawable.background3};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        RelativeLayout relativeLayout = findViewById(R.id.RelativeLayout01);
        relativeLayout.setBackground(getDrawable(backgrounds[new Random().nextInt(backgrounds.length)]));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                StartNextActivity();
            }
        }, 2000);
    }

    private void StartNextActivity() {
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
    }
}