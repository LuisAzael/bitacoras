package com.example.bitacoras2020.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bitacoras2020.BuildConfig;
import com.example.bitacoras2020.Database.Choferes;
import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.Database.Laboratorios;
import com.example.bitacoras2020.Database.LoginZone;
import com.example.bitacoras2020.Database.Lugares;
import com.example.bitacoras2020.Database.Movimientos;
import com.example.bitacoras2020.Database.Sesiones;
import com.example.bitacoras2020.MainActivity;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;
import com.example.bitacoras2020.Utils.ConstantsBitacoras;
import com.example.bitacoras2020.Utils.GeoFenceHelper;
import com.example.bitacoras2020.Utils.Preferences;
import com.example.bitacoras2020.Utils.VolleySingleton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import soup.neumorphism.NeumorphButton;

public class Login extends AppCompatActivity {

    private static final String TAG = "LOGIN";
    TextInputEditText etContrasena;
    Spinner etUsuario;
    Dialog dialogoError;
    String choferSeleccionado = null;

    //*****
    GeofencingClient geofencingClient;
    GeoFenceHelper geoFenceHelper;
    PendingIntent pendingIntent = null;

    //---Actualizacion
    private boolean updatePostponed = false;
    private Timer timerUpdate;
    private Boolean downloadingApp = false;
    ProgressDialog pDialog;
    NeumorphButton btIniciar;
    Button btActualizar;
    TextInputLayout T2;
    boolean success = false;
    String updatee="";
    ImageView btDescargarTodo;
    TextView tvMensajeLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialogoError = new Dialog(Login.this);
        etUsuario =(Spinner) findViewById(R.id.etUsuario);
        etContrasena =(TextInputEditText) findViewById(R.id.etContrasena);
        btIniciar = (NeumorphButton) findViewById(R.id.btInicarSesion);
        btActualizar = (Button) findViewById(R.id.btActualizar);
        T2 = (TextInputLayout) findViewById(R.id.T2);
        TextView etVersion = (TextView) findViewById(R.id.tvVersion);
        etVersion.setText(BuildConfig.VERSION_NAME);
        btDescargarTodo =(ImageView) findViewById(R.id.btDescargarTodo);
        tvMensajeLoading =(TextView) findViewById(R.id.tvMensajeLoading);


        btDescargarTodo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(ApplicationResourcesProvider.checkInternetConnection())
                    loadInformationAndRequestChoferes();
                else
                    showErrorDialog("No hay conexión a internet");

