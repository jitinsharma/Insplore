package io.github.jitinsharma.insplore.service;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.model.AsyncTaskListener;
import io.github.jitinsharma.insplore.model.LocationObject;

/**
 * Created by jitin on 26/06/16.
 */
public class LocationTask extends AsyncTask<LatLng, Void, LocationObject> {
    Context context;
    public AsyncTaskListener<LocationObject> asyncTaskListener;

    public LocationTask(Context context, AsyncTaskListener<LocationObject> asyncTaskListener) {
        this.context = context;
        this.asyncTaskListener = asyncTaskListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(LocationObject locationObject) {
        asyncTaskListener.onTaskComplete(locationObject);
        super.onPostExecute(locationObject);
    }

    @Override
    protected LocationObject doInBackground(LatLng... latLngs) {
        String baseUrl = "https://api.sandbox.amadeus.com/v1.2/airports/nearest-relevant";
        LatLng latLng = latLngs[0];
        String latitude = ""+latLng.latitude;
        String longitude = ""+latLng.longitude;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String airportJson = null;
        Uri uri = Uri.EMPTY;
        uri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("latitude", latitude)
                .appendQueryParameter("longitude", longitude)
                .appendQueryParameter("apikey",context.getString(R.string.ama_sandbox))
                .build();

        try{
            URL url = new URL(uri.toString());
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null){
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0){
                return null;
            }
            airportJson = buffer.toString();
        }
        catch (IOException e){

        }
        finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try{
                    reader.close();
                }
                catch (IOException e){
                    Log.e(this.getClass().getSimpleName(), "Error closing stream",e);
                }
            }
            try {
                return getAirportAndCity(airportJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public LocationObject getAirportAndCity(String airportJson) throws JSONException {
        //JSONObject jsonObject = new JSONObject(airportJson);
        LocationObject locationObject = new LocationObject();
        JSONArray dataArray = new JSONArray(airportJson);
        JSONObject nearestData = dataArray.getJSONObject(0);
        locationObject.setAirportCode(nearestData.getString("airport"));
        locationObject.setCityName(nearestData.getString("city_name"));
        return locationObject;
    }
}
