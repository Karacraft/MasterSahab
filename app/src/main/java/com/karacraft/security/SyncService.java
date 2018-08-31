package com.karacraft.security;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Feb 07 2016
 * File Name        SyncService.java
 * Comments         SyncService.java for SyncAdapter
 *                  together they both download upload data from server
 *                  using content provider is advised.
 *
 *                  This Service will bind with syncadapter, allowing us to call onPerformSync()
 *                  to upload/download data from/to Server.
 */
public class SyncService extends Service
{

    // Storage for an instance of the sync adapter
    private static SyncAdapter syncAdapter = null;
    // Object to use as a thread-safe lock
    private static final Object syncAdapterLock = new Object();

    public SyncService(){
        super();
    }

    /**
     * Instantiate the sync adapter object.
     */
    @Override
    public void onCreate() {
        /**
         * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }
    /**
     * Return an object that allows the system to invoke
     * the sync adapter.
     *
     */
    @Override
    public IBinder onBind(Intent intent) {
        /**
         * Get the object that allows external processes
         * to call onPerformSync(). The object is created
         * in the base class code when the SyncAdapter
         * constructors call super()
         */
        return syncAdapter.getSyncAdapterBinder();
    }
}
