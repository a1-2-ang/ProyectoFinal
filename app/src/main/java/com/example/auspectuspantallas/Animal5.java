package com.example.auspectuspantallas;

import android.os.Bundle;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class Animal5 extends AppCompatActivity {
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
    private Button R1, R2, R3, R4;
    private ImageButton ImgBtn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal5);

        Mp1 = MediaPlayer.create(this, R.raw.caballo);
        Mp2 = MediaPlayer.create(this, R.raw.correct);
        Mp3 = MediaPlayer.create(this, R.raw.incorrect);
        R1 = findViewById(R.id.btnResp1);
        R2 = findViewById(R.id.btnResp2);
        R3 = findViewById(R.id.btnResp3);
        R4 = findViewById(R.id.btnResp4);
        BarraP1 = (ProgressBar) findViewById(R.id.progressBar1);
        ImgBtn1 = (ImageButton) findViewById(R.id.ImgBtn1);

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
//                if (Mp1.isPlaying()){
//                    Mp1.pause();
//                    Handler1.removeCallbacks(runnable1);
//                } else {
                Mp1.start();
                Handler1.post(runnable1);
//                }
            }
        });

        R1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mp3.start();
                Mp1.stop();
                Mp1.reset();
                Mp1 = MediaPlayer.create(Animal5.this, R.raw.caballo);
                String mensajeFinal = "Felicidades, llegaste al final.\nTu resultado es: " + DatosGlobales.contador;
                AlertDialog.Builder builder = new AlertDialog.Builder(Animal5.this, R.style.MiEstiloAlerta2);
                builder.setTitle("Resultado Final");
                builder.setMessage(mensajeFinal);
                builder.setCancelable(false);
                builder.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Animal5.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        });
        R2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mp3.start();
                Mp1.stop();
                Mp1.reset();
                Mp1 = MediaPlayer.create(Animal5.this, R.raw.caballo);
                String mensajeFinal = "Felicidades, llegaste al final.\nTu resultado es: " + DatosGlobales.contador;
                AlertDialog.Builder builder = new AlertDialog.Builder(Animal5.this, R.style.MiEstiloAlerta2);
                builder.setTitle("Resultado Final");
                builder.setMessage(mensajeFinal);
                builder.setCancelable(false);
                builder.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Animal5.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        });
        R3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mp3.start();
                Mp1.stop();
                Mp1.reset();
                Mp1 = MediaPlayer.create(Animal5.this, R.raw.caballo);
//                MiAlerta2.mostrarAlerta(Animal5.this, "Incorrecto", "Has fallado.", Animal5.class);
                String mensajeFinal = "Felicidades, llegaste al final.\nTu resultado es: " + DatosGlobales.contador;
                AlertDialog.Builder builder = new AlertDialog.Builder(Animal5.this, R.style.MiEstiloAlerta2);
                builder.setTitle("Resultado Final");
                builder.setMessage(mensajeFinal);
                builder.setCancelable(false);
                builder.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Animal5.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();
            }
        });
        R4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mp2.start();
                Mp1.stop();
                Mp1.reset();
                Mp1 = MediaPlayer.create(Animal5.this, R.raw.caballo);
                DatosGlobales.contador+=1;
                String mensajeFinal = "Felicidades, llegaste al final.\nTu resultado es: " + DatosGlobales.contador;
                AlertDialog.Builder builder = new AlertDialog.Builder(Animal5.this, R.style.MiEstiloAlerta1);
                builder.setTitle("Resultado Final");
                builder.setMessage(mensajeFinal);
                builder.setCancelable(false);
                builder.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Animal5.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();
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