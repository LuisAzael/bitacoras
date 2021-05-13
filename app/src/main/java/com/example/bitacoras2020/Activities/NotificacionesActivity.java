package com.example.bitacoras2020.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bitacoras2020.Adapters.AdapterBitacorasActivas;
import com.example.bitacoras2020.Adapters.AdapterNotificaciones;
import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.Database.Eventos;
import com.example.bitacoras2020.Database.Notificaciones;
import com.example.bitacoras2020.MainActivity;
import com.example.bitacoras2020.Models.ModelBitacorasActivas;
import com.example.bitacoras2020.Models.ModelNotificaciones;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;
import com.example.bitacoras2020.Utils.ConstantsBitacoras;
import com.example.bitacoras2020.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificacionesActivity extends AppCompatActivity {

    private static final String TAG = "NotificacionesActivity";
    boolean vieneDesdeNotificacion = false;
    LinearLayout frameSinDatos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);
        frameSinDatos = (LinearLayout) findViewById(R.id.frameSinDatos);


        if (getIntent().getExtras() != null) {
            try {
                JSONObject jsonNotificacion = new JSONObject(getIntent().getExtras().getString("message"));
                if (jsonNotificacion.has("click_action")) {
                    if (jsonNotificacion.getString("click_action").equals("NOTIFICACIONES_LIST"))
                        vieneDesdeNotificacion = true;
                    else
                        vieneDesdeNotificacion  = false;
                }
            } catch (Throwable e) {
                Log.e(TAG, "onCreate: Error" + e.getMessage());
            }
        }


        ImageView btBack = (ImageView) findViewById(R.id.btBack);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vieneDesdeNotificacion) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(vieneDesdeNotificacion) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ApplicationResourcesProvider.checkInternetConnection()) {
            downloadNotifications();
        }

        consultarNotificaciones();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void consultarNotificaciones() {
        RecyclerView rvNotificaciones;
        GridLayoutManager gridLayoutManager;
        rvNotificaciones = (RecyclerView) findViewById(R.id.rvNotificaciones);
        rvNotificaciones.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rvNotificaciones.setLayoutManager(gridLayoutManager);
        List<ModelNotificaciones> modelNotificaciones = new ArrayList<>();
        AdapterNotificaciones adapterNotificaciones = null;

        List<Notificaciones> categorias = Notificaciones.findWithQuery(Notificaciones.class, "SELECT * FROM NOTIFICACIONES ORDER BY id DESC");
        if (categorias.size() > 0) {
            rvNotificaciones.setVisibility(View.VISIBLE);
            frameSinDatos.setVisibility(View.GONE);
            for (int i = 0; i <= categorias.size() - 1; i++) {
                ModelNotificaciones product = new ModelNotificaciones(
                        "" + categorias.get(i).getTitulo(),
                        "" + categorias.get(i).getBody(),
                        "" + categorias.get(i).getAction(),
                        "" + categorias.get(i).getBitacora(),
                        "" +categorias.get(i).getFecha()
                );
                modelNotificaciones.add(product);
            }

            adapterNotificaciones = new AdapterNotificaciones(NotificacionesActivity.this, modelNotificaciones);
            rvNotificaciones.setAdapter(adapterNotificaciones);
        } else {
            modelNotificaciones.clear();
            adapterNotificaciones = null;
            rvNotificaciones.setVisibility(View.GONE);
            frameSinDatos.setVisibility(View.VISIBLE);
        }


    }

    private void downloadNotifications()
    {
        showMyCustomDialog();
        JSONObject params = new JSONObject();
        try {
            params.put("usuario", DatabaseAssistant.getUserNameFromSesiones() );
            params.put("token_device", DatabaseAssistant.getTokenDeUsuario());
            params.put("isProveedor", DatabaseAssistant.getIsProveedor());
            params.put("pagina", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_NOTIFICATIONS_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                JSONArray jsonArrayNotifications = new JSONArray();
                try {
                    jsonArrayNotifications = response.getJSONArray("notifications");

                    if(jsonArrayNotifications.length()>0) {
                        Notificaciones.deleteAll(Notificaciones.class);
                        for (int i = 0; i <= jsonArrayNotifications.length() - 1; i++) {
                            String[] title = jsonArrayNotifications.getJSONObject(i).getString("title").split("-");
                            DatabaseAssistant.insertarNotificacion(
                                    "" + jsonArrayNotifications.getJSONObject(i).getString("title"),
                                    "" + jsonArrayNotifications.getJSONObject(i).getString("message"),
                                    "",
                                    "" + title[0],
                                    "" + jsonArrayNotifications.getJSONObject(i).getString("fecha")
                            );
                        }
                        dismissMyCustomDialog();
                        consultarNotificaciones();
                        Log.d(TAG, "onResponse: Notificaciones descargadas correctamente.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onResponse: Error al descargar notificaciones: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Ocurrio un error en descargar notificaciones", Toast.LENGTH_SHORT).show();
                    dismissMyCustomDialog();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e(TAG, "onResponse: Error al descargar notificaciones: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Ocurrio un error en descargar notificaciones", Toast.LENGTH_SHORT).show();
                        dismissMyCustomDialog();
                    }
                }) {
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 1, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }

    private void showMyCustomDialog() {
        final FrameLayout flLoading = (FrameLayout) findViewById(R.id.layoutCargando);
        flLoading.setVisibility(View.VISIBLE);
    }

    private void dismissMyCustomDialog() {
        final FrameLayout flLoading = (FrameLayout) findViewById(R.id.layoutCargando);
        flLoading.setVisibility(View.GONE);
    }
}