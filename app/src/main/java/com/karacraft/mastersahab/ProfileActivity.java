package com.karacraft.mastersahab;

import android.support.v4.app.Fragment;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Nov 22 2015
 * File Name        ProfileActivity.java
 * Comments         Starts ProfileFragment.java
 */

public class ProfileActivity extends SingleFragmentActivity{

    @Override
    protected Fragment CreateFragment()
    {
        //Add fragment
        Fragment fragment = new ProfileFragment();
        return fragment;
    }
}
