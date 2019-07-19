package com.example.momentosapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.widget.SearchView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class BusquedaActivity extends AppCompatActivity implements DetalleFotoFragment.MiListener {

    /*Actividad del layout de busqueda*/


    SearchView svBusqueda;
    CharSequence queryMain;
    GridView gvResultados;

    SQLiteDatabase db;
    ArrayList<Momento> listaMomentos;
    ArrayList<String> listaInformacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        gvResultados = (GridView)findViewById(R.id.gv_resultado_busqueda);

        gvResultados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //accion que levanta el fragment para mostrar detalle de la foto
                //esto estaba en la teoria, simplemente fue reemplazar por lo mio
                DetalleFotoFragment detalle = new DetalleFotoFragment();
                detalle.setArguments(getIntent().getExtras());

                /*como tenemos el nuevo fragment, lo siguiente que hago abajo es reemplazar un componente "vacio"
                Fijate en el layout que hay un FrameLayout como fragment que no tiene nada, lo cual no tiene nada que mostrar
                a ese lo reemplazo por otro layout que se llama fragment_load_detalle, si buscas ese xml en el layout ahi tenes
                lo necesario para mostrar el detalle
                Los pasos que hace las lineas de abajo es asi:
                -Saco el fragment vacio (del layout vacio)
                -meto el fragment nuevo llamado fragment_load_detalle
                -addToBackStack lo que hace es agregarlo a la pila cuando se presiona el boton de retroceder del celular
                -finalmente el commit carga el fragment que se puso mostrandolo en pantalla
                * */

                //commiteamos el fragment

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_load_detalle,detalle)
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                //el commit es asincronico y no puedo llamar para mostrar la foto y detalles en pantalla
                //por lo cual primero guardo en la instancia los datos necesarios, y luego en un metodo privado interno lo cargo
                detalle.cargarEnMomento(listaMomentos.get(i));
            }
        });

        //Funcion para los taps que se mantienen
        gvResultados.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int posicion, long l) {
                //Configuro para el menu contextual
                final CharSequence[] opciones = {"Descargar Imagen", "Cancelar"};
                final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(BusquedaActivity.this);

                alertOpciones.setTitle("Elija una opcion");
                alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (opciones[i].equals("Descargar Imagen")){
                            //descargar imagen
                            //deberia ejecutarse la funcion para descargar la imagen
                            //debido a que no lo implemente directamente muestro la notificacion cosa que aca no esta implementado

                            dialogInterface.dismiss();
                        }else if (opciones[i].equals("Cancelar")){
                            dialogInterface.dismiss();
                        }
                    }
                });
                alertOpciones.show();
                return true;
            }
        });

        //estas funciones son necesarias para que se muestre la barra de busqueda en la pantalla a lo largo del ancho
        svBusqueda = (SearchView)findViewById(R.id.sv_busqueda);
        svBusqueda.setFocusable(true);
        svBusqueda.setIconified(false);
        svBusqueda.requestFocus();
        //svBusqueda.requestFocusFromTouch();
        svBusqueda.setSubmitButtonEnabled(true);

        //seteamos para que cuando se pulse el boton Buscar ya sea del teclado o de la derecha del texto de busqueda
        svBusqueda.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //realiza la busqueda de hastags en la base de datos, luego mostrando los resultados del query
                queryMain = svBusqueda.getQuery();
                Toast.makeText(getApplicationContext(),"ejecutando consulta "+queryMain,Toast.LENGTH_LONG).show();
                consultarDb();
            }
        });

        //realiza la busqueda de hastags en la base de daots, luego mostrando los resultados del query
        svBusqueda.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //el querymain lo guardo debido a que cuando ejecuto la consulta es un tipo CharSecuence
                queryMain = query;
                consultarDb();
                return true;
            }

            @Override//Esta funcion se puso sola, no le des bola
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });


    }



    public void consultarDb(){

        db = new SqlLite(getApplicationContext()).open();
        //se ejecuta la query de la base de datos, no creo que haya dudas aca salvo...
        Momento momento = null;
        listaMomentos = new ArrayList<Momento>();//este arraylist guarda todos los datos de los momentos
        listaInformacion = new ArrayList<String>();//esto lo uso para mostrar pocos detalles en la lista
        String consulta = "SELECT * FROM MOMENTO WHERE "+
                "(DESCRIPCION LIKE '%#"+queryMain+"%')";
        //Toast.makeText(getApplicationContext(),"ejecutando consulta "+query,Toast.LENGTH_LONG).show();
        Cursor cursor = db.rawQuery(consulta,null);

        while (cursor.moveToNext()){//toda la consulta devuelta la recorro una a una y la guardo en una nueva instancia de variable Momento
            momento = new Momento(cursor.getInt(0));
            momento.setFoto(cursor.getString(1));
            momento.setDescripcion(cursor.getString(2));
            momento.setFecha(cursor.getString(3));
            momento.setLatitud(cursor.getDouble(4));
            momento.setLongitud(cursor.getDouble(5));

            listaMomentos.add(momento);//aqui agrego al Arraylist la instancia creada
        }

        obtenerLista();
        ejecutarAdaptador();


    }

    private void obtenerLista() {
        listaInformacion = new ArrayList<String>();
        //instancio la lista que obtendra la informacion de los momentos pero solo agarra la imagen (el path de la imagen), y la fecha.
        for(int i=0; i<listaMomentos.size();i++){
            listaInformacion.add(listaMomentos.get(i).getFecha()+" - "
                    +listaMomentos.get(i).getDescripcion());
        }
    }

    public void ejecutarAdaptador(){
        //ejecuto un adaptador propio para poder mostrar en el gridview la imagen y la fecha
        gvResultados.setAdapter(new ImagenAdapter(this, listaMomentos));
    }

    @Override//AL PARECER EN ESTA ACTIVIDAD NO USO ESTA FUNCION ASI QUE DE MOMENTO NO HAGAS CASO A ESTA FUNCION
    public void onOpcionClicked(int i, Momento momento) {
        //esta funcion se ejecuta cuando hago tap en una de las fotos de la lista devuelta, el objetivo es crear un detalle
        //con los datos del Momento almacenado en el ArrayList
        //PASAMOS A LA ACTIVIDAD CORRESPONDIENTE
        //esto esta en la teoria si mal no recuerdo, basicamente
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment_load_detalle)).commit();
        goToCrearMomentoActivity(i,momento);
    }

    @Override
    public void eliminarMomento(int i) {
        db = new SqlLite(getApplicationContext()).open();
        db.delete("MOMENTO","ID="+i,null);

        Toast.makeText(getApplicationContext(),db.getPath(),Toast.LENGTH_SHORT).show();

        db.close();
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment_load_detalle)).commit();
        //Toast.makeText(getApplicationContext(),"se borro el momento",Toast.LENGTH_SHORT).show();
        consultarDb();
    }

    @Override
    public void descargarFoto(Uri uri) {
        //lo unico que hace es mostrar una notificacion, la descarga no la pude simular lamentablemente
    descargaTerminada();
    }

    @Override
    public void descargaTerminada() {
        NotificationCompat.Builder notificacion =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Descarga completa")
                        .setContentText("Se ha descargado la foto")
                        .setAutoCancel(true)
                        .setCategory(NotificationCompat.CATEGORY_EVENT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationManager.IMPORTANCE_DEFAULT,notificacion.build());
    }

    public void goToCrearMomentoActivity(int i, Momento momento){//si bien dice crear, tambien lo uso para editar los Momentos ya subidos, lo demas es de la teoria
        //este "i" es la opcion
        Intent intent = new Intent(getApplicationContext(),CrearMomentoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("opcion",i);
        intent.putExtra("opcion",i);
        intent.putExtra("momento",momento);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Toast.makeText(getApplicationContext(),"presionaste el backbutton",Toast.LENGTH_SHORT);
        return true;
    }
}
