package bignerdranch.android.earthquake;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;


public class TabListener<T extends android.support.v4.app.Fragment> implements ActionBar.TabListener {
    public Fragment fragment;
    private Activity activity;
    private Class<T> fragmentClass;
    private int fragmentContainer;

    public TabListener(Activity activity, int fragmentContainer, Class<T> fragmentClass) {
        this.activity = activity;
        this.fragmentContainer = fragmentContainer;
        this.fragmentClass = fragmentClass;

    }

    @Override
    public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
        if (fragment == null){
            String fragmentName = fragmentClass.getName();
            fragment = Fragment.instantiate(activity, fragmentName);
            ft.add(fragmentContainer, fragment, fragmentName);
        }else {
            ft.attach(fragment);
        }
    }

    @Override
    public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
        if (fragment != null)
            ft.detach(fragment);
    }

    @Override
    public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
        if (fragment != null)
            ft.attach(fragment);
    }
}