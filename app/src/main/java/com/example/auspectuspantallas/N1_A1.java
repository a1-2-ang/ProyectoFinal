package com.example.auspectuspantallas;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class N1_A1 extends AppCompatActivity {
    private MediaPlayer Mp1, Mp2, Mp3;
    private Handler Handler1 = new Handler();
    private Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            if (Mp1 != null && Mp1.isPlaying()){
                Handler1.postDelayed(this, 1000);
            }
        }
    };
    private Button btnCont;
    private ImageButton ImgBtn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_n1_a1);
        Mp1 = MediaPlayer.create(this, R.raw.miau);
        Mp2 = MediaPlayer.create(this, R.raw.correct);
        Mp3 = MediaPlayer.create(this, R.raw.incorrect);
        btnCont = findViewById(R.id.btnContXML);
        ImgBtn1 = (ImageButton) findViewById(R.id.imageBtn);

        ImgBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Mp1.isPlaying()){
                    Mp1.pause();
                    Handler1.removeCallbacks(runnable1);
                } else {
                    Mp1.start();
                    Handler1.post(runnable1);
                }
            }
        });
        btnCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(N1_A1.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (Mp1 != null){
            Mp1.release();
            Mp1 = null;
        }
        Handler1.removeCallbacks(runnable1);
    }
}