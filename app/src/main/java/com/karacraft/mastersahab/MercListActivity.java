package com.karacraft.mastersahab;

import android.support.v4.app.Fragment;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Feb 15 2016
 * File Name
 * Comments
 */
public class MercListActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment CreateFragment()
    {
        return new MercListFragment();
    }
}
