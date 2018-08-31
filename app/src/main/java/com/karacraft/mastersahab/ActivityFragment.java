package com.karacraft.mastersahab;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.TextView;

import com.karacraft.security.Authenticator;
import com.karacraft.security.MasterSahabProvider;
import com.karacraft.utils.AccountManagerMSP;
import com.karacraft.utils.Constants;
import com.karacraft.utils.Helper;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Nov 14 2015
 * File Name        ActivityFragment.java
 * Comments         The Heart of All The Application
 *                  Uses Helper.java
 */
public class ActivityFragment extends Fragment {

    //For CustomDialogFragment
    public static final int DIALOG_FRAGMENT     =1;
    public static final int LOGIN_FRAGMENT      =2;
    public static final int DIALOG_LOGOUT       =3;
    public static final int DIALOG_ORDER_STATUS =4;

    //Account Manager Local
    AccountManagerMSP accountManagerMSP;
    SharedPreferences sharedPreferences;
    //Views
    Button buttonTailors;
    Button buttonOrders;
    Button buttonSuits;
    Button buttonMerchandise;
    TextView textViewVersion;

    //For Alert Dialog Box
    FragmentManager fm=null;
    CustomAlertDialogFragment customDialog;

    //Menu Item Login
    MenuItem menuLogin;
    MenuItem menuLogout;
    MenuItem menuSync;

    boolean accountExist=false;
    boolean deleteData=false;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_activity,container,false);
        //Initialize Account Manager
        accountManagerMSP = new AccountManagerMSP(getActivity(),getString(R.string.accountType));
