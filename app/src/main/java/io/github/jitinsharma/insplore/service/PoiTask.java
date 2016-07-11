package io.github.jitinsharma.insplore.service;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.model.AsyncTaskListener;
import io.github.jitinsharma.insplore.model.PoiObject;

/**
 * Created by jitin on 05/07/16.
 */
public class PoiTask extends AsyncTask<String, Void, ArrayList<PoiObject>> {
    Context context;
    AsyncTaskListener<ArrayList<PoiObject>> asyncTaskListener;
    ArrayList<PoiObject> poiObjects = new ArrayList<>();

    public PoiTask(Context context, AsyncTaskListener<ArrayList<PoiObject>> asyncTaskListener) {
        this.context = context;
        this.asyncTaskListener = asyncTaskListener;
    }

    @Override
    protected void onPostExecute(ArrayList<PoiObject> poiObjects) {
        asyncTaskListener.onTaskComplete(poiObjects);
        super.onPostExecute(poiObjects);
    }

    @Override
    protected ArrayList<PoiObject> doInBackground(String... strings) {
        String baseUrl = "https://api.sandbox.amadeus.com/v1.2/points-of-interest/yapq-search-text";
        String cityName = strings[0];
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        String poiJson = null;
        Uri uri;
        uri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("city_name", cityName)
                .appendQueryParameter("apikey", context.getResources().getString(R.string.ama_sandbox))
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
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                buffer.append(line + "\n");
            }
            if (buffer.length()==0){
                return null;
            }
            poiJson = buffer.toString();

        }catch (IOException e){

        }
        finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (bufferedReader!=null){
                try{
                    bufferedReader.close();
                }
                catch (IOException e){
                    Log.e(this.getClass().getSimpleName(), "Error closing stream",e);
                }
            }
            try {
                return getPoiData(poiJson);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    private ArrayList<PoiObject> getPoiData(String json)throws JSONException{
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
        return poiObjects;
    }
}
