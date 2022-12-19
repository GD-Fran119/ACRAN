package com.example.ism;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;

public class RegistrarAsistenciaActivity extends AppCompatActivity {
    private EditText usuario, password, aula;
    Context contexto = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_asistencia);
        usuario = (EditText) findViewById(R.id.editTextTextPersonName2);
        password = (EditText) findViewById(R.id.editTextTextPassword);
        aula = (EditText) findViewById(R.id.IDAulaRegistro);
    }

    public void registrarse(View view){

        String user = usuario.getText().toString();
        String pass = password.getText().toString();
        String IDaula = aula.getText().toString();

        if(user.equals("") || pass.equals("") || IDaula.equals("")){
            Toast.makeText(this, "Introduzca todos los datos", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject json = new JSONObject();

        try{
            json.put("nombre_usuario", user);
            json.put("password", pass);
        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        String postUrl = getString(R.string.IP_server) + getString(R.string.rutaAsistencia) + IDaula + "/";

        Intent intencion = new Intent(this, LoadingRegistroActivity.class);
        intencion.putExtra("url", postUrl);
        intencion.putExtra("data", json.toString());
        startActivity(intencion);
        overridePendingTransition(R.transition.new_activity_entrada, R.transition.new_activity_salida);
    }

    public void volver(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intencion = new Intent(this, MainActivity.class);
        startActivity(intencion);
        overridePendingTransition(R.transition.volver_transicion_entrada, R.transition.volver_transicion_salida);
    }
}