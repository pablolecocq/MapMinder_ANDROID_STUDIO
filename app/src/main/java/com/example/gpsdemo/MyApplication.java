package com.example.gpsdemo;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private static MyApplication singleton;

    public List<Location> getMisMarcadores() {
        return misMarcadores;
    }

    public void setMisMarcadores(List<Location> misMarcadores) {
        this.misMarcadores = misMarcadores;
    }

    private List<Location> misMarcadores;
    public MyApplication getSingleton(){
        return singleton;
    }
    public void onCreate(){
        super.onCreate();
        singleton = this;
        misMarcadores = new ArrayList<>();

    }

}// por ahora los marcadores se guardan en esta lista. Si usamos la base de datos en vez de esto
// como podemos poner por pantalla los datos de una forma parecida que en PantallaListaMarcadores
// tambien afecta al poner los marcadores en el mapa. Como esta ahora el programas

