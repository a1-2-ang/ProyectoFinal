package com.example.auspectuspantallas;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//CHECAR PORQUE AL TERMINAR LA PANTALLA QUEDA EN BLANCO Y LAS IMAGENES NO SE VEN

//activity_objetos
public class activity_objetos extends AppCompatActivity {
    private ImageButton imgBtnAud1, imgBtnAud2, imgBtnAud3, imgBtnAud4;
    private Button btnObj1, btnObj2, btnObj3, btnObj4;
    private MediaPlayer mp, mpCorrecto, mpIncorrecto;
    private View primerSeleccion = null, segundaSeleccion = null;
    private int primerValor = -1, segundoValor = -1;

    // Lista de niveles
    private List<NivelObjetos> niveles = new ArrayList<>();
    private int nivelActual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objetos);

        // Inicializar botones
        imgBtnAud1 = findViewById(R.id.imgBtnAud1);
        imgBtnAud2 = findViewById(R.id.imgBtnAud2);
        imgBtnAud3 = findViewById(R.id.imgBtnAud3);
        imgBtnAud4 = findViewById(R.id.imgBtnAud4);

        btnObj1 = findViewById(R.id.btnObj1);
        btnObj2 = findViewById(R.id.btnObj2);
        btnObj3 = findViewById(R.id.btnObj3);
        btnObj4 = findViewById(R.id.btnObj4);

        mpCorrecto = MediaPlayer.create(this, R.raw.correct);
        mpIncorrecto = MediaPlayer.create(this, R.raw.incorrect);

        // Definir niveles con sonidos y textos
        Map<Integer, Integer> paresNivel1 = new HashMap<>();
        paresNivel1.put(R.raw.auto, R.id.btnObj1);
        paresNivel1.put(R.raw.trompeta, R.id.btnObj4);
        paresNivel1.put(R.raw.ventilador, R.id.btnObj3);
        paresNivel1.put(R.raw.agua, R.id.btnObj2);

        String[] textosNivel1 = {"Ventilador", "Agua", "Auto", "Trompeta"};
        int[] imagenesNivel1 = {R.drawable.auto, R.drawable.trompeta, R.drawable.ventilador, R.drawable.agua};

        Map<Integer, Integer> paresNivel2 = new HashMap<>();
        paresNivel2.put(R.raw.telefono, R.id.btnObj4);
        paresNivel2.put(R.raw.puerta, R.id.btnObj3);
        paresNivel2.put(R.raw.bicicleta, R.id.btnObj1);
        paresNivel2.put(R.raw.fuego, R.id.btnObj2);

        String[] textosNivel2 = {"Bicicleta", "Fuego", "Puerta", "Telefono"};
        int[] imagenesNivel2 = {R.drawable.telefono, R.drawable.puerta, R.drawable.fuego, R.drawable.bicicleta};

        niveles.add(new NivelObjetos(paresNivel1, textosNivel1, imagenesNivel1));
        niveles.add(new NivelObjetos(paresNivel2, textosNivel2, imagenesNivel2));

        cargarNivel(nivelActual);
    }

    private void cargarNivel(int index) {
        NivelObjetos nivel = niveles.get(index);
        Map<Integer, Integer> pares = nivel.pares;
        String[] textos = nivel.textos;
        int[] imagenes = nivel.imagenes;

        // Restaurar botones de audio (ImageButton)
        restaurarImageButton(imgBtnAud1, imagenes[0]);
        restaurarImageButton(imgBtnAud2, imagenes[1]);
        restaurarImageButton(imgBtnAud3, imagenes[2]);
        restaurarImageButton(imgBtnAud4, imagenes[3]);

        // Restaurar botones de objetos (Button)
        restaurarBoton(btnObj1, textos[0]);
        restaurarBoton(btnObj2, textos[1]);
        restaurarBoton(btnObj3, textos[2]);
        restaurarBoton(btnObj4, textos[3]);

        // Asignar listeners de audio
        Integer[] audios = pares.keySet().toArray(new Integer[0]);
        imgBtnAud1.setOnClickListener(v -> manejarSeleccion(imgBtnAud1, audios[0], pares));
        imgBtnAud2.setOnClickListener(v -> manejarSeleccion(imgBtnAud2, audios[1], pares));
        imgBtnAud3.setOnClickListener(v -> manejarSeleccion(imgBtnAud3, audios[2], pares));
        imgBtnAud4.setOnClickListener(v -> manejarSeleccion(imgBtnAud4, audios[3], pares));

        // Asignar listeners de objetos
        btnObj1.setOnClickListener(v -> manejarSeleccion(btnObj1, R.id.btnObj1, pares));
        btnObj2.setOnClickListener(v -> manejarSeleccion(btnObj2, R.id.btnObj2, pares));
        btnObj3.setOnClickListener(v -> manejarSeleccion(btnObj3, R.id.btnObj3, pares));
        btnObj4.setOnClickListener(v -> manejarSeleccion(btnObj4, R.id.btnObj4, pares));
    }

    private void manejarSeleccion(View btn, int valor, Map<Integer, Integer> pares) {
        // Si es audio, reproducirlo
        if (pares.containsKey(valor)) {
            mp = MediaPlayer.create(this, valor);
            mp.setOnCompletionListener(mediaPlayer -> {
                mediaPlayer.release();
                mp = null;
            });
            mp.start();
            /*mp = MediaPlayer.create(this, valor);
            mp.start();*/
        }

        if (primerSeleccion == null) {
            // Primera selección debe ser un ImageButton
            if (btn instanceof ImageButton) {
                primerSeleccion = btn;
                primerValor = valor;
                marcarSeleccion(btn, R.color.img_selec);
                imgBtnAud1.setEnabled(false);
                imgBtnAud2.setEnabled(false);
                imgBtnAud3.setEnabled(false);
                imgBtnAud4.setEnabled(false);
            } else {
                Toast.makeText(this, "Primero selecciona un botón de audio", Toast.LENGTH_SHORT).show();
            }
            //marcarSeleccion(btn, R.color.azul_oscuro);
        } else if (segundaSeleccion == null) {
            if (btn instanceof Button) {
                segundaSeleccion = btn;
                segundoValor = valor;
                marcarSeleccion(btn, R.color.img_selec);
                validarSeleccion(pares);
                imgBtnAud1.setEnabled(true);
                imgBtnAud2.setEnabled(true);
                imgBtnAud3.setEnabled(true);
                imgBtnAud4.setEnabled(true);
            } else {
                Toast.makeText(this, "Después selecciona un botón de objeto", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void validarSeleccion(Map<Integer, Integer> pares) {
        if (pares.containsKey(primerValor) && pares.get(primerValor) == segundoValor) {
            //Acierto
            mpCorrecto.start();
            marcarSeleccion(primerSeleccion, R.color.verde_img);
            marcarSeleccion(segundaSeleccion, R.color.verde);

            new Handler().postDelayed(() -> {
                if (mp != null && mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                    mp = null;
                }
                primerSeleccion.setVisibility(View.INVISIBLE);
                segundaSeleccion.setVisibility(View.INVISIBLE);
                verificarFin(pares);
                resetSeleccion();
            }, 500);
        } else {
            //Error
            mpIncorrecto.start();
            marcarSeleccion(primerSeleccion, R.color.rojo_img);
            marcarSeleccion(segundaSeleccion, R.color.rojo);

            new Handler().postDelayed(() -> {
                restaurarColor(primerSeleccion);
                restaurarColor(segundaSeleccion);
                resetSeleccion();
            }, 500);
        }
    }

    private void marcarSeleccion(View v, int colorRes) {
        if (v instanceof Button) {
            ((Button) v).setBackgroundTintList(ContextCompat.getColorStateList(this, colorRes));
        } else if (v instanceof ImageButton) {
            ((ImageButton) v).setColorFilter(ContextCompat.getColor(this, colorRes));
        }
    }

    private void restaurarColor(View v) {
        if (v instanceof Button) {
            ((Button) v).setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.amarillo));
        } else if (v instanceof ImageButton) {
            ((ImageButton) v).setColorFilter(ContextCompat.getColor(this, R.color.trans_parente));
        }
    }

    private void resetSeleccion() {
        primerSeleccion = null;
        segundaSeleccion = null;
        primerValor = -1;
        segundoValor = -1;
    }

    private void verificarFin(Map<Integer, Integer> pares) {
        if (!imgBtnAud1.isShown() && !imgBtnAud2.isShown() && !imgBtnAud3.isShown() && !imgBtnAud4.isShown()) {
            nivelActual++;
            if (nivelActual < niveles.size()) {
                Toast.makeText(this, "¡Nivel completado! Pasando al siguiente...", Toast.LENGTH_SHORT).show();
                cargarNivel(nivelActual);
            } else {
                Toast.makeText(this, "¡Juego terminado!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void restaurarBoton(Button btn, String texto) {
        btn.setVisibility(View.VISIBLE);
        btn.setEnabled(true);
        btn.setText(texto);
        btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.amarillo));
    }

    private void restaurarImageButton(ImageButton btn, int recursoImagen) {
        btn.setVisibility(View.VISIBLE);
        btn.setEnabled(true);
        btn.setColorFilter(ContextCompat.getColor(this, R.color.trans_parente));
        btn.setImageResource(recursoImagen);
    }
}

// Clase para cada nivel
class NivelObjetos {
    Map<Integer, Integer> pares;
    String[] textos;
    int[] imagenes;

    NivelObjetos(Map<Integer, Integer> pares, String[] textos, int[] imagenes) {
        this.pares = pares;
        this.textos = textos;
        this.imagenes = imagenes;
    }
}