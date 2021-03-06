package io.github.jitinsharma.insplore.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
import io.github.jitinsharma.insplore.fragment.DatePickerFragment;
import io.github.jitinsharma.insplore.model.AsyncTaskListener;
import io.github.jitinsharma.insplore.model.Constants;
import io.github.jitinsharma.insplore.model.LocationObject;
import io.github.jitinsharma.insplore.service.LocationTask;
import io.github.jitinsharma.insplore.storage.TinyDB;
import io.github.jitinsharma.insplore.utilities.AnimationUtilities;
import io.github.jitinsharma.insplore.utilities.PermissionUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback {
    public static final String TAG = "InsploreMap";
    protected static final int REQUEST_CHECK_SETTINGS = 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    final int PLACE_PICKER_REQUEST = 1;
    Location mLastLocation;
    LocationObject locationObject;
    TinyDB tinyDB;
    TextView nearbyLocations;
    CardView flightSearchCard;
    ImageView imageView;
    TabLayout tabLayout;
    TextView goButton;
    TextView topDestDate;
    Context self;
    RelativeLayout topDestinationView;
    SwitchCompat tripSwitch;
    ProgressBar progressBar;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tinyDB = new TinyDB(this);
        self = getBaseContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.divider);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        nearbyLocations = (TextView) findViewById(R.id.nearby_locations);
        flightSearchCard = (CardView) findViewById(R.id.flight_search_card);
        imageView = (ImageView) findViewById(R.id.city_image);
        goButton = (TextView) findViewById(R.id.go_button);
        topDestDate = (TextView) findViewById(R.id.top_dest_date_text);
        topDestinationView = (RelativeLayout) findViewById(R.id.top_destination_options);
        tripSwitch = (SwitchCompat) findViewById(R.id.return_trip);
        progressBar = (ProgressBar) findViewById(R.id.main_progress);
        tabLayout.addTab(tabLayout.newTab().setText(getBaseContext().getString(R.string.top_destination)));
        tabLayout.addTab(tabLayout.newTab().setText(getBaseContext().getString(R.string.inspire_search)));

        buildGoogleApiClient();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.lite_map);
        supportMapFragment.getMapAsync(this);
        if (savedInstanceState != null) {
            supportMapFragment.setRetainInstance(true);
            locationObject = savedInstanceState.getParcelable(Constants.LOCATION_OBJ);
        }
        if (locationObject != null) {
            topDestDate.setText(locationObject.getDate());
            tabLayout.getTabAt(locationObject.getTabNumber()).select();
            if (locationObject.getTabNumber() == 1) {
                if (!locationObject.isTripSwitch()) {
                    tripSwitch.setChecked(false);
                } else {
                    tripSwitch.setChecked(true);
                }
                AnimationUtilities.animatedBackgroundColor(ContextCompat.getColor(
                        getBaseContext(), R.color.colorPrimary), ContextCompat.getColor(
                        getBaseContext(), R.color.colorAccent), tabLayout);
                AnimationUtilities.animateImageFadeIn(imageView, self, 500, self.getResources().getDrawable(R.drawable.inspire));
                topDestinationView.setVisibility(View.GONE);
                tripSwitch.setVisibility(View.VISIBLE);
            }
        } else {
            locationObject = new LocationObject();
            locationObject.setTabNumber(0);
            locationObject.setTripSwitch(true);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int month = calendar.get(Calendar.MONTH) - 1;
            String monthString = String.format(Locale.ENGLISH, "%02d", month);
            topDestDate.setText(calendar.get(Calendar.YEAR) + "-" + monthString);
            locationObject.setDate(topDestDate.getText().toString());
        }

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
                if (locationObject.getTabNumber() == 0) {
                    intent.putExtra(Constants.INTENT_TYPE, Constants.TOP_DESTINATION);
                    intent.putExtra(Constants.TD_MONTH, topDestDate.getText().toString());
                    intent.putExtra(Constants.SAVED_AIRPORT_CODE, tinyDB.getString(Constants.SAVED_AIRPORT_CODE));
                } else {
                    intent.putExtra(Constants.INTENT_TYPE, Constants.INSPIRE_ME);
                    intent.putExtra(Constants.TRIP_TYPE, tripSwitch.isChecked() ? "false" : "true");
                    intent.putExtra(Constants.SAVED_AIRPORT_CODE, tinyDB.getString(Constants.SAVED_AIRPORT_CODE));
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(MainActivity.this, imageView,
                                    self.getString(R.string.transition_name));
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        AnimationUtilities.animatedBackgroundColor(ContextCompat.getColor(
                                getBaseContext(), R.color.colorAccent), ContextCompat.getColor(
                                getBaseContext(), R.color.colorPrimary), tabLayout);
                        AnimationUtilities.animateImageFadeIn(imageView, self, 500, self.getResources().getDrawable(R.drawable.top_dest_image));
                        topDestinationView.setVisibility(View.VISIBLE);
                        tripSwitch.setVisibility(View.GONE);
                        locationObject.setTabNumber(0);
                        break;
                    case 1:
                        AnimationUtilities.animatedBackgroundColor(ContextCompat.getColor(
                                getBaseContext(), R.color.colorPrimary), ContextCompat.getColor(
                                getBaseContext(), R.color.colorAccent), tabLayout);
                        AnimationUtilities.animateImageFadeIn(imageView, self, 500, self.getResources().getDrawable(R.drawable.inspire));
                        topDestinationView.setVisibility(View.GONE);
                        tripSwitch.setVisibility(View.VISIBLE);
                        locationObject.setTabNumber(1);
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
                        monthString = String.format("%02d", month);
                        topDestDate.setText(year + "-" + monthString);
                        locationObject.setDate(topDestDate.getText().toString());
                    }
                });
                datePickerFragment.show(getSupportFragmentManager(), "datepicker");
            }
        });

        tripSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    locationObject.setTripSwitch(true);
                } else {
                    locationObject.setTripSwitch(false);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.LOCATION_OBJ, locationObject);
        super.onSaveInstanceState(outState);
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e("tag", "inside activity OK");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mGoogleApiClient.reconnect();
                            }
                        }, 2000);
                        break;
                    case Activity.RESULT_CANCELED:
                        Snackbar.make(getCurrentFocus(), self.getString(R.string.location_error), Snackbar.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        break;
                }
                break;
            case PLACE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(data, this);
                    Uri gmmIntentUri = Uri.parse("geo:" + place.getLatLng().latitude
                            + "," + place.getLatLng().longitude + "?z=21");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(self.getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
                break;
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_top_dest) {
            Intent intent = new Intent(self, SearchActivity.class);
            intent.putExtra(Constants.INTENT_TYPE, Constants.TOP_DESTINATION);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        } else if (id == R.id.nav_poi) {
            Intent intent = new Intent(self, SearchActivity.class);
            intent.putExtra(Constants.INTENT_TYPE, Constants.POI);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        } else if (id == R.id.nav_saved_poi) {
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
        if (locationObject.getLatitude() != 0) {
            zoomMap(locationObject.getLatitude(), locationObject.getLongitude());
        }
    }

    @Override
    protected void onStart() {
        if (locationObject.getLatitude() == 0) {
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
        //Log.e(TAG, "API connected");

        //Log.e(TAG, "API connected" + mLastLocation);
        enableLocation();
        if (mLastLocation != null) {
            doMapOperations();
        } else if (tinyDB.getDouble(Constants.LAST_LAT, 0) != 0) {
            locationObject.setLatitude(tinyDB.getDouble(Constants.LAST_LAT, 0));
            locationObject.setLongitude(tinyDB.getDouble(Constants.LAST_LONG, 0));
            zoomMap(locationObject.getLatitude(), locationObject.getLongitude());
            flightSearchCard.setVisibility(View.VISIBLE);
        } else {
            settingsRequest();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "API failed");
    }

    public void zoomMap(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f));
        mMap.addMarker(new MarkerOptions()
                .position(latLng));
    }

    public void loadAirportCityData(double latitude, double longitude) {
        LocationTask locationTask = new LocationTask(getBaseContext(), new AirportCityListener());
        LatLng latLng = new LatLng(latitude, longitude);
        locationTask.execute(latLng);
    }

    public void enableLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            Log.e("Last location", "" + mLastLocation);
        }
    }

    public void doMapOperations() {
        locationObject.setLatitude(mLastLocation.getLatitude());
        locationObject.setLongitude(mLastLocation.getLongitude());
        tinyDB.putDouble(Constants.LAST_LAT, mLastLocation.getLatitude());
        tinyDB.putDouble(Constants.LAST_LONG, mLastLocation.getLongitude());
        zoomMap(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        if (tinyDB.getString(Constants.SAVED_AIRPORT_CODE).isEmpty() && tinyDB.getString(Constants.SAVED_CITY).isEmpty()) {
            loadAirportCityData(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } else {
            flightSearchCard.setVisibility(View.VISIBLE);
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
            progressBar.setVisibility(View.GONE);
            Snackbar.make(getCurrentFocus(), self.getString(R.string.location_error), Snackbar.LENGTH_LONG).show();
            mGoogleApiClient.disconnect();
        }
    }

    public void settingsRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mGoogleApiClient.reconnect();
                            }
                        }, 2000);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    class AirportCityListener implements AsyncTaskListener<LocationObject> {

        @Override
        public void onTaskComplete(LocationObject result) {
            progressBar.setVisibility(View.GONE);
            if (result != null) {
                flightSearchCard.setVisibility(View.VISIBLE);
                AnimationUtilities.animateViewUp(flightSearchCard, self, 500);
                tinyDB.putString(Constants.SAVED_AIRPORT_CODE, result.getAirportCode());
                tinyDB.putString(Constants.SAVED_CITY, result.getCityName());
            } else {
                Snackbar.make(getCurrentFocus(), self.getString(R.string.server_error), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
