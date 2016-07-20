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
import io.github.jitinsharma.insplore.model.PoiObject;

/**
 * Created by jitin on 18/07/16.
 */
public class PoiService extends IntentService{
    ArrayList<PoiObject> poiObjects = new ArrayList<>();
    public static final String ACTION_PoiService = "io.github.jitinsharma.insplore.service.PoiService";

    public PoiService(){
        super("io.github.jitinsharma.insplore.service.PoiService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //String baseUrl = "https://api.sandbox.amadeus.com/v1.2/points-of-interest/yapq-search-text";
        String baseUrl = "http://api.yapq.com/apigetcity";
        String cityName = null;
        if (intent!=null){
            cityName = intent.getStringExtra(Constants.CITY_NAME);
        }
        Uri uri;
        uri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("city", cityName)
                .appendQueryParameter("apikey", getBaseContext().getResources().getString(R.string.yapq_key))
                .build();

        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    getPoiData(response);
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

    private void getPoiData(String json)throws JSONException{
        JSONObject jsonObject = new JSONObject(json);
        JSONArray placeArray = jsonObject.getJSONArray("points_of_interest");
        for (int i = 0; i <placeArray.length() ; i++) {
            PoiObject poiObject = new PoiObject();
            JSONObject placeObject = placeArray.getJSONObject(i);
            poiObject.setMainImageUrl(placeObject.getString("main_image"));
            poiObject.setTitle(placeObject.getString("title"));
            poiObject.setPoiDescription(placeObject.getJSONObject("details").getString("description"));
            poiObject.setWikipediaLink(placeObject.getJSONObject("details").getString("wiki_page_link"));
            poiObject.setPoiLatitude(placeObject.getJSONObject("location").getString("latitude"));
            poiObject.setPoiLongitude(placeObject.getJSONObject("location").getString("longitude"));
            poiObject.setGeoNameId(placeObject.getString("geoname_id"));
            poiObjects.add(poiObject);
        }
        Intent intent = new Intent();
        intent.setAction(ACTION_PoiService);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Constants.POI_OBJ, poiObjects);
        sendBroadcast(intent);
    }
}
