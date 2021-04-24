package com.example.bitacoras2020.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.Firebase.ClickActionHelper;
import com.example.bitacoras2020.MainActivity;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;
import com.example.bitacoras2020.Utils.Preferences;
import com.karan.churi.PermissionManager.PermissionManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SplashScreen extends AppCompatActivity {


    private static final String TAG = "SplashScreen";
    boolean datosDeNoificacion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        try {
            if (getIntent().getExtras() != null) {
                try {
                    JSONObject jsonNotificacion = new JSONObject(getIntent().getExtras().getString("message"));
                    if (jsonNotificacion.has("click_action")) {

                        if (jsonNotificacion.getString("click_action").equals("NOTHING")) {
                            Log.e(TAG, "onCreate: Normal");
                            datosDeNoificacion = false;
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            DatabaseAssistant.insertarNotificacion(
                                    "" + jsonNotificacion.getString("title"),
                                    "" + jsonNotificacion.getString("body"),
                                    "" + jsonNotificacion.getString("click_action"),
                                    "" + jsonNotificacion.getString("bitacora"),
                                    "" + dateFormat.format(new Date())
                            );
                        } else {
                            if (jsonNotificacion.getString("click_action").equals("SPLASH_SCREEN")) {
                                Log.e(TAG, "onCreate: Entra a la activity principal");

                                if (jsonNotificacion.has("bitacora")) {

                                    if (!jsonNotificacion.getString("bitacora").equals("")) {
                                        boolean sesionIniciada = Preferences.getPreferenceCheckinCheckoutAssistant(SplashScreen.this, Preferences.PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT);
                                        if (sesionIniciada) {
                                            Intent intent = new Intent(getBaseContext(), NuevaBitacora.class);
                                            if (jsonNotificacion.has("bitacora")) {
                                                intent.putExtra("bitacora_from_push_notification", jsonNotificacion.getString("bitacora"));
                                            }
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            boolean aplicacionIniciadaAnteriormente = Preferences.getActividadYaIniciada(SplashScreen.this, Preferences.PREFERENCE_ACTIVIDAD_INICIADA);
                                            if (aplicacionIniciadaAnteriormente) {
                                                boolean isProveedor = Preferences.getIsProveedor(SplashScreen.this, Preferences.PREFERENCE_IS_PROVEEDOR);
                                                if (isProveedor) {
                                                    Intent intent = new Intent(getBaseContext(), Login_Proveedor.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                else{
                                                    Intent intent = new Intent(getBaseContext(), Login.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }

                                            }else {
                                                Intent intent = new Intent(getBaseContext(), Welcome.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        }

                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        DatabaseAssistant.insertarNotificacion(
                                                "" + jsonNotificacion.getString("title"),
                                                "" + jsonNotificacion.getString("body"),
                                                "" + jsonNotificacion.getString("click_action"),
                                                "" + jsonNotificacion.getString("bitacora"),
                                                "" + dateFormat.format(new Date())
                                        );
                                        finish();
                                    } else {
                                        Log.e(TAG, "onCreate: Normal");
                                        datosDeNoificacion = false;
                                    }

                                    datosDeNoificacion = true;
                                } else {
                                    Log.e(TAG, "onCreate: Normal");
                                    datosDeNoificacion = false;

                                }
                            } else {
                                Log.e(TAG, "onCreate: Normal");
                                datosDeNoificacion = false;
                            }
                        }
                    } else {
                        Log.e(TAG, "onCreate: Normal");
                        datosDeNoificacion = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    datosDeNoificacion = false;
                }
            } else {
                datosDeNoificacion = false;
            }
        }catch (Throwable e){
            Log.e(TAG, "onCreate: " + e.getMessage());
            datosDeNoificacion = false;
        }

        if (!datosDeNoificacion) {
            setContentView(R.layout.activity_splash_screen);
            boolean permisos_concedidos = Preferences.getPreferencePermissionsBoolean(SplashScreen.this, Preferences.PREFERENCE_REQUEST_PERMISSIONS_GRANTED);

            if (permisos_concedidos) {

                boolean aplicacionIniciadaAnteriormente = Preferences.getActividadYaIniciada(SplashScreen.this, Preferences.PREFERENCE_ACTIVIDAD_INICIADA);
                if (aplicacionIniciadaAnteriormente) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            boolean checkInAlDia = Preferences.getPreferenceCheckinCheckoutAssistant(SplashScreen.this, Preferences.PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT);
                            if (checkInAlDia) {
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                boolean isProveedor = Preferences.getIsProveedor(SplashScreen.this, Preferences.PREFERENCE_IS_PROVEEDOR);
                                if (isProveedor) {
                                    Intent intent = new Intent(getBaseContext(), Login_Proveedor.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Intent intent = new Intent(getBaseContext(), Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                        }
                    }, 3000);
                }
                else {
                    Intent intent = new Intent(getBaseContext(), Welcome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            } else {


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getBaseContext(), RequestPermissionManager.class);
                        startActivity(intent);
                        finish();
                    }
                }, 3000);


            }
        }

    }

}