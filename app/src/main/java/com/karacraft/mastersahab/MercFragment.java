package com.karacraft.mastersahab;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.karacraft.data.MerchandisesContract;
import com.karacraft.security.MasterSahabProvider;
import com.karacraft.utils.Constants;
import com.karacraft.utils.Helper;

import java.io.File;
import java.util.Date;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Feb 15 2016
 * File Name        MercFragment.java
 * Comments
 */
public class MercFragment extends Fragment
{

    public static final String EDIT_MERCHANDISE     = "edit_merc";
    public static final String ADD_MERCHANDISE      = "add_merc";

    TextView mercDate;
    TextView mercTailorCity;
    TextView mercTailorCardNumber;
    ImageView mercImage;
    Button selectTailor;
    Button saveData;
    Button cancelData;
    Spinner itemsSpinner;

    Camera camera;                               //For Picture

    String orderBy      =Constants.EMPTY_STRING;      //Who Asked for the Data
    String orderType    = Constants.ORDER_TYPE;
    String mercPicName  =Constants.EMPTY_STRING;  //Picture Name if picture selected

    long        itemId;         //Item ID - For itemsSpinner Spinner
    String      itemName;       //Item Name
    String      mercId;         //Merchandise Item ID

    boolean isItNewMerc         = false;
    boolean isPictureLoaded     = false;         //has user has selected a picture for Item.
    boolean isTailorSelected    = false;
    String tempItem             = Constants.EMPTY_STRING;

