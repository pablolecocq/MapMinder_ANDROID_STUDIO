package com.example.gpsdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class PantallaListaMarcadores extends AppCompatActivity {

    ListView lv_marcadoresGuardados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_lista_marcadores);

        lv_marcadoresGuardados =findViewById(R.id.lv_wayPoints);

        MyApplication myApplication =(MyApplication) getApplicationContext();
        List<Location>savedLocations = myApplication.getMisMarcadores();

        lv_marcadoresGuardados.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1,savedLocations));


        Button botonvuelta = (Button) findViewById(R.id.botonvuelta);
        String recuperamos_variableTitulo_string = getIntent().getStringExtra("valorcontenidotitulo");
        String recuperamos_variableDescripcion_string = getIntent().getStringExtra("valorcontenidodescripcion");
        String recuperamos_variableLatitud_string = getIntent().getStringExtra("valorcontenidolatitud");
        String recuperamos_variableLongitud_string = getIntent().getStringExtra("valorcontenidolongitud");
        //TextView tablita = findViewById(R.id.tablita);
        TextView tituloTabla = findViewById(R.id.tituloTabla);
        TextView descripcionTabla = findViewById(R.id.descripcionTabla);
        TextView latitudTabla = findViewById(R.id.latitudTabla);
        TextView longitudTabla = findViewById(R.id.longitudTabla);
        //tablita.setText(recuperamos_variable_string);
        tituloTabla.setText(recuperamos_variableTitulo_string);
        descripcionTabla.setText(recuperamos_variableDescripcion_string);
        latitudTabla.setText(recuperamos_variableLatitud_string);
        longitudTabla.setText(recuperamos_variableLongitud_string);
        // Dividir la cadena en palabras
        botonvuelta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}