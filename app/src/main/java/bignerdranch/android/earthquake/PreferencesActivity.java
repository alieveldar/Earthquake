package bignerdranch.android.earthquake;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import java.lang.Object;
import android.widget.BaseAdapter;


import java.io.IOException;

import static android.widget.ArrayAdapter.createFromResource;
import static junit.runner.BaseTestRunner.savePreferences;

/**
 * Created by modus on 2/27/18.
 */

public class PreferencesActivity extends Activity {
    private static final String USER_PREFERENCE = "USER_PREFERENCE";
    public static final String PREF_AUTO_UPDDATE = "PREF_AUTO_UPDATE";
    public static final String PREF_MIN_MAG_INDEX = "PREF_MIN_MAG_INDEX";
    public static final String PREF_UPDATE_FREQ_INDEX = "PREF_UPDATE_FREQ_INDEX";

    SharedPreferences pref;



    CheckBox autoUpdate;
    Spinner updateFreqSpinner;
    Spinner magnitudeSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        autoUpdate = (CheckBox)findViewById(R.id.auto_update_checkbox);
        updateFreqSpinner = (Spinner)findViewById(R.id.spinner_update_frequency);
        magnitudeSpinner = (Spinner)findViewById(R.id.spinner_quake_mag);

        populateSpinners();

        Context context = getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        updateUIFFromPrefereces();

        Button okButton = (Button)findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreferences();
                PreferencesActivity.this.setResult(RESULT_OK);
                finish();
            }
        });

        Button cancelButton = (Button)findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesActivity.this.setResult(RESULT_CANCELED);
                finish();
            }
        });


    }

    private void savePreferences(){
        int updateIndex = updateFreqSpinner.getSelectedItemPosition();
        int minMagIndex = magnitudeSpinner.getSelectedItemPosition();
        boolean autoUpdateChrcked = autoUpdate.isChecked();
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_AUTO_UPDDATE, autoUpdateChrcked);
        editor.putInt(PREF_MIN_MAG_INDEX, minMagIndex);
        editor.putInt(PREF_UPDATE_FREQ_INDEX, updateIndex);
        editor.commit();

    }

    private void populateSpinners(){
        ArrayAdapter<CharSequence> fAdapter = ArrayAdapter.createFromResource(this, R.array.update_freg_options, android.R.layout.simple_spinner_item);
        int spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
        fAdapter.setDropDownViewResource(spinner_dd_item);
        updateFreqSpinner.setAdapter(fAdapter);
        ArrayAdapter<CharSequence> mAdapter = ArrayAdapter.createFromResource(this, R.array.magnitude_options, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(spinner_dd_item);
        magnitudeSpinner.setAdapter(mAdapter);
    }
    private void updateUIFFromPrefereces(){
        boolean autoUpChecked = pref.getBoolean(PREF_AUTO_UPDDATE, false);
        int updateFreqIndex = pref.getInt(PREF_UPDATE_FREQ_INDEX, 2);
        int minMagIndex = pref.getInt(PREF_MIN_MAG_INDEX, 0);
        updateFreqSpinner.setSelection(updateFreqIndex);
        magnitudeSpinner.setSelection(minMagIndex);
        autoUpdate.setChecked(autoUpChecked);
    }


}
