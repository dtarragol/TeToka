package com.dtarragol.houseschedule;

import static com.dtarragol.houseschedule.AdminSQLiteOpenHelper.TABLE_ACTIVIDADES;
import static com.dtarragol.houseschedule.AdminSQLiteOpenHelper.TABLE_ESTANCIAS;
import static com.dtarragol.houseschedule.AdminSQLiteOpenHelper.TABLE_USUARIOS;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Activity_Tareas extends AppCompatActivity {
    Button BT;
    LinearLayout lista;
    List<Integer> IDS = new ArrayList<>();
    List<Integer> IDS_linea = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    List<String> descripciones = new ArrayList<>();
    List<String> dias = new ArrayList<>();

    Dialog mDialog;
    Button btn_pop1, btn_pop2;
    int numeroBoton=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas);
        lista=findViewById(R.id.LL2);
        Inicializar();
    }
    public void Inicializar(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase BD = admin.getReadableDatabase();
        String c = "SELECT * FROM "+ TABLE_ACTIVIDADES;
        Cursor consulta = BD.rawQuery(c, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        if(consulta.moveToFirst()) {
            do {
                String tempID=consulta.getString(0);
                String temp_nombre = consulta.getString(1);
                String tempDesc=consulta.getString(2);
                String tempDia=consulta.getString(3);

                String temp_ID_E= consulta.getString(4);
                String c2 = "SELECT * FROM "+ TABLE_ESTANCIAS+" WHERE ID="+temp_ID_E;
                Cursor consulta2 = BD.rawQuery(c2, null);
                String temp_nombre_estancia="";
                if(consulta2.moveToFirst()) {
                    temp_nombre_estancia= consulta2.getString(1);
                }
                LinearLayout rowLayout = new LinearLayout(this);
                rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);


                rowLayout.setId(View.generateViewId());
                int RLID = rowLayout.getId();
                IDS_linea.add(RLID);

                BT = new Button(this);
                String title=temp_nombre + " ("+ temp_nombre_estancia +")";
                BT.setText(title);

                titles.add(title);
                descripciones.add(tempDesc);
                dias.add(tempDia);

                int color = getResources().getColor(R.color.box);
                BT.setBackgroundColor(color);

                int colorT = getResources().getColor(R.color.oscuro_azulado);
                BT.setTextColor(colorT);

                int marginInPixels = 10; // Obtén el tamaño del margen desde los recursos
                layoutParams.setMargins(0, 0, 0, marginInPixels); // Establece el margen en la parte inferior
                BT.setLayoutParams(layoutParams);
                BT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog = new Dialog(Activity_Tareas.this);
                        mDialog.setContentView(R.layout.popup_tarea);
                        btn_pop1=mDialog.findViewById(R.id.btn_pop1);
                        btn_pop2=mDialog.findViewById(R.id.btn_pop2);
                        int id_esteBoton= view.getId();

                        String a="";
                        String b="";

                        for (int i=0; i< IDS.size();i++){
                            int buttonId = IDS.get(i);
                            if (buttonId == id_esteBoton) {
                                a= titles.get(i);
                                b= dias.get(i)+"--"+descripciones.get(i);
                                break;
                            }
                        }
                        TextView nombreTarea = mDialog.findViewById(R.id.NombreTarea);
                        TextView descripcionTarea = mDialog.findViewById(R.id.descripcion);

                        nombreTarea.setText(a);
                        descripcionTarea.setText(b);
                        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        mDialog.show();


                        btn_pop1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Tareas.this);
                                builder.setMessage("¿Quieres eliminar permanentemente esta tarea?")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                AdminSQLiteOpenHelper dbHelper = new AdminSQLiteOpenHelper(Activity_Tareas.this);
                                                int numeroRegistros= dbHelper.numeroRegistros(dbHelper.getReadableDatabase(),TABLE_ACTIVIDADES);
                                                if(numeroRegistros>1){
                                                    int posicion=-1;
                                                    for (int i=0; i< IDS.size();i++){
                                                        int buttonId = IDS.get(i);
                                                        if (buttonId == id_esteBoton) {
                                                            posicion = i; // Guarda la posición donde se encontró la coincidencia
                                                            break; // Termina el bucle, ya se encontró la coincidencia
                                                        }
                                                    }
                                                    dbHelper.eliminarTarea(dbHelper.getWritableDatabase(),posicion+1,Activity_Tareas.this);
                                                    Intent intent = new Intent(Activity_Tareas.this, Activity_Tareas.class);
                                                    startActivity(intent);
                                                    finish();
                                                }else{
                                                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                                                    database.delete(AdminSQLiteOpenHelper.TABLE_ACTIVIDADES, null, null);
                                                    database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"+ AdminSQLiteOpenHelper.TABLE_ACTIVIDADES+"'");

                                                    database.close();
                                                    Toast.makeText(Activity_Tareas.this,"ÚLTIMA TAREA ELIMINADA", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(Activity_Tareas.this, Activity_Tareas.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
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
                        });
                        btn_pop2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                            }
                        });



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
    public void creacTarea(View v){
        Intent intent = new Intent(this, Activity_NuevaTarea.class);
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