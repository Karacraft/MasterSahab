package com.karacraft.mastersahab;

import android.support.v4.app.Fragment;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Dec 05 2015
 * File Name        OrderActivity.java
 * Comments
 */
public class OrderActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment CreateFragment()
    {
        return new OrderFragment();
    }
}
