package com.example.ownerperson.mindr;

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

public class ViewListItems extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_items);

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("listName");

        TextView title = (TextView) findViewById(R.id.list_title);
        title.setText(name);


        try {
            AsyncTask task =new GetListItemsRequest().execute(name);
            List<GetListItemsRequest.ItemLists> items = (List<GetListItemsRequest.ItemLists>) task.get();
            LinearLayout buttons = (LinearLayout) findViewById(R.id.list_item_names);
            for (GetListItemsRequest.ItemLists itemList : items) {
                for(String item : itemList.items){
                    Button listButton = new Button(this);
                    listButton.setText(item);
                    listButton.setLayoutParams(new Toolbar.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));


                    buttons.addView(listButton);

                }
            }
        } catch(Exception e){
            System.out.println(e.toString());
        }



    }
}
