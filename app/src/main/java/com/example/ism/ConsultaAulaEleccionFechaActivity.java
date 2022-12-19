package com.example.ism;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.dateUtils.Fecha;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConsultaAulaEleccionFechaActivity extends AppCompatActivity {

    private EditText fecha1, fecha2, aula;
    private static TextView ultimoClickado;
    private Context contexto = this;

    //Dialogo para que el usuario introduzca las fechas
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        private String parseDateToString(int year, int month, int day){
            String date = "";
            date += (day < 10 ? "0" + Integer.toString(day) : Integer.toString(day));
            date += "/";
            date += (month + 1 < 10 ? "0" + Integer.toString(month + 1) : Integer.toString(month + 1));
            date += "/";
            date += Integer.toString(year);
            return date;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            String date = parseDateToString(year, month, day);
            ultimoClickado.setText(date);

        }
    }

    public void showDatePickerFragment(View v) {
        ultimoClickado = (EditText) v;
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_aula_eleccion_fecha);

        fecha1 = (EditText) findViewById(R.id.fechaInicio);
        fecha2 = (EditText) findViewById(R.id.fechaFin);
        aula = (EditText) findViewById(R.id.idAulaConsulta);
    }

    public void volver(View view){ onBackPressed(); }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intencion = new Intent(this, MainActivity.class);
        startActivity(intencion);
        overridePendingTransition(R.transition.volver_transicion_entrada, R.transition.volver_transicion_salida);
    }

    public void buscarHoy(View view){

        if(aula.getText().toString().equals("")){
            Toast.makeText(contexto, "Introduzca el ID del aula", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date hoy = new Date();
        String IDaula = aula.getText().toString();

        String url = getString(R.string.IP_server) + getString(R.string.rutaAulas) + IDaula + "/" + formatter.format(hoy);

        Intent intencion = new Intent(contexto, LoadingConsultasActivity.class);
        intencion.putExtra("url", url);
        intencion.putExtra("nextActivity", ConsultaDiaResultActivity.class);
        startActivity(intencion);
        overridePendingTransition(R.transition.new_activity_entrada, R.transition.new_activity_salida);

    }

    public void buscarEnRango(View view){
        String date1 = fecha1.getText().toString();
        String date2 = fecha2.getText().toString();
        String IDaula = aula.getText().toString();

        if(IDaula.toString().equals("")){
            Toast.makeText(contexto, "Introduzca el ID del aula", Toast.LENGTH_SHORT).show();
            return;
        }

        if(date1.equals("") || date2.equals("")){
            Toast.makeText(contexto, "Seleccione las fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        String [] partes1 = date1.split("/");
        String [] partes2 = date2.split("/");

        Fecha fechaObj1 = new Fecha(Integer.parseInt(partes1[0]),Integer.parseInt(partes1[1]), Integer.parseInt(partes1[2]));
        Fecha fechaObj2 = new Fecha(Integer.parseInt(partes2[0]),Integer.parseInt(partes2[1]), Integer.parseInt(partes2[2]));

        if(fechaObj1.greaterThan(fechaObj2)){
            Toast.makeText(contexto, "La fecha fin no puede ser menor que la fecha inicio", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = getString(R.string.IP_server) + getString(R.string.rutaAulas) + IDaula + "/" + partes1[2] + "-" + partes1[1] + "-" + partes1[0] + "/"
                                                   + partes2[2] + "-" + partes2[1] + "-" + partes2[0];
        Intent intencion = new Intent(this, LoadingConsultasActivity.class);
        intencion.putExtra("nextActivity", ConsultaRangoResultActivity.class);
        intencion.putExtra("url", url);

        startActivity(intencion);
        overridePendingTransition(R.transition.new_activity_entrada, R.transition.new_activity_salida);

    }
}