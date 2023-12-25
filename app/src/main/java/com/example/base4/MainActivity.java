package com.example.base4;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.base4.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    Button camera, gallery;
    ImageView imageView;
    TextView result;
    int imageSize = 256;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);

        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);

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

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });
    }

    boolean isLeaf(Bitmap image) {
        // Implementa la lógica para verificar si la imagen es una hoja
        // Por ejemplo, podrías considerar el color, forma u otras características específicas de las hojas.
        // Supongamos que aquí se realiza algún tipo de verificación de hoja.
        // Retorna true si la imagen parece ser una hoja, false si no lo es.
        return true; // Cambia esto según tu lógica de verificación de hojas.
    }

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
            String[] classes = {"Hola pulguilla","Hoja Sana", "Hoja con Tizon Tardio", "Hoja con Tizon Temprano"};
            String predictedClass = classes[maxPos];

            // Formatear el resultado con precisión en porcentaje y mensaje adicional
            String resultText = predictedClass + "\nPrecision: " + String.format("%.2f", maxConfidence * 100) + "%\n";

            if (predictedClass.equals("Hoja Sana")) {
                resultText += "No necesitas aplicar fungicidas";
            } else {
                resultText += "Necesitas aplicar fungicidas";
            }

            result.setText(resultText);

            // Liberar los recursos del modelo si ya no son utilizados.
            model.close();
        } catch (IOException e) {
            // Manejar la excepción
        } catch (NullPointerException e) {
            // Manejar la excepción de puntero nulo si ocurre
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);

                // Verificar si la imagen parece ser una hoja antes de clasificar
                if (isLeaf(image)) {
                    classifyImage(image);
                } else {
                    result.setText("La imagen capturada no es una hoja. Intenta de nuevo.");
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

                // Verificar si la imagen parece ser una hoja antes de clasificar
                if (isLeaf(image)) {
                    classifyImage(image);
                } else {
                    result.setText("La imagen seleccionada no es una hoja. Intenta de nuevo.");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