    Cursor cursor               = null;     //For Editing Merc Data
    SimpleCursorAdapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_merc,container,false);
        //Setup Controls
        setControls(rootView);
        //Set Title
        getActivity().setTitle("Merchandise");
        /** Get The Bundle Extras - This is different from savedStateInstance */
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null){
            orderBy = extras.getString(Constants.ORDER_BY);
            orderType = extras.getString(Constants.ORDER_TYPE);
            if (orderBy.equals(Constants.START_BY_MERCHANDISE_LIST)){
                if(orderType.equals(ADD_MERCHANDISE)){
                    isItNewMerc=true;
                }
                if(orderType.equals(EDIT_MERCHANDISE)){
                    mercId = extras.getString("id");
                    isItNewMerc=false;
                }
            } //orderType
        }//extras
        //Setup Adapter for Items
        ArrayAdapter<CharSequence> itemsAdapter = ArrayAdapter.createFromResource(
                getActivity()
                ,R.array.merchandising_items
                ,android.R.layout.simple_spinner_dropdown_item
        );
        // Specify the layout to use when the list of choices appears
        itemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemsSpinner.setAdapter(itemsAdapter);

        //If it is not a new Merchandise, then its an edit.
        if(!isItNewMerc){
            cursor = getActivity().getContentResolver().query(
                MasterSahabProvider.CONTENT_URI_MERCHANDISES.withAppendedPath(
                        MasterSahabProvider.CONTENT_URI_MERCHANDISES
                        ,String.valueOf(mercId)
                )
                ,MerchandisesContract.PROJECTION
                ,null
                ,null
                ,null
            );
            //If cursor has data, then bind it
            String item=Constants.EMPTY_STRING;
            if (cursor != null){
                if(cursor.moveToFirst()){
                    mercDate.setText(cursor.getString(1));
                    mercTailorCity.setText(cursor.getString(2));
                    mercTailorCardNumber.setText(cursor.getString(3));
                    tempItem = cursor.getString(5);
                    item = cursor.getString(4);
                    //TODO:: add option to load Image if it exist cursor.getString(4) / Geoloc is 5
                }
            }
            for (int i = 0; i < itemsSpinner.getCount() ; i++)
            {
                String value = (String) itemsSpinner.getItemAtPosition(i);
                if(value.equals(item)){
                    itemsSpinner.setSelection(i);
                }
            }
            isTailorSelected=true;
            //Set the Item list Value to the Data in Record
            setItemListValue(tempItem);
        }

        //Select Tailor Button
        selectTailor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(),TailorsListActivity.class);
                intent.putExtra(Constants.ORDER_BY, Constants.START_BY_MERCHANDISE);
                startActivityForResult(intent, Constants.REQUEST_TAILOR_FROM_LIST);
            }
        });
        //Save Data Button
        saveData.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //If Tailor is not selected, then exit.
                if (!isTailorSelected){
                    Toast.makeText(getActivity(), "Select a tailor before saving", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Check if the Picture is loaded and act accordingly.
                String mercPic;
                if(isPictureLoaded){
                    mercPic=mercPicName;
                }else{
                    mercPic=Constants.EMPTY_STRING;
                }

                //Everything is fine, so Save the Data.
                ContentValues values = new ContentValues();
                values.put(MerchandisesContract.MERCHANDISE_DATE,mercDate.getText().toString());
                values.put(MerchandisesContract.MERCHANDISE_TAILOR_CITY,mercTailorCity.getText().toString());
                values.put(MerchandisesContract.MERCHANDISE_CARD_NUMBER,mercTailorCardNumber.getText().toString());
                values.put(MerchandisesContract.MERCHANDISE_ITEM_PIC,mercPic);
                values.put(MerchandisesContract.MERCHANDISE_ITEM_NAME,itemName);
                values.put(MerchandisesContract.MERCHANDISE_ITEM_LOC,Constants.EMPTY_STRING);
                values.put(MerchandisesContract.MERCHANDISE_SYNCABLE,Constants.DIRTY_NEW);
                if (!isItNewMerc){
                    //Edit the Merchandise
                    values.put(MerchandisesContract.MERCHANDISE_SYNCABLE,Constants.DIRTY_UPDATE);
                    getActivity().getContentResolver().update(
                            MasterSahabProvider.CONTENT_URI_MERCHANDISES.withAppendedPath(
                            MasterSahabProvider.CONTENT_URI_MERCHANDISES
                            , cursor.getString(0))
                            , values
                            ,null
                            ,null
                            );
                    getActivity().setResult(android.app.Activity.RESULT_OK, null);
                }else
                {
                    //Add the Merchandise
                    values.put(MerchandisesContract.MERCHANDISE_SYNCABLE,Constants.DIRTY_NEW);
                    getActivity().getContentResolver().insert(
                            MasterSahabProvider.CONTENT_URI_MERCHANDISES
                            , values
                            );
                    getActivity().setResult(android.app.Activity.RESULT_OK, null);
                }
                //Business is done, so get out
                getActivity().finish();
            }
        });
        //---------------------------------------
        cancelData.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                getActivity().setResult(android.app.Activity.RESULT_CANCELED, null);
                getActivity().finish();
            }
        });
        //----------------------------------
        mercImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!isTailorSelected){
                    return;
                }
                //TODO::Add code to select a picture or take a picture and process it accordingly.
                Intent i = new Intent(getActivity(), CameraActivity.class);
                i.putExtra(Constants.ORDER_BY,Constants.START_BY_MERCHANDISE);
                i.putExtra(MerchandisesContract.MERCHANDISE_CARD_NUMBER,mercTailorCardNumber.getText().toString());
                startActivityForResult(i, Constants.REQUEST_MERC_PICTURE);
            }
        });
        //------------Disable Image Button if Camera is not available---------------------//
        // If camera is not available, disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD &&
                        Camera.getNumberOfCameras() > 0);
        if (!hasACamera)
        {
            mercImage.setEnabled(false);
        }
        //-------------------------------
        itemsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                itemId = id;
                itemName = itemsSpinner.getSelectedItem().toString();
                Log.d(Constants.APP_TAG,"Item name is : " + itemName);
                Log.d(Constants.APP_TAG, "Item id is : " + itemId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                //
            }
        });
        return rootView;
    }
    //----------------------------------setControls()-----------------------------//
    private void setControls(View rootView){
        mercDate = (TextView) rootView.findViewById(R.id.textView_mercDate);
        mercTailorCity = (TextView) rootView.findViewById(R.id.textView_mercTailorCity);
        mercTailorCardNumber = (TextView) rootView.findViewById(R.id.textView_mercTailorCardNumber);

        saveData = (Button) rootView.findViewById(R.id.button_mercDataSave);
        cancelData = (Button) rootView.findViewById(R.id.button_mercDataSaveCancel);
        mercImage = (ImageView) rootView.findViewById(R.id.imageView_itemMercPicture);
        selectTailor = (Button) rootView.findViewById(R.id.button_mercSelectTailor);
        itemsSpinner = (Spinner) rootView.findViewById(R.id.spinner_mercSelectItem);

        //Setup views initial values
        mercDate.setText(Helper.getStringFromDate(new Date()));
    }
    //---------------------setItemListValue()---------------------------------------//
    private void setItemListValue(String value){
        for (int i = 0; i < itemsSpinner.getCount(); i++)
        {
            if (value.equals(itemsSpinner.getItemAtPosition(i)))
            {
                itemsSpinner.setSelection(i);
                return;
            }
        }
    }
    //----------------------------------onActivityResult-----------------------------//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
        case Constants.REQUEST_TAILOR_FROM_LIST:
            if(resultCode == Activity.RESULT_OK && data !=null){
                Bundle extras = data.getExtras();
                String card = extras.getString("tailor_card",Constants.EMPTY_STRING);
                String city = extras.getString("tailor_city",Constants.EMPTY_STRING);
                mercTailorCity.setText(city);
                mercTailorCardNumber.setText(card);
                isTailorSelected=true;
            }
            if (resultCode == Activity.RESULT_CANCELED){
                isTailorSelected=false;
            }
            break;
        case Constants.REQUEST_MERC_PICTURE:
            if(resultCode == Activity.RESULT_OK && data !=null){
                // Create a new Photo object and attach it to the crime
                String filename = data
                        .getStringExtra(CameraFragment.EXTRA_PHOTO_FILENAME);
                if(filename != null){
                    File file = new File(filename);
                    if(file.exists()){
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        mercImage.setImageBitmap(bitmap);
                        mercPicName=file.toString();
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED){
                isTailorSelected=false;
            }
            break;
        }
    }
    //-------------------------onDestroyView()---------------------------//
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if(adapter !=null)
            adapter=null;
        if(cursor !=null)
            cursor=null;
    }
    //-------------------------onPause()----------------------------//
    @Override
    public void onPause()
    {
        if(camera !=null){
            camera.release();
            camera=null;
        }
        super.onPause();
    }
}
