package com.example.momentosapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

public class ImagenAdapter extends BaseAdapter {

    ImageView imagen;
    TextView titulo;

    private Context contexto;
    private ArrayList<Momento> momentos;

    public ImagenAdapter(Context context, ArrayList<Momento> momentos){
        this.contexto=context;
        this.momentos=momentos;
    }

    @Override
    public int getCount() {
        return momentos.size();
    }//DEVUELVE LA CANTIDAD DE ITEMS QUE HAY EN EL ARRAYLIST

    @Override
    public Object getItem(int i) {
        return momentos.get(i);
    }//RETORNA EL OBTETO DEL ARRAYLIST

    @Override
    public long getItemId(int i) {
        return 0;
    }//RETORNA EL ID DEL ARRAYLIST, COMO RETORNA 0 CREO QUE NO ESTA SIENDO USADA

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //ESTO SE LLAMA AL EJECUTAR EL ADAPTADOR EN LA ACTIVIDAD QUE CONTIENE LA GRIDVIEW
        LayoutInflater inflater =(LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null){
            view = inflater.inflate(R.layout.fragment_item__view_,null);
        }

        titulo = (TextView) view.findViewById(R.id.item_tv_fecha);
        imagen = (ImageView)view.findViewById(R.id.item_iv_foto);

        titulo.setText(momentos.get(i).getFecha());

        //ESCALAMOS LA FOTO PARA QUE NO SE HAGA LENTO LA APP
        Bitmap bitmap = BitmapFactory.decodeFile(momentos.get(i).getFoto());
        Bitmap escalado = Bitmap.createScaledBitmap(bitmap,100,100,false);

        imagen.setImageBitmap(escalado);

        bitmap.recycle();
        //escalado.recycle();



        return view;
    }
}
