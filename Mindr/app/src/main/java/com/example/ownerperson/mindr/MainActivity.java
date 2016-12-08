package com.example.ownerperson.mindr;

import android.*;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.model.people.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;
import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<Status> {



    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final int REQ_PERMISSION = 999;


    //protected ArrayList<Geofence> mGeofenceList;

    Context context;

    public static final String baseURL = "http://10.0.2.2:3000/";

    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG!!!";
    // Create a Intent send by the notification
    public static Intent makeNotificationIntent(Context context, String msg) {
        System.out.println("INSTIDE MAKE NOTIFICATION INTENT LINE 60!!!");
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

        //mGeofenceList = new ArrayList<Geofence>(mGeofenceList);

        context = this;
        int listID = 1;

        try {
            AsyncTask task = new HttpGetStringList().execute(MainActivity.baseURL + "api/listPrefs");
            List<String> activities = (List<String>) task.get();
            final LinearLayout buttons = (LinearLayout) findViewById(R.id.list_buttons);
            for (String activity : activities) {
                final AppCompatActivity self = this;

                //Create new linear layout (creates line break)
                final LinearLayout listLayout = new LinearLayout(this);
                buttons.addView(listLayout);
                listLayout.setOrientation(LinearLayout.HORIZONTAL);
                listLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                listLayout.setId(listID++);

                //Create list button
                final Button listButton = new Button(this);
                final String listName = activity;
                listButton.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
                listButton.setText(activity);
                int listWidth = 750;
                listButton.setLayoutParams(new Toolbar.LayoutParams(
                        listWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                //Create delete button
                final Button deleteButton = new Button(this);
                int deleteWidth = 130;
                deleteButton.setLayoutParams(new Toolbar.LayoutParams(
                        deleteWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                int img = R.drawable.delete_icon;
                deleteButton.setCompoundDrawablesWithIntrinsicBounds(img,0,0,0);
//                deleteButton.setBackgroundColor(R.color.delete_red);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {

                            AsyncTask task = new HttpDeleteRequest().execute(MainActivity.baseURL + "api/delete", listName, "list");
                            task.get();
                            listLayout.removeView(deleteButton);
                            listLayout.removeView(listButton);
                            deleteGeofences(listName);
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

                listLayout.addView(listButton);
                listLayout.addView(deleteButton);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }


    public void onResume() {
        super.onResume();
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

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.getDouble("Latitude") != 0.0) {
            final Double latitudeExtra = extras.getDouble("Latitude");
            final Double longitudeExtra = extras.getDouble("Longitude");
            final int approachingExtra = extras.getInt("Approaching");

            System.out.println("latitudeExtra: " + latitudeExtra);

            startGeofence(latitudeExtra, longitudeExtra);
        }


        Bundle deleteExtras = getIntent().getExtras();
        if (deleteExtras != null && deleteExtras.getString("DeletedListName") != null) {
            deleteGeofences(deleteExtras.getString("DeletedListName"));
            getIntent().removeExtra("DeletedListName");
        }





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


    // Get last known location
    public void getLastKnownLocation() {
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

    double currentLatitude;
    double currentLongitude;

    // Write location coordinates on UI
    private void writeActualLocation(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        System.out.println( "Lat: " + location.getLatitude() );
        System.out.println( "Long: " + location.getLongitude() );
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
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

    private static final long GEO_DURATION = NEVER_EXPIRE;
    //private static final String GEOFENCE_REQ_ID;
    //private static final float GEOFENCE_RADIUS = 1600.0f; // in meters

    // Create a Geofence
    private Geofence createGeofence( LatLng latLng ) {
        Log.d(TAG, "createGeofence");

        Bundle extras = getIntent().getExtras();

        final String GEOFENCE_REQ_ID = extras.getString("ListName");
        System.out.println("GEOFENCE REQ ID !!!!" + GEOFENCE_REQ_ID);

        final int approachingExtra = extras.getInt("Approaching");
        System.out.println("APPROACHING EXTRA WHEN CREATED " + approachingExtra);

        if(approachingExtra == 1) {
            System.out.println("APPROACHING EXTRA INSIDE IF: " + approachingExtra);
            //400.0f meters is .25mi
            return new Geofence.Builder()
                    .setRequestId(GEOFENCE_REQ_ID)
                    .setCircularRegion(latLng.latitude, latLng.longitude, 400.0f)
                    .setExpirationDuration(GEO_DURATION)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build();
        } else {
            return new Geofence.Builder()
                    .setRequestId(GEOFENCE_REQ_ID)
                    .setCircularRegion(latLng.latitude, latLng.longitude, 200.0f)
                    .setExpirationDuration(GEO_DURATION)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
        }
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest( Geofence geofence ) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }

    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;
    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent!!!");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( this , GeofenceTransitionService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
            System.out.println("SUCCESS FROM ON RESULT - ADDED GEOFENCE");
        } else {
            // inform about fail
        }
    }


    // Start Geofence creation process
    private void startGeofence(Double lat, Double lng) {
        Log.i(TAG, "startGeofence()");
        System.out.println("LAT LNG INSIDE START GEOFENCE: " + lat + " " + lng);
        LatLng coords = new LatLng(lat, lng);
        System.out.println("COORDS INSIDE START GEOFENCE: " + coords);
        if( true ) {
            Geofence geofence = createGeofence( coords );
            GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
            System.out.println("geofence INSICE START GEOFENCE: " + geofence);
            addGeofence( geofenceRequest );
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }



    private void deleteGeofences(String listNameID) {
        ArrayList<String> geofenceIDs = new ArrayList<String>();
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            geofenceIDs.add(listNameID);
            LocationServices.GeofencingApi.removeGeofences(
                    googleApiClient,
                    geofenceIDs
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        System.out.println("GEOFENCE DELETED");
                    }
                }

            });

    }



    public void addLocationClick(View view) {
        Intent sendCurrentCoordsToMap = new Intent(this, AddLocationActivity.class);
        sendCurrentCoordsToMap.putExtra("currentLatitude", currentLatitude);
        sendCurrentCoordsToMap.putExtra("currentLongitude", currentLongitude);
        startActivity(sendCurrentCoordsToMap);
    }

    public void addListClick(View view) {
        startActivity(new Intent(context, AddListActivity.class));
    }



}