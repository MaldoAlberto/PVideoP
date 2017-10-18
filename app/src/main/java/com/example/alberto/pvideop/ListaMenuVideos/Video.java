package com.example.alberto.pvideop.ListaMenuVideos;

import android.graphics.Bitmap;

/**
 * Created by alberto on 4/07/17.
 */
public class Video {

    private String name;
    private Bitmap imagen;
    private String ruta;


    public Video(String name, Bitmap imagen, String ruta) {
        this.imagen = imagen;
        this.name = name;
        this.ruta = ruta;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}