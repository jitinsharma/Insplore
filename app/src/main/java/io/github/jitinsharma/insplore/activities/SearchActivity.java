package io.github.jitinsharma.insplore.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
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
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    private int mMutedColor = 0xFF333333;
    FragmentManager fragmentManager;
    static{
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        appBarLayout = (AppBarLayout)findViewById(R.id.search_app_bar);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar)findViewById(R.id.search_toolbar);
        imageView = (ImageView)findViewById(R.id.toolbar_image);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        self = getBaseContext();
        fragmentManager = getSupportFragmentManager();

        if (getIntent() != null && getIntent().hasExtra(Constants.INTENT_TYPE)) {
            Intent intent = getIntent();
            if (getIntent().getStringExtra(Constants.INTENT_TYPE).equals(Constants.TOP_DESTINATION)) {
                bundle = new Bundle();
                bundle.putString(Constants.TD_MONTH, getIntent().getStringExtra(Constants.TD_MONTH));
                bundle.putString(Constants.SAVED_AIRPORT_CODE, getIntent().getStringExtra(Constants.SAVED_AIRPORT_CODE));
                TopDestinationFragment topDestinationFragment = new TopDestinationFragment();
                topDestinationFragment.setArguments(bundle);
                loadFragment(topDestinationFragment);
            }
            else if (getIntent().getStringExtra(Constants.INTENT_TYPE).equals(Constants.POI)){
                PlaceOfInterestFragment placeOfInterestFragment = PlaceOfInterestFragment.newInstance(null);
                loadFragment(placeOfInterestFragment);
            }
            else if (getIntent().getStringExtra(Constants.INTENT_TYPE).equals(Constants.SAVED_POI)){
                SavedPoiFragment savedPoiFragment = SavedPoiFragment.newInstance();
                loadFragment(savedPoiFragment);
            }
            else if (getIntent().getStringExtra(Constants.INTENT_TYPE).equals(Constants.INSPIRE_ME)){
                InspirationSearchFragment fragment = InspirationSearchFragment.newInstance(intent.getStringExtra(Constants.SAVED_AIRPORT_CODE)
                        ,intent.getStringExtra(Constants.TRIP_TYPE));
                loadFragment(fragment);
            }
        }
        else{
            SavedPoiFragment savedPoiFragment = SavedPoiFragment.newInstance();
            loadFragment(savedPoiFragment);
        }
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(collapsingToolbarLayout.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsingToolbarLayout)) {
                    Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    Palette p = Palette.generate(bitmap, 12);
                    mMutedColor = p.getDarkMutedColor(0xFF333333);
                    collapsingToolbarLayout.setContentScrimColor(mMutedColor);
                    final Window window = getWindow();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        window.setStatusBarColor(mMutedColor);
                    }
                }
                else
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        getWindow().setStatusBarColor(ContextCompat.getColor(self,android.R.color.transparent));
                    }
                }
            }
        });
    }

    public void loadFragment(Fragment fragment){
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void updateWindowColor(ImageView imageView){
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        Palette p = Palette.generate(bitmap, 12);
        mMutedColor = p.getDarkMutedColor(0xFF333333);
        collapsingToolbarLayout.setContentScrimColor(mMutedColor);
    }

    public void updateImage(Drawable drawable){
        imageView.setImageDrawable(drawable);
        imageView.setColorFilter(self.getResources().getColor(android.R.color.darker_gray), android.graphics.PorterDuff.Mode.MULTIPLY);
        updateWindowColor(imageView);
    }

    public void updateTitle(String value){
        getSupportActionBar().setTitle(value);
    }
}
