package com.example.ism;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class NFCActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private Handler handler;
    private Runnable corredor;
    private boolean wasNFCActivated;
    private TextView NFCStatusTextView;
    private Context contexto = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcactivity);
        //If you get here it is supposed that the device supports NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        NFCStatusTextView = (TextView) findViewById(R.id.textViewNFCStatus);
        NFCStatusTextView.setBackgroundColor(ContextCompat.getColor(contexto, R.color.NFC_disabled));

    }

    @Override
    protected void onStart() {
        super.onStart();


        wasNFCActivated = nfcAdapter.isEnabled();

        NFCStatusTextView.setBackgroundColor(ContextCompat.getColor(contexto, (wasNFCActivated ? R.color.NFC_enabled : R.color.NFC_disabled)));
        NFCStatusTextView.setText((wasNFCActivated ? "NFC activado" : "NFC desactivado"));

        corredor = new Runnable() {
            @Override
            public void run() {
                //Si se va a cambiar el color al mismo que se tiene, se establece el handler y se sale de la función
                //No se actualiza el color
                if (nfcAdapter.isEnabled() == wasNFCActivated){
                    handler.postDelayed(this, 5000);
                    return;
                }

                //Si no se va a cambiar al mismo
                //Se comprueba si se ha activado o desactivado

                ValueAnimator animador;
                //Se ha activado
                if(nfcAdapter.isEnabled()){
                    NFCStatusTextView.setText("NFC activado");
                    animador = ValueAnimator.ofObject(new ArgbEvaluator(),
                            ((ColorDrawable) NFCStatusTextView.getBackground()).getColor(), ContextCompat.getColor(contexto, R.color.NFC_enabled));
                    animador.addUpdateListener(animator -> NFCStatusTextView.setBackgroundColor((Integer)animator.getAnimatedValue()));
                    wasNFCActivated = true;


                }
                else{
                    NFCStatusTextView.setText("NFC desactivado");
                    animador = ValueAnimator.ofObject(new ArgbEvaluator(),
                            ((ColorDrawable) NFCStatusTextView.getBackground()).getColor(), ContextCompat.getColor(contexto, R.color.NFC_disabled));
                    animador.addUpdateListener(animator -> NFCStatusTextView.setBackgroundColor((Integer)animator.getAnimatedValue()));
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



    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(corredor);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intencion = new Intent(this, MainActivity.class);
        startActivity(intencion);
        overridePendingTransition(R.transition.volver_transicion_entrada, R.transition.volver_transicion_salida);
    }

    public void volver(View view){
        onBackPressed();
    }

}