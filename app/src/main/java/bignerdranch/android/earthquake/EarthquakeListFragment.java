package bignerdranch.android.earthquake;

import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
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

public class EarthquakeListFragment extends ListFragment {
    ArrayAdapter<Quake> mQuakeArrayAdapter;
    ArrayList<Quake> mQuakeArrayList = new ArrayList<Quake>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        int layOutId = android.R.layout.simple_list_item_1;
        mQuakeArrayAdapter = new ArrayAdapter<Quake>(getActivity(), layOutId, mQuakeArrayList);
        setListAdapter(mQuakeArrayAdapter);
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
                mQuakeArrayList.clear();
                NodeList nl = docEle.getElementsByTagName("event");
                if (nl != null && nl.getLength() > 0) {
                    for (int i =0 ; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        Element title = (Element)entry.getElementsByTagName("mag").item(0);
                        Element g =(Element)entry.getElementsByTagName("evaluationMode").item(0);
                        Element when = (Element)entry.getElementsByTagName("time").item(0);
                        Element link = (Element)entry.getElementsByTagName("creationInfo").item(0);

                        String details = title.getFirstChild().getFirstChild().getNodeValue();
                        String hostname = "http://earthquake.usgs.goov";
                        String linkString = hostname + link.getAttribute("href");

                        String point = g.getFirstChild().getNodeValue();
                        String dt = when.getFirstChild().getFirstChild().getNodeValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                        Date qdate = new GregorianCalendar(0,0,0).getTime();
                        try {
                            qdate = sdf.parse(dt);

                        }catch (ParseException e) {
                            Log.d(TAG, "Dateparsing exception", e);
                        }

                        //String[] location = point.split(" ");
                        //Location l = new Location("dummyGPS");
                        //l.setLatitude(Double.parseDouble(location[0]));
                        //l.setLongitude(Double.parseDouble(location[1]));
                        String l = "someone";

                        String magnitudeString = details;
                        int end =magnitudeString.length() -1 ;
                       Double magnitude = Double.parseDouble(magnitudeString);
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
        }
        finally {

        }
    }
    private void addNewQuake(Quake _quake) {
        ContentResolver cr = getActivity().getContentResolver();
        String w = EarthquakeProvider.KEY_DATE + " = " + _quake.getDate().getTime();

        Cursor query =  cr.query(EarthquakeProvider.CONTENT_URI, null, w, null, null);
        if (query.getCount()==0){
            ContentValues values = new ContentValues();
            values.put(EarthquakeProvider.KEY_DATE, _quake.getDate().getTime());
            values.put(EarthquakeProvider.KEY_SUMMARY, _quake.toString());

            double lat = _quake.getLocation().getLatitude();
            double lng = _quake.getLocation().getLongtitude();
            values.put(EarthquakeProvider.KEY_LOCATION_LAT, lat);
            values.put(EarthquakeProvider.KEY_LOCATION_LNG, lng);
            values.put(EarthquakeProvider.KEY_LINK, _quake.getLink());
            values.put(EarthquakeProvider.KEY_MAGNITUDE, _quake.getMagnitude());

            cr.insert(EarthquakeProvider.CONTENT_URI, values);

        }
        query.close();
    }

}
