package com.example.ownerperson.mindr;

import android.content.Context;
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

public class ViewListItems extends AppCompatActivity {
    Context context;
    LinearLayout buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_items);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);


        Bundle extras = getIntent().getExtras();
        final String name = extras.getString("listName");

        TextView title = (TextView) findViewById(R.id.list_title);
        title.setText(name + " List");


        try {
            AsyncTask task =new GetListItemsRequest().execute(name);
            List<String> items = (List<String>) task.get();
            buttons = (LinearLayout) findViewById(R.id.list_item_names);

            for(String item : items){
                final String itemFinal = item;
                final Button listButton = new Button(this);

                listButton.setText(item);
                listButton.setLayoutParams(new Toolbar.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                buttons.addView(listButton);

                final Button deleteItemButton = new Button(this);
                buttons.addView(deleteItemButton);
                deleteItemButton.setText("Delete Item");
                deleteItemButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            AsyncTask task = new HttpDeleteRequest().execute(MainActivity.baseURL + "api/delete", itemFinal, "item", name);
                            System.out.println("This is final: " + itemFinal);
                            task.get();
                            buttons.removeView(deleteItemButton);
                            buttons.removeView(listButton);
                        } catch (Exception e) {
                            System.out.println(e.toString());
                            e.printStackTrace();
                        }
                    }
                });

            }
        } catch(Exception e){
            System.out.println(e.toString());
        }

    }


}
