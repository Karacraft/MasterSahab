package com.karacraft.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.karacraft.mastersahab.BuildConfig;
import com.karacraft.utils.Constants;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Dec 01 2015
 * File Name        StatsContract.java
 * Comments         Class for controlling all staff data.
 */

public class StatsContract
{
    public static final String TABLE_STATS            = "stats";
    public static final String STATS_ID              = "_id";
    public static final String STATS_TABLE_NAME       = "table_name";
    public static final String STATS_CREATEDAT       = "created_at";
    public static final String STATS_UPDATEDAT       = "updated_at";


    //Database Table creation SQL
    private static final String CREATE_STATS_TABLE =
            "CREATE TABLE " + TABLE_STATS
                    + "( "
                    + STATS_ID +              " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + STATS_TABLE_NAME +      " TEXT,"
                    + STATS_CREATEDAT +      " TEXT,"
                    + STATS_UPDATEDAT +      " TEXT"
                    + ")";

    //Projection for Query
    public static String[] PROJECTION =
            {
                    STATS_ID,
                    STATS_TABLE_NAME,
                    STATS_CREATEDAT,
                    STATS_UPDATEDAT
            };

    //Database Table deletion SQL
    private static final String DELETE_STATS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_STATS;

    //Static so that MasterSahabDatabaseHelper class can access it.
    public static void onCreate(SQLiteDatabase database){
        database.execSQL(CREATE_STATS_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database,int oldVersion, int newVersion){
        //This won't compile with release built
        if (BuildConfig.DEBUG)
        {
            Log.d(Constants.APP_TAG, ItemsContract.class.getName() + " upgrading database from version " + oldVersion + " to version " + newVersion + ", which will destroy all data");
        }
        database.execSQL(DELETE_STATS_TABLE);
        //Recreate the table
        onCreate(database);
    }
}
