package com.karacraft.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.karacraft.mastersahab.BuildConfig;
import com.karacraft.mastersahab.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by       Ali Jibran / Kara Craft
 * For Project      Master Sahab
 * Dated            Nov 02 2015
 * File Name        Helper
 * Comments         Utility Functions for All Project
 *                  make sure all functions are static
 *
 *                  ADD PIC RESOLUTION
 *                  320 x 480 (2:3 ratio)
 */
public class Helper
{

    private static final String TAG = "PTI";

    public static final String PICTURE_DIRECTORY    =   "msp";              //Directory to Save Pics
    public static final String PICTURE_TAILOR       =   "tailor_picture";   //For Images
    public static final String PICTURE_MERC         =   "merc_picture";    //For Images
    public static final String PICTURE_STAFF        =   "staff_picture";    //For Images

    //----------------------------------getAppVersionNumber()-----------------------------//
    public static String getAppVersionNumber(Context context){

        String versionNumber="";
        try
        {
            versionNumber = context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e)
        {
            //This won't compile with release built
            if (BuildConfig.DEBUG)
            {
                Log.d(TAG, "Helper->getAppVersionNumber: " + e.getMessage());
            }
        }

        return versionNumber;
    }
    //----------------------------createFilePath()------------------------------------------------
    public static String[] createFilePath(String makeDirectory,String tagName,String picName){

        File mStorageDirectory;
        boolean mSuccess = true;
        String mTimeStamp="";
        String mFileName="";
        String mFullPath="";
        String mPathWithoutFileName="";
        String mResponse[];

        if (!makeDirectory.equals("")){
            mStorageDirectory =new File( Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES)+File.separator + makeDirectory);
            if(!mStorageDirectory.exists()){
                mSuccess = mStorageDirectory.mkdir();
            }
        }else {
            mStorageDirectory =new File( Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES)+File.separator);
        }

        mPathWithoutFileName = mStorageDirectory.toString() + "/";

        if (tagName.equals(PICTURE_TAILOR)){
            mFileName = "TAILOR_" + picName + ".jpg";
            mFullPath = mStorageDirectory.toString() + "/" + mFileName;
        }else if (tagName.equals(PICTURE_MERC)){
            mTimeStamp = new SimpleDateFormat("yyyyMMdddd_HHmmss").format(new Date());
            mFileName = "MERC_" + picName + "_" + mTimeStamp + ".jpg";
            mFullPath = mStorageDirectory.toString() + "/" + mFileName;
        }else if(tagName.equals(PICTURE_STAFF)){
            mFileName = "STAFF_" + picName + ".jpg";
            mFullPath = mStorageDirectory.toString() + "/" + mFileName;
        }

        mResponse =new String[]{mFullPath,mFileName,mPathWithoutFileName};
        return  mResponse;
    }
    //------------------------------galleryAddPic()--------------------------
    public static void galleryAddPic(String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        //   this.sendBroadcast(mediaScanIntent);
    }
    //----------------notify()--------------------
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void notify(Context context,String info , String title){
        Notification notif = new Notification.Builder(context)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.akms_icon_invert_small)
                .setContentText(info).build();

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), notif);
    }
    //-------------------------getStringFromDate()--------------------//
    public static String getStringFromDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return simpleDateFormat.format(date);
    }
    //-------------------------getDateFromString()--------------------//
    public static Date getDateFromString(String string){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date=new Date();
        try
        {
            date = simpleDateFormat.parse(string);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }
    //------------------------------------isNetworkAvailable()---------------------
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return  networkInfo != null && networkInfo.isConnected();
    }
    //----------------------------------bitmapToBase64()----------------------------//
    public static String bitmapToBase64(Bitmap bitmap){
        String mEncodedString;
        ByteArrayOutputStream mByteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,mByteArrayOutputStream);
        byte[] mImageBytes = mByteArrayOutputStream.toByteArray();
        mEncodedString = Base64.encodeToString(mImageBytes,Base64.DEFAULT);

        return mEncodedString;
    }
    //----------------------------------LoadImageFromGallery()-----------------------------//
    public static Bitmap loadImageFromGallery(Context context, Intent data)
    {
        Bitmap mBitmap=null;
        if (data != null)
        {
            Uri mSelectedImage = data.getData();
            String[] mFilePathColumn = {MediaStore.Images.Media.DATA};
            Cursor mCursor = context.getContentResolver().query(mSelectedImage, mFilePathColumn, null, null, null);
            mCursor.moveToFirst();
            int mColumnIndex = mCursor.getColumnIndex(mFilePathColumn[0]);
            String mPicturePath = mCursor.getString(mColumnIndex);
            mBitmap = BitmapFactory.decodeFile(mPicturePath);
            //imageViewTailorDetail.setImageBitmap(BitmapFactory.decodeFile(mPicturePath));
            mCursor.close();
        }
        return mBitmap;
    }
    /**
     * Read Image from internal storage, Resizes it, display it and upload it
     * @param context   Calling Activity
     * @param bitmap    Bitmap to Load
     * @param tagName   PICTURE_TAILOR,PICTURE_STAFF,PICTURE_MERC
     * @param picName   TailorsContract Card Number , STAFF ID
     * @return          True or False
     */
    public static String[] uploadImageToRest(Context context,Bitmap bitmap,String tagName, String picName){

        String[] mFileInfo;                     //Holds Picture Data 1-Full Pat 2-FileName 3-Path without filename
        String mEncodedString;                  //Base64 Encoded
        OutputStream mOutputStream = null;      //OutputStream

        //Use Helper Method to get File Saving Path.
        mFileInfo=createFilePath(Helper.PICTURE_DIRECTORY,             //Directory name
                tagName,                                                //Is it TailorsContract/Merch/StaffContract
                picName);                                               //Name tag
        //Get Bitmap Information from ImageView
        Bitmap mBitmap =bitmap;
        //Change the size of Bitmap to desired Size
        Bitmap mScaledBitmap = Bitmap.createScaledBitmap(mBitmap,480,640,false);
        mEncodedString = Helper.bitmapToBase64(mScaledBitmap);

        //Now try to save the file to given path
        try
        {
            mOutputStream = new FileOutputStream(mFileInfo[0]);
            mScaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, mOutputStream);
            mOutputStream.close();

        } catch (java.io.IOException e)
        {
            Toast.makeText(context,"Error Saving Image " + mFileInfo[0], Toast.LENGTH_SHORT).show();
        }
        //Check Internet Connection and upload Picture
        if (Helper.isNetworkAvailable(context)){

        }
        return mFileInfo;
    }

}
