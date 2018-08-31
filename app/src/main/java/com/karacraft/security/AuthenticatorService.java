package com.karacraft.security;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Feb 07 2016
 * File Name        AuthenticatorService.java
 * Comments         Link : http://developer.android.com/training/sync-adapters/creating-authenticator.html
 *                  A bound Service that instantiates the authenticator when Started
 */
public class AuthenticatorService extends Service
{

    //Instance field that stores the authenticator object
    private Authenticator authenticator;


    @Override
    public void onCreate()
    {
        //Create a new authenticator object
        authenticator = new Authenticator(this);
    }

    /**
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        return authenticator.getIBinder();
    }
}
