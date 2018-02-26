package bignerdranch.android.earthquake;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Date;

/**
 * Created by mrx on 2/24/18.
 */

public class Quake {
    private Date mDate;
    private String detalis;
    private String mLocation;
    private String magnitude;
    private String link;

    public Quake(Date date, String detalis, String location, String magnitude, String link) {
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
        return mLocation;
    }

    public String getMagnitude() {
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
        return mDate + ": " + magnitude + " " + detalis + " : " +mLocation;

    }
}