//        if(accountManagerMSP.accountExist()){
//           // syncData();
//        }
        //Setup Controls
        setupControls(rootView);
        //Set Title
        getActivity().setTitle(R.string.title_activity);
        //Set Menu Options
        setHasOptionsMenu(true);
        //Set FragmentManager for Dialog Box
        fm = getFragmentManager();

        //TODO:: Add Code to ensure that after time mentioned in Laravel Server Jwt-Auth Referesh, we force the user to Reauthenticate.

        //--------------------------
        buttonTailors.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent iTailors = new Intent(getActivity(), TailorsListActivity.class);
                iTailors.putExtra(Constants.ORDER_BY, Constants.START_BY_DASHBOARD);
                startActivity(iTailors);
            }
        });
        //--------------------------
        buttonOrders.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CustomAlertDialogOptionsFragment cdof = CustomAlertDialogOptionsFragment.newInstance("Select Order Status");
                cdof.setTargetFragment(ActivityFragment.this,DIALOG_ORDER_STATUS);
                cdof.show(fm,"Order Status Fragment");
                //Here we are presenting a choice list, if use selects and option, we go with it.
            }
        });
        //---------------------------
        buttonSuits.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent iSuits = new Intent(getActivity(), SuitsListActivity.class);
                iSuits.putExtra(Constants.ORDER_BY, Constants.START_BY_DASHBOARD);
                startActivity(iSuits);
            }
        });
        //-------------------------------------
        buttonMerchandise.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), MercListActivity.class);
                intent.putExtra(Constants.ORDER_BY, Constants.START_BY_DASHBOARD);
                startActivity(intent);
            }
        });
        //---------------------
        return rootView;
    }
    //----------------------------setupControls()-----------------------------//
    private void setupControls(View rootView){
        buttonTailors = (Button)rootView.findViewById(R.id.button_tailors);
        buttonOrders = (Button)rootView.findViewById(R.id.button_orders);
        buttonSuits = (Button)rootView.findViewById(R.id.button_suits);
        buttonMerchandise = (Button) rootView.findViewById(R.id.button_merchandise);
        textViewVersion = (TextView)rootView.findViewById(R.id.textView_activity_app_version);
        textViewVersion.setText("Version : " + Helper.getAppVersionNumber(getActivity()));
    }
    //------------------showLogin()-------------------------//
    private void showLogin(){
        //Show LoginFragment
        Intent iLogin = new Intent(getActivity(),UserLoginActivity.class);
        iLogin.putExtra(Constants.ORDER_BY,Constants.START_BY_DASHBOARD);
        //Sends result via LoginFragment->sendResult to onActivityResult
        startActivityForResult(iLogin, LOGIN_FRAGMENT);
    }
      //-----------------------------Menu Overrides-----------------------------------//
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.menu_fragment_activity, menu);
        menuLogin = menu.findItem(R.id.menu_login);
        menuLogout = menu.findItem(R.id.menu_logout);
        menuSync = menu.findItem(R.id.menu_sync);
        //Check if Account exist in Account Manager
        accountExist = accountManagerMSP.accountExist();
        if(accountExist){
            disableLogin();
        }else {
            //Disable Buttons
            disableLogout();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
    //--------------------onOptionsItemsSelected()-----------------------------------//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_login:
                showLogin();
                break;
            case R.id.menu_logout:
                customDialog = CustomAlertDialogFragment.newInstance("Do you really want to Logout?");
                customDialog.setTargetFragment(ActivityFragment.this, DIALOG_LOGOUT);
                customDialog.show(fm, "Logout Fragment");
                break;
            case R.id.menu_quit:
                // Show AlertDialog
                customDialog = CustomAlertDialogFragment.newInstance("Do you really want to quit?");
                customDialog.setTargetFragment(ActivityFragment.this, DIALOG_FRAGMENT);
                customDialog.show(fm, "Alert Fragment");
                break;
            case R.id.menu_sync:
                //Force our data to sync
                syncData();
                break;
            default:
                break;
        }
        return true;
    }
    //----------------------------syncData()---------------------------------//
    private void syncData(){
        Bundle extras = new Bundle();
        extras.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        extras.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        AccountManager accountManager = AccountManager.get(getActivity());
        Account myAccount=null;
        Account[] accounts = accountManager.getAccountsByType(Authenticator.KEY_ACCOUNT_TYPE);
        for(Account account : accounts){
            myAccount=account;
        }
        ContentResolver.requestSync(myAccount,MasterSahabProvider.AUTHORITY,extras);
    }
    //------------------------------onResultActivity()-------------------------------//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        boolean status=false;

        switch (requestCode){
            case LOGIN_FRAGMENT:
                if(resultCode == Activity.RESULT_OK)
                    {
                    //Enable Buttons
                    disableLogin();
                    }
                break;
            case DIALOG_FRAGMENT:
                status= data.getBooleanExtra(Constants.KEY, true);
                if (status)
                    {
                        System.exit(0);
                    }
                break;
            case DIALOG_LOGOUT:
                if (resultCode == Activity.RESULT_OK){
                    Log.d(Constants.APP_TAG, "Dialog Logout Clicked");
                    doLogout();
                }
                break;
            case DIALOG_ORDER_STATUS:
                status = data.getBooleanExtra(Constants.KEY,true);
                int selection = data.getIntExtra("selection",0);
                Intent intent=null;
                if(status){
                    switch (selection){
                        case 1:
                            Log.d(Constants.APP_TAG, "Dialog selected Pending");
                            intent = new Intent(getActivity(),OrdersListActivity.class);
                            intent.putExtra(Constants.ORDER_BY,Constants.START_BY_DASHBOARD);
                            intent.putExtra(Constants.ORDER_TYPE,OrdersListFragment.PENDING_ORDERS);
                            startActivity(intent);
                            break;
                        case 2:
                            Log.d(Constants.APP_TAG, "Dialog selected Archive");
                            intent = new Intent(getActivity(),OrdersListActivity.class);
                            intent.putExtra(Constants.ORDER_BY,Constants.START_BY_DASHBOARD);
                            intent.putExtra(Constants.ORDER_TYPE,OrdersListFragment.ARCHIVE_ORDERS);
                            startActivity(intent);
                            break;
                        case 3:
                            Log.d(Constants.APP_TAG, "Dialog selected Rejected");
                            intent = new Intent(getActivity(),OrdersListActivity.class);
                            intent.putExtra(Constants.ORDER_BY,Constants.START_BY_DASHBOARD);
                            intent.putExtra(Constants.ORDER_TYPE,OrdersListFragment.REJECTED_ORDERS);
                            startActivity(intent);
                            break;
                    }
                }else{
                    Log.d(Constants.APP_TAG, "Dialog was Escaped");
                }
                break;
            default:
                break;
        }
    }
    //--------------doLogout()--------------------------//
    private void doLogout(){
        //Delete all the data
        getActivity().getContentResolver().delete(MasterSahabProvider.CONTENT_URI_ORDERS,null,null);
        getActivity().getContentResolver().delete(MasterSahabProvider.CONTENT_URI_SUITS,null,null);
        getActivity().getContentResolver().delete(MasterSahabProvider.CONTENT_URI_STAFF,null,null);
        getActivity().getContentResolver().delete(MasterSahabProvider.CONTENT_URI_TAILORS,null,null);
        getActivity().getContentResolver().delete(MasterSahabProvider.CONTENT_URI_STATS,null,null);
        //Delete the account
        AccountManagerMSP accountManagerMSP = new AccountManagerMSP(getActivity(), Authenticator.KEY_ACCOUNT_TYPE);
        accountManagerMSP.deleteAllAccounts();
        //Delete Shared Preferences
        sharedPreferences = getActivity().getSharedPreferences(Constants.APP_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        accountExist=false;
        //Disable Buttons
        disableLogout();
        Log.d(Constants.APP_TAG,"Do Logout is done");
    }
    // Disable Login Button if Account don't exist
    private void disableLogin(){
        menuLogin.setVisible(false);
        menuLogout.setVisible(true);
        menuSync.setVisible(true);
        buttonOrders.setEnabled(true);
        buttonTailors.setEnabled(true);
        buttonSuits.setEnabled(true);
        buttonMerchandise.setEnabled(true);
        getActivity().invalidateOptionsMenu();
    }
    // Disable Logout if Account exist
    private void disableLogout(){
        menuLogout.setVisible(false);
        menuLogin.setVisible(true);
        menuSync.setVisible(false);
        buttonOrders.setEnabled(false);
        buttonTailors.setEnabled(false);
        buttonSuits.setEnabled(false);
        buttonMerchandise.setEnabled(false);
        getActivity().invalidateOptionsMenu();
    }
}

