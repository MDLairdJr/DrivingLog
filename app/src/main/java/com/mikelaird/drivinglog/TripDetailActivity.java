package com.mikelaird.drivinglog;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import com.mikelaird.drivinglog.data.TripContract.TripEntry;

import java.util.Date;

public class TripDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Tag for the log messages */
    public static final String LOG_TAG = TripDetailActivity.class.getSimpleName();

    private Uri mTripUri;
    private TextView detailDateTime;
    private TextView detailNotes;
    private TextView detailDuration;
    private TextView detailTotalTime;
    private TextView detailNightTime;

    private static final int TRIP_DETAIL_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Inside the onCreate() method of TripDetailActivity . . . ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        // Get the URI by calling getIntent() and getData()
        Intent intent = getIntent();
        mTripUri = intent.getData();

        getLoaderManager().initLoader(TRIP_DETAIL_LOADER, null, this);

        // Get references to widgets
        detailDateTime = (TextView)findViewById(R.id.trip_detail_datetime);
        detailNotes = (TextView) findViewById(R.id.trip_detail_notes);
        detailDuration = (TextView) findViewById(R.id.trip_detail_duration_value);
        detailTotalTime = (TextView) findViewById(R.id.trip_detail_total_time_value);
        detailNightTime = (TextView) findViewById(R.id.trip_detail_night_time_value);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies which columns from the database
        // we will actually use for this query.
        String[] projection = {
                TripEntry._ID,
                TripEntry.COLUMN_NAME_DATETIME,
                TripEntry.COLUMN_NAME_DURATION,
                TripEntry.COLUMN_NAME_TOTAL_TIME,
                TripEntry.COLUMN_NAME_NOTES };

        // this loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(
                this,               // parent activity context
                mTripUri,            // provider contentURI
                projection,         // columns to include in the query
                null,               // where clause
                null,               // where clause arguments
                null);              // sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()) {
            // get the values from the cursor
            long datetime = cursor.getLong(cursor.getColumnIndexOrThrow(TripEntry.COLUMN_NAME_DATETIME));
            String notes = cursor.getString(cursor.getColumnIndexOrThrow(TripEntry.COLUMN_NAME_NOTES));
            long duration = cursor.getLong(cursor.getColumnIndexOrThrow(TripEntry.COLUMN_NAME_DURATION));
            long totalTime = cursor.getLong(cursor.getColumnIndexOrThrow(TripEntry.COLUMN_NAME_TOTAL_TIME));

            // populate the views with the values
            String dateString = DateFormat.getLongDateFormat(this).format(new Date(datetime));
            String timeString = DateFormat.getTimeFormat(this).format(new Date(datetime));
            detailDateTime.setText(dateString + "   ");
            detailDateTime.append(timeString);
            updateTimeText(detailDuration, duration);
            updateTimeText(detailTotalTime, totalTime);
            detailNotes.setText(notes);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        detailDateTime.setText(null);
        detailDuration.setText(null);
        detailTotalTime.setText(null);
        detailNotes.setText(null);
    }

    private void updateTimeText(TextView view, long time) {
        int secs = (int) (time / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        int hrs = mins / 60;
        mins = mins % 60;

        if (hrs > 0) {
            view.append(String.format("%02d", hrs) + ":");
        }

        view.append(String.format("%02d", mins) + ":" +
                String.format("%02d", secs));
    }
}