                return false;
            }
        });

        //requestPlaceAndGeofenceZoneToLogin();

        //Verificar si la lista de choferes se cargo correctamente
        if(DatabaseAssistant.isThereDataChoferes()){
            String nombreChofer = Preferences.getNombreChoferInLogin(Login.this, Preferences.PREFERENCE_NOMBRE_CHOFER);
            if(!nombreChofer.equals("")){
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.getAllChoferes());
                etUsuario.setAdapter(adapter);

                etUsuario.setSelection(adapter.getPosition(nombreChofer));
            }else {
                ArrayAdapter<String> adapterColonias = new ArrayAdapter<String>(Login.this, R.layout.style_spinner, DatabaseAssistant.getAllChoferes());
                etUsuario.setAdapter(adapterColonias);
            }
        }
        else{
            if(ApplicationResourcesProvider.checkInternetConnection())
                loadInformationAndRequest();
            else {
                String nombreChofer = Preferences.getNombreChoferInLogin(Login.this, Preferences.PREFERENCE_NOMBRE_CHOFER);
                if(!nombreChofer.equals("")){
                    //String [] nombreChoferArray = {nombreChofer};
                    //ArrayAdapter<String> adapterColonias = new ArrayAdapter<String>(Login.this, android.R.layout.simple_dropdown_item_1line, nombreChoferArray);
                    //etUsuario.setAdapter(adapterColonias);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.getAllChoferes());
                    etUsuario.setAdapter(adapter);
                    etUsuario.setSelection(adapter.getPosition(nombreChofer));


                }else {
                    showErrorDialog("No hay conexión a internet");
                    //mostrar aqui en aviso de cargar de nuevo el load
                }

            }
        }

        btActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInformationAndRequestNew();
            }
        });


        etUsuario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choferSeleccionado = etUsuario.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                choferSeleccionado = null;
            }
        });



        btIniciar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                if(etContrasena.getText().toString().equals("resetDB%")) {
                    DatabaseAssistant.resetDatabase(ApplicationResourcesProvider.getContext());
                    Toast.makeText(getApplicationContext(), "La base de datos fue reseteada con éxito", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (checkFullFields() && etUsuario.getSelectedItem() != "Selecciona tu nombre...")
                    {
                        Preferences.setWithinTheZone(getApplicationContext(), false, Preferences.PREFERENCE_WITHIN_THE_ZONE_TO_LOGIN);

                        if(ApplicationResourcesProvider.checkInternetConnection()) {
                            showMyCustomDialog();
                            tvMensajeLoading.setText("Cargando zonas...");
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    try {

                                        if(checkGPSConnection())
                                            requestPlaceAndGeofenceZoneToLogin();

                                        synchronized (this) {
                                            wait(4000);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        tvMensajeLoading.setText("Cangando datos de inicio...");
                                                        if (DatabaseAssistant.iniciarSesionSoloEnBunker())
                                                        {
                                                            if (Preferences.getWithinTheZone(Login.this, Preferences.PREFERENCE_WITHIN_THE_ZONE_TO_LOGIN)){
                                                                loginOnline(true);
                                                            } else {
                                                                showErrorDialog("No puedes hacer tu CKECK_IN en esta zona, necesitas estar en la zona indicada por tu coordinador");
                                                                dismissMyCustomDialog();
                                                            }
                                                        } else {
                                                            loginOnline(false);
                                                        }

                                                    } catch (Throwable e) {
                                                        Log.e(TAG, "onClick: " + e.getMessage());
                                                        Toast.makeText(getApplicationContext(), "Ocurrio un error al iniciar sesión, intenta nuevamente." + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        dismissMyCustomDialog();
                                                    }


                                                }
                                            });

                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        Log.e(TAG, "onClick: " + e.getMessage());
                                        Toast.makeText(getApplicationContext(), "Ocurrio un error al iniciar sesión, intenta nuevamente." + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        dismissMyCustomDialog();
                                    }
                                };
                            };
                            thread.start();
                        }else {
                            loginOffline("0");
                            dismissMyCustomDialog();
                        }
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Verifica los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void requestPlaceAndGeofenceZoneToLogin() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsBitacoras.WS_CONFIGURATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    LoginZone.deleteAll(LoginZone.class);
                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArrayConfig = new JSONArray();
                    jsonArrayConfig = json.getJSONArray("config");

                    for (int i = 0; i <= jsonArrayConfig.length() - 1; i++) {

                        try{
                            DatabaseAssistant.insertarLoginZone(
                                    jsonArrayConfig.getJSONObject(i).getString("start_session_blocked"),
                                    jsonArrayConfig.getJSONObject(i).getString("start_session_place"),
                                    jsonArrayConfig.getJSONObject(i).getString("start_session_lat"),
                                    jsonArrayConfig.getJSONObject(i).getString("start_session_lng"),
                                    jsonArrayConfig.getJSONObject(i).getString("end_session_place"),
                                    jsonArrayConfig.getJSONObject(i).getString("end_session_lat"),
                                    jsonArrayConfig.getJSONObject(i).getString("end_session_lng"),
                                    jsonArrayConfig.getJSONObject(i).getString("end_session_blocked")
                            );

                            /*DatabaseAssistant.insertarLoginZone(
                                    "1",
                                    "Bunker",
                                    "311212123",
                                    "3212123",
                                    jsonArrayConfig.getJSONObject(i).getString("end_session_place"),
                                    jsonArrayConfig.getJSONObject(i).getString("end_session_lat"),
                                    jsonArrayConfig.getJSONObject(i).getString("end_session_lng"),
                                    jsonArrayConfig.getJSONObject(i).getString("end_session_blocked")
                            );*/

                            String latitud = jsonArrayConfig.getJSONObject(i).getString("start_session_lat");
                            String longitud = jsonArrayConfig.getJSONObject(i).getString("start_session_lng");
                            //String latitud = "20.65610000";
                            //String longitud = "-103.34670000";
                            if(!latitud.equals("") && !longitud.equals("")) {
                                LatLng coordenadasToGeofenceInLogin = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
                                addGeofenceToLogin(coordenadasToGeofenceInLogin);
                                Log.d(TAG, "requestPlaceAndGeofenceZoneToLogin: onResponse:  se envio a geofence");
                            }
                            else
                                Log.d(TAG, "requestPlaceAndGeofenceZoneToLogin: onResponse: latitud y longitud estan vacios");
                        }catch (Throwable e){
                            Log.e(TAG, "requestPlaceAndGeofenceZoneToLogin: onResponse: " + e.getMessage());
                        }

                        try {
                            updatee = jsonArrayConfig.getJSONObject(i).getString("update");
                        }catch (Throwable e) {
                            Log.e(TAG, "onResponse: " + e.getMessage());
                            updatee = "0";
                        }

                        if (!downloadingApp) {
                            try {
                                int checkUpdate = Integer.parseInt(jsonArrayConfig.getJSONObject(i).getString("update"));
                                Log.d("UpdateCheck", "Debe actualizar: " + (checkUpdate == 1 ? "Si" : "No"));
                                if (checkUpdate == 1 && !updatePostponed)
                                    checkVersion();
                                else if (checkUpdate == 2)
                                    forceUpdate();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(80000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void requestForCheckUpdateApp() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsBitacoras.WS_CONFIGURATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArrayConfig = new JSONArray();
                    jsonArrayConfig = json.getJSONArray("config");
                    for (int i = 0; i <= jsonArrayConfig.length() - 1; i++) {
                        if (!downloadingApp) {
                            try {
                                int checkUpdate = Integer.parseInt(jsonArrayConfig.getJSONObject(i).getString("update"));
                                Log.d("UpdateCheck", "Debe actualizar: " + (checkUpdate == 1 ? "Si" : "No"));
                                if (checkUpdate == 1 && !updatePostponed)
                                    checkVersion();
                                else if (checkUpdate == 2)
                                    forceUpdate();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(80000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    public void addGeofenceToLogin(LatLng latLng)
    {
        geofencingClient = LocationServices.getGeofencingClient(this);
        geoFenceHelper = new GeoFenceHelper(this);

        Geofence geofence = geoFenceHelper.getGeoFence(
                "LOGIN_ZONE",
                latLng,
                100,
                Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_DWELL
                        | Geofence.GEOFENCE_TRANSITION_EXIT
        );

        GeofencingRequest geofencingRequest = geoFenceHelper.getGeoFencingRequest(geofence);
        pendingIntent = geoFenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.v(TAG, "onSuccess, GeoFence add success...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geoFenceHelper.getErrorString(e);
                        Log.e(TAG, "Error en addOnFailureListener() " + errorMessage);
                    }
                });
    }

    private void loginOffline(String isBunker) {
        Toast.makeText(getApplicationContext(), "Iniciando modo offline", Toast.LENGTH_SHORT).show();
        if (checkGPSConnection()) {
            if (checkFullFields()) {
                validarLoginOffline(isBunker);
            }
            else
                Toast.makeText(getApplicationContext(), "Completa los campos", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Enciende tu GPS para continuar", Toast.LENGTH_SHORT).show();
    }


    private void validarLoginOffline(String isBunker)
    {
        if(DatabaseAssistant.isThereLastedDataFromSessions(choferSeleccionado, etContrasena.getText().toString()))
        {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            Preferences.setPreferenceCheckinCheckoutAssistant(getApplicationContext(), true, Preferences.PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT);
            Preferences.setIsProveedor(getApplicationContext(), false, Preferences.PREFERENCE_IS_PROVEEDOR);
            Intent intent = null;
            String[] coordenadasFromApplication = ApplicationResourcesProvider.getCoordenadasFromApplication();

            if (coordenadasFromApplication.length > 0 && coordenadasFromApplication != null) {

                DatabaseAssistant.insertarSesiones(
                        ""+ choferSeleccionado,
                        "" + etContrasena.getText().toString(),
                        ""+ dateFormat.format(new Date()),
                        ""+ coordenadasFromApplication[0],
                        ""+ coordenadasFromApplication[1],
                        "1",
                        "" + timeFormat.format(cal.getTime()),
                        "0",
                        isBunker,
                        "0",
                        Preferences.getGeofenceActual(getApplicationContext(), Preferences.PREFERENCE_GEOFENCE_ACTUAL),
                        "" + DatabaseAssistant.getLastIsFuneraria()
                );

                if(DatabaseAssistant.getLastIsFuneraria().equals("1"))
                    intent = new Intent(getApplicationContext(), PersonalFuneraria.class);
                else if (DatabaseAssistant.getLastIsFuneraria().equals("0"))
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                else
                    intent = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(intent);
                if(geofencingClient!=null)
                    geofencingClient.removeGeofences(pendingIntent);

                finish();

            }else{
                Toast.makeText(getApplicationContext(), "Verifica que tu GPS este encendido o reinicia la aplicación", Toast.LENGTH_SHORT).show();
            }
        }
        else
            showErrorDialog("Usuario o contraseña incorrectos, por favor verifica nuevamente.");
    }

    private void loginOnline(boolean isBunker) {
        if (checkGPSConnection()) {
            if (checkFullFields()) {
                createJsonParametersForLoginOnline(choferSeleccionado, etContrasena.getText().toString(), isBunker);
            }else
                Toast.makeText(this, "Verifica que esten llenos los campos", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(this, "Activa el GPS para continuar", Toast.LENGTH_SHORT).show();
    }

    public boolean checkGPSConnection()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Activa el GPS para continuar", Toast.LENGTH_SHORT).show();
        } else {
            final LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return  false;
    }

    private boolean checkFullFields() {
        return (choferSeleccionado != null || !choferSeleccionado.equals("")) && !etContrasena.getText().toString().equals("");
    }



    @SuppressLint("HardwareIds")
    public void createJsonParametersForLoginOnline(String codigo, String password, boolean isBunker) {
        JSONObject jsonParams = new JSONObject();
        String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        try {
            jsonParams.put("usuario", codigo);
            jsonParams.put("pass", password);
            jsonParams.put("fecha", dateFormat.format(new Date()));
            jsonParams.put("hora",  timeFormat.format(cal.getTime()));
            jsonParams.put("lat", arregloCoordenadas[0]);
            jsonParams.put("lng", arregloCoordenadas[1]);
            jsonParams.put("tipo", "1");
            jsonParams.put("isBunker", isBunker ? "1": "0");
            jsonParams.put("geoFenceActual", Preferences.getGeofenceActual(Login.this, Preferences.PREFERENCE_GEOFENCE_ACTUAL));
            jsonParams.put("token_device", DatabaseAssistant.getTokenDeUsuario());

            requestLoginOnline(jsonParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void requestLoginOnline(JSONObject jsonParams)
    {
        btIniciar.setEnabled(false);
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_LOGIN, jsonParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (response.getBoolean("Success")) {

                                            try {

                                                DatabaseAssistant.insertarSesiones(
                                                        "" + response.getJSONObject("login").getString("usuario"),
                                                        "" + response.getJSONObject("login").getString("pass"),
                                                        "" + response.getJSONObject("login").getString("fecha"),
                                                        "" + response.getJSONObject("login").getString("lat"),
                                                        "" + response.getJSONObject("login").getString("lng"),
                                                        "" + response.getJSONObject("login").getString("tipo"),
                                                        "" + response.getJSONObject("login").getString("hora"),
                                                        "1",
                                                        "" + response.getJSONObject("login").getString("isBunker"),
                                                        "0",
                                                        "" + response.getJSONObject("login").getString("geoFenceActual"),
                                                        "0" //+ response.getJSONObject("login").getString("isFuneraria")
                                                );

                                                success = true;

                                                //******************* LABORATORIOS **************************
                                                if (response.has("laboratorios")) {
                                                    try {
                                                        Laboratorios.deleteAll(Laboratorios.class);
                                                        JSONArray jsonArrayLaboratorios = new JSONArray();
                                                        jsonArrayLaboratorios = response.getJSONArray("laboratorios");
                                                        for(int i =0; i<=jsonArrayLaboratorios.length()-1;i++){
                                                            DatabaseAssistant.insertarLaboratorios(
                                                                    "" + jsonArrayLaboratorios.getJSONObject(i).getString("name"),
                                                                    "" + jsonArrayLaboratorios.getJSONObject(i).getString("status"),
                                                                    "" + jsonArrayLaboratorios.getJSONObject(i).getString("id")
                                                            );
                                                        }
                                                        success = true;
                                                    } catch (Throwable e) {
                                                        Log.e(TAG, "createJsonParametersForLoginOnline: " + e.getMessage());
                                                        success = false;
                                                        dismissMyCustomDialog();
                                                        Toast.makeText(getApplicationContext(), "Ocurrio un error al iniciar sesión, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                                                    }

                                                } else {
                                                    success = false;
                                                    Log.e(TAG, "run: no es success");
                                                    dismissMyCustomDialog();
                                                    Toast.makeText(getApplicationContext(), "Ocurrio un error al iniciar sesión, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                                                }
                                                //******************************************************




                                            } catch (Throwable e) {
                                                Log.e(TAG, "createJsonParametersForLoginOnline: " + e.getMessage());
                                                success = false;
                                                dismissMyCustomDialog();
                                                Toast.makeText(getApplicationContext(), "Ocurrio un error al iniciar sesión, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                                            }




                                        } else {
                                            success = false;
                                            Log.e(TAG, "run: no es success");
                                            dismissMyCustomDialog();
                                            Toast.makeText(getApplicationContext(), "Ocurrio un error al iniciar sesión, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.e(TAG, "onResponse: " + e.getMessage());
                                        success = false;
                                        dismissMyCustomDialog();
                                        Toast.makeText(getApplicationContext(), "Ocurrio un error al iniciar sesión, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            synchronized (this) {
                                wait(2000);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (success) {
                                            try {
                                                Preferences.setPreferenceCheckinCheckoutAssistant(Login.this, true, Preferences.PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT);
                                                Intent intent = null;

                                                if(DatabaseAssistant.getLastIsFuneraria().equals("1"))
                                                    intent = new Intent(getApplicationContext(), PersonalFuneraria.class);
                                                else if (DatabaseAssistant.getLastIsFuneraria().equals("0"))
                                                    intent = new Intent(getApplicationContext(), MainActivity.class);
                                                else
                                                    intent = new Intent(getApplicationContext(), MainActivity.class);


                                                if (geofencingClient != null)
                                                    geofencingClient.removeGeofences(pendingIntent);

                                                Preferences.setNombreChoferInLogin(Login.this, jsonParams.getString("usuario"), Preferences.PREFERENCE_NOMBRE_CHOFER);
                                                Preferences.setIsProveedor(getApplicationContext(), false, Preferences.PREFERENCE_IS_PROVEEDOR);

                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                                Log.v(TAG, "onResponse: Inicio de sesion correcto");

                                            } catch (JSONException e) {
                                                Log.e(TAG, "run: Error en inicio1: " + e.getMessage());
                                                Log.e(TAG, "run: Error en inicio2: " + e.getLocalizedMessage());
                                                btIniciar.setEnabled(true);
                                                dismissMyCustomDialog();
                                                Toast.makeText(getApplicationContext(), "Ocurrio un error al iniciar sesión, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            showErrorDialog("Usuario o contraseña incorrectos, por favor verifica nuevamente.");
                                            btIniciar.setEnabled(true);
                                            dismissMyCustomDialog();
                                            Toast.makeText(getApplicationContext(), "Ocurrio un error al iniciar sesión, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.e(TAG, "run: Error en inicio3: " + e.getMessage());
                            Log.e(TAG, "run: Error en inicio4: " + e.getLocalizedMessage());
                            btIniciar.setEnabled(true);
                            dismissMyCustomDialog();
                            Toast.makeText(getApplicationContext(), "Ocurrio un error al iniciar sesión, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    ;
                };
                thread.start();

            }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e(TAG, "run: Error en inicio5: " + error.getMessage());
                    Log.e(TAG, "run: Error en inicio6: " + error.getLocalizedMessage());
                    btIniciar.setEnabled(true);
                    dismissMyCustomDialog();
                    Toast.makeText(getApplicationContext(), "Ocurrio un error al iniciar sesión, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                }
            }) {

        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(80000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }


    public void showErrorDialog(final String codeError) {
        final NeumorphButton btNo, btSi;
        TextView tvCodeError;
        dialogoError.setContentView(R.layout.layout_error);
        dialogoError.setCancelable(false);
        btNo = (NeumorphButton) dialogoError.findViewById(R.id.btNo);
        btSi = (NeumorphButton) dialogoError.findViewById(R.id.btSi);
        tvCodeError = (TextView) dialogoError.findViewById(R.id.tvCodeError);
        tvCodeError.setText(codeError);

        if (codeError.equals("No tienes ningúna bitácora activa") || codeError.equals("No hay conexión a internet")
                || codeError.equals("Ya tienes una salida actualmente, debes registrar una llegada al destino.")
                || codeError.equals("Ya tienes una llegada actualmente, debes registrar una salida.")
                || codeError.equals("No puedes hacer tu CKECK_IN en esta zona, necesitas estar en la zona indicada por tu coordinador")
                || codeError.equals("Usuario o contraseña incorrectos, por favor verifica nuevamente.")
        ) {
            btSi.setVisibility(View.GONE);
            btNo.setText("Aceptar");
        }

        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoError.dismiss();
            }
        });

        btSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoError.dismiss();
                Intent intent = new Intent(getApplicationContext(), NuevaBitacora.class);
                intent.putExtra("tipo", "2");
                startActivity(intent);
            }
        });


        dialogoError.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogoError.show();

    }


    /*private void downloadChoferesAndAyudantes() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsBitacoras.WS_DRIVERS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Choferes.deleteAll(Choferes.class);
                JSONArray jsonArrayChoferes = new JSONArray();
                try {
                    JSONObject json = new JSONObject(response);
                    jsonArrayChoferes = json.getJSONArray("drivers");

                    for (int i = 0; i <= jsonArrayChoferes.length() - 1; i++) {
                        DatabaseAssistant.insertarChoferes(
                                "" + jsonArrayChoferes.getJSONObject(i).getString("name"),
                                "" + jsonArrayChoferes.getJSONObject(i).getString("status"),
                                "" + jsonArrayChoferes.getJSONObject(i).getString("id")
                        );
                    }
                    Log.i("SINGLEX", response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }*/

    private void downloadChoferesAndAyudantes()
    {
        JSONObject params = new JSONObject();
        try {
            params.put("usuario", DatabaseAssistant.getUserNameFromSesiones() );
            params.put("token_device", DatabaseAssistant.getTokenDeUsuario());
            params.put("isProveedor", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_DRIVERS_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                Choferes.deleteAll(Choferes.class);
                JSONArray jsonArrayChoferes = new JSONArray();
                try {

                    jsonArrayChoferes = response.getJSONArray("drivers");

                    for (int i = 0; i <= jsonArrayChoferes.length() - 1; i++) {
                        DatabaseAssistant.insertarChoferes(
                                "" + jsonArrayChoferes.getJSONObject(i).getString("name"),
                                "" + jsonArrayChoferes.getJSONObject(i).getString("status"),
                                "" + jsonArrayChoferes.getJSONObject(i).getString("id")
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
        };
       // postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }




   /* private void downloadPlaces() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsBitacoras.WS_PLACES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Lugares.deleteAll(Lugares.class);
                JSONArray jsonArrayPlaces = new JSONArray();
                try {
                    JSONObject json = new JSONObject(response);
                    jsonArrayPlaces = json.getJSONArray("places");

                    for (int i = 0; i <= jsonArrayPlaces.length() - 1; i++) {
                        DatabaseAssistant.insertarLugares(
                                "" + jsonArrayPlaces.getJSONObject(i).getString("name"),
                                "" + jsonArrayPlaces.getJSONObject(i).getString("status"),
                                "" + jsonArrayPlaces.getJSONObject(i).getString("latitud"),
                                "" + jsonArrayPlaces.getJSONObject(i).getString("longitud"),
                                "" + jsonArrayPlaces.getJSONObject(i).getString("perimetro"),
                                "" + jsonArrayPlaces.getJSONObject(i).getString("id")
                        );
                    }
                    Log.i("SINGLEX", response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }*/



    private void downloadPlaces()
    {
        JSONObject params = new JSONObject();
        try {
            params.put("usuario", DatabaseAssistant.getUserNameFromSesiones() );
            params.put("token_device", DatabaseAssistant.getTokenDeUsuario());
            params.put("isProveedor", DatabaseAssistant.getIsProveedor());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_PLACES_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                Lugares.deleteAll(Lugares.class);
                JSONArray jsonArrayPlaces = new JSONArray();
                try {
                    jsonArrayPlaces = response.getJSONArray("places");

                    for (int i = 0; i <= jsonArrayPlaces.length() - 1; i++) {
                        DatabaseAssistant.insertarLugares(
                                "" + jsonArrayPlaces.getJSONObject(i).getString("name"),
                                "" + jsonArrayPlaces.getJSONObject(i).getString("status"),
                                "" + jsonArrayPlaces.getJSONObject(i).getString("latitud"),
                                "" + jsonArrayPlaces.getJSONObject(i).getString("longitud"),
                                "" + jsonArrayPlaces.getJSONObject(i).getString("perimetro"),
                                "" + jsonArrayPlaces.getJSONObject(i).getString("id")
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
        };
        //postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }


    private void showMyCustomDialog(){
        final FrameLayout flLoading = (FrameLayout) findViewById(R.id.layoutCargando);
        flLoading.setVisibility(View.VISIBLE);
    }

    private void dismissMyCustomDialog(){
        final FrameLayout flLoading = (FrameLayout) findViewById(R.id.layoutCargando);
        flLoading.setVisibility(View.GONE);
    }



    private void checkVersion() {
        StringRequest request = new StringRequest(Request.Method.GET, ConstantsBitacoras.URL_CHECK_VERSION, new Response.Listener<String>()
        {
            @Override
            public void onResponse(final String response)
            {
                try {
                    PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
                    String version = info.versionName;

                    int comparison = version.compareTo(response);

                    Log.d("UpdateComparison", "Comparison: " + comparison);

                    if (!version.equals(response) && comparison < 0)
                    {
                        if(getApplicationContext() != null)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

                            builder.setTitle("Actualización");
                            builder.setMessage("Hay una nueva versión de la aplicación lista para descargar e instalar. \n\nVersión actual: " + version + "\n\nVersión nueva: " + response);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Actualizar", (dialog1, which) -> {
                                String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                                String fileName = "eBita.apk";
                                final Uri uri = Uri.parse("file://" + destination + fileName);

                                showMyCustomDialog();

                                File file = new File(destination, fileName);

                                if (file.exists())
                                    file.delete();

                                DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(ConstantsBitacoras.URL_DOWNLOAD_APK + response + ".apk"));
                                downloadRequest.setDescription("Descargando eBita");
                                downloadRequest.setTitle("Actualización eBita");

                                downloadRequest.setDestinationUri(uri);
                                downloadRequest.allowScanningByMediaScanner();
                                downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                final DownloadManager manager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                if (manager != null) {
                                    final long downloadId = manager.enqueue(downloadRequest);

                                    BroadcastReceiver onComplete = new BroadcastReceiver() {
                                        @Override
                                        public void onReceive(Context context, Intent intent) {
                                            if (file.exists()) {
                                                Uri apkUri = Uri.fromFile(file);
                                                Intent update = new Intent();
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                    apkUri = FileProvider.getUriForFile(getApplicationContext(), com.example.bitacoras2020.BuildConfig.APPLICATION_ID + ".provider", file);
                                                    update.setAction(Intent.ACTION_INSTALL_PACKAGE);
                                                    update.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                                    update.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                    startActivity(update);
                                                } else {
                                                    update.setAction(Intent.ACTION_VIEW);
                                                    update.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    update.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                                    startActivity(update);
                                                }

                                                Log.i("ApkUpdate", manager.getMimeTypeForDownloadedFile(downloadId));

                                                getApplicationContext().unregisterReceiver(this);
                                                Login.this.finish();
                                            } else {
                                                dismissMyCustomDialog();
                                                timerUpdate = new Timer();

                                                manager.remove(downloadId);

                                                timerUpdate.schedule(new TimerTask() {
                                                    @Override
                                                    public void run() {
                                                        updatePostponed = false;
                                                    }
                                                }, (1000 * 60) * 60);
                                                updatePostponed = true;
                                                Toast.makeText(getApplicationContext(), "No se encontro el archivo de actualizacion", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    };

                                    getApplicationContext().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                }
                            });
                            builder.setNegativeButton("Posponer 1 hora", (dialog1, which) -> {
                                timerUpdate = new Timer();

                                timerUpdate.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        updatePostponed = false;
                                        Log.i("UpdatePostponed", "false");
                                    }
                                }, (1000 * 60) * 60);

                                updatePostponed = true;

                                Log.i("UpdatePostponed", "true");
                            });

                            builder.show();
                        }
                    }
                } catch (PackageManager.NameNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }
        );
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(request);
    }




    private void forceUpdate() {
        StringRequest request = new StringRequest(Request.Method.GET, ConstantsBitacoras.URL_CHECK_VERSION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                downloadingApp = true;
                pDialog = new ProgressDialog(Login.this);
                pDialog.setMessage("Descargando, espera porfavor...");
                pDialog.setCancelable(false);
                pDialog.show();

                String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                String fileName = "eBita.apk";
                final Uri uri = Uri.parse("file://" + destination + fileName);

                //showMyCustomDialog();

                File file = new File(destination, fileName);

                if (file.exists())
                    file.delete();

                DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(ConstantsBitacoras.URL_DOWNLOAD_APK + response + ".apk"));
                downloadRequest.setTitle("Actualización eBita");

                downloadRequest.setDestinationUri(uri);
                downloadRequest.allowScanningByMediaScanner();
                downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                if (manager != null) {
                    final long downloadId = manager.enqueue(downloadRequest);

                    BroadcastReceiver onComplete = new BroadcastReceiver() {

                        File file;

                        @Override
                        public void onReceive(Context context, Intent intent) {

                            if (file.exists()) {
                                Uri apkUri = Uri.fromFile(file);
                                Intent update = new Intent();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    apkUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
                                    update.setAction(Intent.ACTION_INSTALL_PACKAGE);
                                    update.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                    update.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(update);
                                } else {
                                    update.setAction(Intent.ACTION_VIEW);
                                    update.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    update.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                    startActivity(update);
                                }

                                Log.i("ApkUpdate", manager.getMimeTypeForDownloadedFile(downloadId));

                                downloadingApp = false;
                                if(pDialog != null)
                                    pDialog.dismiss();
                                unregisterReceiver(this);
                                //finish();
                            } else {
                                //dismissMyCustomDialog();
                                downloadingApp = false;
                                if(pDialog != null)
                                    pDialog.dismiss();
                                timerUpdate = new Timer();

                                manager.remove(downloadId);

                                timerUpdate.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        updatePostponed = false;
                                    }
                                }, (1000 * 60) * 60);
                                updatePostponed = true;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "No se encontro el archivo de actualización", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }

                        public BroadcastReceiver setData(File file) {
                            this.file = file;
                            return this;
                        }
                    }.setData(file);

                    registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(request);
    }


    private void loadInformationAndRequest() {
        if(ApplicationResourcesProvider.checkInternetConnection()) {
            showMyCustomDialog();
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        downloadChoferesAndAyudantes();
                        downloadPlaces();
                        //requestPlaceAndGeofenceZoneToLogin();
                        synchronized (this) {
                            wait(4000);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(DatabaseAssistant.isThereDataChoferes()) {
                                        ArrayAdapter<String> adapterColonias = new ArrayAdapter<String>(Login.this, R.layout.style_spinner, DatabaseAssistant.getAllChoferes());
                                        etUsuario.setAdapter(adapterColonias);

                                        btActualizar.setVisibility(View.GONE);
                                        etUsuario.setVisibility(View.VISIBLE);
                                        T2.setVisibility(View.VISIBLE);
                                        btIniciar.setVisibility(View.VISIBLE);
                                    }else{
                                        btActualizar.setVisibility(View.VISIBLE);
                                        etUsuario.setVisibility(View.GONE);
                                        T2.setVisibility(View.GONE);
                                        btIniciar.setVisibility(View.GONE);
                                    }
                                    dismissMyCustomDialog();
                                }
                            });

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
            };
            thread.start();
        }else
            showErrorDialog("No hay conexión a internet");
    }


    private void loadInformationAndRequestChoferes() {
        if(ApplicationResourcesProvider.checkInternetConnection()) {
            showMyCustomDialog();
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        downloadChoferesAndAyudantes();
                        synchronized (this) {
                            wait(3000);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   try {
                                       ArrayAdapter<String> adapterColonias = new ArrayAdapter<String>(Login.this, R.layout.style_spinner, DatabaseAssistant.getAllChoferes());
                                       etUsuario.setAdapter(adapterColonias);
                                       btActualizar.setVisibility(View.GONE);
                                       etUsuario.setVisibility(View.VISIBLE);
                                       T2.setVisibility(View.VISIBLE);
                                       btIniciar.setVisibility(View.VISIBLE);
                                       btDescargarTodo.setImageResource(R.drawable.action_done);
                                   }catch (Throwable e){
                                       Log.e(TAG, "run: " + e.getMessage());
                                   }

                                    dismissMyCustomDialog();
                                }
                            });

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
            };
            thread.start();
        }else
            showErrorDialog("No hay conexión a internet");
    }

    private void loadInformationAndRequestNew() {
        if(ApplicationResourcesProvider.checkInternetConnection()) {
            showMyCustomDialog();
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        downloadChoferesAndAyudantes();
                        downloadPlaces();
                        //requestPlaceAndGeofenceZoneToLogin();
                        synchronized (this) {
                            wait(8000);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //**********

                                    //Verificar si la lista de choferes se cargo correctamente
                                    if(DatabaseAssistant.isThereDataChoferes()){
                                        String nombreChofer = Preferences.getNombreChoferInLogin(Login.this, Preferences.PREFERENCE_NOMBRE_CHOFER);
                                        if(!nombreChofer.equals("")){
                                            //String [] nombreChoferArray = {nombreChofer};
                                            //ArrayAdapter<String> adapterColonias = new ArrayAdapter<String>(Login.this, android.R.layout.simple_dropdown_item_1line, nombreChoferArray);
                                            //etUsuario.setAdapter(adapterColonias);

                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.getAllChoferes());
                                            etUsuario.setAdapter(adapter);
                                            etUsuario.setSelection(adapter.getPosition(nombreChofer));

                                        }else {
                                            ArrayAdapter<String> adapterColonias = new ArrayAdapter<String>(Login.this, R.layout.style_spinner, DatabaseAssistant.getAllChoferes());
                                            etUsuario.setAdapter(adapterColonias);
                                        }
                                        btActualizar.setVisibility(View.GONE);
                                        etUsuario.setVisibility(View.VISIBLE);
                                        T2.setVisibility(View.VISIBLE);
                                        btIniciar.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        btActualizar.setVisibility(View.VISIBLE);
                                        etUsuario.setVisibility(View.GONE);
                                        T2.setVisibility(View.GONE);
                                        btIniciar.setVisibility(View.GONE);
                                    }
                                    //*************************
                                    dismissMyCustomDialog();
                                }
                            });

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
            };
            thread.start();
        }else
            showErrorDialog("No hay conexión a internet");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //requestForCheckUpdateApp();

        //if(checkGPSConnection() && ApplicationResourcesProvider.checkInternetConnection())
          //  requestPlaceAndGeofenceZoneToLogin();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext()).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(
                                        Login.this,
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                                Log.e(TAG, "onComplete: error no importante");
                            } catch (ClassCastException e) {
                                Log.e(TAG, "onComplete: error no importante");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            break;
                    }
                }
            }
        });

    }
}