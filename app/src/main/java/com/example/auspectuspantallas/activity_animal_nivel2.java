package com.example.auspectuspantallas;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class activity_animal_nivel2 extends AppCompatActivity {

    private MediaPlayer mpPregunta, mpCorrecto, mpIncorrecto;
    private ProgressBar barraProgreso;
    private Handler handler = new Handler();
    private Runnable runnable;
    private Button btn1, btn2, btn3, btn4;
    private ImageButton btnPlay;

    private List<Pregunta> preguntas;
    private int indiceActual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_nivel2);

        // Inicializar UI
        btn1 = findViewById(R.id.btnResp1);
        btn2 = findViewById(R.id.btnResp2);
        btn3 = findViewById(R.id.btnResp3);
        btn4 = findViewById(R.id.btnResp4);
        btnPlay = findViewById(R.id.imgBtn);
        barraProgreso = findViewById(R.id.progressBar1);

        // Inicializar sonidos de feedback
        mpCorrecto = MediaPlayer.create(this, R.raw.correct);
        mpIncorrecto = MediaPlayer.create(this, R.raw.incorrect);

        // Crear lista de preguntas
        preguntas = Arrays.asList(
                new Pregunta(R.raw.miau, new String[]{"Gato", "Chita", "Guepardo", "León"}, 0),
                new Pregunta(R.raw.oveja, new String[]{"Chivo", "Cordero", "Oveja", "Cabra"}, 2),
                new Pregunta(R.raw.vaca, new String[]{"Buffalo", "Vaca", "Toro", "Bisonte"}, 1),
                new Pregunta(R.raw.buho, new String[]{"Buho", "Lechuza", "Zanate", "Cuervo"}, 0),
                new Pregunta(R.raw.caballo, new String[]{"Burro", "Cebra", "Mula", "Caballo"}, 3)
                // Agrega más preguntas aquí
        );

        // Mostrar primera pregunta
        mostrarPregunta(indiceActual);

        // Listeners de botones de respuesta
        btn1.setOnClickListener(v -> validarRespuesta(0));
        btn2.setOnClickListener(v -> validarRespuesta(1));
        btn3.setOnClickListener(v -> validarRespuesta(2));
        btn4.setOnClickListener(v -> validarRespuesta(3));

        // Botón de reproducir audio
        btnPlay.setOnClickListener(v -> {
            if (mpPregunta != null) {
                mpPregunta.start();
                iniciarBarra();
            }
        });
    }

    private void mostrarPregunta(int index) {
        Pregunta p = preguntas.get(index);

        // Reiniciar MediaPlayer de la pregunta
        if (mpPregunta != null) {
            mpPregunta.release();
        }
        mpPregunta = MediaPlayer.create(this, p.audioResId);

        // Configurar barra de progreso
        mpPregunta.setOnPreparedListener(mediaPlayer -> {
            barraProgreso.setMax(mpPregunta.getDuration());
            barraProgreso.setProgress(0);
        });
        mpPregunta.setOnCompletionListener(mediaPlayer -> {
            barraProgreso.setProgress(0);
            handler.removeCallbacks(runnable);
        });

        // Configurar texto de botones
        btn1.setText(p.opciones[0]);
        btn2.setText(p.opciones[1]);
        btn3.setText(p.opciones[2]);
        btn4.setText(p.opciones[3]);
    }

    private void validarRespuesta(int seleccion) {
        Pregunta p = preguntas.get(indiceActual);

        if (seleccion == p.respuestaCorrecta) {
            mpCorrecto.start();
            DatosGlobales.contador++;
            if (indiceActual < preguntas.size() - 1) {
                Alertas.mostrarAlerta1(activity_animal_nivel2.this, R.style.MiEstiloAlerta1,"Correcto", "Has acertado.");
            }
        } else {
            mpIncorrecto.start();
            if (indiceActual < preguntas.size() - 1) {
                Alertas.mostrarAlerta2(activity_animal_nivel2.this, R.style.MiEstiloAlerta2,"Incorrecto", "Has fallado.");
            }
        }

        // Avanzar a la siguiente pregunta
        indiceActual++;
        if (indiceActual < preguntas.size()) {
            mostrarPregunta(indiceActual);
        } else {
            // Aquí podrías lanzar otra Activity con resultados finales
            String mensajeFinal = "Felicidades, llegaste al final.\nTu resultado es: " + DatosGlobales.contador;
            if (DatosGlobales.contador == 5) {
                mensajeFinal += "\n¡Puntuación perfecta!";
            } else if (DatosGlobales.contador == 0) {
                mensajeFinal += "\nMás suerte la proxima";
            }
            DatosGlobales.contador = 0;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity_animal_nivel2.this, R.style.MiEstiloAlerta3);
            builder.setTitle("Resultado Final");
            builder.setMessage(mensajeFinal);
            builder.setCancelable(false);
            builder.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        }
    }

    private void iniciarBarra() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (mpPregunta != null && mpPregunta.isPlaying()) {
                    barraProgreso.setProgress(mpPregunta.getCurrentPosition());
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mpPregunta != null) {
            mpPregunta.release();
            mpPregunta = null;
        }
        handler.removeCallbacks(runnable);
    }
}

// Clase modelo de pregunta
class Pregunta {
    int audioResId;
    String[] opciones;
    int respuestaCorrecta;

    public Pregunta(int audioResId, String[] opciones, int respuestaCorrecta) {
        this.audioResId = audioResId;
        this.opciones = opciones;
        this.respuestaCorrecta = respuestaCorrecta;
    }
}
