package io.github.jitinsharma.insplore.service;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import io.github.jitinsharma.insplore.model.LocationObject;

/**
 * Created by jitin on 26/06/16.
 */
public class GeocoderTask extends AsyncTask<LatLng, Void, LocationObject> {

    @Override
    protected LocationObject doInBackground(LatLng... latLngs) {
        LatLng latLng = latLngs[0];
        double lat = latLng.latitude;
        return null;
    }
}
