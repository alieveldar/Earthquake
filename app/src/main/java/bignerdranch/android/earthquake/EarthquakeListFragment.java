package bignerdranch.android.earthquake;

import android.app.ListFragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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

    ArrayAdapter<Quake> aa;
    ArrayList<Quake> earthquakes = new ArrayList<Quake>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        int layOutId = android.R.layout.simple_list_item_1;
        aa = new ArrayAdapter<Quake>(getActivity(), layOutId, earthquakes);
        setListAdapter(aa);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                refreshEarthQuakes();

            }
        });
        t.start();
    }

    private static final String  TAG = "EARTHQUAKE";
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
                //Log.d("Earthquake", "HTTTP IS OK");


                InputStream in = httpURLConnection.getInputStream();
              //  Log.d("Earthquake", "INPUT STREAM IS OK");
                DocumentBuilderFactory dbf  = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();

                earthquakes.clear();
               // Log.d("Earthquake", "earthquakelist is clear IS OK");
                NodeList nl = docEle.getElementsByTagName("event");
               // Log.d("Earthquake", nl.getClass().toString());
                if (nl != null && nl.getLength() > 0) {
                   // Log.d("Earthquake", "Node list is not empty");
                    for (int i =0 ; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        Element title = (Element)entry.getElementsByTagName("time").item(0);
                        Element g =(Element)entry.getElementsByTagName("longitude").item(0);
                        Element when = (Element)entry.getElementsByTagName("longitude").item(0);
                        Element link = (Element)entry.getElementsByTagName("longitude").item(0);

                        String details = "no details"; //title.getFirstChild().getNodeValue();
                        String hostname = "http://earthquake.usgs.goov";
                     //   String linkString = hostname + link.getAttribute("href");
                            String linkString = "www.google.com";
                        String point =  "MOSCOW";   //g.getFirstChild().getNodeValue();
                        String dt = "20121201";  //when.getFirstChild().getNodeValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                        Date qdate = new GregorianCalendar(0,0,0).getTime();
                        try {
                            qdate = sdf.parse(dt);

                        }catch (ParseException e) {
                            Log.d(TAG, "Dateparsing exception", e);
                        }

                        String[] location = point.split(" ");
                        Location l = new Location("dummyGPS");
                        //l.setLatitude(Double.parseDouble(location[0]));
                        //l.setLongitude(Double.parseDouble(location[1]));

                        String magnitudeString = details.split(" ")[1];
                        int end =magnitudeString.length() -1 ;
                        Double magnitude = 4.00; //Double.parseDouble(magnitudeString.substring(0, end));
   //                     details = details.split(",")[1].trim();

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
        earthquakes.add(_quake);
        aa.notifyDataSetChanged();
    }

}
