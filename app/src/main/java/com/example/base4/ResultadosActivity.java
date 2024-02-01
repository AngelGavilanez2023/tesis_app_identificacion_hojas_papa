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

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.example.base4.DB.DBmanager;


public class ResultadosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

        //boton de guardar
// Encuentra la referencia del botón
        Button btnGuardar = findViewById(R.id.btnGuardar);

// Agrega un Listener al botón para manejar el evento de clic
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Llama al método para guardar los datos en la base de datos
                guardarDatosEnBaseDeDatos();
            }
        });

        // Establecer la barra de estado como transparente
        getWindow().setStatusBarColor(Color.parseColor("#88000000")); // Negro con 50% de opacidad

        // Obtener el intent que inició esta actividad
        Intent intent = getIntent();

        // Obtener los datos pasados desde MainActivity
        String diseaseName = intent.getStringExtra("diseaseName");
        float precision = intent.getFloatExtra("precision", 0.0f);
        String treatment = intent.getStringExtra("treatment");
        byte[] byteArray = intent.getByteArrayExtra("imagen");
        Bitmap imagen = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        // Barra de progreso
        ProgressBar progressBar = findViewById(R.id.progressBar);

        if (precision > 0.0f) {
            // Se detecta una hoja, configurar la visibilidad y el progreso de la barra
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress((int) precision);
        } else {
            // No se detecta una hoja, ocultar la barra de progreso
            progressBar.setVisibility(View.GONE);
        }

        // Obtener el color de la barra de progreso del estilo
        TypedArray ta = obtainStyledAttributes(R.style.ProgressBarStyle, new int[]{android.R.attr.progressDrawable});
        Drawable progressDrawable = ta.getDrawable(0);
        ta.recycle();

        // Configurar el progreso con el estilo personalizado
        progressBar.setProgressDrawable(progressDrawable);

        // Asignar los datos a los elementos de la interfaz de usuario en ResultadosActivity

        // Mostrar el nombre de la enfermedad
        TextView diseaseNameTextView = findViewById(R.id.diseaseName);
        diseaseNameTextView.setText(diseaseName);

        // Mostrar la precisión
        TextView accuracyTextView = findViewById(R.id.accuracy);
        accuracyTextView.setText(String.format("%.2f", precision) + "%");


        //String diseaseName = intent.getStringExtra("diseaseName");
        //float precision = intent.getFloatExtra("precision", 0.0f);
        String aplicar = intent.getStringExtra("aplicar");
        String modo = intent.getStringExtra("modo");
        String fungicida = intent.getStringExtra("fungicida");
        String dosis = intent.getStringExtra("dosis");
        //byte[] byteArray = intent.getByteArrayExtra("imagen");
        //Bitmap imagen = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);



        // Mostrar el tratamiento
        TextView treatmentTextView = findViewById(R.id.treatment);
        //treatmentTextView.setText(treatment);
        treatmentTextView.setText("APLICAR: " + aplicar + "\nMODO: " + modo + "\nFUNGICIDA: " + fungicida + "\nDOSIS: " + dosis);


        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(imagen);

        // Gestionar clics para volver a la actividad anterior (MainActivity)
        LinearLayout headerLayout = findViewById(R.id.headerLayout);
        Button buttonBack = findViewById(R.id.button2);

        // Obtener la referencia al icono de retroceso en el AppBar
        ImageView toolbarBackButton = findViewById(R.id.toolbarBackButton);

        // Gestionar clics en el icono de retroceso del AppBar
                toolbarBackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();  // Finalizar la actividad actual
                    }
                });

        View.OnClickListener backClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Finalizar la actividad actual
            }
        };

        headerLayout.setOnClickListener(backClickListener);
        buttonBack.setOnClickListener(backClickListener);

    }

    //funciones para guardar dando cliick en el boton de guardar
    private void guardarDatosEnBaseDeDatos() {
        // Obtener datos necesarios (por ejemplo, enfermedad, precisión, etc.) desde los extras o donde sea necesario
        String enfermedad = getIntent().getStringExtra("diseaseName");
        float precision = getIntent().getFloatExtra("precision", 0.0f);
        // ... obtener otros datos necesarios ...

        // Obtener instancia de DBmanager
        DBmanager dbManager = new DBmanager(this);

        try {
            // Abrir la base de datos para escritura
            dbManager.open();

            // Llamar al método insertarResultado con los datos relevantes
            dbManager.insertarResultado(enfermedad, precision, obtenerFechaHoraActual());
            // Puedes agregar más datos según sea necesario

            // Mostrar mensaje de éxito o realizar otras acciones según sea necesario
            Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            // Manejar la excepción (mostrar mensaje de error, registrar en el log, etc.)
        } finally {
            // Cerrar la base de datos
            dbManager.close();
        }
    }

    // Método para obtener la fecha y hora actual (puedes ajustarlo según tus necesidades)
    private String obtenerFechaHoraActual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }




}

