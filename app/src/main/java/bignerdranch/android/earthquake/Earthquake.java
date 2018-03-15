package bignerdranch.android.earthquake;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

;

public class Earthquake extends AppCompatActivity {
    static final private int MENU_PREFERENCES = Menu.FIRST + 1;
    static final private int MENU_UPDATE = Menu.FIRST + 2;
    private static final int SHOW_PREFERENCES = 1;

    public int minimumMagnitude = 0;
    public boolean autoUpdateChecked = false;
    public int updateFreq = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

        return true;
    }

    TabListener<EarthquakeListFragment> listTabListener;
    TabListener<EarthquakeMapFragment> mapTabListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        updateFromPreferences();


        ActionBar actionBar = getActionBar();

        View fragmentContainer = findViewById(R.id.EarthquakeFragmentContainer);

        boolean tabletLayout = fragmentContainer == null;

        if (!tabletLayout){
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayShowTitleEnabled(false);

            android.app.ActionBar.Tab listTab = actionBar.newTab();

            listTabListener = new TabListener<EarthquakeListFragment>(this, R.id.EarthquakeFragmentContainer, EarthquakeListFragment.class);
            listTab.setText("List").setContentDescription("List of earthquakes").setTabListener(listTabListener);
            actionBar.addTab(listTab);

            ActionBar.Tab mapTab = actionBar.newTab();

            mapTabListener = new TabListener<EarthquakeMapFragment>(this, R.id.EarthquakeMapFragment, EarthquakeMapFragment.class);
            mapTab.setText("Map").setContentDescription("Map of earthquakes").setTabListener(mapTabListener);
            actionBar.addTab(mapTab);
        }

    }


    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case (R.id.menu_refresh): {
                startService(new Intent(this, EarthquakeService.class));
                return true;
            }
            case (R.id.menu_preferences): {
                Class c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ?
                        PreferenceActivity.class : FragmentPreferences.class;
                Intent i = new Intent(this, c);
                startActivityForResult(i, SHOW_PREFERENCES);
                return true;
            }
        }
        return false;
    }

    private void updateFromPreferences() {
        Context context = getApplicationContext();
        SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(context);

        minimumMagnitude = Integer.parseInt(spref.getString(PreferencesActivity.PREF_MIN_MAG, "1"));
        updateFreq = Integer.parseInt(spref.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
        autoUpdateChecked = spref.getBoolean(PreferencesActivity.PREF_AUTO_UPDDATE, false);


    }

    @Override
    public void onActivityResult(int requestCode, int rezultCode, Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        if (requestCode == SHOW_PREFERENCES) {
            updateFromPreferences();
            startService(new Intent(this, EarthquakeService.class));

        }

    }

    private static String ACTION_BAR_INDEX = "ACTION_BAR_INDEX";

    @Override
    public void onSaveInstanceState(Bundle outState){
        View fragmentConatainer = findViewById(R.id.EarthquakeFragmentContainer);
        boolean tabletLayout = fragmentConatainer == null;

        if (!tabletLayout){
            int actionBarIndex = getActionBar().getSelectedTab().getPosition();
            SharedPreferences.Editor editor = getPreferences(Activity.MODE_PRIVATE).edit();
            editor.putInt(ACTION_BAR_INDEX, actionBarIndex);
            editor.apply();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (mapTabListener.fragment != null)
                ft.detach(mapTabListener.fragment);
            if (listTabListener.fragment != null)
                ft.detach(listTabListener.fragment);
            ft.commit();

        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        View fragmentContainer = findViewById(R.id.EarthquakeFragmentContainer);
        boolean tabletLayout = fragmentContainer == null;

        if (!tabletLayout){
            SharedPreferences sp = getPreferences(Activity.MODE_PRIVATE);
            int actionBarIndex =  sp.getInt(ACTION_BAR_INDEX, 0);
            getActionBar().setSelectedNavigationItem(actionBarIndex);
        }
    }
}