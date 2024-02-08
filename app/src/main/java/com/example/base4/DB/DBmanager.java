package com.example.base4.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.base4.modelo.Tratamiento;
import com.example.base4.modelo.Resultados;

import java.util.ArrayList;


public class DBmanager {
    //crear la tabla tratamiento
    public static final String TABLE_TRATAMIENTO = "tratamientos";
    public static final String ID = "_id";
    public static final String ENFERMEDAD = "enfermedad";
    public static final String APLICAR = "aplicar";
    public static final String MODO = "modo";
    public static final String FUNGICIDA = "fungicida";
    public static final String DOSIS = "dosis";

    //crear la tabla enfermedad
    public static final String TABLE_TRATAMIENTOS_CREATE = "CREATE TABLE tratamientos (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "enfermedad TEXT," +
            "aplicar TEXT," +
            "modo TEXT," +
            "fungicida TEXT," +
            "dosis TEXT)";

    private DBconexion _conexion;
    private SQLiteDatabase _basededatos;

    //constructor
    public DBmanager(Context context){
        _conexion = new DBconexion(context);
    }

    public DBmanager open() throws Exception{
        _basededatos = _conexion.getWritableDatabase();
        return this;
    }

    public void close(){
        _conexion.close();
    }

    //implementar los metodos CRUD
    public void insertarTratamiento(String enfermedad, String aplicar, String modo, String fungicida, String dosis){
        ContentValues cv = new ContentValues();
        cv.put(ENFERMEDAD, enfermedad);
        cv.put(APLICAR, aplicar);
        cv.put(MODO, modo);
        cv.put(FUNGICIDA, fungicida);
        cv.put(DOSIS, dosis);
        this._basededatos.insert(TABLE_TRATAMIENTO, null, cv);
        Log.d("inserción", "correcta");
    }



    //funcion para obtener los tratamientos desde la bbdd
    public Tratamiento obtenerTratamiento(String enfermedad) {
        Tratamiento tratamiento = null;

        try {
            // Abrir la base de datos para lectura
            SQLiteDatabase db = _conexion.getReadableDatabase();

            // Realizar la consulta para obtener el tratamiento
            String[] columnas = {ID, ENFERMEDAD, APLICAR, MODO, FUNGICIDA, DOSIS};
            String seleccion = ENFERMEDAD + "=?";
            String[] seleccionArgs = {enfermedad};

            Cursor cursor = db.query(TABLE_TRATAMIENTO, columnas, seleccion, seleccionArgs, null, null, null);

            // Verificar si se encontró el tratamiento
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(ID);
                int enfermedadIndex = cursor.getColumnIndex(ENFERMEDAD);
                int aplicarIndex = cursor.getColumnIndex(APLICAR);
                int modoIndex = cursor.getColumnIndex(MODO);
                int fungicidaIndex = cursor.getColumnIndex(FUNGICIDA);
                int dosisIndex = cursor.getColumnIndex(DOSIS);

                int id = cursor.getInt(idIndex);
                String enfermedadValue = cursor.getString(enfermedadIndex);
                String aplicarValue = cursor.getString(aplicarIndex);
                String modoValue = cursor.getString(modoIndex);
                String fungicidaValue = cursor.getString(fungicidaIndex);
                String dosisValue = cursor.getString(dosisIndex);

                tratamiento = new Tratamiento(id, enfermedadValue, aplicarValue, modoValue, fungicidaValue, dosisValue);
            }

            cursor.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tratamiento;
    }

    public static final String TABLE_RESULTADOS = "resultados";
    public static final String ID_RESULTADO = "_id";
    public static final String ENFERMEDAD_RESULTADO = "enfermedad";
    public static final String ACCURACY_RESULTADO = "accuracy";
    public static final String FECHA_HORA_RESULTADO = "fecha_hora";
    public static final String imagen_res_RESULTADO = "imagen_res";  // Cambiado de imagen_res_RESULTADO_PATH a imagen_res_RESULTADO
    public static final String TRATAMIENTO_RESULTADO = "tratamiento"; // Nuevo campo para tratamiento
    public static final String DOSIS_RESULTADO = "dosis"; // Nuevo campo para tratamiento
    public static final String APLICAR_RESULTADO = "aplicar"; // Nuevo campo para tratamiento


