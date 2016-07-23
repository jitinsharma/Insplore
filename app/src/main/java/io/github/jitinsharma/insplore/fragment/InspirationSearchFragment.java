package io.github.jitinsharma.insplore.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.activities.SearchActivity;
import io.github.jitinsharma.insplore.adapter.InspireSearchAdapter;
import io.github.jitinsharma.insplore.model.Constants;
import io.github.jitinsharma.insplore.model.InspireSearchObject;
import io.github.jitinsharma.insplore.model.OnPlacesClick;
import io.github.jitinsharma.insplore.service.InService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InspirationSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InspirationSearchFragment extends Fragment {

    private String airportCode;
    private RecyclerView searchResults;
    private ArrayList<InspireSearchObject> inspireSearchObjects;
    InBroadcastReceiver inBroadcastReceiver;
    ProgressBar progressBar;
    String tripType;
    SearchActivity searchActivity;

    public InspirationSearchFragment() {
        // Required empty public constructor
    }

    public static InspirationSearchFragment newInstance(String airportCode, String tripType) {
        InspirationSearchFragment fragment = new InspirationSearchFragment();
        Bundle args = new Bundle();
        args.putString(Constants.DEP_AIRPORT, airportCode);
        args.putString(Constants.TRIP_TYPE, tripType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            airportCode = getArguments().getString(Constants.DEP_AIRPORT);
            tripType = getArguments().getString(Constants.TRIP_TYPE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constants.INSPIRE_OBJ, inspireSearchObjects);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (inBroadcastReceiver!=null) {
            getContext().unregisterReceiver(inBroadcastReceiver);
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        searchActivity = (SearchActivity) getActivity();
        View root = inflater.inflate(R.layout.fragment_inspiration_search, container, false);
        searchActivity.updateImage(ContextCompat.getDrawable(getContext(), R.drawable.inspire));
        searchActivity.getSupportActionBar().setTitle(getContext().getString(R.string.inspire_search));
        if (savedInstanceState!=null){
            inspireSearchObjects = savedInstanceState.getParcelableArrayList(Constants.INSPIRE_OBJ);
            searchActivity.updateImage(ContextCompat.getDrawable(getContext(), R.drawable.inspire));
            searchActivity.getSupportActionBar().setTitle(getContext().getString(R.string.inspire_search));
        }
        searchResults = (RecyclerView)root.findViewById(R.id.inspire_search_results);
        progressBar = (ProgressBar)root.findViewById(R.id.inspire_progress);
        searchResults.setLayoutManager(new LinearLayoutManager(getContext()));

        initializeReceiver();
        if (inspireSearchObjects!=null){
            progressBar.setVisibility(View.GONE);
            setAdapterWithData();
        }
        else {
            initializeService();
        }
        return root;
    }

    class InBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.GONE);
            if (intent.getStringExtra(Constants.NETWORK_ERROR)!=null){
                Snackbar.make(getView(), getContext().getString(R.string.server_error), Snackbar.LENGTH_LONG).show();
            }
            else {
                inspireSearchObjects = intent.getParcelableArrayListExtra("KEY");
                if (inspireSearchObjects!=null && inspireSearchObjects.size()>0) {
                    setAdapterWithData();
                }
                else{
                    Snackbar.make(getView(), getContext().getString(R.string.no_result_error), Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    public void setAdapterWithData(){
        InspireSearchAdapter inspireSearchAdapter = new InspireSearchAdapter(getContext(), inspireSearchObjects, new OnPlacesClick() {
            @Override
            public void onClick(int position) {
                displayPlacesOfInterest(inspireSearchObjects.get(position).getDestinationCity());
            }
        });
        searchResults.setAdapter(inspireSearchAdapter);
    }

    public void displayPlacesOfInterest(String city){
        searchActivity.updateTitle(getContext().getString(R.string.places_of_interest));
        PlaceOfInterestFragment fragment = PlaceOfInterestFragment.newInstance(city);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void initializeReceiver(){
        inBroadcastReceiver = new InBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(InService.ACTION_InService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getContext().registerReceiver(inBroadcastReceiver, intentFilter);
    }

    public void initializeService(){
        Intent inService = new Intent(getActivity(), InService.class);
        inService.putExtra(Constants.DEP_AIRPORT, airportCode);
        inService.putExtra(Constants.TRIP_TYPE, tripType);
        getActivity().startService(inService);
    }
}
