package com.mikelaird.drivinglog;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.mikelaird.drivinglog.data.TripContract.TripEntry;

public class TripDetailsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Tag for the log messages */
    public static final String LOG_TAG = TripDetailsActivity.class.getSimpleName();

    private static final int TRIP_LOADER = 0;
    TripCursorAdapter mTripCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        // kick off the cursor loader
        getLoaderManager().initLoader(TRIP_LOADER, null, this);

        ListView tripView = (ListView)findViewById(R.id.trip_details_list_view);
        mTripCursorAdapter = new TripCursorAdapter(this, null);
        tripView.setAdapter(mTripCursorAdapter);

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
                this,                       // parent activity context
                TripEntry.CONTENT_URI,      // provider contentURI
                projection,                 // columns to include in the query
                null,                       // where clause
                null,                       // where clause arguments
                null);                      // sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mTripCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTripCursorAdapter.swapCursor(null);
    }
}
