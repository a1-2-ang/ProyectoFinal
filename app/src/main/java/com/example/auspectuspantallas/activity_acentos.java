package com.example.auspectuspantallas;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class activity_acentos extends AppCompatActivity {
        private MediaPlayer mpPregunta, mpCorrecto, mpIncorrecto;
        private ProgressBar barraProgreso;
        private Handler handler = new Handler();
        private Runnable runnable;
        private Button btnVer;
        private EditText etAcento;
        private ImageButton btnPlay;

        private List<EjercicioAcento> ejercicios;
        private int indiceActual = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_acentos);

            // Inicializar UI
            btnVer = findViewById(R.id.btnVerf);
            etAcento = findViewById(R.id.ETAcento);
            barraProgreso = findViewById(R.id.progressBar1);
            btnPlay = findViewById(R.id.imgBtn1);

            etAcento.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

            // Inicializar sonidos de feedback
            mpCorrecto = MediaPlayer.create(this, R.raw.correct);
            mpIncorrecto = MediaPlayer.create(this, R.raw.incorrect);

            // Lista de ejercicios (audio + respuesta esperada)
            ejercicios = Arrays.asList(
                    new EjercicioAcento(R.raw.acento_argentino, "ARGENTINO"),//1
                    new EjercicioAcento(R.raw.chileno, "CHILENO"),//2
                    new EjercicioAcento(R.raw.peruano, "PERUANO"),//3
                    new EjercicioAcento(R.raw.cubano, "CUBANO"),//acento_cubano //4
                    new EjercicioAcento(R.raw.castellano,"CASTELLANO"),//5
                    new EjercicioAcento(R.raw.colombiano,"COLOMBIANO"),//6
                    new EjercicioAcento(R.raw.brasil,"BRASILEÑO"),//7
                    new EjercicioAcento(R.raw.venezolano,"VENEZOLANO"), //8
                    new EjercicioAcento(R.raw.mexicano,"MEXICANO"),  //9
                    new EjercicioAcento(R.raw.puerto_rico,"PUERTO RIQUEÑO")
            );

            // Mostrar primer ejercicio
            mostrarEjercicio(indiceActual);

            // Botón reproducir audio
            btnPlay.setOnClickListener(v -> {
                if (mpPregunta != null) {
                    if (mpPregunta.isPlaying()) {
                        mpPregunta.pause();
                        handler.removeCallbacks(runnable);
                    } else {
                        mpPregunta.start();
                        iniciarBarra();
                    }
                }
            });

            // Botón verificar respuesta
            btnVer.setOnClickListener(v -> validarRespuesta());
        }

        private void mostrarEjercicio(int index) {
            EjercicioAcento e = ejercicios.get(index);

            // Reiniciar MediaPlayer
            if (mpPregunta != null) {
                mpPregunta.release();
            }
            mpPregunta = MediaPlayer.create(this, e.audioResId);

            // Configurar barra de progreso
            mpPregunta.setOnPreparedListener(mediaPlayer -> {
                barraProgreso.setMax(mpPregunta.getDuration());
                barraProgreso.setProgress(0);
            });
            mpPregunta.setOnCompletionListener(mediaPlayer -> {
                barraProgreso.setProgress(0);
                handler.removeCallbacks(runnable);
            });

            // Limpiar campo de texto
            etAcento.setText("");
        }

        private void validarRespuesta() {
            EjercicioAcento e = ejercicios.get(indiceActual);
            String respuestaUsuario = etAcento.getText().toString().trim();

            if (respuestaUsuario.isEmpty()) {
                Toast.makeText(this, "Coloca el acento", Toast.LENGTH_SHORT).show();
                etAcento.requestFocus();
                return;
            }

            if (respuestaUsuario.equals(e.respuestaCorrecta)) {
                mpCorrecto.start();
                DatosGlobales.contador++;
                Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show();
                if (indiceActual < ejercicios.size() - 1) {
                    Alertas.mostrarAlerta1(activity_acentos.this, R.style.MiEstiloAlerta1,"Correcto", "Has acertado.");
                }
            } else {
                mpIncorrecto.start();
                Toast.makeText(this, "Incorrecto", Toast.LENGTH_SHORT).show();
                if (indiceActual < ejercicios.size() - 1) {
                    Alertas.mostrarAlerta2(activity_acentos.this, R.style.MiEstiloAlerta2,"Incorrecto", "Has fallado.");
                }
            }

            // Avanzar al siguiente ejercicio
            indiceActual++;
            if (indiceActual < ejercicios.size()) {
                mostrarEjercicio(indiceActual);
            } else {
                String mensajeFinal = "Felicidades, llegaste al final.\nTu resultado es: " + DatosGlobales.contador;
                if (DatosGlobales.contador == 3) {
                    mensajeFinal += "\n¡Puntuación perfecta!";
                } else if (DatosGlobales.contador == 0) {
                    mensajeFinal += "\nMás suerte la proxima";
                }
                DatosGlobales.contador = 0;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity_acentos.this, R.style.MiEstiloAlerta3);
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

    // Clase modelo para cada ejercicio
    class EjercicioAcento {
        int audioResId;
        String respuestaCorrecta;

        public EjercicioAcento(int audioResId, String respuestaCorrecta) {
            this.audioResId = audioResId;
            this.respuestaCorrecta = respuestaCorrecta;
        }
    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_acentos);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
