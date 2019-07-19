package com.example.momentosapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class PreferencesActivity extends AppCompatActivity {

    SharedPreferences prefs;
    public static final String PREF_STRING_IDIOMA="idioma";
    private int idiomaIndex;


    RadioGroup rgIdioma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        Toolbar toolbar = findViewById(R.id.toolbar);

        prefs = getSharedPreferences(MainActivity.NOMBRE_PREFERENCIAS,MODE_PRIVATE);

        rgIdioma = (RadioGroup)findViewById(R.id.rg_idioma);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //si es la primera vez que lo ejecutamos
        if(prefs.getInt(PREF_STRING_IDIOMA,-1)==-1){
            Toast.makeText(getApplicationContext(),"idioma = nulo",Toast.LENGTH_SHORT).show();
            RadioButton button = findViewById(R.id.rb_op_español);
            button.setChecked(true);
            guardarSettings();
        }else{
        idiomaIndex = prefs.getInt(PREF_STRING_IDIOMA,1);
        ((RadioButton)rgIdioma.getChildAt(idiomaIndex)).setChecked(true);
        }
        //rgIdioma.check(idiomaIndex);
    }


    @Override
    public boolean onSupportNavigateUp() {
        guardarSettings();
        onBackPressed();
        Toast.makeText(getApplicationContext(),"presionaste el backbutton",Toast.LENGTH_SHORT);
        return true;
    }

    private void guardarSettings() {
        int radioButtonID = rgIdioma.getCheckedRadioButtonId();
        View radioButton = rgIdioma.findViewById(radioButtonID);
        //indice del radiobtn seleccionado
        idiomaIndex = rgIdioma.indexOfChild(radioButton);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_STRING_IDIOMA,idiomaIndex);
        editor.commit();
    }

}
