package com.example.auspectuspantallas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dbapp1.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario TEXT UNIQUE NOT NULL," +
                "contrasena TEXT NOT NULL," +
                "imagen BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
        onCreate(db);
    }

    public boolean insertarUsuario(String usuario, String contrasena, Bitmap imagenBitmap) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("usuario", usuario);
        values.put("contrasena", contrasena);

        if (imagenBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagenBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            values.put("imagen", stream.toByteArray());
        }

        long result = db.insert("Usuarios", null, values);
        db.close();
        return result != -1;
    }

    public Usuario validarUsuario(String usuario, String contrasena) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Usuarios WHERE usuario=? AND contrasena=?",
                new String[]{usuario, contrasena});
        Usuario usuarioData = null;

        if(c.moveToFirst()){

            usuarioData = new Usuario();

            usuarioData.setId(
                    c.getInt(
                            c.getColumnIndexOrThrow("id")));

            usuarioData.setNombre(
                    c.getString(
                            c.getColumnIndexOrThrow("usuario")));

            usuarioData.setPassword(
                    c.getString(
                            c.getColumnIndexOrThrow("contrasena")));

            byte[] imgBytes = c.getBlob(c.getColumnIndexOrThrow("imagen"));
           if (imgBytes != null){
               usuarioData.setImagen(imgBytes);
           }
            c.close();
            return usuarioData;
        }
        c.close();
        return null;
    }

    public byte[] obtenerImagen(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT imagen FROM Usuarios WHERE id=?",
                new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            byte[] imagen = cursor.getBlob(0);
            cursor.close();
            db.close();
            return imagen;
        }
        cursor.close();
        db.close();
        return null;
    }
}