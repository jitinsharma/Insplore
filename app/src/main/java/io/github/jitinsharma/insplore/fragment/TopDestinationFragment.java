package io.github.jitinsharma.insplore.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.activities.SearchActivity;
import io.github.jitinsharma.insplore.adapter.TopDestinationAdapter;
import io.github.jitinsharma.insplore.model.Constants;
import io.github.jitinsharma.insplore.model.OnPlacesClick;
import io.github.jitinsharma.insplore.model.TopDestinationObject;
import io.github.jitinsharma.insplore.service.TdService;
import io.github.jitinsharma.insplore.utilities.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopDestinationFragment extends Fragment{
    RecyclerView topDestinationList;
    TopDestinationAdapter topDestinationAdapter;
    String month;
    String airportCode;
    ArrayList<TopDestinationObject> topDestinationObjects;
    ProgressBar progressBar;
    AutoCompleteTextView cityListName;
    Intent tdService;
    TdBroadcastReceiver tdBroadcastReceiver;
    LinearLayout topDestInput;
    TextView topDestDate;
    SearchActivity searchActivity;
    String savedMonthString;
    String cityName;
    boolean placeClicked = false;
    static{
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

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
        //outState.putString(Constants.ENTERED_VALUE, cityListName.getText().toString());
        outState.putBoolean(Constants.PLACE_CLICKED, placeClicked);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (tdBroadcastReceiver!=null) {
            getContext().unregisterReceiver(tdBroadcastReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_top_destination, container, false);
        searchActivity = (SearchActivity)getActivity();
        searchActivity.updateImage(ContextCompat.getDrawable(getContext(), R.drawable.top_dest_image));
        searchActivity.getSupportActionBar().setTitle(getContext().getString(R.string.top_destination));
        if (savedInstanceState != null){
            topDestinationObjects = savedInstanceState.getParcelableArrayList(Constants.TOP_DEST_OBJ);
            savedMonthString = savedInstanceState.getString(Constants.DATE_VALUE);
            cityName = savedInstanceState.getString(Constants.ENTERED_VALUE);
            placeClicked = savedInstanceState.getBoolean(Constants.PLACE_CLICKED);
        }
        topDestInput = (LinearLayout)root.findViewById(R.id.top_dest_input);
        topDestDate = (TextView)root.findViewById(R.id.top_dest_date_text);
        topDestinationList = (RecyclerView)root.findViewById(R.id.top_dest_list);
        progressBar = (ProgressBar)root.findViewById(R.id.top_dest_progress);
        cityListName = (AutoCompleteTextView)root.findViewById(R.id.top_dest_search);

        topDestinationList.setLayoutManager(new LinearLayoutManager(getContext()));
        topDestinationList.setNestedScrollingEnabled(false);
        initializeReceiver();

        if (placeClicked){
            displayPlacesOfInterest(cityName);
        }

        if (airportCode!=null){
            topDestInput.setVisibility(View.GONE);
            if (topDestinationObjects==null) {
                initializeService(airportCode, month);
            }
            else{
                setAdapterWithData();
                progressBar.setVisibility(View.GONE);
            }
        }
        else{
            topDestInput.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            String airports[] = getContext().getResources().getStringArray(R.array.airport_cities);
            ArrayAdapter<String> airportArrayAdapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_dropdown_item_1line, airports);
            cityListName.setAdapter(airportArrayAdapter);
            if (cityName!=null){
                cityListName.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cityListName.showDropDown();
                    }
                },500);
                cityListName.setText(cityName);
                cityListName.setSelection(cityListName.getText().length());

            }
            if (savedMonthString==null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                int month = calendar.get(Calendar.MONTH) - 1;
                String monthString = String.format(Locale.ENGLISH, "%02d", month);
                topDestDate.setText(calendar.get(Calendar.YEAR) + "-" + monthString);
            }
            else{
                topDestDate.setText(savedMonthString);
            }
            topDestDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment datePickerFragment = DatePickerFragment.newInstance(1, new DatePickerFragment.OnDatePickedListener() {
                        @Override
                        public void onDatePicked(int year, int month, int day, int requestCode) {
                            String monthString;
                            //Toast.makeText(MainActivity.this, ""+year+month+day, Toast.LENGTH_SHORT).show();
                            monthString = String.format("%02d",month);
                            topDestDate.setText(year + "-" + monthString);
                        }
                    });
                    datePickerFragment.show(getFragmentManager(), "datepicker");
                }
            });
            cityListName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Utils.hideKeyBoard(getContext(), view);
                    progressBar.setVisibility(View.VISIBLE);
                    String[] data = ((String)adapterView.getItemAtPosition(i)).split(",");
                    initializeService(data[0], topDestDate.getText().toString());
                }
            });
        }
        return root;
    }

    class TdBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.GONE);
            if (intent.getStringExtra(Constants.NETWORK_ERROR)!=null){
                Snackbar.make(getView(), getContext().getString(R.string.server_error), Snackbar.LENGTH_LONG).show();
            }
            else {
                topDestinationObjects = intent.getParcelableArrayListExtra(Constants.RECEIVE_LIST);
                if (topDestinationObjects!=null && topDestinationObjects.size()>0) {
                    setAdapterWithData();
                }
                else{
                    Snackbar.make(getView(), getContext().getString(R.string.no_result_error), Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    public void setAdapterWithData(){
        topDestinationAdapter = new TopDestinationAdapter(getContext(), topDestinationObjects, new OnPlacesClick() {
            @Override
            public void onClick(int position) {
                searchActivity.updateImage(getContext().getResources().getDrawable(R.drawable.places));
                displayPlacesOfInterest(topDestinationObjects.get(position).getCityName());
            }
        });
        topDestinationList.setAdapter(topDestinationAdapter);
    }

    public void displayPlacesOfInterest(String city){
        placeClicked = true;
        PlaceOfInterestFragment fragment = PlaceOfInterestFragment.newInstance(city);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void initializeReceiver(){
        tdBroadcastReceiver = new TdBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(TdService.ACTION_TdService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getContext().registerReceiver(tdBroadcastReceiver, intentFilter);
    }

    public void initializeService(String code, String month){
        tdService = new Intent(getActivity(), TdService.class);
        tdService.putExtra(Constants.DEP_AIRPORT, code);
        tdService.putExtra(Constants.TD_MONTH, month);
        getActivity().startService(tdService);
    }
}
