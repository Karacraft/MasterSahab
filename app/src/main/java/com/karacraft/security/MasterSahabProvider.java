package com.karacraft.security;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.karacraft.data.ItemsContract;
import com.karacraft.data.MasterSahabDatabaseHelper;
import com.karacraft.data.MerchandisesContract;
import com.karacraft.data.OrdersContract;
import com.karacraft.data.StaffContract;
import com.karacraft.data.StatsContract;
import com.karacraft.data.SuitsContract;
import com.karacraft.data.TailorsContract;
import com.karacraft.utils.Constants;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Feb 07 2016
 * File Name        MasterSahabProvider.java
 * Comments         Link:http://developer.android.com/training/sync-adapters/creating-stub-provider.html
 *                  Define an implementation of ContentProvider that stubs out
 */
public class MasterSahabProvider extends ContentProvider
{
    public static final String AUTHORITY ="com.karacraft.security.mastersahab";
    public static final String KEY_URI = "/#";

    //database
    private MasterSahabDatabaseHelper database;

    //Uri for Multiple Tables
    /** Basic URI = content:// authority / path / id
     * **/
    public static final Uri CONTENT_URI_MERCHANDISES    = Uri.parse("content://" + AUTHORITY + "/" + MerchandisesContract.TABLE_MERCHANDISES);
    public static final Uri CONTENT_URI_ORDERS          = Uri.parse("content://" + AUTHORITY + "/" + OrdersContract.TABLE_ORDERS);
    public static final Uri CONTENT_URI_SUITS           = Uri.parse("content://" + AUTHORITY + "/" + SuitsContract.TABLE_SUITS);
    public static final Uri CONTENT_URI_TAILORS         = Uri.parse("content://" + AUTHORITY + "/" + TailorsContract.TABLE_TAILORS);
    public static final Uri CONTENT_URI_STAFF           = Uri.parse("content://" + AUTHORITY + "/" + StaffContract.TABLE_STAFF);
    public static final Uri CONTENT_URI_STATS           = Uri.parse("content://" + AUTHORITY + "/" + StatsContract.TABLE_STATS);

    public static final Uri CONTENT_URI_ORDERS_BY_MERC  = Uri.parse("content://" + AUTHORITY + OrdersContract.TABLE_ORDERS);

    //Return value setup for Uri Matchers
    private static final int MERCHANDISES   = 30;
    private static final int MERCHANDISE_ID = 40;
    private static final int ORDERS         = 50;
    private static final int ORDERS_ID      = 60;
    private static final int SUITS          = 70;
    private static final int SUITS_ID       = 80;
    private static final int TAILORS        = 90;
    private static final int TAILORS_ID     = 100;
    private static final int STAFF          = 110;
    private static final int STAFF_ID       = 120;
    private static final int STATS          = 130;
    private static final int STATS_ID       = 140;

    private static final int ORDERS_BY_MERC = 200;

