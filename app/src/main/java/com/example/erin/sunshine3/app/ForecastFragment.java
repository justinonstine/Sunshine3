package com.example.erin.sunshine3.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.erin.sunshine3.app.data.WeatherContract;
import com.example.erin.sunshine3.app.sync.SunshineSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor>{

    ForecastAdapter mForecastAdapt;
    ListView mListView;
    int mPosition;
    public static final String POSITION_KEY = "position";

    public static final int LOADER_ID = 100;
    public static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_CONDITION_ID = 5;
    static final int COL_LOCATION_LAT = 6;
    static final int COL_LOCATION_LONG = 7;

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String locationSetting = Utility.getPreferredLocation(getActivity());
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri locationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());
        CursorLoader cur = new CursorLoader(getActivity(), locationUri, FORECAST_COLUMNS,
                null, null, sortOrder);
        return cur;
    }
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mListView.smoothScrollToPosition(mPosition);
        mForecastAdapt.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapt.swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_refresh) {
////          URL weatherUrl = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94061&units=metric&cnt=7");
//            updateWeather();
//            return true;
//        }
        if (id == R.id.action_show_on_map) {
            showOnMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onLocationChanged() {
        updateWeather();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public void setDisplayTodayLarge(boolean setting) {
        if (mForecastAdapt != null) {
            mForecastAdapt.setDisplayTodayLarge(setting);
        }
    }
    public void updateWeather() {
//        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        String location = Utility.getPreferredLocation(getActivity());
//        Intent intent = new Intent(getActivity(),
//                SunshineService.class).putExtra(SunshineService.LOCATION_QUERY_EXTRA, location);
//        getActivity().startService(intent);

//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
//        Intent intent = new Intent(getActivity(), SunshineService.AlarmReceiver.class);
//        intent.putExtra(SunshineService.LOCATION_QUERY_EXTRA, location);
//        PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//        alarmManager.set(AlarmManager.RTC_WAKEUP,
//                System.currentTimeMillis() + 5000, alarmIntent);
//        weatherTask.execute(location);
        SunshineSyncAdapter.syncImmediately(getActivity());
    }

    public void showOnMap() {
        if (mForecastAdapt != null) {
            Cursor cursor = mForecastAdapt.getCursor();
            cursor.moveToFirst();
            String lat = cursor.getString(COL_LOCATION_LAT);
            String lon = cursor.getString(COL_LOCATION_LONG);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri geoLocation = Uri.parse("geo:" + lat + "," + lon);
            intent.setData(geoLocation);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mForecastAdapt = new ForecastAdapter(getActivity(), null, 0);

        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mListView.setAdapter(mForecastAdapt);

        updateWeather();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                mPosition = position;
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    ((Callback) getActivity()).onItemSelected(WeatherContract.WeatherEntry.
                            buildWeatherLocationWithDate(locationSetting, cursor.getLong(COL_WEATHER_DATE)));
//                    Intent intent = new Intent(getActivity(), DetailActivity.class)
//                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
//                            ));
//                    startActivity(intent);
                }
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_KEY)) {
            mPosition = savedInstanceState.getInt(POSITION_KEY);
        }

        return rootView;
    }

    public void onSaveInstanceState(Bundle outBundle) {
        outBundle.putInt("position", mPosition);
        super.onSaveInstanceState(outBundle);
    }
    public void onActivityCreated(Bundle bundle) {
        getLoaderManager().initLoader(ForecastFragment.LOADER_ID, null, this);
        super.onActivityCreated(bundle);
    }
}