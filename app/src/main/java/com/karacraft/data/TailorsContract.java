package com.karacraft.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.karacraft.mastersahab.BuildConfig;
import com.karacraft.utils.Constants;

/**
 * Created by       Ali Jibran / Kara Craft
 * For Project      Master Sahab
 * Dated            Nov 02 2015
 * File Name        TailorsContract.java
 * Comments         TailorsContract Object - For Table Management
 *
 */

public class TailorsContract
{

    public static final String TABLE_TAILORS        = "tailors";
    public static final String TAILOR_ID            = "_id";
    public static final String TAILOR_CARD_NUMBER   = "card_number";
    public static final String TAILOR_PIC           = "tailor_pic";
    public static final String TAILOR_NAME          = "tailor_name";
    public static final String TAILOR_SHOP_NAME     = "shop_name";
    public static final String TAILOR_CNIC          = "tailor_cnic";
    public static final String TAILOR_CONTACT       = "tailor_contact";
    public static final String TAILOR_AREA          = "tailor_area";
    public static final String TAILOR_ADDRESS       = "tailor_address";
    public static final String TAILOR_CITY          = "tailor_city";
    public static final String TAILOR_ACTIVE        = "active";

    //Database Table creation SQL
    private static final String CREATE_TAILORS_TABLE =
            "CREATE TABLE " + TABLE_TAILORS
                    + "( "
                    + TAILOR_ID +               " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + TAILOR_CARD_NUMBER +      " TEXT,"
                    + TAILOR_PIC +              " TEXT,"
                    + TAILOR_NAME +             " TEXT,"
                    + TAILOR_SHOP_NAME +        " TEXT,"
                    + TAILOR_CNIC +             " TEXT,"
                    + TAILOR_CONTACT +          " TEXT,"
                    + TAILOR_AREA +             " TEXT,"
                    + TAILOR_ADDRESS +          " TEXT,"
                    + TAILOR_CITY +             " TEXT,"
                    + TAILOR_ACTIVE +           " INTEGER"
                    + ")";

    //Projection for Query
    public static String[] PROJECTION =
            {
                    TAILOR_ID,
                    TAILOR_CARD_NUMBER,
                    TAILOR_PIC,
                    TAILOR_NAME,
                    TAILOR_SHOP_NAME,
                    TAILOR_CNIC,
                    TAILOR_CONTACT,
                    TAILOR_AREA,
                    TAILOR_ADDRESS,
                    TAILOR_CITY,
                    TAILOR_ACTIVE
            };

    //Database Table deletion SQL
    private static final String DELETE_TAILORS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_TAILORS;

    //Static so that MasterSahabDatabaseHelper class can access it.
    public static void onCreate(SQLiteDatabase database){
        database.execSQL(CREATE_TAILORS_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database,int oldVersion, int newVersion){
        //This won't compile with release built
        if (BuildConfig.DEBUG)
        {
            Log.d(Constants.APP_TAG, ItemsContract.class.getName() + " upgrading database from version " + oldVersion + " to version " + newVersion + ", which will destroy all data");
        }
        database.execSQL(DELETE_TAILORS_TABLE);
        //Recreate the table
        onCreate(database);
    }
}
