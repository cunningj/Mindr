package com.example.ownerperson.mindr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewListItems extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_items);

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("listName");

        TextView title = (TextView) findViewById(R.id.list_title);
        title.setText(name);



    }
}
