package com.example.harsh.proj5_fedcash;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harsh.KeyCommon.KeyGenerator;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;



public class MainActivity extends Activity {

    Spinner spinner1;
    EditText editText,year,workingdays;
    Calendar myCalendar;
    Button btn;
    protected static final String TAG = "KeyServiceUser";
    private KeyGenerator mKeyGeneratorService;
    private boolean mIsBound = false;
    int methodnumber;
    public TextView output;
    Button unbind;
    //Dynamic list of request and response
    public static ArrayList<String> mRequestArray=new ArrayList<>();
    public static  ArrayList<String> mResponseArray=new ArrayList<>();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    MyThread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        editText=(EditText) findViewById(R.id.editText1);
        year=(EditText) findViewById(R.id.editText2);
        workingdays=(EditText)findViewById(R.id.editText3);
        btn=(Button) findViewById(R.id.go_button);
        output = (TextView) findViewById(R.id.output);
        unbind=(Button) findViewById(R.id.button1);
        final Button goButton = (Button) findViewById(R.id.button);
        /*On click of button submit*/
        goButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //check if Activity bound toService if not it will do so at this time
                if (!mIsBound) {

                    boolean b = false;
                    Intent i = new Intent(KeyGenerator.class.getName());


                    ResolveInfo info = getPackageManager().resolveService(i, MainActivity.this.BIND_AUTO_CREATE);
                    i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

                    b = bindService(i, MainActivity.this.mConnection, Context.BIND_AUTO_CREATE);
                    if (b) {
                        Log.i(TAG, "bindService() succeeded!");
                    } else {
                        Log.i(TAG, "bindService() failed!");
                    }

                }
                if(methodnumber==1 ||methodnumber==3) {
                    if(!year.getText().toString().equals("") &&(Integer.valueOf(year.getText().toString())>2005&&Integer.valueOf(year.getText().toString())<2017)) {
                        mThread = new MyThread(MainActivity.this);
                        mThread.start();
                    }
                    else{
                        year.setError("Enter valid yearr");
                    }
                    }else if(methodnumber==2){
                    if(!workingdays.getText().toString().equals("") &&(Integer.valueOf(workingdays.getText().toString())>4&&Integer.valueOf(workingdays.getText().toString())<26)) {
                        mThread = new MyThread(MainActivity.this);
                        mThread.start();
                    }
                    else{
                        workingdays.setError("Enter valid working Days");
                    }
                }

            }
        });
        unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsBound) {

                    unbindService(MainActivity.this.mConnection);


                }
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
       /*Make list of methods to implement*/
        List<String> list = new ArrayList<String>();
        list.add("monthlyCash");
        list.add("dailyCash");
        list.add("yearlyAvg");
        //set the arrayAdapter with spinner so it becomes a dropdown for the lists created
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
        //button for going to net activity 2
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,APIViewerActivity.class);
                i.putExtra("request",mRequestArray);
                i.putExtra("response",mResponseArray);
                startActivity(i);
            }
        });
        //seledct item in spinner so that we can decide which method to inovke and fields value to take while processing the input
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this,
                                "OnItemSelectedListener : " + adapterView.getItemAtPosition(i).toString(),
                        Toast.LENGTH_SHORT).show();
                if(adapterView.getItemAtPosition(i).toString().equals("monthlyCash")){
                    year.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                    workingdays.setVisibility(View.GONE);
                    methodnumber=1;
                }else if(adapterView.getItemAtPosition(i).toString().equals("dailyCash")){
                    editText.setVisibility(View.VISIBLE);
                   workingdays.setVisibility(View.VISIBLE);
                    year.setVisibility(View.GONE);
                    methodnumber=2;
                }else if(adapterView.getItemAtPosition(i).toString().equals("yearlyAvg")){
                    year.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                    workingdays.setVisibility(View.GONE);
                    methodnumber=3;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        myCalendar = Calendar.getInstance();

//Edit text displays a calendar
        editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {





                final Calendar calendar = Calendar.getInstance();

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day_of_month) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, (month));
                        calendar.set(Calendar.DAY_OF_MONTH, day_of_month);
                        String myFormat = "dd/MM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        int dayofweek=calendar.get(Calendar.DAY_OF_WEEK);
                        if(dayofweek>1 && dayofweek<7)
                        editText.setText(sdf.format(calendar.getTime()));
                        else
                          editText.setText("Select working day.Try again!");
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                Calendar minCal=new GregorianCalendar(2006,Calendar.JANUARY,1);
                while (minCal.get(Calendar.DAY_OF_WEEK) > minCal.getFirstDayOfWeek()+1) {
                    minCal.add(Calendar.DATE, -1); // Substract 1 day until first day of week.
                }
                while (minCal.get(Calendar.DAY_OF_WEEK) < minCal.getFirstDayOfWeek()+1) {
                    minCal.add(Calendar.DATE, +1); // add 1 day until first day of week.
                }
                dialog.getDatePicker().setMinDate(minCal.getTimeInMillis());
                Calendar maxCal=new GregorianCalendar(2016,Calendar.DECEMBER,31);
                while (minCal.get(Calendar.DAY_OF_WEEK) > minCal.getFirstDayOfWeek()+1) {
                    minCal.add(Calendar.DATE, -1); // Substract 1 day until first day of week.
                }
                while (minCal.get(Calendar.DAY_OF_WEEK) < minCal.getFirstDayOfWeek()+1) {
                    minCal.add(Calendar.DATE, +1); // add 1 day until first day of week.
                }
                dialog.getDatePicker().setMaxDate(maxCal.getTimeInMillis());// TODO: used to hide future date,month and year

                dialog.show();
        }

    });



       /* Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.package.address","com.package.address.MainActivity"));

        Intent msgIntent = new Intent(this, SimpleIntentService.class);
msgIntent.putExtra(SimpleIntentService.PARAM_IN_MSG, strInputMsg);
startService(msgIntent);



final Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day_of_month) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, (month+1));
                        calendar.set(Calendar.DAY_OF_MONTH, day_of_month);
                        String myFormat = "dd/MM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        your_edittext.setText(sdf.format(calendar.getTime()));
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());// TODO: used to hide previous date,month and year
                calendar.add(Calendar.YEAR, 0);
                dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());// TODO: used to hide future date,month and year
                dialog.show();


List<Calendar> dayslist= new LinkedList<Calendar>();
            Calendar[] daysArray;
            Calendar cAux = Calendar.getInstance();
            while ( cAux.getTimeInMillis() <= gc.getTimeInMillis()) {
                if (cAux.get(Calendar.DAY_OF_WEEK) != 1) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(cAux.getTimeInMillis());
                    dayslist.add(c);
                }
                cAux.setTimeInMillis(cAux.getTimeInMillis() + (24*60*60*1000));
            }
            daysArray = new Calendar[dayslist.size()];
            for (int i = 0; i<daysArray.length;i++)
            {
                daysArray[i]=dayslist.get(i);
            }
            datePickerDialog.setSelectableDays(daysArray);
        */
    }


    // Bind to KeyGenerator Service
    @Override
    protected void onResume() {
        super.onResume();

        if (!mIsBound) {

            boolean b = false;
            Intent i = new Intent(KeyGenerator.class.getName());

            // UB:  Stoooopid Android API-20 no longer supports implicit intents
            // to bind to a service #@%^!@..&**!@
            // Must make intent explicit or lower target API level to 19.
            ResolveInfo info = getPackageManager().resolveService(i, this.BIND_AUTO_CREATE);
            i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

            b = bindService(i, this.mConnection, Context.BIND_AUTO_CREATE);
            if (b) {
                Log.i(TAG, "bindService() succeeded!");
            } else {
                Log.i(TAG, "bindService() failed!");
            }

        }
    }


    @Override
    protected void onPause() {



        super.onPause();
    }
