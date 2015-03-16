package com.example.erin.sunshine3.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.erin.sunshine3.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>
{

    public static final String[] DETAIL_COLUMNS = {
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
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_HUMIDITY = 7;
    static final int COL_WIND_SPEED = 8;
    static final int COL_WIND_DEG = 9;
    static final int COL_PRESSURE = 10;


    public static class ViewHolder {
        public final TextView dayView;
        public final TextView dateView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final TextView forecastView;
        public final ImageView iconView;
        public final TextView humidityView;
        public final TextView windView;
        public final TextView pressureView;

        ViewHolder(View view) {
            dayView = (TextView)view.findViewById(R.id.detail_day_textview);
            dateView = (TextView)view.findViewById(R.id.detail_date_textview);
            highTempView = (TextView)view.findViewById(R.id.detail_high_temp_textview);
            lowTempView = (TextView) view.findViewById(R.id.detail_low_temp_textview);
            forecastView = (TextView) view.findViewById(R.id.detail_forecast_textview);
            iconView = (ImageView) view.findViewById(R.id.detail_icon);
            humidityView = (TextView) view.findViewById(R.id.detail_humidity_textview);
            windView = (TextView) view.findViewById(R.id.detail_wind_textview);
            pressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);
        }
    }

    String mWeatherData;
    Uri mUri;
    ShareActionProvider mShareActionProvider;
    public static final int LOADER_ID = 200;
    public static final String DETAIL_URI = "uri";

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (mUri != null) {

            CursorLoader cur = new CursorLoader(getActivity(), mUri, DETAIL_COLUMNS,
                    null, null, null);
            return cur;
        }
        return null;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()){
            boolean isMetric = Utility.isMetric(getActivity());

            String date = Utility.getFormattedMonthDay(getActivity(),
                    cursor.getLong(COL_WEATHER_DATE));
            String hi = Utility.formatTemperature(getActivity(),
                    cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
            String low = Utility.formatTemperature(getActivity(),
                    cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
            String day = Utility.getDayName(getActivity(),
                    cursor.getLong(COL_WEATHER_DATE));

            View topView = getActivity().findViewById(R.id.detail_top_view);
            ViewHolder vh = (ViewHolder) topView.getTag();

            vh.dateView.setText(date);
            vh.dayView.setText(day);
            vh.highTempView.setText(hi);
            vh.lowTempView.setText(low);
            int weatherId = Utility.getArtResourceForWeatherCondition(
                    cursor.getInt(COL_WEATHER_CONDITION_ID));
            vh.iconView.setImageResource(weatherId);
            vh.forecastView.setText(cursor.getString(COL_WEATHER_DESC));
            vh.humidityView.setText(getActivity().getString(R.string.format_humidity,
                    cursor.getFloat(COL_HUMIDITY)));
            vh.windView.setText(Utility.getFormattedWind(getActivity(),
                    cursor.getFloat(COL_WIND_SPEED), cursor.getFloat(COL_WIND_DEG)));
            vh.pressureView.setText(getActivity().getString(R.string.format_pressure,
                    cursor.getFloat(COL_PRESSURE)));

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            }
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static DetailFragment newInstance(int index) {
        DetailFragment f = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index");
    }

    public void onLocationChanged(String newLocation) {
        Uri uri = mUri;
        if (uri != null) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri newUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = newUri;
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }


    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(getActivity());
        String highLowStr = Utility.formatTemperature(getActivity(), high, isMetric) + "/"
                + Utility.formatTemperature(getActivity(), low, isMetric);
        return highLowStr;
    }

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    public void onActivityCreated(Bundle bundle) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        View topView = rootView.findViewById(R.id.detail_top_view);
        ViewHolder vh = new ViewHolder(topView);
        topView.setTag(vh);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        if (mWeatherData != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mWeatherData + " (brought to you by loving fiance's app)");
        return intent;
    }

}