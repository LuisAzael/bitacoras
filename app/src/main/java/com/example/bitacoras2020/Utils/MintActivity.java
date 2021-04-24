package com.example.bitacoras2020.Utils;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.splunk.mint.Mint;

/**
 * Created by Azael Jimenez on 3/26/15.
 */
public class MintActivity extends AppCompatActivity {

    /**********************************************************************************************
     *
     *                                         Splunk MINT SDK for Android
     *
     **********************************************************************************************/
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(getApplicationContext(), "onCreate", Toast.LENGTH_SHORT).show();
        if (BuildConfig.isIsForSplunkMintMonitoring()){
            Mint.disableNetworkMonitoring();
            Mint.initAndStartSession(this.getApplication(), "ccf95704");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getApplicationContext(), "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(getApplicationContext(), "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT).show();
        //Mint.closeSession(this);
    }
}
