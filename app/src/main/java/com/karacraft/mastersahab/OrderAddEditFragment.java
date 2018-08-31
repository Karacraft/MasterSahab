package com.karacraft.mastersahab;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.karacraft.data.OrdersContract;
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
public class OrderAddEditFragment extends Fragment
{
    public static final String EXTRA_ORDER_ADD_EDIT_ID   = "com.karacraft.mastersahab.orderaddeditfragment.id";
    public static final String EDIT_ORDER       = "edit_order";
    public static final String ADD_ORDER        = "add_order";

    Cursor   cursor;
    TextView orderDate;
    TextView cardNumber;
    Spinner suitQtySpinner;
    TextView suitCode;
    TextView tailorCity;
    EditText suitPrice;
    TextView suitColor;
    Spinner orderStatusSpinner;
    TextView orderStatusChangeDate;
    EditText suitDiscPrice;
    EditText receipt;
    TextView catalog;
    Button orderCancel;
    Button orderSave;

    String suitPriceTemp = Constants.EMPTY_STRING;      //For Multiplicaiton

    int     tailorId;
    String  tailorCardNumber;
    String  orderBy;
    String  orderType;

    Boolean isItEdit=false;

    ArrayAdapter<CharSequence> suitQtyAdapter=null;
    ArrayAdapter<CharSequence> orderStatusAdapter=null;

