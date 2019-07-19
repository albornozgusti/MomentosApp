package com.example.momentosapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainMomentos extends AppCompatActivity implements DetalleFotoFragment.MiListener {

    GridView gvMomentos;

    SQLiteDatabase db;
    ArrayList<Momento> listaMomentos;
    ArrayList<String> listaInformacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences(MainActivity.NOMBRE_PREFERENCIAS,MODE_PRIVATE);
        /*al ser el punto de arranque, debemos comprobar si se logueo por lo menos
        * si en las preferencias devuelve nulo, procedemos a iniciar la actividad del login
        * y luego del startactivity finalizamos esta activity por si presiona el backbutton
        * */
        if (pref.getString(MainActivity.NOMBRE_USUARIO,"n").equals("n")) {
            //SI EL USUARIO QUE PEDIMOS NO ESTA ALMACENADO EN LA SHARED PREFERENCES DEVUELVE SOLO "N"
            //EN ESE CASO, EJECUTAMOS LA ACTIVITY DEL LOGIN
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);

            finish();
        }


        setContentView(R.layout.activity_main_momentos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        gvMomentos = (GridView)findViewById(R.id.gv_main);

        //Esto sirve para que el boton back desde el toolbar no se muestre
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        //consultamos los momentos almacenados en la base de datos
        consultarDb();

        gvMomentos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //accion que levanta el fragment para mostrar detalle de la foto

                DetalleFotoFragment detalle = new DetalleFotoFragment();
                detalle.setArguments(getIntent().getExtras());

                //Toast.makeText(getApplicationContext(),"el id del momento es: "+String.valueOf(listaMomentos.get(i).getId()),Toast.LENGTH_SHORT).show();
                //commiteamos el fragment
                //mismo commit que activity de la busqueda
                /*primero reemplazamos el fragment vacio por el fragment del detalle
                * agregamos que se peda volver con el boton back
                * y por ultimo hacemos que se muestre el nuevo detalle*/
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_load_detalle,detalle)
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();

                /*a su vez, mostramos el boton back en el toolbar*/
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

                //el commit es asincronico y no puedo llamar para mostrar la foto y detalles en pantalla
                //por lo cual primero guardo en la instancia los datos necesarios, y luego en un metodo privado interno lo cargo
                detalle.cargarEnMomento(listaMomentos.get(i));
            }
        });


        gvMomentos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int posicion, long l) {
                final CharSequence[] opciones = {"Descargar Imagen", "Cancelar"};
                final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(MainMomentos.this);
                alertOpciones.setTitle("Elija una opcion");
                alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (opciones[i].equals("Descargar Imagen")){
                            //descargar imagen
                            descargarFoto(Uri.parse(listaMomentos.get(posicion).getFoto()));
                            descargaTerminada();
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

        //aprovechamos el boton verde que aparece en los templates como un sobre de correo
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ACCION USADA PARA SACAR FOTO DESDE LA CAMARA
                cargarImagen();
            }
        });
    }

    @Override
    protected void onResume() {//se usa esto para actualizar la base de datos agregando el momento subido recientemente
        super.onResume();
        //eliminamos el boton back del toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        //consultamos y actualizamos la vista del gridview
        consultarDb();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//menu principal del toolbar
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    private void cargarImagen(){//menu contextual para elejir la opcion de galeria o sacar foto con la camara del celular

        final CharSequence[] opciones = {"Tomar Foto", "Cargar Imagen", "Cancelar"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(MainMomentos.this);
        alertOpciones.setTitle("Elija una opcion");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")){
                    //iniciar actividad activity_crear_momento con bundle
                    goToCrearMomentoActivity(1);
                }else if (opciones[i].equals("Cargar Imagen")){
                    //iniciar actividad activity_crear_momento con bundle
                    goToCrearMomentoActivity(2);

                }else {
                 dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }


    public void goToCrearMomentoActivity(int i){

        //este "i" es la opcion recibida por paremtro
        //lo almacenamos en un bundle y lo enviamos a la siguiente actividad
        //esto sirve para crear momento o ejecutar la funcion que sigue que seria para editar el momento elejido
        Intent intent = new Intent(getApplicationContext(),CrearMomentoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("opcion",i);
        intent.putExtra("opcion",i);
        startActivity(intent);
    }

    public void goToCrearMomentoActivity(int i, Momento momento){
        //este "i" es la opcion
        //idem, hicimos la funcion polimorfica cosa de que se ejecute este en caso de que quisieramos editar
        Intent intent = new Intent(getApplicationContext(),CrearMomentoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("opcion",i);
        intent.putExtra("opcion",i);
        intent.putExtra("momento",momento);
        startActivity(intent);
    }


    public void consultarDb(){
            /*realiza las consultas en la base d e datos con los momentos guardados
            * y los muestra*/
            db = new SqlLite(getApplicationContext()).open();

            Momento momento = null;
            listaMomentos = new ArrayList<Momento>();
            listaInformacion = new ArrayList<String>();

            Cursor cursor = db.rawQuery("SELECT * FROM MOMENTO",null);

            while (cursor.moveToNext()){
                momento = new Momento(cursor.getInt(0));
                momento.setFoto(cursor.getString(1));
                momento.setDescripcion(cursor.getString(2));
                momento.setFecha(cursor.getString(3));
                momento.setLatitud(cursor.getDouble(4));
                momento.setLongitud(cursor.getDouble(5));

                listaMomentos.add(momento);
            }
            //obtenerLista lo usamos para que solo muestre pocos detalles del momento
            obtenerLista();
            //el adaptador se usa para ejecutar el ImagenAdapter que creamos
            //normalmente andorid studio viene ocn adaptadores por defecto pero ninguno sirve
        //por lo cual necesitamos crear nuestro propio adaptador
            ejecutarAdaptador();


    }

    private void obtenerLista() {
        listaInformacion = new ArrayList<String>();

        for(int i=0; i<listaMomentos.size();i++){
            listaInformacion.add(listaMomentos.get(i).getFecha()+" - "
                    +listaMomentos.get(i).getDescripcion());
        }
    }

    public void ejecutarAdaptador(){
        //ArrayAdapter adaptador = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listaInformacion);
        //gvMomentos.setAdapter(adaptador);

        gvMomentos.setAdapter(new ImagenAdapter(this, listaMomentos));
    }

    //FUNCION IMPLEMENTADA DEL LISTENER
    @Override
    public void onOpcionClicked(int i, Momento momento) {
        //PASAMOS A LA ACTIVIDAD CORRESPONDIENTE

        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment_load_detalle)).commit();
        goToCrearMomentoActivity(i,momento);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

    }

    @Override
    public void eliminarMomento(int i) {
        //acciones ejecutadas para eliminar momento obteniendo solo el ID del momento
        db = new SqlLite(getApplicationContext()).open();
        db.delete("MOMENTO","ID="+i,null);
        Toast.makeText(getApplicationContext(),db.getPath(),Toast.LENGTH_SHORT).show();
        db.close();
        //eliminamos el fragment volviendo a la lista
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment_load_detalle)).commit();
        //eliminamos el boton back del toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        //Toast.makeText(getApplicationContext(),"se borro el momento",Toast.LENGTH_SHORT).show();
        consultarDb();

    }

    @Override
    public void descargarFoto(Uri uri) {
        //LA DESCARGA PUEDE SER SIMULADA MEDIANTE LA COPIA DE LA IMAGEN ALMACENADA Y ALMACENARLA
        //EN LA CARPETA DOWNLOADS, UNA VEZ EL PROCESO ESTA HECHO SE LE NOTIFICA AL USUARIO
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        descargaTerminada();

        /*
        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        //seteo titulo del request
        request.setTitle("Data download");
        //seteo descripcion
        request.setDescription("descargado mediante downloadmanager");
        //seteo ubicacion
        request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS,"momento.jpg");
        //enqueue download
        long downloadreference = downloadManager.enqueue(request);
        */


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //ejecutado las opciones del toolbar
        switch (item.getItemId()){
            case R.id.action_buscar:
                Intent intent = new Intent(getApplicationContext(),BusquedaActivity.class);
                startActivity(intent);
                //ejecutamos activity para buscar
                break;
            case R.id.action_settings:
                //ejecutamos acitivy con settings persistentes
                intent = new Intent(getApplicationContext(),PreferencesActivity.class);
                startActivity(intent);
                break;
            case R.id.action_cerrar_sesion:
                SharedPreferences getPref = getSharedPreferences(MainActivity.NOMBRE_PREFERENCIAS,MODE_PRIVATE);
                SharedPreferences.Editor edit = getPref.edit();
                edit.putString(MainActivity.NOMBRE_USUARIO,"n");
                edit.apply();
                intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
                //volvemos a al pantalla del login
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        onBackPressed();
        Toast.makeText(getApplicationContext(),"presionaste el backbutton",Toast.LENGTH_SHORT);
        return true;
    }

    @Override
    public void descargaTerminada(){
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
}
