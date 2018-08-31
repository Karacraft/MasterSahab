package com.karacraft.mastersahab;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.karacraft.data.OrdersContract;
import com.karacraft.security.MasterSahabProvider;
import com.karacraft.utils.Constants;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Nov 22 2015
 * File Name        OrdersListFragment.java
 * Comments
 */
public class OrdersListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{

    public static final String PENDING_ORDERS   = "Pending";
    public static final String ARCHIVE_ORDERS   = "Archive";
    public static final String REJECTED_ORDERS  = "Rejected";

    SimpleCursorAdapter     adapter;
    ListView                listView;
    EditText                textFilter;
    String                  curFilter;
    CursorLoader            cursorLoader=null;
    String                  selection;
    String[]                selectArgs;

    //TODO::Correct The projection to show Merchandizer Name

    TextView                tv_status;
    MenuItem                menuAddOrder;
    Boolean                 contextOrderSelected =false;
    Boolean                 showMenu =false;
    Boolean                 showContextActionMenu = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_orders_list,container,false);
        //Setup Controls
        setupControls(rootView);
        //Set Title
        getActivity().setTitle(R.string.orders_list_activity);
        //set Parent Activity
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        /** Bundle Extras are different the savedInstanceState */
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null)
        {
            String orderBy= extras.getString(Constants.ORDER_BY);
            //This should be Pending, Archive, Rejected
            String orderType = extras.getString(Constants.ORDER_TYPE);
            /** Setup Menu & Context Menu as per Requirement */
            if(orderBy.equals(Constants.START_BY_TAILOR))
            {
                Log.d(Constants.APP_TAG," Ordered by Tailor");
                String cardNumber = extras.getString(TailorFragment.EXTRA_TAILOR_CARD);
                selectArgs = new String[]{orderType, cardNumber};
                selection = "suit_status=? AND card_number=?";
                if (orderType.equals("Archive") || orderType.equals("Rejected")){
                    showMenu=false;
                    showContextActionMenu=false;
                }else{
                    showMenu=true;
                    showContextActionMenu=true;
                }
            }

            if (orderBy.equals(Constants.START_BY_DASHBOARD))
            {
                Log.d(Constants.APP_TAG," Ordered by Dash");
                selectArgs = new String[]{orderType};
                selection = "suit_status=?";
                if (orderType.equals("Archive") || orderType.equals("Rejected")){
                    showMenu=false;
                    showContextActionMenu=false;
                }else{
                    showMenu=true;
                    showContextActionMenu=true;
                }

            }
        }

        //Set Menu Options
        setHasOptionsMenu(showMenu);
        //setup Action Menu
        if(showContextActionMenu){
            setupActionMenu();
        }

        /** Load Cursor */
        String[] uiBoundFrom = {
                OrdersContract.ORDER_ID
                ,OrdersContract.ORDER_STAFF_CITY
                ,OrdersContract.ORDER_CARD_NUMBER
                ,OrdersContract.ORDER_SUIT_STATUS
                ,OrdersContract.ORDER_DATE
                ,OrdersContract.ORDER_STAFF_ID
                ,OrdersContract.ORDER_SUIT_CODE
        };

        int[] uiBoundTo = {
                R.id.textView_listOrderDetailId
                ,R.id.tv_listOrderDetailCity
                ,R.id.textView_listOrderDetailCardNumber
                ,R.id.textView_listOrderDetailSuitStatus
                ,R.id.textView_listOrderDetailDate
                ,R.id.textView_listOrderDetailStaff
                ,R.id.textView_listOrderDetailSuitCode
        };
        //Setup Adapter to the Required Data
        adapter = new SimpleCursorAdapter(getActivity()
                ,R.layout.list_item_orders
                ,null
                ,uiBoundFrom
                ,uiBoundTo
                ,0
        );


        //Note the usage of android.R.id.list - it is required in fragment
        listView.setAdapter(adapter);
        listView.setEmptyView(rootView.findViewById(android.R.id.empty));
        getLoaderManager().initLoader(1, null, this);

        //Setup Filter
        textFilter.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                curFilter = s.toString();
                adapter.getFilter().filter(s);
                getLoaderManager().restartLoader(2, null, OrdersListFragment.this);
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        return rootView;
    }
    //-------------------------setupControls()--------------------------------//
    private void setupControls(View rootView){
        listView = (ListView) rootView.findViewById(android.R.id.list);
        textFilter = (EditText) rootView.findViewById(R.id.editText_ordersListSearchFilter);
        tv_status  = (TextView) rootView.findViewById(R.id.textView_listOrderDetailSuitStatus);
    }
    //-----------------setupActionMenu()----------------------//
    private void setupActionMenu(){
        /** Action Menu - Long press to get the Menu of Edit / Delete */
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
            // Use floating context menus on Froyo and Gingerbread
            registerForContextMenu(listView);
        }else{
            // Use contextual action bar on Honeycomb and higher
            listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
            {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
                {
                    // Required, but not used in this implementation
                    //This won't compile with release built
                    if(!contextOrderSelected){
                        mode.setTitle(listView.getCheckedItemCount() + " item Selected");
                        contextOrderSelected = true;
                    } else{
                        return;
                    }
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu)
                {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.context_menu_fragment_orderslist, menu);
                    mode.setTitle("Select Order");
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu)
                {
                    // Required, but not used in this implementation
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item)
                {
                    Log.d(Constants.APP_TAG,"Clicked");
                    switch (item.getItemId()) {
                        case R.id.menu_editOrder:
                            editSelectedOrder();
                            return true;
                        case R.id.menu_deleteOrder:
                            deleteSelectedOrder();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode)
                {
                    ///
                    contextOrderSelected =false;
                }
            });
        }
    }
    //-------------------editSelectedOrder()----------------------------//
    private void editSelectedOrder(){
        long[] ids = listView.getCheckedItemIds();
        long myId = 0;
        for (long id : ids){
            myId = id;
        }
        //Start an Intent to Edit the data
        Intent intent = new Intent(getActivity(),OrderAddEditActivity.class);
        intent.putExtra(Constants.ORDER_BY,Constants.START_BY_ORDER_LIST);
        intent.putExtra(Constants.ORDER_TYPE,OrderAddEditFragment.EDIT_ORDER);
        intent.putExtra("id", String.valueOf(myId));
        startActivityForResult(intent, Constants.REQUEST_ORDER_EDIT);
        //TODO::if this isn't working correctly then comment out the below line
        //getActivity().finish();
    }
    //-------------------deleteSelectedOrder()------------------------//
    private void deleteSelectedOrder(){
        long[] ids = listView.getCheckedItemIds();
        long myId = 0;
        for (long id : ids){
            myId = id;
        }
        getActivity().getContentResolver().delete(MasterSahabProvider.CONTENT_URI_ORDERS, "_id=?", new String[]{String.valueOf(myId)});
        Log.d(Constants.APP_TAG, "Order Deleting " + myId);
    }
    //----------------------------------onCreateOptionsMenu()-----------------------------//
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_fragment_orderlist, menu);
        menuAddOrder = menu.findItem(R.id.menu_addOrder);
        super.onCreateOptionsMenu(menu, inflater);
    }
    //----------------------------------onOptionsItemSelected()-----------------------------//
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /** Add New Order here */
        Intent intent = new Intent(getActivity(),OrderAddEditActivity.class);
        intent.putExtra(Constants.ORDER_BY,Constants.START_BY_ORDER_LIST);
        intent.putExtra(Constants.ORDER_TYPE,OrderAddEditFragment.ADD_ORDER);
        startActivity(intent);
        //return super.onOptionsItemSelected(item);
        return true;
    }
    //----------------------------------onDestroyView()-----------------------------//
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if(adapter !=null)
            adapter=null;
        if(cursorLoader !=null)
            cursorLoader=null;
    }
    //----------------------------------onListItemClick()-----------------------------//
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        //Use ID to show Data
        Intent intent = new Intent(getActivity(),OrderActivity.class);
        /** Here the we are using TailorFragment EXTRA_TAILOR_ID static
         *  as argument. and passing the list item's id to TailorActivity.
         *  Which will utilize it show exact data.
         */
        intent.putExtra(OrderFragment.EXTRA_ORDER_ID, (int) id);
        intent.putExtra(Constants.ORDER_BY,Constants.START_BY_ORDER_LIST);
        intent.putExtra(Constants.ORDER_TYPE,OrderFragment.VIEW_ORDER);
        startActivity(intent);
    }
    //----------------------------------onCreateLoader()-----------------------------//
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        if(id == 1){
            cursorLoader=new CursorLoader(getActivity(),MasterSahabProvider.CONTENT_URI_ORDERS
                    , OrdersContract.PROJECTION
                    , selection
                    , selectArgs
                    , null
            );

        }
        if(id == 2){
            if(curFilter != null){
                cursorLoader=  new CursorLoader(getActivity()
                        ,MasterSahabProvider.CONTENT_URI_ORDERS
                        ,OrdersContract.PROJECTION
                        ,OrdersContract.ORDER_CARD_NUMBER + " LIKE '%"
                        + curFilter + "%'"
                        ,null
                        ,null
                );
            }
        }
        return cursorLoader;
    }
    //----------------------------------onLoadFinished()-----------------------------//
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if(adapter!=null && data !=null)
            adapter.swapCursor(data); //swap the new cursor in
        else
            Log.d(Constants.APP_TAG, "Orders Cursor is null");
    }
    //----------------------------------onLoaderReset()-----------------------------//
    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

        if(adapter != null)
            adapter.swapCursor(null);
        else
            Log.d(Constants.APP_TAG, "Orders adapter is null");
    }
}
