package io.github.jitinsharma.insplore.fragment;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.adapter.PoiAdapter;
import io.github.jitinsharma.insplore.data.InContract;
import io.github.jitinsharma.insplore.model.AsyncTaskListener;
import io.github.jitinsharma.insplore.model.OnItemClick;
import io.github.jitinsharma.insplore.model.PoiObject;
import io.github.jitinsharma.insplore.service.PoiTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaceOfInterestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaceOfInterestFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AutoCompleteTextView placeList;
    private RecyclerView recyclerView;
    PoiAdapter poiAdapter;
    ArrayList<PoiObject> testList;
    Cursor cursor;
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaceOfInterestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaceOfInterestFragment newInstance(String param1, String param2) {
        PlaceOfInterestFragment fragment = new PlaceOfInterestFragment();
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
        View root = inflater.inflate(R.layout.fragment_place_of_interest, container, false);
        placeList = (AutoCompleteTextView)root.findViewById(R.id.poi_search);
        String places[] = getContext().getResources().getStringArray(R.array.yapq_cities);
        ArrayAdapter<String> placeArrayAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, places);
        placeList.setAdapter(placeArrayAdapter);
        recyclerView = (RecyclerView)root.findViewById(R.id.poi_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        /*testList = new ArrayList<>(10);

        for (int i = 0; i < 10; i++) {
            PoiObject object = new PoiObject();
            object.setTitle("Test");
            object.setPoiDescription(getContext().getResources().getString(R.string.city_short));
            object.setPoiLatitude("28.5933");
            object.setPoiLongitude("77.2506");
            object.setWikipediaLink("www.wikipedia.org");
            object.setMainImageUrl("www.google.com");
            object.setGeoNameId("456566");
            testList.add(object);
        }

        poiAdapter = new PoiAdapter(getContext(), testList, new OnItemClick() {
            @Override
            public void onViewClicked(int position) {

            }

            @Override
            public void onFavoriteClicked(int position) {

            }

            @Override
            public void onMapClicked(int position) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=28.5933,77.2506");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
        recyclerView.setAdapter(poiAdapter);
        recyclerView.setNestedScrollingEnabled(false);*/
        placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager in = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                PoiTask poiTask = new PoiTask(getContext(), new PoiListener());
                poiTask.execute((String) adapterView.getItemAtPosition(i));

            }
        });
        return root;
    }

    class PoiListener implements AsyncTaskListener<ArrayList<PoiObject>>{

        @Override
        public void onTaskComplete(ArrayList<PoiObject> result) {
            poiAdapter = new PoiAdapter(getContext(), result, new OnItemClick() {
                @Override
                public void onViewClicked(int position) {

                }

                @Override
                public void onFavoriteClicked(int position) {

                }

                @Override
                public void onMapClicked(int position) {

                }
            });
            recyclerView.setAdapter(poiAdapter);
            recyclerView.setNestedScrollingEnabled(false);
        }
    }
}
