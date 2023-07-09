package com.dtarragol.houseschedule;

import static com.dtarragol.houseschedule.AdminSQLiteOpenHelper.TABLE_ACTIVIDADES;
import static com.dtarragol.houseschedule.AdminSQLiteOpenHelper.TABLE_ESTANCIAS;
import static com.dtarragol.houseschedule.AdminSQLiteOpenHelper.TABLE_USUARIOS;

import static java.sql.Types.NULL;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Activity_Usuario extends AppCompatActivity {
    LinearLayout lista;
    Spinner sp_actividades;
    Button btn_eliminar, btn_guardar;
    RelativeLayout marco;
    List<String> opciones = new ArrayList<>();
    int posicion_spinner_actividades, id_us;
    TextView tvUsuario, tvActividad, tvDia, tvDescripcion, tvEstancia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        //boton qeu se deberia eliminar cuando hay una tarea asignada
        btn_guardar=findViewById(R.id.button_guardar);
        marco = findViewById(R.id.Marco);


        lista= findViewById(R.id.L_Alumno);

        Inicializar();
    }
    public void Inicializar(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase BD = admin.getReadableDatabase();
        Intent intent = getIntent();
        String id_usuario= intent.getStringExtra("id_usuario");
        id_us=Integer.parseInt(id_usuario);
        String nombre_usuario = intent.getStringExtra("nombre_usuario");
        String id_actividad_usuario = intent.getStringExtra("id_actividad_usuario");

        String c = "SELECT * FROM "+ TABLE_ACTIVIDADES+ " WHERE id="+id_actividad_usuario;
        Cursor consulta = BD.rawQuery(c, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        if(consulta.moveToFirst()) {
            marco.removeView(btn_guardar);
            do {
                String temp_nombre_actividad = consulta.getString(1);
                String temp_descripcion_actividad = consulta.getString(2);
                String temp_dia_actividad = consulta.getString(3);
                String temp_estanciaID_actividad = consulta.getString(4);

                String c_estancia = "SELECT * FROM "+ TABLE_ESTANCIAS+ " WHERE id="+temp_estanciaID_actividad;
                Cursor consulta2 = BD.rawQuery(c_estancia, null);
                if(consulta2.moveToFirst()){
                    do {
                        String nombre_estancia = consulta2.getString(1);
                        LinearLayout rowLayout = new LinearLayout(this);
                        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        rowLayout.setOrientation(LinearLayout.VERTICAL);

                        tvUsuario = new TextView(this);
                        tvUsuario.setText(id_usuario +". "+ nombre_usuario);
                        tvUsuario.setTextSize(20);
                        int color = getResources().getColor(R.color.box);
                        int color2 = getResources().getColor(R.color.oscuro_azulado);
                        int color3 = getResources().getColor(R.color.oscuro);
                        int color4 = getResources().getColor(R.color.white);
                        int color5 = getResources().getColor(R.color.luminoso);
                        tvUsuario.setBackgroundColor(color2);
                        tvUsuario.setTextColor(color4);

                        int marginInPixels = 10; // Obtén el tamaño del margen desde los recursos
                        layoutParams.setMargins(0, 0, 0, marginInPixels); // Establece el margen en la parte inferior
                        tvUsuario.setLayoutParams(layoutParams);


                        tvActividad = new TextView(this);
                        tvActividad.setText(temp_nombre_actividad + "(Estancia: "+nombre_estancia+")");
                        tvActividad.setTextSize(25);
                        tvActividad.setBackgroundColor(color);
                        tvActividad.setLayoutParams(layoutParams);

                        tvDescripcion = new TextView(this);
                        tvDescripcion.setText(temp_descripcion_actividad);
                        tvDescripcion.setTextSize(20);
                        tvDescripcion.setBackgroundColor(color);
                        tvDescripcion.setLayoutParams(layoutParams);

                        tvDia = new TextView(this);
                        tvDia.setText(temp_dia_actividad);
                        tvDia.setTextSize(25);
                        tvDia.setBackgroundColor(color3);
                        tvDia.setTextColor(color4);
                        tvDia.setLayoutParams(layoutParams);

                        btn_eliminar = new Button(this);
                        btn_eliminar.setText("¡Hecho!");
                        btn_eliminar.setTextSize(30);
                        btn_eliminar.setBackgroundColor(color5);
                        btn_eliminar.setLayoutParams(layoutParams);
                        btn_eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(Activity_Usuario.this);
                                SQLiteDatabase BD = admin.getReadableDatabase();
                                ContentValues modificacion = new ContentValues();
                                modificacion.put("actividad_id", NULL);
                                String whereClause = "id = ?";
                                String[] whereArgs = {String.valueOf(id_us)};
                                long newRowId = BD.update(TABLE_USUARIOS, modificacion,whereClause,whereArgs);
                                if(newRowId==-1){
                                    Toast.makeText(Activity_Usuario.this, "¡NO se ha modificado!", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(Activity_Usuario.this, "Tarea realizada y desvinculada del usuario", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Activity_Usuario.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                        rowLayout.addView(tvUsuario);
                        rowLayout.addView(tvActividad);
                        rowLayout.addView(tvDescripcion);
                        rowLayout.addView(tvDia);
                        rowLayout.addView(btn_eliminar);
                        lista.addView(rowLayout);
                    }while (consulta2.moveToNext());
                }
            }while (consulta.moveToNext());
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Usuario sin tarea");
            builder.setMessage("¿Quieres asignar una tarea a "+nombre_usuario+"?");
            builder.setPositiveButton("Asignar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    LinearLayout rowLayout = new LinearLayout(Activity_Usuario.this);
                    rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    int marginInPixels = 10; // Obtén el tamaño del margen desde los recursos
                    layoutParams.setMargins(0, 0, 0, marginInPixels); // Establece el margen en la parte inferior

                    LinearLayout linearLayout = new LinearLayout(Activity_Usuario.this);
                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    linearLayout.setOrientation(LinearLayout.VERTICAL);


                    tvUsuario = new TextView(Activity_Usuario.this);
                    tvUsuario.setText(id_usuario +". "+ nombre_usuario);
                    tvUsuario.setTextSize(20);
                    int color = getResources().getColor(R.color.box);
                    tvUsuario.setBackgroundColor(color);
                    tvUsuario.setLayoutParams(layoutParams);


                    sp_actividades= new Spinner(Activity_Usuario.this);
                    sp_actividades.setLayoutParams(layoutParams);
                    opciones = llenarSpinnerActividades();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_Usuario.this, android.R.layout.simple_spinner_item, opciones);


                    boolean SPisEmpty = adapter.getCount() == 0;
                    if(SPisEmpty){
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Usuario.this);
                        builder.setTitle("Faltan datos");
                        builder.setMessage("Para poder asignar tareas, primero hay que crear ESTANCIAS y/o TAREAS");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Activity_Usuario.this, Activity_Opciones.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        builder.setNegativeButton("Cancelar", null);
                        AlertDialog dialog2 = builder.create();
                        dialog2.show();
                    }


                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_actividades.setAdapter(adapter);

                    sp_actividades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            posicion_spinner_actividades=position;
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Este método se llama cuando no se selecciona ningún elemento en el Spinner
                        }
                    });


                    linearLayout.addView(tvUsuario);
                    linearLayout.addView(sp_actividades);
                    lista.addView(linearLayout);
                }
            });

