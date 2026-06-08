package com.example.auspectuspantallas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;
//Prueba 1 Monzon
public class InicioSesion extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;

    private LinearLayout layoutLogin, layoutRegistro;
    private EditText EtUsuario, EtContra, EtUsuarioReg, EtContraReg;
    private Button btnIniciarSesion, btnRegistrarInicio, btnRegistar1, btnIS2;
    private ImageButton img1;
    private DBHelper dbHelper;

    private Bitmap imagenSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        dbHelper = new DBHelper(this);

        layoutLogin = findViewById(R.id.layoutLogin);
        layoutRegistro = findViewById(R.id.layoutRegistro);

        EtUsuario = findViewById(R.id.EtUsuario);
        EtContra = findViewById(R.id.EtContra);
        EtUsuarioReg = findViewById(R.id.EtUsuarioReg);
        EtContraReg = findViewById(R.id.EtContraReg);

        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnRegistrarInicio = findViewById(R.id.btnRegistrarInicio);
        btnRegistar1 = findViewById(R.id.btnRegistar1);
        btnIS2 = findViewById(R.id.btnIS2);
        img1 = findViewById(R.id.img1);
        SessionManager sesion =
                new SessionManager(this);
        if(sesion.haySesion()) {
            Intent intent = new Intent(InicioSesion.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        // Cambiar entre login y registro
        btnRegistrarInicio.setOnClickListener(v -> {
            layoutLogin.setVisibility(View.GONE);
            layoutRegistro.setVisibility(View.VISIBLE);
        });

        btnIS2.setOnClickListener(v -> {
            layoutRegistro.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
        });

        // Seleccionar imagen en registro
        img1.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        // Registrar nuevo usuario
        btnRegistar1.setOnClickListener(v -> {
            String usuario = EtUsuarioReg.getText().toString().trim();
            String contrasena = EtContraReg.getText().toString().trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                boolean insertado = dbHelper.insertarUsuario(usuario, contrasena, imagenSeleccionada);
                if (insertado) {
                    Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                    layoutRegistro.setVisibility(View.GONE);
                    layoutLogin.setVisibility(View.VISIBLE);
                    EtUsuarioReg.setText("");
                    EtContraReg.setText("");
                    imagenSeleccionada = null;
                    img1.setImageResource(R.drawable.usuarios);
                } else {
                    Toast.makeText(this, "Error: usuario ya existe", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Iniciar sesión
        btnIniciarSesion.setOnClickListener(v -> {
            String usuario = EtUsuario.getText().toString().trim();
            String contrasena = EtContra.getText().toString().trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Coloca todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                Usuario valido = dbHelper.validarUsuario(usuario, contrasena);
                if (valido != null) {
                    Toast.makeText(this, "Bienvenido " + usuario, Toast.LENGTH_SHORT).show();

                    //la clase Sessionmanager es donde se guarda toda la informacion de la cuenta del usuario

                    sesion.guardarUsuario(
                            valido.getId(),
                            valido.getNombre());

                    // Ir a MainActivity
                    Intent intent = new Intent(InicioSesion.this, MainActivity.class);
                    //intent.putExtra("usuario", usuario);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para corregir orientación de imágenes
    private Bitmap corregirOrientacion(Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

        ExifInterface exif = new ExifInterface(getContentResolver().openInputStream(uri));
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
        }

        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                // Corregir orientación automáticamente
                imagenSeleccionada = corregirOrientacion(imageUri);

                img1.setImageBitmap(imagenSeleccionada);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
