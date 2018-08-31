package com.karacraft.mastersahab;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.karacraft.data.TailorsContract;
import com.karacraft.security.MasterSahabProvider;
import com.karacraft.utils.Constants;


/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Nov 22 2015
 * File Name        TailorsListFragment.java
 * Comments
 */
public class TailorsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    SimpleCursorAdapter adapter;
    ListView listView;
    EditText textFilter;
    TextView textCardNumber;
    String curFilter;
    CursorLoader cursorLoader=null;
    Cursor cursor=null;

    //Flag to disable List Selection
    boolean disableTailorDetails = false;

    //For Cursor Loader
    String[] uiBindFrom;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_tailors_list,container,false);

        listView = (ListView) rootView.findViewById(android.R.id.list);
        textFilter = (EditText) rootView.findViewById(R.id.editText_tailorsListSearchFilter);
        textCardNumber = (TextView) rootView.findViewById(R.id.textView_listTailorCardNumber);
        //---------------------------Do Basic Stuff----------------------------------------//
        //Set Title
        getActivity().setTitle(R.string.tailors_list_activity);
        //set Parent Activity
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        /** Extras are different from savedInstance bundle **/
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras !=null){
            String orderBy =extras.getString(Constants.ORDER_BY);
            if (orderBy.equals(Constants.START_BY_DASHBOARD)){
                //Ordered by dash
                disableTailorDetails=false;
            }
            if (orderBy.equals(Constants.START_BY_MERCHANDISE)){
                //Ordered by Merc
                disableTailorDetails=true;
            }
            if (orderBy.equals(Constants.START_BY_ORDER_ADD_EDIT)){
                //Order by AddEditOrder
                disableTailorDetails=true;
            }
        }

        //Load Cursor
        uiBindFrom = new String[]{
                 TailorsContract.TAILOR_NAME
                ,TailorsContract.TAILOR_CONTACT
                ,TailorsContract.TAILOR_SHOP_NAME
                ,TailorsContract.TAILOR_CARD_NUMBER
        };

        int[] uiBindTo = {
                R.id.textView_listTailorName
                ,R.id.textView_listTailorContact
                ,R.id.textView_listTailorShopName
                ,R.id.textView_listTailorCardNumber};


        adapter = new SimpleCursorAdapter(getActivity()
                ,R.layout.list_item_tailors
                ,null,uiBindFrom,uiBindTo,0);
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
                getLoaderManager().restartLoader(2, null, TailorsListFragment.this);
                //getLoaderManager().initLoader(2, null, TailorsListFragment.this);
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        return rootView;
    }
    //----------------------------------Overrides-----------------------------//
    //----------------------------------onDestroyView()-----------------------------//
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if (adapter != null)
            adapter=null;
        if(cursorLoader !=null)
            cursorLoader=null;
        if(cursor !=null)
            cursor = null;
    }
    //----------------------------------onListItemClick()-----------------------------//
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        if (disableTailorDetails){
            Intent intent = new Intent();
            cursor = adapter.getCursor();
            intent.putExtra("tailor_card",cursor.getString(1));
            intent.putExtra("tailor_city", cursor.getString(9));
            getActivity().setResult(Activity.RESULT_OK,intent);
            getActivity().finish();
        }else {
        //Use ID to show Data
        Intent i = new Intent(getActivity(),TailorActivity.class);
        /** Here the we are using TailorFragment EXTRA_TAILOR_ID static
         *  as argument. and passing the list item's id to TailorActivity.
         *  Which will utilize it show exact data.
         */
        //String card=textCardNumber.getText().toString();
        i.putExtra(TailorFragment.EXTRA_TAILOR_ID,(int)id);
        //i.putExtra(TailorFragment.EXTRA_TAILOR_CARD,card);
        startActivity(i);
        }
    }
    //----------------------------------onCreateLoader()-----------------------------//
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        switch (id){
            case 1:
                cursorLoader = new CursorLoader(getActivity(),MasterSahabProvider.CONTENT_URI_TAILORS
                        ,null
                        ,null
                        ,null
                        ,null);
                break;
            case 2:
                if (curFilter !=null){
                    cursorLoader= new CursorLoader(getActivity()
                            ,MasterSahabProvider.CONTENT_URI_TAILORS        //URI To
                            ,TailorsContract.PROJECTION
                            ,TailorsContract.TAILOR_CARD_NUMBER + " LIKE '%"
                            + curFilter + "%' OR " + TailorsContract.TAILOR_NAME + " LIKE '%"
                            + curFilter + "%' OR " + TailorsContract.TAILOR_SHOP_NAME + " LIKE '%"
                            + curFilter + "%'"
                            ,null
                            ,null);
                    }
                break;
        }
        return cursorLoader;
    }
    //----------------------------------onLoadFinished()-----------------------------//
    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data)
    {
        if(adapter!=null && data !=null)
            adapter.swapCursor(data); //swap the new cursor in
        else
            Log.d(Constants.APP_TAG, "Tailors Cursor is null");
    }
    //----------------------------------onLoaderReset()-----------------------------//
    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        if(adapter != null)
            adapter.swapCursor(null);
        else
            Log.d(Constants.APP_TAG, "Tailors adapter is null");
    }
}
