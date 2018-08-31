package com.karacraft.mastersahab;

import android.app.*;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.karacraft.data.SuitsContract;
import com.karacraft.security.MasterSahabProvider;
import com.karacraft.utils.Constants;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Nov 22 2015
 * File Name        SuitsListFragment.java
 * Comments
 */
public class SuitsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{

    SimpleCursorAdapter adapter;
    ListView listView;
    EditText textFilter;
    String curFilter;
    CursorLoader cursorLoader=null;

    Boolean startedFromOtherActivity=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_suits_list,container,false);

        listView = (ListView) rootView.findViewById(android.R.id.list);
        textFilter = (EditText) rootView.findViewById(R.id.editText_suitsListSearchFilter);

        //Set Title
        getActivity().setTitle(R.string.suits_list_activity);
        //Load Cursor
        //set Parent Activity
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        /** Get Bundle */
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null){
            String orderBy = extras.getString(Constants.ORDER_BY);
            if (orderBy.equals(Constants.START_BY_ORDER_ADD_EDIT)){
                startedFromOtherActivity=true;
            }
        }

        String[] uiBindForm = {
                SuitsContract.SUITS_CODE
                , SuitsContract.SUIT_CATALOG
                , SuitsContract.SUITS_PRICE
                , SuitsContract.SUITS_COLOR
                , SuitsContract.SUITS_DESCRIPTION};
        int[] uiBindTo = {
                R.id.textView_listSuitsSuitCode
                , R.id.textView_listSuitsSuitCatalog
                , R.id.textView_listSuitsSuitPrice
                , R.id.textView_listSuitsSuitColorType
                , R.id.textView_listSuitsSuitDescription};

        adapter = new SimpleCursorAdapter(getActivity()
                ,R.layout.list_item_suits
                ,null,uiBindForm,uiBindTo,0);
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
                getLoaderManager().restartLoader(2,null,SuitsListFragment.this);
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        if(startedFromOtherActivity){
            Cursor cursor = adapter.getCursor();
            Intent intent = new Intent();
            intent.putExtra("suit_code",cursor.getString(1));
            intent.putExtra("suit_catalog",cursor.getString(2));
            intent.putExtra("suit_price",cursor.getString(6));
            intent.putExtra("suit_color",cursor.getString(3));
            cursor.close();
            getActivity().setResult(android.app.Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }
    //--------------------Overrides()----------------------------//
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if (adapter != null)
            adapter=null;
        if(cursorLoader != null)
            cursorLoader=null;
    }
    //------------------onCreateLoader()----------------------//
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        CursorLoader cursorLoader=null;
        if(id ==1){
            cursorLoader =new CursorLoader(getActivity(),MasterSahabProvider.CONTENT_URI_SUITS
                    , null
                    , null
                    , null
                    , null
            );
        }
        if(id == 2){
            if(curFilter != null){
                cursorLoader=  new CursorLoader(getActivity()
                        ,MasterSahabProvider.CONTENT_URI_SUITS
                        ,SuitsContract.PROJECTION
                        ,SuitsContract.SUITS_CODE + " LIKE '%"
                        + curFilter + "%'"
                        ,null
                        ,null
                );
            }
        }

        return cursorLoader;
    }
    //-------------------onLoadFinished()----------------------------//
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if(adapter!=null && data!=null)
            adapter.swapCursor(data); //swap the new cursor in
        else
            Log.d(Constants.APP_TAG, "Suits Cursor is null");
    }
    //--------------------onLoaderReset()--------------------------//
    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        if(adapter != null)
            adapter.swapCursor(null);
        else
            Log.d(Constants.APP_TAG, "Suits adapter is null");
    }
}
