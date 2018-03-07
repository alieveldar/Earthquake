package bignerdranch.android.earthquake;

import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Date;

/**
 * Created by mrx on 2/24/18.
 */

public class Quake {
    private Date mDate;
    private String detalis;
    private Location mLocation;
    private Double magnitude;
    private String link;

    public Quake(Date date, String detalis, Location location, Double magnitude, String link) {
        mDate = date;
        this.detalis = detalis;
        mLocation = location;
        this.magnitude = magnitude;
        this.link = link;
    }


    public Date getDate() {
        return mDate;
    }

    public String getDetalis() {
        return detalis;
    }

    public String getLocation() {
        String location = new String(String.valueOf(mLocation));
        return location;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    public String getLink() {
        return link;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public String toString(){
//        SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
  //      String dateString = sdf.format(mDate);
        return mDate + ": " + magnitude + " " + detalis;

    }
}
