package com.example.ownerperson.mindr;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ViewListItems extends AppCompatActivity {
    Context context;
    LinearLayout buttons;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_items);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);


        Bundle extras = getIntent().getExtras();
        name = extras.getString("listName");

        TextView title = (TextView) findViewById(R.id.list_title);
        title.setText(name + " List");


        try {
            AsyncTask task =new GetListItemsRequest().execute(name);
            List<String> items = (List<String>) task.get();
            buttons = (LinearLayout) findViewById(R.id.list_item_names);
            int listID = 1;
            for(String item : items){

                final LinearLayout itemLayout = new LinearLayout(this);
                buttons.addView(itemLayout);
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                itemLayout.setId(listID++);
                final String itemFinal = item;
                final Button listButton = new Button(this);

                listButton.setText(item);
                listButton.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
                int listWidth = 750;
                listButton.setLayoutParams(new Toolbar.LayoutParams(
                        listWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT));


                final Button deleteItemButton = new Button(this);
                int img = R.drawable.delete_icon;
                int deleteWidth = 130;
                deleteItemButton.setLayoutParams(new Toolbar.LayoutParams(
                        deleteWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                deleteItemButton.setCompoundDrawablesWithIntrinsicBounds(img,0,0,0);

                itemLayout.addView(listButton);
                itemLayout.addView(deleteItemButton);

                deleteItemButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            AsyncTask task = new HttpDeleteRequest().execute(MainActivity.baseURL + "api/delete", itemFinal, "item", name);
                            System.out.println("This is final: " + itemFinal);
                            task.get();
                            itemLayout.removeView(deleteItemButton);
                            itemLayout.removeView(listButton);
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

    public void deleteListClick (View view) {
        context = this;
        try {

            AsyncTask task = new HttpDeleteRequest().execute(MainActivity.baseURL + "api/delete", name, "list");
            task.get();

        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        System.out.println("We are back out of the delete");
        startActivity(new Intent(context, MainActivity.class));
    }

}
