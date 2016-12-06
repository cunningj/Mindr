package com.example.ownerperson.mindr;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
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


public class PostListRequest extends AsyncTask<String, Void, PostListRequest.LatLngResponse> {
    private static final Moshi MOSHI = new Moshi.Builder().build();
    private static final JsonAdapter<LatLngResponse> LATLNG_RESPONSE_JSON_ADAPTER = MOSHI.adapter(PostListRequest.LatLngResponse.class);

    public static class LatLngResponse{
        public double latitude;
        public double longitude;
        public int approaching;
    }

    private static final String ADD_LIST_ENDPOINT = MainActivity.baseURL + "api/addList";


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    protected LatLngResponse doInBackground(String... args) {
        Map<String, String> reqData = new HashMap<String, String>();
        reqData.put("listName", args[0]);
        reqData.put("locationName", args[1]);
        reqData.put("approaching", args[2]);
        int arraySize = args.length;

        for (int i = 3; i < arraySize; i++) {
            reqData.put("item" + i, args[i]);
        }


        JSONObject reqJson = new JSONObject(reqData);
        System.out.println("JSON!!!: "+ reqJson.toString());
        OkHttpClient client = new OkHttpClient();
        // Create request for remote resource.
        RequestBody body = RequestBody.create(JSON, reqJson.toString());
        Request request = new Request.Builder()
                .url(ADD_LIST_ENDPOINT)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println("boo yaaaaaa");

            ResponseBody respBody = response.body();

            LatLngResponse latLngResponse = LATLNG_RESPONSE_JSON_ADAPTER.fromJson(respBody.source());
            respBody.close();
            System.out.println("latLngResponse" + latLngResponse);
            return latLngResponse;

        } catch (Exception e){
            System.out.println("Could not make http request : " + e.toString());
        }

        return null;
    }

}