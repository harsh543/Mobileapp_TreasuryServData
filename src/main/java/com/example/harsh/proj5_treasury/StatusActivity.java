package com.example.harsh.proj5_treasury;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class StatusActivity extends AppCompatActivity {
  //  String PREF_IS_RUNNING="servicerunning";
    TextView servicestatus;
    MyResultReceiver resultReceiver;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        resultReceiver = new MyResultReceiver(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         servicestatus=(TextView) findViewById(R.id.textView);
        intent = new Intent(this, TreasuryService.class);
        servicestatus.setText("Service is not yet bound");
        //help recieve status of service also
        intent.putExtra("receiver", resultReceiver);
        startService(intent);


    }

    @Override
    protected void onStart() {
        super.onStart();




    }
    @Override
    protected void onResume() {
        super.onResume();

        intent = new Intent(this, TreasuryService.class);
        //help recieve status of service also
        intent.putExtra("receiver", resultReceiver);
        startService(intent);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    //Help to update UI based on values/inputs from Service
    class UpdateUI implements Runnable
    {
        String updateString;

        public UpdateUI(String updateString) {
            this.updateString = updateString;
        }
        public void run() {
            servicestatus.setText(updateString);
        }
    }
    //Helper class to recieve status of service
    class MyResultReceiver extends ResultReceiver
    {
        public MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
          /*Result code helps us seperate values and retrive data and SetText of Status accordingly based on the position Service is in*/
            if(resultCode == 100){
                runOnUiThread(new UpdateUI(resultData.getString("bind")));
            }
            else if(resultCode == 200){
                runOnUiThread(new UpdateUI(resultData.getString("unbind")));

            }else if(resultCode == 500){
                runOnUiThread(new UpdateUI(resultData.getString("destroy")));
            }else if(resultCode == 400){
                runOnUiThread(new UpdateUI(resultData.getString("API")));
            }

        }
    }
    /*
            public static boolean isServiceRunning(Context ctx, String serviceClassName) {
                ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                    if (TrackingService.class.getName().equals(serviceClassName)) {
                        return true;
                    }
                }
                return false;
            }
        */
/*public boolean checkServiceRunning(){
    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
    {        if ("com.example.harsh.proj5_treasury.TreasuryService"
                .equals(service.service.getClassName()))
        {
            return true;
        }
    }
    return false;
}*/
/*public  boolean isRunning() {
    SharedPreferences pref = getSharedPreferences(PREF_IS_RUNNING,Context.MODE_PRIVATE);

    Log.d("Status",String.valueOf(pref.getBoolean(PREF_IS_RUNNING, false)));
    return pref.getBoolean(PREF_IS_RUNNING, false);
}*/

}
