package com.mikelaird.drivinglog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class TripDetailActivity extends AppCompatActivity {

    /** Tag for the log messages */
    public static final String LOG_TAG = TripDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Inside the onCreate() method of TripDetailActivity . . . ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        // Get references to widgets
        TextView detailDateTime = (TextView)findViewById(R.id.trip_detail_datetime);
        detailDateTime.setText("Testing, testing, testing . . . ");

    }

}
