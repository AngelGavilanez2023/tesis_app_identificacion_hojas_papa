package com.example.base4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultadosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

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
    }
}
