package com.example.ownerperson.mindr;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;


public class AddListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);
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

        Spinner alertRangeSpinner = (Spinner) findViewById(R.id.alert_spinner);
        ArrayAdapter<CharSequence> alert_adapter = ArrayAdapter.createFromResource(this,
                R.array.alert_ranges, android.R.layout.simple_spinner_item);
        alert_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertRangeSpinner.setAdapter(alert_adapter);

    }

    public void addListItem(View view){
        LinearLayout itemTextBoxes = (LinearLayout) findViewById(R.id.add_list_layout);
        EditText textBox = new EditText(this);
        textBox.setHint("Enter item here");
        itemTextBoxes.addView(textBox);
    }

    EditText listName;
    Switch approaching;
    Spinner alertRange;
    Spinner locationName;
    EditText item;
    Context context;

    public void submitListClick(View view) {

        String approachingNum;
        listName = (EditText) findViewById(R.id.list_name);
        locationName = (Spinner) findViewById(R.id.location_spinner);
        approaching = (Switch) findViewById(R.id.approaching);
        alertRange = (Spinner) findViewById(R.id.alert_spinner);

        System.out.println("This is approaching " + approaching);
        if(approaching != null){
            //Approaching is true, switch turned on, default
             approachingNum = "1";
        } else {
             approachingNum = "0";
        }

        String listNameText = listName.getText().toString();
        String locationNameText = locationName.getSelectedItem().toString();
        String alertRangeText = alertRange.getSelectedItem().toString();
        context = this;


        try {
            AsyncTask task = new PostListRequest().execute(listNameText, locationNameText, approachingNum, alertRangeText);
            task.get();

        } catch(Exception e){
            System.out.println(e.toString());
        }

        startActivity(new Intent(context, MainActivity.class));

    }

}
