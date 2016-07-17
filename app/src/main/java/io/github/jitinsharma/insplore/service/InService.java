package io.github.jitinsharma.insplore.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.utilities.Utils;
import io.github.jitinsharma.insplore.model.InspireSearchObject;

/**
 * Created by jitin on 06/07/16.
 */
public class InService extends IntentService {
    ArrayList<InspireSearchObject> inspireSearchObjects = new ArrayList<>();
    public static final String ACTION_InService = "io.github.jitinsharma.insplore.service.InService";

    public InService(String name) {
        super(name);
    }

    public InService() {
        super("io.github.jitinsharma.insplore.service.InService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String baseUrl = "http://api.sandbox.amadeus.com/v1.2/flights/inspiration-search";
        String code = "BLR";
        Uri uri;
        uri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("origin", code)
                .appendQueryParameter("apikey", getBaseContext().getString(R.string.ama_sandbox))
                .build();
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    getData(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", error.getLocalizedMessage());
            }
        });
        requestQueue.add(stringRequest);
    }

    public void getData(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has("results")) {
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            JSONObject airportObject = new JSONObject(Utils.loadJSONFromAsset(getBaseContext(), "airport_list.json"));
            for (int i = 0; i < jsonArray.length(); i++) {
                InspireSearchObject inspireSearchObject = new InspireSearchObject();
                inspireSearchObject.setDepCode(jsonObject.getString("origin"));
                inspireSearchObject.setDestinationCode(jsonArray.getJSONObject(i).getString("destination"));
                inspireSearchObject.setDepDate(jsonArray.getJSONObject(i).getString("departure_date"));
                inspireSearchObject.setArrDate(jsonArray.getJSONObject(i).getString("return_date"));
                inspireSearchObject.setPrice(jsonArray.getJSONObject(i).getString("price"));
                inspireSearchObject.setAirlineCode(jsonArray.getJSONObject(i).getString("airline"));
                JSONObject cityObject = airportObject.getJSONObject(inspireSearchObject.getDestinationCode());
                inspireSearchObject.setDestinationCity(cityObject.getString("city"));
                inspireSearchObjects.add(inspireSearchObject);
            }
        }
        Intent intent = new Intent();
        intent.setAction(ACTION_InService);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("KEY", inspireSearchObjects);
        sendBroadcast(intent);
    }
}