    //----------------------------------onCreateView()-----------------------------//
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_order_detail_add_edit,container,false);
        //Setup Controls
        setupControls(rootView);
        //Set Title
        getActivity().setTitle(R.string.order_detail_fragment);
        /** Get The Bundle Extras - This is different from savedStateInstance */
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null){
            orderBy = extras.getString(Constants.ORDER_BY);
            orderType = extras.getString(Constants.ORDER_TYPE);
            switch (orderType){
                case ADD_ORDER:
                    isItEdit=false;
                    //Set The Dates
                    orderDate.setText(Helper.getStringFromDate(new Date()));
                    orderStatusChangeDate.setText(orderDate.getText().toString());
                    if (orderBy.equals(Constants.START_BY_ORDER_LIST)){
                        //Order list wants to add up new order, so let the user select tailor from the list
                    }
                    if (orderBy.equals(Constants.START_BY_TAILOR)){
                        //Order is from Tailor, so get the Tailor Card Number
                        cardNumber.setText(extras.getString(TailorFragment.EXTRA_TAILOR_CARD));
                        tailorCardNumber= extras.getString(TailorFragment.EXTRA_TAILOR_CARD);
                        tailorId=extras.getInt(TailorFragment.EXTRA_TAILOR_ID);
                    }
                    suitQtySpinner.setEnabled(false);
                    break;
                case EDIT_ORDER:
                    isItEdit=true;
                    cursor = getActivity().getContentResolver().query(
                            MasterSahabProvider.CONTENT_URI_ORDERS.withAppendedPath(
                                    MasterSahabProvider.CONTENT_URI_ORDERS
                                    ,String.valueOf(extras.getString("id"))
                            )
                            , OrdersContract.PROJECTION
                            ,null
                            ,null
                            ,null
                    );
                    orderStatusChangeDate.setText(Helper.getStringFromDate(new Date()));
                    //Bind Data to Controls
                    assignDataToControls(cursor);
                    suitQtySpinner.setEnabled(true);

                    if (orderBy.equals(Constants.START_BY_ORDER_LIST)){
                        //Order list wants to edit the Order, So allow it

                    }
                    if (orderBy.equals(Constants.START_BY_TAILOR)){
                        //Tailor wants to edit Order, so do the appropriate thing

                    }
                    break;
            }

        }

        //-----------------------------------
        suitQtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(suitPrice.getText().toString().equals("Price"))
                {
                    suitPrice.setText(String.valueOf(0));
                    return;
                }

                if(parent.isEnabled()){
                    String value= (String) parent.getItemAtPosition(position);
                    int value2 = Integer.parseInt(suitPrice.getText().toString()) * Integer.parseInt(value);
                    suitPrice.setText(String.valueOf(value2));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        //-------------------------------------------
        orderCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().finish();
            }
        });
        //--------------------------------------------
        orderSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                //Check if Card Number or Suit is Selected
                if(cardNumber.getText().toString().equals(Constants.EMPTY_STRING) || suitCode.getText().toString().equals(Constants.EMPTY_STRING)){
                    Toast.makeText(getActivity(), "Please Select Card Number & Suit Code to proceed", Toast.LENGTH_SHORT).show();
                    return;
                }
                String id=Constants.EMPTY_STRING;
                SharedPreferences prefs = getActivity().getSharedPreferences(Constants.APP_PREF_KEY, Context.MODE_PRIVATE);
                if (prefs.contains(Constants.PREF_USER_ID_KEY)){
                    id = prefs.getString(Constants.PREF_USER_ID_KEY, "");
                    Log.d(Constants.APP_TAG,"KEy is : " + id);
                }
                //TODO:: ADD COde to save Order
                ContentValues values = new ContentValues();
                values.put(OrdersContract.ORDER_DATE,orderDate.getText().toString());
                values.put(OrdersContract.ORDER_CARD_NUMBER,cardNumber.getText().toString());
                values.put(OrdersContract.ORDER_STAFF_ID,Integer.parseInt(id));
                values.put(OrdersContract.ORDER_QUANTITY,suitQtySpinner.getSelectedItemPosition() + 1);
                values.put(OrdersContract.ORDER_SUIT_CODE,suitCode.getText().toString());
                values.put(OrdersContract.ORDER_STAFF_CITY,tailorCity.getText().toString());
                values.put(OrdersContract.ORDER_SUIT_PRICE,Integer.parseInt(suitPrice.getText().toString()));
                values.put(OrdersContract.ORDER_SUIT_COLOR,suitColor.getText().toString());
                switch (orderStatusSpinner.getSelectedItemPosition()+1){
                    case 1:
                        values.put(OrdersContract.ORDER_SUIT_STATUS,"Pending");
                        values.put(OrdersContract.ORDER_REDEEM,1);
                        break;
                    case 2:
                        values.put(OrdersContract.ORDER_SUIT_STATUS,"Archive");
                        values.put(OrdersContract.ORDER_REDEEM,1);
                        break;
                    case 3:
                        values.put(OrdersContract.ORDER_SUIT_STATUS,"Rejected");
                        values.put(OrdersContract.ORDER_REDEEM,0);
                        break;
                }
                values.put(OrdersContract.ORDER_STATUS_CHANGE_DATE,orderStatusChangeDate.getText().toString());
                if(suitDiscPrice.getText().toString().equals(Constants.EMPTY_STRING)){
                    values.put(OrdersContract.ORDER_SUIT_DISCOUNTED_PRICE,0);
                }else{
                    values.put(OrdersContract.ORDER_SUIT_DISCOUNTED_PRICE,Integer.parseInt(suitDiscPrice.getText().toString()));
                }
                values.put(OrdersContract.ORDER_RECEIPT,receipt.getText().toString());
                values.put(OrdersContract.ORDER_SUIT_CATALOUGE,catalog.getText().toString());
                if(isItEdit){
                    //It is an edit
                    getActivity().getContentResolver().update(
                            MasterSahabProvider.CONTENT_URI_ORDERS.withAppendedPath(
                                    MasterSahabProvider.CONTENT_URI_ORDERS
                                    , cursor.getString(0))
                            , values
                            , null
                            , null);
                    Log.d(Constants.APP_TAG, "Edited");
                    getActivity().finish();
                }else{
                    //It is new
                    getActivity().getContentResolver().insert(MasterSahabProvider.CONTENT_URI_ORDERS, values);
                    Log.d(Constants.APP_TAG, "Added");
                    getActivity().finish();
                }
            }
        });
        return rootView;
    }
    //-----------------------assignDataToControls()-----------------//
    private void assignDataToControls(Cursor cursor){
        String qty=Constants.EMPTY_STRING;
        String status;
        if (cursor !=null)
        {
            if(cursor.moveToFirst()){
                orderDate.setText(cursor.getString(1));
                cardNumber.setText(cursor.getString(2));
                qty=cursor.getString(4);
                suitCode.setText(cursor.getString(5));
                tailorCity.setText(cursor.getString(6));
                suitPrice.setText(cursor.getString(7));
                suitColor.setText(cursor.getString(8));
                status = cursor.getString(9);
                //orderStatusChangeDate.setText(cursor.getString(10));
                suitDiscPrice.setText(cursor.getString(11));
                receipt.setText(cursor.getString(13));
                catalog.setText(cursor.getString(14));
            }
        }

        //Setup Suit Qunatity as per Order Row Data
        for (int i = 0; i < suitQtySpinner.getCount(); i++)
        {
            String value = (String) suitQtySpinner.getItemAtPosition(i);
            if (value.equals(qty));
            suitQtySpinner.setSelection(i);
            return;
        }
        //Setup Suit Status as per Order Row Data
        for (int i = 0; i < orderStatusSpinner.getCount(); i++)
        {
            String value = (String) orderStatusSpinner.getItemAtPosition(i);
            if (value.equals(qty));
            orderStatusSpinner.setSelection(i);
            return;
        }

    }
    //------------------------setupControls()------------------------------//
    private void setupControls(View rootView){
        //Create Views Data
        orderDate = (TextView) rootView.findViewById(R.id.textView_orderDetailAddEditPlacementDate);
        cardNumber = (TextView) rootView.findViewById(R.id.textView_orderDetailAddEditCardNumber);
        suitQtySpinner = (Spinner) rootView.findViewById(R.id.spinner_orderDetailAddEditSuitQty);
        suitCode = (TextView) rootView.findViewById(R.id.textView_orderDetailAddEditSuitCode);
        tailorCity = (TextView)rootView.findViewById(R.id.textView_orderDetailAddEditCity);
        suitPrice = (EditText)rootView.findViewById(R.id.editText_orderDetailAddEditSuitPrice);
        suitColor = (TextView) rootView.findViewById(R.id.textView_orderDetailAddEditSuitColor);
        orderStatusSpinner = (Spinner) rootView.findViewById(R.id.spinner_orderDetailAddEditSuitStatus);
        orderStatusChangeDate = (TextView) rootView.findViewById(R.id.textView_orderDetailAddEditStatusChangeDate);
        suitDiscPrice = (EditText) rootView.findViewById(R.id.editText_orderDetailAddEditDiscPrice);
        receipt = (EditText) rootView.findViewById(R.id.editText_orderDetailAddEditReceipt);
        catalog = (TextView) rootView.findViewById(R.id.textView_orderDetailAddEditCatalog);

        orderSave = (Button) rootView.findViewById(R.id.button_orderDetailSaveData);
        orderCancel = (Button) rootView.findViewById(R.id.button_orderDetailCancelData);

        receipt.setText(" ");
        suitDiscPrice.setText(String.valueOf(0));
        suitPrice.setText(String.valueOf(0));
        suitPrice.setEnabled(false);    //Only enable, if Selected suit is Other
        suitColor.setText(" ");
        catalog.setText(" ");

        /** Set up Suit Qty Adapter */
         //Create an ArrayAdapter using the string array and a default spinner layout
        suitQtyAdapter = ArrayAdapter.createFromResource(getActivity()
                ,R.array.suit_qty
                , android.R.layout.simple_spinner_item
                );
        // Specify the layout to use when the list of choices appears
        suitQtySpinner.setEnabled(false);
        suitQtyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suitQtySpinner.setAdapter(suitQtyAdapter);


        /** Set up Order Status Adapter */
        orderStatusAdapter = ArrayAdapter.createFromResource(
                getActivity()
                ,R.array.order_status
                ,android.R.layout.simple_spinner_item);
        orderStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderStatusSpinner.setAdapter(orderStatusAdapter);

        /** Card Number is clickable */
        cardNumber.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(),TailorsListActivity.class);
                intent.putExtra(Constants.ORDER_BY,Constants.START_BY_ORDER_ADD_EDIT);
                startActivityForResult(intent,Constants.REQUEST_TAILOR_FROM_LIST);
            }
        });

        /** Suit Code is clickable */
        suitCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(),SuitsListActivity.class);
                intent.putExtra(Constants.ORDER_BY,Constants.START_BY_ORDER_ADD_EDIT);
                startActivityForResult(intent,Constants.REQUEST_SUIT);
            }
        });

    }
    //----------------------------------onDestroyView()-----------------------------//
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if(cursor !=null)
            cursor=null;
    }
    //----------------------------onActivityResult()------------------------//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constants.REQUEST_TAILOR_FROM_LIST:
                if (resultCode == Activity.RESULT_OK && data !=null){
                    Bundle extras = data.getExtras();
                    cardNumber.setText(extras.getString("tailor_card",Constants.EMPTY_STRING));
                    tailorCity.setText(extras.getString("tailor_city", Constants.EMPTY_STRING));
                }
                break;
            case Constants.REQUEST_SUIT:
                if(resultCode == Activity.RESULT_OK && data != null){
                    Bundle extras = data.getExtras();
                    suitCode.setText(extras.getString("suit_code",Constants.EMPTY_STRING));
                    suitColor.setText(extras.getString("suit_color",Constants.EMPTY_STRING));
                    catalog.setText(extras.getString("suit_catalog",Constants.EMPTY_STRING));
                    suitPrice.setText(extras.getString("suit_price", Constants.EMPTY_STRING));
                    if(suitCode.getText().toString().equals("OTHERCOL")){
                        suitPrice.setEnabled(true);
                    }else{
                        suitPrice.setEnabled(false);
                    }
                }
                suitQtySpinner.setEnabled(true);
                break;
        }
    }
}
