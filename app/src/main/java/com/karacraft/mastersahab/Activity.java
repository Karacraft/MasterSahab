package com.karacraft.mastersahab;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.karacraft.utils.Constants;

/**
 * Created by       Ali Jibran / Kara Craft
 * For Project      Master Sahab
 * Dated            Nov 02 2015
 * File Name        Activity.java
 * Comments         Extends SingleFragmentActivty abstract class
 *                  Represents ActivityFragment.java
 *                  Uses CrimeListFragment.java
 *                  Mother of All Fragment Activities
 */

public class Activity extends SingleFragmentActivity{

    @Override
    protected Fragment CreateFragment() {
        //This won't compile with release built
        if (BuildConfig.DEBUG) {
            Log.d(Constants.APP_TAG, "Activity->CreateFragment:");
        }
        return new ActivityFragment();
    }
}
