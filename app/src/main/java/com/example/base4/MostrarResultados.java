
package com.example.base4;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.base4.DB.DBmanager;
import com.example.base4.modelo.Resultados;
import java.util.List;




public class MostrarResultados extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_resultados);

        // Obtener instancia de DBmanager
        DBmanager dbManager = new DBmanager(this);

        try {
            // Abrir la base de datos para lectura
            dbManager.open();

            // Obtener todos los resultados desde la base de datos
            List<Resultados> listaResultados = dbManager.obtenerTodosResultados();

            // Crear un adaptador personalizado
            ResultadosAdapter adapter = new ResultadosAdapter(listaResultados);

            // Obtener el RecyclerView desde el diseño
            RecyclerView recyclerViewResultados = findViewById(R.id.recyclerViewResultados);

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
    }
}
