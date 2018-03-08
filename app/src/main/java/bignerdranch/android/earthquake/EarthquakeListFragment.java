package bignerdranch.android.earthquake;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by mrx on 2/23/18.
 */

public class EarthquakeListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter adapter;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, new String[] {EarthquakeProvider.KEY_SUMMARY}, new int[] { android.R.id.text1}, 0);
        setListAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                refreshEarthQuakes();
            }
        });

        t.start();
    }

    private static final String  TAG = "EARTQUAKE";
    private Handler mHandler = new Handler();
    public void refreshEarthQuakes(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                getLoaderManager().restartLoader(0, null, EarthquakeListFragment.this);
            }
        });
    }


    }
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

        CursorLoader loader = new CursorLoader(getActivity(), EarthquakeProvider.CONTENT_URI, projection, where, null,null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
