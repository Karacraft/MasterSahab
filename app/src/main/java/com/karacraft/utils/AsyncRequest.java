package com.karacraft.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.karacraft.mastersahab.BuildConfig;
import com.karacraft.mastersahab.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Nov 17 2015
 * File Name        AsyncRequest.java
 * Comments         SubClass of AsyncTask, Takes as Parameters
 *                  Activity(type:activity)
 *                  Method(POST/GET type:String)
 *                  Parameters(type: ContentValues)
 *                  Shows a ProgressBar
 */

public class AsyncRequest extends AsyncTask<String /**Parameter**/,Integer /**Progress**/,String /**Result**/>
{
    OnAsyncRequestComplete  mListener;
    Context                 mContext;
    String                  mMethod;
    String                  mTypeData;
    ProgressDialog          mProgressDialog=null;

    //----------------------------------INTERFACE-----------------------------//
    //----------------------------------OnAsyncRequestComplete-----------------------------//
    // Interface to be implemented by calling activity
    public interface OnAsyncRequestComplete {
        void asyncResponse(String response,boolean key);
    }
    //----------------------------------AsyncRequest()-----------------------------//

    /**
     * Initialzie AsyncRequest Properly
     * @param activity Activty Context() - in Fragment getActivity()
     * @param caller  If Fragment, then Full Fragment Name i.e LoginFragment.this
     * @param method  POST/GET
     */
    public AsyncRequest(Context activity,
                        OnAsyncRequestComplete caller,
                        String method ,
                        String typeData){

        mListener = caller;
        mContext = activity;
        mMethod = method;
        mTypeData = typeData;
    }
    //----------------------------------AsyncRequest()-----------------------------//
    public AsyncRequest(Context activity){
        mListener = (OnAsyncRequestComplete) activity;
        mContext = activity;
    }
    //----------------------------------Overrides-----------------------------//
    @Override
    protected String doInBackground(String... params)
    {
        //getInitData url pointing to entry point of API
        String mAddress = params[0].toString();
        if(mMethod == Constants.POST && mTypeData == Constants.AR_GET_TOKEN){
            return postGetToken(mAddress,
                    params[1].toString(),
                    params[2].toString());
        }
        if(mMethod == Constants.GET && mTypeData == Constants.AR_FETCH_INIT_DATA){
            return getInitData(mAddress,
                    params[1].toString());
        }
        return null;
    }
    //----------------------------------onPreExecute()-----------------------------//
    @Override
    protected void onPreExecute()
    {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.show();
        //super.onPreExecute();
    }
    //----------------------------------onProgressUpdate()-----------------------------//
    @Override
    protected void onProgressUpdate(Integer... values)
    {
        // you can implement some progressBar and update it in this record
        // setProgressPercent(progress[0]);
        super.onProgressUpdate(values);
    }
    //----------------------------------onPostExectue()-----------------------------//
    @Override
    protected void onPostExecute(String response)
    {
        //Dismiss Progress Dialog
        if(mProgressDialog !=null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
        //super.onPostExecute(response);
        if (isCancelled()){
            mListener.asyncResponse(response,false);
        }
        mListener.asyncResponse(response, true);
    }
    //----------------------------------onCancelled()-----------------------------//
    @Override
    protected void onCancelled(String response)
    {
        //Dismiss Progress Dialog
        if(mProgressDialog !=null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
        //super.onCancelled(response);
    }

    //----------------------------------getInitData()-----------------------------//
    @SuppressWarnings("deprecation")
    private String getInitData(String address, String token) {

        URL mUrl=null;
        HttpURLConnection mConnection=null;
        String mResponse="";
        String EQ = "=";
        String AMP = "&";
        String mQuery = address + Constants.APP_TOKEN_KEY +EQ+ token;
        Log.d(Constants.APP_TAG,mQuery);
        // Do some work here like download data
        try {
            mUrl = new URL(mQuery);
            mConnection = (HttpURLConnection) mUrl.openConnection();

            int i = mConnection.getResponseCode();

            if (i != 200) {
                //Something happened, response is failure
                return toJsonResponse("Response Code: " + i + " Message: " + mConnection.getResponseMessage(),false);
            }
            //Every Thing is Ok, Read the response
            mResponse = readData(mConnection);
            //Disconnect Connection
            mConnection.disconnect();
            //Response is good. Return.
            return  mResponse;

        } catch (MalformedURLException e) {
            return toJsonResponse(e.toString(),false);
        } catch (IOException e) {
            return  toJsonResponse(e.toString(),false);
        }
    }
    //-------------------postGetToken()-----------------------------
    private String postGetToken(String address, String username, String password) {

        URL mUrl=null;
        HttpURLConnection mConnection=null;
        String mResponse="";
        String EQ = "=";
        String AMP = "&";
        String mQuery = Constants.PREF_USERNAME_KEY + EQ + username + AMP + Constants.PREF_PASSWORD_KEY + EQ + password;

        try {
            mUrl = new URL(address + mQuery);
            //This won't compile with release built
            if (BuildConfig.DEBUG)
            {
                Log.d(Constants.APP_TAG, "AsyncRequest->postGetToken: " + mUrl.toString());            }

            mConnection = (HttpURLConnection) mUrl.openConnection();
            mConnection.setDoOutput(true);
            mConnection.setConnectTimeout(10000);
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

            if (i != 200) {
                return toJsonResponse("Response Code: " + i + " Message: " + mConnection.getResponseMessage(),false);
            }
            //Every Thing is Ok, Read the response
            mResponse = readData(mConnection);
            //Disconnect Connection
            mConnection.disconnect();
            return  mResponse ;

        } catch (MalformedURLException e) {

            return toJsonResponse(e.toString(),false);
        } catch (IOException e) {

            return toJsonResponse(e.toString(),false);
        }
    }
    //------------------readData()-----------------------------------
    private String readData(HttpURLConnection conn){

        String mResponse="";
        String line="";
        BufferedReader mBufferedReader = null;
        //Try to Read the InputStream
        try
        {
            mBufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }
        catch (IOException e)
        {
            return toJsonResponse(e.toString(),false);
        }
        //Try to read the Object Line by Line
        try
        {
            while ((line = mBufferedReader.readLine()) != null) {
                mResponse = mResponse + line;
            }
            mBufferedReader.close();
            return mResponse;
        }
        catch (IOException e)
        {
            return toJsonResponse(e.toString(),false);
        }
    }
    //-------------
    private String toJsonResponse(String result,boolean key){

        JSONObject mJsonObject = new JSONObject();
        try{
            if (!key){
                mJsonObject.put("response","failure");
                mJsonObject.put("data",result);
            } else {
                mJsonObject.put("response","success");
                mJsonObject.put("data",result);
            }
            return  mJsonObject.toString();
        }catch (JSONException e){
            return "Error Occurred" + e.toString();
        }
    }
}
