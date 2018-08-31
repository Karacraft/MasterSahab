package com.karacraft.mastersahab;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.karacraft.data.MerchandisesContract;
import com.karacraft.data.OrdersContract;
import com.karacraft.data.StaffContract;
import com.karacraft.data.StatsContract;
import com.karacraft.data.SuitsContract;
import com.karacraft.data.TailorsContract;
import com.karacraft.security.Authenticator;
import com.karacraft.security.MasterSahabProvider;
import com.karacraft.utils.AccountManagerMSP;
import com.karacraft.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Feb 07 2016
 * File Name
 * Since we are using Account Authenticator, it is necessary that we extend the LoginActivity with AccountAuthenticatorActivity
 * This allow us to send response from activity to Account Manager.
 */
public class UserLoginActivity extends AccountAuthenticatorActivity
{

    Button buttonLogin,buttonCancel;
    EditText editTextUserName,editTextPassword;
    TextView textViewLoginInfo;
    Boolean hasErrors=false;

    //UserLoginTask
    ProgressDialog progressDialog;
    String response=Constants.EMPTY_STRING;
    Boolean success=false;
    Boolean loggedIn=false;
    Boolean dataFetched=false;
    Context context;
    int flag;

    //Shared Pref/Account Manager Requirement
    String token="";
    String id="";
    String name="";
    String role="";
    String city="";
    String username="";
    String password="";
    //For Token parsing
    JSONObject jsonObject=null;
    //For Authenticator
    AccountManager accountManager;
    AccountManagerMSP accountManagerMSP;
    SharedPreferences sharedPreferences;

    //------------------UserLoginTask----
    URL mUrl=null;
    HttpURLConnection mConnection=null;
    String EQ = "=";
    String AMP = "&";

    //----------------------------------onCreate()-----------------------------//
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        progressDialog = new ProgressDialog(this);
        accountManagerMSP = new AccountManagerMSP(this,getString(R.string.accountType));
        context = this.getApplicationContext();

        //Setup Controls
        setupControls();
        //Disable the Controls before showing dialog box
        disableControls();
        //Check if Account exist in Account Manager

        boolean accountExits = accountManagerMSP.accountExist();

