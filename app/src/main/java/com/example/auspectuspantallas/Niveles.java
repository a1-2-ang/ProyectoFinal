package com.example.auspectuspantallas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Niveles extends AppCompatActivity {
    private Button btn1, btn2, btn3, btn4, btn5;
    private ImageButton imgBtnSal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_niveles);
        btn1 = findViewById(R.id.btnN1);
        btn2 = findViewById(R.id.btnN2);
        btn3 = findViewById(R.id.btnN3);
        btn4 = findViewById(R.id.btnN4);
        btn5 = findViewById(R.id.btnN5);
        imgBtnSal = findViewById(R.id.imgBtnSalir);

        imgBtnSal.setOnClickListener(v -> finish());
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Niveles.this, N1_A1.class);
                startActivity(intent);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Niveles.this, activity_animal_nivel2.class);//N2_A1
                startActivity(intent);
            }
        });
    }
}