package com.karacraft.security;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.karacraft.data.DBHelper;
import com.karacraft.data.MerchandisesContract;
import com.karacraft.data.OrdersContract;
import com.karacraft.data.StaffContract;
import com.karacraft.data.StatsContract;
import com.karacraft.data.SuitsContract;
import com.karacraft.data.TailorsContract;
import com.karacraft.mastersahab.BuildConfig;
import com.karacraft.utils.Constants;
import com.karacraft.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Feb 07 2016
 * File Name        SyncAdapter.java
 * Comments         Link :http://developer.android.com/training/sync-adapters/creating-sync-adapter.html
 *                  Handle the transfer of data between a server and an
 *                  app, using the Android sync adapter framework.
 *                  The Class needs a SyncService to work properly
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter
{
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    //Other Variables
    String token;                       //Token from Server
    AccountManager accountManager;      //Android Account Manager
    Account myAccount=null;             //For functions / Account of the Current User

    Cursor cursor=null;                 //Check if data is loaded or not

    Boolean fetchData=false;            //Fetch Data Flag
    Boolean getNewToken =false;         //Refreshes Token

    Date AccountCreationDate=null;      //Time to be utilized for refreshing token (default 90 mins)

    /**
     * Setup the SyncAdapter
     */
    public SyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        /**
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        Log.d(Constants.APP_TAG,"SyncAdapter(1)");
        AccountCreationDate=new Date();
    }
    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs)
    {
        super(context, autoInitialize, allowParallelSyncs);
        /**
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        Log.d(Constants.APP_TAG,"SyncAdapter(2)");
        AccountCreationDate=new Date();
    }
    /** The sync adapter component does not automatically do data transfer.
     *  Instead, it encapsulates your data transfer code, so that the sync adapter framework
     *  can run the data transfer in the background, without involvement from your app.
     *  When the framework is ready to sync your application's data, it invokes your implementation of the method
     *
     * The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPerformSync(
            Account account /** An Account object associated with the event that triggered the sync adapter. If your server doesn't use accounts, you don't need to use the information in this object. */
            , Bundle extras /** A Bundle containing flags sent by the event that triggered the sync adapter.  */
            , String authority /** The authority of a content provider in the system. Your app has to have access to this provider. Usually, the authority corresponds to a content provider in your own app. */
            , ContentProviderClient provider /** If you're using a content provider to store data for your app, you can connect to the provider with this object. Otherwise, you can ignore it. */
            , SyncResult syncResult /**     A SyncResult object that you use to send information to the sync adapter framework. */
                        )
    {
        /** Setup Timer to Fetch Data */
        Log.d(Constants.APP_TAG, "==================Sync Adapter=================");
        long difference = (new Date()).getTime() - AccountCreationDate.getTime();
        Log.d(Constants.APP_TAG,"Difference in time : " + difference);
        if(difference >= 3600000){
            getNewToken=true;
        }else {
            getNewToken=false;
        }

        /** Setup Local Variables */
        accountManager = AccountManager.get(getContext());
        myAccount = account;
        token = accountManager.getUserData(account, Constants.APP_TOKEN_KEY);

        Log.d(Constants.APP_TAG, "======================================================");
        Log.d(Constants.APP_TAG, "Master Sahab Program : Actual Data Sync Starts Here..");
        Log.d(Constants.APP_TAG, "------------------------------------------------------");
        /** Check all cursors, all time for data-Usually required, if data is deleted etc */
        //checkDataStatus();
        /** Cursors are empty, fetch them now */

        /** Refresh the Token */

        /** if Data available then sync it
         *  Do All Update & New Addition to Server Here
         * */
//        JSONArray jsonArray;
//        jsonArray = DBHelper.selectNewMerchandise(getContext());
//        if (jsonArray.length() > 0){
//            Log.d(Constants.APP_TAG,"New Merc Has Data : Trying to Sync");
//            try
//            {
//                int i = DBHelper.syncNewMerchandise(token,jsonArray);
//            } catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }

        try {
            int j = DBHelper.check(token);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //-------------------Do All Update & Upload tasks here ----------------//

        /** Following Task could be performed here
         * Connecting to a server
         * Downloading & Uploading data (user content provider or database, also handle errors
         * Handling data conflicts (if data is current or not, use own code)
         * Clean Up (close server connections, remove temp files etc)
         */
    }
    //-----------------checkDataStatus()-------------------------------//
    private void checkDataStatus(){
        cursor = getContext().getContentResolver().query(MasterSahabProvider.CONTENT_URI_ORDERS, null, null, null, null);
        if(cursor !=null){
            if (!cursor.moveToFirst()){
                //We don't have Data, refetch all data
                fetchData=true;
            }
        }
        cursor = getContext().getContentResolver().query(MasterSahabProvider.CONTENT_URI_SUITS,null,null,null,null);
        if(cursor !=null){
            if (!cursor.moveToFirst()){
                //We don't have Data, refetch all data
                fetchData=true;
            }
        }
        cursor = getContext().getContentResolver().query(MasterSahabProvider.CONTENT_URI_TAILORS,null,null,null,null);
        if(cursor !=null){
            if (!cursor.moveToFirst()){
                //We don't have Data, refetch all data
                fetchData=true;
            }
        }
        cursor = getContext().getContentResolver().query(MasterSahabProvider.CONTENT_URI_STATS,null,null,null,null);
        if(cursor !=null){
            if (!cursor.moveToFirst()){
                //We don't have Data, refetch all data
                fetchData=true;
            }
        }
        cursor = getContext().getContentResolver().query(MasterSahabProvider.CONTENT_URI_STAFF,null,null,null,null);
        if(cursor !=null){
            if (!cursor.moveToFirst()){
                //We don't have Data, refetch all data
                fetchData=true;
            }
        }
        cursor = getContext().getContentResolver().query(MasterSahabProvider.CONTENT_URI_MERCHANDISES,null,null,null,null);
        if(cursor !=null){
            if (!cursor.moveToFirst()){
                //We don't have Data, refetch all data
                fetchData=true;
            }
        }
    }
    //---------------onSyncCanceled()----------------------------------//
    @Override
    public void onSyncCanceled()
    {
        super.onSyncCanceled();
        if(cursor != null)
            cursor=null;
        Helper.notify(getContext(), "Data Sync Cancelled", "Master Sahab");
    }
    //------------------readData()-----------------------------------//
//    private String readData(HttpURLConnection conn){
//
//        String mResponse="";
//        String line="";
//        BufferedReader mBufferedReader = null;
//        //Try to Read the InputStream
//        try
//        {
//            mBufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            while ((line = mBufferedReader.readLine()) != null) {
//                mResponse = mResponse + line;
//            }
//            mBufferedReader.close();
//        }
//        catch (IOException e)
//        {
//            System.out.println(e.getMessage());
//        }
//        return mResponse;
//    }
    //-----------------deleteAllData()--------------------//
//    private void deleteAllData(ContentProviderClient contentProviderClient){
//
//        Log.d(Constants.APP_TAG,"SyncAdapter->deleteAllData:Performing Delete...");
//        try
//        {
//            contentProviderClient.delete(MasterSahabProvider.CONTENT_URI_ORDERS,null,null);
//            contentProviderClient.delete(MasterSahabProvider.CONTENT_URI_SUITS, null, null);
//            contentProviderClient.delete(MasterSahabProvider.CONTENT_URI_STAFF, null, null);
//            contentProviderClient.delete(MasterSahabProvider.CONTENT_URI_TAILORS, null, null);
//            contentProviderClient.delete(MasterSahabProvider.CONTENT_URI_STATS,null,null);
//            contentProviderClient.delete(MasterSahabProvider.CONTENT_URI_MERCHANDISES, null, null);
//
//        } catch (RemoteException e)
//        {
//            e.printStackTrace();
//        }
//    }
    //----------------ifAccountExist()--------------------//
//    private int checkStats(ContentProviderClient providerClient) throws IOException, JSONException
//    {
//
//        /** Laravel jwt-Auth token gets updated every time we try to login
//         * so changed its settings in jwt.php and set the blacklist to false
//         */
//        String response=Constants.EMPTY_STRING;
//        int result=0;
//        URL mUrl=null;
//        HttpURLConnection mConnection=null;
//        String mQuery = Constants.APP_TOKEN_KEY + Constants.EQUALS + token;
//        Log.d(Constants.APP_TAG,mQuery);
//        JSONObject jsonObject=null;
//
//        mUrl = new URL(Constants.CHECK_STATS + mQuery);
//        //Log.d(Constants.APP_TAG,"CheckStats->URL to Server is : " + mUrl.toString());
//        mConnection = (HttpURLConnection) mUrl.openConnection();
//
//        int i = mConnection.getResponseCode();
//
//        if (i == 200){
//            Log.d(Constants.APP_TAG,"CheckStats->Stats : Data Fetched");
//            //Every Thing is Ok, Read the response
//            response = readData(mConnection);
//            //Trying JSON
//            jsonObject=new JSONObject(response);
//
//            if (jsonObject.has(Constants.RESPONSE_REPLY))
//            {
//                String myToken = jsonObject.getString("refreshToken");
//                accountManager.setAuthToken(myAccount,"single",myToken);
//                Log.d(Constants.APP_TAG,"CheckStats->Refresh Token Set : " + myToken);
//                //Orders
//                JSONArray statsArray = jsonObject.getJSONArray("statsQuery");
//                stats = new ArrayList<String>();
//                boolean ok= stats.add(String.valueOf(statsArray));
//                if(ok){
//                    Log.d(Constants.APP_TAG,"CheckStats->Stats Loaded");
//                    //return 1;
//                   // result=1;
//                }else{
//                    Log.d(Constants.APP_TAG,"CheckStats->Stats Nil.");
//                    //return 0;
//                    //result=0;
//                }
//            }//If Object Has reply
//        }
//        return i;
//    } //checkStats()
    //---------------getInitData()--------------------//
//    private int getInitData(ContentProviderClient providerClient) throws RemoteException, Exception, JSONException{
//
//        String response=Constants.EMPTY_STRING;
//        URL mUrl=null;
//        HttpURLConnection mConnection=null;
//        String mQuery = Constants.APP_TOKEN_KEY + Constants.EQUALS + token;
//        Log.d(Constants.APP_TAG,mQuery);
//        JSONObject jsonObject=null;
//
//        mUrl = new URL(Constants.FETCH_ALL + mQuery);
//        mConnection = (HttpURLConnection) mUrl.openConnection();
//        Log.d(Constants.APP_TAG,"SyncAdapter->getInitData: Server URL is : " +mUrl.toString());
//        int i = mConnection.getResponseCode();
//
//        if (i == 200){
//            //Now delete all data
//            deleteAllData(providerClient);
//            // Go Ahead
//            Log.d(Constants.APP_TAG,"SyncAdapter->loadAllData: Updating local database...");
//            //Every Thing is Ok, Read the response
//            response = readData(mConnection);
//            //Trying JSON
//            jsonObject=new JSONObject(response);
//            //Check if there is a reply
//            if (jsonObject.has(Constants.RESPONSE_REPLY))
//            {
//                ContentValues values = new ContentValues();
//                Uri uri = null;
//                /**         Load Staff Data         */
//                JSONArray staffArray = jsonObject.getJSONArray("staffQuery");
//                for (int j = 0; j < staffArray.length(); j++)
//                {
//                    JSONObject Object= staffArray.getJSONObject(j);
//
//                    values.put(StaffContract.STAFF_ID,Integer.parseInt(Object.getString("id")));
//                    values.put(StaffContract.STAFF_NAME, Object.getString("name"));
//                    values.put(StaffContract.STAFF_ROLE, Object.getString("role"));
//                    values.put(StaffContract.STAFF_CITY, Object.getString("city"));
//
//                    uri = providerClient.insert(MasterSahabProvider.CONTENT_URI_STAFF, values);
//                }
//                Log.d(Constants.APP_TAG, "Staff Data uploaded.");
//                /**         Load Tailors Data       */
//                JSONArray tailorArray = jsonObject.getJSONArray("tailorsQuery");
//                values.clear();
//                for (int l = 0 ; l < tailorArray.length(); l++){
//                    JSONObject Object = tailorArray.getJSONObject(l);
//
//                    values.put(TailorsContract.TAILOR_ID, Integer.parseInt(Object.getString("id")));
//                    values.put(TailorsContract.TAILOR_CARD_NUMBER, Object.getString("card_number"));
//                    values.put(TailorsContract.TAILOR_PIC, Object.getString("tailor_pic"));
//                    values.put(TailorsContract.TAILOR_NAME, Object.getString("tailor_name"));
//                    values.put(TailorsContract.TAILOR_SHOP_NAME, Object.getString("shop_name"));
//                    values.put(TailorsContract.TAILOR_CNIC, Object.getString("tailor_cnic"));
//                    values.put(TailorsContract.TAILOR_CONTACT, Object.getString("tailor_contact"));
//                    values.put(TailorsContract.TAILOR_AREA, Object.getString("tailor_area"));
//                    values.put(TailorsContract.TAILOR_ADDRESS, Object.getString("tailor_address"));
//                    values.put(TailorsContract.TAILOR_CITY, Object.getString("tailor_city"));
//                    values.put(TailorsContract.TAILOR_ACTIVE, Integer.parseInt(Object.getString("active")));
//
//                    uri = providerClient.insert(MasterSahabProvider.CONTENT_URI_TAILORS, values);
//                }
//                Log.d(Constants.APP_TAG,"Tailors Data uploaded.");
//                /**         Load Suits Data         */
//                JSONArray suitsArray = jsonObject.getJSONArray("suitsQuery");
//                values.clear();
//                for (int l = 0 ; l < suitsArray.length(); l++){
//                    JSONObject Object = suitsArray.getJSONObject(l);
//
//                    values.put(SuitsContract.SUITS_ID, Integer.parseInt(Object.getString("id")));
//                    values.put(SuitsContract.SUITS_CODE, Object.getString("suit_code"));
//                    values.put(SuitsContract.SUIT_CATALOG, Object.getString("suit_catalog"));
//                    values.put(SuitsContract.SUITS_COLOR, Object.getString("suit_color"));
//                    values.put(SuitsContract.SUITS_TYPE, Object.getString("suit_type"));
//                    values.put(SuitsContract.SUITS_DESCRIPTION, Object.getString("suit_description"));
//                    values.put(SuitsContract.SUITS_PRICE,Integer.parseInt(Object.getString("suit_price")));
//                    values.put(SuitsContract.SUITS_ACTIVE, Integer.parseInt(Object.getString("active")));
//
//                    uri = providerClient.insert(MasterSahabProvider.CONTENT_URI_SUITS, values);
//                }
//                Log.d(Constants.APP_TAG,"Suits Data uploaded.");
//                /**         Load Orders Data        */
//                JSONArray ordersArray = jsonObject.getJSONArray("ordersQuery");
//                values.clear();
//                for (int l = 0; l < ordersArray.length(); l++){
//                    JSONObject Object = ordersArray.getJSONObject(l);
//
//                    values.put(OrdersContract.ORDER_ID,Integer.parseInt(Object.getString("id")));
//                    values.put(OrdersContract.ORDER_DATE,Object.getString("order_placement_date"));
//                    values.put(OrdersContract.ORDER_CARD_NUMBER,Object.getString("card_number"));
//                    values.put(OrdersContract.ORDER_STAFF_ID,Integer.parseInt(Object.getString("staff_id")));
//                    values.put(OrdersContract.ORDER_QUANTITY,Integer.parseInt(Object.getString("quantity")));
//                    values.put(OrdersContract.ORDER_SUIT_CODE,Object.getString("suit_code"));
//                    values.put(OrdersContract.ORDER_STAFF_CITY,Object.getString("staff_city"));
//                    values.put(OrdersContract.ORDER_SUIT_PRICE,Integer.parseInt(Object.getString("suit_price")));
//                    values.put(OrdersContract.ORDER_SUIT_COLOR,Object.getString("suit_color"));
//                    values.put(OrdersContract.ORDER_SUIT_STATUS,Object.getString("order_status"));
//                    values.put(OrdersContract.ORDER_STATUS_CHANGE_DATE,Object.getString("order_status_change_date"));
//                    values.put(OrdersContract.ORDER_SUIT_DISCOUNTED_PRICE,Object.getString("suit_discounted_price"));
//                    values.put(OrdersContract.ORDER_REDEEM,Integer.parseInt(Object.getString("redeem")));
//                    values.put(OrdersContract.ORDER_RECEIPT,Object.getString("receipt"));
//                    values.put(OrdersContract.ORDER_SUIT_CATALOUGE,Object.getString("suit_catlouge"));
//
//                    uri = providerClient.insert(MasterSahabProvider.CONTENT_URI_ORDERS, values);
//                }
//                Log.d(Constants.APP_TAG,"Orders Data uploaded.");
//                /**         Load Stats Data     **/
//                JSONArray statsArray = jsonObject.getJSONArray("statsQuery");
//                values.clear();
//                for (int l = 0; l < statsArray.length(); l++){
//                    JSONObject Object = statsArray.getJSONObject(l);
//
//                    values.put(StatsContract.STATS_ID,Integer.parseInt(Object.getString("id")));
//                    values.put(StatsContract.STATS_TABLE_NAME,Object.getString("table_name"));
//                    values.put(StatsContract.STATS_CREATEDAT,Object.getString("created_at"));
//                    values.put(StatsContract.STATS_UPDATEDAT,Object.getString("updated_at"));
//
//                    uri = providerClient.insert(MasterSahabProvider.CONTENT_URI_STATS, values);
//
//                }
//                Log.d(Constants.APP_TAG,"Stats Data uploaded.");
//                /**             Load Merchandise Data */
//                JSONArray mercArray = jsonObject.getJSONArray("mercQuery");
//                values.clear();
//                for (int l = 0; l < mercArray.length(); l++){
//                    JSONObject Object = mercArray.getJSONObject(l);
//
//                    values.put(MerchandisesContract.MERCHANDISE_ID,Integer.parseInt(Object.getString("id")));
//                    values.put(MerchandisesContract.MERCHANDISE_DATE,Object.getString("date"));
//                    values.put(MerchandisesContract.MERCHANDISE_TAILOR_CITY,Object.getString("tailor_city"));
//                    values.put(MerchandisesContract.MERCHANDISE_CARD_NUMBER,Object.getString("card_number"));
//                    values.put(MerchandisesContract.MERCHANDISE_ITEM_PIC,Object.getString("item_pic"));
//                    values.put(MerchandisesContract.MERCHANDISE_ITEM_NAME,Object.getString("item_name"));
//                    values.put(MerchandisesContract.MERCHANDISE_ITEM_LOC,Object.getString("item_geo_loc"));
//                    values.put(MerchandisesContract.MERCHANDISE_SYNCABLE,Object.getString("syncable"));
//
//                    uri = providerClient.insert(MasterSahabProvider.CONTENT_URI_MERCHANDISES, values);
//                }
//                Log.d(Constants.APP_TAG, "Merchandise Data uploaded.");
//                return 0;
//            }//If Object Has reply
//        } else {
//            Log.d(Constants.APP_TAG, "SyncAdapter->loadAllData: Unable to fetch data " + i);
//            return -1;
//        }
//        return 0;
//    }
    //---------------reLogCurrentUser()-----------------//
//    private int reLogCurrentUser(ContentProviderClient providerClient) throws IOException, JSONException
//    {
//
//        URL mUrl=null;
//        JSONObject jsonObject=null;
//        HttpURLConnection mConnection=null;
//        String mQuery = Constants.PREF_USERNAME_KEY + Constants.EQUALS
//                + accountManager.getUserData(myAccount,Constants.PREF_USERNAME_KEY)
//                + Constants.AMPERSAND + Constants.PREF_PASSWORD_KEY + Constants.EQUALS
//                + accountManager.getUserData(myAccount,Constants.PREF_PASSWORD_KEY);
//
//            mUrl = new URL(Constants.LOGIN_URL + mQuery);
//            mConnection = (HttpURLConnection) mUrl.openConnection();
//            mConnection.setDoOutput(true);
//            mConnection.setConnectTimeout(5000);
//            mConnection.setInstanceFollowRedirects(false);
//            mConnection.setRequestMethod(Constants.POST);
//            mConnection.setRequestProperty("Content-type", "application/json");
//            mConnection.setRequestProperty("charset", "utf-8");
//            //Write Post Data
//            DataOutputStream wr = new DataOutputStream(mConnection.getOutputStream());
//            wr.writeUTF(mQuery);
//            wr.flush();
//            wr.close();
//
//            int i = mConnection.getResponseCode();
//
//            if (i == 200) {
//
//                //Every Thing is Ok, Read the response
//                response = readData(mConnection);
//                //Trying JSON
//                jsonObject=new JSONObject(response);
//                //This response is returned by getTokenFromRest() if successful. use other methods to fetch all data.
//                if (jsonObject.has(Constants.APP_TOKEN_KEY))
//                {
//                    String myToken = jsonObject.get(Constants.APP_TOKEN_KEY).toString();
//                    accountManager.setAuthToken(myAccount, "single", myToken);
//                    Log.d(Constants.APP_TAG,"reLoginCurrentUser->Refreshed Token is : " + myToken);
//                    //Set the flag to false - It sets up , if time is above 60 minutes
//                    getNewToken=false;
//                    //Set timer
//                    AccountCreationDate=new Date();
//                    Helper.notify(getContext(),"Date is Reset. Token Refreshed.","Master Sahab");
//                }
//            }else{
//                Helper.notify(getContext(), "Error Refreshing Token. Try Again by Manually Syncing Application.", "Master Sahab");
//            }
//            return i;
//        }
}
