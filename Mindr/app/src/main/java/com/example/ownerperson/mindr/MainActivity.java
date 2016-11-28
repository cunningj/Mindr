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
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String baseURL = "http://10.0.2.2:3000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onResume() {
        super.onResume();

        try {
            AsyncTask task =new OkHttpRequests().execute();
            List<OkHttpRequests.MainActivityLists> lists = (List<OkHttpRequests.MainActivityLists>) task.get();
            LinearLayout buttons = (LinearLayout) findViewById(R.id.list_buttons);
            for (OkHttpRequests.MainActivityLists listName : lists) {
                for(String name : listName.names){
                    Button listButton = new Button(this);
                    listButton.setText(name);
                    listButton.setLayoutParams(new Toolbar.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    final AppCompatActivity self = this;
                    listButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            startActivity(new Intent(self, ViewListItems.class));
                        }

                    });

                    buttons.addView(listButton);

                }
            }
        } catch(Exception e){
            System.out.println(e.toString());
        }



    }

}
