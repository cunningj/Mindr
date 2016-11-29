package com.example.ownerperson.mindr;

import android.os.AsyncTask;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class GetListItemsRequest extends AsyncTask<String,Void, List<GetListItemsRequest.ItemLists>> {

    private static final String LIST_ITEMS_ENDPOINT = MainActivity.baseURL + "api/listItems";

    private static final Moshi MOSHI = new Moshi.Builder().build();
    private static final JsonAdapter<List<ItemLists>> VIEW_LIST_ITEMS_JSON_ADAPTER = MOSHI.adapter(
            Types.newParameterizedType(List.class, ItemLists.class));

    static class ItemLists {
        List<String> items;
    }

    protected List<ItemLists> doInBackground(String... args) {
        System.out.println("beep");
        OkHttpClient client = new OkHttpClient();
        // Create request for remote resource.
        Request request = new Request.Builder()
                .url(LIST_ITEMS_ENDPOINT)
                .build();
        try {
            // Execute the request and retrieve the response.
            Response response = client.newCall(request).execute();

            // Deserialize HTTP response to concrete type.
            ResponseBody body = response.body();
            List<ItemLists> itemLists = VIEW_LIST_ITEMS_JSON_ADAPTER.fromJson(body.source());
            body.close();
            return itemLists;
        } catch (Exception e){
            System.out.println("Could not make http request : " + e.toString());
        }
        return null;
    }

}
