package com.mikelaird.drivinglog.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.mikelaird.drivinglog.data.TripContract.TripEntry;

public class TripProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = TripProvider.class.getSimpleName();

    // some constants to use with the UriMatcher
    private static final int TRIPS = 100;
    private static final int TRIP_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private TripDbOpenHelper mDbOpenHelper;

    static {
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS, TRIPS);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS + "/#", TRIP_ID);
    }

    public TripProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRIPS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = db.delete(TripEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRIP_ID:
                // Delete a single row given by the ID in the URI
                selection = TripEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(TripEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if(rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRIPS:
                return TripEntry.CONTENT_LIST_TYPE;
            case TRIP_ID:
                return TripEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch(match) {
            case TRIPS:
                return insertTrip(uri, values);
            default:
                throw new IllegalArgumentException("Insert not supported for " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        mDbOpenHelper = new TripDbOpenHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch(match) {
            case TRIPS:
                cursor = db.query(TripEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case TRIP_ID:
                selection = TripEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TripEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query invalid URI " + uri);

        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int match = sUriMatcher.match(uri);
        switch(match) {
            case TRIPS:
                return updateTrip(uri, values, selection, selectionArgs);
            case TRIP_ID:
                selection = TripEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateTrip(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot query invalid URI " + uri);

        }
    }

    private Uri insertTrip(Uri uri, ContentValues values) {

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        // do some data validation here

        long id = db.insert(TripEntry.TABLE_NAME, null, values);
        if(id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        Log.i(LOG_TAG,"Inserted datetime value of: " + values.getAsLong(TripEntry.COLUMN_NAME_DATETIME));

        // Notify all listeners that the data has changed for the trip content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateTrip(Uri uri, ContentValues values, String selection,
                           String[] selectionArgs) {

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        // do some data validation here

        int rowsUpdated =  db.update(TripEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
