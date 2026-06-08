package com.example.auspectuspantallas;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class Alertas {
    public static void mostrarAlerta1(Context context, int estilo, String titulo, String mensaje) {//REMOVIDO , Class<?> destino
        AlertDialog.Builder builder = new AlertDialog.Builder(context, estilo);
        builder.setTitle("Correcto");
        builder.setMessage("Has acertado.");

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            //Intent intent = new Intent(context, destino);
            //context.startActivity(intent);
        });

        builder.create().show();
    }
    public static void mostrarAlerta2(Context context, int estilo, String titulo, String mensaje) { //REMOVIDO , Class<?> destino
        AlertDialog.Builder builder = new AlertDialog.Builder(context, estilo);
        builder.setTitle("Incorrecto");
        builder.setMessage("Has fallado.");

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            //Intent intent = new Intent(context, destino);
            //context.startActivity(intent);
        });

        builder.create().show();
    }
    public static void mostrarAlerta3(Context context, int estilo, String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, estilo);
        builder.setTitle("Incorrecto");
        builder.setMessage("Has fallado.");

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            //Intent intent = new Intent(context, destino);
            //context.startActivity(intent);
        });

        builder.create().show();
    }
}
