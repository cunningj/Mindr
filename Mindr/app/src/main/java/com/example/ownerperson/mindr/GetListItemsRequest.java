package com.example.ownerperson.mindr;

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


public class GetListItemsRequest extends AsyncTask<String,Void, List<String>> {

    private static final String LIST_ITEMS_ENDPOINT = MainActivity.baseURL + "api/listItems";

    private static final Moshi MOSHI = new Moshi.Builder().build();
    private static final JsonAdapter<List<String>> VIEW_LIST_ITEMS_JSON_ADAPTER = MOSHI.adapter(
            Types.newParameterizedType(List.class, String.class));

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    protected List<String> doInBackground(String... args) {
        Map<String, String> reqData = new HashMap<String, String>();
        reqData.put("listName", args[0]);
        JSONObject reqJson = new JSONObject(reqData);
        System.out.println("JSON!!!: "+ reqJson.toString());
        OkHttpClient client = new OkHttpClient();
        // Create request for remote resource.
        RequestBody body = RequestBody.create(JSON, reqJson.toString());
        Request request = new Request.Builder()
                .url(LIST_ITEMS_ENDPOINT)
                .post(body)
                .build();
        try {
            // Execute the request and retrieve the response.
            Response response = client.newCall(request).execute();

            // Deserialize HTTP response to concrete type.
            ResponseBody respBody = response.body();
            List<String> itemLists = VIEW_LIST_ITEMS_JSON_ADAPTER.fromJson(respBody.source());
            respBody.close();
            return itemLists;
        } catch (Exception e){
            System.out.println("Could not make http request : " + e.toString());
        }
        return null;
    }

}