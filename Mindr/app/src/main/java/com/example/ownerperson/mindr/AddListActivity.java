package com.example.ownerperson.mindr;

import android.*;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class AddListActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private GoogleApiClient googleApiClient;

    List<String> items = new ArrayList<String>();

    EditText previousItem;
    String previousText;
    TextView newTextView;
    EditText listName;
    Switch approaching;
    Spinner alertRange;
    Spinner locationName;
    Context context;
    EditText lastItem;
    private static final String TAG = AddListActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        createGoogleApi();
        List<String> locations = new ArrayList<String>();
        try {
            AsyncTask task = new HttpGetStringList().execute(MainActivity.baseURL + "api/locations");
            locations = (List<String>) task.get();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        Spinner locationSpinner = (Spinner) findViewById(R.id.location_spinner);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

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








    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = 16000.0f; // in meters

    // Create a Geofence
    private Geofence createGeofence(LatLng latLng, float radius ) {
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration( GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest(Geofence geofence ) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }



    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;
    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        System.out.println("before setting intent to go to GeofenceTransitionService.class");
        Intent intent = new Intent( this, GeofenceTransitionService.class);
        System.out.println("after setting intent to go to GeofenceTransitionService.class");
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
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

    // Start Geofence creation process

    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
            System.out.println("On Result Callback?");
        } else {
            // inform about fail
        }
    }



    //     Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission()) {
            System.out.println("inside addGeofence first line");
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(AddListActivity.this);
            System.out.println("inside addGeofence last line");

        }
    }

    public void startGeofence(double lat, double lng) {
        Log.i(TAG, "startGeofence()");
        ///// we will need to do our get request here for the coordinates
        LatLng lastCoordinates = new LatLng(lat, lng);
        System.out.println("lastCoordinates: " + lastCoordinates);
        if( true ) {
            Geofence geofence = createGeofence( lastCoordinates, GEOFENCE_RADIUS );
            System.out.println("geofence baby: " + geofence);

            GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
            addGeofence( geofenceRequest );

            System.out.println("lastCoordinates " + lastCoordinates);
            System.out.println("geofenceRequest " + geofenceRequest);
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }




    public void addListItem(View view){
        LinearLayout itemTextBoxes = (LinearLayout) findViewById(R.id.add_list_layout);
        previousItem = (EditText) findViewById(R.id.list_item);
        System.out.println("This is previous item " + previousItem);
        previousText = previousItem.getText().toString();
        System.out.println("This is previous text " + previousText);

        if(!previousText.equals("")){
            items.add(previousText);
        }

        TextView textBox = new TextView(this);
        textBox.setText(previousText);
        itemTextBoxes.addView(textBox);
        previousItem.setText("");

    }

    public void submitListClick(View view) {

        String approachingNum;
        listName = (EditText) findViewById(R.id.list_name);
        locationName = (Spinner) findViewById(R.id.location_spinner);
        approaching = (Switch) findViewById(R.id.approaching);
        lastItem = (EditText) findViewById(R.id.list_item);


        //approaching = approaching.getSelectedItem().toString();


        if(approaching.isChecked()){
            //Approaching is true, switch turned on, default
             approachingNum = "1";
        } else {
             approachingNum = "0";
        }

        System.out.println(approachingNum);

        String listNameText = listName.getText().toString();
        String locationNameText = locationName.getSelectedItem().toString();

        items.add(lastItem.getText().toString());

        context = this;

        String[] baseParams = {listNameText, locationNameText, approachingNum};
        List<String> params = new LinkedList<String>(Arrays.asList(baseParams));
        params.addAll(items);

        try {
            AsyncTask task = new PostListRequest().execute((String[])params.toArray(new String[params.size()]));
            task.get();
            startGeofence(46.8686846, -114.009869);


        } catch(Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
        }


        // get route
        // get lat long
        // set as extras and pass back to main activity lat/long vars
        startActivity(new Intent(context, MainActivity.class));

    }

}
