package com.example.ownerperson.mindr;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Context context;

    public static final String baseURL = "http://10.0.2.2:3000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        try {
            AsyncTask task = new HttpGetStringList().execute(MainActivity.baseURL + "api/listPrefs");
            List<String> activities = (List<String>) task.get();
            LinearLayout buttons = (LinearLayout) findViewById(R.id.list_buttons);
            for (String activity : activities) {
                    Button listButton = new Button(this);
                    listButton.setText(activity);
                    listButton.setLayoutParams(new Toolbar.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    final String buttonName = activity;
                    final AppCompatActivity self = this;
                    listButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            Intent viewListItems = new Intent(self, ViewListItems.class);
                            viewListItems.putExtra("listName", buttonName);
                            startActivity(viewListItems);
                        }

                    });

                    buttons.addView(listButton);
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


    public void onResume() {
        super.onResume();


    }

}
