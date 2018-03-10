package bignerdranch.android.earthquake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by mrx on 3/10/18.
 */

public class EarthqakeAlarmReceiver extends BroadcastReceiver {
    public static final String ACTION_REFRESH_EARTHQUAKE_ALARM = "com.bignerdranch.earthquake.ACTION_REFRESH_EARTHQUAKE_ALARM";
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startIntent = new Intent(context, EarthquakeService.class);
        context.startService(startIntent);
    }
}
