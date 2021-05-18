package com.example.bitacoras2020.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Person;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bitacoras2020.Database.Adicional;
import com.example.bitacoras2020.Database.Bitacoras;
import com.example.bitacoras2020.Database.Carros;
import com.example.bitacoras2020.Database.Choferes;
import com.example.bitacoras2020.Database.Comentarios;
import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.Database.Documentos;
import com.example.bitacoras2020.Database.Equipocortejo;
import com.example.bitacoras2020.Database.Equipoinstalacion;
import com.example.bitacoras2020.Database.Inventario;
import com.example.bitacoras2020.Database.Lugares;
import com.example.bitacoras2020.MainActivity;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;
import com.example.bitacoras2020.Utils.Constants;
import com.example.bitacoras2020.Utils.ConstantsBitacoras;
import com.example.bitacoras2020.Utils.ForegroundService;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.polyak.iconswitch.IconSwitch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import soup.neumorphism.NeumorphButton;

public class PersonalFuneraria extends AppCompatActivity {

    private static final String TAG = "PERSONAL_FUNERARIA";
    Button btScanner, btBuscarBitacora, btScannerDirecta, btEliminarUrnaVentaDirecta;
    TextView tvEliminarAtaud;
    ImageView btDescargarTodo;
    IconSwitch btCerrarAsistencia;
    TextView tvCodigoUrna, tvSerieUrna, tvFechaUrna, tvProveedorUrna, tvEliminarUrna, tvBitacora;
    NeumorphButton tvRegistroDeAsistencia;
    AutoCompleteTextView etBitacora;
    LinearLayout layoutUrna, layoutBitacora, layoutUrnaEscaneada;
    private boolean scannerAtaurna= false;
    private boolean scannerAtaurnaVentaDirecta= false;

    boolean errorStackTraceBitacoras = false;
    String codigoErrorStackTraceBitacoras = "";
    boolean errorStackTraceBitacorasDetalles = false;
    boolean errorStackTraceBitacorasComentarios = false;

    boolean errorStackTraceChoferes = false;
    boolean errorStackTraceCarrosas = false;
    boolean errorStackTracePlaces = false;
    String codigoErrorStackTraceChoferes = "";
    String codigoErrorStackTraceCarrosas = "";
    String codigoErrorStackTracePlaces = "";
    String descripcionPeticion ="";

    String bitacoraSeleccionada = "";

    GeofencingClient geofencingClient;
    GeoFenceHelper geoFenceHelper;
    PendingIntent pendingIntent = null;
    TextView tvUrnaVendidaGeneral;
    String fechaCapturaDeUrnaParaEliminar="", codigoDeUrnaParaEliminar="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_funeraria);
        btScanner =(Button) findViewById(R.id.btScanner);
        btDescargarTodo =(ImageView) findViewById(R.id.btDescargarTodo);
        btCerrarAsistencia =(IconSwitch) findViewById(R.id.btCerrarAsistencia);
        tvRegistroDeAsistencia =(NeumorphButton) findViewById(R.id.tvRegistroDeAsistencia);
        etBitacora =(AutoCompleteTextView) findViewById(R.id.etBitacora);
        btBuscarBitacora =(Button) findViewById(R.id.btBuscarBitacora);
        btScannerDirecta =(Button) findViewById(R.id.btScannerDirecta);

        tvCodigoUrna =(TextView) findViewById(R.id.tvCodigoUrna);
        tvSerieUrna =(TextView) findViewById(R.id.tvSerieUrna);
        tvFechaUrna =(TextView) findViewById(R.id.tvFechaUrna);
        tvProveedorUrna =(TextView) findViewById(R.id.tvProveedorUrna);
        tvEliminarUrna =(TextView) findViewById(R.id.tvEliminarUrna);
        tvBitacora =(TextView) findViewById(R.id.tvBitacora);
        tvUrnaVendidaGeneral = (TextView) findViewById(R.id.tvUrnaVendida);
        layoutUrnaEscaneada = (LinearLayout) findViewById(R.id.layoutUrnaEscaneada);
        btEliminarUrnaVentaDirecta = (Button) findViewById(R.id.btEliminarUrna);
        tvEliminarAtaud = (TextView) findViewById(R.id.tvEliminarAtaud);

        layoutBitacora =(LinearLayout) findViewById(R.id.layoutBitacora);
        startService();

        if (Preferences.getPreferenceCheckinCheckoutAssistant(PersonalFuneraria.this, Preferences.PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT))
            btCerrarAsistencia.setChecked(IconSwitch.Checked.RIGHT);



