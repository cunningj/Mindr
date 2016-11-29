package com.example.ownerperson.mindr;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

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
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.alert_ranges, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);

    }

    public void addListItem(View view){
        LinearLayout itemTextBoxes = (LinearLayout) findViewById(R.id.add_list_layout);
        EditText textBox = new EditText(this);
        textBox.setHint("Enter item here");
        itemTextBoxes.addView(textBox);
    }


}
