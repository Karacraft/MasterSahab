package com.karacraft.mastersahab;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.karacraft.data.MerchandisesContract;
import com.karacraft.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


/**
 * Created by       duke / Kara Craft
 * For Project      CriminalIntent
 * Dated            Nov 16 2015
 * File Name        CameraFragment.java
 * Comments
 */
public class CameraFragment extends Fragment
{
    public static final String EXTRA_PHOTO_FILENAME ="com.karacraft.camerafragment.photo_filename";

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;

    private String cardNumber = Constants.EMPTY_STRING;
    private String filename = Constants.EMPTY_STRING;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        //Check caller and set the filename accordingly
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null)
        {
            String orderBy = extras.getString(Constants.ORDER_BY);
            if (orderBy.equals(Constants.START_BY_MERCHANDISE))
            {
                cardNumber = extras.getString(MerchandisesContract.MERCHANDISE_CARD_NUMBER);
                filename ="MERC_" + cardNumber+ UUID.randomUUID().toString() + ".jpg";
            }
            if (orderBy.equals(Constants.START_BY_TAILOR))
            {
                //Add card Number
                filename = "TAILOR_" + cardNumber + ".jpg";
            }
            if (orderBy.equals(Constants.START_BY_STAFF))
            {

                filename = "STAFF_" + UUID.randomUUID().toString() + ".jpg";
            }
        }

        Button takePictureButton = (Button) rootView.findViewById(R.id.camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().finish();
                if (mCamera != null){
                    mCamera.takePicture(mShutterCallback,null,mJpegCallback);
                }
            }
        });

        mProgressContainer = rootView.findViewById(R.id.camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        mSurfaceView = (SurfaceView) rootView.findViewById(R.id.camera_surfaceview);
        SurfaceHolder holder=mSurfaceView.getHolder();
        // setType() and SURFACE_TYPE_PUSH_BUFFERS are both deprecated,
        // but are required for Camera preview to work on pre-3.0 devices.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //Implement SurfaceHolder Callback
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // Tell the camera to use this surface as its preview area
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException exception) {
                    //This won't compile with release built
                    if (BuildConfig.DEBUG) {
                        Log.e(Constants.APP_TAG, "CameraFragment->surfaceCreated: Error setting up preview display", exception);
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera == null)return;

                //Ther Surface has changed size; update the camera preview size
                Camera.Parameters parameters = mCamera.getParameters();
                //Camera.Size s =null; //To be reset in next Section
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(),width,height);
                parameters.setPreviewSize(s.width, s.height);
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(),width,height);
                parameters.setPictureSize(s.width,s.height);
                mCamera.setParameters(parameters);
                try {
                    mCamera.startPreview();
                }catch (Exception e){
                    //This won't compile with release built
                    if (BuildConfig.DEBUG) {
                        Log.e(Constants.APP_TAG, "CameraFragment->surfaceChanged: Couldn't start view", e);
                    }
                    mCamera.release();
                    mCamera=null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // We can no longer display on this surface, so stop the preview.
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }
        });

        return rootView;
    }

    //For Camera
    //--------------------------------------------onResume()-----------------------------------//
    @TargetApi(9)
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(0);
        } else {
            mCamera = Camera.open();
        }
        //This won't compile with release built
        if (BuildConfig.DEBUG) {
            Log.d(Constants.APP_TAG, "CameraFragment->onResume: Camera Open");
        }
    }
    @Override
    public void onPause() {
        super.onPause();

        if(mCamera !=null){
            mCamera.release();
            mCamera=null;
        }
        //This won't compile with release built
        if (BuildConfig.DEBUG) {
            Log.d(Constants.APP_TAG, "CameraFragment->onPause: Camera Released");
        }
    }
    //Resize Byte Array Data to Given Size
    public byte[] resizeImage(byte[] input) {
        Bitmap original = BitmapFactory.decodeByteArray(input, 0, input.length);
        Bitmap resized = Bitmap.createScaledBitmap(original, 640, 480, true);

        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 80, blob);

        return blob.toByteArray();
    }
    //--------------------------------------------getBestSupportedSize()-----------------------------------//
    /** A simple algorithm to get the largest size available. For a more
     * robust version, see CameraPreview.java in the ApiDemos
     * sample app from Android. */
    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }
    //--------------------------------------------Camera.Callbacks-----------------------------------//
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            // Display the progress indicator
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };
    //-------------------------------------------------------------------------------//
    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            //Get Resized Data Image
            byte[] resized = resizeImage(data);
            boolean dirCreated=false;        //Is Directory Available
            String fullPath = Constants.EMPTY_STRING;
            //Create directory if not exist
            File mStorageDirectory =new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES)+Constants.FILE_SEPARATOR+Constants.APP_PIC_DIR);
            if (!mStorageDirectory.exists())
            {
                   dirCreated = mStorageDirectory.mkdir();
            }
            //Create the file
            File file = new File(mStorageDirectory + Constants.FILE_SEPARATOR + filename);
            Log.d(Constants.APP_TAG,"File Path : " + file.toString());
            //Delete if exists
//            if (file.exists())
//                file.delete();
            //Save the jpeg data to Disk
            FileOutputStream os = null;
            boolean success = true;
            try {
                os = new FileOutputStream(file,true);
                os.write(resized);
            }catch (Exception e){
                //This won't compile with release built
                if (BuildConfig.DEBUG) {
                    Log.e(Constants.APP_TAG, "CameraFragment->onPictureTaken: Error Writing to file : " + file, e);
                }
                success=false;
            }finally {
                try {
                    if (os!=null){
                        os.close();
                    }
                } catch (IOException e) {
                    //This won't compile with release built
                    if (BuildConfig.DEBUG) {
                        Log.e(Constants.APP_TAG, "CameraFragment->onPictureTaken: Error Closing file : " + file,e );
                    }
                    success = false;
                }
            }
            if (success) {
                //This won't compile with release built
                if (BuildConfig.DEBUG) {
                    Log.i(Constants.APP_TAG, "CameraFragment->onPictureTaken: JPEG saved at : " + file);
                }
                //Set the photofilename at the on the result intent
                Intent i = new Intent();
                i.putExtra(EXTRA_PHOTO_FILENAME,file.toString());
                getActivity().setResult(Activity.RESULT_OK,i);
            }else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }
            getActivity().finish();
        }
    };

}
