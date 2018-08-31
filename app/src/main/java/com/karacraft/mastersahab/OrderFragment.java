package com.karacraft.mastersahab;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.karacraft.security.MasterSahabProvider;
import com.karacraft.utils.Constants;
import com.karacraft.utils.Helper;

import java.util.Date;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Dec 05 2015
 * File Name        OrderFragment.java
 * Comments
 */
public class OrderFragment extends Fragment
{
    public static final String EXTRA_ORDER_ID   = "com.karacraft.mastersahab.orderfragment.id";
    public static final String VIEW_ORDER       = "view_order";

    Cursor   cursor;
    TextView mOrderDate;
    TextView mCardNumber;
    TextView mSuitQty;
    TextView mSuitCode;
    TextView mCity;
    TextView mPrice;
    TextView mSuitColor;
    TextView mOrderStatus;
    TextView mOrderStatusChangeDate;
    TextView mOrderSuitDisPrice;
    TextView mOrderReceipt;
    TextView mCatalog;

    boolean isItNewOrder=false;
    int     mOrderId;
    String  orderBy;
    String  orderType;

    //----------------------------------onCreateView()-----------------------------//
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_order_detail,container,false);
        //Setup Controls
        setupControls(rootView);
        //Set Title
        getActivity().setTitle(R.string.order_detail_fragment);
        /** Get The Bundle Extras - This is different from savedStateInstance */
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null){
            orderBy = extras.getString(Constants.ORDER_BY);
            orderType = extras.getString(Constants.ORDER_TYPE);
            switch (orderBy){
                case Constants.START_BY_TAILOR:
                        isItNewOrder=true;
                        mOrderDate.setText(Helper.getStringFromDate(new Date()));
                        mCardNumber.setText("2224343");
                        //Setup Way to get Tailor Card Number
                        //Setup Way to get Suit Qty
                        //Get Suit Code
                    break;
                case Constants.START_BY_ORDER_LIST:
                    if (orderType.equals(VIEW_ORDER)){
                        //This is an order that required Edit
                        mOrderId = extras.getInt(EXTRA_ORDER_ID);
                        isItNewOrder=false;
                        cursor = getActivity().getContentResolver().query(
                                MasterSahabProvider.CONTENT_URI_ORDERS.withAppendedPath(
                                        MasterSahabProvider.CONTENT_URI_ORDERS
                                        , String.valueOf(mOrderId))
                                ,null
                                ,null
                                ,null
                                ,null);
                        assignDataToControls(cursor);
                    }
                    break;
            }//switch Case
        }
        return rootView;
    }
    //-----------------------assignDataToControls()-----------------//
    private void assignDataToControls(Cursor cursor){
        //--------------Assign data---------------------//
        if (cursor !=null)
        {
            if(cursor.moveToFirst()){
                mOrderDate.setText(cursor.getString(1));
                mCardNumber.setText(cursor.getString(2));
                mSuitQty.setText(cursor.getString(4));
                mSuitCode.setText(cursor.getString(5));
                mCity.setText(cursor.getString(6));
                mPrice.setText(cursor.getString(7));
                mSuitColor.setText(cursor.getString(8));
                mOrderStatus.setText(cursor.getString(9));
                mOrderStatusChangeDate.setText(cursor.getString(10));
                mOrderSuitDisPrice.setText(cursor.getString(11));
                mOrderReceipt.setText(cursor.getString(13));
                mCatalog.setText(cursor.getString(14));
            }
        }
    }
    //------------------------setupControls()------------------------------//
    private void setupControls(View rootView){
        //Create Views Data
        mOrderDate = (TextView) rootView.findViewById(R.id.textView_orderDetailPlacementDate);
        mCardNumber = (TextView) rootView.findViewById(R.id.textView_listOrderDetailCardNumber);
        mSuitQty = (TextView) rootView.findViewById(R.id.editView_orderDetailAddEditSuitQty);
        mSuitCode = (TextView) rootView.findViewById(R.id.textView_listOrderDetailSuitCode);
        mCity = (TextView)rootView.findViewById(R.id.textView_orderDetailCity);
        mPrice = (TextView)rootView.findViewById(R.id.textView_orderDetailSuitPrice);
        mSuitColor = (TextView) rootView.findViewById(R.id.textView_orderDetailSuitColor);
        mOrderStatus = (TextView) rootView.findViewById(R.id.textView_listOrderDetailSuitStatus);
        mOrderStatusChangeDate = (TextView) rootView.findViewById(R.id.textView_orderDetailStatusChangeDate);
        mOrderSuitDisPrice = (TextView) rootView.findViewById(R.id.textView_orderDetailDiscPrice);
        mOrderReceipt = (TextView) rootView.findViewById(R.id.textView_orderDetailReceipt);
        mCatalog = (TextView) rootView.findViewById(R.id.textView_orderDetailCatalog);
    }
    //----------------------------------overrides-----------------------------//
    //----------------------------------onDestroyView()-----------------------------//
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if(cursor !=null)
            cursor=null;
    }
}
