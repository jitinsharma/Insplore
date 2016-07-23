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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.model.Constants;
import io.github.jitinsharma.insplore.utilities.Utils;
import io.github.jitinsharma.insplore.model.InspireSearchObject;

/**
 * Created by jitin on 06/07/16.
 */
public class InService extends IntentService {
    ArrayList<InspireSearchObject> inspireSearchObjects = new ArrayList<>();
    public static final String ACTION_InService = "io.github.jitinsharma.insplore.service.InService";
    String oneWay;
    String places[];

    public InService(String name) {
        super(name);
    }

    public InService() {
        super("io.github.jitinsharma.insplore.service.InService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String depAirportCode = null;
        oneWay = "false";
        if (intent!=null){
            depAirportCode = intent.getStringExtra(Constants.DEP_AIRPORT);
            oneWay = intent.getStringExtra(Constants.TRIP_TYPE);
        }
        String baseUrl = "http://api.sandbox.amadeus.com/v1.2/flights/inspiration-search";
        Uri uri;
        uri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("origin", depAirportCode)
                .appendQueryParameter("one-way",oneWay)
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
                Intent intent = new Intent();
                intent.setAction(ACTION_InService);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.putExtra(Constants.NETWORK_ERROR, ""+error.getMessage());
                sendBroadcast(intent);
            }
        });
        requestQueue.add(stringRequest);
    }

    public void getData(String json) throws JSONException {
        places = getBaseContext().getResources().getStringArray(R.array.yapq_cities);
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has("results")) {
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            JSONObject airportObject = new JSONObject(Utils.loadJSONFromAsset(getBaseContext(), "airport_list.json"));
            for (int i = 0; i < jsonArray.length(); i++) {
                InspireSearchObject inspireSearchObject = new InspireSearchObject();
                inspireSearchObject.setDepCode(jsonObject.getString("origin"));
                inspireSearchObject.setCurrencyCode(jsonObject.getString("currency"));
                inspireSearchObject.setDestinationCode(jsonArray.getJSONObject(i).getString("destination"));
                inspireSearchObject.setDepDate(formatDate(jsonArray.getJSONObject(i).getString("departure_date")));
                if (oneWay.equals("false")) {
                    inspireSearchObject.setArrDate(formatDate(jsonArray.getJSONObject(i).getString("return_date")));
                }
                inspireSearchObject.setPrice(jsonArray.getJSONObject(i).getString("price"));
                inspireSearchObject.setAirlineCode(jsonArray.getJSONObject(i).getString("airline"));
                JSONObject cityObject = airportObject.getJSONObject(inspireSearchObject.getDestinationCode());
                inspireSearchObject.setDestinationCity(cityObject.getString("city"));
                for (String place : places) {
                    if (place.equals(inspireSearchObject.getDestinationCity())){
                        inspireSearchObject.setPlaceEnabled(true);
                    }
                }
                inspireSearchObjects.add(inspireSearchObject);
            }
        }
        Intent intent = new Intent();
        intent.setAction(ACTION_InService);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("KEY", inspireSearchObjects);
        sendBroadcast(intent);
    }

    public String formatDate(String date){
        String finalValue = null;
        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("d MMM, yy");
            finalValue = formatter.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return finalValue;
    }
}
