package com.example.erin.sunshine3.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    
    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;
    public boolean mDisplayTodayLarge = false;

    public static class ViewHolder {
        public final TextView dateView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final TextView forecastView;
        public final ImageView iconView;

        public ViewHolder(View view) {
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
            forecastView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        }
    }



    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setDisplayTodayLarge(boolean setting) {
        mDisplayTodayLarge = setting;
    }

    public int getItemViewType(int position) {
        if (position == 0 && mDisplayTodayLarge) {
            return VIEW_TYPE_TODAY;
        }else{
            return VIEW_TYPE_FUTURE_DAY;
        }
    }

    public int getViewTypeCount(){
        return 2;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_forecast;
                break;
            }
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder vh = new ViewHolder(view);
        view.setTag(vh);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        int viewType = getItemViewType(cursor.getPosition());
        ViewHolder vh = (ViewHolder) view.getTag();
        TextView dateView = vh.dateView;
        long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        dateView.setText(Utility.getFriendlyDayString(context, date));

        boolean isMetric = Utility.isMetric(context);
        TextView highTempView = vh.highTempView;
        double highTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        highTempView.setText(Utility.formatTemperature(context, highTemp, isMetric));

        TextView lowTempView = vh.lowTempView;
        double lowTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        lowTempView.setText(Utility.formatTemperature(context, lowTemp, isMetric));

        TextView descView = vh.forecastView;
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        descView.setText(description);

        ImageView imageView = vh.iconView;
        int weatherResource = Utility.getIconResourceForWeatherCondition(
                cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID));
        if (viewType == VIEW_TYPE_TODAY) {
            weatherResource = Utility.getArtResourceForWeatherCondition(
                    cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID));
        }
        imageView.setImageResource(weatherResource);
//
//        TextView tv = (TextView)view;
//        tv.setText(convertCursorRowToUXFormat(cursor));
    }
}