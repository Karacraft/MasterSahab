package com.karacraft.mastersahab;

import android.support.v4.app.Fragment;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Nov 29 2015
 * File Name        TailorActivity.java
 * Comments
 */
public class TailorActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment CreateFragment()
    {
        Fragment mFragment = new TailorFragment();
        return mFragment;
    }
}
