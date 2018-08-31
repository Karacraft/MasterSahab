package com.karacraft.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.karacraft.mastersahab.BuildConfig;
import com.karacraft.utils.Constants;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Dec 01 2015
 * File Name        StaffContract.java
 * Comments         Class for controlling all staff data.
 */

public class StaffContract
{
    public static final String TABLE_STAFF      = "staff";
    public static final String STAFF_ID         = "_id";
    public static final String STAFF_NAME       = "name";
    public static final String STAFF_ROLE       = "role";
    public static final String STAFF_CITY       = "city";


    //Database Table creation SQL
    private static final String CREATE_STAFF_TABLE =
            "CREATE TABLE " + TABLE_STAFF
                    + "( "
                    + STAFF_ID +        " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + STAFF_NAME +      " TEXT,"
                    + STAFF_ROLE +      " TEXT,"
                    + STAFF_CITY +      " TEXT"
                    + ")";

    //Projection for Query
    public static String[] PROJECTION =
            {
                    STAFF_ID,
                    STAFF_NAME,
                    STAFF_ROLE,
                    STAFF_CITY
            };

    //Database Table deletion SQL
    private static final String DELETE_ITEMS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_STAFF;

    //Static so that MasterSahabDatabaseHelper class can access it.
    public static void onCreate(SQLiteDatabase database){
        database.execSQL(CREATE_STAFF_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database,int oldVersion, int newVersion){
        //This won't compile with release built
        if (BuildConfig.DEBUG)
        {
            Log.d(Constants.APP_TAG, ItemsContract.class.getName() + " upgrading database from version " + oldVersion + " to version " + newVersion + ", which will destroy all data");
        }
        database.execSQL(DELETE_ITEMS_TABLE);
        //Recreate the table
        onCreate(database);
    }
}
