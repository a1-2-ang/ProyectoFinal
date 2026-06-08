package com.example.auspectuspantallas;

import android.graphics.Bitmap;

public class Usuario {

    private int id;
    private String nombre;
    private String password;
    private byte[] Imagen;

    public Usuario(){}

    public Usuario(
            int id,
            String nombre,
            String password,
            byte[] Imagen){

        this.id=id;
        this.nombre=nombre;
        this.password=password;
        this.Imagen=Imagen;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre){
        this.nombre=nombre;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password=password;
    }
    public byte[] getImagen(){
        return Imagen;
    }

    public void setImagen(byte[] imagen){
        this.Imagen=imagen;
    }
}