package com.karacraft.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Feb 07 2016
 * File Name        AccountManagerMSP.java
 * Comments
 */
public class AccountManagerMSP
{
    AccountManager accountManager;
    String keyAccountType;
    Context context;

    public AccountManagerMSP(Context context, String keyAccountType)
    {
        this.context = context;
        accountManager = AccountManager.get(context);
        this.keyAccountType = keyAccountType;
    }

    //---------------checkAccount()----------------------------//
    /**
     * Checks if an account exist in Android AccountManager
     *
     * @return true or false
     */
    public boolean accountExist()
    {
        Account[] accounts = accountManager.getAccountsByType(keyAccountType);

        if (accounts.length > 0)
        {
            return true;
        } else
        {
            return false;
        }
    }
    //----------------------------------getAccountsCount-----------------------------//
    public int getAccountsCount(){
        Account[] accounts = accountManager.getAccountsByType(keyAccountType);
        return  accounts.length;
    }
    //-----------------deleteAccount()---------------------------//
    /**
     * Checks if an account exist in Android AccountManager
     *
     * @return nothing
     */
    public void deleteAccount(String accountName)
    {
        Account[] accounts = accountManager.getAccountsByType(keyAccountType);
        for(Account account : accounts){
            if (account.name.equals(accountName)){
                accountManager.removeAccount(account,null,null);
            }
        }
    }
    //----------------deleteSingleAccount()----------------------//
    public void deleteAllAccounts(){
        Account[] accounts = accountManager.getAccountsByType(keyAccountType);
        for(Account account : accounts){
            accountManager.removeAccount(account,null,null);
        }
    }
    //---------------getAccountData()--------------------------//
    public String getAccountData(String accountName,String keyToRetrieve){
        String result="";
        Account[] accounts = accountManager.getAccountsByType(keyAccountType);
        for (Account account : accounts){
            if(account.name.equals(accountName)){
                result = accountManager.getUserData(account,keyToRetrieve);
            }
        }
        return result;
    }
    //---------------selectFromMultipleAccounts()---------------------------//
    public boolean selectFromMultipleAccounts()
    {
        boolean result = false;
        Account[] accounts = accountManager.getAccountsByType(keyAccountType);

        if (accounts.length >= 2)
            result = true;
        if(accounts.length == 1)
            result = false;

        return result;
    }
    //--------------doesAccountExists()---------------------//
    public boolean accountExistsByName(String accountName){
        Account[] accounts = accountManager.getAccountsByType(keyAccountType);
        for(Account account : accounts){
            if (account.name.equals(accountName)){
                return true;
            }
        }
        return false;
    }
}
