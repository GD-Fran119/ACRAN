package com.example.ism;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistroResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_result);

        Date fecha = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String fechaString = formatter.format(fecha);
        SharedPreferences preferencias = getSharedPreferences(getString(R.string.preferenciasUltimoRegistro), this.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString(getString(R.string.ultimoRegistro), fechaString);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        Intent intencion = new Intent(this, MainActivity.class);
        startActivity(intencion);
        overridePendingTransition(R.transition.volver_transicion_entrada, R.transition.volver_transicion_salida);
    }

    public void volver(View view){
        onBackPressed();
    }
}