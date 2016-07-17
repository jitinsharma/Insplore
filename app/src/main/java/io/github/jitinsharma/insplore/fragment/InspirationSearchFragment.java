package io.github.jitinsharma.insplore.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.adapter.InspireSearchAdapter;
import io.github.jitinsharma.insplore.model.InspireSearchObject;
import io.github.jitinsharma.insplore.service.InService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InspirationSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InspirationSearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView searchResults;
    private InspireSearchAdapter inspireSearchAdapter;
    private ArrayList<InspireSearchObject> inspireSearchObjects;
    InBroadcastReceiver inBroadcastReceiver;
    ProgressBar progressBar;

    public InspirationSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InspirationSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InspirationSearchFragment newInstance(String param1, String param2) {
        InspirationSearchFragment fragment = new InspirationSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_inspiration_search, container, false);
        searchResults = (RecyclerView)root.findViewById(R.id.inspire_search_results);
        progressBar = (ProgressBar)root.findViewById(R.id.inspire_progress);
        searchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        //inspireSearchAdapter = new InspireSearchAdapter(getContext(), new ArrayList<InspireSearchObject>());
        //searchResults.setAdapter(inspireSearchAdapter);

        Intent inService = new Intent(getActivity(), InService.class);
        getActivity().startService(inService);
        inBroadcastReceiver = new InBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(InService.ACTION_InService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getContext().registerReceiver(inBroadcastReceiver, intentFilter);

        return root;
    }

    class InBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            inspireSearchObjects = intent.getParcelableArrayListExtra("KEY");
            inspireSearchAdapter = new InspireSearchAdapter(getContext(), inspireSearchObjects);
            searchResults.setAdapter(inspireSearchAdapter);
            progressBar.setVisibility(View.GONE);
        }
    }
}
