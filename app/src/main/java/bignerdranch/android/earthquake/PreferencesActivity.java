package bignerdranch.android.earthquake;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

/**
 * Created by modus on 2/27/18.
 */

public class PreferencesActivity extends PreferenceActivity {
    public static final String PREF_MIN_MAG = "PREF_MIN_MAG";
    public static final String PREF_UPDATE_FREQ = "PREF_UPDATE_FREQ";
    public static final String PREF_AUTO_UPDDATE = "PREF_AUTO_UPDATE";

    SharedPreferences pref;




    @Override
    public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);
    }


}
