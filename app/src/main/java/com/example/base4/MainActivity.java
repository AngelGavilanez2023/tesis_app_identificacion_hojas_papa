package com.example.base4;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.base4.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.example.base4.DB.DBmanager;
import com.example.base4.modelo.Tratamiento;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class MainActivity extends AppCompatActivity {

    private Button camera, gallery;
    private ImageView imageView;
    private TextView result;

    private int imageSize = 256;
    private DBmanager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Establecer la barra de estado como transparente
        getWindow().setStatusBarColor(Color.parseColor("#88000000")); // Negro con 50% de opacidad

        Button btnMostrarResultados = findViewById(R.id.btnMostrarResultados);

        // Desactivar el modo oscuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        btnMostrarResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad MostrarResultadosActivity
                Intent intent = new Intent(MainActivity.this, MostrarResultados.class);
                startActivity(intent);
            }
        });
        // Inicializar DBmanager
        dbManager = new DBmanager(this);

        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);

        //Widget: android:id="@+id/result del activity_main.xml
        imageView = findViewById(R.id.imageView);

        //*********************************| Funcion Camara ]********************************************
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        //*********************************| Funcion Galeria |********************************************
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 2); // Cambié el código de solicitud a 2
            }
        });//Fin

        //*********************************| BDD Tratamientos |********************************************
        try {
            // Abrir la base de datos para escritura
            dbManager.open();
            // Insertar datos de prueba
            dbManager.insertarTratamiento("\nPlaga Hoja con Pulguilla", "Aspersión, intervalo se seguridad 14 días.","Contacto, ingestión", "KUIK 90 PS","300g - 400g por hectárea" );
            dbManager.insertarTratamiento("\nHoja Sana", "No necesitas aplicar fungicidas","SD","SD","SD");
            dbManager.insertarTratamiento("\nEnfermedad Hoja con Tizón Tardío", "Aspersión, intervalo de seguridad 14 días.","Sistémico","ALLIETE","2.5 kg por hectárea");
            dbManager.insertarTratamiento("\nEnfermedad Hoja con Tizón Temprano", "Asperción", "Contacto","CUPRAVIT","2kg - 4kg por hectárea");
            //dbManager.insertarTratamiento("\nHoja con Pulguilla", "Aspersión","Contacto, ingestión, Sistémico", "FOLIMART","600ml - 900ml por hectárea" );

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cerrar la base de datos
            dbManager.close();
        }
        //fin codigo tratamiento con bbdd

    }

    //*********************************| Cambiar foto del modelo por la de defecto |********************************************
    public void resetDefaultImage() {
        imageView.setImageResource(R.drawable.agricultor_celular_3);
    }

    //*********************************| Establecer imagen predeterminada |********************************************
    public void setDefaultImage() {
        imageView.setImageResource(R.drawable.agricultor_celular_3);
    }
    //*********************************| Funcion Clasificcion con Modelo.tflite |********************************************

    public void classifyImage(Bitmap image) {
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Controlando entradas de imágenes.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            // Iterar sobre cada píxel y extraer los valores de R, G y B. Agregar esos valores individualmente al búfer de bytes.
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Ejecutar el modelo entrenado y obtener el resultado
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();

            // encontrar el índice de la clase con la mayor confianza o precisión
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"\nPlaga Hoja con Pulguilla", "\nHoja Sana", "\nEnfermedad Hoja con Tizón Tardío", "\nEnfermedad Hoja con Tizón Temprano"};
            String predictedClass = classes[maxPos];

            // Obtener tratamiento asociado a la enfermedad desde la base de datos
            Tratamiento tratamiento = dbManager.obtenerTratamiento(predictedClass);

            // Formatear el resultado con precisión en porcentaje y mensaje adicional
            String resultText = predictedClass + "\nPrecisión: " + String.format("%.2f", maxConfidence * 100) + "%";

            if (tratamiento != null) {
                resultText += "\n\nTratamiento: " + tratamiento.getAplicar() + "\n" + tratamiento.getModo() + "\n" + tratamiento.getFungicida() + "\n" + tratamiento.getDosis();
            } else {
                resultText += "\n\nNo se encontró tratamiento en la base de datos.";
            }

            float precisionPercentage = maxConfidence * 100;

            // Mostrar los resultados en la pantalla (aquí puedes usar resultText)

            // Llamar al método para establecer la imagen predeterminada
            setDefaultImage();

            // Pasar la precisión como un extra en el Intent
            Intent resultadosIntent = new Intent(MainActivity.this, ResultadosActivity.class);
            resultadosIntent.putExtra("diseaseName", predictedClass);
            resultadosIntent.putExtra("precision", precisionPercentage);
            resultadosIntent.putExtra("fungicida", resultText);




            // Verificar si se obtuvo un tratamiento
            if (tratamiento != null) {
                // Si se obtuvo, pasar los atributos del tratamiento
                resultadosIntent.putExtra("aplicar", tratamiento.getAplicar());
                resultadosIntent.putExtra("modo", tratamiento.getModo());
                resultadosIntent.putExtra("fungicida", tratamiento.getFungicida());
                resultadosIntent.putExtra("dosis", tratamiento.getDosis());
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            resultadosIntent.putExtra("imagen", byteArray);

            // Iniciar ResultadosActivity solo si la imagen es una hoja
            startActivity(resultadosIntent);
            model.close();
        } catch (IOException e) {
            // Manejar la excepción
        } catch (NullPointerException e) {
            // Manejar la excepción de puntero nulo si ocurre
        }
    }

    // Método para obtener la fecha y hora actual en el formato deseado
    private String obtenerFechaHoraActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    //*********************************| FIN Clasificacion con Modelo.tflite |********************************************

    //*********************************| Metodo de Resultados capturados hacia la Vista Dos (resultadosActivity) |********************************************
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bitmap image = null;  // Declarar la variable image al principio del método

        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);

                // Eliminado el uso de validarHoja
                classifyImage(image);
            } else {
                Uri dat = data.getData();
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);

                // Eliminado el uso de validarHoja
                classifyImage(image);
            }
        } else {
            // Eliminado el uso de validarHoja
            String errorMessage = "\nEsto no parece ser una hoja de Papa.";
            Intent resultadosIntent = new Intent(MainActivity.this, ResultadosActivity.class);
            resultadosIntent.putExtra("diseaseName", errorMessage);

            // Convertir la imagen a un array de bytes
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            resultadosIntent.putExtra("imagen", byteArray);

            // Llamar al método para establecer la imagen predeterminada
            setDefaultImage();

            startActivity(resultadosIntent);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
