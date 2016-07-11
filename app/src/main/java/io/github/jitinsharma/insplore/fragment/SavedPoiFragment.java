package io.github.jitinsharma.insplore.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.adapter.SavedPoiAdapter;
import io.github.jitinsharma.insplore.data.InContract.PoiEntry;
import io.github.jitinsharma.insplore.model.OnItemClick;
import io.github.jitinsharma.insplore.model.PoiObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SavedPoiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedPoiFragment extends Fragment {
    public static final String[] POI_COLUMNS = {
            PoiEntry._ID,
            PoiEntry.COLUMN_POI_TITLE,
            PoiEntry.COLUMN_POI_DESC,
            PoiEntry.COLUMN_POI_IMAGE,
            PoiEntry.COLUMN_POI_LAT,
            PoiEntry.COLUMN_POI_LONG,
            PoiEntry.COLUMN_GEO_ID,
            PoiEntry.COLUMN_POI_WIKI_LINK
    };
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Cursor cursor;
    ArrayList<PoiObject> poiObjects;
    RecyclerView savedPoiList;
    SavedPoiAdapter savedPoiAdapter;

    public SavedPoiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SavedPoiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SavedPoiFragment newInstance(String param1, String param2) {
        SavedPoiFragment fragment = new SavedPoiFragment();
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
        View root = inflater.inflate(R.layout.fragment_saved_poi, container, false);
        savedPoiList = (RecyclerView)root.findViewById(R.id.saved_poi_list);
        poiObjects = new ArrayList<>();
        cursor = getContext().getContentResolver().query(
                PoiEntry.CONTENT_URI,
                POI_COLUMNS,
                null,
                null,
                null
        );
        if (cursor!=null && cursor.moveToFirst()){
            do{
                PoiObject poiObject = new PoiObject();
                poiObject.setGeoNameId(cursor.getString(cursor.getColumnIndex(PoiEntry.COLUMN_GEO_ID)));
                poiObject.setTitle(cursor.getString(cursor.getColumnIndex(PoiEntry.COLUMN_POI_TITLE)));
                poiObject.setPoiDescription(cursor.getString(cursor.getColumnIndex(PoiEntry.COLUMN_POI_DESC)));
                poiObject.setPoiLatitude(cursor.getString(cursor.getColumnIndex(PoiEntry.COLUMN_POI_LAT)));
                poiObject.setPoiLongitude(cursor.getString(cursor.getColumnIndex(PoiEntry.COLUMN_POI_LONG)));
                poiObject.setWikipediaLink(cursor.getString(cursor.getColumnIndex(PoiEntry.COLUMN_POI_WIKI_LINK)));
                poiObject.setImageArray(cursor.getBlob(cursor.getColumnIndex(PoiEntry.COLUMN_POI_IMAGE)));
                poiObjects.add(poiObject);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        savedPoiAdapter = new SavedPoiAdapter(getContext(), poiObjects, new OnItemClick() {
            @Override
            public void onViewClicked(int position) {

            }

            @Override
            public void onFavoriteClicked(int position) {
                savedPoiList.removeViewAt(position);
                poiObjects.remove(position);
                savedPoiAdapter.notifyItemRemoved(position);
                savedPoiAdapter.notifyItemRangeChanged(position, poiObjects.size());
                savedPoiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onMapClicked(int position) {

            }
        });
        savedPoiList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        savedPoiList.setAdapter(savedPoiAdapter);
        savedPoiList.setNestedScrollingEnabled(false);
        return root;
    }

}
