package com.example.auspectuspantallas;

import android.os.Bundle;
import android.text.InputFilter;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Acentos1 extends AppCompatActivity {
    private MediaPlayer Mp1, Mp2, Mp3;
    private ProgressBar BarraP1;
    private Handler Handler1 = new Handler();
    private Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            if (Mp1 != null && Mp1.isPlaying()){
                BarraP1.setProgress(Mp1.getCurrentPosition());
                Handler1.postDelayed(this, 1000);
            }
        }
    };
    private Button btnVerificar;
    private EditText EtAcento;
    private ImageButton ImgBtn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_acentos1);
        Mp1 = MediaPlayer.create(this, R.raw.acento_argentino);
        Mp2 = MediaPlayer.create(this, R.raw.correct);
        Mp3 = MediaPlayer.create(this, R.raw.incorrect);
        btnVerificar = findViewById(R.id.btnVerf);
        EtAcento = findViewById(R.id.ETAcento);
        BarraP1 = (ProgressBar) findViewById(R.id.progressBar1);
        ImgBtn1 = (ImageButton) findViewById(R.id.ImgBtn1);
        EtAcento.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        Mp1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                BarraP1.setMax(Mp1.getDuration());
                BarraP1.setProgress(0);
            }
        });
        Mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                BarraP1.setProgress(0);
                Handler1.removeCallbacks(runnable1);
            }
        });

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

        btnVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Mp2.start();
                Mp1.stop();
                Mp1.reset();
                Mp1 = MediaPlayer.create(Acentos1.this, R.raw.acento_argentino);

                String EAcento = EtAcento.getText().toString().trim();
                if (EAcento.isEmpty()){
                    Toast.makeText(Acentos1.this, "Coloca el acento", Toast.LENGTH_SHORT).show();
                    EtAcento.requestFocus();
                } else if (!EAcento.equals("ARGENTINO")){
                    Toast.makeText(Acentos1.this, "Es Incorrecto", Toast.LENGTH_SHORT).show();
                    EtAcento.requestFocus();
                } else if (EAcento.equals("ARGENTINO")) {
                    MiAlerta1.mostrarAlerta1(Acentos1.this, R.style.MiEstiloAlerta1,"Correcto", "Has acertado.", MainActivity.class);
                }
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