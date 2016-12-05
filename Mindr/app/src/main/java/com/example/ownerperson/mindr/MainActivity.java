package com.example.ownerperson.mindr;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;


public class MainActivity extends AppCompatActivity {



    Context context;

    public static final String baseURL = "http://10.0.2.2:3000/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);



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



    public void addLocationClick(View view) {
        startActivity(new Intent(context, AddLocationActivity.class));
    }

    public void addListClick(View view) {
        startActivity(new Intent(context, AddListActivity.class));
    }

    public void lookAtMapClick(View view) {
        startActivity(new Intent(context, MapsActivity.class));
    }


    public void onResume() {
        super.onResume();


    }

}
