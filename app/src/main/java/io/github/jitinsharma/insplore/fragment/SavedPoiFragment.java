package io.github.jitinsharma.insplore.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.activities.SearchActivity;
import io.github.jitinsharma.insplore.adapter.SavedPoiAdapter;
import io.github.jitinsharma.insplore.data.InContract.PoiEntry;
import io.github.jitinsharma.insplore.model.OnItemClick;
import io.github.jitinsharma.insplore.model.PoiObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SavedPoiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedPoiFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
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
    ArrayList<PoiObject> poiObjects;
    RecyclerView savedPoiList;
    SavedPoiAdapter savedPoiAdapter;
    public static final int POI_LOADER = 0;
    SearchActivity searchActivity;

    public SavedPoiFragment() {
        // Required empty public constructor
    }

    public static SavedPoiFragment newInstance() {
        return new SavedPoiFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_saved_poi, container, false);
        searchActivity = (SearchActivity)getActivity();
        searchActivity.updateImage(ContextCompat.getDrawable(getContext(), R.drawable.places));
        searchActivity.getSupportActionBar().setTitle(getContext().getString(R.string.my_saved_places));
        savedPoiList = (RecyclerView)root.findViewById(R.id.saved_poi_list);
        poiObjects = new ArrayList<>();
        savedPoiList.setLayoutManager(new LinearLayoutManager(getContext()));
        savedPoiList.setNestedScrollingEnabled(false);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(POI_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                PoiEntry.CONTENT_URI,
                POI_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
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
            public void onFavoriteClicked(int position) {
                savedPoiList.removeViewAt(position);
                poiObjects.remove(position);
                savedPoiAdapter.notifyItemRemoved(position);
                savedPoiAdapter.notifyItemRangeChanged(position, poiObjects.size());
                savedPoiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onMapClicked(int position) {
                Uri gmmIntentUri = Uri.parse("geo:" + poiObjects.get(position).getPoiLatitude()
                        +","+poiObjects.get(position).getPoiLongitude()+"?z=15");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }

            @Override
            public void onWikiClick(int position) {
                String url = poiObjects.get(position).getWikipediaLink();
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
            }

            @Override
            public void onShareIconClick(int position) {
                PoiObject poiObject = poiObjects.get(position);
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Explore " + poiObject.getTitle() + "\n"
                                + "Map: http://maps.google.com?q=" + poiObject.getPoiLatitude() +","+poiObject.getPoiLongitude()
                                + "\n" + "Wikipedia: " + poiObject.getWikipediaLink())
                        .getIntent(), getString(R.string.action_share)));

            }
        });
        savedPoiList.setAdapter(savedPoiAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
