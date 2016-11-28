package com.example.ownerperson.mindr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ViewListItems extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_items);

        System.out.println("Application Context is " + getApplicationContext().toString());
    }
}
