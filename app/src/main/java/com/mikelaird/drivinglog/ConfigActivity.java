package com.mikelaird.drivinglog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class ConfigActivity extends AppCompatActivity {

    public static final String TOTAL_TIME = "com.mikelaird.drivinglog.totalTime";
    public static final String NIGHT_TIME = "com.mikelaird.drivinglog.nightTime";

    private NumberPicker totalHr10Picker;
    private NumberPicker totalHr1Picker;
    private NumberPicker totalMin10Picker;
    private NumberPicker totalMin1Picker;
    private NumberPicker totalSec10Picker;
    private NumberPicker totalSec1Picker;
    private NumberPicker nightHr10Picker;
    private NumberPicker nightHr1Picker;
    private NumberPicker nightMin10Picker;
    private NumberPicker nightMin1Picker;
    private NumberPicker nightSec10Picker;
    private NumberPicker nightSec1Picker;
    private Button saveConfigButton;
    private Button cancelConfigButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // Get references to widgets
        totalHr10Picker = (NumberPicker)findViewById(R.id.totalHr10Picker);
        totalHr1Picker = (NumberPicker)findViewById(R.id.totalHr1Picker);
        totalMin10Picker = (NumberPicker)findViewById(R.id.totalMin10Picker);
        totalMin1Picker = (NumberPicker)findViewById(R.id.totalMin1Picker);
        totalSec10Picker = (NumberPicker)findViewById(R.id.totalSec10Picker);
        totalSec1Picker = (NumberPicker)findViewById(R.id.totalSec1Picker);
        nightHr10Picker = (NumberPicker)findViewById(R.id.nightHr10Picker);
        nightHr1Picker = (NumberPicker)findViewById(R.id.nightHr1Picker);
        nightMin10Picker = (NumberPicker)findViewById(R.id.nightMin10Picker);
        nightMin1Picker = (NumberPicker)findViewById(R.id.nightMin1Picker);
        nightSec10Picker = (NumberPicker)findViewById(R.id.nightSec10Picker);
        nightSec1Picker = (NumberPicker)findViewById(R.id.nightSec1Picker);
        saveConfigButton = (Button)findViewById(R.id.saveConfigButton);
        cancelConfigButton = (Button)findViewById(R.id.cancelConfigButton);

        // Set the ranges for the pickers
        totalHr10Picker.setMinValue(0);
        totalHr10Picker.setMaxValue(9);
        totalHr1Picker.setMinValue(0);
        totalHr1Picker.setMaxValue(9);
        totalMin10Picker.setMinValue(0);
        totalMin10Picker.setMaxValue(5);
        totalMin1Picker.setMinValue(0);
        totalMin1Picker.setMaxValue(9);
        totalSec10Picker.setMinValue(0);
        totalSec10Picker.setMaxValue(5);
        totalSec1Picker.setMinValue(0);
        totalSec1Picker.setMaxValue(9);

        nightHr10Picker.setMinValue(0);
        nightHr10Picker.setMaxValue(9);
        nightHr1Picker.setMinValue(0);
        nightHr1Picker.setMaxValue(9);
        nightMin10Picker.setMinValue(0);
        nightMin10Picker.setMaxValue(5);
        nightMin1Picker.setMinValue(0);
        nightMin1Picker.setMaxValue(9);
        nightSec10Picker.setMinValue(0);
        nightSec10Picker.setMaxValue(5);
        nightSec1Picker.setMinValue(0);
        nightSec1Picker.setMaxValue(9);

        if (getIntent().getBooleanExtra(MainActivity.IS_RUNNING, true)) {
            saveConfigButton.setEnabled(false);
        }

        saveConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long totTimeMillis = totalSec1Picker.getValue() * 1000;
                totTimeMillis += totalSec10Picker.getValue() * 10 * 1000;
                totTimeMillis += totalMin1Picker.getValue() * 60 * 1000;
                totTimeMillis += totalMin10Picker.getValue() * 600 * 1000;
                totTimeMillis += totalHr1Picker.getValue() * 3600 * 1000;
                totTimeMillis += totalHr10Picker.getValue() * 36000 *1000;

                long nightTimeMillis = nightSec1Picker.getValue() * 1000;
                nightTimeMillis += nightSec10Picker.getValue() * 10 * 1000;
                nightTimeMillis += nightMin1Picker.getValue() * 60 * 1000;
                nightTimeMillis += nightMin10Picker.getValue() * 600 * 1000;
                nightTimeMillis += nightHr1Picker.getValue() * 3600 * 1000;
                nightTimeMillis += nightHr10Picker.getValue() * 36000 *1000;

                Intent resultIntent = new Intent(getBaseContext(), MainActivity.class);
                resultIntent.putExtra(TOTAL_TIME, totTimeMillis);
                resultIntent.putExtra(NIGHT_TIME, nightTimeMillis);
                setResult(RESULT_OK, resultIntent);

                finish();
            }

        });

        cancelConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent resultIntent = new Intent(getBaseContext(), MainActivity.class);
                setResult(RESULT_CANCELED, resultIntent);

                finish();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_config, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
