package bignerdranch.android.earthquake;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

/**
 * Created by modus on 3/7/18.
 */

public class EarthQuakeSearchResullts extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState){
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                new String[] {EarthquakeProvider.KEY_SUMMARY}, new int[] {android.R.id.text1}, 0);
        setListAdapter(adapter);

        getLoaderManager().initLoader(0,null,this);
        parseIntent(getIntent());
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        parseIntent(getIntent());
    }
    private void parseIntent(Intent intent){

    }
}
