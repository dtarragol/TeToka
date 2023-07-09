package com.dtarragol.houseschedule;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final int DATABE_VERSION = 1;
    private static final String DATABASE_NOMBRE = "HSC.db";
    public static final String TABLE_ACTIVIDADES = "t_ac";
    public static final String TABLE_ESTANCIAS = "t_es";
    public static final String TABLE_USUARIOS = "t_us";

    public AdminSQLiteOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NOMBRE, null, DATABE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ESTANCIAS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL" +
                ")");
        db.execSQL("CREATE TABLE " + TABLE_ACTIVIDADES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "descripcion TEXT NOT NULL," +
                "diayhora TEXT NOT NULL," +
                "estancia_id INTEGER NOT NULL," +
                "FOREIGN KEY (estancia_id) REFERENCES "+TABLE_ESTANCIAS+"(id)"+
                ")");
        db.execSQL("CREATE TABLE " + TABLE_USUARIOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "actividad_id INTEGER," +
                "FOREIGN KEY (actividad_id) REFERENCES "+TABLE_ACTIVIDADES+"(id)"+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVIDADES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ESTANCIAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    public void eliminarTarea(SQLiteDatabase db,int id, Activity activity){
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(id)};
        int rowsDeleted = db.delete(TABLE_ACTIVIDADES, whereClause, whereArgs);
        if (rowsDeleted > 0) {
            Toast.makeText(activity, "TAREA ELIMINADA", Toast.LENGTH_LONG).show();
            try {
                // Resto del código...
                db.execSQL("DROP TABLE IF EXISTS tabla2;");
                db.execSQL("CREATE TABLE tabla2 (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, descripcion TEXT NOT NULL, diayhora TEXT NOT NULL, estancia_id INTEGER NOT NULL, FOREIGN KEY (estancia_id) REFERENCES "+TABLE_ESTANCIAS+"(id))");

                // Resto del código...
            } catch (SQLException e) {
                // Manejar la excepción, por ejemplo, mostrar un mensaje de error
                Toast.makeText(activity, "Error al crear la tabla: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            db.execSQL("INSERT INTO tabla2 (nombre, descripcion, diayhora, estancia_id) SELECT nombre, descripcion, diayhora, estancia_id FROM "+TABLE_ACTIVIDADES+" WHERE id <> "+ id +";");
            db.execSQL("DROP TABLE "+TABLE_ACTIVIDADES+";");
            db.execSQL("ALTER TABLE tabla2 RENAME TO "+TABLE_ACTIVIDADES+";");
        } else {
            Toast.makeText(activity, "No se ha podido eliminar", Toast.LENGTH_LONG).show();
        }
        db.close();
    }
    @SuppressLint("Range")
    public int numeroRegistros(SQLiteDatabase db, String tabla){
        String query = "SELECT COUNT(*) AS count FROM "+tabla+"";
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("count"));
        }
        cursor.close();
        db.close();
        return count;
    }
}
