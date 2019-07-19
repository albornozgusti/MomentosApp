package com.example.momentosapp;

public class Usuario {

    private int id;
    private String nombre;
    private String mail;
    private String password;

    public Usuario(){}

    public void setNombre(String nombre){
        this.nombre=nombre;
    }

    public String getNombre(){
        return this.nombre;
    }

    public void setMail(String mail){
        this.mail=mail;
    }

    public String getMail(){
        return this.mail;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public  String getPassword(){
        return this.password;
    }
}
