package com.example.ism;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoadingConsultasActivity extends AppCompatActivity {

    private ImageView loading1, loading2, loading3;
    private RequestQueue queue;
    private Context contexto = this;
    String requestRresponse;
    private CountDownTimer temporizador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        loading1 = (ImageView) findViewById(R.id.loadingImage);
        loading2 = (ImageView) findViewById(R.id.loadingImage2);
        loading3 = (ImageView) findViewById(R.id.loadingImage3);
        queue = Volley.newRequestQueue(this);

        RotateAnimation animacion1 = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animacion1.setRepeatCount(Animation.INFINITE);
        animacion1.setDuration(3000);
        animacion1.setInterpolator(new LinearInterpolator());

        RotateAnimation animacion2 = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animacion2.setRepeatCount(Animation.INFINITE);
        animacion2.setDuration(2700);
        animacion2.setInterpolator(new LinearInterpolator());

        RotateAnimation animacion3 = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animacion3.setRepeatCount(Animation.INFINITE);
        animacion3.setDuration(2500);
        animacion3.setInterpolator(new LinearInterpolator());

        loading1.startAnimation(animacion1);
        loading2.startAnimation(animacion2);
        loading3.startAnimation(animacion3);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intencion = getIntent();
        String url = intencion.getStringExtra("url");
        String asignatura = intencion.getStringExtra("asignatura");


        StringRequest peticion = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                requestRresponse = response;
                temporizador = new CountDownTimer(2000,3000) {
                    @Override
                    public void onTick(long l) {
                        //No se hace nada en cada intervalo
                    }

                    @Override
                    public void onFinish() {
                        try{
                            JSONObject json = new JSONObject(response);
                            String message = json.getString("errMessage");
                            Intent intencion = new Intent(contexto, ErrorViewActivity.class);
                            intencion.putExtra("error", "1000");
                            if(asignatura != null){
                                intencion.putExtra("asignatura", asignatura);
                            }
                            intencion.putExtra("message", message);
                            startActivity(intencion);
                            overridePendingTransition(R.transition.new_activity_entrada, R.transition.new_activity_salida);
                            return;
                        }catch (Exception e){}
                        onRequestFinished();
                    }
                };

                temporizador.start();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                temporizador = new CountDownTimer(2000,3000) {
                    @Override
                    public void onTick(long l) {
                        //No se hace nada en cada intervalo
                    }

                    @Override
                    public void onFinish() {
                        Intent intencion = new Intent(contexto, ErrorViewActivity.class);

                        if(error.toString().contains("Server")){
                            intencion.putExtra("error", "500");
                        }
                        else if(error.toString().contains("Client")){
                            intencion.putExtra("error", "400");
                        }
                        else{
                            intencion.putExtra("error", "1000");
                        }
                        intencion.putExtra("message", error.toString());

                        startActivity(intencion);
                        overridePendingTransition(R.transition.new_activity_entrada, R.transition.new_activity_salida);
                    }
                };

                temporizador.start();

            }
        });

        peticion.setTag(0);
        queue.add(peticion);

    }

    public void onRequestFinished(){
        Intent intencion = new Intent(this, (Class<?>) getIntent().getSerializableExtra("nextActivity"));

        intencion.putExtra("asignatura", getIntent().getStringExtra("asignatura"));
        intencion.putExtra("message", requestRresponse);
        startActivity(intencion);
        overridePendingTransition(R.transition.new_activity_entrada, R.transition.new_activity_salida);
    }

    @Override
    public void onBackPressed() {
        Intent intencion = new Intent(this, MainActivity.class);
        startActivity(intencion);
        overridePendingTransition(R.transition.volver_transicion_entrada, R.transition.volver_transicion_salida);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        queue.cancelAll(0);
        try{
            temporizador.cancel();
        }catch (Exception e){}
    }
}