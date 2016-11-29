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

/**
 * Created by ownerperson on 29/11/16.
 */

public class HttpGetStringList extends AsyncTask<String,Void, List<String>> {

    private static final Moshi MOSHI = new Moshi.Builder().build();
    private static final JsonAdapter<List<String>> LOCATION_LIST_JSON_ADAPTER = MOSHI.adapter(
            Types.newParameterizedType(List.class, String.class));

    protected List<String> doInBackground(String... args) {
        System.out.println("beep");
        OkHttpClient client = new OkHttpClient();
        // Create request for remote resource.
        Request request = new Request.Builder()
                .url(args[0])
                .build();
        try {
            // Execute the request and retrieve the response.
            Response response = client.newCall(request).execute();

            // Deserialize HTTP response to concrete type.
            ResponseBody body = response.body();
            List<String> locationList = LOCATION_LIST_JSON_ADAPTER.fromJson(body.source());
            body.close();
            return locationList;
        } catch (Exception e) {
            System.out.println("Could not make http request : " + e.toString());
        }
        return null;
    }


}
