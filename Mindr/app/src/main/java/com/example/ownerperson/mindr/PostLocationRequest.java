package com.example.ownerperson.mindr;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class PostLocationRequest extends AsyncTask<String, Void, List<String>> {

    Context context;

    private static final String ADD_LOCATION_ENDPOINT = MainActivity.baseURL + "api/addLocation";

    private static final Moshi MOSHI = new Moshi.Builder().build();

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    protected List<String> doInBackground(String... args) {
        Map<String, String> reqData = new HashMap<String, String>();
        reqData.put("locationName", args[0]);
        reqData.put("latitude", args[1]);
        reqData.put("longitude", args[2]);


        JSONObject reqJson = new JSONObject(reqData);
        OkHttpClient client = new OkHttpClient();
        // Create request for remote resource.
        RequestBody body = RequestBody.create(JSON, reqJson.toString());
        Request request = new Request.Builder()
                .url(ADD_LOCATION_ENDPOINT)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println("boo yaaaaaa");

            ResponseBody respBody = response.body();
            respBody.close();

        } catch (Exception e){
            System.out.println("Could not make http request : " + e.toString());
        }

        return null;
    }

}