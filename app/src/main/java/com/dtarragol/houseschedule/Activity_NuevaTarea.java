package com.dtarragol.houseschedule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Activity_NuevaTarea extends AppCompatActivity {

    public EditText txt_nombre, txt_descripcion;
    public Spinner sp_dias, sp_estancias;

    int posicion_spinner_estancias;
    String[] diasSemana = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
    String dia="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_tarea);
        txt_nombre=findViewById(R.id.txt_nombreTarea);
        txt_descripcion=findViewById(R.id.txt_descTarea);
        sp_dias=findViewById(R.id.spDias);
        sp_estancias=findViewById(R.id.spEstancias);
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase BD = admin.getReadableDatabase();
        String c = "SELECT * FROM "+ AdminSQLiteOpenHelper.TABLE_ESTANCIAS;
        Cursor consulta = BD.rawQuery(c, null);
        if(consulta.moveToFirst()) {
            List<String> opciones = new ArrayList<>();
            do{
                String temp_id_estancia = consulta.getString(0);
                String temp_name_estancia= consulta.getString(1);
                opciones.add(temp_id_estancia + ". " + temp_name_estancia);
            }while (consulta.moveToNext());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_estancias.setAdapter(adapter);
            BD.close();
        }
        sp_estancias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posicion_spinner_estancias=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Este método se llama cuando no se selecciona ningún elemento en el Spinner
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, diasSemana);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_dias.setAdapter(adapter);
        sp_dias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) dia="Lunes";
                if(position==1) dia="Martes";
                if(position==2) dia="Miercoles";
                if(position==3) dia="Jueves";
                if(position==4) dia="Viernes";
                if(position==5) dia="Sabado";
                if(position==6) dia="Domingo";
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Este método se llama cuando no se selecciona ningún elemento en el Spinner
            }
        });
    }
    public void guardarTarea(View v){
        AdminSQLiteOpenHelper dbHelper = new AdminSQLiteOpenHelper(Activity_NuevaTarea.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues modificacion = new ContentValues();
        modificacion.put("nombre", txt_nombre.getText().toString());
        modificacion.put("descripcion", txt_descripcion.getText().toString());
        modificacion.put("estancia_id", posicion_spinner_estancias+1);
        if(!dia.equals("")){
            modificacion.put("diayhora", dia);
        }else{
            //CODIGO DE AVISO!!!!!
            Toast.makeText(this, "Selecciona un dia", Toast.LENGTH_LONG).show();
        }
        long newRowId = db.insert(AdminSQLiteOpenHelper.TABLE_ACTIVIDADES,null, modificacion);
        if(newRowId==-1){
            Toast.makeText(this, "¡No se ha guardado!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "¡Tarea guardada!", Toast.LENGTH_LONG).show();
        }

        db.close();
        Intent intent = new Intent(this, Activity_Tareas.class);
        startActivity(intent);
        finish();
    }
    public void volver(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}