package com.karacraft.mastersahab;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Feb 15 2016
 * File Name        MercActivity.java
 * Comments
 */
public class MercActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment CreateFragment()
    {
        return  new MercFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
