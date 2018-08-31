package com.karacraft.mastersahab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.karacraft.utils.Helper;

/**
 * Created by       Ali Jibran / Kara Craft
 * For Project      Master Sahab
 * Dated            Nov 02 2015
 * File Name        ActivitySplash.java
 * Comments         Splash Screen with Al-Karam Logo Animation
 *                  Shows first when App Starts.
 *
 */
public class ActivitySplash extends FragmentActivity{

    ImageView mImageLogo; //AKMS_Logo
    Animation mFadeIn; //XML Animation
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Views Variables
        mTextView = (TextView)findViewById(R.id.textView_splash_app_version);
        mTextView.setText("Version : " + Helper.getAppVersionNumber(this));
        mImageLogo = (ImageView) findViewById(R.id.ImageView_LogoAKMS);
        mFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        mImageLogo.startAnimation(mFadeIn);

        //Animation Listener
        mFadeIn.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {

                Intent i = new Intent(ActivitySplash.this, Activity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }

        });
    }
}
