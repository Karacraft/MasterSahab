package com.karacraft.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.karacraft.mastersahab.BuildConfig;
import com.karacraft.utils.Constants;

/**
 * Created by duke on 7/11/15.
 */
public class MerchandisesContract
{
    //Database Table
    public static final String TABLE_MERCHANDISES       = "merchandises";
    public static final String MERCHANDISE_ID           = "_id";
    public static final String MERCHANDISE_DATE         = "date";
    public static final String MERCHANDISE_TAILOR_CITY  = "tailor_city";
    public static final String MERCHANDISE_CARD_NUMBER  = "card_number";
    public static final String MERCHANDISE_ITEM_PIC     = "item_pic";
    public static final String MERCHANDISE_ITEM_NAME    = "item_name";
    public static final String MERCHANDISE_ITEM_LOC     = "item_geo_loc";
    public static final String MERCHANDISE_SYNCABLE     = "syncable";

    //Database Table creation SQL
    private static final String CREATE_MERCHANDISES_TABLE =
            "CREATE TABLE " + TABLE_MERCHANDISES
                    + "( "
                    + MERCHANDISE_ID +          " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + MERCHANDISE_DATE +        " TEXT,"
                    + MERCHANDISE_TAILOR_CITY + " TEXT,"
                    + MERCHANDISE_CARD_NUMBER + " TEXT,"
                    + MERCHANDISE_ITEM_PIC +    " TEXT,"
                    + MERCHANDISE_ITEM_NAME +   " TEXT,"
                    + MERCHANDISE_ITEM_LOC +    " TEXT,"
                    + MERCHANDISE_SYNCABLE +    " TEXT"
                    + ")";

    //Projection for Query
    public static String[] PROJECTION =
            {
                    MERCHANDISE_ID,
                    MERCHANDISE_DATE,
                    MERCHANDISE_TAILOR_CITY,
                    MERCHANDISE_CARD_NUMBER,
                    MERCHANDISE_ITEM_PIC,
                    MERCHANDISE_ITEM_NAME,
                    MERCHANDISE_ITEM_LOC,
                    MERCHANDISE_SYNCABLE
            };

    //Database Table deletion SQL
    private static final String DELETE_ITEMS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_MERCHANDISES;

    //Static so that MasterSahabDatabaseHelper class can access it.
    public static void onCreate(SQLiteDatabase database){
        database.execSQL(CREATE_MERCHANDISES_TABLE);
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
