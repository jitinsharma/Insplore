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
import io.github.jitinsharma.insplore.model.Constants;
import io.github.jitinsharma.insplore.model.TopDestinationObject;
import io.github.jitinsharma.insplore.utilities.Utils;

/**
 * Created by jitin on 18/07/16.
 */
public class TdService extends IntentService{
    ArrayList<TopDestinationObject> topDestinationObjects = new ArrayList<>();
    public static final String ACTION_TdService = "io.github.jitinsharma.insplore.service.TdService";
    String places[];

    public TdService(String name) {
        super(name);
    }

    public TdService(){
        super("io.github.jitinsharma.insplore.service.TdService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String baseUrl = "https://api.sandbox.amadeus.com/v1.2/travel-intelligence/top-destinations";
        String depAirport = null;
        String month = null;
        if (intent!=null){
            depAirport = intent.getStringExtra(Constants.DEP_AIRPORT);
            month = intent.getStringExtra(Constants.TD_MONTH);
        }

        Uri uri;
        uri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("period", month)
                .appendQueryParameter("origin", depAirport)
                .appendQueryParameter("apikey",getBaseContext().getString(R.string.ama_sandbox))
                .build();
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    getTopDestinationData(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", ""+error.getLocalizedMessage());
                Intent intent = new Intent();
                intent.setAction(ACTION_TdService);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.putExtra(Constants.NETWORK_ERROR, ""+error.getMessage());
                sendBroadcast(intent);
            }
        });
        requestQueue.add(stringRequest);
    }

    public void getTopDestinationData(String json) throws JSONException{
        places = getBaseContext().getResources().getStringArray(R.array.yapq_cities);
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has("results")) {
            JSONArray dataArray = jsonObject.getJSONArray("results");
            JSONObject airportObject = new JSONObject(Utils.loadJSONFromAsset(getBaseContext(), "airport_list.json"));
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject data = dataArray.getJSONObject(i);
                TopDestinationObject topDestinationObject = new TopDestinationObject();
                topDestinationObject.setDestination(data.getString("destination"));
                JSONObject cityObject = airportObject.getJSONObject(topDestinationObject.getDestination());
                topDestinationObject.setCityName(cityObject.getString("city"));
                topDestinationObject.setNoOfFlights(data.getString("flights"));
                topDestinationObject.setNoOfPax(data.getString("travelers"));
                for (String place : places) {
                    if (place.equals(topDestinationObject.getCityName())){
                        topDestinationObject.setPlaceEnabled(true);
                    }
                }
                topDestinationObjects.add(topDestinationObject);
            }
        }
        Intent intent = new Intent();
        intent.setAction(ACTION_TdService);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Constants.RECEIVE_LIST, topDestinationObjects);
        sendBroadcast(intent);
    }
}
