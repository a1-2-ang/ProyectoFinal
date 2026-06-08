package com.example.auspectuspantallas;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF =
            "sesion";

    private static final String ID =
            "usuario_id";

    private static final String NOMBRE =
            "usuario_nombre";

    private SharedPreferences prefs;

    public SessionManager(Context c){

        prefs =
                c.getSharedPreferences(
                        PREF,
                        Context.MODE_PRIVATE);
    }

    public void guardarUsuario(
            int id,
            String nombre){

        prefs.edit()
                .putInt(ID,id)
                .putString(NOMBRE,nombre)
                .apply();
    }

    public int getUsuarioId(){

        return prefs.getInt(ID,-1);
    }

    public String getUsuarioNombre(){

        return prefs.getString(
                NOMBRE,
                "");
    }

    public boolean haySesion(){

        return getUsuarioId()!=-1;
    }

    public void cerrarSesion(){

        prefs.edit().clear().apply();
    }
}