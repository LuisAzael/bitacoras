package com.example.bitacoras2020.Firebase;

import android.util.Log;

import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseService extends FirebaseInstanceIdService
{
    String TAG = "FIREBASE";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refresh Token: "+ token);

        if(token!=null)
            DatabaseAssistant.insertarToken(token);
        else
            DatabaseAssistant.insertarToken("Unknown");
    }

}
