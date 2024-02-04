package com.example.base4;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.base4.DB.DBmanager;
import com.example.base4.modelo.Resultados;
import java.util.List;
import android.Manifest;

public class MostrarResultados extends AppCompatActivity {

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isAccepted -> {
                if (isAccepted) {
                    Toast.makeText(this, "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show();
                    generarPDF(); // Llamar al método para generar el PDF después de obtener los permisos
                } else {
                    Toast.makeText(this, "PERMISOS DENEGADOS", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(Color.parseColor("#88000000")); // Negro con 50% de opacidad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_resultados);

        // Obtener instancia de DBmanager
        DBmanager dbManager = new DBmanager(this);

        // Declarar el RecyclerView fuera del bloque try para que sea accesible en otros métodos
        RecyclerView recyclerViewResultados = findViewById(R.id.recyclerViewResultados);

        try {
            // Abrir la base de datos para lectura
            dbManager.open();

            // Obtener todos los resultados desde la base de datos
            List<Resultados> listaResultados = dbManager.obtenerTodosResultados();

            // Crear un adaptador personalizado
            ResultadosAdapter adapter = new ResultadosAdapter(listaResultados, this);

            // Configurar el RecyclerView y asignar el adaptador
            recyclerViewResultados.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewResultados.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
            // Manejar la excepción (mostrar mensaje de error, registrar en el log, etc.)
        } finally {
            // Cerrar la base de datos
            dbManager.close();
        }


        // Gestionar clics en el icono de retroceso del AppBar
        ImageView toolbarBackButton = findViewById(R.id.toolbarBackButton);
        toolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();  // Simula el comportamiento del botón de retroceso del sistema
            }
        });
    }

    // Método para generar el PDF
    private void generarPDF() {
        // Obtener todos los resultados desde el RecyclerView
        RecyclerView recyclerViewResultados = findViewById(R.id.recyclerViewResultados);
        ResultadosAdapter adapter = (ResultadosAdapter) recyclerViewResultados.getAdapter();

        // Verificar si el adaptador no es nulo y si tiene la lista de resultados
        if (adapter != null && adapter.getListaResultados() != null) {
            List<Resultados> listaResultados = adapter.getListaResultados();

            // Lógica para generar el PDF con los resultados
            PdfGenerator.generatePDF(listaResultados, this);
        } else {
            // Manejar el caso en el que el adaptador o la lista sean nulos
            Toast.makeText(this, "No hay resultados para generar el PDF", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para solicitar permisos antes de generar el PDF
// Método para solicitar permisos antes de generar el PDF
    public void onRequestPDF(View view) {
        // Verificar si se tienen los permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                        Environment.isExternalStorageManager())) {
            // Si los permisos ya están concedidos o se tiene el control del almacenamiento externo, generar el PDF
            generarPDF();
        } else {
            // Si no se tienen permisos, solicitarlos
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestPermissionLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    // Manejar la respuesta a la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {  // Puedes cambiar 1 por la constante que hayas usado al solicitar los permisos
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, ahora puedes generar el PDF
                generarPDF();
            } else {
                // Permiso denegado, muestra un mensaje o toma alguna acción
                Toast.makeText(this, "Permiso de escritura denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
