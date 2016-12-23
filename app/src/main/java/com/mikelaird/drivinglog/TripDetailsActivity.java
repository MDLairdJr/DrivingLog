package com.mikelaird.drivinglog;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.mikelaird.drivinglog.data.TripContract.TripEntry;

public class TripDetailsActivity extends AppCompatActivity {

    /** Tag for the log messages */
    public static final String LOG_TAG = TripDetailsActivity.class.getSimpleName();

    /*
     * @todo When this activity starts, it causes a new trip to start
     * @todo This needs to be fixed.  Maybe use an AsyncTask in MainActivity?
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the trips database.
     */
    private void displayDatabaseInfo() {

        // Define a projection that specifies which columns from the database
        // we will actually use for this query.
        String[] projection = {
                TripEntry._ID,
                TripEntry.COLUMN_NAME_DATETIME,
                TripEntry.COLUMN_NAME_DURATION,
                TripEntry.COLUMN_NAME_NOTES };

        Cursor cursor = getContentResolver().query(TripEntry.CONTENT_URI, projection, null,
                null, null);

        ListView tripView = (ListView)findViewById(R.id.trip_details_list_view);
        TripCursorAdapter tripCursorAdapter = new TripCursorAdapter(this, cursor);
        tripView.setAdapter(tripCursorAdapter);
    }
}
