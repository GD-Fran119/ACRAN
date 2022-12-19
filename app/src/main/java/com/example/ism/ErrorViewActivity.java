package com.example.ism;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ErrorViewActivity extends AppCompatActivity {

    private TextView error, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_view);
        error = (TextView) findViewById(R.id.textView2);
        message = (TextView) findViewById(R.id.textView3);

        Intent intencion = getIntent();

        String error_string = error.getText().toString() + " " + intencion.getStringExtra("error");
        String message_string = intencion.getStringExtra("message");

        error.setText(error_string);
        message.setText(message_string);

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