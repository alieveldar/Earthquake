package bignerdranch.android.earthquake;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by modus on 3/8/18.
 */

public class EarthquakeService extends Service {
    private static final String TAG = "EARTHQUAKE_UPDATE_SERVICE";
    private Timer updateTimer;


    public int onStartommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int updateFreq = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
        boolean autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDDATE, false);

        updateTimer.cancel();
        if (autoUpdateChecked) {
            updateTimer = new Timer("earthquakeUpdates");
            updateTimer.scheduleAtFixedRate(doreFresh, 0, updateFreq * 60 * 1000);
        } else {
            Thread t = new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {
                    refreshEarthQuakes();
                }
            });
            t.start();
        }
        return Service.START_STICKY;
    };

    private TimerTask doreFresh = new TimerTask() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            refreshEarthQuakes();
        }
    };

    @Override
    public void onCreate(){
        super.onCreate();
        updateTimer = new Timer("earthquakeUpdates");
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addNewQuake(Quake _quake) {
        ContentResolver cr = getContentResolver();
        String w = EarthquakeProvider.KEY_DATE + " = " + _quake.getDate().getTime();

        Cursor query = cr.query(EarthquakeProvider.CONTENT_URI, null, w, null, null);
        if (query.getCount() == 0) {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void refreshEarthQuakes() {

        URL url;
        try {
            String quakeFeed = getString(R.string.quake_feed);
            url = new URL(quakeFeed);
            URLConnection connection;
            connection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpURLConnection.getInputStream();

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();

                NodeList nl = docEle.getElementsByTagName("event");
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element entry = (Element) nl.item(i);
                        Element title = (Element) entry.getElementsByTagName("description").item(0);
                        Element g = (Element) entry.getElementsByTagName("magnitude").item(0);
                        Element when = (Element) entry.getElementsByTagName("time").item(0);
                        Element longt = (Element) entry.getElementsByTagName("origin").item(0);
                        Element latit = (Element) entry.getElementsByTagName("origin").item(0);
                        Element link = (Element) entry.getElementsByTagName("creationInfo").item(0);

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
                        final Quake quake = new Quake(qdate, details, l, magnitude, linkString);


                        addNewQuake(quake);
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        finally {

        }
    }
}