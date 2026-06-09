package com.example.auspectuspantallas;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.IOException;
//Prueba 1 Monzon
public class InicioSesion extends AppCompatActivity {
    private LinearLayout layoutLogin, layoutRegistro;
    private EditText EtUsuario, EtContra, EtUsuarioReg, EtContraReg;
    private Button btnIniciarSesion, btnRegistrarInicio, btnRegistar1, btnIS2;
    private ImageButton img1;
    private DBHelper dbHelper;
    private Uri fotoUri;
    private Bitmap imagenSeleccionada;

    private static final int PICK_IMAGE = 100;
    private static final int TAKE_PHOTO = 200;
    private static final int REQUEST_PERMISSIONS = 300;

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

        verificarPermisos();

        // Seleccionar imagen en registro
        img1.setOnClickListener(v -> {
            String[] opciones = {"Tomar foto", "Elegir de galería"};

            new android.app.AlertDialog.Builder(InicioSesion.this)
                    .setTitle("Seleccionar imagen")
                    .setItems(opciones, (dialog, which) -> {
                        if (which == 0) {
                            // Tomar foto con cámara
                            abrirCamara();
                        } else {
                            // Elegir de galería
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, PICK_IMAGE);
                        }
                    })
                    .show();
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

                    SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("usuario", valido.getNombre());
                    editor.apply();

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

    private void verificarPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
        }
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

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Crear archivo temporal en almacenamiento interno de la app
        File fotoArchivo = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "foto_temp.jpg");

        try {
            if (!fotoArchivo.exists()) {
                fotoArchivo.createNewFile();
            }

            // Obtener URI con FileProvider
            fotoUri = FileProvider.getUriForFile(
                    this,
                    "com.example.auspectuspantallas.fileprovider", // 👈 usa tu packageName
                    fotoArchivo
            );

            // Pasar URI a la cámara
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, TAKE_PHOTO);
            } else {
                Toast.makeText(this, "No hay app de cámara disponible", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creando archivo para foto", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null) {
                // Imagen desde galería
                Uri imageUri = data.getData();
                try {
                    imagenSeleccionada = corregirOrientacion(imageUri);
                    img1.setImageBitmap(imagenSeleccionada);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == TAKE_PHOTO) {
                // Foto tomada con cámara
                if (fotoUri != null) {
                    try {
                        imagenSeleccionada = corregirOrientacion(fotoUri);
                        img1.setImageBitmap(imagenSeleccionada);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al cargar foto", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}