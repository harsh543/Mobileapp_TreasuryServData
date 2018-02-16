package com.example.harsh.proj5_fedcash;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class APIViewerActivity  extends Activity implements
        RequestFragment.ListSelectionListener {

    public static String[] mTitleArray;
    public static String[] mQuoteArray;


        private final ResponseFragment mResponseFragment = new ResponseFragment();
        private FragmentManager mFragmentManager;
        private FrameLayout mRequestFrameLayout, mResponseFrameLayout;

        private static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
        private static final String TAG = "QuoteViewerActivity";

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");

            super.onCreate(savedInstanceState);

            // Get the string arrays with the titles and responses
            Bundle b = getIntent().getExtras();
            ArrayList request= new ArrayList();
            ArrayList response= new ArrayList();
            if(b!=null) {
                request = (ArrayList)b.getSerializable("request");
                response = (ArrayList)b.getSerializable("response");
            }

            mTitleArray = new String[request.size()];
            mQuoteArray = new String[response.size()];

            for(int i=0; i< request.size(); i++){
                mTitleArray[i]= (String)request.get(i);
                if( response.get(i) instanceof ArrayList ){
                    String s="";
                    for(int j=0;j< ((ArrayList) response.get(i)).size(); j++){
                        s=s+((ArrayList) response.get(i)).get(j)+"\r\n";
                    }
                    mQuoteArray[i]=s;
                }else{
                    mQuoteArray[i]= String.valueOf(response.get(i));//convert the int yearlyAvg to String
                }
            }
            setContentView(R.layout.activity_apiviewer);

            // Get references to the TitleFragment and to the ResponseFragment
            mRequestFrameLayout = (FrameLayout) findViewById(R.id.title_fragment_container);
            mResponseFrameLayout = (FrameLayout) findViewById(R.id.quote_fragment_container);


            // Get a reference to the FragmentManager
            mFragmentManager = getFragmentManager();

            // Start a new FragmentTransaction
            FragmentTransaction fragmentTransaction = mFragmentManager
                    .beginTransaction();

            // Add the TitleFragment to the layout
            fragmentTransaction.add(R.id.title_fragment_container,
                    new RequestFragment());

            // Commit the FragmentTransaction
            fragmentTransaction.commit();

            // Add a OnBackStackChangedListener to reset the layout when the back stack changes
            mFragmentManager
                    .addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                        public void onBackStackChanged() {
                            setLayout();
                        }
                    });
        }

        private void setLayout() {

            // Determine whether the QuoteFragment has been added
            if (!mResponseFragment.isAdded()) {

                // Make the TitleFragment occupy the entire layout
                mRequestFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        MATCH_PARENT, MATCH_PARENT));
                mResponseFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT));
            } else {

                // Make the TitleLayout take 1/3 of the layout's width
                mRequestFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT, 1f));

                // Make the QuoteLayout take 2/3's of the layout's width
                mResponseFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT, 2f));
            }
        }

        // Called when the user selects an item in the RequestFragment
        @Override
        public void onListSelection(int index) {

            // If the QuoteFragment has not been added, add it now
            if (!mResponseFragment.isAdded()) {

                // Start a new FragmentTransaction
                FragmentTransaction fragmentTransaction = mFragmentManager
                        .beginTransaction();

                // Add the QuoteFragment to the layout
                fragmentTransaction.add(R.id.quote_fragment_container,
                        mResponseFragment);

                // Add this FragmentTransaction to the backstack
                fragmentTransaction.addToBackStack(null);

                // Commit the FragmentTransaction
                fragmentTransaction.commit();

                // Force Android to execute the committed FragmentTransaction
                mFragmentManager.executePendingTransactions();
            }

            if (mResponseFragment.getShownIndex() != index) {

                // Tell the QuoteFragment to show the quote string at position index
                mResponseFragment.showQuoteAtIndex(index);

            }
        }

        @Override
        protected void onDestroy() {
            Log.i(TAG, getClass().getSimpleName() + ":entered onDestroy()");
            super.onDestroy();
        }

        @Override
        protected void onPause() {
            Log.i(TAG, getClass().getSimpleName() + ":entered onPause()");
            super.onPause();
        }

        @Override
        protected void onRestart() {
            Log.i(TAG, getClass().getSimpleName() + ":entered onRestart()");
            super.onRestart();
        }

        @Override
        protected void onResume() {
            Log.i(TAG, getClass().getSimpleName() + ":entered onResume()");
            super.onResume();
        }

        @Override
        protected void onStart() {
            Log.i(TAG, getClass().getSimpleName() + ":entered onStart()");
            super.onStart();
        }

        @Override
        protected void onStop() {
            Log.i(TAG, getClass().getSimpleName() + ":entered onStop()");
            super.onStop();
        }

    }




