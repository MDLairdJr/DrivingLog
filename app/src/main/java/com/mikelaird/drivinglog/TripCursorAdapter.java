package com.mikelaird.drivinglog;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mikelaird.drivinglog.data.TripContract.TripEntry;

import java.util.Date;

/**
 * Created by Mike on 12/22/2016.
 */

public class TripCursorAdapter extends CursorAdapter {

    /** Tag for the log messages */
    public static final String LOG_TAG = TripCursorAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link TripCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public TripCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_trip, parent, false);
    }

    /**
     * This method binds the trip data (in the current row pointed to by cursor) to the given
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    public void bindView(View view, Context context, Cursor cursor) {

        // get the fields that we need to populate
        TextView tvDatetime = (TextView) view.findViewById(R.id.list_item_datetime);
        TextView tvDuration = (TextView) view.findViewById(R.id.list_item_duration);
        TextView tvDetails = (TextView) view.findViewById(R.id.list_item_details);
        TextView tvTotalTime = (TextView) view.findViewById(R.id.list_item_total_time);

        // get the values from the cursor
        long datetime = cursor.getLong(cursor.getColumnIndexOrThrow(TripEntry.COLUMN_NAME_DATETIME));
        long duration = cursor.getLong(cursor.getColumnIndexOrThrow(TripEntry.COLUMN_NAME_DURATION));
        long totalTime = cursor.getLong(cursor.getColumnIndexOrThrow(TripEntry.COLUMN_NAME_TOTAL_TIME));
        String details = cursor.getString(cursor.getColumnIndexOrThrow(TripEntry.COLUMN_NAME_NOTES));

        // populate the views with the values
        String dateString = DateFormat.getMediumDateFormat(context).format(new Date(datetime));
        tvDatetime.setText(dateString);
        updateTimeText(tvDuration, duration);
        updateTimeText(tvTotalTime, totalTime);
        tvDetails.setText(details);
    }

    private void updateTimeText(TextView view, long time) {
        int secs = (int)(time/1000);
        int mins = secs/60;
        secs = secs % 60;
        int hrs = mins/60;
        mins = mins % 60;

        if(hrs > 0) {
            view.append(String.format("%02d", hrs) + ":");
        }

        view.append(String.format("%02d", mins) + ":" +
                String.format("%02d", secs));

//        view.setText(String.format("%02d", hrs) + ":" +
//                String.format("%02d", mins) + ":" +
//                String.format("%02d", secs));

        Log.i(LOG_TAG, "Set duration text to: " + view.getText());
    }
}