        try {
            FirebaseMessaging.getInstance().subscribeToTopic("Latinoamericana")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Log.v("FCM", "onComplete: Tema creado correctamente");
                            } else
                                Log.e("FCM", "onComplete: " + task.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("FCM", "onFailure: No se creo el tema");
                }
            });
        }catch (Throwable e){
            Log.e(TAG, "onCreate: " + e.getMessage());
        }

        tvEliminarAtaud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(PersonalFuneraria.this);
                dialogo1.setCancelable(false);
                dialogo1.setTitle("Información");
                dialogo1.setMessage("¿Seguro que deséas eliminar la Urna?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                        TextView tvUrna = ( TextView ) findViewById(R.id.tvCodigoAtaud);
                        String codigo = tvUrna.getText().toString();

                        List<Inventario> inventarioList = Inventario.findWithQuery(Inventario.class, "SELECT * FROM INVENTARIO WHERE codigo = '" + codigo + "' order by id desc limit 1");
                        if (inventarioList.size() > 0)
                        {

                            DatabaseAssistant.insertarArticuloCancelado(
                                    "" + inventarioList.get(0).getCodigo(),
                                    "" + inventarioList.get(0).getDescripcion(),
                                    "" + inventarioList.get(0).getSerie(),
                                    "" + inventarioList.get(0).getFecha(),
                                    "" + inventarioList.get(0).getProveedor(),
                                    "" + inventarioList.get(0).getBitacora(),
                                    "" + inventarioList.get(0).getCapturado()
                            );


                            Inventario.executeQuery("UPDATE INVENTARIO SET borrado = '1' WHERE codigo ='" + codigo + "'");
                            llenarCamposDeAtaurna(bitacoraSeleccionada);
                        }else{
                            Log.d(TAG, "onClick: NO hay ataudes con el codigo descrito");
                        }

                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.dismiss();
                    }
                });
                dialogo1.show();

            }
        });



        tvEliminarUrna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(PersonalFuneraria.this);
                dialogo1.setCancelable(false);
                dialogo1.setTitle("Información");
                dialogo1.setMessage("¿Seguro que deséas eliminar la Urna?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                        TextView tvUrna = ( TextView ) findViewById(R.id.tvCodigoUrna);
                        String codigo = tvUrna.getText().toString();

                        List<Inventario> inventarioList = Inventario.findWithQuery(Inventario.class, "SELECT * FROM INVENTARIO WHERE codigo = '" + codigo + "' order by id desc limit 1");
                        if (inventarioList.size() > 0)
                        {

                            DatabaseAssistant.insertarArticuloCancelado(
                                    "" + inventarioList.get(0).getCodigo(),
                                    "" + inventarioList.get(0).getDescripcion(),
                                    "" + inventarioList.get(0).getSerie(),
                                    "" + inventarioList.get(0).getFecha(),
                                    "" + inventarioList.get(0).getProveedor(),
                                    "" + inventarioList.get(0).getBitacora(),
                                    "" + inventarioList.get(0).getCapturado()
                            );


                            Inventario.executeQuery("UPDATE INVENTARIO SET borrado = '1' WHERE codigo ='" + codigo + "'");
                            llenarCamposDeAtaurna(bitacoraSeleccionada);
                        }else{
                            Log.d(TAG, "onClick: NO hay ataudes con el codigo descrito");
                        }

                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.dismiss();
                    }
                });
                dialogo1.show();
            }
        });




























        btEliminarUrnaVentaDirecta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(PersonalFuneraria.this);
                dialogo1.setCancelable(false);
                dialogo1.setTitle("Información");
                dialogo1.setMessage("¿Seguro que deséas eliminar la Urna?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                        List<Inventario> inventarioList = Inventario.findWithQuery(Inventario.class, "SELECT * FROM INVENTARIO WHERE codigo = '" + codigoDeUrnaParaEliminar + "' and capturado = '" + fechaCapturaDeUrnaParaEliminar + "' order by id desc limit 1");
                        if (inventarioList.size() > 0)
                        {
                            DatabaseAssistant.insertarArticuloCancelado(
                                    "" + inventarioList.get(0).getCodigo(),
                                    "" + inventarioList.get(0).getDescripcion(),
                                    "" + inventarioList.get(0).getSerie(),
                                    "" + inventarioList.get(0).getFecha(),
                                    "" + inventarioList.get(0).getProveedor(),
                                    "" + inventarioList.get(0).getBitacora(),
                                    "" + inventarioList.get(0).getCapturado()
                            );

                            Inventario.executeQuery("UPDATE INVENTARIO SET borrado = '1' WHERE codigo = '" + codigoDeUrnaParaEliminar + "' and capturado = '" + fechaCapturaDeUrnaParaEliminar + "'");
                            //llenarCamposDeAtaurna(bitacoraSeleccionada);
                            tvUrnaVendidaGeneral.setText("");
                            layoutUrnaEscaneada.setVisibility(View.GONE);
                            Toast.makeText(PersonalFuneraria.this, "Urna eliminada", Toast.LENGTH_SHORT).show();
                        }else{
                            Log.d(TAG, "onClick: NO hay ataudes con el codigo descrito");
                        }

                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.dismiss();
                    }
                });
                dialogo1.show();
            }
        });

        btCerrarAsistencia.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                Log.d("ASISTENCIA", "onCheckChanged: " + current.toggle());
                if(current.name().equals("RIGHT")){
                    //Encendido
                    Log.d("ASISTENCIA", "onCheckChanged: Entro a RIGHT");
                }
                else if(current.name().equals("LEFT")){
                    //APAGADO
                    Log.d("ASISTENCIA", "onCheckChanged: Entro a LEFT");
                    showErrorDialog("¿Cerrar sesión?", "");
                }
            }
        });


        NeumorphButton tvRegistroDeAsistencia =(NeumorphButton) findViewById(R.id.tvRegistroDeAsistencia);
        tvRegistroDeAsistencia.setTypeface(ApplicationResourcesProvider.regular);
        tvRegistroDeAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean actualmenteEsEntrada=false;

                if(Preferences.getPreferencesAsistenciaEntrada(PersonalFuneraria.this, Preferences.PREFERENCE_ASISTENCIA_ENTRADA)) {
                    actualmenteEsEntrada = true;
                }else {
                    actualmenteEsEntrada = false;
                }

                showBottomLayoutForHorariosDeAsistencia(v, actualmenteEsEntrada);
            }
        });


        btScannerDirecta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scannerAtaurnaVentaDirecta = true;
                scannerAtaurna = false;
                new IntentIntegrator(PersonalFuneraria.this).initiateScan();
            }
        });

        etBitacora.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bitacoraSeleccionada = null;
                LinearLayout layoutUrna = (LinearLayout) findViewById(R.id.layoutUrna);
                layoutUrna.setVisibility(View.GONE);
                LinearLayout layoutAtaud = (LinearLayout) findViewById(R.id.layoutAtaud);
                layoutAtaud.setVisibility(View.GONE);
                btScanner.setVisibility(View.INVISIBLE);
                tvBitacora.setText("");
                layoutBitacora.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btBuscarBitacora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etBitacora.getText().toString().equals("") && etBitacora.getText().toString().length() > 8) {
                    if (!DatabaseAssistant.laBitacoraEstaActiva(etBitacora.getText().toString().toUpperCase())) {
                        searchBitacoraInWeb(etBitacora.getText().toString().toUpperCase());
                    }else
                        Toast.makeText(getApplicationContext(), "Ya tienes esta bitácora activa.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Por favor verificar la bitácora a buscar.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        btScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scannerAtaurna = true;
                scannerAtaurnaVentaDirecta = false;
                new IntentIntegrator(PersonalFuneraria.this).initiateScan();
            }
        });




        btDescargarTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ApplicationResourcesProvider.checkInternetConnection()){
                    Snackbar.make(v, "Cargando...", Snackbar.LENGTH_SHORT).show(); //Snackbar es necesario para crear la vista
                    loadRequests();
                }else{
                    showErrorDialog("No hay conexión a internet", "");
                }
            }
        });




        if (DatabaseAssistant.cerrarSesionEnUnLugarEspecifico()) {
            String[] coordinates = DatabaseAssistant.getCoordinatesToDoGeofenceAndCloseSesion();
            LatLng coordenadasToDoGeofenceCheckOut = new LatLng(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
            addGeofenceToCloseLogin(coordenadasToDoGeofenceCheckOut);
        }

        Preferences.setActividadYaIniciada(getApplicationContext(), true, Preferences.PREFERENCE_ACTIVIDAD_INICIADA);
    }


    public void addGeofenceToCloseLogin(LatLng latLng) {
        geofencingClient = LocationServices.getGeofencingClient(this);
        geoFenceHelper = new GeoFenceHelper(this);

        Geofence geofence = geoFenceHelper.getGeoFence(
                "CHECK_OUT_LOGIN_ZONE",
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            if (result.getContents() != null)
            {
                if(scannerAtaurna)
                {



                    /**********************************************************/


                    if(ApplicationResourcesProvider.checkInternetConnection()) {

                        /**Hay que verificar por medio del WS si en la bitacora seleccioada, tiene el ataud o urna disponible para su uso**/
                        try {

                            if (result.getContents().contains("|"))
                            {

                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ApplicationResourcesProvider.createJsonForSync();
                                        }
                                    });
                                }catch (Throwable e){
                                    Log.e(TAG, "onActivityResult: Error en sincronizar: " + e.getMessage() );
                                }

                                showMyCustomDialog();

                                JSONObject parametrosParaSaberSiElCodigoDeAtaurnaFunciona = new JSONObject();
                                parametrosParaSaberSiElCodigoDeAtaurnaFunciona.put("serie_ataurna", result.getContents());

                                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_CHECK_STATUS_ATAURNA, parametrosParaSaberSiElCodigoDeAtaurnaFunciona, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {
                                            if (response.has("error")) {
                                                Log.d(TAG, "onResponse: Ocurrio un error en la respuesta del WS de ATAURNAS CHECK: " + response.getString("error_message"));
                                                Toast.makeText(getApplicationContext(), "Parece que el código es incorrecto", Toast.LENGTH_SHORT).show();
                                                dismissMyCustomDialog();
                                            } else {
                                                if (response.has("result")) {

                                                    /*
                                                        result
                                                        1 = No disponible para escanear, esta guardada en Web con otra bitacora
                                                        0 = Disponible para escanear.
                                                     */

                                                    if (response.getString("result").equals("1")) {
                                                        showErrorDialog("Parece que el ataúd o urna no esta disponible, ya que se encuentra asignado(a) a otra bitácora", "");
                                                        dismissMyCustomDialog();
                                                    } else if (response.getString("result").equals("0")) {
                                                        /**Declaramos la variable booleana para indiicar si se guarda el escaneo**/
                                                        boolean guardarCapturaDeAtaurnas = false;

                                                        /**Verificamos si ya tiene guardado el mismo codigo de ataud, en la bitacora indicada**/
                                                        if (DatabaseAssistant.hayDatosDeAtaurnasEnLaBitacora(bitacoraSeleccionada)) {

                                                            /**Validar si tiene borrado 0 o 1 para insertar uno nuevo**/
                                                            if (DatabaseAssistant.getDatosDeAtaurnasEnLaBitacora(bitacoraSeleccionada).equals("1")) {
                                                                guardarCapturaDeAtaurnas = true;
                                                            } else if (DatabaseAssistant.getDatosDeAtaurnasEnLaBitacora(bitacoraSeleccionada).equals("0")) {
                                                                guardarCapturaDeAtaurnas = false;
                                                                showErrorDialog("Antes de guardar debes eliminar el Ataúd o la Urna que tienes registrada", "");
                                                            }

                                                        } else {
                                                            guardarCapturaDeAtaurnas = true;
                                                        }

                                                        /**Verificamos si fue verdadero para guardar, si es así, procedemos a procesar y desocomponer la cadena de texto**/
                                                        if (guardarCapturaDeAtaurnas) {
                                                            try {
                                                                if (result.getContents().contains("|")) {
                                                                    String[] codeResult = result.getContents().replace("|", "X").split("X");

                                                                    DatabaseAssistant.insertarInventario(
                                                                            "" + codeResult[0],
                                                                            "" + codeResult[1],
                                                                            "" + codeResult[2],
                                                                            "" + codeResult[3],
                                                                            "" + codeResult[4],
                                                                            "" + bitacoraSeleccionada,
                                                                            "0"
                                                                    );
                                                                    llenarCamposDeAtaurna(bitacoraSeleccionada);
                                                                    dismissMyCustomDialog();
                                                                    Toast.makeText(getApplicationContext(), "Equipo guardado correctamente", Toast.LENGTH_SHORT).show();

                                                                } else {
                                                                    showErrorDialog("El código no corresponde a un Ataúd o Urna, verifica nuevamente", "");
                                                                    dismissMyCustomDialog();
                                                                }
                                                            } catch (Throwable e) {
                                                                Log.e(TAG, "onActivityResult: La cadena del escaneo de ataurna no tiene el formato correcto" + e.getMessage());
                                                                showErrorDialog("Código incorrecto, asegurate que sea un código de Ataúd o de Urna. Verifica nuevamente", "");
                                                                dismissMyCustomDialog();
                                                            }
                                                        } else {
                                                            Log.d(TAG, "onActivityResult: No se guardaran datos de ataurnas");
                                                            dismissMyCustomDialog();
                                                        }
                                                    }
                                                }
                                                else{
                                                    //No tiene result
                                                    Toast.makeText(getApplicationContext(), "Parece que el código es incorrecto", Toast.LENGTH_SHORT).show();
                                                    dismissMyCustomDialog();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            Log.e("TIMER", "onErrorResponse: Error: " + e.getMessage());
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "Ocurrio un error en Ataurnas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            dismissMyCustomDialog();
                                        }
                                    }
                                },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                error.printStackTrace();
                                                Log.e("TIMER", "onErrorResponse: Error: " + error.getMessage());
                                                dismissMyCustomDialog();
                                                Toast.makeText(getApplicationContext(), "Ocurrio un error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }) {

                                };
                                postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);

                            }else {
                                showErrorDialog("El código no corresponde a un Ataúd o Urna, verifica nuevamente", "");
                            }

                        } catch (Exception e) {
                            Log.d("TIMER", "getStatusBinaccle: error en creacion de json para status de bitacora");
                            Toast.makeText(this, "Ocurrio un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            dismissMyCustomDialog();
                        }


                    }else
                        showErrorDialog("Necesitas conexión a internet para realizar el escaneo del Ataúd o Urna.", "");



                    /**********************************************************/

                    /*boolean guardarCapturaDeAtaurnas = false;
                    if (DatabaseAssistant.hayDatosDeAtaurnasEnLaBitacora(bitacoraSeleccionada)) {
                        //Validar si tiene borrado 0 o 1 para insertar uno nuevo
                        if (DatabaseAssistant.getDatosDeAtaurnasEnLaBitacora(bitacoraSeleccionada).equals("1")) {
                            guardarCapturaDeAtaurnas = true;
                        } else if (DatabaseAssistant.getDatosDeAtaurnasEnLaBitacora(bitacoraSeleccionada).equals("0")) {
                            guardarCapturaDeAtaurnas = false;
                            showErrorDialog("Antes de guardar debes eliminar la Urna que tienes registrada", "");
                        }
                    } else {
                        guardarCapturaDeAtaurnas = true;
                    }

                    if (guardarCapturaDeAtaurnas) {
                        try {
                            if (result.getContents().contains("|")) {
                                String[] codeResult = result.getContents().replace("|", "X").split("X");

                                DatabaseAssistant.insertarInventario(
                                        "" + codeResult[0],
                                        "" + codeResult[1],
                                        "" + codeResult[2],
                                        "" + codeResult[3],
                                        "" + codeResult[4],
                                        "" + bitacoraSeleccionada,
                                        "0"
                                );
                                llenarCamposDeAtaurna(bitacoraSeleccionada);
                                Toast.makeText(this, "Urna guardado correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                showErrorDialog("El código no corresponde a una Urna, verifica nuevamente", "");
                            }
                        } catch (Throwable e) {
                            Log.e("TAG", "onActivityResult: La cadena del escaneo de ataurna no tiene el formato correcto" + e.getMessage());
                            showErrorDialog("Código incorrecto, asegurate que sea un código de Urna. Verifica nuevamente", "");
                        }
                    } else
                        Log.d("TAG", "onActivityResult: No se guardaran datos de ataurnas");*/

                }
                else if (scannerAtaurnaVentaDirecta)
                {





                    /**********************************************************/


                    if(ApplicationResourcesProvider.checkInternetConnection()) {

                        /**Hay que verificar por medio del WS si en la bitacora seleccioada, tiene el ataud o urna disponible para su uso**/
                        try {

                            if (result.getContents().contains("|"))
                            {

                                showMyCustomDialog();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ApplicationResourcesProvider.createJsonForSync();
                                    }
                                });
                                TextView tvMensajeLoad =(TextView) findViewById(R.id.tvMensajeLoad);
                                tvMensajeLoad.setText("Cargando");
                                Button btCancelarPeticion =(Button) findViewById(R.id.btCancelarPeticion);
                                btCancelarPeticion.setVisibility(View.GONE);
                                JSONObject parametrosParaSaberSiElCodigoDeAtaurnaFunciona = new JSONObject();
                                parametrosParaSaberSiElCodigoDeAtaurnaFunciona.put("serie_ataurna", result.getContents());

                                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_CHECK_STATUS_ATAURNA, parametrosParaSaberSiElCodigoDeAtaurnaFunciona, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {
                                            if (response.has("error")) {
                                                Log.d(TAG, "onResponse: Ocurrio un error en la respuesta del WS de ATAURNAS CHECK: " + response.getString("error_message"));
                                                Toast.makeText(getApplicationContext(), "Parece que el código es incorrecto", Toast.LENGTH_SHORT).show();
                                                dismissMyCustomDialog();
                                            } else {
                                                if (response.has("result")) {

                                                    /*
                                                        result
                                                        1 = No disponible para escanear, esta guardada en Web con otra bitacora
                                                        0 = Disponible para escanear.
                                                     */

                                                    if (response.getString("result").equals("1")) {
                                                        showErrorDialog("Parece que el ataúd o urna no esta disponible, ya que se encuentra asignado(a) a otra bitácora", "");
                                                        dismissMyCustomDialog();
                                                    } else if (response.getString("result").equals("0"))
                                                    {
                                                        try {
                                                            if (result.getContents().contains("|"))
                                                            {
                                                                String[] codeResult = result.getContents().replace("|", "X").split("X");
                                                                showBottomGuardarUrnaParaVenta(codeResult[0], codeResult[1], codeResult[2], codeResult[3], codeResult[4]);
                                                            } else {
                                                                showErrorDialog("El código no corresponde a una Urna, verifica nuevamente", "");
                                                            }
                                                            dismissMyCustomDialog();
                                                        } catch (Throwable e) {
                                                            Log.e("TAG", "onActivityResult: La cadena del escaneo de ataurna no tiene el formato correcto" + e.getMessage());
                                                            dismissMyCustomDialog();
                                                            showErrorDialog("Código incorrecto, asegurate que sea un código de Urna. Verifica nuevamente", "");
                                                        }
                                                    }
                                                }
                                                else{
                                                    //No tiene result
                                                    Toast.makeText(getApplicationContext(), "Parece que el código es incorrecto", Toast.LENGTH_SHORT).show();
                                                    dismissMyCustomDialog();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            Log.e("TIMER", "onErrorResponse: Error: " + e.getMessage());
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "Ocurrio un error en Ataurnas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            dismissMyCustomDialog();
                                        }
                                    }
                                },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                error.printStackTrace();
                                                Log.e("TIMER", "onErrorResponse: Error: " + error.getMessage());
                                                dismissMyCustomDialog();
                                                Toast.makeText(getApplicationContext(), "Ocurrio un error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }) {

                                };
                                postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);

                            }else {
                                showErrorDialog("El código no corresponde a un Ataúd o Urna, verifica nuevamente", "");
                            }

                        } catch (Exception e) {
                            Log.d("TIMER", "getStatusBinaccle: error en creacion de json para status de bitacora");
                            Toast.makeText(this, "Ocurrio un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }else
                        showErrorDialog("Necesitas conexión a internet para realizar el escaneo del Ataúd o Urna.", "");



                    /**********************************************************/

                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: onResume");
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
                                        PersonalFuneraria.this,
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

        //llenarCamposDeAtaurna(bitacoraSeleccionada);
    }

    private void loadRequests() {
        showMyCustomDialog();
        JSONObject params = new JSONObject();
        try {
            params.put("usuario", DatabaseAssistant.getUserNameFromSesiones());
            params.put("token_device", DatabaseAssistant.getTokenDeUsuario());
            params.put("isProveedor", DatabaseAssistant.getIsProveedor());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_VEHICLES_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Carros.deleteAll(Carros.class);
                JSONArray jsonArrayCarrosas = new JSONArray();
                try {
                    jsonArrayCarrosas = response.getJSONArray("vehicles");
                    for (int i = 0; i <= jsonArrayCarrosas.length() - 1; i++) {
                        DatabaseAssistant.insertarCarrosas(
                                "" + jsonArrayCarrosas.getJSONObject(i).getString("name"),
                                "" + jsonArrayCarrosas.getJSONObject(i).getString("status"),
                                "" + jsonArrayCarrosas.getJSONObject(i).getString("id")
                        );
                    }
                    errorStackTraceCarrosas = false;

                    JsonObjectRequest postRequestDrivers = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_DRIVERS_URL, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
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
                                errorStackTraceChoferes = false;
                                JsonObjectRequest postRequestPlaces = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_PLACES_URL, params, new Response.Listener<JSONObject>() {
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
                                            errorStackTracePlaces = false;
                                            dismissMyCustomDialog();
                                            Toast.makeText(getApplicationContext(), "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            errorStackTracePlaces = true;
                                            codigoErrorStackTracePlaces = e.getMessage();
                                            dismissMyCustomDialog();
                                            Toast.makeText(getApplicationContext(), "Ocurrio un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                error.printStackTrace();
                                                errorStackTracePlaces = true;
                                                codigoErrorStackTracePlaces = error.getMessage();
                                                dismissMyCustomDialog();
                                                Toast.makeText(getApplicationContext(), "Ocurrio un error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }) {
                                };
                                postRequestPlaces.setRetryPolicy(new DefaultRetryPolicy(90000, 1, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
                                VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequestPlaces);

                            } catch (Exception e) {
                                e.printStackTrace();
                                errorStackTraceChoferes = true;
                                codigoErrorStackTraceChoferes = e.getMessage();
                                dismissMyCustomDialog();
                                Toast.makeText(getApplicationContext(), "Ocurrio un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                    errorStackTraceChoferes = true;
                                    codigoErrorStackTraceChoferes = error.getMessage();
                                    dismissMyCustomDialog();
                                    Toast.makeText(getApplicationContext(), "Ocurrio un error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }) {
                    };
                    postRequestDrivers.setRetryPolicy(new DefaultRetryPolicy(90000, 1, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
                    VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequestDrivers);


                } catch (Exception e) {
                    e.printStackTrace();
                    errorStackTraceCarrosas = true;
                    codigoErrorStackTraceCarrosas = e.getMessage();
                    dismissMyCustomDialog();
                    Toast.makeText(getApplicationContext(), "Ocurrio un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        errorStackTraceCarrosas = true;
                        codigoErrorStackTraceCarrosas = error.getMessage();
                        dismissMyCustomDialog();
                        Toast.makeText(getApplicationContext(), "Ocurrio un error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 1, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }


    public void showErrorDialog(final String codeError, String bitacora) {
        final NeumorphButton btNo, btSi;
        TextView tvCodeError, tvBitacora;
        EditText etDescripcionPeticion;
        Dialog dialogoError = new Dialog(PersonalFuneraria.this);
        dialogoError.setContentView(R.layout.layout_error);
        dialogoError.setCancelable(true);
        etDescripcionPeticion = (EditText) dialogoError.findViewById(R.id.etDescripcionPeticion);
        btNo = (NeumorphButton) dialogoError.findViewById(R.id.btNo);
        btSi = (NeumorphButton) dialogoError.findViewById(R.id.btSi);
        tvCodeError = (TextView) dialogoError.findViewById(R.id.tvCodeError);
        tvBitacora = (TextView) dialogoError.findViewById(R.id.tvBitacora);
        tvCodeError.setText(codeError);

        if(!codeError.equals("¿Cerrar sesión?"))
            btSi.setVisibility(View.INVISIBLE);


        if (codeError.equals("Estas a punto de terminar la bitácora\n¿Estás de acuerdo?")) {
            tvBitacora.setText(bitacora);
            tvBitacora.setVisibility(View.VISIBLE);
        } else if( codeError.equals("Parece que los articulos de velación no estan completos, necesitas autorización para terminar la bitácora.") ){
            etDescripcionPeticion.setVisibility(View.VISIBLE);

            btSi.setText("Solicitar autorización");
            btNo.setText("Cancelar");
        }
        else {
            tvBitacora.setText("");
            tvBitacora.setVisibility(View.GONE);
        }

        if (codeError.equals("No tienes ningúna bitácora activa") || codeError.equals("No hay conexión a internet")
                || codeError.equals("Ya tienes una salida actualmente, debes registrar una llegada al destino.")
                || codeError.equals("No puedes hacer tu CHECK_OUT en esta zona, necesitas estar en la zona indicada por tu coordinador")
                || codeError.equals("Salida invalida, tienes que seleccionar el destino diferente a tu llegada.")
                || codeError.equals("Debes seleccionar una hora para guardar tu asistencia.")
                || codeError.equals("Parece que el ataúd o urna no esta disponible, ya que se encuentra asignado(a) a otra bitácora")
                || codeError.equals("Ya tienes una llegada actualmente, debes registrar una salida."))
            btSi.setVisibility(View.GONE);



        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(codeError.equals("¿Cerrar sesión?")){
                    btCerrarAsistencia.setChecked(IconSwitch.Checked.RIGHT);
                    dialogoError.dismiss();
                }
                dialogoError.dismiss();
            }
        });

        btSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (codeError.equals("Estas a punto de terminar la bitácora\n¿Estás de acuerdo?")) {
                    registrarBitacoraTerminada(bitacora);
                    dialogoError.dismiss();
                }
                else {
                    if (codeError.equals("¿Cerrar sesión?")) {
                        if (DatabaseAssistant.cerrarSesionEnUnLugarEspecifico()) {
                            if (Preferences.getEndLoginTheZone(getApplicationContext(), Preferences.PREFERENCE_END_SESION_THE_ZONE_TO_LOGIN)) {
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                                Calendar cal = Calendar.getInstance();
                                Preferences.setPreferenceCheckinCheckoutAssistant(getApplicationContext(), false, Preferences.PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT);
                                String[] coordenadasFromApplication = ApplicationResourcesProvider.getCoordenadasFromApplication();
                                String[] dataSessions = DatabaseAssistant.getLastedDataFromSessions();

                                if (coordenadasFromApplication.length > 0 && dataSessions.length > 0) {
                                    DatabaseAssistant.insertarSesiones(
                                            "" + DatabaseAssistant.getUserNameFromSesiones(),
                                            "" + dataSessions[1],
                                            "" + dateFormat.format(new Date()),
                                            "" + coordenadasFromApplication[0],
                                            "" + coordenadasFromApplication[1],
                                            "2",
                                            "" + timeFormat.format(cal.getTime()),
                                            "0",
                                            "0",
                                            Preferences.getIsProveedor(getApplicationContext(), Preferences.PREFERENCE_IS_PROVEEDOR) ? "1" : "0",
                                            Preferences.getGeofenceActual(getApplicationContext(), Preferences.PREFERENCE_GEOFENCE_ACTUAL),
                                            "" + DatabaseAssistant.getLastIsFuneraria(),
                                            ""+ DatabaseAssistant.getUserNameFromSesiones()
                                    );
                                    dialogoError.dismiss();
                                    Toast.makeText(getApplicationContext(), "Hasta luego", Toast.LENGTH_SHORT).show();
                                    finishAffinity();

                                } else
                                    Toast.makeText(getApplicationContext(), "Ocurrio un error al cerrar tu sesión, intenta nuevamente", Toast.LENGTH_SHORT).show();
                            } else
                                showErrorDialog("No puedes hacer tu CHECK_OUT en esta zona, necesitas estar en la zona indicada por tu coordinador", "");
                        } else {
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                            Calendar cal = Calendar.getInstance();
                            Preferences.setPreferenceCheckinCheckoutAssistant(getApplicationContext(), false, Preferences.PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT);
                            String[] coordenadasFromApplication = ApplicationResourcesProvider.getCoordenadasFromApplication();
                            String[] dataSessions = DatabaseAssistant.getLastedDataFromSessions();

                            if (coordenadasFromApplication.length > 0 && dataSessions.length > 0) {
                                DatabaseAssistant.insertarSesiones(
                                        "" +DatabaseAssistant.getUserNameFromSesiones(),
                                        "" + dataSessions[1],
                                        "" + dateFormat.format(new Date()),
                                        "" + coordenadasFromApplication[0],
                                        "" + coordenadasFromApplication[1],
                                        "2",
                                        "" + timeFormat.format(cal.getTime()),
                                        "0",
                                        "0",
                                        Preferences.getIsProveedor(getApplicationContext(), Preferences.PREFERENCE_IS_PROVEEDOR) ? "1" : "0",
                                        Preferences.getGeofenceActual(getApplicationContext(), Preferences.PREFERENCE_GEOFENCE_ACTUAL),
                                        "" + DatabaseAssistant.getLastIsFuneraria(),
                                        ""+ DatabaseAssistant.getUserNameFromSesiones()
                                );
                                dialogoError.dismiss();
                                Toast.makeText(getApplicationContext(), "Hasta luego", Toast.LENGTH_SHORT).show();

                                finishAffinity();

                            } else
                                Toast.makeText(getApplicationContext(), "Ocurrio un error al cerrar tu sesión, intenta nuevamente", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        dialogoError.dismiss();
                        Intent intent = new Intent(getApplicationContext(), NuevaBitacora.class);
                        intent.putExtra("tipo", "2");
                        registrarBitacoraTerminada(bitacora);
                        startActivity(intent);
                    }
                }
            }
        });


        dialogoError.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogoError.show();

    }


    private void showMyCustomDialog() {
        final FrameLayout flLoading = (FrameLayout) findViewById(R.id.layoutCargando);
        flLoading.setVisibility(View.VISIBLE);
    }

    private void dismissMyCustomDialog() {
        final FrameLayout flLoading = (FrameLayout) findViewById(R.id.layoutCargando);
        flLoading.setVisibility(View.GONE);
    }



    private void llenarCamposDeAtaurna(String bitacora) {
        List<Inventario> inventarioList = Inventario.findWithQuery(Inventario.class, "SELECT * FROM INVENTARIO WHERE bitacora = '" + bitacora + "' and borrado = '0' ORDER BY id DESC LIMIT 1");
        if (inventarioList.size() > 0) {
            String codigo = inventarioList.get(0).getCodigo();

            String descripcion = "Descripción: " + inventarioList.get(0).getDescripcion();
            String serie = "No. Serie: " + inventarioList.get(0).getSerie();
            String fecha = "Fecha admisión: " + inventarioList.get(0).getFecha();
            String proveedor = "Proveedor: " + inventarioList.get(0).getProveedor();

            if(codigo.contains("UR")){


                LinearLayout layoutUrna = ( LinearLayout ) findViewById(R.id.layoutUrna);
                TextView tvCodigoUrna = ( TextView ) findViewById(R.id.tvCodigoUrna);
                TextView tvDescripcionUrna = ( TextView ) findViewById(R.id.tvDescripcionUrna);
                TextView tvSerieUrna = ( TextView ) findViewById(R.id.tvSerieUrna);
                TextView tvFechaUrna = ( TextView ) findViewById(R.id.tvFechaUrna);
                TextView tvProveedorUrna = ( TextView ) findViewById(R.id.tvProveedorUrna);

                tvCodigoUrna.setText(inventarioList.get(0).getCodigo());
                tvDescripcionUrna.setText(descripcion);
                tvSerieUrna.setText(serie);
                tvFechaUrna.setText(fecha);
                tvProveedorUrna.setText(proveedor);

                layoutUrna.setVisibility(View.VISIBLE);
            }
            else if(codigo.contains("AT")){


                LinearLayout layoutAtaud = ( LinearLayout ) findViewById(R.id.layoutAtaud);
                layoutAtaud.setVisibility(View.VISIBLE);

                TextView tvCodigoAtaud = ( TextView ) findViewById(R.id.tvCodigoAtaud);
                TextView tvDescripcionAtaud = ( TextView ) findViewById(R.id.tvDescripcionAtaud);
                TextView tvSerieAtaud = ( TextView ) findViewById(R.id.tvSerieAtaud);
                TextView tvFechaAtaud = ( TextView ) findViewById(R.id.tvFechaAtaud);
                TextView tvProveedorAtaud = ( TextView ) findViewById(R.id.tvProveedorAtaud);

                tvCodigoAtaud.setText(inventarioList.get(0).getCodigo());
                tvDescripcionAtaud.setText(descripcion);
                tvSerieAtaud.setText(serie);
                tvFechaAtaud.setText(fecha);
                tvProveedorAtaud.setText(proveedor);


            }
            else {
                Log.d(TAG, "llenarCamposDeAtaurna: No hay datos de ataudes o urnas");
                LinearLayout layoutAtaud = ( LinearLayout ) findViewById(R.id.layoutAtaud);
                layoutAtaud.setVisibility(View.GONE);
                LinearLayout layoutUrna = ( LinearLayout ) findViewById(R.id.layoutUrna);
                layoutUrna.setVisibility(View.GONE);
                Inventario.executeQuery("DELETE INVENTARIO WHERE codigo =''");
            }

        } else {
            Log.d(TAG, "llenarCamposDeAtaurna: No hay datos de ataudes o urnas");

            LinearLayout layoutUrna = ( LinearLayout ) findViewById(R.id.layoutUrna);
            layoutUrna.setVisibility(View.GONE);
            LinearLayout layoutAtaud = (LinearLayout) findViewById(R.id.layoutAtaud);
            layoutAtaud.setVisibility(View.GONE);
        }
    }


    void registrarBitacoraTerminada(String bitacora) {
        try {
            Calendar cal = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            String hora = horaFormat.format(cal.getTime());
            String[] datosBitacoraActiva = DatabaseAssistant.getLastedEvent(bitacora);
            String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();

            if (datosBitacoraActiva.length > 0) {
                DatabaseAssistant.insertarEventos(
                        "" + datosBitacoraActiva[0],
                        "" + datosBitacoraActiva[1],
                        "" + datosBitacoraActiva[2],
                        "" + datosBitacoraActiva[3],
                        "" + datosBitacoraActiva[5],
                        "" + dateFormat.format(new Date()),
                        "" + hora,
                        "" + datosBitacoraActiva[8],
                        "" + Double.parseDouble(arregloCoordenadas[0]),
                        "" + Double.parseDouble(arregloCoordenadas[1]),
                        "0",
                        "Terminada",
                        "" + datosBitacoraActiva[10],
                        "" + datosBitacoraActiva[4],
                        "" + datosBitacoraActiva[11],
                        "" + datosBitacoraActiva[6],
                        "0",
                        "" + datosBitacoraActiva[12]
                );

                DatabaseAssistant.updateBitacorasToCero(bitacora);

                ApplicationResourcesProvider.insertarMovimiento(datosBitacoraActiva[1], datosBitacoraActiva[2], "REGISTRO DE BITÁCORA TERMINADA: " + datosBitacoraActiva[0]);

                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup)findViewById(R.id.relativeLayout1));
                LottieAnimationView lottieAnimationView = view.findViewById(R.id.imageView1);
                lottieAnimationView.setAnimation("success_toast.json");
                lottieAnimationView.loop(false);
                lottieAnimationView.playAnimation();
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view);
                toast.show();
            }
        } catch (Throwable e) {
            Log.e(TAG, "registrarBitacoraTerminada: " + e.getMessage());
        }
    }


    private void searchBitacoraInWeb(String bitacora)
    {
        bitacoraSeleccionada = bitacora;

        if (ApplicationResourcesProvider.checkInternetConnection()) {
            showMyCustomDialog();
            try {
                String token = FirebaseInstanceId.getInstance().getToken();
                Log.d("FIREBASE", "Refresh Token: " + token);
                if (token != null)
                    DatabaseAssistant.insertarToken(token);
                else
                    DatabaseAssistant.insertarToken("Unknown");
            } catch (Throwable e) {
                Log.e(TAG, "downloadBitacoras: " + e.getMessage());
            }

            JSONObject params = new JSONObject();
            try {
                params.put("usuario", DatabaseAssistant.getUserNameFromSesiones());
                params.put("token_device", DatabaseAssistant.getTokenDeUsuario());
                params.put("isProveedor", DatabaseAssistant.getIsProveedor());
                params.put("isBunker", Preferences.getPreferenceIsbunker(getApplicationContext(), Preferences.PREFERENCE_ISBUNKER) ? "1" : "0");
                params.put("bitacora", bitacora);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_DOWNLOAD_BITACORA_INDIVIDUAL, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Bitacoras.deleteAll(Bitacoras.class);
                    JSONArray bitacorasArray = new JSONArray();
                    JSONArray bitacoraDetallesArray = new JSONArray();
                    JSONArray bitacorasComentariosArray = new JSONArray();

                    try {
                        if(!response.getBoolean("error"))
                        {
                            if (response.has("bitacoraDrivers"))
                            {
                                try {

                                    bitacorasArray = response.getJSONArray("bitacoraDrivers");

                                    if(bitacorasArray.length()>0)
                                    {
                                        errorStackTraceBitacoras = false;
                                        for (int i = 0; i <= bitacorasArray.length() - 1; i++) {

                                            DatabaseAssistant.insertarBitacoras(
                                                    "" + i,
                                                    "" + bitacorasArray.getJSONObject(i).getString("name"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("secondName"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("address"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("phones"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("chofer"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("ayudante"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("vehiculo"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("salida"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("destino"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("destino_domicilio"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("destino_latitud"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("destino_longitud"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("ataud"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("panteon"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("tipo_servicio"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("inicio_velacion"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("inicio_cortejo"),
                                                    "" + bitacorasArray.getJSONObject(i).getString("templo")

                                            );
                                        }
                                        Log.d(TAG, "onResponse: Bitacora guardada y consultada correctamente");
                                        errorStackTraceBitacoras = false;
                                        Toast.makeText(PersonalFuneraria.this, "Bitácora encontrada.", Toast.LENGTH_SHORT).show();
                                        btScanner.setVisibility(View.VISIBLE);
                                        bitacoraSeleccionada = bitacora.toUpperCase();
                                        tvBitacora.setText(bitacoraSeleccionada);
                                        layoutBitacora.setVisibility(View.VISIBLE);
                                        llenarCamposDeAtaurna(bitacoraSeleccionada);
                                        dismissMyCustomDialog();
                                    }
                                    else{
                                        codigoErrorStackTraceBitacoras = "No hay datos de bitacoras desde WS";
                                        layoutBitacora.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "No encontramos información de la bitácora que ingresaste, verifica nuevamente.", Toast.LENGTH_SHORT).show();
                                        dismissMyCustomDialog();
                                    }


                                } catch (Throwable e) {
                                    errorStackTraceBitacoras = true;
                                    Log.e(TAG, "onResponse: Error al consultar y guardar la bitacora individual: " + e.getMessage());
                                    layoutBitacora.setVisibility(View.GONE);
                                    dismissMyCustomDialog();
                                    Toast.makeText(getApplicationContext(), "No encontramos información de la bitácora que ingresaste, verifica nuevamente.", Toast.LENGTH_SHORT).show();
                                }


                            }else {
                                codigoErrorStackTraceBitacoras = "No hay datos de bitacoras desde WS";
                                layoutBitacora.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "No encontramos información de la bitácora que ingresaste, verifica nuevamente.", Toast.LENGTH_SHORT).show();
                                dismissMyCustomDialog();
                            }

                        }else{
                            codigoErrorStackTraceBitacoras = "Error en WS error = true";
                            layoutBitacora.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "No encontramos información de la bitácora que ingresaste, verifica nuevamente.", Toast.LENGTH_SHORT).show();
                            dismissMyCustomDialog();
                        }



                    } catch (Exception e) {
                        e.printStackTrace();
                        errorStackTraceBitacoras = true;
                        codigoErrorStackTraceBitacoras = e.getMessage();
                        layoutBitacora.setVisibility(View.GONE);
                        dismissMyCustomDialog();
                        Toast.makeText(getApplicationContext(), "No encontramos información de la bitácora que ingresaste, verifica nuevamente.", Toast.LENGTH_SHORT).show();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            errorStackTraceBitacoras = true;
                            errorStackTraceBitacorasDetalles = true;
                            errorStackTraceBitacorasComentarios = true;
                            codigoErrorStackTraceBitacoras = error.getMessage();
                            layoutBitacora.setVisibility(View.GONE);
                            dismissMyCustomDialog();
                            Toast.makeText(getApplicationContext(), "No encontramos información de la bitácora que ingresaste, verifica nuevamente.", Toast.LENGTH_SHORT).show();
                        }
                    }) {

            };

            postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);

        } else
            showErrorDialog("No hay conexión a internet", "");

    }


    public void showBottomLayoutForHorariosDeAsistencia(View view, boolean actualmenteEsEntrada) {
        try {
            String tipoDEntrada="";
            RadioButton bt7am, bt8am, bt9am, bt19, bt20, bt7amm, bt199, bt19a9;
            NeumorphButton btGuardarAsistencia;
            TextView tvMensaje, tvOrigen, tvDestino;
            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(PersonalFuneraria.this);
            mBottomSheetDialog.setContentView(R.layout.bottom_layout_horarios);
            bt7am = (RadioButton) mBottomSheetDialog.findViewById(R.id.bt7am);
            bt8am = (RadioButton) mBottomSheetDialog.findViewById(R.id.bt8am);
            bt9am = (RadioButton) mBottomSheetDialog.findViewById(R.id.bt9am);
            bt19 = (RadioButton) mBottomSheetDialog.findViewById(R.id.bt19);
            bt7amm = (RadioButton) mBottomSheetDialog.findViewById(R.id.bt7amm);
            bt20 = (RadioButton) mBottomSheetDialog.findViewById(R.id.bt20);
            bt199 = (RadioButton) mBottomSheetDialog.findViewById(R.id.bt199);
            bt19a9 = (RadioButton) mBottomSheetDialog.findViewById(R.id.bt19a9);
            btGuardarAsistencia =(NeumorphButton) mBottomSheetDialog.findViewById(R.id.btGuardarAsistencia);
            tvMensaje =(TextView) mBottomSheetDialog.findViewById(R.id.tvMensaje);
            tvOrigen =(TextView) mBottomSheetDialog.findViewById(R.id.tvOrigen);
            tvDestino =(TextView) mBottomSheetDialog.findViewById(R.id.tvDestino);

            tvOrigen.setText("Entrada: " + DatabaseAssistant.getLastHoraEntrada());
            tvDestino.setText("Próxima salida: " + DatabaseAssistant.getLastHoraSalida());


            LinearLayout layoutRegistrosEntradaSalida = (LinearLayout) mBottomSheetDialog.findViewById(R.id.layoutRegistrosEntradaSalida);
            if(!DatabaseAssistant.getLastHoraEntrada().equals(""))
                layoutRegistrosEntradaSalida.setVisibility(View.VISIBLE);
            else
                layoutRegistrosEntradaSalida.setVisibility(View.GONE);


            if(actualmenteEsEntrada){
                //mostrar las horas de salida
                tipoDEntrada = "4";
                LinearLayout layoutHoraDeEntrada =(LinearLayout) mBottomSheetDialog.findViewById(R.id.layoutHoraDeEntrada);
                layoutHoraDeEntrada.setVisibility(View.GONE);
                //LinearLayout layoutHorasDeSalida =(LinearLayout) mBottomSheetDialog.findViewById(R.id.layoutHoraDeSalida);
                //layoutHorasDeSalida.setVisibility(View.VISIBLE);
                btGuardarAsistencia.setText("Guardar salida");

            }else {
                //mostrar las horas de entrada
                tipoDEntrada = "3";
                //LinearLayout layoutRegistrosEntradaSalida =(LinearLayout) mBottomSheetDialog.findViewById(R.id.layoutRegistrosEntradaSalida);
                layoutRegistrosEntradaSalida.setVisibility(View.GONE);
                //LinearLayout layoutHoraDeEntrada =(LinearLayout) mBottomSheetDialog.findViewById(R.id.layoutHoraDeEntrada);
                //layoutHoraDeEntrada.setVisibility(View.VISIBLE);
                btGuardarAsistencia.setText("Guardar entrada");
            }


            String finalTipoDEntrada = tipoDEntrada;



            btGuardarAsistencia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tipoDeHorario="", horaSeleccionada="", horaEntrada="", horaSalida="";
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();
                    Calendar cal = Calendar.getInstance();

                    try {
                        if (bt7am.isChecked()) {
                            horaSeleccionada = "07:00 - 19:00 hrs";
                            horaEntrada = "07:00 hrs";
                            horaSalida = "19:00 hrs";
                        }else if (bt8am.isChecked()) {
                            horaSeleccionada = "08:00 - 19:00 hrs";
                            horaEntrada = "08:00 hrs";
                            horaSalida = "19:00 hrs";
                        }else if (bt9am.isChecked()) {
                            horaSeleccionada = "09:00 - 19:00 hrs";
                            horaEntrada = "09:00 hrs";
                            horaSalida = "19:00 hrs";
                        }else if (bt199.isChecked()) {
                            horaSeleccionada = "19:00 - 07:00 hrs";
                            horaEntrada = "19:00 hrs";
                            horaSalida = "07:00 hrs";
                        }
                        else if (bt19a9.isChecked()) {
                            horaSeleccionada = "19:00 - 09:00 hrs";
                            horaEntrada = "19:00 hrs";
                            horaSalida = "09:00 hrs";
                        }else {
                            horaSeleccionada = DatabaseAssistant.getLastHoraAsistencia();
                            horaEntrada = DatabaseAssistant.getLastHoraEntrada();
                            horaSalida = DatabaseAssistant.getLastHoraSalida();
                            //horaSalida = "00:00:00";
                        }

                        /*if(DatabaseAssistant.getLastHoraEntrada().equals("")) {
                            horaEntrada = DatabaseAssistant.getLastHoraEntrada();
                        }else {
                            horaSalida = DatabaseAssistant.getLastHoraSalida();
                        }*/



                    }catch (Throwable e){
                        Log.e(TAG, "onClick: Error" + e.getMessage());
                    }



                    if(finalTipoDEntrada.equals("3") && horaSeleccionada.equals("00:00:00")){
                        //No se guarda nada y avisar al usuario que seleccione la hora
                        showErrorDialog("Debes seleccionar una hora para guardar tu asistencia.", "");
                    }else
                    {
                        DatabaseAssistant.insertarAsistencia(
                                "" + DatabaseAssistant.getUserNameFromSesiones(),
                                "",
                                "" + dateFormat.format(new Date()),
                                "" + arregloCoordenadas[0],
                                "" + arregloCoordenadas[1],
                                "" + finalTipoDEntrada,
                                "" + timeFormat.format(cal.getTime()),
                                "0",
                                "" + (Preferences.getPreferenceIsbunker(PersonalFuneraria.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0"),
                                "" + DatabaseAssistant.getIsProveedor(),
                                Preferences.getGeofenceActual(getApplicationContext(), Preferences.PREFERENCE_GEOFENCE_ACTUAL),
                                "" + horaSeleccionada,
                                "" + horaEntrada,
                                ""+ horaSalida
                        );

                        if(finalTipoDEntrada.equals("3")){
                            Preferences.setPreferencesAsistenciaEntrada(PersonalFuneraria.this, true, Preferences.PREFERENCE_ASISTENCIA_ENTRADA);

                        }else {
                            Preferences.setPreferencesAsistenciaEntrada(PersonalFuneraria.this, false, Preferences.PREFERENCE_ASISTENCIA_ENTRADA);
                        }

                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup) findViewById(R.id.relativeLayout1));
                            LottieAnimationView lottieAnimationView = view.findViewById(R.id.imageView1);
                            lottieAnimationView.setAnimation("success_toast.json");
                            lottieAnimationView.loop(false);
                            lottieAnimationView.playAnimation();
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(view);
                            toast.show();
                            if (mBottomSheetDialog.isShowing())
                                mBottomSheetDialog.dismiss();
                            Log.d(TAG, "onClick: Asistencia insertada correctamente");
                        }catch (Throwable e){
                            Log.e(TAG, "onClick: Error en layout de guadrdado correctamente " + e.getMessage() );
                        }
                    }
                }
            });

            mBottomSheetDialog.show();
        } catch (Throwable e) {
            Log.e(TAG, "Error en showDownloadsPDF(): " + e.toString());
        }

    }

    public void startService() {
        try {
            Intent serviceIntent = new Intent(this, ForegroundService.class);
            serviceIntent.putExtra("inputExtra", Constants.ACTION.STARTFOREGROUND_ACTION);
            ContextCompat.startForegroundService(this, serviceIntent);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public void stopService() {
        try {
            Intent serviceIntent = new Intent(this, ForegroundService.class);
            serviceIntent.putExtra("inputExtra", Constants.ACTION.STOPFOREGROUND_ACTION);
            stopService(serviceIntent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
        Preferences.setWithinTheZone(getApplicationContext(), false, Preferences.PREFERENCE_WITHIN_THE_ZONE_TO_LOGIN);
        Preferences.setEndLoginTheZone(getApplicationContext(), false, Preferences.PREFERENCE_END_SESION_THE_ZONE_TO_LOGIN);
    }



    public void showBottomGuardarUrnaParaVenta(String codigoParamtro, String descripcionParamtro,
                                               String serieParamtro, String fechaParamtro, String proveedorParamtro)
    {
        try {
            Button btCancelar, btGuardar;
            TextView tvUrnaVendida;


            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(PersonalFuneraria.this);
            mBottomSheetDialog.setContentView(R.layout.bottom_layout_urna);
            mBottomSheetDialog.setCancelable(false);
            mBottomSheetDialog.setCanceledOnTouchOutside(false);

            tvUrnaVendida = (TextView) mBottomSheetDialog.findViewById(R.id.tvUrnaVendida);
            btCancelar = (Button) mBottomSheetDialog.findViewById(R.id.btCancelar);
            btGuardar = (Button) mBottomSheetDialog.findViewById(R.id.btGuardar);


            String codigo = "Código: " + codigoParamtro;
            String descripcion = "Descripción: " + descripcionParamtro;
            String serie = "No. Serie: " + serieParamtro;
            String fecha = "Fecha admisión: " + fechaParamtro;
            String proveedor = "Proveedor: " + proveedorParamtro;
            tvUrnaVendida.setText(codigo + "\n" + descripcion+ "\n" +serie+ "\n" +fecha+ "\n" +proveedor);


            btCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomSheetDialog.dismiss();
                }
            });

            btGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseAssistant.insertarInventario(
                                    "" + codigoParamtro,
                                    "" + descripcionParamtro,
                                    "" + serieParamtro,
                                    "" + fechaParamtro,
                                    "" + proveedorParamtro,
                                    "VENTA DIRECTA",
                                    "0"
                            );

                    mBottomSheetDialog.dismiss();

                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup) findViewById(R.id.relativeLayout1));
                    LottieAnimationView lottieAnimationView = view.findViewById(R.id.imageView1);
                    lottieAnimationView.setAnimation("success_toast.json");
                    lottieAnimationView.loop(false);
                    lottieAnimationView.playAnimation();
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view);
                    toast.show();

                    List<Inventario> lista = Inventario.findWithQuery(Inventario.class, "SELECT * FROM INVENTARIO ORDER BY id DESC LIMIT 1");
                    if(lista.size() > 0) {
                        String codigo = "Código: " + lista.get(0).getCodigo();
                        String descripcion = "Descripción: " + lista.get(0).getDescripcion();
                        String serie = "No. Serie: " +  lista.get(0).getSerie();
                        String fecha = "Fecha admisión: " +  lista.get(0).getFecha();
                        String proveedor = "Proveedor: " +  lista.get(0).getProveedor();

                        tvUrnaVendidaGeneral.setText(codigo + "\n" + descripcion + "\n" + serie + "\n" + fecha + "\n" + proveedor);
                        layoutUrnaEscaneada.setVisibility(View.VISIBLE);

                        fechaCapturaDeUrnaParaEliminar = lista.get(0).getCapturado();
                        codigoDeUrnaParaEliminar = lista.get(0).getCodigo();
                    }

                }
            });

            Toast.makeText(this, "Urna escaneada correctamente", Toast.LENGTH_SHORT).show();





            mBottomSheetDialog.show();
        } catch (Throwable e) {
            Log.e(TAG, "Error en showDownloadsPDF(): " + e.toString());
        }
    }


}