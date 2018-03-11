package bignerdranch.android.earthquake;

//import android.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

/**
 * Created by mrx on 2/23/18.
 */

public class EarthquakeListFragment extends android.support.v4.app.ListFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter adapter;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, new String[] {EarthquakeProvider.KEY_SUMMARY}, new int[] { android.R.id.text1}, 0);
        setListAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);

        refreshEarthQuakes();
    }

    private static final String  TAG = "EARTQUAKE";



    //@TargetApi(Build.VERSION_CODES.N)



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[] {
                EarthquakeProvider.KEY_ID,
                EarthquakeProvider.KEY_SUMMARY
        };

        Earthquake earthquakeActivvity = (Earthquake)getActivity();
        String where = EarthquakeProvider.KEY_MAGNITUDE + " > " +
                earthquakeActivvity.minimumMagnitude;

        Loader<Cursor> loader = new CursorLoader(getActivity(), EarthquakeProvider.CONTENT_URI, projection, where, null,null);
        return loader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    public void refreshEarthQuakes(){
        getLoaderManager().restartLoader(0,null,EarthquakeListFragment.this);
        getActivity().startService(new Intent(getActivity(), EarthquakeService.class));
    }
}
