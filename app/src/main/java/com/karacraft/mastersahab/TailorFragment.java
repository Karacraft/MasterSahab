package com.karacraft.mastersahab;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karacraft.data.TailorsContract;
import com.karacraft.security.MasterSahabProvider;
import com.karacraft.utils.Constants;
import com.karacraft.utils.Helper;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Nov 29 2015
 * File Name        TailorFragment.java
 * Comments
 */
public class TailorFragment extends Fragment
{

    public static final String EXTRA_TAILOR_ID      = "com.mastersahab.tailorfragment.id";
    public static final String EXTRA_TAILOR_CARD    = "com.mastersahab.tailorfragment.cardnumber";

    public static final int DIALOG_ORDER_STATUS     = 4;
    public static final int RESULT_LOAD_IMAGE       = 301;  //For Image Upload

    int mTailorId;
    String mTailorCard;
    Cursor cursor;
    Cursor cursorPoints;

    //For Alert Dialog Box
    FragmentManager fm=null;

    //Views
    ImageView imageViewTailorDetail;

    TextView textViewName;
    TextView textViewShop;
    TextView textViewCard;
    TextView textViewPoints;
    TextView textViewCity;
    TextView textViewContact;
    TextView textViewCNIC;
    TextView textViewAddress;

    MenuItem menuAddOrder;
    MenuItem menuViewMerc;
    MenuItem menuViewOrders;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle extras = getActivity().getIntent().getExtras();

        if(extras != null){
            mTailorId = extras.getInt(EXTRA_TAILOR_ID);
        }

        //Load the Data
        cursor = getActivity().getContentResolver().query(
                MasterSahabProvider.CONTENT_URI_TAILORS.withAppendedPath(
                        MasterSahabProvider.CONTENT_URI_TAILORS
                        , String.valueOf(mTailorId))
                , TailorsContract.PROJECTION
                , null
                , null
                , null);

        if(cursor != null){
            if(cursor.moveToFirst()){
                mTailorCard=cursor.getString(1);
            }
        }

        String[] projection = {"SUM(suit_discounted_price / 50)"};
        String selection = "card_number=?";     //"suit_discounted_price=?";
        String[] selectArgs = {mTailorCard};
        cursorPoints = getActivity().getContentResolver().query(
                MasterSahabProvider.CONTENT_URI_ORDERS
                ,projection
                ,selection  //OrdersContract.ORDER_CARD_NUMBER +"=" + String.valueOf(mTailorId)
                ,selectArgs //Filter
                ,null);
    }
    //----------------------------------onCreateView()-----------------------------//
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_tailor, container, false);
        //set Controls
        setupControls(rootView);
        //set Title
        getActivity().setTitle("Tailor Details");
        //Set Menus
        setHasOptionsMenu(true);
        //Set FragmentManager for Dialog Box
        fm = getFragmentManager();

        if (cursor != null){
            cursor.moveToFirst();
            textViewName.setText(cursor.getString(3));
            textViewShop.setText(cursor.getString(4));
            textViewCard.setText(cursor.getString(1));
            textViewCity.setText(cursor.getString(9));
            textViewContact.setText(cursor.getString(6));
            textViewCNIC.setText(cursor.getString(5));
            textViewAddress.setText(cursor.getString(8));
        }

        if (cursorPoints != null)
        {
           if(cursorPoints.moveToFirst()){
                textViewPoints.setText(cursorPoints.getString(0));
           }
        }
        //---------------------------ImageView-----------------------------------------//
        imageViewTailorDetail = (ImageView)rootView.findViewById(R.id.imageView_tailorDetail);
        imageViewTailorDetail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Use Gallery to add up Images
                Intent mImageFromGallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(mImageFromGallery,RESULT_LOAD_IMAGE);
            }
        });

        return rootView;
    }
    //---------------------------setupControls()-------------------------------//
    private void setupControls(View rootView){
        //Populate Views
        textViewName = (TextView)rootView.findViewById(R.id.textView_tailorDetailName);
        textViewShop = (TextView)rootView.findViewById(R.id.textView_tailorDetailShop);
        textViewCard = (TextView)rootView.findViewById(R.id.textView_tailorDetailCard);
        textViewPoints = (TextView)rootView.findViewById(R.id.textView_tailorDetailPoints);
        textViewCity = (TextView)rootView.findViewById(R.id.textView_tailorDetailCity);
        textViewContact = (TextView)rootView.findViewById(R.id.textView_tailorDetailContact);
        textViewCNIC = (TextView)rootView.findViewById(R.id.textView_tailorDetailCnic);
        textViewAddress = (TextView)rootView.findViewById(R.id.textView_tailorDetailAddress);
    }
    //----------------------------------onDestroy()-----------------------------//
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(cursor !=null)
            cursor=null;
        if(cursorPoints != null)
            cursorPoints = null;
    }
    //----------------------------------onCreateOptionsMenu()-----------------------------//
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_tailor_details, menu);

        menuAddOrder = menu.findItem(R.id.menu_add_tailor_order);
