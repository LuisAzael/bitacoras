package com.example.bitacoras2020.Utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.bitacoras2020.Database.DatabaseAssistant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@SuppressLint("Registered")
public abstract class BaseActivity extends MintActivity
{
    private static final String TAG = "BASE_ACTIVITY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int lastTimeStarted = settings.getInt("last_time_started", -1);
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);

        /*if (today != lastTimeStarted) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("last_time_started", today);
            editor.commit();
            Log.d(TAG, "onCreate: Nuevo dia, actualiza la información");

            //Cerrar la sesion de chofer aqui
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            Preferences.setPreferenceCheckinCheckoutAssistant(getApplicationContext(), false, Preferences.PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT);
            String[] coordenadasFromApplication = ApplicationResourcesProvider.getCoordenadasFromApplication();
            String[] dataSessions = DatabaseAssistant.getLastedDataFromSessions();

            try {
                if (coordenadasFromApplication.length > 0 && dataSessions.length > 0) {
                    DatabaseAssistant.insertarSesiones(
                            "" + dataSessions[0],
                            "" + dataSessions[1],
                            "" + dateFormat.format(new Date()),
                            "" + coordenadasFromApplication[0],
                            "" + coordenadasFromApplication[1],
                            "2",
                            "" + timeFormat.format(cal.getTime())
                    );
                    Toast.makeText(getApplicationContext(), "Se cerro tu sesión automaticamente, del día de ayer", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    //onDestroy();
                } else
                    Log.d(TAG, "onCreate: No se encuentran datos de sesiones");
            }catch (Throwable e){
                Log.d(TAG, "onCreate: Error " + e.getMessage());
            }
        }
        else{
            Log.d(TAG, "onCreate: Mismo dia, no se actualiza información");
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }


}


