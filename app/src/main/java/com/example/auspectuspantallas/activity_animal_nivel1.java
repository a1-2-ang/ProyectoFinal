package com.example.auspectuspantallas;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class activity_animal_nivel1 extends AppCompatActivity {

    private ImageButton imgBtn;
    private Button btnCon;
    private TextView txtAniJv;
    private MediaPlayer mp;

    // Lista de pares imagen-sonido
    private List<AnimalItem> listaAnimales;
    private int indiceActual = 0; // índice del animal actual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_nivel1);

        imgBtn = findViewById(R.id.imageBtn);
        btnCon = findViewById(R.id.btnContXML);
        txtAniJv = findViewById(R.id.txtArrXML);

        // Inicializar lista de animales con imágenes y sonidos
        listaAnimales = new ArrayList<>();
        listaAnimales.add(new AnimalItem(R.drawable.gato, R.raw.miau, "Gato"));
        listaAnimales.add(new AnimalItem(R.drawable.oveja, R.raw.oveja, "Oveja"));
        listaAnimales.add(new AnimalItem(R.drawable.vaca, R.raw.vaca, "Vaca"));
        listaAnimales.add(new AnimalItem(R.drawable.buho, R.raw.buho, "Buho"));
        listaAnimales.add(new AnimalItem(R.drawable.caballo, R.raw.caballo, "Caballo"));
        listaAnimales.add(new AnimalItem(R.drawable.hamster, R.raw.hamster, "Hamster"));
        listaAnimales.add(new AnimalItem(R.drawable.murcielago, R.raw.murcielago, "Murcielago"));
        listaAnimales.add(new AnimalItem(R.drawable.elefante, R.raw.elefante, "Elefante"));
        listaAnimales.add(new AnimalItem(R.drawable.leon, R.raw.leon, "León"));
        listaAnimales.add(new AnimalItem(R.drawable.delfin, R.raw.delfin, "Delfin"));

        // Mezclar aleatoriamente la lista
        Collections.shuffle(listaAnimales);

        // Mostrar el primer animal
        mostrarAnimal(indiceActual);

        // Al presionar la imagen → reproducir sonido
        imgBtn.setOnClickListener(v -> reproducirSonido());

        // Al presionar continuar → pasar al siguiente animal
        btnCon.setOnClickListener(v -> {
            detenerSonido();
            indiceActual++;
            if (indiceActual < listaAnimales.size()) {
                mostrarAnimal(indiceActual);
            } else {
                // Cuando ya se mostraron todos → ir a otra pantalla
                Intent intent = new Intent(activity_animal_nivel1.this, activity_animal_nivel2.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void mostrarAnimal(int index) {
        AnimalItem animal = listaAnimales.get(index);
        txtAniJv.setText(animal.nombre);
        imgBtn.setImageResource(animal.imagenRes);
        detenerSonido();
        mp = MediaPlayer.create(this, animal.sonidoRes);
    }

    private void reproducirSonido() {
        if (mp != null) {
            mp.start();
        }
    }

    private void detenerSonido() {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            mp = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detenerSonido();
    }

    // Clase interna para agrupar imagen y sonido
    static class AnimalItem {
        int imagenRes;
        int sonidoRes;
        String nombre;

        AnimalItem(int imagenRes, int sonidoRes, String nombre) {
            this.imagenRes = imagenRes;
            this.sonidoRes = sonidoRes;
            this.nombre = nombre;
        }
    }
}