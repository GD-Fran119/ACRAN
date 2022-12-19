package com.example.ism;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AsignaturaEleccionActivity extends AppCompatActivity {
    private Button volver;
    private Spinner gradosSpinner, asignaturasSpinner;
    private RequestQueue queue;
    private Context contexto = this;
    JSONArray grados;
    JSONArray asignaturas;
    String asinaturaSelected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignatura_eleccion);

        volver = (Button) findViewById(R.id.volverConsulta);
        gradosSpinner = (Spinner) findViewById(R.id.gradosSpinner);
        asignaturasSpinner = (Spinner) findViewById(R.id.asignaturasSpinner);
        queue = Volley.newRequestQueue(this);

        ArrayList<String> lista = new ArrayList<>();
        lista.add("Grado");


        StringRequest peticion = new StringRequest(Request.Method.GET, getString(R.string.IP_server) + getString(R.string.rutaGrados),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Si hay respuesta del server se introducen los grados en el spinner
                        try{

                            grados = new JSONArray(response);

                            for(int i = 0; i < grados.length(); i++){
                                lista.add(grados.getJSONObject(i).getString("nombre"));
                            }
                        }catch (Exception e){
                            Toast.makeText(contexto, e.toString(),Toast.LENGTH_SHORT).show();
                        }

                        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(contexto, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, lista);
                        gradosSpinner.setAdapter(stringArrayAdapter);

                        //Cuando se seleccione un grado se buscan sus asignaturas
                        gradosSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if(i < 1) {
                                    if (asignaturasSpinner != null){
                                        asignaturasSpinner.setAdapter(null);
                                    }
                                    asinaturaSelected = null;
                                    return;
                                }

                                int idGradoSelected;
                                try {
                                    idGradoSelected = grados.getJSONObject(i - 1).getInt("codigo");
                                }catch (Exception e){
                                    Toast.makeText(contexto, "Fallo json 2 peticion", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String url =  getString(R.string.IP_server) + "/" + Integer.toString(idGradoSelected) + getString(R.string.rutaAsignaturas);

                                //Peticion para las asignaturas del grado seleccionado
                                StringRequest peticion = new StringRequest(Request.Method.GET, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                //Si se recibe respuesta se introducen los datos en el spinner de las asignaturas
                                                ArrayList<String> lista = new ArrayList<>();
                                                lista.add("Asignatura");

                                                try{

                                                    asignaturas = new JSONArray(response);

                                                    for(int i = 0; i < asignaturas.length(); i++){
                                                        lista.add(asignaturas.getJSONObject(i).getString("nombre"));
                                                    }
                                                }catch (Exception e){
                                                    Toast.makeText(contexto, e.toString(),Toast.LENGTH_SHORT).show();
                                                }

                                                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(contexto, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, lista);
                                                asignaturasSpinner.setAdapter(stringArrayAdapter);

                                            }
                                        }, new Response.ErrorListener() {
                                    //Fallo en la peticion de aulas
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(contexto, "Error al pedir las asignaturas del grado seleccionado", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //Se manda la peticion
                                peticion.setTag(0);
                                queue.add(peticion);

                                //Se establece la acción a realizar si se selecciona una asignatura
                                //Se guarda el nombre convertido a percentage

                                asignaturasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        if(i < 1) {
                                            asinaturaSelected = null;
                                            return;
                                        }
                                        try {
                                            asinaturaSelected = asignaturas.getJSONObject(i - 1).getString("nombre").replace(" ", "%20").replace("Ñ", "%C3%91");
                                        }catch (Exception e){
                                            asinaturaSelected = null;
                                            Toast.makeText(contexto, "Fallo listener asignatura", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }
                }, new Response.ErrorListener() {
            //Si falla la peticcion de los grados
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(contexto, "Fallo al consultar los grados", Toast.LENGTH_SHORT).show();
            }
        });

        peticion.setTag(0);
        queue.add(peticion);

    }

    @Override
    protected void onStart() {
        super.onStart();

        ArrayList<String> lista = new ArrayList<>();
        lista.add("Grado");

        Intent intencion = getIntent();
    }


    @Override
    protected void onStop() {
        super.onStop();
        queue.cancelAll(0);
    }

    public void buscarAsignatura(View view) {

        if (asinaturaSelected == null){
            Toast.makeText(this, "Escoja la asignatura que quiera consultar", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        long nowMilis = new Date().getTime();
        //Se multiplica por 6 porque se buscan 7 días, el actual y 6 más
        long sevenDaysLaterMilis = 6 * 24 * 60 * 60 * 1000 + nowMilis;
        String fechaInicio = formater.format(new Date(nowMilis));
        String fechaFin = formater.format(new Date(sevenDaysLaterMilis));

        String url = getString(R.string.IP_server) + getString(R.string.rutaAsignaturaReservas)+ asinaturaSelected + "/" + fechaInicio + "/" + fechaFin;

        Intent intencion = new Intent(this, LoadingConsultasActivity.class);

        intencion.putExtra("url", url);
        intencion.putExtra("asignatura", asignaturasSpinner.getSelectedItem().toString());
        intencion.putExtra("nextActivity", VistaReservaAsignaturasActivity.class);

        startActivity(intencion);
        overridePendingTransition(R.transition.new_activity_entrada, R.transition.new_activity_salida);
    }

    public void volver(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intencion = new Intent(this, MainActivity.class);
        startActivity(intencion);
        overridePendingTransition(R.transition.volver_transicion_entrada, R.transition.volver_transicion_salida);
    }
}