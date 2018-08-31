package com.karacraft.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.karacraft.mastersahab.BuildConfig;
import com.karacraft.utils.Constants;

/**
 * Created by duke on 7/18/15.
 */
public class OrdersContract
{

    public static final String TABLE_ORDERS                 = "orders";
    public static final String ORDER_ID                     = "_id";
    public static final String ORDER_DATE                   = "order_placement_date";
    public static final String ORDER_CARD_NUMBER            = "card_number";
    public static final String ORDER_STAFF_ID               = "staff_id";
    public static final String ORDER_QUANTITY               = "quantity";
    public static final String ORDER_SUIT_CODE              = "suit_code";
    public static final String ORDER_STAFF_CITY             = "staff_city";
    public static final String ORDER_SUIT_PRICE             = "suit_price";
    public static final String ORDER_SUIT_COLOR             = "suit_color";
    public static final String ORDER_SUIT_STATUS            = "suit_status";
    public static final String ORDER_STATUS_CHANGE_DATE     = "order_status_change_date";
    public static final String ORDER_SUIT_DISCOUNTED_PRICE  = "suit_discounted_price";
    public static final String ORDER_REDEEM                 = "redeem";
    public static final String ORDER_RECEIPT                = "receipt";
    public static final String ORDER_SUIT_CATALOUGE         = "suit_catlouge";

    //Database Table creation SQL
    private static final String CREATE_ORDERS_TABLE =
            "CREATE TABLE " + TABLE_ORDERS
                    + "( "
                    + ORDER_ID +                    " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ORDER_DATE +                  " TEXT,"
                    + ORDER_CARD_NUMBER +           " TEXT  ,"
                    + ORDER_STAFF_ID +              " INTEGER,"
                    + ORDER_QUANTITY +              " INTEGER,"
                    + ORDER_SUIT_CODE +             " TEXT,"
                    + ORDER_STAFF_CITY +            " TEXT,"
                    + ORDER_SUIT_PRICE +            " INTEGER,"
                    + ORDER_SUIT_COLOR +            " TEXT,"
                    + ORDER_SUIT_STATUS +           " TEXT,"
                    + ORDER_STATUS_CHANGE_DATE +    " TEXT,"
                    + ORDER_SUIT_DISCOUNTED_PRICE + " INTEGER,"
                    + ORDER_REDEEM +                " INTEGER,"
                    + ORDER_RECEIPT +               " TEXT,"
                    + ORDER_SUIT_CATALOUGE +        " TEXT"
                    + ")";

    //Projection for Query
    public static String[] PROJECTION =
            {
                    ORDER_ID,
                    ORDER_DATE,
                    ORDER_CARD_NUMBER,
                    ORDER_STAFF_ID,
                    ORDER_QUANTITY,
                    ORDER_SUIT_CODE,
                    ORDER_STAFF_CITY,
                    ORDER_SUIT_PRICE,
                    ORDER_SUIT_COLOR,
                    ORDER_SUIT_STATUS,
                    ORDER_STATUS_CHANGE_DATE,
                    ORDER_SUIT_DISCOUNTED_PRICE,
                    ORDER_REDEEM,
                    ORDER_RECEIPT,
                    ORDER_SUIT_CATALOUGE
            };

    //Database Table deletion SQL
    private static final String DELETE_ITEMS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_ORDERS;

    //Static so that MasterSahabDatabaseHelper class can access it.
    public static void onCreate(SQLiteDatabase database){
        database.execSQL(CREATE_ORDERS_TABLE);
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
