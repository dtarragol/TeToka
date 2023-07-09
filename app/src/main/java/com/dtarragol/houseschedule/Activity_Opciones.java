package com.dtarragol.houseschedule;

import static com.dtarragol.houseschedule.AdminSQLiteOpenHelper.TABLE_ESTANCIAS;
import static com.dtarragol.houseschedule.AdminSQLiteOpenHelper.TABLE_USUARIOS;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Activity_Opciones extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);
    }
    public void borrarUsuarios(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Está seguro de que desea eliminar todos los usuarios?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AdminSQLiteOpenHelper dbHelper = new AdminSQLiteOpenHelper(Activity_Opciones.this);
                        SQLiteDatabase database = dbHelper.getWritableDatabase();
                        database.delete(AdminSQLiteOpenHelper.TABLE_USUARIOS, null, null);
                        database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"+ AdminSQLiteOpenHelper.TABLE_USUARIOS+"'");

                        database.close();
                        //lista.removeAllViews();
                        Toast.makeText(Activity_Opciones.this,"USUARIOS ELIMINADOS", Toast.LENGTH_LONG).show();

                        //inicicializar();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Cerrar el diálogo
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void borrarTareas(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Está seguro de que desea eliminar todas las tareas?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AdminSQLiteOpenHelper dbHelper = new AdminSQLiteOpenHelper(Activity_Opciones.this);
                        SQLiteDatabase database = dbHelper.getWritableDatabase();
                        database.delete(AdminSQLiteOpenHelper.TABLE_ACTIVIDADES, null, null);
                        database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"+ AdminSQLiteOpenHelper.TABLE_ACTIVIDADES+"'");

                        database.close();
                        //lista.removeAllViews();
                        Toast.makeText(Activity_Opciones.this,"TAREAS ELIMINADAS", Toast.LENGTH_LONG).show();

                        //inicicializar();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Cerrar el diálogo
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void borrarEstancias(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Está seguro de que desea eliminar todas las estancias?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AdminSQLiteOpenHelper dbHelper = new AdminSQLiteOpenHelper(Activity_Opciones.this);
                        SQLiteDatabase database = dbHelper.getWritableDatabase();
                        database.delete(TABLE_ESTANCIAS, null, null);
                        database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"+ AdminSQLiteOpenHelper.TABLE_ESTANCIAS+"'");

                        database.close();
                        //lista.removeAllViews();
                        Toast.makeText(Activity_Opciones.this,"ESTANCIAS ELIMINADAS", Toast.LENGTH_LONG).show();

                        //inicicializar();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Cerrar el diálogo
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void CrearAct(View v){
        Intent intent = new Intent(this, Activity_NuevaTarea.class);
        startActivity(intent);
        finish();
    }
    public void CrearEstancia(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nombre estancia: ");
        final EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = editText.getText().toString();
                if(!nombre.equals("")){
                    AdminSQLiteOpenHelper dbHelper = new AdminSQLiteOpenHelper(Activity_Opciones.this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues modificacion = new ContentValues();
                    modificacion.put("nombre", nombre);
                    long newRowId = db.insert(TABLE_ESTANCIAS,null, modificacion);
                    if(newRowId==-1){
                        Toast.makeText(Activity_Opciones.this, "No se ha guardado la estancia", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(Activity_Opciones.this, "¡Estancia guardada!", Toast.LENGTH_LONG).show();
                    }
                    db.close();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void volver(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}