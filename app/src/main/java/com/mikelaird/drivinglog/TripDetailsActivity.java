package com.mikelaird.drivinglog;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.mikelaird.drivinglog.data.TripContract.TripEntry;

public class TripDetailsActivity extends AppCompatActivity {

    /*
     * @todo When this activity starts, it causes a new trip to start
     * @todo This needs to be fixed.  Maybe use an AsyncTask in MainActivity?
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        TextView tripView = (TextView)findViewById(R.id.trip_details_text_view);

        // Define a projection that specifies which columns from the database
        // we will actually use for this query.
        String[] projection = {
                TripEntry._ID,
                TripEntry.COLUMN_NAME_DATETIME,
                TripEntry.COLUMN_NAME_DURATION,
                TripEntry.COLUMN_NAME_NOTES };

        Cursor cursor = getContentResolver().query(TripEntry.CONTENT_URI, projection, null,
                null, null);

        try {
            // Create a header row
            tripView.setText("The trips table has " + cursor.getCount() + " rows.\n\n");
            tripView.append(TripEntry._ID + " - " +
                TripEntry.COLUMN_NAME_DATETIME + " - " +
                TripEntry.COLUMN_NAME_DURATION + " - " +
                TripEntry.COLUMN_NAME_NOTES + "\n");

            // get the index of each column
            int idColumnIndex = cursor.getColumnIndex(TripEntry._ID);
            int datetimeColumnIndex = cursor.getColumnIndex(TripEntry.COLUMN_NAME_DATETIME);
            int durationColumnIndex = cursor.getColumnIndex(TripEntry.COLUMN_NAME_DURATION);
            int notesColumnIndex = cursor.getColumnIndex(TripEntry.COLUMN_NAME_NOTES);

            // iterate over the cursor
            while(cursor.moveToNext()) {
                // read each column in the current row
                int currentId = cursor.getInt(idColumnIndex);
                String currentDatetime = cursor.getString(datetimeColumnIndex);
                String currentDuration = cursor.getString(durationColumnIndex);
                String currentNotes = cursor.getString(notesColumnIndex);

                // append the row to the text view
                tripView.append("\n" + currentId + " - " +
                    currentDatetime + " - " +
                    currentDuration + " - " +
                    currentNotes);
            }
        } finally {
            cursor.close();
        }
    }
}
