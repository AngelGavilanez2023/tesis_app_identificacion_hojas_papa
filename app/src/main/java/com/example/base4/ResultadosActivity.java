package com.example.base4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultadosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);
        // Establecer la barra de estado como transparente
        getWindow().setStatusBarColor(Color.parseColor("#88000000")); // Negro con 50% de opacidad

        // Obtener el intent que inició esta actividad
        Intent intent = getIntent();

        // Obtener los datos pasados desde MainActivity
        String resultado = intent.getStringExtra("resultado");
        byte[] byteArray = intent.getByteArrayExtra("imagen");
        Bitmap imagen = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        // Asignar los datos a los elementos de la interfaz de usuario en ResultadosActivity
        TextView resultadoTextView = findViewById(R.id.result);
        resultadoTextView.setText(resultado);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(imagen);

        //**********************************| Metodo para volver al Main Vista Uno CON FLECHA VOLVER|******************************
        // Obtener el LinearLayout del encabezado
        LinearLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para regresar a la MainActivity
                Intent intent = new Intent(ResultadosActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //**********************************| Metodo para volver al Main Vista Uno CON BOTON VOLVER|******************************
        Button buttonBack = findViewById(R.id.button2);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para regresar a la MainActivity
                Intent intent = new Intent(ResultadosActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
    //**********************************| Metodo cambia foto actual a la de defecto |******************************
    //metodo que cambia la imagen del modelo a la imagen por defecto
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ResultadosActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
