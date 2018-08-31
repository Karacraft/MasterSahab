package com.karacraft.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.BuildConfig;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.karacraft.mastersahab.Activity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Dec 03 2015
 * File Name        UploadImage.java
 * Comments
 */
public class UploadImage extends AsyncTask<Void,Void,Void>
{

    Context             mContext;
    String              mImageName;
    String              mToken;
    FileInputStream     mFileInputStream;
    String              mResponse;
    ProgressDialog      mProgressDialog=null;

    public UploadImage(Context context ,String imageName,String token)
    {
        mContext = context;
        mImageName = imageName;
        mToken = token;
    }

    public UploadImage(Context context,String imageName){
        mContext = context;
        mImageName = imageName;
    }

    @Override
    protected void onPreExecute()
    {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("Uploading Image...");
        mProgressDialog.show();
        //super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params)
    {

        String mIFileName = mImageName;
        String mLineEnd = "\r\n";
        String mTwoHyphens = "--";
        String boundary = "*****";
        String mPostURL =Constants.UPLOAD_IMAGE; //+ "token=" + mToken;
        Log.d(Constants.APP_TAG,mPostURL);

        try
        {
            mFileInputStream = new FileInputStream(
                    Environment.getExternalStorageDirectory().toString()+"/Pictures/msp/" + mIFileName);
            URL mUrl = new URL(mPostURL);
            HttpURLConnection mHttpUrlConnection = (HttpURLConnection) mUrl.openConnection();
            mHttpUrlConnection.setDoInput(true);
            mHttpUrlConnection.setDoOutput(true);
            mHttpUrlConnection.setUseCaches(false);
            mHttpUrlConnection.setConnectTimeout(10000);
            mHttpUrlConnection.setChunkedStreamingMode(1024);
            mHttpUrlConnection.setInstanceFollowRedirects(false);
            mHttpUrlConnection.setRequestMethod("POST");
            mHttpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            mHttpUrlConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary="+boundary);
            mHttpUrlConnection.setRequestProperty("charset", "utf-8");
            //Write Post Data
            DataOutputStream wr = new DataOutputStream(mHttpUrlConnection.getOutputStream());
            wr.writeBytes(mTwoHyphens + boundary + mLineEnd);
            wr.writeBytes("Content-Disposition: form-data; name=\"title\""+ mLineEnd);
            wr.writeBytes(mLineEnd);
            wr.writeBytes(mIFileName);
            wr.writeBytes(mLineEnd);
            wr.writeBytes(mTwoHyphens + boundary + mLineEnd);
            wr.writeBytes("Content-Disposition: form-data; name=\"description\""+ mLineEnd);
            wr.writeBytes(mLineEnd);
            wr.writeBytes("MSP StaffContract-TailorsContract-Merch Picture");
            wr.writeBytes(mLineEnd);
            wr.writeBytes(mTwoHyphens + boundary + mLineEnd);
            wr.writeBytes("Content-Disposition: form-data; name=\"photo\";filename=\""
                    + mIFileName +"\"" + mLineEnd);
            wr.writeBytes(mLineEnd);

            Log.d(Constants.APP_TAG,"Headers are written");

            // create a buffer of maximum size
            int bytesAvailable = mFileInputStream.available();

            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[ ] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                wr.write(buffer, 0, bufferSize);
                bytesAvailable = mFileInputStream.available();
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                bytesRead = mFileInputStream.read(buffer, 0,bufferSize);
            }
            wr.writeBytes(mLineEnd);
            wr.writeBytes(mTwoHyphens + boundary + mTwoHyphens + mLineEnd);

            mFileInputStream.close();
            wr.flush();
            wr.close();

            Log.d(Constants.APP_TAG, "File Sent , Response " + mHttpUrlConnection.getResponseCode());
            mResponse = readData(mHttpUrlConnection);
            Log.d(Constants.APP_TAG,mResponse);

            mHttpUrlConnection.disconnect();

        } catch (MalformedURLException e)
        {
            Toast.makeText(mContext,"ERROR : " + e.getMessage() , Toast.LENGTH_LONG).show();
            mResponse = "Error in Uploading Image : " + e.getMessage();
        } catch (IOException e)
        {
            Toast.makeText(mContext,"ERROR : " + e.getMessage() , Toast.LENGTH_LONG).show();
            mResponse = "Error in Uploading Image : " + e.getMessage();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        //super.onPostExecute(aVoid);

        //Dismiss Progress Dialog
        if(mProgressDialog !=null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }

        Toast.makeText(mContext, mResponse, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCancelled(Void aVoid)
    {
        //Dismiss Progress Dialog
        if(mProgressDialog !=null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
        //super.onCancelled(aVoid);
    }

    //------------------readData()-----------------------------------
    private String readData(HttpURLConnection conn){

        String mResponse=Constants.EMPTY_STRING;
        String line=Constants.EMPTY_STRING;
        BufferedReader mBufferedReader = null;
        //Try to Read the InputStream
        try
        {
            mBufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }
        catch (IOException e)
        {
           mResponse ="Error : " + e.getMessage();
        }
        //Try to read the Object Line by Line
        try
        {
            while ((line = mBufferedReader.readLine()) != null) {
                mResponse = mResponse + line;
            }
            mBufferedReader.close();

        }
        catch (IOException e)
        {
            mResponse = "Error : "  + e.getMessage();
        }
        catch (NullPointerException e)
        {
            mResponse = "Error : " + e.getMessage();
        }
        return mResponse;
    }
}
