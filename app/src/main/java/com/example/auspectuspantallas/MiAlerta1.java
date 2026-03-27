package com.example.auspectuspantallas;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;

public class MiAlerta1 {
    public static void mostrarAlerta1(Context context, int estilo, String titulo, String mensaje, Class<?> destino) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, estilo);
        builder.setTitle("Correcto");
        builder.setMessage("Has acertado.");

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            Intent intent = new Intent(context, destino);
            context.startActivity(intent);
        });

        builder.create().show();
    }
}
