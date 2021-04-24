package com.example.bitacoras2020.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bitacoras2020.Database.Choferes;
import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.Database.LoginZone;
import com.example.bitacoras2020.Database.Lugares;
import com.example.bitacoras2020.MainActivity;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;
import com.example.bitacoras2020.Utils.ConstantsBitacoras;
import com.example.bitacoras2020.Utils.Preferences;
import com.example.bitacoras2020.Utils.VolleySingleton;
import com.google.android.gms.maps.model.LatLng;
import com.karan.churi.PermissionManager.PermissionManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RequestPermissionManager extends AppCompatActivity {
    private static final String TAG = "PERMISION_MANAGER";
    private PermissionManager permissionManager;
    Button btPermisos;
    Dialog dialogoError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permission_manager);
        btPermisos = (Button) findViewById(R.id.btPermisos);
        dialogoError = new Dialog(RequestPermissionManager.this);

        permissionManager = new PermissionManager(){};
        permissionManager.checkAndRequestPermissions(RequestPermissionManager.this);

        if(ApplicationResourcesProvider.checkInternetConnection()) {
            downloadChoferesAndAyudantes();
            downloadPlaces();
            requestPlaceAndGeofenceZoneToLogin();
        }
        else
            showErrorDialog("No hay conexión a internet");


        btPermisos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ApplicationResourcesProvider.checkInternetConnection()) {

                    Dexter.withContext(getApplicationContext())
                            .withPermissions
                                    (Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(
                            new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                        Toast.makeText(RequestPermissionManager.this, "Permisos concedidos", Toast.LENGTH_LONG).show();
                                        Preferences.setPreferencePermissionsBoolean(RequestPermissionManager.this, true, Preferences.PREFERENCE_REQUEST_PERMISSIONS_GRANTED);

                                        boolean checkInAlDia = Preferences.getPreferenceCheckinCheckoutAssistant(RequestPermissionManager.this, Preferences.PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT);
                                        if (checkInAlDia) {
                                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            //Era login antes
                                            Intent intent = new Intent(getBaseContext(), Welcome.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }

                                    } else
                                        Toast.makeText(RequestPermissionManager.this, "Necesitamos los permisos activados", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            }).check();
                }else
                    showErrorDialog("No hay conexión a internet");
            }
        });
    }

    public void showErrorDialog(final String codeError) {
        final Button btNo, btSi;
        TextView tvCodeError;
        dialogoError.setContentView(R.layout.layout_error);
        dialogoError.setCancelable(false);
        btNo = (Button) dialogoError.findViewById(R.id.btNo);
        btSi = (Button) dialogoError.findViewById(R.id.btSi);
        tvCodeError = (TextView) dialogoError.findViewById(R.id.tvCodeError);
        tvCodeError.setText(codeError);

        if (codeError.equals("No hay conexión a internet")){
            btSi.setVisibility(View.GONE);
            btNo.setText("Verificar mas tarde");
        }

        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoError.dismiss();
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
                    jsonArrayChoferes = json.getJSONArray("driverss");

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
            params.put("isProveedor", DatabaseAssistant.getIsProveedor());
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
        //postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }

    /*private void downloadPlaces() {
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
                        }catch (Throwable e){
                            Log.e(TAG, "requestPlaceAndGeofenceZoneToLogin: onResponse: " + e.getMessage());
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
}