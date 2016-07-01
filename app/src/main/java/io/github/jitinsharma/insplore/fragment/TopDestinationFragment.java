package io.github.jitinsharma.insplore.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.adapter.TopDestinationAdapter;
import io.github.jitinsharma.insplore.model.AsyncTaskListener;
import io.github.jitinsharma.insplore.model.TopDestinationObject;
import io.github.jitinsharma.insplore.service.TopDestinationTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopDestinationFragment extends Fragment {
    RecyclerView topDestinationList;
    TopDestinationAdapter topDestinationAdapter;

    public TopDestinationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_top_destination, container, false);
        topDestinationList = (RecyclerView)root.findViewById(R.id.top_dest_list);
        TopDestinationTask topDestinationTask = new TopDestinationTask(getContext(), new TopDestinationListener());
        //topDestinationTask.execute("BLR");
        return root;
    }

    class TopDestinationListener implements AsyncTaskListener<ArrayList<TopDestinationObject>> {

        @Override
        public void onTaskComplete(ArrayList<TopDestinationObject> result) {
            topDestinationAdapter = new TopDestinationAdapter(getContext(), result);
            topDestinationList.setLayoutManager(new LinearLayoutManager(getContext()));
            topDestinationList.setAdapter(topDestinationAdapter);
            topDestinationList.setNestedScrollingEnabled(false);
        }
    }
}
