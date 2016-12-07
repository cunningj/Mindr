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

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;


public class AddListActivity extends AppCompatActivity {


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

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

    int resetValue = 1;
    public void addListItem(View view){
        LinearLayout itemTextBoxes = (LinearLayout) findViewById(R.id.add_list_layout);
        if(resetValue == 1) {
            TextView listTitle = new TextView(this);
            listTitle.setText("List Items:");
            listTitle.setTextSize(20);
            itemTextBoxes.addView(listTitle);
            resetValue++;
        }
        previousItem = (EditText) findViewById(R.id.list_item);
        System.out.println("This is previous item " + previousItem);
        previousText = previousItem.getText().toString();
        System.out.println("This is previous text " + previousText);

        if(!previousText.equals("")){
            items.add(previousText);
        }

        TextView textBox = new TextView(this);
        textBox.setText(previousText);
        textBox.setTextSize(15);
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
            PostListRequest.LatLngResponse resp= (PostListRequest.LatLngResponse) task.get();
            System.out.println("VICTORY lat : " + resp.latitude + " lng: " + resp.longitude + " approaching: " + resp.approaching);

            Intent sendCoordsToGeofence = new Intent(this, MainActivity.class);
            sendCoordsToGeofence.putExtra("Latitude", resp.latitude);
            sendCoordsToGeofence.putExtra("Longitude", resp.longitude);
            sendCoordsToGeofence.putExtra("Approaching", resp.approaching);
            startActivity(sendCoordsToGeofence);

        } catch(Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
        }

    }

}
