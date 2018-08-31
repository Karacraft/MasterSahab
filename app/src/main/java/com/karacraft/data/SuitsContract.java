package com.karacraft.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.karacraft.mastersahab.BuildConfig;
import com.karacraft.utils.Constants;

/**
 * Created by duke on 7/12/15.
 */
public class SuitsContract
{
    public static final String TABLE_SUITS          = "suits";
    public static final String SUITS_ID             = "_id";
    public static final String SUITS_CODE           = "suit_code";
    public static final String SUIT_CATALOG         = "suit_catalog";
    public static final String SUITS_COLOR          = "suit_color";
    public static final String SUITS_TYPE           = "suit_type";
    public static final String SUITS_DESCRIPTION    = "suit_description";
    public static final String SUITS_PRICE          = "suit_price";
    public static final String SUITS_ACTIVE         = "active";

    //Database Table creation SQL
    private static final String CREATE_SUITS_TABLE =
            "CREATE TABLE " + TABLE_SUITS
                    + "( "
                    + SUITS_ID +            " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + SUITS_CODE +          " TEXT,"
                    + SUIT_CATALOG +        " TEXT,"
                    + SUITS_COLOR +         " TEXT,"
                    + SUITS_TYPE +          " TEXT,"
                    + SUITS_DESCRIPTION +   " TEXT,"
                    + SUITS_PRICE +         " INTEGER,"
                    + SUITS_ACTIVE +        " INTEGER"
                    + ")";

    //Projection for Query
    public static String[] PROJECTION =
            {
                    SUITS_ID,
                    SUITS_CODE, SUIT_CATALOG,
                    SUITS_COLOR,
                    SUITS_TYPE,
                    SUITS_DESCRIPTION,
                    SUITS_PRICE,
                    SUITS_ACTIVE
            };

    //Database Table deletion SQL
    private static final String DELETE_SUITS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_SUITS;

    //Static so that MasterSahabDatabaseHelper class can access it.
    public static void onCreate(SQLiteDatabase database){
        database.execSQL(CREATE_SUITS_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database,int oldVersion, int newVersion){
        //This won't compile with release built
        if (BuildConfig.DEBUG)
        {
            Log.d(Constants.APP_TAG, ItemsContract.class.getName() + " upgrading database from version " + oldVersion + " to version " + newVersion + ", which will destroy all data");
        }
        database.execSQL(DELETE_SUITS_TABLE);
        //Recreate the table
        onCreate(database);
    }
}