    public static final String TABLE_RESULTADOS_CREATE = "CREATE TABLE resultados (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "enfermedad TEXT," +
            "accuracy REAL," +
            "fecha_hora TEXT," +
            "imagen_res BLOB," +
            "tratamiento TEXT," +
            "dosis TEXT," +
            "aplicar TEXT)";  // Nuevo campo para tratamiento


    public void insertarResultado(String enfermedad, float accuracy, String fechaHora, byte[] imagen_res, String tratamiento, String dosis, String aplicar) {  // Nuevo campo para tratamiento
        ContentValues cv = new ContentValues();
        cv.put(ENFERMEDAD_RESULTADO, enfermedad);
        cv.put(ACCURACY_RESULTADO, accuracy);
        cv.put(FECHA_HORA_RESULTADO, fechaHora);
        cv.put(imagen_res_RESULTADO, imagen_res);
        cv.put(TRATAMIENTO_RESULTADO, tratamiento);  // Nuevo campo para tratamiento
        cv.put(DOSIS_RESULTADO, dosis);  // Nuevo campo para tratamiento
        cv.put(APLICAR_RESULTADO, aplicar);  // Nuevo campo para tratamiento
        this._basededatos.insert(TABLE_RESULTADOS, null, cv);
        Log.d("inserción resultado", "correcta");
    }



    //obtnener todos los resultados de la bbdd
    public ArrayList<Resultados> obtenerTodosResultados() {
        ArrayList<Resultados> resultados = new ArrayList<>();

        try {
            // Abrir la base de datos para lectura
            SQLiteDatabase db = _conexion.getReadableDatabase();

            // Realizar la consulta para obtener los resultados
            String[] columnas = {ID_RESULTADO, ENFERMEDAD_RESULTADO, ACCURACY_RESULTADO, FECHA_HORA_RESULTADO, imagen_res_RESULTADO, TRATAMIENTO_RESULTADO, DOSIS_RESULTADO, APLICAR_RESULTADO};  // Nuevo campo para tratamiento

            Cursor cursor = db.query(TABLE_RESULTADOS, columnas, null, null, null, null, null, null);

            // Verificar si se encontraron resultados
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(ID_RESULTADO);
                int enfermedadIndex = cursor.getColumnIndex(ENFERMEDAD_RESULTADO);
                int accuracyIndex = cursor.getColumnIndex(ACCURACY_RESULTADO);
                int fechaHoraIndex = cursor.getColumnIndex(FECHA_HORA_RESULTADO);
                int imagen_resIndex = cursor.getColumnIndex(imagen_res_RESULTADO); // Cambiado de imagen_resPathIndex a imagen_resIndex
                int tratamientoIndex = cursor.getColumnIndex(TRATAMIENTO_RESULTADO);  // Nuevo campo para tratamiento
                int dosisIndex = cursor.getColumnIndex(DOSIS_RESULTADO);  // Nuevo campo para tratamiento
                int aplicarIndex = cursor.getColumnIndex(APLICAR_RESULTADO);  // Nuevo campo para tratamiento

                do {
                    int id = cursor.getInt(idIndex);
                    String enfermedad = cursor.getString(enfermedadIndex);
                    float accuracy = cursor.getFloat(accuracyIndex);
                    String fechaHora = cursor.getString(fechaHoraIndex);
                    byte[] imagen_resBytes = cursor.getBlob(imagen_resIndex); // Cambiado de getString a getBlob
                    String tratamiento = cursor.getString(tratamientoIndex);  // Nuevo campo para tratamiento
                    String dosis = cursor.getString(dosisIndex);  // Nuevo campo para tratamiento
                    String aplicar = cursor.getString(aplicarIndex);  // Nuevo campo para tratamiento


                    Resultados resultado = new Resultados(id, enfermedad, String.valueOf(accuracy), fechaHora, imagen_resBytes, tratamiento, dosis, aplicar );  // Nuevo campo para tratamiento
                    resultados.add(resultado);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultados;
    }

    //funcion para eliminar un resultado
    public void eliminarResultadoDeBD(int resultadoId) {
        try {
            SQLiteDatabase db = _conexion.getWritableDatabase();
            db.delete(TABLE_RESULTADOS, ID_RESULTADO + "=?", new String[]{String.valueOf(resultadoId)});
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
