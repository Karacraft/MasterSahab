package com.karacraft.utils;

import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by duke on 8/11/15.
 */
public class PhotoHandler implements Camera.PictureCallback{

    private Context context;

    public PhotoHandler(Context context){
        this.context = context;
    }


    //Implemented by Camera.PictureCallback
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        File pictureFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //Check if the directory exist then exit.
        if(!pictureFileDir.exists() && !pictureFileDir.mkdirs()){

            Log.d(Constants.APP_TAG,"PhotoHandler-> onPictureTaken: Can't create directory to save Image.");
            return;
        }
        //createProfilePicName() is defined in Base_AKMS
        String filename = pictureFileDir.getPath() + File.separator ;

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            Toast.makeText(context, "New Image Saved : " , Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.d(Constants.APP_TAG,"PhotoHandler-> onPictureTaken: File" + filename + "not saved: "
                    + e.getMessage());
            Toast.makeText(context, "Image could not be saved.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
