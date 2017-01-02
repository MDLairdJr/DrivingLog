package com.mikelaird.drivinglog.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.mikelaird.drivinglog.data.TripContract.TripEntry;

/**
 * Created by Mike on 12/11/2016.
 *
 * This subclass of SQLiteOpenHelper implements the methods
 * necessary to create a database to store driving details
 *
 * https://developer.android.com/training/basics/data-storage/databases.html
 * 
 * To view the contents of the database for debugging purposes:
 * 	1. Open the Android Device Monitor
 * 	2. Click the green Android icon to open the File Explorer to show the 
 * 		device's file system
 * 	3. Go to data/data/<app's package name> (e.g. com.mikelaird.drivinglog) 
 * 		to see the data associated with that application
 * 	4. download it through DDMS by pressing the pull a file from device icon
 * 	5. store the file locally and open it in SQLite from the command line
 */

public class TripDbOpenHelper extends SQLiteOpenHelper {

    /** Tag for the log messages */
    public static final String LOG_TAG = TripDbOpenHelper.class.getSimpleName();

    // Database details
    private static final String DATABASE_NAME = "driving_details.db";
    private static final int DATABASE_VERSION = 1;

    final private static String CREATE_CMD = "CREATE TABLE " + TripEntry.TABLE_NAME + " (" +
            TripEntry._ID + " integer primary key autoincrement, " +
            TripEntry.COLUMN_NAME_DATETIME + " integer not null, " +
            TripEntry.COLUMN_NAME_DURATION + " integer null, " +
            TripEntry.COLUMN_NAME_NOTES + " text null);";


    public TripDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Need to implement this later
    }
}
