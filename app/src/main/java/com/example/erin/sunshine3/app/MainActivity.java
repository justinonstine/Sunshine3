package com.example.erin.sunshine3.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.erin.sunshine3.app.sync.SunshineSyncAdapter;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    String mLocation;
    public static final String FRAGMENT_TAG = "ftag";
    boolean mTwoPane;

    @Override
    public void onStart() {
        Log.v("rotateMonitor", "OnStart!");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.v("rotateMonitor", "OnStop!");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.v("rotateMonitor", "OnDestroy!");
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Log.v("rotateMonitor", "OnPause!");
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);

        if(location != null && !mLocation.equals(location)) {
            ForecastFragment ff =
                    (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);

            if (ff != null) {
                ff.onLocationChanged();
            }

            DetailFragment df =
                    (DetailFragment)getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            if (df != null) {
                df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().
                findFragmentById(R.id.fragment_forecast);

        ff.setDisplayTodayLarge(!mTwoPane);
        SunshineSyncAdapter.initializeSyncAdapter(this);
    }


    public void onItemSelected(Uri dateUri) {

        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, dateUri);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().
                    replace(R.id.weather_detail_container, fragment, FRAGMENT_TAG).commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).setData(dateUri);
            startActivity(intent);
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
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }


}
