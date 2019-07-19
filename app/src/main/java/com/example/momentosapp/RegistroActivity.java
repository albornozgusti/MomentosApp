package com.example.momentosapp;

import android.content.ContentValues;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistroActivity extends AppCompatActivity {

    /*actividad ejecutada para registrar un usuario para que se loguee*/
    private EditText et_usuario;
    private EditText et_password;
    private EditText et_email;
    private Button btn_registrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        setTitle("Registrar usuario");

        et_usuario = (EditText)findViewById(R.id.et_usuario);
        et_password = (EditText)findViewById(R.id.et_password);
        et_email = (EditText)findViewById(R.id.et_email);
        btn_registrar = (Button)findViewById(R.id.btn_registrarse);

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //obtenemos valores de los campos
                String usuario = et_usuario.getText().toString();
                String password = et_password.getText().toString();
                String email = et_email.getText().toString();

                //creamos el registro clave valor
                ContentValues registro = new ContentValues();
                registro.put("NOMBRE", usuario);
                registro.put("MAIL", email);
                registro.put("PASSWORD", password);
                //nombre, mail y password

                SqlLite bd = new SqlLite(getApplicationContext());
                bd.open().insert("USUARIO",null,registro);
                bd.close();

                //notificamos el registro exitoso
                Toast.makeText(getBaseContext(),"Se ha registrado el usuario",Toast.LENGTH_SHORT).show();

                //finalizamos la actividad actual de registro
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
}
