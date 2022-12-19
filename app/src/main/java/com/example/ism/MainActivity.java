package com.example.ism;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    TextView texto, NFCStatus;
    private Handler handler;
    private Runnable corredor;
    private boolean wasNFCActivated;
    private NfcAdapter nfcAdapter;
    private final Context contexto = this;
    private int NFCcounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_ISM);
        setContentView(R.layout.activity_main);

        NFCcounter = 0;

        texto = findViewById(R.id.textView4);
        NFCStatus = findViewById(R.id.NFCStatusMain);
        nfcAdapter = NfcAdapter.getDefaultAdapter(contexto);

        checkNFCSupport();

        if(!deviceHasNFC())
            NFCStatus.setBackgroundColor(ContextCompat.getColor(contexto, R.color.noNFCMain));
        else{
            setUpCheckerNFCStatus();
        }

        setUltimoRegistroText();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setTheme(R.style.Theme_ISM);
    }

    private void checkNFCSupport(){
        SharedPreferences NFCPreferencias = getSharedPreferences(getString(R.string.preferenciasNFC), Context.MODE_PRIVATE);
        if(NFCPreferencias.contains(getString(R.string.supportsNFC))) return;

        boolean NFCSupport = deviceHasNFC();
        NFCPreferencias.edit().putBoolean(getString(R.string.supportsNFC), NFCSupport).apply();
    }

    private boolean deviceHasNFC(){
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        return (nfcAdapter != null);
    }

    public void ok(View view){
        Intent intencion = new Intent(this, ConsultaAulaEleccionFechaActivity.class);
        startActivity(intencion);
        overridePendingTransition(R.transition.new_activity_entrada, R.transition.new_activity_salida);
    }

    public void goToNFC(View view){

        //Si no se tiene NFC no se hace nada
        if(nfcAdapter == null){
            return;
        }

        //Si se tiene, se debe pulsar 10 veces para acceder a la vista
        if(NFCcounter < 10){
            NFCcounter ++;
            return;
        }

        Intent intencion = new Intent(this, NFCActivity.class);
        startActivity(intencion);
        overridePendingTransition(R.transition.new_activity_entrada, R.transition.new_activity_salida);
    }

    public void goToAsignatura(View view){
        Intent intencion = new Intent(contexto, AsignaturaEleccionActivity.class);
        startActivity(intencion);
        overridePendingTransition(R.transition.new_activity_entrada, R.transition.new_activity_salida);

    }

    public void goToAsistencia(View view){
        Intent intencion = new Intent(this, RegistrarAsistenciaActivity.class);
        startActivity(intencion);
        overridePendingTransition(R.transition.new_activity_entrada, R.transition.new_activity_salida);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (nfcAdapter != null) handler.removeCallbacks(corredor);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }

    private void setUltimoRegistroText(){
        SharedPreferences registroPreferencias = getSharedPreferences(getString(R.string.preferenciasUltimoRegistro), Context.MODE_PRIVATE);
        String ultimo_registro = registroPreferencias.getString(getString(R.string.ultimoRegistro), null);

        String textoRegistro = "Ultimo registro: " + ((ultimo_registro == null) ? "Nunca" : ultimo_registro);
        texto.setText(textoRegistro);
    }

    private void setUpCheckerNFCStatus(){

        wasNFCActivated = nfcAdapter.isEnabled();

        if(wasNFCActivated){
            NFCStatus.setBackgroundColor(ContextCompat.getColor(contexto, R.color.NFC_enabled));
        }
        else{
            NFCStatus.setBackgroundColor(ContextCompat.getColor(contexto, R.color.NFC_disabledMain));
        }
        //Start runner
        corredor = new Runnable() {
            @Override
            public void run() {
                //Si no se ha actualizado el estado
                if (nfcAdapter.isEnabled() == wasNFCActivated){
                    handler.postDelayed(this, 5000);
                    return;
                }

                ValueAnimator animador;
                //Si ha cambiado
                if(nfcAdapter.isEnabled()){
                    animador = ValueAnimator.ofObject(new ArgbEvaluator(),
                            ((ColorDrawable) NFCStatus.getBackground()).getColor(), ContextCompat.getColor(contexto, R.color.NFC_enabled));
                    animador.addUpdateListener(animator -> NFCStatus.setBackgroundColor((Integer)animator.getAnimatedValue()));
                    wasNFCActivated = true;
                }
                else{
                    animador = ValueAnimator.ofObject(new ArgbEvaluator(),
                            ((ColorDrawable) NFCStatus.getBackground()).getColor(), ContextCompat.getColor(contexto, R.color.NFC_disabledMain));
                    animador.addUpdateListener(animator -> NFCStatus.setBackgroundColor((Integer)animator.getAnimatedValue()));
                    wasNFCActivated = false;
                }

                animador.setDuration(500);
                animador.start();

                handler.postDelayed(this, 5000);

            }
        };

        handler = new Handler();
        corredor.run();
    }

}