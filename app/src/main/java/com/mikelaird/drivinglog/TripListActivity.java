package com.mikelaird.drivinglog;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mikelaird.drivinglog.data.TripContract.TripEntry;

public class TripListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Tag for the log messages */
    public static final String LOG_TAG = TripListActivity.class.getSimpleName();

    private static final int TRIP_LOADER = 0;
    TripCursorAdapter mTripCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        // kick off the cursor loader
        getLoaderManager().initLoader(TRIP_LOADER, null, this);

        ListView tripView = (ListView)findViewById(R.id.trip_list_view);
        mTripCursorAdapter = new TripCursorAdapter(this, null);
        tripView.setAdapter(mTripCursorAdapter);

        /*
         * Need to define an OnItemClickListener on the tripView to launch a new
         * activity when one of the trip items in the list is clicked.  Need to add
         * the URI to the data field of the intent
         */
        tripView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create the URI for the trip item that was clicked
                Uri uri = ContentUris.withAppendedId(TripEntry.CONTENT_URI, id);

                // We want to start a new TripDetailActivity to view the selected trip
                Intent intent = new Intent(TripListActivity.this, TripDetailActivity.class);

                // Add the URI to the intent
                intent.setData(uri);

                // launch the {@link TripDetailActivity} to display details of the selected trip
                startActivity(intent);
            }
        });

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
