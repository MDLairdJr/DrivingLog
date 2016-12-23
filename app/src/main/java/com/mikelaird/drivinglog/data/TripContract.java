package com.mikelaird.drivinglog.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mike on 12/19/2016.
 */

public final class TripContract {

    /** Tag for the log messages */
    public static final String LOG_TAG = TripContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.mikelaird.drivinglog";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TRIPS = "trips";

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TripContract() {}

    //Inner class that defines the table contents
    public static class TripEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_TRIPS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRIPS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRIPS;

        public static final String TABLE_NAME = "trips";

        // Database columns
        public static final String COLUMN_NAME_DATETIME = "datetime";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_NOTES = "notes";
    }
}
