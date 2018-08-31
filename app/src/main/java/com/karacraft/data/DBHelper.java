package com.karacraft.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Entity;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.karacraft.security.MasterSahabProvider;
import com.karacraft.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Mar 20 2016
 * File Name
 * Comments
 */
public class DBHelper
{
    //------------------readData()-----------------------------------//
    public static String readData(HttpURLConnection conn){

        String mResponse="";
        String line="";
        BufferedReader mBufferedReader = null;
        //Try to Read the InputStream
        try
        {
            mBufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = mBufferedReader.readLine()) != null) {
                mResponse = mResponse + line;
            }
            mBufferedReader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }finally
        {
           if(mBufferedReader !=null)
               mBufferedReader=null;
        }
        return mResponse;
    }

    //-----------------deleteAllData()--------------------//
    public static void deleteAllData(ContentProviderClient contentProviderClient){

        Log.d(Constants.APP_TAG, "DBHelper -> deleteAllData : Performing Delete...");
        try
        {
            contentProviderClient.delete(MasterSahabProvider.CONTENT_URI_ORDERS,null,null);
            contentProviderClient.delete(MasterSahabProvider.CONTENT_URI_SUITS, null, null);
            contentProviderClient.delete(MasterSahabProvider.CONTENT_URI_STAFF, null, null);
            contentProviderClient.delete(MasterSahabProvider.CONTENT_URI_TAILORS, null, null);
            contentProviderClient.delete(MasterSahabProvider.CONTENT_URI_STATS,null,null);
            contentProviderClient.delete(MasterSahabProvider.CONTENT_URI_MERCHANDISES, null, null);

        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
    //---------------getInitData()--------------------//
    public static int getInitData(ContentProviderClient providerClient,String token) throws Exception
    {

        String response=Constants.EMPTY_STRING;
        URL mUrl=null;
        HttpURLConnection mConnection=null;
        String mQuery = Constants.APP_TOKEN_KEY + Constants.EQUALS + token;
        Log.d(Constants.APP_TAG,mQuery);
        JSONObject jsonObject=null;

        mUrl = new URL(Constants.FETCH_ALL + mQuery);
        mConnection = (HttpURLConnection) mUrl.openConnection();
        Log.d(Constants.APP_TAG,"DBHelper -> getInitData: Server URL is : " +mUrl.toString());
        int i = mConnection.getResponseCode();

        if (i == 200){
            //Now delete all data
            deleteAllData(providerClient);
            // Go Ahead
            Log.d(Constants.APP_TAG,"DBHelper -> Updating local database...");
            //Every Thing is Ok, Read the response
            response = readData(mConnection);
            //Trying JSON
            jsonObject=new JSONObject(response);
            //Check if there is a reply
            if (jsonObject.has(Constants.RESPONSE_REPLY))
            {
                ContentValues values = new ContentValues();
                Uri uri = null;
                /**         Load Staff Data         */
                JSONArray staffArray = jsonObject.getJSONArray("staffQuery");
                for (int j = 0; j < staffArray.length(); j++)
                {
                    JSONObject Object= staffArray.getJSONObject(j);

                    values.put(StaffContract.STAFF_ID,Integer.parseInt(Object.getString("id")));
                    values.put(StaffContract.STAFF_NAME, Object.getString("name"));
                    values.put(StaffContract.STAFF_ROLE, Object.getString("role"));
                    values.put(StaffContract.STAFF_CITY, Object.getString("city"));

                    uri = providerClient.insert(MasterSahabProvider.CONTENT_URI_STAFF, values);
                }
                Log.d(Constants.APP_TAG, "Staff Data uploaded.");
                /**         Load Tailors Data       */
                JSONArray tailorArray = jsonObject.getJSONArray("tailorsQuery");
                values.clear();
                for (int l = 0 ; l < tailorArray.length(); l++){
                    JSONObject Object = tailorArray.getJSONObject(l);

                    values.put(TailorsContract.TAILOR_ID, Integer.parseInt(Object.getString("id")));
                    values.put(TailorsContract.TAILOR_CARD_NUMBER, Object.getString("card_number"));
                    values.put(TailorsContract.TAILOR_PIC, Object.getString("tailor_pic"));
                    values.put(TailorsContract.TAILOR_NAME, Object.getString("tailor_name"));
                    values.put(TailorsContract.TAILOR_SHOP_NAME, Object.getString("shop_name"));
                    values.put(TailorsContract.TAILOR_CNIC, Object.getString("tailor_cnic"));
                    values.put(TailorsContract.TAILOR_CONTACT, Object.getString("tailor_contact"));
                    values.put(TailorsContract.TAILOR_AREA, Object.getString("tailor_area"));
                    values.put(TailorsContract.TAILOR_ADDRESS, Object.getString("tailor_address"));
                    values.put(TailorsContract.TAILOR_CITY, Object.getString("tailor_city"));
                    values.put(TailorsContract.TAILOR_ACTIVE, Integer.parseInt(Object.getString("active")));

                    uri = providerClient.insert(MasterSahabProvider.CONTENT_URI_TAILORS, values);
                }
                Log.d(Constants.APP_TAG,"Tailors Data uploaded.");
                /**         Load Suits Data         */
                JSONArray suitsArray = jsonObject.getJSONArray("suitsQuery");
                values.clear();
                for (int l = 0 ; l < suitsArray.length(); l++){
                    JSONObject Object = suitsArray.getJSONObject(l);

                    values.put(SuitsContract.SUITS_ID, Integer.parseInt(Object.getString("id")));
                    values.put(SuitsContract.SUITS_CODE, Object.getString("suit_code"));
                    values.put(SuitsContract.SUIT_CATALOG, Object.getString("suit_catalog"));
                    values.put(SuitsContract.SUITS_COLOR, Object.getString("suit_color"));
                    values.put(SuitsContract.SUITS_TYPE, Object.getString("suit_type"));
                    values.put(SuitsContract.SUITS_DESCRIPTION, Object.getString("suit_description"));
                    values.put(SuitsContract.SUITS_PRICE,Integer.parseInt(Object.getString("suit_price")));
                    values.put(SuitsContract.SUITS_ACTIVE, Integer.parseInt(Object.getString("active")));

                    uri = providerClient.insert(MasterSahabProvider.CONTENT_URI_SUITS, values);
                }
                Log.d(Constants.APP_TAG,"Suits Data uploaded.");
                /**         Load Orders Data        */
                JSONArray ordersArray = jsonObject.getJSONArray("ordersQuery");
                values.clear();
                for (int l = 0; l < ordersArray.length(); l++){
                    JSONObject Object = ordersArray.getJSONObject(l);

                    values.put(OrdersContract.ORDER_ID,Integer.parseInt(Object.getString("id")));
                    values.put(OrdersContract.ORDER_DATE,Object.getString("order_placement_date"));
                    values.put(OrdersContract.ORDER_CARD_NUMBER,Object.getString("card_number"));
                    values.put(OrdersContract.ORDER_STAFF_ID,Integer.parseInt(Object.getString("staff_id")));
                    values.put(OrdersContract.ORDER_QUANTITY,Integer.parseInt(Object.getString("quantity")));
                    values.put(OrdersContract.ORDER_SUIT_CODE,Object.getString("suit_code"));
                    values.put(OrdersContract.ORDER_STAFF_CITY,Object.getString("staff_city"));
                    values.put(OrdersContract.ORDER_SUIT_PRICE,Integer.parseInt(Object.getString("suit_price")));
                    values.put(OrdersContract.ORDER_SUIT_COLOR,Object.getString("suit_color"));
                    values.put(OrdersContract.ORDER_SUIT_STATUS,Object.getString("order_status"));
                    values.put(OrdersContract.ORDER_STATUS_CHANGE_DATE,Object.getString("order_status_change_date"));
                    values.put(OrdersContract.ORDER_SUIT_DISCOUNTED_PRICE,Object.getString("suit_discounted_price"));
                    values.put(OrdersContract.ORDER_REDEEM,Integer.parseInt(Object.getString("redeem")));
                    values.put(OrdersContract.ORDER_RECEIPT,Object.getString("receipt"));
                    values.put(OrdersContract.ORDER_SUIT_CATALOUGE,Object.getString("suit_catlouge"));

                    uri = providerClient.insert(MasterSahabProvider.CONTENT_URI_ORDERS, values);
                }
                Log.d(Constants.APP_TAG,"Orders Data uploaded.");
                /**         Load Stats Data     **/
                JSONArray statsArray = jsonObject.getJSONArray("statsQuery");
                values.clear();
                for (int l = 0; l < statsArray.length(); l++){
                    JSONObject Object = statsArray.getJSONObject(l);

                    values.put(StatsContract.STATS_ID,Integer.parseInt(Object.getString("id")));
                    values.put(StatsContract.STATS_TABLE_NAME,Object.getString("table_name"));
                    values.put(StatsContract.STATS_CREATEDAT,Object.getString("created_at"));
                    values.put(StatsContract.STATS_UPDATEDAT,Object.getString("updated_at"));

                    uri = providerClient.insert(MasterSahabProvider.CONTENT_URI_STATS, values);

                }
                Log.d(Constants.APP_TAG,"Stats Data uploaded.");
                /**             Load Merchandise Data */
                JSONArray mercArray = jsonObject.getJSONArray("mercQuery");
                values.clear();
                for (int l = 0; l < mercArray.length(); l++){
                    JSONObject Object = mercArray.getJSONObject(l);

                    values.put(MerchandisesContract.MERCHANDISE_ID,Integer.parseInt(Object.getString("id")));
                    values.put(MerchandisesContract.MERCHANDISE_DATE,Object.getString("date"));
                    values.put(MerchandisesContract.MERCHANDISE_TAILOR_CITY,Object.getString("tailor_city"));
                    values.put(MerchandisesContract.MERCHANDISE_CARD_NUMBER,Object.getString("card_number"));
                    values.put(MerchandisesContract.MERCHANDISE_ITEM_PIC,Object.getString("item_pic"));
                    values.put(MerchandisesContract.MERCHANDISE_ITEM_NAME,Object.getString("item_name"));
                    values.put(MerchandisesContract.MERCHANDISE_ITEM_LOC,Object.getString("item_geo_loc"));
                    values.put(MerchandisesContract.MERCHANDISE_SYNCABLE,Object.getString("syncable"));

                    uri = providerClient.insert(MasterSahabProvider.CONTENT_URI_MERCHANDISES, values);
                }
                Log.d(Constants.APP_TAG, "Merchandise Data uploaded.");
                return 0;
            }//If Object Has reply
        } else {
            Log.d(Constants.APP_TAG, "SyncAdapter->loadAllData: Unable to fetch data " + i);
            return -1;
        }
        return 0;
    }
    //---------------reLogCurrentUser()-----------------//
    public static String reLogCurrentUser(AccountManager accountManager,Account myAccount)
    {

        String response="";
        URL mUrl=null;
        JSONObject jsonObject=null;
        HttpURLConnection mConnection=null;

        String mQuery = Constants.PREF_USERNAME_KEY + Constants.EQUALS
                + accountManager.getUserData(myAccount,Constants.PREF_USERNAME_KEY)
                + Constants.AMPERSAND + Constants.PREF_PASSWORD_KEY + Constants.EQUALS
                + accountManager.getUserData(myAccount,Constants.PREF_PASSWORD_KEY);

        try
        {
            mUrl = new URL(Constants.LOGIN_URL + mQuery);
            mConnection = (HttpURLConnection) mUrl.openConnection();
            mConnection.setDoOutput(true);
            mConnection.setConnectTimeout(5000);
            mConnection.setInstanceFollowRedirects(false);
            mConnection.setRequestMethod(Constants.POST);
            mConnection.setRequestProperty("Content-type", "application/json");
            mConnection.setRequestProperty("charset", "utf-8");
            //Write Post Data
            DataOutputStream wr = new DataOutputStream(mConnection.getOutputStream());
            wr.writeUTF(mQuery);
            wr.flush();
            wr.close();

            int i = mConnection.getResponseCode();

            if (i == 200) {
                //Every Thing is Ok, Read the response
                response =DBHelper.readData(mConnection);
                //Trying JSON
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
//                    Helper.notify(getContext(), "Date is Reset. Token Refreshed.", "Master Sahab");
//                }
            }
            return response;
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (ProtocolException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return response;
    }
    //--------------selectNewMerchandise()------------------------//
    public static JSONArray selectNewMerchandise(Context context){
        Cursor cursor=null;
        JSONArray resultSet= new JSONArray();
        JSONObject result =null;

        cursor = context.getContentResolver().query(MasterSahabProvider.CONTENT_URI_MERCHANDISES
                ,MerchandisesContract.PROJECTION
                ,"syncable=?",new String[]{"new"},null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false){
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            Log.d(Constants.APP_TAG, cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Log.d(Constants.APP_TAG, e.getMessage()  );
                    }
                }
            }// End For
            resultSet.put(rowObject);
            cursor.moveToNext();
        } // End While
        cursor.close();
        //Log.d(Constants.APP_TAG, resultSet.toString());
        return resultSet;
    }
    //-----------------syncNewMerchandise()---------------------------//
    public static int syncNewMerchandise(String token, JSONArray jsonArray) throws IOException
    {
        int flag;
        String response=Constants.EMPTY_STRING;
        URL mUrl=null;
        HttpURLConnection mConnection=null;
        String message=jsonArray.toString();
        JSONObject myObject = new JSONObject();

        String mQuery = Constants.APP_TOKEN_KEY
                + Constants.EQUALS + token + Constants.AMPERSAND + "newmerc=" + message;

        Log.d(Constants.APP_TAG,"================== Syncing New Merchandise =================" );
        Log.d(Constants.APP_TAG, "syncNewMerchandise -> Query is : " + Constants.CREATE_MERCHANDISE + mQuery);

        mUrl = new URL(Constants.CREATE_MERCHANDISE + mQuery);
        mConnection = (HttpURLConnection) mUrl.openConnection();
        mConnection.setDoInput(true);
        mConnection.setDoOutput(true);
        //mConnection.setFixedLengthStreamingMode(message.getBytes().length);
        mConnection.setConnectTimeout(5000);
        mConnection.setInstanceFollowRedirects(false);
        mConnection.setRequestMethod(Constants.POST);

        mConnection.setRequestProperty("Content-type", "application/json");
        mConnection.setRequestProperty("charset", "utf-8");
        //Write Post Data

        DataOutputStream wr = new DataOutputStream(mConnection.getOutputStream());
        wr.writeUTF(message.getBytes().toString());
        wr.flush();
        wr.close();

        flag = mConnection.getResponseCode();
        if (flag != 200) {
            Log.d(Constants.APP_TAG,"Failure " + flag + " - " + mConnection.getResponseMessage());
        }
        if (flag == 200){
            response = readData(mConnection);
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.has("reply"))
                    Log.d(Constants.APP_TAG,"Reply Received -> " + jsonObject.getString("reply"));
                if(jsonObject.has("newmercs"))
                    Log.d(Constants.APP_TAG,"Data Received -> " + jsonObject.getString("newmercs"));
                if(jsonObject.has("count"))
                    Log.d(Constants.APP_TAG,"Count Received -> " + jsonObject.getString("count"));
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return flag;
    }
    public static int check(String token) throws IOException
    {
        int flag;
        String response=Constants.EMPTY_STRING;
        URL mUrl=null;
        HttpURLConnection mConnection=null;

        String message="";
        JSONObject myObject1 = new JSONObject();
        JSONObject myObject2 = new JSONObject();
        JSONObject myObject3 = new JSONObject();

        JSONArray myArray = new JSONArray();
        JSONObject mySentObject = new JSONObject();
        try {
            myObject1.put("id","01");
            myObject1.put("name","Ali Jibran");
            myObject1.put("date","2016-04-01");
            myArray.put(myObject1);
            myObject2.put("id","02");
            myObject2.put("name","Imran Raja");
            myObject2.put("date","2016-05-03");
            myArray.put(myObject2);
            myObject3.put("id","03");
            myObject3.put("name","M.Quddus Raja");
            myObject3.put("date","2015-06-01");
            myArray.put(myObject3);
            //mySentObject.put("data",myArray.toString());
            //message = mySentObject.toString();
            message = myArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //String mQuery = Constants.APP_TOKEN_KEY
        //        + Constants.EQUALS + token + Constants.AMPERSAND + "posted=" + message;
        String mQuery ="token=" + token + "&posted=" + message;
        byte[] bytes = mQuery.getBytes("UTF-8");

        Log.d(Constants.APP_TAG,"================== Checking =================" );
        Log.d(Constants.APP_TAG, "check -> Query is : " + Constants.APP_URL + "info?" + mQuery);

        mUrl = new URL(Constants.APP_URL + "info?" + mQuery);
        mConnection = (HttpURLConnection) mUrl.openConnection();
//        mConnection.setDoInput(true);
        mConnection.setDoOutput(true);
        mConnection.setFixedLengthStreamingMode(bytes.length);
        mConnection.setConnectTimeout(5000);
        mConnection.setInstanceFollowRedirects(false);
        mConnection.setRequestMethod(Constants.POST);
        mConnection.setRequestProperty("Content-type", "application/json");
        mConnection.setRequestProperty("charset", "utf-8");
        //Write Post Data

        OutputStream wr = mConnection.getOutputStream();
        wr.write(bytes);
        wr.flush();
        wr.close();

        flag = mConnection.getResponseCode();
        if (flag != 200) {
            Log.d(Constants.APP_TAG,flag + " " + mConnection.getResponseMessage());
        }
        if (flag == 200){
            response = readData(mConnection);
            Log.d(Constants.APP_TAG,"RESPONSE : " + response);
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.has("reply"))
                    Log.d(Constants.APP_TAG,"Reply Received -> " + jsonObject.getString("reply"));
                if(jsonObject.has("newmercs"))
                    Log.d(Constants.APP_TAG,"Data Received -> " + jsonObject.getString("newmercs"));
                if(jsonObject.has("count"))
                    Log.d(Constants.APP_TAG,"Count Received -> " + jsonObject.getString("count"));
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return flag;
    }

}
