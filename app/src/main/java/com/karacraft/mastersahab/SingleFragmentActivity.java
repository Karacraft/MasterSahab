package com.karacraft.mastersahab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.karacraft.utils.Constants;
/**
 * Created by       Ali Jibran / Kara Craft
 * For Project      Master Sahab
 * Dated            Nov 02 2015
 * File Name        SingleFragmentActivity.java
 * Comments
 */
public abstract class SingleFragmentActivity extends FragmentActivity {

    protected abstract Fragment CreateFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        //This won't compile with release built
        if (BuildConfig.DEBUG) {
            Log.d(Constants.APP_TAG, "SingleFragmentActivity->onCreate: Layout Created ");
        }

        /** Fragment Manager keeps track of all the Fragments even if activities pauses.
         *  Here you can add , replace fragments.
         */
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.mspFragmentContainer);

        //This won't compile with release built
        if (BuildConfig.DEBUG) {
            Log.d(Constants.APP_TAG, "SingleFragmentActivity->onCreate: Fragment Manager Started");
        }

        if(fragment == null){
            fragment = CreateFragment();
            fm.beginTransaction()
                    .add(R.id.mspFragmentContainer,fragment)
                    .commit();
        }
    }
}
