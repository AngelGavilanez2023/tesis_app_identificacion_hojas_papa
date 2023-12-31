package com.example.base4;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

        // Inicializar DBmanager
        dbManager = new DBmanager(this);

        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);

        //Widget: android:id="@+id/result del activity_main.xml
        result = findViewById(R.id.result);
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
            dbManager.insertarTratamiento("\nHoja con Pulguilla", "Aplicar aceite de neem en las hojas y tallos de la planta");
            dbManager.insertarTratamiento("\nHoja Sana", "No necesitas aplicar fungicidas");
            dbManager.insertarTratamiento("\nHoja con Tizón Tardío", "Aplicar fungicida clorotalonil, mancozeb, o cimoxamil en las hojas");
            dbManager.insertarTratamiento("\nHoja con Tizón Temprano", "Aplicar fungicida clorotalonil, mancozeb, o cimoxamil en las hojas");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cerrar la base de datos
            dbManager.close();
        }
        //fin codigo tratamiento con bbdd

    }

    //*********************************| Funcion Validar si es Hoja |********************************************
    boolean validarHoja(Bitmap image) {
        // verifica si la imagen tiene un área verde significativa
        int greenPixels = 0;
        int totalPixels = image.getWidth() * image.getHeight();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getPixel(x, y);
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;

                // Si el componente verde es mayor que los demás, se considera un píxel verde
                if (green > red && green > blue) {
                    greenPixels++;
                }
            }
        }

        // Calcula la proporción de píxeles verdes en la imagen
        double greenRatio = (double) greenPixels / totalPixels;

        // Establece un umbral para definir si la imagen parece ser una hoja
        // en un inicio se tenia esto threshold = 0.1; pero se aumento para una validacion mas robusta
        double threshold = 0.3;

        return greenRatio > threshold;
    }

    //*********************************| Cambiar foto del modelo por la de defecto |********************************************
    public void resetDefaultImage() {
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
            String[] classes = {"\nHoja con Pulguilla", "\nHoja Sana", "\nHoja con Tizón Tardío", "\nHoja con Tizón Temprano"};
            String predictedClass = classes[maxPos];

            // Obtener tratamiento asociado a la enfermedad desde la base de datos
            String tratamiento = dbManager.obtenerTratamiento(predictedClass);

            // Formatear el resultado con precisión en porcentaje y mensaje adicional
            String resultText = predictedClass + "\nPrecisión: " + String.format("%.2f", maxConfidence * 100) + "%";

            if (tratamiento != null) {
                resultText += "\n\nTratamiento: " + tratamiento;
            } else {
                resultText += "\n\nNo se encontró tratamiento en la base de datos.";
            }


            float precisionPercentage = maxConfidence * 100;
            //vista dos
            // Calcular el progreso basado en la precisión obtenida
            int progress = (int) precisionPercentage;
            // Crear un Intent para pasar a la ResultadosActivity


            // Intent para pasar a ResultadosActivity
            Intent resultadosIntent = new Intent(MainActivity.this, ResultadosActivity.class);
            resultadosIntent.putExtra("resultado", resultText);

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
    //*********************************| FIN Clasificacion con Modelo.tflite |********************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);

                if (validarHoja(image)) {
                    classifyImage(image);
                } else {
                    // Mostrar mensaje y enviar imagen a ResultadosActivity
                    String errorMessage = "\nEsto no parece ser una hoja de Papa.";
                    Intent resultadosIntent = new Intent(MainActivity.this, ResultadosActivity.class);
                    resultadosIntent.putExtra("resultado", errorMessage);

                    // Convertir la imagen a un array de bytes
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    resultadosIntent.putExtra("imagen", byteArray);

                    startActivity(resultadosIntent);
                }
            } else {
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);

                if (validarHoja(image)) {
                    classifyImage(image);
                } else {
                    result.setText("Esto no parece ser una hoja de Papa.");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
