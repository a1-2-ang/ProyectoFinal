package com.example.auspectuspantallas;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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

public class activity_naturaleza extends AppCompatActivity {
    private Button btnAud1, btnAud2, btnAud3, btnAud4;
    private Button btnNat1, btnNat2, btnNat3, btnNat4;
    private MediaPlayer mp, mpCorrecto, mpIncorrecto;
    private Button primerSeleccion = null, segundaSeleccion = null;
    private int primerValor = -1, segundoValor = -1;
    // Lista de niveles
    private List<NivelNaturaleza> niveles = new ArrayList<>();
    private int nivelActual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naturaleza);

        // Inicializar botones
        btnAud1 = findViewById(R.id.btnAud1);
        btnAud2 = findViewById(R.id.btnAud2);
        btnAud3 = findViewById(R.id.btnAud3);
        btnAud4 = findViewById(R.id.btnAud4);

        btnNat1 = findViewById(R.id.btnNat1);
        btnNat2 = findViewById(R.id.btnNat2);
        btnNat3 = findViewById(R.id.btnNat3);
        btnNat4 = findViewById(R.id.btnNat4);

        // Definir niveles con sonidos y textos
        Map<Integer, Integer> paresNivel1 = new HashMap<>();
        paresNivel1.put(R.raw.miau, R.id.btnNat1);//R.raw.playa
        paresNivel1.put(R.raw.oveja, R.id.btnNat2);//R.raw.bosque
        paresNivel1.put(R.raw.buho, R.id.btnNat3);//R.raw.desierto
        paresNivel1.put(R.raw.vaca, R.id.btnNat4);//R.raw.montana

        mpCorrecto = MediaPlayer.create(this, R.raw.correct);
        mpIncorrecto = MediaPlayer.create(this, R.raw.incorrect);

        String[] textosNivel1 = {"Playa", "Bosque", "Desierto", "Montaña"};

        Map<Integer, Integer> paresNivel2 = new HashMap<>();
        paresNivel2.put(R.raw.caballo, R.id.btnNat1);//R.raw.lluvia
        paresNivel2.put(R.raw.acento_argentino, R.id.btnNat2);//R.raw.trueno
        paresNivel2.put(R.raw.chileno, R.id.btnNat3);//R.raw.viento
        paresNivel2.put(R.raw.miau, R.id.btnNat4);//R.raw.pajaros

        String[] textosNivel2 = {"Lluvia", "Trueno", "Viento", "Pájaros"};

        niveles.add(new NivelNaturaleza(paresNivel1, textosNivel1));
        niveles.add(new NivelNaturaleza(paresNivel2, textosNivel2));

        cargarNivel(nivelActual);
    }

    private void cargarNivel(int index) {
        NivelNaturaleza nivel = niveles.get(index);
        Map<Integer, Integer> pares = nivel.pares;
        String[] textos = nivel.textos;

        // Restaurar botones de audio
        restaurarBoton(btnAud1, "Audio 1");
        restaurarBoton(btnAud2, "Audio 2");
        restaurarBoton(btnAud3, "Audio 3");
        restaurarBoton(btnAud4, "Audio 4");

        // Restaurar botones de naturaleza con textos dinámicos
        restaurarBoton(btnNat1, textos[0]);
        restaurarBoton(btnNat2, textos[1]);
        restaurarBoton(btnNat3, textos[2]);
        restaurarBoton(btnNat4, textos[3]);

        // Asignar listeners de audio
        Integer[] audios = pares.keySet().toArray(new Integer[0]);
        btnAud1.setOnClickListener(v -> manejarSeleccion(btnAud1, audios[0], pares));
        btnAud2.setOnClickListener(v -> manejarSeleccion(btnAud2, audios[1], pares));
        btnAud3.setOnClickListener(v -> manejarSeleccion(btnAud3, audios[2], pares));
        btnAud4.setOnClickListener(v -> manejarSeleccion(btnAud4, audios[3], pares));

        // Asignar listeners de naturaleza
        btnNat1.setOnClickListener(v -> manejarSeleccion(btnNat1, R.id.btnNat1, pares));
        btnNat2.setOnClickListener(v -> manejarSeleccion(btnNat2, R.id.btnNat2, pares));
        btnNat3.setOnClickListener(v -> manejarSeleccion(btnNat3, R.id.btnNat3, pares));
        btnNat4.setOnClickListener(v -> manejarSeleccion(btnNat4, R.id.btnNat4, pares));
    }

    /*private void manejarSeleccion(Button btn, int valor, Map<Integer, Integer> pares) {
        // Si es audio, reproducirlo
        if (pares.containsKey(valor)) {
            mp = MediaPlayer.create(this, valor);
            mp.start();
        }

        if (primerSeleccion == null) {
            primerSeleccion = btn;
            primerValor = valor;
            btn.setBackgroundColor(Color.DKGRAY);
        } else {
            if (pares.containsKey(primerValor) && pares.get(primerValor) == valor) {
                // Acierto
                primerSeleccion.setBackgroundColor(Color.GREEN);
                btn.setBackgroundColor(Color.GREEN);
                primerSeleccion.setEnabled(false);
                btn.setEnabled(false);

                new Handler().postDelayed(() -> {
                    if (primerSeleccion != null) primerSeleccion.setVisibility(View.INVISIBLE);
                    if (btn != null) btn.setVisibility(View.INVISIBLE);
                    verificarFin(pares);
                    // Reiniciar referencias después de procesar ambos
                    primerSeleccion = null;
                    primerValor = -1;
                }, 500);
            } else {
                // Error
                primerSeleccion.setBackgroundColor(Color.RED);
                btn.setBackgroundColor(Color.RED);
                new Handler().postDelayed(() -> {
                    restaurarColor(primerSeleccion);
                    restaurarColor(btn);
                    primerSeleccion = null;
                    primerValor = -1;
                }, 500);
            }
            Toast.makeText(this, "" + pares, Toast.LENGTH_LONG).show();
            primerSeleccion = null;
            primerValor = -1;
        }
    }*/
    private void manejarSeleccion(Button btn, int valor, Map<Integer, Integer> pares) {
        // Si es audio, reproducirlo
        if (pares.containsKey(valor)) {
            mp = MediaPlayer.create(this, valor);
            mp.start();
        }

        if (primerSeleccion == null) {
            // Guardar primera selección
            primerSeleccion = btn;
            primerValor = valor;
            btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.azul_oscuro));
            btn.setEnabled(false);
            //btn.setBackgroundColor(Color.DKGRAY);
        } else if (segundaSeleccion == null) {
            // Guardar segunda selección
            segundaSeleccion = btn;
            segundoValor = valor;
            btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.azul_oscuro));
            //btn.setBackgroundColor(Color.DKGRAY);

            // Ya tenemos dos selecciones → validar
            validarSeleccion(pares);
        }
    }

    private void validarSeleccion(Map<Integer, Integer> pares) {
        if (pares.containsKey(primerValor) && pares.get(primerValor) == segundoValor) {
            // Acierto
            mpCorrecto.start();
            primerSeleccion.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.verde));
            segundaSeleccion.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.verde));
            primerSeleccion.setEnabled(false);
            segundaSeleccion.setEnabled(false);

            new Handler().postDelayed(() -> {
                primerSeleccion.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.azul_bajo));
                segundaSeleccion.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.azul_bajo));
                if (primerSeleccion != null) primerSeleccion.setVisibility(View.INVISIBLE);
                if (segundaSeleccion != null) segundaSeleccion.setVisibility(View.INVISIBLE);
                verificarFin(pares);
                resetSeleccion();
            }, 500);
        } else {
            // Error
            mpIncorrecto.start();
            primerSeleccion.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.rojo));
            segundaSeleccion.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.rojo));

            new Handler().postDelayed(() -> {
                primerSeleccion.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.azul_bajo));
                segundaSeleccion.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.azul_bajo));
                resetSeleccion();
            }, 500);
        }
        Toast.makeText(this, "" + primerSeleccion, Toast.LENGTH_LONG).show();
    }

    private void resetSeleccion() {
        if (primerSeleccion != null && primerSeleccion.isShown()) {
            primerSeleccion.setEnabled(true);
        }

        primerSeleccion = null;
        segundaSeleccion = null;
        primerValor = -1;
        segundoValor = -1;
    }

    private void verificarFin(Map<Integer, Integer> pares) {
        if (!btnAud1.isEnabled() && !btnAud2.isEnabled() && !btnAud3.isEnabled() && !btnAud4.isEnabled()) {
            nivelActual++;
            if (nivelActual < niveles.size()) {
                Toast.makeText(this, "¡Nivel completado! Pasando al siguiente...", Toast.LENGTH_SHORT).show();
                cargarNivel(nivelActual);
            } else {
                Toast.makeText(this, "¡Juego terminado!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        //if (!btnAud1.isShown() && !btnAud2.isShown() && !btnAud3.isShown() && !btnAud4.isShown()) {}
    }

    private void restaurarBoton(Button btn, String texto) {
        btn.setVisibility(View.VISIBLE);
        btn.setEnabled(true);
        btn.setText(texto);
        btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.azul_bajo));
        //restaurarColor(btn);
    }

    private void restaurarColor(Button btn) {
        if (btn != null && btn.isShown()) {//
            btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.azul_bajo));
        }
    }
}

// Clase para cada nivel
class NivelNaturaleza {
    Map<Integer, Integer> pares;
    String[] textos;

    NivelNaturaleza(Map<Integer, Integer> pares, String[] textos) {
        this.pares = pares;
        this.textos = textos;
    }
}