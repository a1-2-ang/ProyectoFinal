package com.example.auspectuspantallas;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;

public class MiAlerta2 {
    public static void mostrarAlerta(Context context, int estilo, String titulo, String mensaje, Class<?> destino) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, estilo);
        builder.setTitle("Incorrecto");
        builder.setMessage("Has fallado.");

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            Intent intent = new Intent(context, destino);
            context.startActivity(intent);
        });

        builder.create().show();
    }
}