//        menuAddMerc = menu.findItem(R.id.menu_add_tailor_merchandise);
        menuViewMerc = menu.findItem(R.id.menu_view_tailor_merchandise);
        menuViewOrders = menu.findItem(R.id.menu_view_tailor_orders);
        Log.d(Constants.APP_TAG, "TailorFragment -> onCreateOptionsMenu : Menu Created)");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;
        switch (item.getItemId()){
            case R.id.menu_add_tailor_order:
                intent = new Intent(getActivity(),OrderAddEditActivity.class);
                intent.putExtra(Constants.ORDER_BY, Constants.START_BY_TAILOR);
                intent.putExtra(Constants.ORDER_TYPE,OrderAddEditFragment.ADD_ORDER);
                intent.putExtra(EXTRA_TAILOR_ID,mTailorId);
                intent.putExtra(EXTRA_TAILOR_CARD,mTailorCard);
                startActivityForResult(intent,Constants.REQUEST_ORDER_ADD);
                break;
            case R.id.menu_view_tailor_merchandise:
                intent = new Intent(getActivity(),MercListActivity.class);
                intent.putExtra(Constants.ORDER_BY,Constants.START_BY_TAILOR);
                intent.putExtra(Constants.ORDER_TYPE,MercListFragment.VIEW_MERCHANDISE_LIST);
                intent.putExtra(EXTRA_TAILOR_ID,mTailorId);
                intent.putExtra(EXTRA_TAILOR_CARD,mTailorCard);
                startActivity(intent);
                break;
            case R.id.menu_view_tailor_orders:
                CustomAlertDialogOptionsFragment cdof = CustomAlertDialogOptionsFragment.newInstance("Select Order Status");
                cdof.setTargetFragment(TailorFragment.this,DIALOG_ORDER_STATUS);
                cdof.show(fm,"Order Status Fragment");
                //Here we are presenting a choice list, if use selects and option, we go with it.

//                intent = new Intent(getActivity(), OrdersListActivity.class);
//                intent.putExtra(Constants.ORDER_BY,Constants.START_BY_TAILOR);
//                intent.putExtra(Constants.ORDER_TYPE,Constants.REQUEST_PENDING_ORDERS);
//                intent.putExtra(EXTRA_TAILOR_ID,mTailorId);
//                intent.putExtra(EXTRA_TAILOR_CARD,mTailorCard);
//                startActivity(intent);
                break;
        }
        return true;
    }
    //----------------------------------onActivtyResult()-----------------------------//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (resultCode)
        {
            //This correspond to the Image Loaded via Gallery
            case Activity.RESULT_OK:
                if (requestCode == RESULT_LOAD_IMAGE  && data !=null)
                    {
                        //If we have data, Get the Bitmap
                        Bitmap mBitmap = Helper.loadImageFromGallery(getActivity(), data);
                        //Save the Bitmap to Directory
//                        String info[]= Helper.uploadImageToRest(getActivity(), mBitmap, Helper.PICTURE_TAILOR, mTailor.getTailorCardNumber());
//                        imageViewTailorDetail.setImageBitmap(Helper.loadImageFromGallery(getActivity(), data));
//                        UploadImage mUploadImage = new UploadImage(getActivity(),info[1]);
//                        mUploadImage.execute();
                    }

                if (requestCode == Constants.REQUEST_MERCHANDISE_ADD)
                {
                    Log.d(Constants.APP_TAG,"TailorFragment -> onActivityResult -> Merchandise Added by Tailor");
                }

                if(requestCode == Constants.REQUEST_ORDER_ADD){
                    Log.d(Constants.APP_TAG,"TailorFragment -> onActivityResult -> Order Added by Tailor");
                }

                if(requestCode == DIALOG_ORDER_STATUS){
                    //This won't compile with release built
                    if (BuildConfig.DEBUG)
                    {
                        Log.d(Constants.APP_TAG, "TailorFragment->onActivityResult: We Wer here");
                    }

                    boolean status = data.getBooleanExtra(Constants.KEY,true);
                    int selection = data.getIntExtra("selection",0);
                    Intent intent=null;
                    if(status)
                    {
                        switch (selection)
                        {
                            case 1:
                                Log.d(Constants.APP_TAG, "Dialog selected Pending");
                                intent = new Intent(getActivity(), OrdersListActivity.class);
                                intent.putExtra(Constants.ORDER_BY, Constants.START_BY_TAILOR);
                                intent.putExtra(Constants.ORDER_TYPE, OrdersListFragment.PENDING_ORDERS);
                                intent.putExtra(EXTRA_TAILOR_CARD,mTailorCard);
                                startActivity(intent);
                                break;
                            case 2:
                                Log.d(Constants.APP_TAG, "Dialog selected Archive");
                                intent = new Intent(getActivity(), OrdersListActivity.class);
                                intent.putExtra(Constants.ORDER_BY, Constants.START_BY_TAILOR);
                                intent.putExtra(Constants.ORDER_TYPE, OrdersListFragment.ARCHIVE_ORDERS);
                                intent.putExtra(EXTRA_TAILOR_CARD,mTailorCard);
                                startActivity(intent);
                                break;
                            case 3:
                                Log.d(Constants.APP_TAG, "Dialog selected Rejected");
                                intent = new Intent(getActivity(), OrdersListActivity.class);
                                intent.putExtra(Constants.ORDER_BY, Constants.START_BY_TAILOR);
                                intent.putExtra(Constants.ORDER_TYPE, OrdersListFragment.REJECTED_ORDERS);
                                intent.putExtra(EXTRA_TAILOR_CARD,mTailorCard);
                                startActivity(intent);
                                break;
                        }
                    }
                }
                break;

            case Activity.RESULT_CANCELED:
                    Log.d(Constants.APP_TAG,"TailorFragment -> onActivityResult -> Result is Cancelled.");
                break;
        }
    }
}