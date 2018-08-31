package com.karacraft.data;

import android.os.AsyncTask;
import android.util.Log;

import com.karacraft.mastersahab.BuildConfig;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by duke on 7/1/15.
 */
public class iNetData extends AsyncTask<String /**Param **/,Integer /** Progress **/,String /** Result **/>{

    CallBackListener myListener; //Pointer to the Method of Calling Class
    String response; //This response will be sent back

    URL url; //Url Conneciton to be used - Defined in Base_AKMS APP_URL
    HttpURLConnection con; //Generic Connection


    public void setMyListener(CallBackListener myListener) {
        this.myListener = myListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Use This area to show progress dialog etc

    }

    @Override
    protected String doInBackground(String... params) {

        if (params[1] == "POST")
        {
            //Do Post HttpUrlConection
            String postParam ="&username=" + params[2] + "&password=" + params[3];
            Log.d("PTI", postParam);
            try {
                url = new URL(params[0]);
                con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setConnectTimeout(10000);
                con.setInstanceFollowRedirects(false);
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                con.setRequestProperty("charset", "utf-8");
                //Write Post Data
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeUTF(postParam);
                wr.flush();
                wr.close();

                int i = con.getResponseCode();
                if (i != 200) {
                    if (BuildConfig.DEBUG)
                    {
                        Log.d("PTI", "Response Code is : " + i);
                        Log.d("PTI", "Response Message is : " + con.getResponseMessage());
                        Log.d("PTI", "Aborting Operation...");
                    }
                    response = "Failure";
                }
                else if (i == 200)
                {
                    if (BuildConfig.DEBUG) {
                        Log.d("PTI", "Response Code is : " + i);
                        Log.d("PTI", "Response Message is : " + con.getResponseMessage());
                        Log.d("PTI", "Contuining Operation...");
                    }
                    response = ReadData(con);
                }
                //Disconnect Connection
                con.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } // "POST" ends here
        else if (params[1]=="GET")
        {
            // Do some work here like download data
            try {
                url = new URL(params[0]);
                con = (HttpURLConnection) url.openConnection();

                int i = con.getResponseCode();
                if (i != 200) {
                    if (BuildConfig.DEBUG)
                    {
                        Log.d("PTI", "Response Code is : " + i);
                        Log.d("PTI", "Response Message is : " + con.getResponseMessage());
                        Log.d("PTI", "Aborting Operation...");
                    }
                    response = "Failure";
                }
                else if (i == 200)
                {
                    if (BuildConfig.DEBUG) {
                        Log.d("PTI", "Response Code is : " + i);
                        Log.d("PTI", "Response Message is : " + con.getResponseMessage());
                        Log.d("PTI", "Contuining Operation...");
                    }
                    response = ReadData(con);
                }
                //Disconnect Connection
                con.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } // "GET" ends Here
        return response;

    }
    //-----------------------------------------------------
    private String ReadData(HttpURLConnection conn){

        String response="";
        String line;
        BufferedReader reader = null;

        try
        {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try
        {
            while ((line = reader.readLine()) != null) {
                response = response + line;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    //---------------------------------------------------

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //Update the progress here
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //Update the UI here, Remove dialog boxes etc. This is the end
        myListener.callback(response);
    }
}
