package com.example.ownerperson.mindr;

import android.*;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {



    Context context;

    public static final String baseURL = "http://10.0.2.2:3000/";

    private GoogleApiClient googleApiClient;
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
    // Create a Intent send by the notification
    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent( context, MainActivity.class );
        intent.putExtra( NOTIFICATION_MSG, msg );
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        createGoogleApi();

        context = this;

        try {
            AsyncTask task = new HttpGetStringList().execute(MainActivity.baseURL + "api/listPrefs");
            List<String> activities = (List<String>) task.get();
            final LinearLayout buttons = (LinearLayout) findViewById(R.id.list_buttons);
            for (String activity : activities) {
                    final AppCompatActivity self = this;
                    final Button listButton = new Button(this);
                    final String listName = activity;
                    listButton.setText(activity);
                    listButton.setLayoutParams(new Toolbar.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));

                    final Button deleteButton = new Button(this);
                    deleteButton.setLayoutParams(new Toolbar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                    deleteButton.setText("X");
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            try {

                                AsyncTask task = new HttpDeleteRequest().execute(MainActivity.baseURL + "api/delete", listName, "list");
                                task.get();
                                buttons.removeView(deleteButton);
                                buttons.removeView(listButton);
                            } catch (Exception e) {
                                System.out.println(e.toString());
                                e.printStackTrace();
                            }
                        }
                    });

                    final String buttonName = activity;
                    listButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            Intent viewListItems = new Intent(self, ViewListItems.class);
                            viewListItems.putExtra("listName", buttonName);
                            startActivity(viewListItems);
                        }

                    });

                    buttons.addView(listButton);
                    buttons.addView(deleteButton);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }


    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
    }

    // GoogleApiClient.ConnectionCallbacks connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");
        getLastKnownLocation();
    }

    // GoogleApiClient.ConnectionCallbacks suspended
    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    // GoogleApiClient.OnConnectionFailedListener fail
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

    private Location lastLocation;
    //...


    // Get last known location
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if ( checkPermission() ) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( lastLocation != null ) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        }
        else askPermission();
    }

    //ADDED BASED ON CODE EXAMPLE
    private final int REQ_PERMISSION = 999;

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                REQ_PERMISSION
        );
    }

    // Verify user's response of the permission requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case REQ_PERMISSION: {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    // Permission granted
                    getLastKnownLocation();

                } else {
                    // Permission denied
                    permissionsDenied();
                }
                break;
            }
        }
    }

    // App cannot work without the permissions
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
    }


    private LocationRequest locationRequest;
    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL =  1000;
    private final int FASTEST_INTERVAL = 900;

    // Start location Updates
    private void startLocationUpdates(){
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged ["+location+"]");
        lastLocation = location;
        writeActualLocation(location);
    }

    // Write location coordinates on UI
    private void writeActualLocation(Location location) {
        System.out.println( "Lat: " + location.getLatitude() );
        System.out.println( "Long: " + location.getLongitude() );
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }





    public void addLocationClick(View view) {
        startActivity(new Intent(context, AddLocationActivity.class));
    }

    public void addListClick(View view) {
        startActivity(new Intent(context, AddListActivity.class));
    }


    public void onResume() {
        super.onResume();


    }

}
