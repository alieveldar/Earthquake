package bignerdranch.android.earthquake;

import android.icu.text.SimpleDateFormat;
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
    private double magnitude;
    private String link;

    public Quake(Date date, String detalis, Location location, double magnitude, String link) {
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

    public Location getLocation() {
        return mLocation;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getLink() {
        return link;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public String toString(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
        String dateString = sdf.format(mDate);
        return dateString + ": " + magnitude + " " + detalis;

    }
}
