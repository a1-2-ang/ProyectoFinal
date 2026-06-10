package com.example.auspectuspantallas;

import static com.example.auspectuspantallas.DatosGlobales.voz;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private Button btn1, btn2, btn3, btn4, btnCerrarSesion, btnGuardarCambios, btnEliminarCuenta;
    private ImageButton btnUsuario, btnPerfil, btnClose;
    private EditText etNombreUsuario, txtPntAni, txtPntAce, txtPntNat, txtPntObj, txtPntTot;
    private CardView cardUsuario;
    private LinearLayout linLayPnts;
    private DBHelper dbHelper;
    private String usuario;
    private Bitmap nuevaImagenSeleccionada;
    private Uri fotoUri;
    private static final int REQ_CODE_SPEECH_INPUT = 400;
    private TextToSpeech tts;
    private Switch switchMain;
    private static final int PICK_IMAGE = 100;
    private static final int TAKE_PHOTO = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);

        switchMain = findViewById(R.id.switchMain);

        cardUsuario = findViewById(R.id.cardUsuario);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);

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

        if (voz) {
            // Inicializar TTS
            tts = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(new Locale("es", "ES"));
                    speakInstruction();
                }
            });
            switchMain.setChecked(true);
        }

        switchMain.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // El interruptor está encendido
                voz = true;
                tts = new TextToSpeech(this, status -> {
                    if (status == TextToSpeech.SUCCESS) {
                        tts.setLanguage(new Locale("es", "ES"));
                        speakInstruction();
                    }
                });
            } else {
                // El interruptor está apagado
                voz = false;
            }
        });


        byte[] imagenBytes = dbHelper.obtenerImagen(sesion.getUsuarioId());
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
            String[] opciones = {"Tomar foto", "Elegir de galería"};

            new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("Seleccionar imagen")
                    .setItems(opciones, (dialog, which) -> {
                        if (which == 0) {
                            // Usa el mismo flujo de cámara que en InicioSesion
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File fotoArchivo = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "perfil_temp.jpg");
                            fotoUri = FileProvider.getUriForFile(this,
                                    "com.example.auspectuspantallas.fileprovider",
                                    fotoArchivo);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                            startActivityForResult(intent, TAKE_PHOTO);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, PICK_IMAGE);
                        }
                    })
                    .show();
        });

        btnGuardarCambios.setOnClickListener(v -> {
            String nuevoNombre = etNombreUsuario.getText().toString().trim();
            ContentValues values = new ContentValues();

            if (!nuevoNombre.isEmpty() && !nuevoNombre.equals(usuario)) {
                values.put("usuario", nuevoNombre);
            }

            if (nuevaImagenSeleccionada != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                nuevaImagenSeleccionada.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                values.put("imagen", stream.toByteArray());
            }

            if (values.size() > 0) {
                int filas = dbHelper.getWritableDatabase()
                        .update("Usuarios", values, "id=?", new String[]{String.valueOf(sesion.getUsuarioId())});

                if (filas > 0) {
                    // Actualizar variables y SharedPreferences
                    if (!nuevoNombre.isEmpty()) {
                        usuario = nuevoNombre;
                    }
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("usuario", usuario);
                    editor.apply();

                    if (nuevaImagenSeleccionada != null) {
                        btnUsuario.setImageBitmap(nuevaImagenSeleccionada);
                        btnPerfil.setImageBitmap(nuevaImagenSeleccionada);
                    }

                    Toast.makeText(MainActivity.this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "No hay cambios para guardar", Toast.LENGTH_SHORT).show();
            }
        });


        btnCerrarSesion.setOnClickListener(v -> {
            sesion.cerrarSesion();
            startActivity(new Intent(MainActivity.this, InicioSesion.class));
            finish();
        });

        btnEliminarCuenta.setOnClickListener(v -> {
            // Crear objeto Usuario con el id actual
            Usuario usuarioActual = new Usuario();
            usuarioActual.setId(sesion.getUsuarioId());

            // Eliminar usuario de la BD
            int filas = dbHelper.getWritableDatabase()
                    .delete("Usuarios", "id=?", new String[]{String.valueOf(usuarioActual.getId())});

            if (filas > 0) {
                // Cerrar sesión y limpiar preferencias
                sesion.cerrarSesion();
                prefs.edit().clear().apply(); // 👈 aquí ya usas el prefs que declaraste arriba

                Toast.makeText(MainActivity.this, "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show();

                // Volver a InicioSesion
                startActivity(new Intent(MainActivity.this, InicioSesion.class));
                finish();
            } else {
                Toast.makeText(MainActivity.this, "No se pudo eliminar la cuenta", Toast.LENGTH_SHORT).show();
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, activity_animal_nivel1.class));
                finish();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, activity_acentos.class));
                finish();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, activity_naturaleza.class));
                finish();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, activity_objetos.class));
                finish();
            }
        });
        //btn1.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, activity_animal_nivel1.class)));
        //btn2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, activity_acentos.class)));
        //btn3.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, activity_naturaleza.class)));
        //btn4.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, activity_objetos.class)));
    }

    private void speakInstruction() {
        String mensaje = "Menú de navegación, por favor diga cual de los siguientes menus quiere abrir, 'animales'. 'acentos'. 'naturaleza'. 'objetos'.";
        tts.speak(mensaje, TextToSpeech.QUEUE_FLUSH, null, null);

        btnUsuario.postDelayed(this::startVoiceInput, 6000);
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di tu comando...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            tts.speak("Tu dispositivo no soporta reconocimiento de voz", TextToSpeech.QUEUE_FLUSH, null, null);
            Toast.makeText(this, "Tu dispositivo no soporta reconocimiento de voz", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = result.get(0);
            if (spokenText.equalsIgnoreCase("animales")) {
                btn1.performClick();
                //startActivity(new Intent(MainActivity.this, MainActivity2.class));
            } else if (spokenText.equalsIgnoreCase("acentos")) {
                btn2.performClick();
                //tts.speak("Se ha iniciado sesión correctamente", TextToSpeech.QUEUE_FLUSH, null, null);
            } else if (spokenText.equalsIgnoreCase("naturaleza")) {
                btn3.performClick();
                //tts.speak("Se ha iniciado sesión correctamente", TextToSpeech.QUEUE_FLUSH, null, null);
            } else if (spokenText.equalsIgnoreCase("objetos")) {
                btn4.performClick();
                //tts.speak("Se ha iniciado sesión correctamente", TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                tts.speak("Palabra no reconocida, intente nuevamente", TextToSpeech.QUEUE_FLUSH, null, null);
                startVoiceInput();
            }

        }
        else if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null) {
                // Imagen desde galería
                Uri imageUri = data.getData();
                try {
                    nuevaImagenSeleccionada = corregirOrientacion(imageUri);
                    btnPerfil.setImageBitmap(nuevaImagenSeleccionada);
                    btnUsuario.setImageBitmap(nuevaImagenSeleccionada);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == TAKE_PHOTO) {
                // Foto tomada con cámara
                if (fotoUri != null) {
                    try {
                        nuevaImagenSeleccionada = corregirOrientacion(fotoUri);
                        btnPerfil.setImageBitmap(nuevaImagenSeleccionada);
                        btnUsuario.setImageBitmap(nuevaImagenSeleccionada);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al cargar foto", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}