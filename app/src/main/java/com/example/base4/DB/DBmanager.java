package com.example.base4.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBmanager {
    //crear la tabla tratamiento
    public static final String TABLE_TRATAMIENTO = "tratamientos";
    public static final String ID = "_id";
    public static final String ENFERMEDAD = "enfermedad";
    public static final String APLICAR = "aplicar";

    //crear la tabla enfermedad
    public static final String TABLE_TRATAMIENTOS_CREATE = "CREATE TABLE tratamientos (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "enfermedad TEXT," +
            "aplicar TEXT)";

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
    public void insertarTratamiento(String enfermedad, String aplicar){
        ContentValues cv = new ContentValues();
        cv.put(ENFERMEDAD, enfermedad);
        cv.put(APLICAR, aplicar);
        this._basededatos.insert(TABLE_TRATAMIENTO, null, cv);
        Log.d("inserción", "correcta");
    }


    //funcion para obtener los tratamientos desde la bbdd
    public String obtenerTratamiento(String enfermedad) {
        String tratamiento = null;

        try {
            // Abrir la base de datos para lectura
            SQLiteDatabase db = _conexion.getReadableDatabase();

            // Realizar la consulta para obtener el tratamiento
            String[] columnas = {APLICAR};
            String seleccion = ENFERMEDAD + "=?";
            String[] seleccionArgs = {enfermedad};

            Cursor cursor = db.query(TABLE_TRATAMIENTO, columnas, seleccion, seleccionArgs, null, null, null);

            // Verificar si se encontró el tratamiento
            int columnIndex = cursor.getColumnIndex(APLICAR);
            if (columnIndex != -1 && cursor.moveToFirst()) {
                tratamiento = cursor.getString(columnIndex);
            }

            cursor.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tratamiento;
    }




}
