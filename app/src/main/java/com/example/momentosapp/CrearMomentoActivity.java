package com.example.momentosapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CrearMomentoActivity extends AppCompatActivity implements Serializable {

    /*Actividad que ejecuta el layout Activity_crear_momento

    * */
    //paths para las carpetas a guardar las fotos que uno saca de la camara
    private final String CARPETA_RAIZ="MisImagenesPrueba";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"MisFotos";

    final int COD_SELECCIONA=10;//codigo para seleccionar de la galeria
    final int COD_FOTO=20;//codigo para sacar foto

    private LocationManager locationManager = null;
    private MyLocationListener locationListener = null;

    Date date;
    String path;

    ImageView imagen;
    EditText etDescripcion;
    TextView tvFecha;
    TextView tvLatitud;
    TextView tvLongitud;
    Button btnSubir;

    Momento momento;

    int opcionseleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_momento);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //getActionBar().setTitle("Subir momento");
        //obtenemos la opcion del bundle


        final int opcion;
        Bundle bundle = getIntent().getExtras();
        opcion = bundle.getInt("opcion");

        if (opcion == 3) {//este se ejecuta si la actividad anterior se elijió para editar el momento seleccionado
            //como pase los datos del Momento por el Bundle, lo primero que hago al ejecutar la actividad es obtener los datos para luego mostrar los datos
            momento = (Momento) bundle.getSerializable("momento");
            //toolbar.setTitle("Editar Momento");
            setTitle("Editar Momento");
        }else {
            //toolbar.setTitle("Crear Momento");
            setTitle("Crear Momento");
        }



        //obtengo el locationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //asociamos elementos
        imagen = (ImageView)findViewById(R.id.iv_foto);
        etDescripcion = (EditText)findViewById(R.id.et_descripcion);
        tvFecha = (TextView) findViewById(R.id.tv_fecha);
        tvLatitud = (TextView)findViewById(R.id.tv_latitud);
        tvLongitud = (TextView)findViewById(R.id.tv_longitud);
        btnSubir = (Button)findViewById(R.id.btn_publicar);

        //ARRANCAMOS EL GPS PARA HACERLO LO MAS PRECISO POSIBLE
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,locationListener);

        } else {
            Toast.makeText(getApplicationContext(),"GPS no habilitado!",Toast.LENGTH_SHORT).show();
        }

        //switch para ejecutar la opcion obtenida
        switch (opcion){
            case 1:
                //toma foto usando la camara del dispositivo
                opcionseleccionada = opcion;
                tomarFoto();
                break;
            case 2:
                cargarImagen();
                opcionseleccionada = opcion;
                break;
            case 3:
                //editar detalle
                opcionseleccionada = opcion;
                momento = (Momento) bundle.getSerializable("momento");
                getIntentMomento();
                break;
        }

        //registramos el listener para almacenar en la base de datos
        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CODIGO PARA SUBIR LA FOTO Y DETALLES A LA BASE DE DATOS

                /*DEJAMOS ESTO DE EJEMPLO PARA ALMACENAR UNA FOTO EN UNA BASE DE DATOS, ES COMPLETAMENTE INEFICIENTE PERO POSIBLE
                //obtenemos el bitmap de la imagen
                Bitmap bitmap = ((BitmapDrawable)imagen.getDrawable()).getBitmap();
                //lo transformamos en un array de bytes
                byte[] imagenByte = DbBitMapUtility.getBytes(bitmap);*/


                String foto = path;//path de la foto almacenada en el dispositivo
                //obtenemos los datos (descripcion, fecha, latitud y longitud)
                String descripcion = etDescripcion.getText().toString();
                String fecha = tvFecha.getText().toString();
                String latitud = tvLatitud.getText().toString();
                String longitud = tvLongitud.getText().toString();

                //el registro es para almacenar en la base de datos
                //creamos el registro clave valor
                ContentValues registro = new ContentValues();
                registro.put("FOTO",foto);
                registro.put("DESCRIPCION",descripcion);
                registro.put("FECHA",fecha);
                registro.put("LATITUD",latitud);
                registro.put("LONGITUD",longitud);
                //foto, descripcion, fecha, latitud y longitud


                //APLICAR CAMBIOS NECESARIOS PARA HACER MODIFICACIONES ADEMAS DE CARGAR FOTOS NUEVAS


                SqlLite bd = new SqlLite(getApplicationContext());
                if (opcion == 3) {//ejecutado si queremos editar el momento
                    bd.open().update("MOMENTO",registro,"ID="+momento.getId()+"",null);
                    Toast.makeText(getApplicationContext(),"Se modificó el momento exitosamente",Toast.LENGTH_SHORT).show();
                } else {//ejecutado si registramos un nuevo momento
                    bd.open().insert("MOMENTO", null, registro);
                    Toast.makeText(getBaseContext(),"Se ha registrado el Momento",Toast.LENGTH_SHORT).show();
                }
                bd.close();

                //notificamos el registro exitoso

                //finalizamos la actividad actual de registro
                finish();


            }
        });
    }




    private void tomarFoto(){//requerido para usar la camara del celular

        /*/
        *
        * mucho no puedo explicar aqui, fue mas un copy paste debido al apuro que tenia en temrinar la aplicacion
        * */
        File fileImagen = new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada = fileImagen.exists();
        String nombreImagen="";

        if (!isCreada){
            isCreada=fileImagen.mkdirs();
        }
        if (isCreada){
            nombreImagen = (System.currentTimeMillis()/1000+".jpg");
        }

        path=Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombreImagen;

        File imagen = new File(path);

        //intent que llama a la camara
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        startActivityForResult(intent,COD_FOTO);

    }


    private void cargarImagen(){
        //intent llamando a la galeria
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent,"Seleccione la aplicacion"),COD_SELECCIONA);
    }


    private void obtenerDatos(){
        //FUNCION QUE OBTIENE FECHA LATITUD Y LONGITUD A LA HORA DE CARGAR LA FOTO
        //Y LAS UBICA EN LOS TEXTVIEWS RESPECTIVOS
        tvFecha.setText(obtenerFecha());
        //Toast.makeText(getApplicationContext(),String.valueOf(locationListener.getLatitude()),Toast.LENGTH_LONG).show();
        if (locationListener.getLatitude()==0.0) {

            Toast.makeText(getApplicationContext(),"no obtuve ubicacion",Toast.LENGTH_LONG).show();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, locationListener);
            }
        }
        tvLatitud.setText(locationListener.getStringlatitude());
        tvLongitud.setText(locationListener.getStringLongitude());
        locationManager.removeUpdates(locationListener);//removemos el update para desactivar el servicio
    }

    private String obtenerFecha() {
        //funcion que obtiene la fecha del sistema y retorna en forma de string
        date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyy");
        return df.format(date);
    }

    @Override//resultado de la actividad de sacar la foto o elejirla de la galeria
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        //AQUI ES DONDE USAMOS LAS CONSTANTES DECLARADAS AL PRINCIPIO, NORMALMENTE SE USAN EN STARTACTIVITYFORRESULT()
        if(resultCode==RESULT_OK){
            switch (requestCode) {
                case COD_SELECCIONA:
                    Uri miPath = data.getData();
                    imagen.setImageURI(miPath);
                    path = miPath.toString();
                    break;
                case COD_FOTO:
                    MediaScannerConnection.scanFile(this, new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String s, Uri uri) {
                                    Log.i("Ruta de almacenamiento","Path: "+path);
                                }
                            });
                    //ESPECIAL ATENCION A ESTO YA QUE TRANSFORMA EL ARCHIVO A LA IMAGEN
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    //escalo la imagen para que la ejecucion no sea tan lenta
                    Bitmap reducido = Bitmap.createScaledBitmap(bitmap,300,300,false);
                    imagen.setImageBitmap(reducido);
                    bitmap.recycle();//liberamos la memoria

                    break;
            }
        }
        obtenerDatos();//obtenemos los datos de fecha latitud y longitud
    }

    public void getIntentMomento(){//ubicamos en el layout el momento a editar o capturar
        imagen.setImageURI(Uri.parse(momento.getFoto()));
        path = momento.getFoto();
        etDescripcion.setText(momento.getDescripcion());
        tvFecha.setText(momento.getFecha());
        tvLatitud.setText((String.valueOf(momento.getLatitud())));
        tvLongitud.setText((String.valueOf(momento.getLongitud())));

    }






    //LOCATION LISTENER DEL EJEMPLO DESARROLLADO EN CLASE
    private class MyLocationListener implements LocationListener {

        private double longitude, latitude;
        private String slongitude, slatitude;

        @Override
        public void onLocationChanged(Location location) {
            this.longitude = location.getLongitude();
            this.latitude = location.getLatitude();
            this.slatitude = String.valueOf(location.getLatitude());
            this.slongitude = String.valueOf(location.getLongitude());
            //muestro las coordenadas

        }

        public double getLongitude(){return this.longitude;}

        public double getLatitude() {return latitude;}

        public String getStringLongitude(){return this.slongitude;}

        public String getStringlatitude(){return this.slatitude;}

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}

        @Override
        public void onProviderEnabled(String s) {}

        @Override
        public void onProviderDisabled(String s) {}

    }

    //ESTA FUNCION NO HAGAS CASO, LO DEJE DE EJEMPLO PARA VER SI SE PODIA ROTAR LA IMAGEN
    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }


    }
    private static Bitmap rotateImage(Bitmap img, int degree) {//NO SIRVE
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    @Override
    public boolean onSupportNavigateUp() {//EJECUTADO CUANDO PRESIONAS EL BOTON BACK
        onBackPressed();
        Toast.makeText(getApplicationContext(),"presionaste el backbutton",Toast.LENGTH_SHORT);
        return true;
    }

}
