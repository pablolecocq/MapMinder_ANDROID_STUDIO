package com.example.gpsdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    // VARIABLES internas
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;


    private SQLiteDatabase bd;
    static String datosConsultaTitulo;
    static String datosConsultaDescripcion;
    static String datosConsultaLatitud;
    static String datosConsultaLongitud;


    // referencias de los elementos de la interfaz
    TextView lat, lon, altitud, precision, velocidad, sensor, actualizaciones, Direccion, NumMarcadores, btn_abrirMapa;

    EditText titulo,descripcion;
    Button btn_NuevoMarcador, btn_abrirLista,botonBorrar;

    Switch sw_On_Off_Localizacion, sw_gps;

    // API de google para servicios de localización, la mayoria de las funciones de la app usan esto
    FusedLocationProviderClient fusedLocationProviderClient;


    // variablree to member if we are tracking location or not
    boolean updateOn = false;

    //posicion actual
    private static Location currentLocation;





    // lista de los marcadores
    List<Location> savedLocations;

    // location request is a config file for all settings related to FusedLocationProviderClient
    LocationRequest locationRequest;

    LocationCallback locationCallBack;


    public static Location getCurrentLocation(){
        return currentLocation;
    }
    public void setCurrentLocation(Location currentLocation){
        this.currentLocation = currentLocation;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // damos valores a los elemento de la interfaz
        lat = findViewById(R.id.lat);
        lon = findViewById(R.id.lon);
        altitud = findViewById(R.id.altitud);
        precision = findViewById(R.id.precision);
        velocidad = findViewById(R.id.velocidad);
        sensor = findViewById(R.id.sensor);
        actualizaciones = findViewById(R.id.actualizar);
        Direccion = findViewById(R.id.Direccion);
        sw_gps = findViewById(R.id.sw_gps);
        sw_On_Off_Localizacion = findViewById(R.id.sw_On_Off_Localizacion);
        btn_NuevoMarcador = findViewById(R.id.btn_NuevoMarcador);
        btn_abrirLista = findViewById(R.id.btn_abrirLista);
        NumMarcadores = findViewById(R.id.NumMarcadores);
        btn_abrirMapa = findViewById(R.id.btn_abrirMapa);
        titulo = findViewById(R.id.titulo);
        descripcion = findViewById(R.id.descripcion);
        botonBorrar= (Button) findViewById(R.id.borrar);


        // propiedades de LocationRequest

        locationRequest = new LocationRequest();

        // intervalo de tiempo de checkeo de localizacion

        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);

        // lo mismo pero checkeo mas frecuente.

        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // evento que se ejecuta cada cierto tiempo
        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                // save the location

                updateUIValues(locationResult.getLastLocation());
            }
        };

        btn_NuevoMarcador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // obtener la localizacion por gps




                //Para el alta
                String longitudR=lon.getText().toString();
                String latitudR= lat.getText().toString();
                if(titulo.getText().toString().isEmpty() || descripcion.getText().toString().isEmpty() || longitudR.isEmpty() || latitudR.isEmpty()){
                    Toast.makeText(MainActivity.this, "Faltan datos por rellenar", Toast.LENGTH_SHORT).show();
                }else{//Paso el string a double y lo almaceno en una nueva variable local
                    double longitudF = Double.parseDouble(longitudR);
                    double latitudF = Double.parseDouble(latitudR);
                    insertData(titulo.getText().toString(),descripcion.getText().toString(),longitudF,latitudF);
                    // añadimos la localizacion a la lista
                    MyApplication myApplication = (MyApplication) getApplicationContext();
                    savedLocations = myApplication.getMisMarcadores();
                    savedLocations.add(currentLocation);
                    updateGPS();

                }
            }


        });


        botonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                baja(titulo,descripcion);
            }
        });

        btn_abrirLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarConsulta();
                Intent intent = new Intent(MainActivity.this, PantallaListaMarcadores.class);
                intent.putExtra("valorcontenidotitulo", datosConsultaTitulo);
                intent.putExtra("valorcontenidodescripcion", datosConsultaDescripcion);
                intent.putExtra("valorcontenidolatitud", datosConsultaLatitud);
                intent.putExtra("valorcontenidolongitud", datosConsultaLongitud);
                startActivity(intent);
            }
        });

        btn_abrirMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });


        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    // most accurate -use GPS
                    locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
                    sensor.setText("Uso el sensor GPS");
                } else {
                    locationRequest.setPriority(locationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    sensor.setText("Uso las torres de movil + Wifi");
                }

            }
        });

        sw_On_Off_Localizacion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sw_On_Off_Localizacion.isChecked()) {
                    // encender localizacion
                    startLocationUpdates();
                } else {
                    // apagar localizacion
                    stopLocationUpdates();
                }
            }
        });

        updateGPS();
        SQLiteBaseDatos conexion = new SQLiteBaseDatos(this);
        bd = conexion.getWritableDatabase();
        conexion.onCreate(bd);//Para asegurar que se crea la base de datos

    }// fin de Oncreate

    private void stopLocationUpdates() {
        actualizaciones.setText("La localización no está disponible");
        lat.setText("La localización no está disponible");
        lon.setText("La localización no está disponible");
        velocidad.setText("La localización no está disponible");
        Direccion.setText("La localización no está disponible");
        precision.setText("La localización no está disponible");
        altitud.setText("La localización no está disponible");
        sensor.setText("La localización no está disponible");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                ;
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(this, "Dar permisos de localización", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void startLocationUpdates() {
        // poner pedir permisos
        actualizaciones.setText("  Localización activada");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }



    private void updateGPS(){
        // pedir permisos GPS
        // obtencion de current location desde fused client
        //actualizar la interfaz con la nueva info

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            // el usuario ha proporcionado los permisos
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // hemos recibido los permisos. ponemos los valores en la pantalla
                    updateUIValues(location);
                    currentLocation = location;


                }
            });
        }
        else{
            // permisos denegados
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void updateUIValues(Location location){

        if (location != null){

        lat.setText(String.valueOf(location.getLatitude()));
        lon.setText(String.valueOf(location.getLongitude()));
        precision.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude()){
            altitud.setText(String.valueOf(location.getAltitude()));
        }
        else{
            altitud.setText("No disponible");
        }
        if(location.hasSpeedAccuracy()){
            velocidad.setText(String.valueOf(location.getSpeed()));
        }
        else{
            velocidad.setText("No disponible");
        }

        Geocoder geocoder = new Geocoder(MainActivity.this);

        try{// nota : usar para base de datos nombres repetidos
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
            Direccion.setText(addresses.get(0).getAddressLine(0));
        }
        catch (Exception e){
            Direccion.setText("Dirección no disponible");

        }

        MyApplication myApplication = (MyApplication) getApplicationContext();
        savedLocations = myApplication.getMisMarcadores();
        // mostrar el numero de marcadores guardados.
        NumMarcadores.setText(Integer.toString(savedLocations.size()));
        }

    }// nos gustaria añadir marcadores manteniedo pulsada la pantalla. para asi añadirlos sin tener que estar en usuario en esa

    ////////////////////////////////////////////////// metodos de la base de datos.
    private void insertData(String titulo, String descripcion, double longitud, double latitud) {
        ContentValues values = new ContentValues();
        values.put("titulo", titulo);
        values.put("descripcion", descripcion);
        values.put("longitud", longitud);
        values.put("latitud", latitud);
        // Insertar datos en la tabla
        bd.insert("lista", null, values);
        Toast.makeText(this, "Datos almacenados", Toast.LENGTH_SHORT).show();
    }
    private void realizarConsulta() {
        // Abrir la base de datos en modo lectura
        SQLiteBaseDatos conexion = new SQLiteBaseDatos(this);
        bd = conexion.getReadableDatabase();

        // Realizar la consulta
        Cursor cursor = bd.rawQuery("SELECT * FROM lista", null);

        // Procesar los resultados
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int tituloIndex = cursor.getColumnIndex("titulo");
                int descripcionIndex = cursor.getColumnIndex("descripcion");
                int longitudIndex = cursor.getColumnIndex("longitud");
                int latitudIndex = cursor.getColumnIndex("latitud");
                String titulo="";
                String descripcion="";
                String longitud="";
                String latitud="";
                do {
                    // Verificar que el índice sea mayor o igual a 0 antes de acceder a la columna
                    if (tituloIndex >= 0) {
                        titulo += cursor.getString(tituloIndex)+ "\n"+"\n";

                    }

                    if (descripcionIndex >= 0) {
                        descripcion += cursor.getString(descripcionIndex)+"\n"+"\n";

                    }

                    if (longitudIndex >= 0) {
                        longitud += String.valueOf(cursor.getDouble(longitudIndex))+"\n"+"\n";

                    }

                    if (latitudIndex >= 0) {
                        latitud += String.valueOf(cursor.getDouble(latitudIndex))+"\n"+"\n";

                    }
                    //datosConsulta=("Titulo: " + titulo + "\n Descripcion: " + descripcion +
                    //"\n Longitud: " + longitud + "\n Latitud: " + latitud);
                    datosConsultaTitulo=( titulo );
                    datosConsultaDescripcion=(descripcion);
                    datosConsultaLatitud=(latitud);
                    datosConsultaLongitud=(longitud);
                    //datosConsultaLongitud=("Longitud: \n" + longitud);
                } while (cursor.moveToNext());

            }
            // Cerrar el cursor
            cursor.close();
        } else {
            Toast.makeText(this, "No se encontraron resultados", Toast.LENGTH_SHORT).show();
        }
        // Cerrar la base de datos
    }
    public void baja(EditText titulo, EditText descripcion) {
        SQLiteBaseDatos conexion = new SQLiteBaseDatos(this);
        bd = conexion.getReadableDatabase();
        String tituloR = titulo.getText().toString();
        int cant = bd.delete("lista", "titulo='" + tituloR + "'",
                null);
        bd.close();
        titulo.setText("");
        descripcion.setText("");

        if (cant == 1)
            Toast.makeText(this, "Recordatorio eliminado",
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "No hay recordatorio con ese título",
                    Toast.LENGTH_SHORT).show();
    }

}