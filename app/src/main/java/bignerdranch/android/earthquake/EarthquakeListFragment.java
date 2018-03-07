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

        URL url;
        try {
            String quakeFeed = getString(R.string.quake_feed);
            url = new URL(quakeFeed);
            URLConnection connection;
            connection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK){
                InputStream in = httpURLConnection.getInputStream();

                DocumentBuilderFactory dbf  = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();

                NodeList nl = docEle.getElementsByTagName("event");
                if (nl != null && nl.getLength() > 0) {
                    for (int i =0 ; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        Element title = (Element)entry.getElementsByTagName("description").item(0);
                        Element g =(Element)entry.getElementsByTagName("magnitude").item(0);
                        Element when = (Element)entry.getElementsByTagName("time").item(0);
                        Element longt = (Element)entry.getElementsByTagName("origin").item(0);
                        Element latit = (Element)entry.getElementsByTagName("origin").item(0);
                        Element link = (Element)entry.getElementsByTagName("creationInfo").item(0);

                        String longtitude = longt.getElementsByTagName("longitude").item(0).getTextContent();
                        String latitude = latit.getElementsByTagName("latitude").item(0).getTextContent();
                        String details = title.getElementsByTagName("text").item(0).getTextContent();
                        String mag = g.getElementsByTagName("mag").item(0).getFirstChild().getTextContent();
                        String hostname = "http://earthquake.usgs.goov";
                        String linkString = hostname + link.getAttribute("href");
                        String dt = when.getFirstChild().getFirstChild().getNodeValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S'Z'");
                        //Date qdate = new GregorianCalendar(0,0,0).getTime();
                        Date qdate = sdf.parse(dt);

                        Double dlongtitude = new Double(longtitude);
                        Double dlatitude = new Double(latitude);
                        //String[] location = point.split(" ");
                        Location l = new Location("dummyGPS");
                        l.setLatitude(dlatitude);
                        l.setLongitude(dlongtitude);



                       // int end = magnitudeString.length() -1 ;
                        Log.d("This is mag", mag);
                       Double magnitude = new Double(mag);
                        //String magnitude = magnitudeString;
                        //details = details.split(",")[1].trim();
                        final Quake quake = new Quake(qdate, details, l,  magnitude, linkString);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                addNewQuake(quake);
                            }
                        });

                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {

        }
    }
    @TargetApi(Build.VERSION_CODES.N)
    private void addNewQuake(Quake _quake) {
        ContentResolver cr = getActivity().getContentResolver();
        String w = EarthquakeProvider.KEY_DATE + " = " + _quake.getDate().getTime();

        Cursor query =  cr.query(EarthquakeProvider.CONTENT_URI, null, w, null, null);
        if (query.getCount()==0){
            ContentValues values = new ContentValues();
            values.put(EarthquakeProvider.KEY_DATE, _quake.getDate().getTime());
            values.put(EarthquakeProvider.KEY_SUMMARY, _quake.toString());

            String lat = "latitude";
            String lng = "longtitude";
            values.put(EarthquakeProvider.KEY_LOCATION_LAT, lat);
            values.put(EarthquakeProvider.KEY_LOCATION_LNG, lng);
            values.put(EarthquakeProvider.KEY_LINK, _quake.getLink());
            values.put(EarthquakeProvider.KEY_MAGNITUDE, _quake.getMagnitude());

            cr.insert(EarthquakeProvider.CONTENT_URI, values);

        }
        query.close();
    }


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
