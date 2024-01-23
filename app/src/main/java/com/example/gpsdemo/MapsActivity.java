package com.example.gpsdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.gpsdemo.databinding.ActivityMapsBinding;


import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<Location> marcadoresGuardados;
    private ActivityMapsBinding binding;

    List<Location> savedLocations;

    // creating a variable
    // for search view.
    SearchView searchView;

    // lista de los marcadores
   // List<Location> savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MyApplication myApplication = (MyApplication) getApplicationContext();
        marcadoresGuardados = myApplication.getMisMarcadores();// lista de marcadores guardados


        // funcionalidad barra buscador

        // initializing our search view.
        searchView = findViewById(R.id.idSearchView);

        // adding on query listener for our search view.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // on below line we are getting the
                // location name from search view.
                String location = searchView.getQuery().toString();

                // below line is to create a list of address
                // where we will store the list of all address.
                List<Address> addressList = null;

                // checking if the entered location is null or not.
                if (location != null || location.equals("")) {
                    // on below line we are creating and initializing a geo coder.
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // on below line we are getting the location
                    // from our list a first position.
                    Address address = addressList.get(0);

                    // on below line we are creating a variable for our location
                    // where we will add our locations latitude and longitude.
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    // on below line we are adding marker to that position.
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));

                    // below line is to animate camera to that position.
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // at last we calling our map fragment to update.
        mapFragment.getMapAsync(this);
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // añadimos un marcador en la etsii y hacemos zoom 40.440403666296504, -3.6902464221423683
        LatLng etsii = new LatLng(40.440403666296504, -3.6902464221423683);
        Location ETSII =new Location("locationA");
        ETSII.setLatitude(etsii.latitude);
        ETSII.setLongitude(etsii.longitude);
        mMap.addMarker(new MarkerOptions().position(etsii).title("ETSII está a :"+ calcDistancia(ETSII)+" metros"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(etsii));

        LatLng lastlocationPlaced = etsii;

        for(Location location: marcadoresGuardados){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            //markerOptions.title("Lat:"+location.getLatitude()+"Lon:"+location.getLongitude());

            Location startPoint=new Location("locationA");
            startPoint.setLatitude(MainActivity.getCurrentLocation().getLatitude());
            startPoint.setLongitude(MainActivity.getCurrentLocation().getLongitude());

            Location endPoint=new Location("locationA");
            endPoint.setLatitude(location.getLatitude());
            endPoint.setLongitude(location.getLongitude());

            double distance=startPoint.distanceTo(endPoint);



            markerOptions.title("El marcador está a : "+ distance +" metros");
            mMap.addMarker(markerOptions);

            lastlocationPlaced = latLng;

        }// si en vez de usar una lista usamos una base de datos, puedo poner por pantalla los marcadores con el mismo metodo de aqui arriba??

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastlocationPlaced,12.0f));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                //lets count the number of times the pin is clicked
                Integer clicks = (Integer) marker.getTag();
                if (clicks ==null){
                    clicks =0;
                }
                clicks ++;
                marker.setTag(clicks);
                Toast.makeText(MapsActivity.this, "Marker "+marker.getTitle()+ " was clicked "+ marker.getTag()+ " times",Toast.LENGTH_SHORT).show();
                return false;



            }
        });


            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {

                    MyApplication myApplication = (MyApplication) getApplicationContext();
                    savedLocations = myApplication.getMisMarcadores();
                    // mostrar el numero de marcadores guardados.




                    Location startPoint=new Location("nuevo marcador" + savedLocations.size());
                    startPoint.setLatitude(latLng.latitude);
                    startPoint.setLongitude(latLng.longitude);
                    savedLocations.add(startPoint);



                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("El marcador esta a "+ calcDistancia(startPoint)+" metros")
                            .snippet("Your marker snippet"));


                }
            });



    }
    // añadimos un marcador en la etsii y hacemos zoom 40.440403666296504, -3.6902464221423683
    public double calcDistancia(Location loc){
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(MainActivity.getCurrentLocation().getLatitude());
        startPoint.setLongitude(MainActivity.getCurrentLocation().getLongitude());

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(loc.getLatitude());
        endPoint.setLongitude(loc.getLongitude());

        double distance =startPoint.distanceTo(endPoint);
        return distance;


    }




}