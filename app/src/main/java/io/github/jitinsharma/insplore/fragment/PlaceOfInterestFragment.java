package io.github.jitinsharma.insplore.fragment;


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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.activities.SearchActivity;
import io.github.jitinsharma.insplore.adapter.PoiAdapter;
import io.github.jitinsharma.insplore.model.AsyncTaskListener;
import io.github.jitinsharma.insplore.model.Constants;
import io.github.jitinsharma.insplore.model.OnItemClick;
import io.github.jitinsharma.insplore.model.PoiObject;
import io.github.jitinsharma.insplore.service.PoiTask;
import io.github.jitinsharma.insplore.utilities.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaceOfInterestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaceOfInterestFragment extends Fragment {

    private RecyclerView recyclerView;
    PoiAdapter poiAdapter;
    ProgressBar progressBar;
    ArrayList<PoiObject> poiObjects;
    String cityName = null;
    SearchActivity searchActivity;
    View root;

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
        root = inflater.inflate(R.layout.fragment_place_of_interest, container, false);
        searchActivity = (SearchActivity)getActivity();
        searchActivity.updateImage(ContextCompat.getDrawable(getContext(), R.drawable.places));
        searchActivity.updateTitle(getContext().getString(R.string.places_of_interest));
        progressBar = (ProgressBar)root.findViewById(R.id.poi_progress);
        AutoCompleteTextView placeList = (AutoCompleteTextView) root.findViewById(R.id.poi_search);
        recyclerView = (RecyclerView)root.findViewById(R.id.poi_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);

        if (poiObjects==null) {
            if (cityName != null) {
                progressBar.setVisibility(View.VISIBLE);
                placeList.setVisibility(View.GONE);
                initializeRequest(cityName);
            } else {
                String places[] = getContext().getResources().getStringArray(R.array.yapq_cities);
                ArrayAdapter<String> placeArrayAdapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, places);
                placeList.setAdapter(placeArrayAdapter);

                placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        progressBar.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        Utils.hideKeyBoard(getContext(), view);
                        initializeRequest((String) adapterView.getItemAtPosition(i));
                    }
                });
            }
        }

        if (poiObjects!=null && poiObjects.size()>0){
            setAdapterWithData();
        }
        return root;
    }

    class PoiListener implements AsyncTaskListener<ArrayList<PoiObject>>{

        @Override
        public void onTaskComplete(ArrayList<PoiObject> result) {
            if (result!=null && result.size()>0) {
                poiObjects = result;
                setAdapterWithData();
            }
            else{
                progressBar.setVisibility(View.GONE);
                Snackbar.make(root, getContext().getString(R.string.poi_error), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void initializeRequest(String data){
        PoiTask poiTask = new PoiTask(getContext(), new PoiListener());
        poiTask.execute(data);
    }

    public void setAdapterWithData(){
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
                .setText(getContext().getString(R.string.share_explore) + poiObject.getTitle() + "\n"
                        + getContext().getString(R.string.share_map) + "http://maps.google.com?q="
                        + poiObject.getPoiLatitude() +","+poiObject.getPoiLongitude()
                        + "\n" + getContext().getString(R.string.share_wiki) + poiObject.getWikipediaLink())
                .getIntent(), getString(R.string.action_share)));
    }
}
