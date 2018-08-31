package com.karacraft.mastersahab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karacraft.utils.Constants;
import com.karacraft.utils.Helper;
import com.karacraft.utils.UploadImage;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Nov 22 2015
 * File Name        ProfileFragment.java
 * Comments
 */
public class ProfileFragment extends Fragment
{
    //Shared Preference
    SharedPreferences sharedPreferences;

    //Views
    ImageView mImageView;
    TextView mTextViewFullName;
    TextView mTextViewUserName;
    TextView mTextViewRole;
    TextView mTextViewId;
    TextView mTextViewCity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_profile,container,false);

        //Set Title
        getActivity().setTitle(R.string.profile_activity);

        //-------------------------------------------
        mImageView = (ImageView)rootView.findViewById(R.id.imageView_profilePicture);
        mImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent mImageFromGallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(mImageFromGallery, Constants.REQUEST_STAFF_PICTURE);
            }
        });
        //---------------------------------------------------------------
        mTextViewFullName = (TextView) rootView.findViewById(R.id.textView_fullName);
        mTextViewUserName = (TextView) rootView.findViewById(R.id.textView_userName);
        mTextViewRole = (TextView) rootView.findViewById(R.id.textView_role);
        mTextViewId = (TextView) rootView.findViewById(R.id.textView_id);
        mTextViewCity = (TextView)rootView.findViewById(R.id.textView_city);

        sharedPreferences = getActivity().getSharedPreferences(Constants.APP_PREF_KEY,
                Context.MODE_PRIVATE);
        mTextViewFullName.setText(sharedPreferences.getString(Constants.PREF_USER_NAME_KEY,""));
        mTextViewUserName.setText(sharedPreferences.getString(Constants.PREF_USERNAME_KEY,""));
        mTextViewRole.setText(sharedPreferences.getString(Constants.PREF_USER_ROLE_KEY,""));
        mTextViewId.setText(sharedPreferences.getString(Constants.PREF_USER_ID_KEY,""));
        mTextViewCity.setText(sharedPreferences.getString(Constants.PREF_USER_CITY_KEY,""));

        return rootView;
    }
    //----------------------------------onActivityResult-----------------------------//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case Constants.REQUEST_STAFF_PICTURE:
                if (resultCode == Activity.RESULT_OK)
                {
                    if (data != null)
                        {
                        //If we have data, Get the Bitmap
                        Bitmap mBitmap = Helper.loadImageFromGallery(getActivity(), data);
                        //Save the Bitmap to Directory
                        String[] info = Helper.uploadImageToRest(getActivity(), mBitmap, Helper.PICTURE_STAFF, mTextViewUserName.getText().toString());
                        mImageView.setImageBitmap(Helper.loadImageFromGallery(getActivity(), data));
                        saveImageToRest(info[1]);
                        }
                }
                break;
        }
    }
    //----------------------------------saveImageToRest-----------------------------//
    public void saveImageToRest(String filename){
        sharedPreferences = getActivity().getSharedPreferences(Constants.APP_PREF_KEY,
                Context.MODE_PRIVATE);
        String mToken = sharedPreferences.getString(Constants.APP_TOKEN_KEY,"");
        UploadImage uploadImage = new UploadImage(getActivity(),filename,mToken);
        uploadImage.execute();
    }
}
