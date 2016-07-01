package io.github.jitinsharma.insplore;

import android.app.ActivityOptions;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.android.gms.vision.text.Text;

import io.github.jitinsharma.insplore.Utilities.AnimationUtilities;
import io.github.jitinsharma.insplore.activities.SearchActivity;
import io.github.jitinsharma.insplore.fragment.DatePickerFragment;
import io.github.jitinsharma.insplore.model.AsyncTaskListener;
import io.github.jitinsharma.insplore.model.LocationObject;
import io.github.jitinsharma.insplore.service.LocationTask;
import io.github.jitinsharma.insplore.storage.TinyDB;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
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
    Button goButton;
    TextView topDestDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        goButton = (Button)findViewById(R.id.go_button);
        topDestDate = (TextView)findViewById(R.id.top_dest_date_text);

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

        nearbyLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(intentBuilder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                intent.putExtra("TYPE", "TOP_DEST");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(MainActivity.this, imageView, "search");
                    startActivity(intent, options.toBundle());
                }
                else{
                    startActivity(intent);
                }

            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        AnimationUtilities.animatedBackgroundColor(ContextCompat.getColor(
                                getBaseContext(), R.color.colorAccent), ContextCompat.getColor(
                                getBaseContext(), R.color.colorPrimary), tabLayout);

                        AnimationUtilities.animatedBackgroundColor(ContextCompat.getColor(
                                getBaseContext(), R.color.colorPrimary), ContextCompat.getColor(
                                getBaseContext(), R.color.colorAccent), goButton);
                        break;
                    case 1:
                        AnimationUtilities.animatedBackgroundColor(ContextCompat.getColor(
                                getBaseContext(), R.color.colorPrimary), ContextCompat.getColor(
                                        getBaseContext(), R.color.colorAccent), tabLayout);


                        AnimationUtilities.animatedBackgroundColor(ContextCompat.getColor(
                                getBaseContext(), R.color.colorAccent), ContextCompat.getColor(
                                getBaseContext(), R.color.colorPrimary), goButton);
                        break;
                    default:
                        AnimationUtilities.animatedBackgroundColor(ContextCompat.getColor(
                                getBaseContext(), R.color.colorAccent), ContextCompat.getColor(
                                getBaseContext(), R.color.colorPrimary), tabLayout);

                        AnimationUtilities.animatedBackgroundColor(ContextCompat.getColor(
                                getBaseContext(), R.color.colorPrimary), ContextCompat.getColor(
                                getBaseContext(), R.color.colorAccent), goButton);
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
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), "datepicker");
            }
        });
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
            // Handle the camera action
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.e(TAG, "API connected" + mLastLocation);
        if (mLastLocation != null) {
            zoomMap(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            //coord = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            locationObject.setLatitude(mLastLocation.getLatitude());
            locationObject.setLongitude(mLastLocation.getLongitude());
            if (tinyDB.getString("Airport").isEmpty() && tinyDB.getString("City").isEmpty()){
                loadAirportCityData(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }
            Log.e(TAG, "API connected" + coord);
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
            tinyDB.putString("Airport", result.getAirportCode());
            tinyDB.putString("City", result.getCityName());
            Toast.makeText(getBaseContext(), result.getAirportCode() + result.getCityName(), Toast.LENGTH_SHORT).show();
        }
    }
}
