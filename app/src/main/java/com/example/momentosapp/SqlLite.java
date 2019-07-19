package com.example.momentosapp;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlLite extends SQLiteOpenHelper {

    private static  final String DATABASE_NAME="MomentosDB";
    private static  final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_USUARIO = "CREATE TABLE USUARIO"+
            "("+
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "MAIL TEXT NOT NULL,"+
                    "PASSWORD TEXT NOT NULL"+");";

    //RECORDAR QUE FALTA RELACIONAR MOMENTO CON USUARIOS
    private static final String DATABASE_CREATE_MOMENTO =
            "CREATE TABLE MOMENTO"+
            "("+
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+                              /* id de momento */
                    //+RESERVADO PARA LA RELACION                              /* id de usuario */
                    "FOTO TEXT NOT NULL,"+                              /* foto del momento */
                    "DESCRIPCION TEXT NOT NULL,"+
                    "FECHA TEXT NOT NULL,"+
                    "LATITUD FLOAT(20) NOT NULL,"+
                    "LONGITUD FLOAT(10) NOT NULL"+
       ");";

    private SQLiteDatabase bd;


    public SqlLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            //CREAMOS 2 TABLAS
            db.execSQL(DATABASE_CREATE_USUARIO);
            db.execSQL(DATABASE_CREATE_MOMENTO);
        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS USUARIO");
        db.execSQL("DROP TABLE IF EXISTS MOMENTO");
        onCreate(db);
    }

    public SQLiteDatabase open(){
        bd = this.getWritableDatabase();

        return bd;
    }

    public void close(){
        bd.close();
    }
}
