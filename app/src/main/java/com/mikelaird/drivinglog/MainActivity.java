package com.mikelaird.drivinglog;

import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.mikelaird.drivinglog.data.TripContract.TripEntry;


public class MainActivity extends AppCompatActivity
                implements NewTripDialogFragment.NewTripDialogListener {

    /** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final int CONFIG_CODE = 1;
    public static final String IS_RUNNING = "isRunning";

    private TextView tvTotalTime;
    private TextView tvTripTime;
    private TextView tvNightTime;
    private Button timerButton;
    private EditText driveLogEditText;
    private Button driveLogSaveButton;
    private Button driveLogViewButton;

    private boolean isRunning = false;
    private boolean isNight = false;
    private boolean isNewTrip = true;
    private long tripId;

    private Handler timerHandler = new Handler();

    private long startTime = 0L;        // The system time as of when the Start button is clicked
    long elapsedTime = 0L;              // Current system time minus the startTime
    long priorTripElapsedTime = 0L;     // The trip elapsed time as of the last click of Stop
    long tripElapsedTime = 0L;          // Sum of priorTripElapsedTime plus current elapsedTime
    long priorTotalElapsedTime = 0L;    // The total elapsed time as of the last click of Stop
    long totalElapsedTime = 0L;         // Sum of priorTotalElapsedTime plus current elapsedTime
    long totalNightTime = 0L;           // Sum of priorTotalNightTime plus current elapsedTime
    long priorTotalNightTime = 0L;      // The total night time as of the last click of Stop

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Inside the onCreate() method of MainActivity . . . ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get references to widgets
        Button newTripButton = (Button)findViewById(R.id.newTripButton);
        Switch nightSwitch = (Switch)findViewById(R.id.nightSwitch);
        tvTotalTime = (TextView)findViewById(R.id.totalTime);
        tvTripTime = (TextView)findViewById(R.id.tripTime);
        tvNightTime = (TextView)findViewById(R.id.totalNightTime);
        timerButton = (Button)findViewById(R.id.timerButton);
        driveLogEditText = (EditText)findViewById(R.id.driveLogEditText);
        driveLogSaveButton = (Button)findViewById(R.id.driveLogSaveButton);
        driveLogViewButton = (Button)findViewById(R.id.driveLogViewButton);

        restorePersistedTimeValues();

        // Disable the driveLogSaveButton
        driveLogSaveButton.setEnabled(false);

        timerButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (isRunning) {
                    // Stop button was pressed
                    isRunning = false;
                    timerButton.setText(R.string.start_button_text);

                    priorTripElapsedTime = tripElapsedTime;
                    priorTotalElapsedTime = totalElapsedTime;
                    priorTotalNightTime = totalNightTime;
                    timerHandler.removeCallbacks(updateTimerThread);

                } else {
                    //Start button was pressed
                    isRunning = true;
                    timerButton.setText(R.string.stop_button_text);

                    // capture the system time when the start button is clicked
                    startTime = SystemClock.elapsedRealtime();

                    // now call a Handler to start a separate thread to update the timer
                    timerHandler.post(updateTimerThread);

                    // Enable the driveLogSaveButton
                    driveLogSaveButton.setEnabled(true);

                    // if this is a new trip, create an entry in the database
                    if(isNewTrip) {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put(TripEntry.COLUMN_NAME_DATETIME, System.currentTimeMillis());
                        Uri uri = getContentResolver().insert(TripEntry.CONTENT_URI, initialValues);
                        if(uri == null) {
                            throw new IllegalArgumentException("Failed to insert new trip using " + uri);
                        }
                        tripId = ContentUris.parseId(uri);
                    }

                    isNewTrip = false;

                }

            }
        });

        newTripButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment dialog = new NewTripDialogFragment();
                dialog.show(getFragmentManager(),"NewTripDialogFragment");

//                // Stop the timer if it is running
//                isRunning = false;
//                isNewTrip = true;
//                timerButton.setText(R.string.start_button_text);
//
//                // Disable the driveLogSaveButton
//                driveLogSaveButton.setEnabled(false);
//                driveLogEditText.setText(null);
//
//                //end the current trip
//                persistTripDetails();
//                priorTripElapsedTime = 0;
//                priorTotalElapsedTime = totalElapsedTime;
//                priorTotalNightTime = totalNightTime;
//                timerHandler.removeCallbacks(updateTimerThread);
//
//                tvTripTime.setText(String.format("%02d", 0) + ":" +
//                        String.format("%02d", 0) + ":" +
//                        String.format("%02d", 0));
            }

        });

        nightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Stop the timer if it is running
                if (isRunning) {
                    isRunning = false;
                    timerButton.setText(R.string.start_button_text);
                    priorTripElapsedTime = tripElapsedTime;
                    priorTotalElapsedTime = totalElapsedTime;
                    priorTotalNightTime = totalNightTime;
                    timerHandler.removeCallbacks(updateTimerThread);
                }

                if (isChecked) {
                    // It is night time
                    isNight = true;
                } else {
                    // It is day time
                    isNight = false;
                }
            }
        });

        driveLogEditText.setHorizontallyScrolling(false);
        driveLogEditText.setMaxLines(Integer.MAX_VALUE);
        driveLogEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        driveLogEditText.setImeActionLabel(getResources().getString(R.string.done_button_text), EditorInfo.IME_ACTION_DONE);

        // Is this the right listener to implement?  Need to learn more about these . . .
        // https://github.com/codepath/android_guides/wiki/Basic-Event-Listeners
        // This one fires when an "action" button on the soft keyboard is pressed
        driveLogEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE) {
                    driveLogEditText.clearFocus();
                }
                return false;
            }
        });

        driveLogSaveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                persistTripDetails();
            }
        });

        driveLogViewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // We want to start a new ListActivity to view the stored trips
                Intent intent = new Intent(MainActivity.this, TripListActivity.class);
                startActivity(intent);
            }
        });

    }

    private Runnable updateTimerThread = new Runnable() {

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            elapsedTime =  SystemClock.elapsedRealtime() - startTime;
            tripElapsedTime = priorTripElapsedTime + elapsedTime;
            totalElapsedTime = priorTotalElapsedTime + elapsedTime;

            updateTimeText(tvTripTime, tripElapsedTime);
            updateTimeText(tvTotalTime, totalElapsedTime);

            if(isNight){
                totalNightTime = priorTotalNightTime + elapsedTime;
                updateTimeText(tvNightTime, totalNightTime);
            }

            timerHandler.post(this);
        }
    };

    public void restorePersistedTimeValues() {

        // Restore persisted time values
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        priorTotalElapsedTime = settings.getLong("totalDriveTime",0);
        totalElapsedTime = priorTotalElapsedTime;
        priorTotalNightTime = settings.getLong("totalNightTime",0);
        totalNightTime = priorTotalNightTime;

        // Restore state of driveLogSaveButton
        driveLogSaveButton.setEnabled(settings.getBoolean("isSaveButtonEnabled",false));

        // Initialize the display with the persisted time values
        updateTimeText(tvTotalTime, priorTotalElapsedTime);
        updateTimeText(tvNightTime, priorTotalNightTime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_config) {
            /*
             * Need to figure out what to do here.  Now that we have a database that stores
             * all of the actual trip time details, it doesn't make sense to allow the user
             * to set these values directly.
             */
//            Intent intent = new Intent(getBaseContext(), ConfigActivity.class);
//            intent.putExtra(IS_RUNNING, isRunning);
//            startActivityForResult(intent, CONFIG_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONFIG_CODE) {
            if (resultCode == RESULT_OK) {
                persistTotalTime(data.getLongExtra(ConfigActivity.TOTAL_TIME, totalElapsedTime));
                persistTotalNightTime(data.getLongExtra(ConfigActivity.NIGHT_TIME, totalNightTime));
                restorePersistedTimeValues();
                priorTripElapsedTime = 0L;
                tripElapsedTime = priorTripElapsedTime;
                updateTimeText(tvTripTime, tripElapsedTime);
            }
        }
    }

    protected void onStart() {
        Log.i(LOG_TAG, "Inside the onStart() method of MainActivity . . . ");
        super.onStart();
    }

    protected void onPause() {
        Log.i(LOG_TAG, "Inside the onPause() method of MainActivity . . . ");
        persistTripDetails();
        super.onPause();
    }

    protected void onStop(){
        Log.i(LOG_TAG, "Inside the onStop() method of MainActivity . . . ");
        super.onStop();
    }

    protected void onDestroy() {
        Log.i(LOG_TAG, "Inside the onDestroy() method of MainActivity . . . ");
        super.onDestroy();
    }

    private void persistTripDetails() {
        // Let's derive the URI for the current trip record in the database
        Uri uri = ContentUris.withAppendedId(TripEntry.CONTENT_URI, tripId);

        // Now create a ContentValues object and set the value of the fields
        ContentValues values = new ContentValues();
        values.put(TripEntry.COLUMN_NAME_NOTES, driveLogEditText.getText().toString());
        values.put(TripEntry.COLUMN_NAME_DURATION, tripElapsedTime);
        values.put(TripEntry.COLUMN_NAME_TOTAL_TIME, totalElapsedTime);
        values.put(TripEntry.COLUMN_NAME_NIGHT_TIME, totalNightTime);

        // Call the update method on the ContentResolver to perform the database update
        getContentResolver().update(uri, values, null, null);

        // now store the time details in SharedPreferences
        persistTotalTime(totalElapsedTime);
        persistTotalNightTime(totalNightTime);
        persistIsSaveButtonEnabled(driveLogSaveButton.isEnabled());
    }

    private void persistTotalTime(long time) {
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("totalDriveTime", time);
        editor.apply();
    }

    private void persistTotalNightTime(long time) {
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("totalNightTime", time);
        editor.apply();
    }

    private void persistIsSaveButtonEnabled(boolean value) {
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isSaveButtonEnabled", value);
        editor.apply();
    }

    private void updateTimeText(TextView view, long time) {
        int secs = (int)(time/1000);
        int mins = secs/60;
        secs = secs % 60;
        int hrs = mins/60;
        mins = mins % 60;

        view.setText(String.format("%02d", hrs) + ":" +
                String.format("%02d", mins) + ":" +
                String.format("%02d", secs));
    }

    @Override
    public void onNewTripConfirmClick(DialogFragment dialog) {
        // Stop the timer if it is running
        isRunning = false;
        isNewTrip = true;
        timerButton.setText(R.string.start_button_text);

        // Disable the driveLogSaveButton
        driveLogSaveButton.setEnabled(false);
        driveLogEditText.setText(null);

        //end the current trip
        persistTripDetails();
        priorTripElapsedTime = 0;
        priorTotalElapsedTime = totalElapsedTime;
        priorTotalNightTime = totalNightTime;
        timerHandler.removeCallbacks(updateTimerThread);

        tvTripTime.setText(String.format("%02d", 0) + ":" +
                String.format("%02d", 0) + ":" +
                String.format("%02d", 0));
    }

    @Override
    public void onNewTripCancelClick(DialogFragment dialog) {

    }

}
