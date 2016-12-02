package com.example.ownerperson.mindr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class AddLocationActivity extends AppCompatActivity {

    //Figure out what Tag is? Randomly added to pacify red squiggly line overlords
    private static final String TAG = "AddLocationActivity";

    final AppCompatActivity self = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                Log.i(TAG, "Lat/log " + place.getLatLng());
                Log.i(TAG, "ID " + place.getId());

                Intent goToMap = new Intent(self, MapsActivity.class);
                //this is working to send over the latlng screen to map activity, need to know how to get information
                // in a format that we can use to addmarker with latlng
                goToMap.putExtra("coordinates", place.getLatLng().toString());
                startActivity(goToMap);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }



}
