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
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class AddListActivity extends AppCompatActivity {

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

    List<String> items = new ArrayList<String>();

    EditText previousItem;
    String previousText;
    TextView newTextView;


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

        System.out.println("This is items " + items);



    }

    EditText listName;
    Switch approaching;
    Spinner alertRange;
    Spinner locationName;
    Context context;
    EditText lastItem;

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

        System.out.println("here are items " + items);

        context = this;

        String[] baseParams = {listNameText, locationNameText, approachingNum};
        System.out.println("here are baseparams " + baseParams);
        List<String> params = new LinkedList<String>(Arrays.asList(baseParams));
        params.addAll(items);

        System.out.println("here are params " + params);
        try {
            AsyncTask task = new PostListRequest().execute((String[])params.toArray(new String[params.size()]));
            task.get();

        } catch(Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
        }

        startActivity(new Intent(context, MainActivity.class));

    }

}
