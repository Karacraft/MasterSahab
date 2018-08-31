package com.karacraft.security;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.karacraft.mastersahab.UserLoginActivity;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Feb 07 2016
 * File Name        Authenticator.java
 * Comments         Link : http://developer.android.com/training/sync-adapters/creating-authenticator.html
 *                  This is a stub Authenticator. To Use an actual account, Please implement Data
 *                  In current state, with AuthenticateService.java, The "Master Sahab" is
 *                  visible in android accounts data, but it doesn't show any thing.
 */
public class Authenticator extends AbstractAccountAuthenticator
{
    //ACCOUNT TYPE
    public static final String KEY_ACCOUNT_TYPE  ="com.karacraft.mastersahab";
    //public static final String KEY_PROVIDER_NAME ="com.karacraft.se.StubProvider";

    Context context;
    //Simple Constructor
    public Authenticator(Context context)
    {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response
            , String accountType)
    {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response
            , String accountType    //com.karacraft.mastersahab
            , String authTokenType
            , String[] requiredFeatures
            , Bundle options) throws NetworkErrorException
    {
        //TODO::here you can stop user from adding more accounts
        //We want to add account via LoginActivity
        final Intent intent = new Intent(context, UserLoginActivity.class);
        //Add Extras in here
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT,intent);
        //return null;
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response
            , Account account
            , Bundle options) throws NetworkErrorException
    {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response
            , Account account
            , String authTokenType
            , Bundle options) throws NetworkErrorException
    {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType)
    {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response
            , Account account
            , String authTokenType
            , Bundle options) throws NetworkErrorException
    {

        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response
            , Account account
            , String[] features) throws NetworkErrorException
    {
        return null;
    }

}
