package io.github.jitinsharma.insplore.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import io.github.jitinsharma.insplore.R;
import io.github.jitinsharma.insplore.fragment.TopDestinationFragment;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent()!=null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (getIntent().getStringExtra("TYPE").equals("TOP_DEST")){
                TopDestinationFragment topDestinationFragment = new TopDestinationFragment();
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, topDestinationFragment)
                        .commit();
            }
        }
    }

}
