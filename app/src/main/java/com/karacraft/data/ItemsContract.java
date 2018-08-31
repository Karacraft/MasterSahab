package com.karacraft.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.karacraft.mastersahab.BuildConfig;
import com.karacraft.utils.Constants;

/**
 * Created by duke on 7/30/15.
 */
public class ItemsContract
{

    //Database Table
    public static final String TABLE_ITEMS      = "items";
    public static final String ITEM_ID          = "_id";
    public static final String ITEM_NAME        = "item_name";
    public static final String ITEM_ACTIVE      = "active";

    //Database Table creation SQL
    private static final String CREATE_ITEMS_TABLE =
            "CREATE TABLE " + TABLE_ITEMS
                    + "( "
                    + ITEM_ID +     " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ITEM_NAME +   " TEXT,"
                    + ITEM_ACTIVE + " INTEGER"
                    + ")";

    //Projection for Query
    public static String[] PROJECTION =
            {
            ITEM_ID,
            ITEM_NAME,
            ITEM_ACTIVE
            };

    //Database Table deletion SQL
    private static final String DELETE_ITEMS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_ITEMS;

    //Static so that MasterSahabDatabaseHelper class can access it.
    public static void onCreate(SQLiteDatabase database){
        database.execSQL(CREATE_ITEMS_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database,int oldVersion, int newVersion){
        //This won't compile with release built
        if (BuildConfig.DEBUG)
        {
            Log.d(Constants.APP_TAG, ItemsContract.class.getName() + " upgrading database from version "
            + oldVersion + " to version " + newVersion + ", which will destroy all data"
            );
        }
        database.execSQL(DELETE_ITEMS_TABLE);
        //Recreate the table
        onCreate(database);
    }

}