// Configuración del botón Cancelar
            builder.setNegativeButton("Volver", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(Activity_Usuario.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                    dialog.dismiss(); // Cerrar el AlertDialog
                }
            });

// Crear y mostrar el AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();



        }
        BD.close();

    }
    private List<String> llenarSpinnerActividades(){
        List<String> opciones = new ArrayList<>();
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase BD = admin.getReadableDatabase();
        String c = "SELECT * FROM "+ TABLE_ACTIVIDADES;
        Cursor consulta = BD.rawQuery(c, null);
        if(consulta.moveToFirst()) {
            do{
                String temp_id_A = consulta.getString(0);
                String temp_name_A= consulta.getString(1);
                opciones.add(temp_id_A + ". " + temp_name_A);
            }while (consulta.moveToNext());
            BD.close();
        }
        return opciones;
    }
    public void guardarTareaAUsuario(View v){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase BD = admin.getReadableDatabase();
        ContentValues modificacion = new ContentValues();
        modificacion.put("actividad_id", posicion_spinner_actividades+1);
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(id_us)};
        long newRowId = BD.update(AdminSQLiteOpenHelper.TABLE_USUARIOS,modificacion, whereClause, whereArgs);
        if(newRowId==-1){
            Toast.makeText(this, "¡No se ha asignado la tarea!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "¡Tarea asignada!", Toast.LENGTH_LONG).show();
        }
        BD.close();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void volver(View v) {
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