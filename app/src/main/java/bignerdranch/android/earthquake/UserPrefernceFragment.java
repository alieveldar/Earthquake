package bignerdranch.android.earthquake;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by mrx on 3/3/18.
 */

public class UserPrefernceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);
    }
}
