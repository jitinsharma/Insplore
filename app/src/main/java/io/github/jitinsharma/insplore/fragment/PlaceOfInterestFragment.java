package io.github.jitinsharma.insplore.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.adapter.PoiAdapter;
import io.github.jitinsharma.insplore.data.InContract;
import io.github.jitinsharma.insplore.model.AsyncTaskListener;
import io.github.jitinsharma.insplore.model.Constants;
import io.github.jitinsharma.insplore.model.OnItemClick;
import io.github.jitinsharma.insplore.model.PoiObject;
import io.github.jitinsharma.insplore.service.PoiTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaceOfInterestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaceOfInterestFragment extends Fragment {

    private AutoCompleteTextView placeList;
    private RecyclerView recyclerView;
    PoiAdapter poiAdapter;
    ProgressBar progressBar;
    ArrayList<PoiObject> poiObjects;
    String cityName = null;
    public static final String[] POI_COLUMNS = {
            InContract.PoiEntry._ID,
            InContract.PoiEntry.COLUMN_POI_TITLE,
            InContract.PoiEntry.COLUMN_POI_DESC,
            InContract.PoiEntry.COLUMN_POI_IMAGE,
            InContract.PoiEntry.COLUMN_POI_LAT,
            InContract.PoiEntry.COLUMN_POI_LONG,
            InContract.PoiEntry.COLUMN_POI_WIKI_LINK
    };

    public PlaceOfInterestFragment() {
        // Required empty public constructor
    }

    public static PlaceOfInterestFragment newInstance(String city) {
        PlaceOfInterestFragment fragment = new PlaceOfInterestFragment();
        Bundle args = new Bundle();
        args.putString(Constants.POI_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityName = getArguments().getString(Constants.POI_CITY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constants.RECEIVE_LIST, poiObjects);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_place_of_interest, container, false);
        progressBar = (ProgressBar)root.findViewById(R.id.poi_progress);
        placeList = (AutoCompleteTextView)root.findViewById(R.id.poi_search);
        recyclerView = (RecyclerView)root.findViewById(R.id.poi_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);

        if (cityName!=null && poiObjects==null){
            progressBar.setVisibility(View.VISIBLE);
            placeList.setVisibility(View.GONE);
            PoiTask poiTask = new PoiTask(getContext(), new PoiListener());
            poiTask.execute(cityName);
        }
        else {
            String places[] = getContext().getResources().getStringArray(R.array.yapq_cities);
            ArrayAdapter<String> placeArrayAdapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_dropdown_item_1line, places);
            placeList.setAdapter(placeArrayAdapter);

            placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                    PoiTask poiTask = new PoiTask(getContext(), new PoiListener());
                    poiTask.execute((String) adapterView.getItemAtPosition(i));

                }
            });
        }

        if (poiObjects!=null && poiObjects.size()>0){
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            poiAdapter = new PoiAdapter(getContext(), poiObjects, new OnItemClick() {
                @Override
                public void onFavoriteClicked(int position) {

                }

                @Override
                public void onMapClicked(int position) {
                    openMap(position);
                }

                @Override
                public void onWikiClick(int position) {
                    openWikipediaPage(position);
                }

                @Override
                public void onShareIconClick(int position) {
                    createShareIntent(position);
                }
            });
            recyclerView.setAdapter(poiAdapter);
        }
        return root;
    }

    class PoiListener implements AsyncTaskListener<ArrayList<PoiObject>>{

        @Override
        public void onTaskComplete(ArrayList<PoiObject> result) {
            if (result!=null && result.size()>0) {
                poiObjects = result;
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                poiAdapter = new PoiAdapter(getContext(), result, new OnItemClick() {
                    @Override
                    public void onFavoriteClicked(int position) {

                    }

                    @Override
                    public void onMapClicked(int position) {
                        openMap(position);
                    }

                    @Override
                    public void onWikiClick(int position) {
                        openWikipediaPage(position);
                    }

                    @Override
                    public void onShareIconClick(int position) {
                        createShareIntent(position);
                    }
                });
                recyclerView.setAdapter(poiAdapter);
                recyclerView.setNestedScrollingEnabled(false);
            }
            else{
                Snackbar.make(getView(), "An error occurred with your search. Please try again later", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void openMap(int position){
        Uri gmmIntentUri = Uri.parse("geo:" + poiObjects.get(position).getPoiLatitude()
                +","+poiObjects.get(position).getPoiLongitude()+"?z=15");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    public void openWikipediaPage(int position){
        String url = poiObjects.get(position).getWikipediaLink();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
    }

    public void createShareIntent(int position){
        PoiObject poiObject = poiObjects.get(position);
        startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setText("Explore " + poiObject.getTitle() + "\n"
                        + "Map: http://maps.google.com?q=" + poiObject.getPoiLatitude() +","+poiObject.getPoiLongitude()
                        + "\n" + "Wikipedia: " + poiObject.getWikipediaLink())
                .getIntent(), getString(R.string.action_share)));
    }
}
