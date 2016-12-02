package com.example.ownerperson.mindr;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddLocationActivity extends AppCompatActivity {

    //Figure out what Tag is? Randomly added to pacify red squiggly line overlords
    private static final String TAG = "AddLocationActivity";
    private GoogleMap mMap;
    final AppCompatActivity self = this;

    EditText locationName;
    String latString;
    String lngString;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        FragmentManager fmanager = getSupportFragmentManager();
        Fragment fragment = fmanager.findFragmentById(R.id.map);
        SupportMapFragment supportmapfragment = (SupportMapFragment)fragment;
        supportmapfragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //System.out.println("onMapReady callback");
                mMap = googleMap;

                // Add current location marker here

            }


        });


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                Log.i(TAG, "Lat/log " + place.getLatLng());
                Log.i(TAG, "ID " + place.getId());

                String coord = place.getLatLng().toString();
                latString = coord.substring(coord.indexOf("(") + 1,coord.indexOf(","));
                double lat = Double.parseDouble(latString);

                lngString = coord.substring(coord.indexOf(",") + 1,coord.indexOf(")"));
                double lng = Double.parseDouble(lngString);

                LatLng newLocation = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions()
                        .position(newLocation)
                        .title(place.getName().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }

    public void confirmLocationClick(View view) {

        context = this;

        locationName = (EditText) findViewById(R.id.location_name);

        String locationNameText = locationName.getText().toString();

        try {
            AsyncTask task = new PostLocationRequest().execute(locationNameText, latString, lngString);
            task.get();

        } catch(Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
        }

        startActivity(new Intent(context, MainActivity.class));
    }


}
