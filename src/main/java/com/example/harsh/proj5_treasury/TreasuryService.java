package com.example.harsh.proj5_treasury;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.harsh.KeyCommon.KeyGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by harsh on 12/2/2017.
 */

public class TreasuryService extends Service {
    private String TAG = "TestService";
    String PREF_IS_RUNNING="servicerunning";
    static final public String COPA_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";
    ResultReceiver resultReceiver;
    static final public String COPA_MESSAGE = "com.controlj.copame.backend.COPAService.COPA_MSG";

    ProgressDialog pd;
   /* public TreasuryService() {
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
        return null;
    }*/

   // Set of already assigned IDs
    // Note: These keys are not guaranteed to be unique if the Service is killed
    // and restarted.

    private final static Set<UUID> mIDs = new HashSet<UUID>();
    private final static Set<UUID> mIDs1 = new HashSet<UUID>();
    private final static Set<UUID> mIDs2 = new HashSet<UUID>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand executed");
        //Reciever to communicate with StatusAcitivty so as to notify instantly about progress of service like Binded,Unbinder,Executing API calls etc.
        resultReceiver = intent.getParcelableExtra("receiver");
        return Service.START_NOT_STICKY;
    }
    // Implement the Stub for this Object
    private final KeyGenerator.Stub mBinder = new KeyGenerator.Stub() {

        // Implement the remote method 1
        public String monthlyCash(int year) {

            UUID id;
            String response;
            // Acquire lock to ensure exclusive access to mIDs
            // Then examine and modify mIDs
             //to synchronise method1 invoking threads
            synchronized (mIDs) {

                do {

                    id = UUID.randomUUID();
                      response="";
                       String queryStr = "SELECT  DISTINCT(\"open_mo\") \n" +
                            "FROM\n" +
                            "t1\n" +
                            "WHERE ( \"year\" = '"+year+ "' AND \"account\" = 'Federal Reserve Account'  )";
                    String urlStr = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=" + URLEncoder.encode(queryStr);
                    Bundle bundle = new Bundle();
                    bundle.putString("API", "The service has called API....");
                    resultReceiver.send(400, bundle);
                   try {
                      response= new JsonTask().execute(urlStr).get();

                   }
                   catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    catch (ExecutionException e1){
                        e1.printStackTrace();
                    }
                } while (mIDs.contains(id));

                mIDs.add(id);

            }
            return response;
        }


        // Implement the remote method 2
        public String dailyCash(int day, int month, int year, int workingDays) {

            UUID id;
            String response;
            StringBuffer l1;
            // Acquire lock to ensure exclusive access to mIDs
            // Then examine and modify mIDs
            //to synchronise method1 invoking threads
            synchronized (mIDs2) {

                do {

                    id = UUID.randomUUID();
                    response="";
                    l1=new StringBuffer();
                     Bundle bundle = new Bundle();
                    //CHANGE
                    bundle.putString("API", "The service has bound and running an API method....");
                    resultReceiver.send(400, bundle);
                    int count=1;
                    int newDay=day;

                   for(int i=count;count<workingDays+1;i++) {
                       String queryStr = "SELECT \"open_today\"\n" +
                               "FROM\n" +
                               "t1\n" +
                               "WHERE (\"month\" = '" + month + "' AND \"year\" = '" + year + "' AND \"day\" = '" + newDay + "' AND \"account\" = 'Federal Reserve Account')";
                       String urlStr = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=" + URLEncoder.encode(queryStr);

                       try {
                           response = new JsonTask().execute(urlStr).get();

                           //Parsing the JSON String to build List
                           if (response != null) {
                               try {
                                   // Getting JSON Array node
                                   JSONArray jsonarray = new JSONArray(response);//getJSONArray(jsonStr);
                                   if (jsonarray.length() != 0) {
                                       for (int j = 0; j < jsonarray.length(); j++) {
                                           JSONObject jsonObj = jsonarray.getJSONObject(j);

                                           String value = jsonObj.getString("open_today");

                                           l1.append(value);
                                           l1.append(" ");
                                       }
                                       count++;
                                       newDay = newDay + 1;
                                   } else {
                                       newDay = newDay + 1;
                                   }

                               } catch (final JSONException e) {
                                   Log.e("DAILY CASH ", "Json parsing error: " + e.getMessage());
                               }

                           }
                          bundle = new Bundle();
                           //CHANGE
                           bundle.putString("bind", "The service has bound to one or more clients but idle....");
                           resultReceiver.send(100, bundle);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       } catch (ExecutionException e1) {
                           e1.printStackTrace();
                       }
                   }
                } while (mIDs2.contains(id));

                mIDs2.add(id);

            }
            return l1.toString();
        }
        // Implement the remote method number 3
        public String yearlyAvg(int year) {

            UUID id;
            String response,parsed_response;
            // Acquire lock to ensure exclusive access to mIDs
            // Then examine and modify mIDs
            //to synchronise method 2 invoking threads
            synchronized (mIDs1) {

                do {

                    id = UUID.randomUUID();
                    response="";
                    parsed_response="";
                    //make query for the treasury.io query
                    String queryStr = "SELECT AVG( \"open_mo\") \n" +
                            "FROM\n" +
                            "t1\n" +
                            "WHERE ( \"year\" = '"+year+ "' AND \"account\" = 'Federal Reserve Account'  )";
                    /*Execute URL woth query*/
                    String urlStr = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=" + URLEncoder.encode(queryStr);
                    Bundle bundle = new Bundle();
                    bundle.putString("API", "The service has called API....");
                    //Send status to Status Activity which can display current progress of the service
                    resultReceiver.send(400, bundle);
                    /*Parsing response of JSON to get the integer as space seperated as we cancatenate and space seperate them*/
                    try {
                        //Get response from API based on query .get() so that it waits till we get a String as a JSON Response
                        response= new JsonTask().execute(urlStr).get();
                        try {
                            //used to parse JSON response as here we get as array
                            JSONArray jsonArray = new JSONArray(response);

                                //Get value from column name of table
                                //take out the object inside them and get the Key which is the column and get value which we need from that column as we do get
                                parsed_response=String.valueOf(jsonArray.getJSONObject(0).get("AVG( \"open_mo\")"));

                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    catch (ExecutionException e1){
                        e1.printStackTrace();
                    }
                } while (mIDs1.contains(id));

                mIDs1.add(id);

            }
            //return the parsed response where we have extracted the values
            return parsed_response;
        }
    };

    // Return the Stub defined above
    @Override
    public IBinder onBind(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putString("bind", "The service has binded....");
        //send status of service to StatusActivity
        resultReceiver.send(100, bundle);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putString("unbind", "The service has been Destroyed....");
        //send status of service to StatusActivity
        resultReceiver.send(200, bundle);
        return super.onUnbind(intent);

    }

    @Override
    public void onDestroy() {
        Bundle bundle = new Bundle();
        //send status of service to StatusActivity
        bundle.putString("destroy ", "The service has been Destroyed....");
        resultReceiver.send(300, bundle);
        super.onDestroy();

    }


//To execute API Call
    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();


        }
//execute on Worker thread not diturbig UI
        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


        }
    }
}




