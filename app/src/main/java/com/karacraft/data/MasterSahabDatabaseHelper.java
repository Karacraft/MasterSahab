package com.karacraft.data;

import android.content.ClipData;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by       Ali Jibran / Kara Craft
 * For Project      Master Sahab
 * Dated            Nov 02 2015
 * File Name        MasterSahabDatabaseHelper.java
 * Comments         Extends SQLiteOpenHelper
 *                  Helps in maintaining Internal SQLITE Database
 *                  Creates all the table, when App is
 *                  activated first time.
 *                  The Version is created by replecating Vogella Tutorial
 *                  http://www.vogella.com/tutorials/AndroidSQLite/article.html#contentprovider
 */

public class MasterSahabDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION       = 1;
    private static final String DATABASE_NAME       = "mastersahab.db";

    //-----------------------------------------------------------------------------------
    //This Creates the Database in System
    public MasterSahabDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create Tables
        ItemsContract.onCreate(db);
        MerchandisesContract.onCreate(db);
        OrdersContract.onCreate(db);
        SuitsContract.onCreate(db);
        TailorsContract.onCreate(db);
        StaffContract.onCreate(db);
        StatsContract.onCreate(db);
    }
    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ItemsContract.onUpgrade(db, oldVersion, newVersion);
        MerchandisesContract.onUpgrade(db, oldVersion, newVersion);
        OrdersContract.onUpgrade(db, oldVersion, newVersion);
        SuitsContract.onUpgrade(db, oldVersion, newVersion);
        TailorsContract.onUpgrade(db, oldVersion, newVersion);
        StaffContract.onUpgrade(db, oldVersion, newVersion);
        StatsContract.onUpgrade(db, oldVersion, newVersion);
    }
    //-----------------------------Items-----------------------------//


}
