package bignerdranch.android.earthquake;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Earthquake extends AppCompatActivity {
static final private int MENU_PREFERENCES = Menu.FIRST + 1;
static final private int MENU_UPDATE = Menu.FIRST + 2;
private static final int SHOW_PREFERENCES = 1;

    public int minimumMagnitude = 0;
    public boolean autoUpdateChecked = false;
    public int updateFreq = 0;

@Override
public boolean onCreateOptionsMenu(Menu menu){
    super.onCreateOptionsMenu(menu);
    menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);
    return true;
}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        updateFromPreferences();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case (MENU_PREFERENCES): {
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivityForResult(i, SHOW_PREFERENCES);
                return true;
            }
        }
        return false;
    }

    private void updateFromPreferences(){
        Context context = getApplicationContext();
        SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(context);

        minimumMagnitude = Integer.parseInt(spref.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
        updateFreq = Integer.parseInt(spref.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
        autoUpdateChecked = spref.getBoolean(PreferencesActivity.PREF_AUTO_UPDDATE, false);



    }

    @Override
    public void onActivityResult(int requestCode, int rezultCode, Intent  data ){
        super.onActivityResult(requestCode, requestCode, data);
        if (requestCode == SHOW_PREFERENCES)
            updateFromPreferences();

                FragmentManager fm = getFragmentManager();
                final EarthquakeListFragment earthQuakeList = (EarthquakeListFragment)fm.findFragmentById(R.id.Earthquakelistfragment);

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        earthQuakeList.refreshEarthQuakes();
                    }
                });
                t.start();

    }

}