    //Custom Method
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, MerchandisesContract.TABLE_MERCHANDISES,MERCHANDISES);
        uriMatcher.addURI(AUTHORITY, MerchandisesContract.TABLE_MERCHANDISES + KEY_URI,MERCHANDISE_ID);
        uriMatcher.addURI(AUTHORITY, OrdersContract.TABLE_ORDERS,ORDERS);
        uriMatcher.addURI(AUTHORITY, OrdersContract.TABLE_ORDERS + KEY_URI,ORDERS_ID);
        uriMatcher.addURI(AUTHORITY, SuitsContract.TABLE_SUITS,SUITS);
        uriMatcher.addURI(AUTHORITY, SuitsContract.TABLE_SUITS + KEY_URI,SUITS_ID);
        uriMatcher.addURI(AUTHORITY, TailorsContract.TABLE_TAILORS,TAILORS);
        uriMatcher.addURI(AUTHORITY, TailorsContract.TABLE_TAILORS + KEY_URI,TAILORS_ID);
        uriMatcher.addURI(AUTHORITY, StaffContract.TABLE_STAFF , STAFF);
        uriMatcher.addURI(AUTHORITY, StaffContract.TABLE_STAFF+ KEY_URI, STAFF_ID);
        uriMatcher.addURI(AUTHORITY, StatsContract.TABLE_STATS , STATS);
        uriMatcher.addURI(AUTHORITY, StatsContract.TABLE_STATS + KEY_URI, STATS_ID);

        uriMatcher.addURI(AUTHORITY,OrdersContract.TABLE_ORDERS,ORDERS);
    }

    public boolean onCreate()
    {
        //Create Database
        database = new MasterSahabDatabaseHelper(getContext());
        return false;
    }

    /**
     URI         uri         The URI of the object(s) to access. This is the only argument that must not be null
     String[]    projection  This String array indicates which columns/attributes of the objects you want to access
     String      selection   With this argument you can determine which records to return
     String[]    selectionArgs   The binding parameters to the previous selection argument
     String      sortOrder   If the result should be ordered you must use this argument to determine the sort order
     */
    @Nullable
    @Override
    public Cursor query(Uri uri
            , String[] projection
            , String selection
            , String[] selectionArgs
            , String sortOrder)
    {
        //Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor=null;

        int uriType = uriMatcher.match(uri);
//        Log.d(Constants.APP_TAG,"URIType is : " + uriType);
//        Log.d(Constants.APP_TAG,"URI is : " + uri.toString());

        switch (uriType){
            case MERCHANDISES:
                //Set the table
                builder.setTables(MerchandisesContract.TABLE_MERCHANDISES);
                //adding the ID to original Query
                cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
                // make sure that potential listeners are getting notified
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            case MERCHANDISE_ID:
                //Set the table
                builder.setTables(MerchandisesContract.TABLE_MERCHANDISES);
                //adding the ID to original Query
                builder.appendWhere(MerchandisesContract.MERCHANDISE_ID + "=" + uri.getLastPathSegment());
                cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
                // make sure that potential listeners are getting notified
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            case ORDERS:
                //Set the table
                builder.setTables(OrdersContract.TABLE_ORDERS);
                //adding the ID to original Query
                cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
                // make sure that potential listeners are getting notified
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            case ORDERS_ID:
                //Set the table
                builder.setTables(OrdersContract.TABLE_ORDERS);
                //adding the ID to original Query
                builder.appendWhere(OrdersContract.ORDER_ID + "=" + uri.getLastPathSegment());
                cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
                // make sure that potential listeners are getting notified
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            case TAILORS:
                //Set the table
                builder.setTables(TailorsContract.TABLE_TAILORS);
                //adding the ID to original Query
                cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
                // make sure that potential listeners are getting notified
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            case TAILORS_ID:
                //Set the table
                builder.setTables(TailorsContract.TABLE_TAILORS);
                //adding the ID to original Query
                builder.appendWhere(TailorsContract.TAILOR_ID + "=" + uri.getLastPathSegment());
                cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
                // make sure that potential listeners are getting notified
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            case SUITS:
                //Set the table
                builder.setTables(SuitsContract.TABLE_SUITS);
                //adding the ID to original Query
                cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
                // make sure that potential listeners are getting notified
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            case SUITS_ID:
                //Set the table
                builder.setTables(SuitsContract.TABLE_SUITS);
                //adding the ID to original Query
                builder.appendWhere(SuitsContract.SUITS_ID + "=" + uri.getLastPathSegment());
                cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
                // make sure that potential listeners are getting notified
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            case STAFF:
                //Set the table
                builder.setTables(StaffContract.TABLE_STAFF);
                //adding the ID to original Query
                cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
                // make sure that potential listeners are getting notified
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            case STAFF_ID:
                //Set the table
                builder.setTables(StaffContract.TABLE_STAFF);
                //adding the ID to original Query
                builder.appendWhere(StaffContract.STAFF_ID + "=" + uri.getLastPathSegment());
                cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
                // make sure that potential listeners are getting notified
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            case STATS:
                //Set the table
                builder.setTables(StatsContract.TABLE_STATS);
                //adding the ID to original Query
                cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
                // make sure that potential listeners are getting notified
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            case STATS_ID:
                //Set the table
                builder.setTables(StatsContract.TABLE_STATS);
                //adding the ID to original Query
                builder.appendWhere(StatsContract.STATS_ID + "=" + uri.getLastPathSegment());
                cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
                // make sure that potential listeners are getting notified
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            case ORDERS_BY_MERC:
                //Set the table
                //builder.setTables("orders LEFT OUTER JOIN staff ON (orders.staff_id = staff._id)");
                builder.setTables("orders LEFT OUTER JOIN staff ON orders.staff_id = staff._id");
                //builder.setTables("orders,staff");
                //adding the ID to original Query
                cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
                // make sure that potential listeners are getting notified
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri)
    {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri
            , ContentValues values)
    {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
//        Log.d(Constants.APP_TAG,"URIType is : " + uriType);
//        Log.d(Constants.APP_TAG,"URI is : " + uri.toString());

        long id = 0;
        switch (uriType){
            case MERCHANDISES:
                id = db.insert(MerchandisesContract.TABLE_MERCHANDISES,null,values);
                getContext().getContentResolver().notifyChange(uri,null);
                return Uri.parse(MerchandisesContract.TABLE_MERCHANDISES + "/" + id);

            case ORDERS:
                id = db.insert(OrdersContract.TABLE_ORDERS,null,values);
                getContext().getContentResolver().notifyChange(uri,null);
                Log.d(Constants.APP_TAG, "Added new Order");
                return Uri.parse(OrdersContract.TABLE_ORDERS + "/" + id);
            case SUITS:
                id = db.insert(SuitsContract.TABLE_SUITS,null,values);
                getContext().getContentResolver().notifyChange(uri,null);
                return Uri.parse(SuitsContract.TABLE_SUITS + "/" + id);

            case STAFF:
                id = db.insert(StaffContract.TABLE_STAFF,null, values);
                getContext().getContentResolver().notifyChange(uri,null);
                return Uri.parse(StaffContract.TABLE_STAFF + "/" + id);

            case TAILORS:
                id = db.insert(TailorsContract.TABLE_TAILORS,null,values);
                getContext().getContentResolver().notifyChange(uri,null);
                return Uri.parse(TailorsContract.TABLE_TAILORS + "/" + id);

            case STATS:
                id = db.insert(StatsContract.TABLE_STATS,null,values);
                getContext().getContentResolver().notifyChange(uri,null);
                return Uri.parse(StatsContract.TABLE_STATS + "/" + id);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri
            , String selection
            , String[] selectionArgs)
    {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        String id ="";
        //TODO::Add Code to Create a list of Deleteable items and send it to server for deletion
        switch (uriType) {
            case MERCHANDISES:
                rowsDeleted = sqlDB.delete(MerchandisesContract.TABLE_MERCHANDISES, selection, selectionArgs);
                Log.d(Constants.APP_TAG,"Merchandise deleted");
                break;
            case MERCHANDISE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(MerchandisesContract.TABLE_MERCHANDISES, MerchandisesContract.MERCHANDISE_ID+ "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(MerchandisesContract.TABLE_MERCHANDISES, MerchandisesContract.MERCHANDISE_ID + "=" + id + " AND " + selection, selectionArgs);
                }
                Log.d(Constants.APP_TAG,"Merchandise ID " + id + " deleted");
                break;
            case TAILORS:
                rowsDeleted = sqlDB.delete(TailorsContract.TABLE_TAILORS, selection, selectionArgs);
                break;
            case TAILORS_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(TailorsContract.TABLE_TAILORS, TailorsContract.TAILOR_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(TailorsContract.TABLE_TAILORS, TailorsContract.TAILOR_ID + "=" + id + " AND " + selection, selectionArgs);
                }
                break;
            case STAFF:
                rowsDeleted = sqlDB.delete(StaffContract.TABLE_STAFF, selection, selectionArgs);
                break;
            case STAFF_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(StaffContract.TABLE_STAFF, StaffContract.STAFF_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(StaffContract.TABLE_STAFF, StaffContract.STAFF_ID + "=" + id + " AND " + selection, selectionArgs);
                }
                break;
            case SUITS:
                rowsDeleted = sqlDB.delete(SuitsContract.TABLE_SUITS, selection, selectionArgs);
                break;
            case SUITS_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(SuitsContract.TABLE_SUITS, SuitsContract.SUITS_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(SuitsContract.TABLE_SUITS, SuitsContract.SUITS_ID + "=" + id + " AND " + selection, selectionArgs);
                }
                break;
            case ORDERS:
                rowsDeleted = sqlDB.delete(OrdersContract.TABLE_ORDERS, selection, selectionArgs);
                break;
            case ORDERS_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(OrdersContract.TABLE_ORDERS, OrdersContract.ORDER_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(OrdersContract.TABLE_ORDERS, OrdersContract.ORDER_ID + "=" + id + " AND " + selection, selectionArgs);
                }
                break;
            case STATS:
                rowsDeleted = sqlDB.delete(StatsContract.TABLE_STATS, selection, selectionArgs);
                break;
            case STATS_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(StatsContract.TABLE_STATS, StatsContract.STATS_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(StatsContract.TABLE_STATS, StatsContract.STATS_ID + "=" + id + " AND " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri
            , ContentValues values
            , String selection
            , String[] selectionArgs)
    {
        String id="";
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsUpdated=0;

        switch (uriType){
            case MERCHANDISES:
                rowsUpdated = db.update(MerchandisesContract.TABLE_MERCHANDISES,values,selection,selectionArgs);
                break;
            case MERCHANDISE_ID:
                id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection)){
                    rowsUpdated=db.update(MerchandisesContract.TABLE_MERCHANDISES,values,MerchandisesContract.MERCHANDISE_ID + "=" + id,null);
                }else {
                    rowsUpdated=db.update(MerchandisesContract.TABLE_MERCHANDISES,values,MerchandisesContract.MERCHANDISE_ID + "=" + id + " AND " + selection,selectionArgs);
                }
                break;
            case ORDERS:
                rowsUpdated = db.update(OrdersContract.TABLE_ORDERS, values, selection, selectionArgs);
                break;
            case ORDERS_ID:
                id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection)){
                    rowsUpdated=db.update(OrdersContract.TABLE_ORDERS,values,OrdersContract.ORDER_ID + "=" + id,null);
                }else {
                    rowsUpdated=db.update(OrdersContract.TABLE_ORDERS,values,OrdersContract.ORDER_ID+ "=" + id + " AND " + selection,selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }
}
