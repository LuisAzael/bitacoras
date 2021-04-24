package com.example.bitacoras2020.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

public class MyLocationService extends BroadcastReceiver
{
    public static final String ACTION_PROCESS_UPDATE="com.example.bitacoras2020.UPDATE_LOCATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null)
        {
            final String action = intent.getAction();
            if(ACTION_PROCESS_UPDATE.equals(action))
            {
                LocationResult result = LocationResult.extractResult(intent);
                if(result!=null)
                {
                    Location location = result.getLastLocation();
                    try
                    {
                        ApplicationResourcesProvider.updateCoordenadasLocation(location);
                    }catch (Throwable e){
                        Log.d("xLOCATIONS_NEW", e.getMessage());
                    }
                }
                else {
                    Location location = null;
                    ApplicationResourcesProvider.updateCoordenadasLocation(location);
                }



            }
        }
    }
}
