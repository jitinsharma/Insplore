package io.github.jitinsharma.insplore.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.fragment.InspirationSearchFragment;
import io.github.jitinsharma.insplore.fragment.PlaceOfInterestFragment;
import io.github.jitinsharma.insplore.fragment.SavedPoiFragment;
import io.github.jitinsharma.insplore.fragment.TopDestinationFragment;
import io.github.jitinsharma.insplore.model.Constants;

public class SearchActivity extends AppCompatActivity {
    Context self;
    Bundle bundle;
    ImageView imageView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private int mMutedColor = 0xFF333333;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        imageView = (ImageView)findViewById(R.id.toolbar_image);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        self = getBaseContext();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (getIntent() != null && getIntent().hasExtra(Constants.INTENT_TYPE)) {
            Intent intent = getIntent();
            if (getIntent().getStringExtra(Constants.INTENT_TYPE).equals(Constants.TOP_DESTINATION)) {
                imageView.setBackgroundDrawable(self.getResources().getDrawable(R.drawable.skyscrapers));
                getSupportActionBar().setTitle(self.getString(R.string.top_destination));
                bundle = new Bundle();
                bundle.putString(Constants.TD_MONTH, getIntent().getStringExtra(Constants.TD_MONTH));
                bundle.putString(Constants.SAVED_AIRPORT_CODE, getIntent().getStringExtra(Constants.SAVED_AIRPORT_CODE));
                TopDestinationFragment topDestinationFragment = new TopDestinationFragment();
                topDestinationFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, topDestinationFragment)
                        .commit();
            }
            else if (getIntent().getStringExtra(Constants.INTENT_TYPE).equals(Constants.POI)){
                getSupportActionBar().setTitle(self.getString(R.string.places_of_interest));
                imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.places_toronto));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                PlaceOfInterestFragment placeOfInterestFragment = PlaceOfInterestFragment.newInstance("","");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, placeOfInterestFragment)
                        .commit();
            }
            else if (getIntent().getStringExtra(Constants.INTENT_TYPE).equals(Constants.SAVED_POI)){
                getSupportActionBar().setTitle(self.getString(R.string.my_saved_places));
                imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.places_toronto));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                SavedPoiFragment savedPoiFragment = SavedPoiFragment.newInstance("","");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, savedPoiFragment)
                        .commit();
            }
            else if (getIntent().getStringExtra(Constants.INTENT_TYPE).equals(Constants.INSPIRE_ME)){
                getSupportActionBar().setTitle(self.getString(R.string.inspire_search));
                imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.inspire));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setColorFilter(self.getResources().getColor(android.R.color.darker_gray), android.graphics.PorterDuff.Mode.MULTIPLY);
                InspirationSearchFragment fragment = InspirationSearchFragment.newInstance("","");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }
        }
        else{
            getSupportActionBar().setTitle(self.getString(R.string.my_saved_places));
            imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.places_toronto));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            SavedPoiFragment savedPoiFragment = SavedPoiFragment.newInstance("","");
            fragmentManager.beginTransaction()
                    .replace(R.id.container, savedPoiFragment)
                    .commit();
        }
        updateWindowColor(imageView);
    }

    public void updateWindowColor(ImageView imageView){
        Bitmap bitmap = ((BitmapDrawable)imageView.getBackground()).getBitmap();
        Palette p = Palette.generate(bitmap, 12);
        mMutedColor = p.getDarkMutedColor(0xFF333333);
        collapsingToolbarLayout.setContentScrimColor(mMutedColor);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(mMutedColor);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent!=null){
            String test = ""+intent.getStringExtra(Constants.SAVED_POI);
            Log.d("tag", test);
        }
        super.onNewIntent(intent);
    }
}
