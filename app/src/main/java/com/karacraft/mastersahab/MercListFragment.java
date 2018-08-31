package com.karacraft.mastersahab;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.karacraft.data.MerchandisesContract;
import com.karacraft.security.MasterSahabProvider;
import com.karacraft.utils.Constants;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Feb 15 2016
 * File Name
 * Comments
 */
public class MercListFragment extends ListFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>
{

    public static final String VIEW_MERCHANDISE_LIST = "view_merchandise_list";

    SimpleCursorAdapter         adapter;
    ListView                    listView;
    EditText                    textFilter;
    String                      curFilter;
    CursorLoader                cursorLoader=null;
    MenuItem                    menuAddMerc;
    TextView                    mercImageTemp;
    ImageView                   mercImageView;
    String                      selection;
    String[]                    selectArgs;

    Boolean                     contextItemSelected=false;
    Boolean                     showMainMenu=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_merc_list,container,false);

        /** Check which activity called, and present data accordingly */
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras !=null){
            String orderBy= extras.getString(Constants.ORDER_BY);
            String orderType=extras.getString(Constants.ORDER_TYPE);
            switch (orderBy){
                case Constants.START_BY_TAILOR:
                    //Tailor Detail calls the Activity. List should only show his data
                    String cardNumber = extras.getString(TailorFragment.EXTRA_TAILOR_CARD);
                    selectArgs = new String[]{cardNumber};
                    selection = "card_number=?";
                    showMainMenu=false;
                    break;
                case Constants.START_BY_DASHBOARD:
                    selectArgs = null;
                    selection = null;
                    showMainMenu=true;
                    break;
            }
        } //extras
        //Set Title
        getActivity().setTitle(R.string.merc_list_activity);
        //Setup Controls
        setupControls(rootView);
        //setup Menu
        setHasOptionsMenu(showMainMenu);
        //set Parent Activity
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        //setupActionMenu
        setupActionMenu();
        /** Load Cursor */
        String[] uiBindForm = {

                MerchandisesContract.MERCHANDISE_CARD_NUMBER
                , MerchandisesContract.MERCHANDISE_DATE
                , MerchandisesContract.MERCHANDISE_ITEM_NAME
                , MerchandisesContract.MERCHANDISE_ITEM_PIC
                            };
        int[] uiBindTo = {
                 R.id.textView_listItemCardNumber
                , R.id.textView_listItemMercDate
                , R.id.textView_listItemName
                , R.id.tv_mercImageTemp
                            };

