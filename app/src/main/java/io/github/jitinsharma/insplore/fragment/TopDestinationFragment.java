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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import io.github.jitinsharma.insplore.adapter.TopDestinationAdapter;
import io.github.jitinsharma.insplore.model.Constants;
import io.github.jitinsharma.insplore.model.TopDestinationObject;
import io.github.jitinsharma.insplore.service.TdService;

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
    public void onDestroy() {
        //getContext().unregisterReceiver(tdBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if (tdBroadcastReceiver!=null) {
            getContext().unregisterReceiver(tdBroadcastReceiver);
        }
        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null){
            topDestinationObjects = savedInstanceState.getParcelableArrayList(Constants.TOP_DEST_OBJ);
        }
        View root = inflater.inflate(R.layout.fragment_top_destination, container, false);
        topDestInput = (LinearLayout)root.findViewById(R.id.top_dest_input);
        topDestDate = (TextView)root.findViewById(R.id.top_dest_date_text);
        topDestinationList = (RecyclerView)root.findViewById(R.id.top_dest_list);
        progressBar = (ProgressBar)root.findViewById(R.id.top_dest_progress);
        cityListName = (AutoCompleteTextView)root.findViewById(R.id.top_dest_search);
        topDestinationList.setLayoutManager(new LinearLayoutManager(getContext()));
        topDestinationList.setNestedScrollingEnabled(false);

        if (airportCode!=null){
            topDestInput.setVisibility(View.GONE);
            if (topDestinationObjects==null) {
                tdService = new Intent(getActivity(), TdService.class);
                tdService.putExtra(Constants.DEP_AIRPORT, airportCode);
                tdService.putExtra(Constants.TD_MONTH, month);
                getActivity().startService(tdService);
                tdBroadcastReceiver = new TdBroadcastReceiver();
                IntentFilter intentFilter = new IntentFilter(TdService.ACTION_TdService);
                intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
                getContext().registerReceiver(tdBroadcastReceiver, intentFilter);
                //TopDestinationTask topDestinationTask = new TopDestinationTask(getContext(), new TopDestinationListener());
                //topDestinationTask.execute(airportCode, month);
            }
            else{
                topDestinationAdapter = new TopDestinationAdapter(getContext(), topDestinationObjects, new TopDestinationAdapter.OnDetailClick() {
                    @Override
                    public void onClick(int position) {
                        displayPlacesOfInterest(topDestinationObjects.get(position).getCityName());
                    }
                });
                topDestinationList.setLayoutManager(new LinearLayoutManager(getContext()));
                topDestinationList.setNestedScrollingEnabled(false);
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
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int month = calendar.get(Calendar.MONTH)-1;
            String monthString = String.format(Locale.ENGLISH,"%02d",month);
            topDestDate.setText(calendar.get(Calendar.YEAR) + "-" + monthString);
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
                    InputMethodManager in = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                    progressBar.setVisibility(View.VISIBLE);
                    String[] data = ((String)adapterView.getItemAtPosition(i)).split(",");
                    tdService = new Intent(getActivity(), TdService.class);
                    tdService.putExtra(Constants.DEP_AIRPORT, data[0]);
                    tdService.putExtra(Constants.TD_MONTH, topDestDate.getText());
                    getActivity().startService(tdService);
                    tdBroadcastReceiver = new TdBroadcastReceiver();
                    IntentFilter intentFilter = new IntentFilter(TdService.ACTION_TdService);
                    intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
                    getContext().registerReceiver(tdBroadcastReceiver, intentFilter);
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
                Snackbar.make(getView(), "A server error occured. Please try again later", Snackbar.LENGTH_LONG).show();
            }
            else {
                topDestinationObjects = intent.getParcelableArrayListExtra(Constants.RECEIVE_LIST);
                if (topDestinationObjects!=null && topDestinationObjects.size()>0) {
                    topDestinationAdapter = new TopDestinationAdapter(getContext(), topDestinationObjects, new TopDestinationAdapter.OnDetailClick() {
                        @Override
                        public void onClick(int position) {
                            displayPlacesOfInterest(topDestinationObjects.get(position).getCityName());
                        }
                    });
                    topDestinationList.setAdapter(topDestinationAdapter);
                }
                else{
                    Snackbar.make(getView(), "No Results found for your search", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    public void displayPlacesOfInterest(String city){
        PlaceOfInterestFragment fragment = PlaceOfInterestFragment.newInstance(city);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    /*class TopDestinationListener implements AsyncTaskListener<ArrayList<TopDestinationObject>> {

        @Override
        public void onTaskComplete(ArrayList<TopDestinationObject> result) {
            topDestinationObjects = result;
            topDestinationAdapter = new TopDestinationAdapter(getContext(), result);
            topDestinationList.setAdapter(topDestinationAdapter);
            progressBar.setVisibility(View.GONE);
        }
    }*/
}
