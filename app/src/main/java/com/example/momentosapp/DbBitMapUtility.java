package com.example.momentosapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class DbBitMapUtility {
    //ESTA CLASE NO SIRVE, ASI QUE NO HAGAS CASO A ESTA

    //DEJAMOS ESTA CLASE COMO MUESTRA DE TRANSFORMACION DE IMAGENES A BYTES


    //transforma la imagen en un array de bytes
    public static byte[] getBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,0,stream);
        return  stream.toByteArray();
    }

    //transforma un array de bytes a una imagen
    public static Bitmap getImagen(byte[] imagen){
        return BitmapFactory.decodeByteArray(imagen,0,imagen.length);
    }
}
