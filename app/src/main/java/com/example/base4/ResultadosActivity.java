package com.example.base4;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

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
        float precision = intent.getFloatExtra("precision", 0.0f);
        byte[] byteArray = intent.getByteArrayExtra("imagen");
        Bitmap imagen = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        // Barra de progreso
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress((int) precision);

        // Obtener el color de la barra de progreso del estilo
        TypedArray ta = obtainStyledAttributes(R.style.ProgressBarStyle, new int[]{android.R.attr.progressDrawable});
        Drawable progressDrawable = ta.getDrawable(0);
        ta.recycle();

        // Configurar el progreso con el estilo personalizado
        progressBar.setProgressDrawable(progressDrawable);

        // Cambiar la visibilidad de la barra de progreso
        progressBar.setVisibility(View.VISIBLE);

        // Asignar los datos a los elementos de la interfaz de usuario en ResultadosActivity
        TextView resultadoTextView = findViewById(R.id.result);
        resultadoTextView.setText(resultado);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(imagen);

        // Gestionar clics para volver a la actividad anterior (MainActivity)
        LinearLayout headerLayout = findViewById(R.id.headerLayout);
        Button buttonBack = findViewById(R.id.button2);

        View.OnClickListener backClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Finalizar la actividad actual
            }
        };

        headerLayout.setOnClickListener(backClickListener);
        buttonBack.setOnClickListener(backClickListener);
    }
}
