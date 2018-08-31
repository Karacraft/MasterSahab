package com.karacraft.utils;

import android.util.Log;

import com.karacraft.mastersahab.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Nov 17 2015
 * File Name        JsonParser.java
 * Comments
 */
public class JsonParser
{
    private static final String TAG = "karacraft";

    static JSONObject mJsonObject = null;

    //Constructor
    public JsonParser(){
        //Empty;
    }
    //----------------------------------getJsonKey()-----------------------------//
    public String getJsonKey(String data,String key){
        String mKey;
        mJsonObject= getJsonFromString(data);
    try
        {
            mKey = mJsonObject.getString(key);
        } catch (JSONException e)
        {
            mKey = "Operation ended with JsonException " + e;
            return mKey;
        }
        return mKey;
    }
    //----------------------------------hasJsonKey()-----------------------------//
    public boolean hasJsonKey(String key){
        return mJsonObject.has(key);
    }

    //----------------------------getJsonFromString()-----------------------------------//
    public JSONObject getJsonFromString(String response){

    try
        {
        mJsonObject = new JSONObject(response);
        }
    catch (JSONException e)
        {
        //This won't compile with release built
        if (BuildConfig.DEBUG)
            {
            Log.e(TAG, "JsonParser->getJsonFromString: Error Parsing Data", e);
            }
        }
        //return JSON Object;
        return mJsonObject;
    }
}
