package com.example.ownerperson.mindr;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public String coord;

    private double lat;
    private double lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //get latitude
//        Bundle extras = getIntent().getExtras();
//        coord = extras.getString("coordinates");
//        String latString = coord.substring(coord.indexOf("(") + 1,coord.indexOf(","));
//        System.out.println("THIS IS LAT " + latString);
//        lat = Double.parseDouble(latString);
//        System.out.println("THIS IS LAT DOUBLE " + lat);
//
//        //get longitude
//        String lngString = coord.substring(coord.indexOf(",") + 1,coord.indexOf(")"));
//        System.out.println("THIS IS LONG " + lngString);
//        lng = Double.parseDouble(lngString);
//        System.out.println("THIS IS LAT DOUBLE " + lng);
//
//        System.out.println("these are coordinates " + coord);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng missoula = new LatLng(46.8787, -113.9966);

        LatLng GFS = new LatLng(lat, lng);

        mMap.addMarker(new MarkerOptions()
                .position(missoula)
                .title("Home Sweet Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(missoula));

        mMap.addMarker(new MarkerOptions()
                .position(GFS)
                .title("Good Food Store"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(GFS));
    }


}

