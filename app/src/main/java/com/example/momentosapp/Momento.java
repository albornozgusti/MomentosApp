package com.example.momentosapp;

import android.media.Image;

import java.io.Serializable;
import java.util.Date;

public class Momento implements Serializable {

    /*normalmente cuando interactuamos con bases de datos, necesitamos un objecto que almacene todos los datos extraidos*/
    private int id;
    private String foto;
    private String descripcion;
    private String fecha;
    private double latitud;
    private double longitud;

    public Momento(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setFoto(String foto){
        this.foto=foto;
    }

    public String getFoto(){
        return this.foto;
    }

    public void setDescripcion(String descripcion){
        this.descripcion=descripcion;
    }

    public String getDescripcion(){
        return this.descripcion;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFecha(){
        return this.fecha;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLongitud() {
        return longitud;
    }
}
