package com.example.alumnedam.aplicacio_client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGuardar = (Button) findViewById(R.id.button);
        btnGuardar.setOnClickListener(this);




    }

    @Override
    public void onClick(View view) {

        EditText editTextMatric = (EditText) findViewById(R.id.editText2);
        EditText editTextPass = (EditText) findViewById(R.id.editText);

        editTextMatric.getText();
        editTextPass.getText();
        SQLiteDatabase db;
        //Iniciamos la base de datos
        BDAutobus usdbh =
                new BDAutobus(this,"BDAutobus", null, 1);

        db = usdbh.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT MATRICULA, PASSWORD FROM Login WHERE (MATRICULA = '" + editTextMatric.getText() + "') AND (PASSWORD = '" + editTextPass.getText() + "')", null);
        TextView txtResultado = (TextView) findViewById(R.id.txtResultado);

        //if(c.getString(0).length()< 0) {
          //  txtResultado.setText("La select no devuelve nada! D:");
        //}
            TextView txtRes2 = (TextView) findViewById(R.id.textView2);
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya mas registros

            do {
                //comparar lo que devuelve la BD con lo que introduce el usuario por pantalla
                String cod = c.getString(0);
                String horini = c.getString(1);

                if(cod.getBytes()!= null){

                    txtRes2.setText("Usuario Logueado! :D");

                    startService(new Intent(MainActivity.this,
                            ServicioSegundoPlano.class));

                }
            } while (c.moveToNext());
        }else{
            txtRes2.setText("usuario no logeado D:");

        }
    }

}
