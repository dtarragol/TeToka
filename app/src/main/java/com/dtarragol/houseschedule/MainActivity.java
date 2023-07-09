package com.dtarragol.houseschedule;

import static com.dtarragol.houseschedule.AdminSQLiteOpenHelper.TABLE_ESTANCIAS;
import static com.dtarragol.houseschedule.AdminSQLiteOpenHelper.TABLE_USUARIOS;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button BT;
    LinearLayout lista;
    List<Integer> IDS = new ArrayList<>();
    List<Integer> IDS_linea = new ArrayList<>();
    int numeroBoton=1;
    private boolean mostrarBanner;
    Dialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lista=findViewById(R.id.LL);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        mostrarBanner = sharedPreferences.getBoolean("mostrarBanner", true);
        Inicializar();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mostrarBanner) {
            //popup con explicacion de la app
            mDialog = new Dialog(this);
            mDialog.setContentView(R.layout.popup_inicio);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDialog.show();
            // Cambiar el estado de mostrarBanner a false
            mostrarBanner = false;
            // Guardar el nuevo estado utilizando SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("mostrarBanner", mostrarBanner);
            editor.apply();
        }
    }
    public void Inicializar(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase BD = admin.getReadableDatabase();
        String c = "SELECT * FROM "+ TABLE_USUARIOS;
        Cursor consulta = BD.rawQuery(c, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        if(consulta.moveToFirst()) {
            do {
                String temp_id = consulta.getString(0);
                String temp_nombre = consulta.getString(1);
                String temp_actividadId = consulta.getString(2);
                LinearLayout rowLayout = new LinearLayout(this);
                rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);


                rowLayout.setId(View.generateViewId());
                int RLID = rowLayout.getId();
                IDS_linea.add(RLID);

                BT = new Button(this);
                BT.setText(temp_nombre);
                int color = getResources().getColor(R.color.box);
                BT.setBackgroundColor(color);
                int colorT = getResources().getColor(R.color.oscuro_azulado);
                BT.setTextColor(colorT);

                int marginInPixels = 10; // Obtén el tamaño del margen desde los recursos
                layoutParams.setMargins(0, 0, 0, marginInPixels); // Establece el margen en la parte inferior

                BT.setLayoutParams(layoutParams);

                BT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Activity_Usuario.class);
                        intent.putExtra("id_usuario", temp_id);
                        intent.putExtra("nombre_usuario", temp_nombre);
                        intent.putExtra("id_actividad_usuario", temp_actividadId);
                        startActivity(intent);
                        finish();
                    }
                });
                BT.setId(View.generateViewId());
                int btnID= BT.getId();
                IDS.add(btnID);

                rowLayout.addView(BT);
                lista.addView(rowLayout);
                numeroBoton++;

            } while (consulta.moveToNext());
        }
        BD.close();
    }
    public void btnCrearUsuario(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nombre usuario: ");
        final EditText editText = new EditText(this);
        builder.setView(editText);


        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = editText.getText().toString();
                if(!nombre.equals("")){
                    AdminSQLiteOpenHelper dbHelper = new AdminSQLiteOpenHelper(MainActivity.this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues modificacion = new ContentValues();
                    modificacion.put("nombre", nombre);
                    long newRowId = db.insert(TABLE_USUARIOS,null, modificacion);
                    if(newRowId==-1){
                        Toast.makeText(MainActivity.this, "No se ha guardado el usuario", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this, "¡Usuario Guardado!", Toast.LENGTH_LONG).show();
                    }
                    db.close();
                    Inicializar();
                }



            }
        });


        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

// Muestra el AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void MenuOpciones(View v){
        Intent intent = new Intent(this, Activity_Opciones.class);
        startActivity(intent);
        finish();
    }
    public void MenuTareas(View v){
        Intent intent = new Intent(this, Activity_Tareas.class);
        startActivity(intent);
        finish();
    }
    public void salir(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar salida");
        builder.setMessage("¿Estás segur@ de que deseas salir de la aplicación?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
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
                    AdminSQLiteOpenHelper dbHelper = new AdminSQLiteOpenHelper(MainActivity.this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues modificacion = new ContentValues();
                    modificacion.put("nombre", nombre);
                    long newRowId = db.insert(TABLE_ESTANCIAS,null, modificacion);
                    if(newRowId==-1){
                        Toast.makeText(MainActivity.this, "No se ha guardado la estancia", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this, "¡Estancia guardada!", Toast.LENGTH_LONG).show();
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
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar salida");
        builder.setMessage("¿Estás segur@ de que deseas salir de la aplicación?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}