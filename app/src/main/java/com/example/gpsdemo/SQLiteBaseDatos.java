package com.example.gpsdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteBaseDatos extends SQLiteOpenHelper {

    public SQLiteBaseDatos(Context context) {
        //administracion es el nombre de la base de datos
        super(context, "Administracion", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla
        String query = "CREATE TABLE IF NOT EXISTS lista(titulo TEXT PRIMARY KEY,descripcion TEXT, longitud REAL,latitud REAL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Puedes realizar acciones específicas si la versión de la base de datos cambia
    }
}
