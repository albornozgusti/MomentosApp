package com.example.momentosapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;




public class DetalleFotoFragment extends Fragment  {

    /*fragment_detalle_foto es el layout implementado en este fragment
    * al reemplazarse el fragment vacio se ejecuta esta clase Java que esta vinculada
    * con el layout
    * */

    private ImageView imagen;
    private TextView detalleTv;
    private TextView fecha;
    private TextView latitud;
    private TextView longitud;
    private Button opciones;

    private Momento momento;

    private MiListener activityCallback;//listener propio
    /*implementacion no posible de momento
    private MapView mapView;
    private GoogleMap googleMap;*/




    public interface MiListener{//realizamos una interface listener para poder llamar a funciones de la actividad
        //todas estas funciones aparecen si o si en la implementacion correspondiente
        void onOpcionClicked(int i, Momento momento);
        void eliminarMomento(int i);
        void descargarFoto(Uri uri);
        void descargaTerminada();
    }

    //constructor vacio necesario para ejecutar metodos que no seria posible de otra forma
    //ya intente de otras maneras y no se pudo
    public DetalleFotoFragment(){}

    public interface OnFragmentInteractionListener {//NO USADO
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAttach(Context activity){
        super.onAttach(activity);
        /*Esta en la teoria esto, al activity que recibimos como parametro, podemos comunicarnos mediante las funciones de la interface
        * primero obtenemos la actividad, y luego la asociamos a la variable
        * activityCallback declarada al principio que es de tipo MiListener
        * */

        Activity a;

        try{
            a = (Activity) activity;
            activityCallback = (MiListener) a;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+" debe implementar Milistener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
    ViewGroup container, Bundle saverInstance){

        View view = inflater.inflate(R.layout.fragment_detalle_foto,container,false);
        //asociamos
        imagen = (ImageView)view.findViewById(R.id.iv_imagen_detalle);
        detalleTv = (TextView)view.findViewById(R.id.et_descripcion_detalle);
        fecha = (TextView)view.findViewById(R.id.tv_fecha_detalle);
        latitud = (TextView)view.findViewById(R.id.tv_latitud_detalle);
        longitud = (TextView)view.findViewById(R.id.tv_longitud_detalle);
        opciones = (Button)view.findViewById(R.id.btn_opciones_fragment);



        /*implementacion no posible de momento
        mapView = (MapView)view.findViewById(R.id.mapView);
        mapView.getMapAsync(this);*/

        mostrarDetalleFoto(this.momento);

        opciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //menu contextual ofreciendo, editar detalle, borrar momento o descargar la foto
                cargarOpciones();

            }
        });

        view.setBackgroundColor(Color.WHITE);//color de fondo del background
        //seteamos el tamaño del fragment para que no se muestre lo de atras
        //el fragment en si no es muy grande por lo cual es necesario agrandarlo para
        //que tape toda la lista y no sea posible hacer tap a traves del fragment
        view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

        imagen.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cargarOpcionesLongClickFoto();
                return true;
            }
        });

        return view;
    }

    private void mostrarDetalleFoto(Momento momento){//obtenemos los datos del momento recibido y los ponemos en cada coimponente del fragment
        imagen.setImageURI(Uri.parse(momento.getFoto()));
        detalleTv.setText(momento.getDescripcion());
        fecha.setText(momento.getFecha());
        latitud.setText(Double.toString(momento.getLatitud()));
        longitud.setText(Double.toString(momento.getLongitud()));

    }

    public void cargarEnMomento(Momento momentoIn){//usada cuando se inicializa el fragment, debido a que no carga las vistas, primero lo guardamos en una variable y luego ejecutamos el mostrarDetalleFoto()
        momento = new Momento(momentoIn.getId());
        momento.setFoto(momentoIn.getFoto());
        momento.setDescripcion(momentoIn.getDescripcion());
        momento.setFecha(momentoIn.getFecha());
        momento.setLatitud(momentoIn.getLatitud());
        momento.setLongitud(momentoIn.getLongitud());
    }

    public void cargarOpciones(){
        //MENU CONTEXTUAL MOSTRANDO LAS OPCIONES DISPONIBLES

        final CharSequence[] opciones = {"Editar Detalle", "Descargar Imagen", "Eliminar Momento", "Cancelar"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(getView().getContext());
        alertOpciones.setTitle("Elija una opcion");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Editar Detalle")){
                    //iniciar actividad crear momento, enviando los detalles del momento actual
                    //callback
                    editarDetalle(3, momento);

                    dialogInterface.dismiss();
                }else if (opciones[i].equals("Descargar imagen")){
                    //inicia una descarga de la foto
                    descargarFoto(Uri.parse(momento.getFoto()));

                    dialogInterface.dismiss();

                }else if (opciones[i].equals("Eliminar Momento")){
                    //elimina el momento de la base de datos y finaliza este fragment
                    eliminarMomento(momento.getId());
                    //dialogInterface.dismiss();
                } else if (opciones[i].equals("Cancelar")){
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    public void cargarOpcionesLongClickFoto(){
        //MENU CONTEXTUAL EN CASO DE QUE EL USUARIO MANTENGA EL TAP SOBRE LA FOTO
        final CharSequence[] opciones = {"Descargar Imagen","Cancelar"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(getView().getContext());
        alertOpciones.setTitle("Elija una opcion");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Descargar imagen")){
                    //inicia una descarga de la foto
                    descargarFoto(Uri.parse(momento.getFoto()));
                    dialogInterface.dismiss();
                } else if (opciones[i].equals("Cancelar")){
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    //ESTAS 3 FUNCIONES SE ENCARGAN DE COMUNICAR CON LA ACTIVITY
    //PARA EJECUTAR LAS FUNCIONES QUE CORRESPONDAN A LA ACTIVITY PRINCIPAL
    private void descargarFoto(Uri uri) {
        activityCallback.descargaTerminada();
    }

    private void eliminarMomento(int id) {
        activityCallback.eliminarMomento(id);
    }

    public void editarDetalle(int i, Momento momento){
        //notifico a la activity para cambiar de actividad y editar el detalle
        activityCallback.onOpcionClicked(i, momento);
    }

    /*implementacion no posible de momento
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/

}
