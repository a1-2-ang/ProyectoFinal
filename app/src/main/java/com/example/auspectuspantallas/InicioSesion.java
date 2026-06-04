package com.example.auspectuspantallas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InicioSesion extends AppCompatActivity {

    private Button BtnInicio, BtnRegistrame, BtnInicioReg, BtnRegistrarReg;

    private EditText EtUsuario, EtContra, EtUsuarioreg, EtContraReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_sesion);
        BtnInicio = findViewById(R.id.btnIniciarSesion);
        BtnRegistrame = findViewById(R.id.btnRegistrarInicio);


    }

    @Override
    protected void on
}