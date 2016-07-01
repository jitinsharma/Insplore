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
import io.github.jitinsharma.insplore.Utilities.Utils;
import io.github.jitinsharma.insplore.model.AsyncTaskListener;
import io.github.jitinsharma.insplore.model.TopDestinationObject;

/**
 * Created by jitin on 28/06/16.
 */
public class TopDestinationTask extends AsyncTask<String, Void, ArrayList<TopDestinationObject>> {
    Context context;
    ArrayList<TopDestinationObject> topDestinationObjects = new ArrayList<>();
    AsyncTaskListener<ArrayList<TopDestinationObject>> asyncTaskListener;

    @Override
    protected void onPostExecute(ArrayList<TopDestinationObject> topDestinationObjects) {
        asyncTaskListener.onTaskComplete(topDestinationObjects);
        super.onPostExecute(topDestinationObjects);
    }

    public TopDestinationTask(Context context, AsyncTaskListener<ArrayList<TopDestinationObject>> asyncTaskListener) {
        this.context = context;
        this.asyncTaskListener = asyncTaskListener;
    }

    @Override
    protected ArrayList<TopDestinationObject> doInBackground(String... strings) {
        String baseUrl = "https://api.sandbox.amadeus.com/v1.2/travel-intelligence/top-destinations";
        String fromAirport = strings[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String airportJson = null;
        Uri uri = Uri.EMPTY;
        uri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("period", "2015-09")
                .appendQueryParameter("origin", fromAirport)
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
                return getTopDestinationData(airportJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public ArrayList<TopDestinationObject> getTopDestinationData(String json) throws JSONException{
        JSONObject jsonObject = new JSONObject(json);
        JSONArray dataArray = jsonObject.getJSONArray("results");
        JSONObject airportObject = new JSONObject(Utils.loadJSONFromAsset(context, "airport_list.json"));
        for (int i = 0; i <dataArray.length() ; i++) {
            JSONObject data = dataArray.getJSONObject(i);
            TopDestinationObject topDestinationObject = new TopDestinationObject();
            topDestinationObject.setDestination(data.getString("destination"));
            JSONObject cityObject = airportObject.getJSONObject(topDestinationObject.getDestination());
            topDestinationObject.setCityName(cityObject.getString("city"));
            topDestinationObject.setNoOfFlights(data.getString("flights"));
            topDestinationObject.setNoOfPax(data.getString("travelers"));
            topDestinationObjects.add(topDestinationObject);
        }
        return topDestinationObjects;
    }
}
