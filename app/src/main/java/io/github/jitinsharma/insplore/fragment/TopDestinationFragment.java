package io.github.jitinsharma.insplore.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.adapter.TopDestinationAdapter;
import io.github.jitinsharma.insplore.model.AsyncTaskListener;
import io.github.jitinsharma.insplore.model.Constants;
import io.github.jitinsharma.insplore.model.TopDestinationObject;
import io.github.jitinsharma.insplore.service.TopDestinationTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopDestinationFragment extends Fragment {
    RecyclerView topDestinationList;
    TopDestinationAdapter topDestinationAdapter;
    String month;
    String cityName="";
    String airportCode;
    ArrayList<TopDestinationObject> topDestinationObjects;
    ProgressBar progressBar;
    AutoCompleteTextView cityListName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments()!=null){
            month = getArguments().getString(Constants.TD_MONTH);
            airportCode = getArguments().getString(Constants.SAVED_AIRPORT_CODE);
        }
        super.onCreate(savedInstanceState);
    }

    public TopDestinationFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constants.TOP_DEST_OBJ, topDestinationObjects);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null){
            topDestinationObjects = savedInstanceState.getParcelableArrayList(Constants.TOP_DEST_OBJ);
        }
        View root = inflater.inflate(R.layout.fragment_top_destination, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        topDestinationList = (RecyclerView)root.findViewById(R.id.top_dest_list);
        progressBar = (ProgressBar)root.findViewById(R.id.top_dest_progress);
        cityListName = (AutoCompleteTextView)root.findViewById(R.id.top_dest_search);
        if (getArguments()!=null){
            cityListName.setVisibility(View.GONE);
        }
        if (topDestinationObjects==null) {
            TopDestinationTask topDestinationTask = new TopDestinationTask(getContext(), new TopDestinationListener());
            topDestinationTask.execute(airportCode, month);
        }
        else{
            topDestinationAdapter = new TopDestinationAdapter(getContext(), topDestinationObjects);
            topDestinationList.setLayoutManager(new LinearLayoutManager(getContext()));
            topDestinationList.setNestedScrollingEnabled(false);
            progressBar.setVisibility(View.GONE);
        }
        //topDestinationAdapter = new TopDestinationAdapter(getContext(), new ArrayList<TopDestinationObject>());
        //topDestinationList.setLayoutManager(new LinearLayoutManager(getContext()));
        //topDestinationList.setAdapter(new AlphaInAnimationAdapter(topDestinationAdapter));
        //topDestinationList.setItemAnimator(new SlideInUpAnimator());
        //topDestinationList.setNestedScrollingEnabled(false);
        return root;
    }

    class TopDestinationListener implements AsyncTaskListener<ArrayList<TopDestinationObject>> {

        @Override
        public void onTaskComplete(ArrayList<TopDestinationObject> result) {
            topDestinationObjects = result;
            topDestinationAdapter = new TopDestinationAdapter(getContext(), result);
            topDestinationList.setLayoutManager(new LinearLayoutManager(getContext()));
            topDestinationList.setAdapter(topDestinationAdapter);
            topDestinationList.setNestedScrollingEnabled(false);
            progressBar.setVisibility(View.GONE);
        }
    }
}
