package com.example.auspectuspantallas;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private Button btn1, btn2, btn3, btn4, btnCerrarSesion, btnGuardarCambios;
    private ImageButton btnUsuario, btnPerfil, btnClose;
    private EditText etNombreUsuario;
    private CardView cardUsuario;

    private DBHelper dbHelper;
    private String usuario;
    private Bitmap nuevaImagenSeleccionada;
    private static final int PICK_IMAGE = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);

        cardUsuario = findViewById(R.id.cardUsuario);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        btnClose = findViewById(R.id.BtnClose);
        btnUsuario = findViewById(R.id.btnUsuario);
        btnPerfil = findViewById(R.id.btnPerfil);
        etNombreUsuario = findViewById(R.id.etNombreUsuario);

        dbHelper = new DBHelper(this);
        SessionManager sesion =
                new SessionManager(this);
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        usuario = prefs.getString("usuario", sesion.getUsuarioNombre());

        etNombreUsuario.setText(usuario);

        byte[] imagenBytes = dbHelper.obtenerImagen(usuario);
        if (imagenBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
            btnUsuario.setImageBitmap(bitmap);
            btnPerfil.setImageBitmap(bitmap);
        } else {
            btnUsuario.setImageResource(R.drawable.usuarios);
            btnPerfil.setImageResource(R.drawable.usuarios);
        }

        btnClose.setOnClickListener(v -> {
            cardUsuario.setVisibility(View.GONE);
        });

        btnUsuario.setOnClickListener(v -> {
            cardUsuario.setVisibility(View.VISIBLE);
        });

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnGuardarCambios.setOnClickListener(v -> {
            String nuevoNombre = etNombreUsuario.getText().toString().trim();
            ContentValues values = new ContentValues();

            if (!nuevoNombre.isEmpty() && !nuevoNombre.equals(usuario)) {
                values.put("usuario", nuevoNombre);
            }

            if (nuevaImagenSeleccionada != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                nuevaImagenSeleccionada.compress(Bitmap.CompressFormat.PNG, 100, stream);
                values.put("imagen", stream.toByteArray());
            }

            if (values.size() > 0) {
                int filas = dbHelper.getWritableDatabase()
                        .update("Usuarios", values, "usuario=?", new String[]{usuario});

                if (filas > 0) {
                    // Actualizar variables y SharedPreferences
                    if (!nuevoNombre.isEmpty()) {
                        usuario = nuevoNombre;
                    }
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("usuario", usuario);
                    editor.apply();

                    if (nuevaImagenSeleccionada != null) {
                        btnUsuario.setImageBitmap(nuevaImagenSeleccionada); // actualizar botón redondo
                    }

                    Toast.makeText(MainActivity.this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "No hay cambios para guardar ", Toast.LENGTH_SHORT).show();
            }
        });


        btnCerrarSesion.setOnClickListener(v -> {
            sesion.cerrarSesion();
            startActivity(new Intent(MainActivity.this, InicioSesion.class));
            finish();
        });


        btn1.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Niveles.class)));
        btn2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, activity_acentos.class)));
        btn3.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, activity_naturaleza.class)));
        btn4.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, activity_objetos.class)));
    }

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
                nuevaImagenSeleccionada = corregirOrientacion(imageUri);

                btnPerfil.setImageBitmap(nuevaImagenSeleccionada);
                btnUsuario.setImageBitmap(nuevaImagenSeleccionada);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