        if (accountExits){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Account already Exist. Please delete it & try again.");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    response = "";
                    finish();
                }
            });
            alertDialog.create();
            alertDialog.show();
        }
        //----------------------------------------------
        enableControls();
        //Controls Magic
        editTextUserName.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                textViewLoginInfo.setText("");
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        //------------------------------------------------------
        editTextPassword.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                textViewLoginInfo.setText("");
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        //------------------------------------------------------
        buttonLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                doLogin();
            }
        });
        //------------------------------------------------------
        buttonCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendCancel();
            }
        });
    }
    //----------------------------------setupControls()-----------------------------//
    private void setupControls(){
        editTextUserName = (EditText) findViewById(R.id.editText_loginUserName);
        editTextPassword = (EditText) findViewById(R.id.editText_loginPassword);
        textViewLoginInfo = (TextView)findViewById(R.id.textView_login_info);
        textViewLoginInfo.setTextColor(Color.RED);
        buttonLogin = (Button) findViewById(R.id.button_loginPost);
        buttonCancel=(Button) findViewById(R.id.button_loginCancel);
    }
    //----------------------------------disableControls()-----------------------------//
    private void disableControls(){
        editTextUserName.setEnabled(false);
        editTextPassword.setEnabled(false);
        textViewLoginInfo.setEnabled(false);
        textViewLoginInfo.setEnabled(false);
        buttonLogin.setEnabled(false);
        buttonCancel.setEnabled(false);
    }
    //----------------------------------enableControls()-----------------------------//
    private void enableControls(){
        editTextUserName.setEnabled(true);
        editTextPassword.setEnabled(true);
        textViewLoginInfo.setEnabled(true);
        textViewLoginInfo.setEnabled(true);
        buttonLogin.setEnabled(true);
        buttonCancel.setEnabled(true);
    }
    //----------------------------------isEmailValid()-----------------------------//
    private boolean isEmailValid(String email)
    {
        return email.contains("@");
    }
    //----------------------------------isPasswordValid()-----------------------------//
    private boolean isPasswordValid(String password)
    {
        return password.length() < 5;
    }
    //----------------------------------isUserIdValid()-----------------------------//
    private boolean isUserIdValid(String userid){
        return userid.length() < 4;
    }
    //----------------------------------doLogin()-----------------------------//
    public void doLogin(){

        username = editTextUserName.getText().toString();
        password = editTextPassword.getText().toString();
        hasErrors=false;

        if(isUserIdValid(username))
        {
            hasErrors=true;
            editTextUserName.setBackgroundColor(Color.MAGENTA);
            textViewLoginInfo.setText("User Id field must be 4 Characters long.");
        }
        if(isPasswordValid(password)){
            hasErrors=true;
            editTextPassword.setBackgroundColor(Color.MAGENTA);
            textViewLoginInfo.setText("Password field must be 5 Characters long.");
        }

        if(hasErrors){
            return;
        }
        /** Call The Aysnctask to Login User  */
        new UserLoginTask().execute();

    }
    //----------------------------------ResultOfUserLogin()-----------------------------//
    private void resultOfUserLogin(){
        //If we are loggedIn, ite means we need to get the inital data
        if(!dataFetched ){
               /** this will let us remain on the activity page, unless cancelled */
                showUserLoginError(flag);
                /** User pressed cancel on the activity */
        }else{
            final Intent intent = new Intent();
            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Authenticator.KEY_ACCOUNT_TYPE);
            intent.putExtra(AccountManager.KEY_AUTHTOKEN, token);
            this.setResult(RESULT_OK, intent);
            this.finish();
        }
    }
    //-----------------------------sendCancel()--------------------//
    private void sendCancel(){
        Intent intent = new Intent();
        this.setResult(Activity.RESULT_CANCELED,intent);
        this.finish();
    }
    //------------showUserLoginError()-----------------------//
    private void showUserLoginError(int i){

        switch (i){
            case 401:
                textViewLoginInfo.setText(Constants.RESPONSE_401);
                Log.d(Constants.APP_TAG, "UserLoginActivity->resultOfUserLogin: " + Constants.RESPONSE_401);
                break;
            case 404:
                textViewLoginInfo.setText(Constants.RESPONSE_404);
                Log.d(Constants.APP_TAG, "UserLoginActivity->resultOfUserLogin: " + Constants.RESPONSE_404);
                break;
            case 408:
                textViewLoginInfo.setText(Constants.RESPONSE_408);
                Log.d(Constants.APP_TAG, "UserLoginActivity->resultOfUserLogin: " + Constants.RESPONSE_408);
                break;
            case 500:
                textViewLoginInfo.setText(Constants.RESPONSE_500);
                Log.d(Constants.APP_TAG, "UserLoginActivity->resultOfUserLogin: " + Constants.RESPONSE_500);
                break;
            case 503:
                textViewLoginInfo.setText(Constants.RESPONSE_503);
                Log.d(Constants.APP_TAG, "UserLoginActivity->resultOfUserLogin: " + Constants.RESPONSE_503);
                break;
            default:
                textViewLoginInfo.setText(response);
                Log.d(Constants.APP_TAG, "UserLoginActivity->resultOfUserLogin: Unknown Error");
        }
    }
    /**
     * Represents an AsyncTask login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog.setTitle("Logging In...");
            progressDialog.show();
        }
        //----------------------------------onPostExecute()-----------------------------//
        @Override
        protected void onPostExecute(Void aVoid)
        {
            //super.onPostExecute(s);
            //Dismiss Progress Dialog
            if(progressDialog !=null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            resultOfUserLogin();
        }
        //----------------------------------onProgressUpdate()-----------------------------//
        @Override
        protected void onProgressUpdate(Void... values)
        {
            // you can implement some progressBar and update it in this record
            // setProgressPercent(progress[0]);
            super.onProgressUpdate(values);
        }
        //----------------------------------doInBackground()-----------------------------//
        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                if(!loggedIn){
                    flag = getTokenFromRest();
                    if(flag == 200){
                        loggedIn=true;
                        flag=getInitData();
                        if (flag==200){
                            dataFetched=true;
                            setupAccount();
                        }
                        //setupAccount();
                    }else {
                        loggedIn=false;
                    }
                }
            }  catch (Exception e)
            {
                response= e.getMessage();
                //Log.d(Constants.APP_TAG, "doInBackground -> :  " + e.getMessage());
                e.printStackTrace();
            } finally
            {
                if(mConnection != null)
                    mConnection.disconnect();
                Log.d(Constants.APP_TAG,"doInBackground -> Finally connection is closed ");
            }
            /** we can register the account with Account Manager Here.
             * But currently, we are doing it in ActivityFragment.java
             */
            return null;
        }
        //-------------------------getTokenFromRest()------------------------------//
        private int getTokenFromRest() throws IOException, JSONException
        {

            String mQuery = Constants.PREF_USERNAME_KEY + EQ + username
                    + AMP + Constants.PREF_PASSWORD_KEY + EQ + password;

            Log.d(Constants.APP_TAG, "getTokenFromRest -> Query is : " + mQuery);

            mUrl = new URL(Constants.LOGIN_URL + mQuery);
            mConnection = (HttpURLConnection) mUrl.openConnection();
            mConnection.setDoOutput(true);
            mConnection.setConnectTimeout(5000);
            mConnection.setInstanceFollowRedirects(false);
            mConnection.setRequestMethod(Constants.POST);
            mConnection.setRequestProperty("Content-type", "application/json");
            mConnection.setRequestProperty("charset", "utf-8");
            //Write Post Data
            DataOutputStream wr = new DataOutputStream(mConnection.getOutputStream());
            wr.writeUTF(mQuery);
            wr.flush();
            wr.close();

            flag = mConnection.getResponseCode();
            if (flag != 200) {
                cancel(true);
                return flag;
            }
            /** If AsyncTask isn't cancelled, then carry on */
            if(!isCancelled()){
                //Every Thing is Ok, Read the response
                response = readData(mConnection);
                //Trying JSON
                jsonObject=new JSONObject(response);
                //This response is returned by getTokenFromRest() if successful. use other methods to fetch all data.
                if (jsonObject.has(Constants.APP_TOKEN_KEY))
                {
                    token = jsonObject.get(Constants.APP_TOKEN_KEY).toString();
                    id = jsonObject.get(Constants.PREF_USER_ID_KEY).toString();
                    name =jsonObject.get(Constants.PREF_USER_NAME_KEY).toString();
                    role = jsonObject.get(Constants.PREF_USER_ROLE_KEY).toString();
                    city = jsonObject.get(Constants.PREF_USER_CITY_KEY).toString();
                }
            }   //isCancelled()
            return flag;
        }
        //----------------------------------onCancelled()-----------------------------//
        @Override
        protected void onCancelled(Void aVoid)
        {
            //Dismiss Progress Dialog
            if(progressDialog !=null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            resultOfUserLogin();
            //super.onCancelled(response);
        }
        @Override
        protected void onCancelled()
        {
        //Dismiss Progress Dialog
            if(progressDialog !=null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            resultOfUserLogin();
            //super.onCancelled();
        }
        //---------------getInitData()--------------------//
        private int getInitData() throws Exception{

            String response=Constants.EMPTY_STRING;
            URL mUrl=null;
            HttpURLConnection mConnection=null;
            String mQuery = Constants.APP_TOKEN_KEY + Constants.EQUALS + token;
            Log.d(Constants.APP_TAG,mQuery);
            JSONObject jsonObject=null;

            mUrl = new URL(Constants.FETCH_ALL + mQuery);
            mConnection = (HttpURLConnection) mUrl.openConnection();
            Log.d(Constants.APP_TAG,"UserLoginActivity->getInitData: Server URL is : " +mUrl.toString());
            int i = mConnection.getResponseCode();

            if (i == 200){
                // Go Ahead
                Log.d(Constants.APP_TAG,"UserLoginActivity->loadAllData: Updating local database...");
                //Every Thing is Ok, Read the response
                response = readData(mConnection);
                //Trying JSON
                jsonObject=new JSONObject(response);
                //Check if there is a reply

                if (jsonObject.has(Constants.RESPONSE_REPLY))
                {
                    ContentValues values = new ContentValues();
                    Uri uri = null;
                    /**         Load Staff Data         */
                    JSONArray staffArray = jsonObject.getJSONArray("staffQuery");
                    for (int j = 0; j < staffArray.length(); j++)
                    {
                        JSONObject Object= staffArray.getJSONObject(j);

                        values.put(StaffContract.STAFF_ID,Integer.parseInt(Object.getString("id")));
                        values.put(StaffContract.STAFF_NAME, Object.getString("name"));
                        values.put(StaffContract.STAFF_ROLE, Object.getString("role"));
                        values.put(StaffContract.STAFF_CITY, Object.getString("city"));

                        uri = context.getContentResolver().insert(MasterSahabProvider.CONTENT_URI_STAFF, values);
                    }
                    Log.d(Constants.APP_TAG, "Staff Data uploaded.");
                    /**         Load Tailors Data       */
                    JSONArray tailorArray = jsonObject.getJSONArray("tailorsQuery");
                    values.clear();
                    for (int l = 0 ; l < tailorArray.length(); l++){
                        JSONObject Object = tailorArray.getJSONObject(l);

                        values.put(TailorsContract.TAILOR_ID, Integer.parseInt(Object.getString("id")));
                        values.put(TailorsContract.TAILOR_CARD_NUMBER, Object.getString("card_number"));
                        values.put(TailorsContract.TAILOR_PIC, Object.getString("tailor_pic"));
                        values.put(TailorsContract.TAILOR_NAME, Object.getString("tailor_name"));
                        values.put(TailorsContract.TAILOR_SHOP_NAME, Object.getString("shop_name"));
                        values.put(TailorsContract.TAILOR_CNIC, Object.getString("tailor_cnic"));
                        values.put(TailorsContract.TAILOR_CONTACT, Object.getString("tailor_contact"));
                        values.put(TailorsContract.TAILOR_AREA, Object.getString("tailor_area"));
                        values.put(TailorsContract.TAILOR_ADDRESS, Object.getString("tailor_address"));
                        values.put(TailorsContract.TAILOR_CITY, Object.getString("tailor_city"));
                        values.put(TailorsContract.TAILOR_ACTIVE, Integer.parseInt(Object.getString("active")));

                        uri = context.getContentResolver().insert(MasterSahabProvider.CONTENT_URI_TAILORS, values);
                    }
                    Log.d(Constants.APP_TAG,"Tailors Data uploaded.");
                    /**         Load Suits Data         */
                    JSONArray suitsArray = jsonObject.getJSONArray("suitsQuery");
                    values.clear();
                    for (int l = 0 ; l < suitsArray.length(); l++){
                        JSONObject Object = suitsArray.getJSONObject(l);

                        values.put(SuitsContract.SUITS_ID, Integer.parseInt(Object.getString("id")));
                        values.put(SuitsContract.SUITS_CODE, Object.getString("suit_code"));
                        values.put(SuitsContract.SUIT_CATALOG, Object.getString("suit_catalog"));
                        values.put(SuitsContract.SUITS_COLOR, Object.getString("suit_color"));
                        values.put(SuitsContract.SUITS_TYPE, Object.getString("suit_type"));
                        values.put(SuitsContract.SUITS_DESCRIPTION, Object.getString("suit_description"));
                        values.put(SuitsContract.SUITS_PRICE,Integer.parseInt(Object.getString("suit_price")));
                        values.put(SuitsContract.SUITS_ACTIVE, Integer.parseInt(Object.getString("active")));

                        uri = context.getContentResolver().insert(MasterSahabProvider.CONTENT_URI_SUITS, values);
                    }
                    Log.d(Constants.APP_TAG,"Suits Data uploaded.");
                    /**         Load Orders Data        */
                    JSONArray ordersArray = jsonObject.getJSONArray("ordersQuery");
                    values.clear();
                    for (int l = 0; l < ordersArray.length(); l++){
                        JSONObject Object = ordersArray.getJSONObject(l);

                        values.put(OrdersContract.ORDER_ID,Integer.parseInt(Object.getString("id")));
                        values.put(OrdersContract.ORDER_DATE,Object.getString("order_placement_date"));
                        values.put(OrdersContract.ORDER_CARD_NUMBER,Object.getString("card_number"));
                        values.put(OrdersContract.ORDER_STAFF_ID,Integer.parseInt(Object.getString("staff_id")));
                        values.put(OrdersContract.ORDER_QUANTITY,Integer.parseInt(Object.getString("quantity")));
                        values.put(OrdersContract.ORDER_SUIT_CODE,Object.getString("suit_code"));
                        values.put(OrdersContract.ORDER_STAFF_CITY,Object.getString("staff_city"));
                        values.put(OrdersContract.ORDER_SUIT_PRICE,Integer.parseInt(Object.getString("suit_price")));
                        values.put(OrdersContract.ORDER_SUIT_COLOR,Object.getString("suit_color"));
                        values.put(OrdersContract.ORDER_SUIT_STATUS,Object.getString("order_status"));
                        values.put(OrdersContract.ORDER_STATUS_CHANGE_DATE,Object.getString("order_status_change_date"));
                        values.put(OrdersContract.ORDER_SUIT_DISCOUNTED_PRICE,Object.getString("suit_discounted_price"));
                        values.put(OrdersContract.ORDER_REDEEM,Integer.parseInt(Object.getString("redeem")));
                        values.put(OrdersContract.ORDER_RECEIPT,Object.getString("receipt"));
                        values.put(OrdersContract.ORDER_SUIT_CATALOUGE,Object.getString("suit_catlouge"));

                        uri =  context.getContentResolver().insert(MasterSahabProvider.CONTENT_URI_ORDERS, values);
                    }
                    Log.d(Constants.APP_TAG,"Orders Data uploaded.");
                    /**         Load Stats Data     **/
                    JSONArray statsArray = jsonObject.getJSONArray("statsQuery");
                    values.clear();
                    for (int l = 0; l < statsArray.length(); l++){
                        JSONObject Object = statsArray.getJSONObject(l);

                        values.put(StatsContract.STATS_ID,Integer.parseInt(Object.getString("id")));
                        values.put(StatsContract.STATS_TABLE_NAME,Object.getString("table_name"));
                        values.put(StatsContract.STATS_CREATEDAT,Object.getString("created_at"));
                        values.put(StatsContract.STATS_UPDATEDAT,Object.getString("updated_at"));

                        uri =  context.getContentResolver().insert(MasterSahabProvider.CONTENT_URI_STATS, values);

                    }
                    Log.d(Constants.APP_TAG,"Stats Data uploaded.");
                    /**             Load Merchandise Data */
                    JSONArray mercArray = jsonObject.getJSONArray("mercQuery");
                    values.clear();
                    for (int l = 0; l < mercArray.length(); l++){
                        JSONObject Object = mercArray.getJSONObject(l);

                        values.put(MerchandisesContract.MERCHANDISE_ID,Integer.parseInt(Object.getString("id")));
                        values.put(MerchandisesContract.MERCHANDISE_DATE,Object.getString("date"));
                        values.put(MerchandisesContract.MERCHANDISE_TAILOR_CITY,Object.getString("tailor_city"));
                        values.put(MerchandisesContract.MERCHANDISE_CARD_NUMBER,Object.getString("card_number"));
                        values.put(MerchandisesContract.MERCHANDISE_ITEM_PIC,Object.getString("item_pic"));
                        values.put(MerchandisesContract.MERCHANDISE_ITEM_NAME,Object.getString("item_name"));
                        values.put(MerchandisesContract.MERCHANDISE_ITEM_LOC,Object.getString("item_geo_loc"));
                        values.put(MerchandisesContract.MERCHANDISE_SYNCABLE,Object.getString("syncable"));

                        uri =  context.getContentResolver().insert(MasterSahabProvider.CONTENT_URI_MERCHANDISES, values);
                    }
                    Log.d(Constants.APP_TAG, "Merchandise Data uploaded.");
                }//If Object Has reply
            }
            return i;
        }
        //------------------readData()-----------------------------------
        private String readData(HttpURLConnection conn){

            String mResponse="";
            String line="";
            BufferedReader mBufferedReader = null;
            //Try to Read the InputStream
            try
            {
                mBufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = mBufferedReader.readLine()) != null) {
                    mResponse = mResponse + line;
                }
                mBufferedReader.close();
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
            return mResponse;
        }
        //-------------------------setupAccount()----------------------------//
        private void setupAccount(){

            //It's A New Account, Add it. (Step-2)
            accountManager = AccountManager.get(context);
            final Account account = new Account(username, Authenticator.KEY_ACCOUNT_TYPE);
            //Add data to Account
            final Bundle putExtra = new Bundle();
            putExtra.putString(Constants.APP_TOKEN_KEY, token);
            putExtra.putString(Constants.PREF_USER_ID_KEY, id);
            putExtra.putString(Constants.PREF_USERNAME_KEY, editTextUserName.getText().toString());
            putExtra.putString(Constants.PREF_PASSWORD_KEY, editTextPassword.getText().toString());
            putExtra.putString(Constants.PREF_USER_NAME_KEY, name);
            putExtra.putString(Constants.PREF_USER_CITY_KEY, city);
            putExtra.putString(Constants.PREF_USER_ROLE_KEY, role);
            //Save Preferences
            sharedPreferences = getSharedPreferences(Constants.APP_PREF_KEY,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(Constants.APP_TOKEN_KEY,token);
            editor.putString(Constants.PREF_USER_ID_KEY,id);
            editor.putString(Constants.PREF_USERNAME_KEY,editTextUserName.getText().toString());
            editor.putString(Constants.PREF_PASSWORD_KEY,editTextUserName.getText().toString());
            editor.putString(Constants.PREF_USER_NAME_KEY,name);
            editor.putString(Constants.PREF_USER_CITY_KEY,city);
            editor.putString(Constants.PREF_USER_ROLE_KEY,role);
            editor.commit();

            //Add to account
            accountManager.addAccountExplicitly(account,password,putExtra);
            //Set it to Auto Sync
            ContentResolver.setSyncAutomatically(account, getString(R.string.contentAuthority), true);
        }
    }
}
