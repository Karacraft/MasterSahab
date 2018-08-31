package com.karacraft.mastersahab;

import android.support.v4.app.Fragment;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Nov 22 2015
 * File Name        OrdersListActivity.java
 * Comments
 */
public class OrdersListActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment CreateFragment()
    {
        Fragment fragment = new OrdersListFragment();
        return fragment;
    }
}