//        if(mercImageTemp.getText().equals(Constants.EMPTY_STRING)){
//                File file = new File(mercImageTemp.getText().toString());
//                if(file.exists()){
//                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                    mercImageView.setImageBitmap(bitmap);
//                    //mercPicName=file.toString();
//                }
//            }

        adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_merc, null, uiBindForm, uiBindTo, 0);
        listView.setAdapter(adapter);
        listView.setEmptyView(rootView.findViewById(android.R.id.empty));
        getLoaderManager().initLoader(1, null, this);

        /** Text Filter for Search */
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
                getLoaderManager().restartLoader(2, null, MercListFragment.this);
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        return rootView;
    }
    //-------------------setupControls()-----------------------//
    private void setupControls(View rootView){
        listView = (ListView) rootView.findViewById(android.R.id.list);
        textFilter = (EditText) rootView.findViewById(R.id.editText_mercListSearchFilter);
        mercImageTemp = (TextView) rootView.findViewById(R.id.tv_mercImageTemp);
        mercImageView = (ImageView) rootView.findViewById(R.id.imageView_listMercImage);
    }
    //-------------------setupActionMenu()----------------------//
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
                    if(!contextItemSelected){
                        mode.setTitle(listView.getCheckedItemCount() + " item Selected");
                        contextItemSelected = true;
                    } else{
                        return;
                    }
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu)
                {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.context_menu_fragment_merchandiselist, menu);
                    mode.setTitle("Select Merchandise");
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
                    switch (item.getItemId()) {
                        case R.id.menu_editMerchandise:
                            editSelectedMerchandise();
                            return true;
                        case R.id.menu_deleteMerchandise:
                            deleteSelectedMerchandise();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode)
                {
                    ///
//                    for (int i = 0 ; i < listView.getCount(); i++){
//                        listView.setItemChecked(i,false);
//                    }
//                    listView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
                    contextItemSelected=false;
                }
            });
        }
    }
    //--------------------editSelectedMerchandise()---------------//
    private void editSelectedMerchandise(){
        long[] ids = listView.getCheckedItemIds();
        long myId = 0;
        for (long id : ids){
            myId = id;
        }
        //Start an Intent to Edit the data
        Intent intent = new Intent(getActivity(),MercActivity.class);
        intent.putExtra(Constants.ORDER_BY,Constants.START_BY_MERCHANDISE_LIST);
        intent.putExtra(Constants.ORDER_TYPE,MercFragment.EDIT_MERCHANDISE);
        intent.putExtra("id", String.valueOf(myId));
        startActivityForResult(intent, Constants.REQUEST_MERCHANDISE_EDIT);
        //TODO::if this isn't working correctly then comment out the below line
        //getActivity().finish();
    }
    //--------------------deleteSelectedMerchandise()------------//
    private void deleteSelectedMerchandise(){
        long[] ids = listView.getCheckedItemIds();
        long myId = 0;
        for (long id : ids){
            myId = id;
        }
        getActivity().getContentResolver().delete(MasterSahabProvider.CONTENT_URI_MERCHANDISES, "_id=?", new String[]{String.valueOf(myId)});
        Log.d(Constants.APP_TAG, "Deleting but not getting there. " + myId);
    }
    //--------------------Overrides()----------------------------//
    @Override
    public void onDestroyView()
    {
        if (adapter != null)
            adapter=null;
        if(cursorLoader != null)
            cursorLoader=null;
        super.onDestroyView();
    }
    //------------------------onCreateOptionsMenu()--------------------------//
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_fragment_merchandiselist, menu);
        menuAddMerc = menu.findItem(R.id.menu_addMerchandise);
        super.onCreateOptionsMenu(menu, inflater);
    }
    //-----------------------onOptionsItemSelected()-------------------------//
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.menu_addMerchandise:
                Intent intent = new Intent(getActivity(),MercActivity.class);
                intent.putExtra(Constants.ORDER_BY, Constants.START_BY_MERCHANDISE_LIST);
                intent.putExtra(Constants.ORDER_TYPE,MercFragment.ADD_MERCHANDISE);
                startActivityForResult(intent,Constants.REQUEST_MERCHANDISE_ADD);
                return true;
            case R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //----------------------onCreateLoader()-----------------------//
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        CursorLoader cursorLoader=null;
        if(id ==1){
            cursorLoader =new CursorLoader(getActivity()
                    , MasterSahabProvider.CONTENT_URI_MERCHANDISES
                    , MerchandisesContract.PROJECTION
                    , selection
                    , selectArgs
                    , null
            );
        }
        if(id == 2){
            if(curFilter != null){
                cursorLoader=  new CursorLoader(getActivity()
                        ,MasterSahabProvider.CONTENT_URI_MERCHANDISES
                        ,MerchandisesContract.PROJECTION
                        ,MerchandisesContract.MERCHANDISE_ITEM_NAME + " LIKE '%"
                        + curFilter + "%' OR " + MerchandisesContract.MERCHANDISE_CARD_NUMBER + " LIKE '%"
                        + curFilter + "%'"
                        ,null
                        ,null
                );
            }
        }

        return cursorLoader;
    }
    //-------------------------onLoadFinished()------------------------------//
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if(adapter!=null && data!=null)
            adapter.swapCursor(data); //swap the new cursor in
        else
            Log.d(Constants.APP_TAG, "Merchandise Cursor is null");
    }
    //--------------------------onLoaderReset()------------------------------//
    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        if(adapter != null)
            adapter.swapCursor(null);
        else
            Log.d(Constants.APP_TAG, "Merchandise adapter is null");
    }
    //-----------------onActivityResult()-------------------------------------//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case Activity.RESULT_OK:
                if(requestCode == Constants.REQUEST_MERCHANDISE_EDIT){
                    Log.d(Constants.APP_TAG,"MercListFragment -> onActivityResult : Merchandise Edited");
                }
                if(requestCode == Constants.REQUEST_MERCHANDISE_ADD)
                    Log.d(Constants.APP_TAG, "MercListFragment -> onActivityResult : Merchandise Added");
                break;
            case Activity.RESULT_CANCELED:
                    //Operation resulted in Error
                    Log.d(Constants.APP_TAG,"MercListFragment -> onActivityResult : Cancelled Result");
                break;
        }
    }
}