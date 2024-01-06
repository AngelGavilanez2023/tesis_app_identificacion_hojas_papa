package com.example.base4.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBconexion extends SQLiteOpenHelper {
    //nombre de la base de datos
    private static final String name = "papa.db";
    private static final int version = 2;
    public DBconexion(@Nullable Context context) {
        super(context, name, null, version);
    }

    //aqui le vamos a crear la tabla
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DBmanager.TABLE_TRATAMIENTOS_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DBmanager.TABLE_TRATAMIENTO);

    }

}
