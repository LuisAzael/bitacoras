package com.example.bitacoras2020.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bitacoras2020.BuildConfig;
import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.Database.Lugares;
import com.example.bitacoras2020.MainActivity;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;
import com.example.bitacoras2020.Utils.ConstantsBitacoras;
import com.example.bitacoras2020.Utils.Preferences;
import com.example.bitacoras2020.Utils.VolleySingleton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import soup.neumorphism.NeumorphButton;

public class Login_Proveedor extends AppCompatActivity {

    private static final String TAG = "Login_Proveedor";
    TextInputEditText etUsuario, etContrasena;
    NeumorphButton btInicarSesion;
    Button btActualizar;
    TextView tvVersion;
    FrameLayout layoutCargando;
    Dialog dialogoError;
    boolean success = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__proveedor);
        etUsuario =( TextInputEditText) findViewById(R.id.etUsuario);
        etContrasena =( TextInputEditText) findViewById(R.id.etContrasena);
        btInicarSesion =( NeumorphButton) findViewById(R.id.btInicarSesion);
        btActualizar =( Button) findViewById(R.id.btActualizar);
        tvVersion =( TextView) findViewById(R.id.tvVersion);
        dialogoError = new Dialog(Login_Proveedor.this);
        layoutCargando =( FrameLayout) findViewById(R.id.layoutCargando);
        tvVersion.setText(BuildConfig.VERSION_NAME);

        try {
            if (ApplicationResourcesProvider.checkInternetConnection())
                downloadPlaces();
        }catch (Throwable e){
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }


        String nombreChofer = Preferences.getNombreChoferInLogin(Login_Proveedor.this, Preferences.PREFERENCE_NOMBRE_CHOFER);
        if(!nombreChofer.equals("")){
            etUsuario.setText(nombreChofer);
        }
        else{
            try {
                if (!ApplicationResourcesProvider.checkInternetConnection()){
                    showErrorDialog("No hay internet, verifica tu conexión");
                }
            }catch (Throwable e){
                Log.e(TAG, "onCreate: " + e.getMessage() );
            }
        }

        btInicarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFullFields()) {
                    try {
                        if (ApplicationResourcesProvider.checkInternetConnection())
                            loginOnline();
                        else
                            loginOffline();
                    } catch (Throwable e) {
                        Log.e(TAG, "onClick: " + e.getMessage());
                    }
                }
                else
                    Toast.makeText(getApplicationContext(), "Completa los campos", Toast.LENGTH_SHORT).show();
            }
        });
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

        if (codeError.equals("No hay internet, verifica tu conexión")) {
            btSi.setText("Verificar");
            btNo.setText("Salir");
        }

        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoError.dismiss();
                finishAffinity();
            }
        });

        btSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ApplicationResourcesProvider.checkInternetConnection())
                    showErrorDialog("No hay internet, verifica tu conexión");
                else {
                    if(dialogoError.isShowing())
                        dialogoError.dismiss();
                }
            }
        });


        dialogoError.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogoError.show();

    }

    private void showMyCustomDialog(){
        final FrameLayout flLoading = (FrameLayout) findViewById(R.id.layoutCargando);
        flLoading.setVisibility(View.VISIBLE);
    }

    private void dismissMyCustomDialog(){
        final FrameLayout flLoading = (FrameLayout) findViewById(R.id.layoutCargando);
        flLoading.setVisibility(View.GONE);
    }

    private void loginOnline() {
        if (checkGPSConnection())
            createJsonParametersForLoginOnline(etUsuario.getText().toString(), etContrasena.getText().toString());
        else
            Toast.makeText(this, "Activa el GPS para continuar", Toast.LENGTH_SHORT).show();
    }

    private void loginOffline() {
        Toast.makeText(getApplicationContext(), "Iniciando modo offline", Toast.LENGTH_SHORT).show();
        if (checkGPSConnection())
            validarLoginOffline();
        else
            Toast.makeText(getApplicationContext(), "Enciende tu GPS para continuar", Toast.LENGTH_SHORT).show();
    }

    private void validarLoginOffline() {
        if(DatabaseAssistant.isThereLastedDataFromSessions(etUsuario.getText().toString(), etContrasena.getText().toString()))
        {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            Preferences.setPreferenceCheckinCheckoutAssistant(getApplicationContext(), true, Preferences.PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            String[] coordenadasFromApplication = ApplicationResourcesProvider.getCoordenadasFromApplication();
            if (coordenadasFromApplication.length > 0) {
                DatabaseAssistant.insertarSesiones(
                        ""+ etUsuario.getText().toString(),
                        ""+ etContrasena.getText().toString(),
                        ""+ dateFormat.format(new Date()),
                        ""+ coordenadasFromApplication[0],
                        ""+ coordenadasFromApplication[1],
                        "1",
                        "" + timeFormat.format(cal.getTime()),
                        "0",
                        "0",
                        "1",
                        Preferences.getGeofenceActual(getApplicationContext(), Preferences.PREFERENCE_GEOFENCE_ACTUAL),
                        "" + DatabaseAssistant.getLastIsFuneraria()
                );
            }
            Preferences.setIsProveedor(getApplicationContext(), true, Preferences.PREFERENCE_IS_PROVEEDOR);
            startActivity(intent);
            finish();

        }
        else {
            //Toast.makeText(this, "Verifica tu usuario y contraseña", Toast.LENGTH_SHORT).show();
            showErrorDialog("Usuario o contraseña incorrectos, por favor verifica nuevamente.");
        }

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
        return !etUsuario.getText().toString().equals("") && !etContrasena.getText().toString().equals("");
    }

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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void createJsonParametersForLoginOnline(String codigo, String password) {
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
            jsonParams.put("isBunker", "0");
            jsonParams.put("isProveedor", "1");
            jsonParams.put("token_device", DatabaseAssistant.getTokenDeUsuario());
            requestLoginOnline(jsonParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestLoginOnline(JSONObject jsonParams)
    {

        btInicarSesion.setEnabled(false);
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_LOGIN, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showMyCustomDialog();
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    try {
                                        if (response.getBoolean("Success")) {
                                            try {
                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                                                Calendar cal = Calendar.getInstance();

                                                DatabaseAssistant.insertarSesiones(
                                                        "" + jsonParams.getString("usuario"),
                                                        "" + jsonParams.getString("pass"),
                                                        "" + jsonParams.getString("fecha"),
                                                        "" + jsonParams.getString("lat"),
                                                        "" + jsonParams.getString("lng"),
                                                        "1",
                                                        "" + timeFormat.format(cal.getTime()),
                                                        "1",
                                                        jsonParams.getString("isBunker"),
                                                        "1",
                                                        Preferences.getGeofenceActual(getApplicationContext(), Preferences.PREFERENCE_GEOFENCE_ACTUAL),
                                                        "0"
                                                );
                                                success = true;
                                            } catch (Throwable e) {
                                                Log.e(TAG, "createJsonParametersForLoginOnline: " + e.getMessage());
                                                success = false;
                                            }
                                        } else {
                                            success = false;
                                            Log.e(TAG, "run: no es success");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.e(TAG, "onResponse: " + e.getMessage());
                                        success = false;
                                    }
                                    synchronized (this) {
                                        wait(3000);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(success) {
                                                    try {
                                                        Preferences.setPreferenceCheckinCheckoutAssistant(Login_Proveedor.this, true, Preferences.PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT);
                                                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);


                                                        Preferences.setNombreChoferInLogin(Login_Proveedor.this, jsonParams.getString("usuario"), Preferences.PREFERENCE_NOMBRE_CHOFER);
                                                        Preferences.setIsProveedor(getApplicationContext(), true, Preferences.PREFERENCE_IS_PROVEEDOR);
                                                        finish();
                                                        Log.v(TAG, "onResponse: Inicio de sesion correcto");
                                                    } catch (JSONException e) {
                                                        Log.e(TAG, "run: Error en inicio: " + e.getMessage());
                                                        btInicarSesion.setEnabled(true);
                                                    }
                                                }
                                                else {
                                                    showErrorDialog("Usuario o contraseña incorrectos, por favor verifica nuevamente.");
                                                    btInicarSesion.setEnabled(true);
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

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        btInicarSesion.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Ocurrio un error al iniciar sesión, por favor vuelve a intentarlo", Toast.LENGTH_LONG).show();
                    }
                }) {

        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(80000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }
}