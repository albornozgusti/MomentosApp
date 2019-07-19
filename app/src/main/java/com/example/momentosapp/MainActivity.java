package com.example.momentosapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText et_usuario;
    private EditText et_password;
    private Button btn_login;
    private Button btn_registrar;
    SQLiteDatabase db;

    private Button btn_verUsuario;

    //VARIABLES FINALES USADAS PARA LAS SHAREDPREFERENCES
    public static final String NOMBRE_PREFERENCIAS = "preferenciasUsuario";
    public static final String NOMBRE_USUARIO = "usuario";
    SharedPreferences login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Momentos  - Login");

        login = getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);

        if (!login.getString(NOMBRE_USUARIO,"n").equals("n")){
            //SI HAY NOMBRE DE USUARIO DEFINIDO, DIRECTAMENTE EJECUTA LA ACTIVIDAD DE MAINMOMENTOS
            //SALTANDO EL LOGIN
            Intent intent = new Intent(getApplicationContext(),MainMomentos.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //en caso de que el usuario presione back, al volver aqui, finalizamos la actividad evitando que se muestre el login
            finish();
        }

        et_usuario = (EditText)findViewById(R.id.et_usuario);
        et_password = (EditText)findViewById(R.id.et_password);
        btn_login = (Button)findViewById(R.id.btn_ingresar);
        btn_registrar = (Button) findViewById(R.id.btn_registrarse);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //APLICA LOGICA DE BASE DE DATOS DE USUARIOS

                if (checkUsuario(et_usuario.getText().toString(),et_password.getText().toString())){

                    //si el login es correcto, lo ponemos al shared preferences cosa de que no se vuelva a poner el login nuevamente
                    //cuando abramos de vuelta la aplicacion
                    SharedPreferences.Editor editLogin = login.edit();

                    editLogin.putString(NOMBRE_USUARIO,et_usuario.getText().toString());

                    editLogin.apply();
                Intent intent = new Intent(view.getContext(),MainMomentos.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                }else {
                    //si el usuario no se encuentra en la base de datos notificamos
                    Toast.makeText(getApplicationContext(),"Usuario no registrado",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //vamos a la activity que registra el usuario nuevo
                Intent intent = new Intent(view.getContext(),RegistroActivity.class);
                startActivity(intent);
            }
        });

    }

    public boolean checkUsuario(String usuario, String password){
        /*FUNCION QUE RETORNA TRUE SI EL USUARIO SE ENCUENTRA EN LA BASE DE DATOS Y
        * COINCIDE CON LA CONTRASEÑA INGRESADA*/

        db = new SqlLite(getApplicationContext()).open();

        Usuario usuariodb = null;
        String query = "SELECT NOMBRE, PASSWORD FROM USUARIO WHERE" +
                " (NOMBRE='"+usuario+"'" +
                " AND PASSWORD = '"+password+"')";

        Cursor cursor = db.rawQuery(query,null);

        while (cursor.moveToNext()){
            usuariodb = new Usuario();
            usuariodb.setNombre(cursor.getString(0));
            usuariodb.setPassword(cursor.getString(1));

        }
        db.close();
        if (usuariodb!=null) {
            return  (usuariodb.getNombre().equals(usuario) && usuariodb.getPassword().equals(password));
        }else {
            return false;
        }
    }
}
