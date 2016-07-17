package io.github.jitinsharma.insplore.activities;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.utilities.AnimationUtilities;
import io.github.jitinsharma.insplore.utilities.PermissionUtils;
import io.github.jitinsharma.insplore.fragment.DatePickerFragment;
import io.github.jitinsharma.insplore.model.AsyncTaskListener;
import io.github.jitinsharma.insplore.model.Constants;
import io.github.jitinsharma.insplore.model.LocationObject;
import io.github.jitinsharma.insplore.service.LocationTask;
import io.github.jitinsharma.insplore.storage.TinyDB;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        DatePickerFragment.OnDatePickedListener,
        ActivityCompat.OnRequestPermissionsResultCallback{
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LatLng coord;
    LocationObject locationObject;
    public static final String TAG = "InsploreMap";
    TinyDB tinyDB;
    TextView nearbyLocations;
    int PLACE_PICKER_REQUEST = 1;
    TextView topDestinationButton;
    ImageView imageView;
    TabLayout tabLayout;
    TextView goButton;
    TextView topDestDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    Context self;
    RelativeLayout topDestinationView;
    SwitchCompat returnTripSwitch;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        self = getBaseContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout)findViewById(R.id.divider);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        nearbyLocations = (TextView)findViewById(R.id.nearby_locations);
        imageView = (ImageView)findViewById(R.id.city_image);
        goButton = (TextView) findViewById(R.id.go_button);
        topDestDate = (TextView)findViewById(R.id.top_dest_date_text);
        topDestinationView = (RelativeLayout)findViewById(R.id.top_destination_options);
        returnTripSwitch = (SwitchCompat)findViewById(R.id.return_trip);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.lite_map);
        supportMapFragment.getMapAsync(this);
        locationObject = new LocationObject();
        tinyDB = new TinyDB(this);
        tabLayout.addTab(tabLayout.newTab().setText(getBaseContext().getString(R.string.top_destination)));
        tabLayout.addTab(tabLayout.newTab().setText(getBaseContext().getString(R.string.inspire_search)));
        locationObject.setTabSelection(Constants.TOP_DESTINATION);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int month = calendar.get(Calendar.MONTH)-1;
        String monthString = String.format(Locale.ENGLISH,"%02d",month);
        topDestDate.setText(calendar.get(Calendar.YEAR) + "-" + monthString);

        nearbyLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(intentBuilder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                if (locationObject.getTabSelection().equals(Constants.TOP_DESTINATION)) {
                    intent.putExtra(Constants.INTENT_TYPE, Constants.TOP_DESTINATION);
                    intent.putExtra(Constants.TD_MONTH, topDestDate.getText().toString());
                    intent.putExtra(Constants.SAVED_AIRPORT_CODE, tinyDB.getString(Constants.SAVED_AIRPORT_CODE));
                }
                else{
                    intent.putExtra(Constants.INTENT_TYPE, Constants.INSPIRE_ME);
                    intent.putExtra(Constants.TRIP_TYPE, "");
                    intent.putExtra(Constants.SAVED_AIRPORT_CODE, tinyDB.getString(Constants.SAVED_AIRPORT_CODE));
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(MainActivity.this, imageView, "search");
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        locationObject.setTabSelection(Constants.TOP_DESTINATION);
                        AnimationUtilities.animatedBackgroundColor(ContextCompat.getColor(
                                getBaseContext(), R.color.colorAccent), ContextCompat.getColor(
                                getBaseContext(), R.color.colorPrimary), tabLayout);
                        AnimationUtilities.animateSlideIn(imageView, self, 500, self.getResources().getDrawable(R.drawable.skyscrapers));
                        topDestinationView.setVisibility(View.VISIBLE);
                        returnTripSwitch.setVisibility(View.GONE);
                        break;
                    case 1:
                        locationObject.setTabSelection(Constants.INSPIRE_ME);
                        AnimationUtilities.animatedBackgroundColor(ContextCompat.getColor(
                                getBaseContext(), R.color.colorPrimary), ContextCompat.getColor(
                                        getBaseContext(), R.color.colorAccent), tabLayout);
                        AnimationUtilities.animateSlideIn(imageView, self, 500, self.getResources().getDrawable(R.drawable.inspire));
                        topDestinationView.setVisibility(View.GONE);
                        returnTripSwitch.setVisibility(View.VISIBLE);
                        break;
                    default:
                        AnimationUtilities.animatedBackgroundColor(ContextCompat.getColor(
                                getBaseContext(), R.color.colorAccent), ContextCompat.getColor(
                                getBaseContext(), R.color.colorPrimary), tabLayout);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
                datePickerFragment.show(getSupportFragmentManager(), "datepicker");
            }
        });
    }

    @Override
    public void onDatePicked(int year, int month, int day, int requestCode) {
        //Toast.makeText(MainActivity.this, ""+year+month+day, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_top_dest) {
        }
        else if (id==R.id.nav_poi){
            Intent intent = new Intent(self, SearchActivity.class);
            intent.putExtra(Constants.INTENT_TYPE, Constants.POI);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
        else if (id == R.id.nav_saved_poi){
            Intent intent = new Intent(self, SearchActivity.class);
            intent.putExtra(Constants.INTENT_TYPE, Constants.SAVED_POI);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        if (locationObject.getLatitude()==0) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "API connected");
        enableLocation();
        Log.e(TAG, "API connected" + mLastLocation);
        if (mLastLocation != null) {
            locationObject.setLatitude(mLastLocation.getLatitude());
            locationObject.setLongitude(mLastLocation.getLongitude());
            tinyDB.putDouble(Constants.LAST_LAT, mLastLocation.getLatitude());
            tinyDB.putDouble(Constants.LAST_LONG, mLastLocation.getLongitude());
            zoomMap(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            if (tinyDB.getString(Constants.SAVED_AIRPORT_CODE).isEmpty() && tinyDB.getString(Constants.SAVED_CITY).isEmpty()){
                loadAirportCityData(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }
        }
        else if (tinyDB.getDouble(Constants.LAST_LAT,0)!=0){
            locationObject.setLatitude(tinyDB.getDouble(Constants.LAST_LAT,0));
            locationObject.setLongitude(tinyDB.getDouble(Constants.LAST_LONG,0));
            zoomMap(locationObject.getLatitude(), locationObject.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "API failed");
    }

    public void zoomMap(double latitude, double longitude){
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f));
        mMap.addMarker(new MarkerOptions()
        .position(latLng));
    }

    public void loadAirportCityData(double latitude, double longitude){
        LocationTask locationTask = new LocationTask(getBaseContext(), new AirportCityListener());
        LatLng latLng = new LatLng(latitude, longitude);
        locationTask.execute(latLng);
    }

    class AirportCityListener implements AsyncTaskListener<LocationObject> {

        @Override
        public void onTaskComplete(LocationObject result) {
            tinyDB.putString(Constants.SAVED_AIRPORT_CODE, result.getAirportCode());
            tinyDB.putString(Constants.SAVED_CITY, result.getCityName());
            Toast.makeText(getBaseContext(), result.getAirportCode() + result.getCityName(), Toast.LENGTH_SHORT).show();
        }
    }

    public void enableLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        else{
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
        }
    }
}