//service make connection and Bind
    private final ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder iservice) {

            mKeyGeneratorService = KeyGenerator.Stub.asInterface(iservice);

            mIsBound = true;

        }
//service disconnects
        public void onServiceDisconnected(ComponentName className) {

            mKeyGeneratorService = null;

            mIsBound = false;

        }
    };
    //On Activity Start Connect with server of AIDL which is TreasuryServ Service here

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "KeyServiceUser Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.harsh.proj5_fedcash/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }
//DIsconnect with Server of AIDL on stol
    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "KeyServiceUser Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.harsh.proj5_fedcash/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    public class MyThread extends Thread
    {
        public Handler  myThreadHandler;
        Activity        mainActivity;


        public MyThread ( MainActivity anActivity ) {
            this.mainActivity = anActivity;

        }

        @Override
        public void run()
        {

            Looper.prepare();
            try {


                // Call KeyGenerator and get a new ID
                //Check if Activity  is bound to service if not it will do so at this time when submit is pressed
                if (mIsBound) {
                        /*If method 1 is selected*/
                    if(methodnumber==1) {
                        //as method 1 is selected invoke our method 1 with help of AIDL using WORKER THREAD REQUIRED
                     final  String response = mKeyGeneratorService.monthlyCash(Integer.parseInt(year.getText().toString()));


                       mRequestArray.add("monthlyCash("+year.getText().toString()+")");

                        try {
                            StringBuffer parsed_response = new StringBuffer();
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0;i<jsonArray.length();i++){

                                parsed_response.append( jsonArray.getJSONObject(i).get("open_mo"));
                                parsed_response.append(" ");
                            }
                            mainActivity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    output.setText(response);
                                }
                            });
                            mResponseArray.add(parsed_response.toString());
                        }
                        catch (JSONException e){

                        }
                    }else   /*If method 2 is selected*/
                        if(methodnumber==2) {
                            //as method 2 is selected invoke our method 2 with help of AIDL using WORKER THREAD REQUIRED
                            String date=editText.getText().toString();
                            if(date!=null ||!date.equals("")) {
                                String dateArr[] = new String[date.length()];

                                dateArr = date.split("/");

                                final String response = mKeyGeneratorService.dailyCash( Integer.valueOf(dateArr[0]), Integer.valueOf( dateArr[1]), Integer.valueOf( dateArr[2]), Integer.valueOf(workingdays.getText().toString()));


                                mRequestArray.add("dailyCash("+dateArr[0]+"," + dateArr[1]+","+  dateArr[2]+","+ Integer.valueOf(workingdays.getText().toString())+")");



                                    mainActivity.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            output.setText(response);
                                        }
                                    });
                                    mResponseArray.add(response.toString());

                            }

                        }
                    else if(methodnumber==3){
                        //as method 3 is selected invoke our method 3 with help of AIDL using WORKER THREAD REQUIRED
                       final String response = mKeyGeneratorService.yearlyAvg(Integer.parseInt(year.getText().toString()));
                        mRequestArray.add("yearlyAvg("+year.getText().toString()+")");
                        mResponseArray.add(response);
                        mainActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                output.setText(response);
                            }
                        });
                    }

                }
                else {
                    Log.i(TAG, "The service was not bound!");
                }

            } catch (RemoteException e) {

                Log.e(TAG, e.toString());

            }

            Looper.loop();
        }
    }
}
