package com.example.auspectuspantallas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Niveles extends AppCompatActivity {
    private Button btn1, btn2, btn3, btn4, btn5;

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
                Intent intent = new Intent(Niveles.this, N2_A1.class);
                startActivity(intent);
            }
        });
    }
}