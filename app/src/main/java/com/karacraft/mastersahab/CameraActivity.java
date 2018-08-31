package com.karacraft.mastersahab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by       duke / Kara Craft
 * For Project      CriminalIntent
 * Dated            Nov 16 2015
 * File Name        CameraActivity.java
 * Comments         Load CameraFragment
 */
public class CameraActivity extends SingleFragmentActivity {

    @Override
    protected Fragment CreateFragment()
    {
        return new CameraFragment();
    }

    /**
     * We want to remove Actionbar from our camera fragment.
     * to remove it, we need to remove it from the calling activity
     * as it is removed before creation of fragment.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Hide the status bar and other OS-level chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }
}
