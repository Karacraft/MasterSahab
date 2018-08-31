package com.karacraft.mastersahab;

import android.support.v4.app.Fragment;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Nov 22 2015
 * File Name        TailorsListActivity.java
 * Comments
 */
public class TailorsListActivity extends SingleFragmentActivity
{

    @Override
    protected Fragment CreateFragment()
    {
        Fragment fragment = new TailorsListFragment();
        return fragment;
    }
}
