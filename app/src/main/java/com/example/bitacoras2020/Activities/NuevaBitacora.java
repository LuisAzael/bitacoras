package com.example.bitacoras2020.Activities;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bitacoras2020.Adapters.AdapterEquiposCortejo;
import com.example.bitacoras2020.Adapters.AdapterEquiposInstalacion;
import com.example.bitacoras2020.Adapters.AdapterEquiposRecoleccion;
import com.example.bitacoras2020.Adapters.AdapterEquiposTraslado;
import com.example.bitacoras2020.Callbacks.CancelarArticuloCortejo;
import com.example.bitacoras2020.Callbacks.CancelarArticuloInstalacion;
import com.example.bitacoras2020.Callbacks.CancelarArticuloRecoleccion;
import com.example.bitacoras2020.Callbacks.CancelarArticuloTraslado;
import com.example.bitacoras2020.Database.Adicional;
import com.example.bitacoras2020.Database.Bitacoras;
import com.example.bitacoras2020.Database.CatalogoArticulos;
import com.example.bitacoras2020.Database.Codigos;
import com.example.bitacoras2020.Database.Comentarios;
import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.Database.Documentos;
import com.example.bitacoras2020.Database.EquipoRecoleccion;
import com.example.bitacoras2020.Database.EquipoTraslado;
import com.example.bitacoras2020.Database.Equipocortejo;
import com.example.bitacoras2020.Database.Equipoinstalacion;
import com.example.bitacoras2020.Database.Inventario;
import com.example.bitacoras2020.MainActivity;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;
import com.example.bitacoras2020.Utils.ConstantsBitacoras;
import com.example.bitacoras2020.Utils.Preferences;
import com.example.bitacoras2020.Utils.VolleySingleton;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import soup.neumorphism.NeumorphButton;

public class NuevaBitacora extends Activity implements LinearLayoutThatDetectsSoftKeyboard.Listener, CancelarArticuloInstalacion, CancelarArticuloRecoleccion, CancelarArticuloTraslado {
    private static final String TAG = "NuevaBitacora";
    public String bitacoraSeleccionada = "", choferSeleccionado = "", ayudanteSeleccionado = "", carroSeleccinado = "", lugarSeleccionado = "", destinoSeleccionado = "", movimientoSeleccionado = "";
    AutoCompleteTextView etBitacora, etCarro, etChofer, etAyudante;
    Spinner spLugares, spDestino, spMovimiento;
    ImageView btBack;
    LinearLayout frameRegistro;
    String tipoOpcion = "";
    BottomSheetDialog dialogBottomNavigation;
    Dialog dialogoError;
    TextView tvAtaud, tvPanteon, tvCrematorio;
    NeumorphButton btBuscarBitacora;
    boolean errorStackTraceBitacoras = false;
    boolean errorStackTraceBitacorasDetalles = false;
    boolean errorStackTraceBitacorasComentarios = false, errorStackTraceAtaurna=false;
    boolean bitacoraFromNotification = false;
    String codigoErrorStackTraceBitacoras = "", bitacoraGlobal="";

    private boolean equipoDeTraslado = false, equipoDeInstalacion = false, equipoRecoleccion = false,  isArticuloDeVelacion = false, scannerAtaurna= false;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (bitacoraFromNotification) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        eliminarArticulosDeInstalacionNoGuardados(bitacoraGlobal);
        eliminarArticulosDeCortejoNoGuardados(bitacoraGlobal);
        eliminarArticulosDeRecoleccionNoGuardados(bitacoraGlobal);
        eliminarArticulosDeTrasladoNoGuardados(bitacoraGlobal);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialogBottomNavigation = new BottomSheetDialog(NuevaBitacora.this);
        dialogoError = new Dialog(this);
        setContentView(R.layout.activity_nueva_bitacora);
        NeumorphButton btCancelar = findViewById(R.id.btCancelar);
        NeumorphButton btGuardar = findViewById(R.id.btGuardar);

        LinearLayout layoutInstalacion =(LinearLayout) findViewById(R.id.layoutInstalacion);
        LinearLayout layoutRecoleccion =(LinearLayout) findViewById(R.id.layoutRecoleccion);
        LinearLayout layoutTraslado =(LinearLayout) findViewById(R.id.layoutTraslado);

        btBack = (ImageView) findViewById(R.id.btBack);
        etBitacora = (AutoCompleteTextView) findViewById(R.id.etBitacora);
        etChofer = (AutoCompleteTextView) findViewById(R.id.etChofer);
        etAyudante = (AutoCompleteTextView) findViewById(R.id.etAyudante);
        etCarro = (AutoCompleteTextView) findViewById(R.id.etCarro);
        spLugares = (Spinner) findViewById(R.id.spLugares);
        spDestino = (Spinner) findViewById(R.id.spDestino);
        spMovimiento = (Spinner) findViewById(R.id.spMovimiento);
        frameRegistro = (LinearLayout) findViewById(R.id.frameRegistro);
        tvAtaud = (TextView) findViewById(R.id.tvAtaud);
        tvPanteon = (TextView) findViewById(R.id.tvPanteon);
        tvCrematorio = (TextView) findViewById(R.id.tvCrematorio);
        btBuscarBitacora = (NeumorphButton) findViewById(R.id.btBuscarBitacora);


        btBuscarBitacora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etBitacora.getText().toString().equals("") && etBitacora.getText().toString().length() > 8) {
                    if (!DatabaseAssistant.laBitacoraEstaActiva(etBitacora.getText().toString().toUpperCase())) {
                        searchBitacoraInWeb(etBitacora.getText().toString().toUpperCase());
                    }else
                        Toast.makeText(NuevaBitacora.this, "Ya tienes esta bitácora activa.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NuevaBitacora.this, "Por favor verificar la bitácora a buscar.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ImageView btComentarios = (ImageView) findViewById(R.id.btComentarios);
        btComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ayudaTutorial();
            }
        });

        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitacoraFromNotification) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                eliminarArticulosDeInstalacionNoGuardados(bitacoraGlobal);
                eliminarArticulosDeCortejoNoGuardados(bitacoraGlobal);
                eliminarArticulosDeRecoleccionNoGuardados(bitacoraGlobal);
                eliminarArticulosDeTrasladoNoGuardados(bitacoraGlobal);
                finish();
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitacoraFromNotification) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                eliminarArticulosDeInstalacionNoGuardados(bitacoraGlobal);
                eliminarArticulosDeCortejoNoGuardados(bitacoraGlobal);
                eliminarArticulosDeRecoleccionNoGuardados(bitacoraGlobal);
                eliminarArticulosDeTrasladoNoGuardados(bitacoraGlobal);
                finish();
            }
        });


        final Bundle extras = getIntent().getExtras();
        if (extras != null) {

            if (extras.containsKey("bitacora_from_push_notification")) {
                etBitacora.setText(extras.getString("bitacora_from_push_notification"));
                //Verificar si la bitacora existe en BD local
                if (DatabaseAssistant.laBitacoraExisteEnLaBaseDeDatos(extras.getString("bitacora_from_push_notification"))) {
                    checkAndShowDataInBoxes(extras.getString("bitacora_from_push_notification"));
                } else
                    //Actualizar las bitacoras desde WS con loadAnimation
                    loadInformationAndRequest(extras.getString("bitacora_from_push_notification"));

                bitacoraFromNotification = true;
            }

            if (extras.containsKey("tipo"))
                tipoOpcion = extras.getString("tipo");

            if (tipoOpcion.equals("2")) {
                DatabaseAssistant.updateBitacorasToCero("auxiliar");
            }
        } else
            Log.v(TAG, "No se recuperaron datos de extras");


        ArrayAdapter<String> adapterLugares = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.getLugares());
        spLugares.setAdapter(adapterLugares);
        spLugares.setSelection(2);
        spDestino.setAdapter(adapterLugares);


        String[] movimientos = {"Selecciona movimiento...", "Recolección", "Traslado", "Instalación", "Cortejo"};
        ArrayAdapter<String> adapterMovimientos = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, movimientos);
        spMovimiento.setAdapter(adapterMovimientos);

        spMovimiento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                movimientoSeleccionado = spMovimiento.getItemAtPosition(position).toString();
                closeTeclado();

                /** Validar las posiciones del spinner para activar o desactivar los menus de escaneo **/

                if(spMovimiento.getSelectedItemPosition() == 1){/**Articulo de Recoleccion**/
                    LinearLayout expand_view = (LinearLayout) findViewById(R.id.expand_view);
                    expand_view.setVisibility(View.VISIBLE);
                    layoutInstalacion.setVisibility(View.GONE);
                    layoutRecoleccion.setVisibility(View.VISIBLE);
                    layoutTraslado.setVisibility(View.GONE);

                }else if(spMovimiento.getSelectedItemPosition() == 2){/**Articulo de Traslado**/
                    layoutInstalacion.setVisibility(View.GONE);
                    layoutRecoleccion.setVisibility(View.GONE);
                    layoutTraslado.setVisibility(View.VISIBLE);
                    LinearLayout expand_view = (LinearLayout) findViewById(R.id.expand_view);
                    expand_view.setVisibility(View.VISIBLE);
                }
                else if(spMovimiento.getSelectedItemPosition() == 3){/**Articulo de Instalacion**/
                    layoutInstalacion.setVisibility(View.VISIBLE);
                    layoutRecoleccion.setVisibility(View.GONE);
                    layoutTraslado.setVisibility(View.GONE);
                    LinearLayout expand_view = (LinearLayout) findViewById(R.id.expand_view);
                    expand_view.setVisibility(View.VISIBLE);

                } else if(spMovimiento.getSelectedItemPosition() == 4){/**Articulo de Cortejo**/
                    layoutInstalacion.setVisibility(View.GONE);
                    layoutRecoleccion.setVisibility(View.GONE);
                    layoutTraslado.setVisibility(View.GONE);
                    LinearLayout expand_view = (LinearLayout) findViewById(R.id.expand_view);
                    expand_view.setVisibility(View.GONE);
                }
                else{
                    LinearLayout expand_view = (LinearLayout) findViewById(R.id.expand_view);
                    expand_view.setVisibility(View.GONE);
                    layoutInstalacion.setVisibility(View.GONE);
                    layoutRecoleccion.setVisibility(View.GONE);
                    layoutTraslado.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                movimientoSeleccionado = null;
            }
        });

        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**Verificar lls lugares seleccionados tanto origen como destino, que no sean iguales, para poder levanar el evento de bitacora**/
                if (!lugarSeleccionado.equals(destinoSeleccionado)) {

                    String geofenceActual = Preferences.getGeofenceActual(NuevaBitacora.this, Preferences.PREFERENCE_GEOFENCE_ACTUAL);
                    if (!geofenceActual.equals(""))
                    {
                        Log.i(TAG, "onCreate: Geofence actual: " + geofenceActual);

                        if (geofenceActual.equals(lugarSeleccionado)) {
                            try {
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
                                String hora = horaFormat.format(cal.getTime());
                                String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();

                                if (checkFields()) {
                                    @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                                    String bitacora = bitacoraSeleccionada != null ? bitacoraSeleccionada : etBitacora.getText().toString();
                                    DatabaseAssistant.insertarEventos(
                                            "" + bitacora,
                                            "" + choferSeleccionado,
                                            "" + ayudanteSeleccionado != null ? ayudanteSeleccionado : etAyudante.getText().toString(),
                                            "" + carroSeleccinado != null ? carroSeleccinado : etCarro.getText().toString(),
                                            "" + geofenceActual,
                                            "" + arregloCoordenadas[2],
                                            "" + hora,
                                            "" + android_id,
                                            "" + Double.parseDouble(arregloCoordenadas[0]),
                                            "" + Double.parseDouble(arregloCoordenadas[1]),
                                            "0",
                                            "Salida",
                                            "" + DatabaseAssistant.getNameBitacora(bitacora),
                                            "" + DatabaseAssistant.getDomicilioBitacora(bitacora),
                                            "" + DatabaseAssistant.getTelefonoBitacora(bitacora),
                                            "" + destinoSeleccionado != null ? destinoSeleccionado : spDestino.getSelectedItem().toString(),
                                            "0",
                                            "" + movimientoSeleccionado != null ? movimientoSeleccionado : spMovimiento.getSelectedItem().toString()
                                    );

                                    DatabaseAssistant.insertarActivos(
                                            "" + bitacora,
                                            "" + choferSeleccionado != null || !choferSeleccionado.equals("") ? choferSeleccionado : etChofer.getText().toString(),
                                            "" + ayudanteSeleccionado != null ? ayudanteSeleccionado : etAyudante.getText().toString(),
                                            "" + carroSeleccinado != null ? carroSeleccinado : etCarro.getText().toString(),
                                            "" + lugarSeleccionado != null ? lugarSeleccionado : spLugares.getSelectedItem().toString(),
                                            "" + arregloCoordenadas[2],
                                            "00:00",
                                            "" + android_id,
                                            "" + Double.parseDouble(arregloCoordenadas[0]),
                                            "" + Double.parseDouble(arregloCoordenadas[1]),
                                            "0",
                                            "Salida",
                                            "" + DatabaseAssistant.getNameBitacora(bitacora),
                                            "" + DatabaseAssistant.getDomicilioBitacora(bitacora),
                                            "" + DatabaseAssistant.getTelefonoBitacora(bitacora),
                                            "",
                                            "" + destinoSeleccionado != null ? destinoSeleccionado : spDestino.getSelectedItem().toString(),
                                            "1",
                                            "" + DatabaseAssistant.getDomicilioDestinoFromBitacora(bitacora),
                                            "" + DatabaseAssistant.getLatitudDestino(bitacora),
                                            "" + DatabaseAssistant.getLongitudDestino(bitacora),
                                            "" + movimientoSeleccionado != null ? movimientoSeleccionado : spMovimiento.getSelectedItem().toString()
                                    );

                                    ApplicationResourcesProvider.insertarMovimiento("", "", "REGISTRO DE BITÁCORA NUEVA: " + bitacora);
                                    updateArticulosDeInstalacionConSync4(bitacora);
                                    updateArticulosDeCortejoConSync4(bitacora);
                                    updateArticulosDeRecoleccionConSync4(bitacora);
                                    updateArticulosDeTrasladoConSync4(bitacora);

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


                                    //VERIFICAR SI LA BITACORA TIENE LUGAR DE DESTINO PARA MANDARLO A NAVEGACIÓN

                                    try {
                                        String[] origenAndDestinos = DatabaseAssistant.getOrigenAndDestinoFromBitacora(bitacora);
                                        if (Double.parseDouble(origenAndDestinos[1]) != 0 || Double.parseDouble(origenAndDestinos[2]) != 0) {
                                            showNavigationType(
                                                    "" + Double.parseDouble(arregloCoordenadas[0]),
                                                    "" + Double.parseDouble(arregloCoordenadas[1]),
                                                    "" + origenAndDestinos[1],
                                                    "" + origenAndDestinos[2]
                                            );
                                        } else if (!origenAndDestinos[0].equals("") && origenAndDestinos[3].equals(destinoSeleccionado)) {
                                            LatLng coordenadas = getCoordenadasFromAddress(getApplicationContext(), origenAndDestinos[0]);

                                            showNavigationType(
                                                    "" + Double.parseDouble(arregloCoordenadas[0]),
                                                    "" + Double.parseDouble(arregloCoordenadas[1]),
                                                    "" + String.valueOf(coordenadas.latitude),
                                                    "" + String.valueOf(coordenadas.longitude));
                                        } else {
                                            //finish();
                                            if (bitacoraFromNotification) {
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                            }
                                            finish();
                                        }

                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                        Log.i(TAG, "onClick: No pudimos obtener las coordenadas de nada");
                                        Toast.makeText(NuevaBitacora.this, "Hubo un error al crear ruta", Toast.LENGTH_SHORT).show();
                                        //finish();
                                        if (bitacoraFromNotification) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                        }
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(NuevaBitacora.this, "Por favor verifica que los campos estén llenos y sean válidos.", Toast.LENGTH_LONG).show();
                                }
                            } catch (Throwable e) {
                                Log.e(TAG, "Error en btGuardar.OnClick() " + e.getMessage());
                                Toast.makeText(NuevaBitacora.this, "No se guardo la bitácora, intenta nuevamente", Toast.LENGTH_SHORT).show();
                            }
                        } else
                            showErrorDialog("No se puede crear la bitácora por que la zona dónde estás, no corresponde a tu lugar seleccionado.");
                    } else {
                        Log.d(TAG, "onCreate: La Geofence esta vacia");
                        try {
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
                            String hora = horaFormat.format(cal.getTime());

                            String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();
                            if (checkFields()) {
                                @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                                String bitacora = bitacoraSeleccionada != null ? bitacoraSeleccionada : etBitacora.getText().toString();
                                DatabaseAssistant.insertarEventos(
                                        "" + bitacora,
                                        "" + choferSeleccionado,
                                        "" + ayudanteSeleccionado != null ? ayudanteSeleccionado : etAyudante.getText().toString(),
                                        "" + carroSeleccinado != null ? carroSeleccinado : etCarro.getText().toString(),
                                        "" + lugarSeleccionado != null ? lugarSeleccionado : spLugares.getSelectedItem().toString(),
                                        "" + arregloCoordenadas[2],
                                        "" + hora,
                                        "" + android_id,
                                        "" + Double.parseDouble(arregloCoordenadas[0]),
                                        "" + Double.parseDouble(arregloCoordenadas[1]),
                                        "0",
                                        "Salida",
                                        "" + DatabaseAssistant.getNameBitacora(bitacora),
                                        "" + DatabaseAssistant.getDomicilioBitacora(bitacora),
                                        "" + DatabaseAssistant.getTelefonoBitacora(bitacora),
                                        "" + destinoSeleccionado != null ? destinoSeleccionado : spDestino.getSelectedItem().toString(),
                                        "0",
                                        "" + movimientoSeleccionado != null ? movimientoSeleccionado : spMovimiento.getSelectedItem().toString()
                                );

                                DatabaseAssistant.insertarActivos(
                                        "" + bitacora,
                                        "" + choferSeleccionado != null || !choferSeleccionado.equals("") ? choferSeleccionado : etChofer.getText().toString(),
                                        "" + ayudanteSeleccionado != null ? ayudanteSeleccionado : etAyudante.getText().toString(),
                                        "" + carroSeleccinado != null ? carroSeleccinado : etCarro.getText().toString(),
                                        "" + lugarSeleccionado != null ? lugarSeleccionado : spLugares.getSelectedItem().toString(),
                                        "" + arregloCoordenadas[2],
                                        "00:00",
                                        "" + android_id,
                                        "" + Double.parseDouble(arregloCoordenadas[0]),
                                        "" + Double.parseDouble(arregloCoordenadas[1]),
                                        "0",
                                        "Salida",
                                        "" + DatabaseAssistant.getNameBitacora(bitacora),
                                        "" + DatabaseAssistant.getDomicilioBitacora(bitacora),
                                        "" + DatabaseAssistant.getTelefonoBitacora(bitacora),
                                        "",
                                        "" + destinoSeleccionado != null ? destinoSeleccionado : spDestino.getSelectedItem().toString(),
                                        "1",
                                        "" + DatabaseAssistant.getDomicilioDestinoFromBitacora(bitacora),
                                        "" + DatabaseAssistant.getLatitudDestino(bitacora),
                                        "" + DatabaseAssistant.getLongitudDestino(bitacora),
                                        "" + movimientoSeleccionado != null ? movimientoSeleccionado : spMovimiento.getSelectedItem().toString()
                                );

                                ApplicationResourcesProvider.insertarMovimiento("", "", "REGISTRO DE BITÁCORA NUEVA: " + bitacora);
                                updateArticulosDeInstalacionConSync4(bitacora);
                                updateArticulosDeCortejoConSync4(bitacora);
                                updateArticulosDeRecoleccionConSync4(bitacora);
                                updateArticulosDeTrasladoConSync4(bitacora);

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

                                //VERIFICAR SI LA BITACORA TIENE LUGAR DE DESTINO PARA MANDARLO A NAVEGACIÓN

                                try {
                                    String[] origenAndDestinos = DatabaseAssistant.getOrigenAndDestinoFromBitacora(bitacora);
                                    if (Double.parseDouble(origenAndDestinos[1]) != 0 || Double.parseDouble(origenAndDestinos[2]) != 0) {
                                        showNavigationType(
                                                "" + Double.parseDouble(arregloCoordenadas[0]),
                                                "" + Double.parseDouble(arregloCoordenadas[1]),
                                                "" + origenAndDestinos[1],
                                                "" + origenAndDestinos[2]
                                        );

                                    } else if (!origenAndDestinos[0].equals("") && origenAndDestinos[3].equals(destinoSeleccionado)) {
                                        LatLng coordenadas = getCoordenadasFromAddress(getApplicationContext(), origenAndDestinos[0]);

                                        showNavigationType(
                                                "" + Double.parseDouble(arregloCoordenadas[0]),
                                                "" + Double.parseDouble(arregloCoordenadas[1]),
                                                "" + String.valueOf(coordenadas.latitude),
                                                "" + String.valueOf(coordenadas.longitude));
                                    } else {
                                        //finish();
                                        if (bitacoraFromNotification) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                        }
                                        finish();
                                    }

                                } catch (Throwable e) {
                                    e.printStackTrace();
                                    Log.i(TAG, "onClick: No pudimos obtener las coordenadas de nada");
                                    Toast.makeText(NuevaBitacora.this, "Hubo un error al crear ruta", Toast.LENGTH_SHORT).show();
                                    //finish();
                                    if (bitacoraFromNotification) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                    finish();
                                }
                            } else {
                                Toast.makeText(NuevaBitacora.this, "Por favor verifica que los campos estén llenos y sean válidos.", Toast.LENGTH_LONG).show();
                            }
                        } catch (Throwable e) {
                            Log.e(TAG, "Error en btGuardar.OnClick() " + e.getMessage());
                            Toast.makeText(NuevaBitacora.this, "No se guardo la bitácora, intenta nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.e(TAG, "onClick: El destino es igual al origen seleccionado");
                    showErrorDialog("No podemos crear la bitacora porque el destino debe ser diferente origen");
                }

            }
        });


        etBitacora.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bitacoraSeleccionada = null;
                LinearLayout layoutDatos = (LinearLayout) findViewById(R.id.layoutDatos);
                layoutDatos.setVisibility(View.GONE);



                /*if(etBitacora.getText().toString().length() >= 4) {
                    etBitacora.setThreshold(4);
                    try {
                        ArrayAdapter<String> adapterColonias = new ArrayAdapter<String>(NuevaBitacora.this, R.layout.style_spinner, DatabaseAssistant.getBitacoraName(etBitacora.getText().toString()));
                        etBitacora.setAdapter(adapterColonias);
                    }catch (Throwable e){
                        Log.e(TAG, "onTextChanged: Error en etBitacora.onTextChange() " + e.getMessage() );
                    }
                }*/


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etBitacora.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bitacoraSeleccionada = etBitacora.getEditableText().toString();

                if (DatabaseAssistant.getDataBitacora(bitacoraSeleccionada) != null) {
                    closeTeclado();
                    String[] bitacompletaData = DatabaseAssistant.getDataBitacora(bitacoraSeleccionada);


                    bitacoraSeleccionada = bitacompletaData[1];
                    try {
                        choferSeleccionado = bitacompletaData[3];
                        etChofer.setText(bitacompletaData[3]);
                    } catch (Throwable e) {
                        Log.e(TAG, "onItemClick: " + e.getMessage());
                        choferSeleccionado = null;
                        etChofer.setText("");
                    }

                    try {
                        ayudanteSeleccionado = bitacompletaData[4];
                        etAyudante.setText(bitacompletaData[4]);
                    } catch (Throwable e) {
                        Log.e(TAG, "onItemClick: " + e.getMessage());
                        ayudanteSeleccionado = null;
                        etAyudante.setText("");
                    }

                    try {
                        carroSeleccinado = bitacompletaData[5];
                        etCarro.setText(bitacompletaData[5]);
                    } catch (Throwable e) {
                        Log.e(TAG, "onItemClick: " + e.getMessage());
                        carroSeleccinado = null;
                        etCarro.setText("");
                    }

                    try {
                        lugarSeleccionado = bitacompletaData[6];
                    } catch (Throwable e) {
                        Log.e(TAG, "onItemClick: " + e.getMessage());
                        lugarSeleccionado = null;
                    }

                    try {
                        destinoSeleccionado = bitacompletaData[7];
                    } catch (Throwable e) {
                        Log.e(TAG, "onItemClick: " + e.getMessage());
                        destinoSeleccionado = null;
                    }


                    TextView tvAtaud = (TextView) findViewById(R.id.tvAtaud);
                    tvAtaud.setText(bitacompletaData[14]);
                    LinearLayout layoutAtaud = (LinearLayout) findViewById(R.id.layoutAtaud);
                    layoutAtaud.setVisibility(View.VISIBLE);

                    TextView tvPanteon = (TextView) findViewById(R.id.tvPanteon);
                    tvPanteon.setText(bitacompletaData[15]);
                    LinearLayout layoutPanteon = (LinearLayout) findViewById(R.id.layoutPanteon);
                    layoutPanteon.setVisibility(View.VISIBLE);

                    TextView tvCrematorio = (TextView) findViewById(R.id.tvCrematorio);
                    tvCrematorio.setText(bitacompletaData[16]);
                    LinearLayout layoutSevicio = (LinearLayout) findViewById(R.id.layoutSevicio);
                    layoutSevicio.setVisibility(View.VISIBLE);

                    etChofer.setTextColor(Color.parseColor("#969da9"));
                    etAyudante.setTextColor(Color.parseColor("#969da9"));
                    etCarro.setTextColor(Color.parseColor("#969da9"));


                    if (etChofer.getText().toString().equals("") || choferSeleccionado.equals("")) {
                        etChofer.setTextColor(Color.RED);
                        etChofer.setText("**** FALTA DATO AQUÍ ****");
                        choferSeleccionado = null;
                    }

                    if (etAyudante.getText().toString().equals("") || ayudanteSeleccionado.equals("")) {
                        etAyudante.setTextColor(Color.RED);
                        etAyudante.setText("**** FALTA DATO AQUÍ ****");
                        ayudanteSeleccionado = null;
                    }

                    if (etCarro.getText().toString().equals("") || carroSeleccinado.equals("")) {
                        etCarro.setTextColor(Color.RED);
                        etCarro.setText("**** FALTA DATO AQUÍ ****");
                        carroSeleccinado = null;
                    }

                    //EXIT
                    String geofenceActual = Preferences.getGeofenceActual(NuevaBitacora.this, Preferences.PREFERENCE_GEOFENCE_ACTUAL);
                    if (!geofenceActual.equals("")) {
                        ArrayList<String> geofenceActualPorUbicacion = new ArrayList<String>();
                        geofenceActualPorUbicacion.add(geofenceActual);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, geofenceActualPorUbicacion);
                        spLugares.setAdapter(adapter);
                        spLugares.setEnabled(false);
                    } else {

                        if (lugarSeleccionado.equals("**** FALTA DATO AQUÍ ****")) {
                            spLugares.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_style_red));
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                            spLugares.setAdapter(adapter);
                        } else {
                            //String[] opciones ={lugarSeleccionado};
                            //ArrayAdapter<String> adapterLugares = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, opciones);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                            spLugares.setAdapter(adapter);
                            spLugares.setSelection(adapter.getPosition(lugarSeleccionado));
                        }

                    }


                    if (destinoSeleccionado.equals("**** FALTA DATO AQUÍ ****")) {
                        spDestino.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_style_red));
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                        spDestino.setAdapter(adapter);
                    } else {
                        //String[] opciones2 ={destinoSeleccionado};
                        //ArrayAdapter<String> adapterOpciones2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, opciones2);
                        ArrayAdapter<String> adapterOpciones2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                        spDestino.setAdapter(adapterOpciones2);
                        spDestino.setSelection(adapterOpciones2.getPosition(destinoSeleccionado));
                    }

                    LinearLayout layoutDatos = (LinearLayout) findViewById(R.id.layoutDatos);
                    layoutDatos.setVisibility(View.VISIBLE);

                } else
                    Toast.makeText(NuevaBitacora.this, "No se encontraron datos de la bitácora seleccionada", Toast.LENGTH_LONG).show();


            }
        });


        etChofer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //choferSeleccionado = null;
                if (etChofer.getText().toString().length() >= 1) {
                    ArrayAdapter<String> adapterColonias = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner,
                            DatabaseAssistant.getChoferes(etChofer.getText().toString()));
                    etChofer.setAdapter(adapterColonias);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etChofer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                choferSeleccionado = etChofer.getEditableText().toString();
                etChofer.setTextColor(Color.parseColor("#969da9"));
                closeTeclado();
            }
        });


        etAyudante.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //ayudanteSeleccionado = null;
                ArrayAdapter<String> adapterColonias = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner,
                        DatabaseAssistant.getChoferes(etAyudante.getText().toString()));
                etAyudante.setAdapter(adapterColonias);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etAyudante.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ayudanteSeleccionado = etAyudante.getEditableText().toString();
                etAyudante.setTextColor(Color.parseColor("#969da9"));
                closeTeclado();
            }
        });


        etCarro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //carroSeleccinado = null;

                ArrayAdapter<String> adapterColonias = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.getCarrosas(etCarro.getText().toString()));
                etCarro.setAdapter(adapterColonias);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etCarro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                carroSeleccinado = etCarro.getEditableText().toString();
                etCarro.setTextColor(Color.parseColor("#969da9"));
                closeTeclado();
            }
        });


        spLugares.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lugarSeleccionado = spLugares.getItemAtPosition(position).toString();
                Log.v(TAG, "onItemSelected: lUGAR SELECCIONADO: " + lugarSeleccionado);
                closeTeclado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                lugarSeleccionado = null;
            }
        });


        spDestino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                destinoSeleccionado = spDestino.getItemAtPosition(position).toString();
                closeTeclado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                destinoSeleccionado = null;
            }
        });

        frameRegistro.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = frameRegistro.getRootView().getHeight() - frameRegistro.getHeight();
                if (heightDiff > dpToPx(getApplicationContext(), 400)) {
                    // ... do something here
                }
            }
        });


        String geofenceActual = Preferences.getGeofenceActual(NuevaBitacora.this, Preferences.PREFERENCE_GEOFENCE_ACTUAL);
        if (!geofenceActual.equals("")) {
            Log.w(TAG, "onCreate: Geofence actual: " + geofenceActual);
        } else
            Log.w(TAG, "onCreate: La Geofence esta vacia");


        if (!bitacoraFromNotification) {
            LinearLayout layoutDatos = (LinearLayout) findViewById(R.id.layoutDatos);
            layoutDatos.setVisibility(View.GONE);
        }





        TextView btAnadirEquipo = (TextView) findViewById(R.id.btAnadirEquipo);
        btAnadirEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                equipoDeInstalacion = true;
                isArticuloDeVelacion = false;
                scannerAtaurna = false;
                equipoRecoleccion = false;
                equipoDeTraslado = false;
                new IntentIntegrator(NuevaBitacora.this).initiateScan();
            }
        });

        TextView btAnadirEquipoDeTraslado = (TextView) findViewById(R.id.btAnadirEquipoTraslado);
        btAnadirEquipoDeTraslado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                equipoDeInstalacion = false;
                isArticuloDeVelacion = false;
                scannerAtaurna = false;
                equipoRecoleccion = false;
                equipoDeTraslado = true;
                new IntentIntegrator(NuevaBitacora.this).initiateScan();
            }
        });




        TextView btAnadirEquipoRecoleccion = (TextView) findViewById(R.id.btAnadirEquipoRecoleccion);
        btAnadirEquipoRecoleccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                equipoDeInstalacion = false;
                isArticuloDeVelacion = false;
                scannerAtaurna = false;
                equipoRecoleccion = true;
                equipoDeTraslado = false;
                new IntentIntegrator(NuevaBitacora.this).initiateScan();
            }
        });

    }//End onCreate


    public LatLng getCoordenadasFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return p1;
    }

    private void closeTeclado() {
        try {
            View view = this.getCurrentFocus();
            view.clearFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Throwable e) {
            Log.e(TAG, "closeTeclado: Error en cerrar teclado" + e.getMessage());
        }

    }

    private boolean checkFields() {
        return bitacoraSeleccionada != null && !bitacoraSeleccionada.equals("")
                && (choferSeleccionado != null && !etChofer.getText().toString().contains("**** FALTA DATO AQUÍ ****"))
                && (ayudanteSeleccionado != null && !etAyudante.getText().toString().contains("**** FALTA DATO AQUÍ ****"))
                && (carroSeleccinado != null && !etCarro.getText().toString().contains("**** FALTA DATO AQUÍ ****"))
                && (lugarSeleccionado != null && !lugarSeleccionado.equals("**** FALTA DATO AQUÍ ****"))
                && (movimientoSeleccionado != null && !movimientoSeleccionado.equals("**** FALTA DATO AQUÍ ****") && !spMovimiento.getSelectedItem().toString().equals("Selecciona movimiento..."))
                && (destinoSeleccionado != null && !destinoSeleccionado.equals("**** FALTA DATO AQUÍ ****"));
    }


    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    @Override
    public void onSoftKeyboardShown(boolean isShowing) {
        // do whatever you need to do here
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    public void showNavigationType(String latitudOrigen, String longitudOrigen, String latitudDestino, String longitudDestino) {
        TextView tvNoIniciarNavegacion;
        ImageView imgWaze, imgMaps;
        dialogBottomNavigation.setContentView(R.layout.layout_recorrido_mapas);
        dialogBottomNavigation.setCancelable(false);

        tvNoIniciarNavegacion = (TextView) dialogBottomNavigation.findViewById(R.id.tvNoIniciarNavegacion);
        imgWaze = (ImageView) dialogBottomNavigation.findViewById(R.id.imgWaze);
        imgMaps = (ImageView) dialogBottomNavigation.findViewById(R.id.imgMaps);

        tvNoIniciarNavegacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogBottomNavigation.isShowing())
                    dialogBottomNavigation.dismiss();
                finish();
            }
        });

        imgWaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String uri = "https://www.waze.com/ul?ll=" + latitudDestino + "," + longitudDestino + "&navigate=yes&zoom=17";
                    Intent intentWaze = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                    intentWaze.setPackage("com.waze");
                    startActivity(intentWaze);
                    onPause();
                    dialogBottomNavigation.dismiss();
                    finish();
                } catch (ActivityNotFoundException e) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze")));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.waze")));
                    }
                }
            }
        });

        imgMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:" + latitudOrigen + "," + longitudOrigen + "?z=16&q=" + latitudDestino + "," + longitudDestino + "(Destino)"));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                    onPause();
                    dialogBottomNavigation.dismiss();
                    finish();
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
                    startActivity(intent);
                }
            }
        });

        dialogBottomNavigation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBottomNavigation.show();

    }

    public void showErrorDialog(final String codeError) {
        final NeumorphButton btNo, btSi;
        TextView tvCodeError, tvBitacora;
        dialogoError.setContentView(R.layout.layout_error);
        dialogoError.setCancelable(true);
        btNo = (NeumorphButton) dialogoError.findViewById(R.id.btNo);
        btSi = (NeumorphButton) dialogoError.findViewById(R.id.btSi);
        tvCodeError = (TextView) dialogoError.findViewById(R.id.tvCodeError);
        tvBitacora = (TextView) dialogoError.findViewById(R.id.tvBitacora);
        tvCodeError.setText(codeError);

        if (codeError.equals("No se puede crear la bitácora por que la zona dónde estás, no corresponde a tu lugar seleccionado.") ||
                codeError.equals("No podemos crear la bitacora porque el destino debe ser diferente origen")) {
            btNo.setVisibility(View.GONE);
            btSi.setVisibility(View.VISIBLE);
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
            }
        });

        dialogoError.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogoError.show();
    }


    private void loadInformationAndRequest(String bitacora)
    {
        if (ApplicationResourcesProvider.checkInternetConnection())
        {
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
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_DOWNLOAD_BITACORAS_COMPLETAS, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Bitacoras.deleteAll(Bitacoras.class);
                    JSONArray bitacorasArray = new JSONArray();
                    try {
                        bitacorasArray = response.getJSONArray("catalogo");
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

                        checkAndShowDataInBoxes(bitacora);
                        dismissMyCustomDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorStackTraceBitacoras = true;
                        codigoErrorStackTraceBitacoras = e.getMessage();
                        Toast.makeText(NuevaBitacora.this, "Ocurrio un error: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            errorStackTraceBitacoras = true;
                            codigoErrorStackTraceBitacoras = error.getMessage();
                            Toast.makeText(NuevaBitacora.this, "Ocurrio un error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

            };

            postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);

        } else
            showErrorDialog("No hay conexión a internet");
    }


    private void showMyCustomDialog() {
        final FrameLayout flLoading = (FrameLayout) findViewById(R.id.layoutCargando);
        flLoading.setVisibility(View.VISIBLE);
    }

    private void dismissMyCustomDialog() {
        final FrameLayout flLoading = (FrameLayout) findViewById(R.id.layoutCargando);
        flLoading.setVisibility(View.GONE);
    }

    /*private void downloadBitacoras() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsBitacoras.WS_DOWNLOAD_BITACORAS_COMPLETAS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Bitacoras.deleteAll(Bitacoras.class);
                JSONArray bitacorasArray = new JSONArray();
                try {
                    JSONObject json = new JSONObject(response);
                    bitacorasArray = json.getJSONArray("catalogo");
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
                                "" + bitacorasArray.getJSONObject(i).getString("tipo_servicio")
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorStackTraceBitacoras = true;
                    codigoErrorStackTraceBitacoras = e.getMessage();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                errorStackTraceBitacoras = true;
                codigoErrorStackTraceBitacoras = error.getMessage();
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }*/


    private void downloadBitacoras() {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_DOWNLOAD_BITACORAS_COMPLETAS, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Bitacoras.deleteAll(Bitacoras.class);
                JSONArray bitacorasArray = new JSONArray();
                try {
                    bitacorasArray = response.getJSONArray("catalogo");
                    errorStackTraceBitacoras = false;
                    for (int i = 0; i <= bitacorasArray.length() - 1; i++) {
                        /*DatabaseAssistant.insertarBitacoras(
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
                                bitacorasArray.getJSONObject(i).has("inicio_velacion") ?  bitacorasArray.getJSONObject(i).getString("inicio_velacion") : "12:38:89",
                                bitacorasArray.getJSONObject(i).has("inicio_cortejo") ?  bitacorasArray.getJSONObject(i).getString("inicio_cortejo") : "05:34:13",
                                bitacorasArray.getJSONObject(i).has("templo") ?  bitacorasArray.getJSONObject(i).getString("templo") : "Templo de San Paracho"
                        );*/

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
                } catch (Exception e) {
                    e.printStackTrace();
                    errorStackTraceBitacoras = true;
                    codigoErrorStackTraceBitacoras = e.getMessage();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        errorStackTraceBitacoras = true;
                        codigoErrorStackTraceBitacoras = error.getMessage();
                    }
                }) {

        };

        //postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }


    private void checkAndShowDataInBoxes(String bitacora) {
        if (DatabaseAssistant.getDataBitacora(bitacora) != null) {
            bitacoraSeleccionada = bitacora;
            closeTeclado();
            String[] bitacompletaData = DatabaseAssistant.getDataBitacora(bitacoraSeleccionada);


            bitacoraSeleccionada = bitacompletaData[1];
            try {
                choferSeleccionado = bitacompletaData[3];
                etChofer.setText(bitacompletaData[3]);
            } catch (Throwable e) {
                Log.e(TAG, "onItemClick: " + e.getMessage());
                choferSeleccionado = null;
                etChofer.setText("");
            }

            try {
                ayudanteSeleccionado = bitacompletaData[4];
                etAyudante.setText(bitacompletaData[4]);
            } catch (Throwable e) {
                Log.e(TAG, "onItemClick: " + e.getMessage());
                ayudanteSeleccionado = null;
                etAyudante.setText("");
            }

            try {
                carroSeleccinado = bitacompletaData[5];
                etCarro.setText(bitacompletaData[5]);
            } catch (Throwable e) {
                Log.e(TAG, "onItemClick: " + e.getMessage());
                carroSeleccinado = null;
                etCarro.setText("");
            }

            try {
                lugarSeleccionado = bitacompletaData[6];
            } catch (Throwable e) {
                Log.e(TAG, "onItemClick: " + e.getMessage());
                lugarSeleccionado = null;
            }

            try {
                destinoSeleccionado = bitacompletaData[7];
            } catch (Throwable e) {
                Log.e(TAG, "onItemClick: " + e.getMessage());
                destinoSeleccionado = null;
            }


            TextView tvAtaud = (TextView) findViewById(R.id.tvAtaud);
            tvAtaud.setText(bitacompletaData[14]);
            LinearLayout layoutAtaud = (LinearLayout) findViewById(R.id.layoutAtaud);
            layoutAtaud.setVisibility(View.VISIBLE);

            TextView tvPanteon = (TextView) findViewById(R.id.tvPanteon);
            tvPanteon.setText(bitacompletaData[15]);
            LinearLayout layoutPanteon = (LinearLayout) findViewById(R.id.layoutPanteon);
            layoutPanteon.setVisibility(View.VISIBLE);

            TextView tvCrematorio = (TextView) findViewById(R.id.tvCrematorio);
            tvCrematorio.setText(bitacompletaData[16]);
            LinearLayout layoutSevicio = (LinearLayout) findViewById(R.id.layoutSevicio);
            layoutSevicio.setVisibility(View.VISIBLE);


            etChofer.setTextColor(Color.parseColor("#969da9"));
            etAyudante.setTextColor(Color.parseColor("#969da9"));
            etCarro.setTextColor(Color.parseColor("#969da9"));


            if (etChofer.getText().toString().equals("") || choferSeleccionado.equals("")) {
                etChofer.setTextColor(Color.RED);
                etChofer.setText("**** FALTA DATO AQUÍ ****");
                choferSeleccionado = null;
            }

            if (etAyudante.getText().toString().equals("") || ayudanteSeleccionado.equals("")) {
                etAyudante.setTextColor(Color.RED);
                etAyudante.setText("**** FALTA DATO AQUÍ ****");
                ayudanteSeleccionado = null;
            }

            if (etCarro.getText().toString().equals("") || carroSeleccinado.equals("")) {
                etCarro.setTextColor(Color.RED);
                etCarro.setText("**** FALTA DATO AQUÍ ****");
                carroSeleccinado = null;
            }

            //EXIT
            String geofenceActual = Preferences.getGeofenceActual(NuevaBitacora.this, Preferences.PREFERENCE_GEOFENCE_ACTUAL);
            if (!geofenceActual.equals("")) {
                ArrayList<String> geofenceActualPorUbicacion = new ArrayList<String>();
                geofenceActualPorUbicacion.add(geofenceActual);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, geofenceActualPorUbicacion);
                spLugares.setAdapter(adapter);
                //spLugares.setEnabled(false);
            } else {

                if (lugarSeleccionado.equals("**** FALTA DATO AQUÍ ****")) {
                    spLugares.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_style_red));
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                    spLugares.setAdapter(adapter);
                } else {
                    //String[] opciones ={lugarSeleccionado};
                    //ArrayAdapter<String> adapterLugares = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, opciones);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                    spLugares.setAdapter(adapter);
                    spLugares.setSelection(adapter.getPosition(lugarSeleccionado));
                }

            }


            if (destinoSeleccionado.equals("**** FALTA DATO AQUÍ ****")) {
                spDestino.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_style_red));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                spDestino.setAdapter(adapter);
            } else {
                //String[] opciones2 ={destinoSeleccionado};
                //ArrayAdapter<String> adapterOpciones2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, opciones2);
                ArrayAdapter<String> adapterOpciones2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                spDestino.setAdapter(adapterOpciones2);
                spDestino.setSelection(adapterOpciones2.getPosition(destinoSeleccionado));
            }

            LinearLayout layoutDatos = (LinearLayout) findViewById(R.id.layoutDatos);
            layoutDatos.setVisibility(View.VISIBLE);

        } else
            Toast.makeText(NuevaBitacora.this, "No se encontraron datos de la bitácora seleccionada", Toast.LENGTH_LONG).show();
    }


    private void ayudaTutorial() {
        final Drawable droid = ContextCompat.getDrawable(this, R.drawable.ic_help);
        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.btComentarios), "Ayuda", "Si una bitácora no aparece en la lista, actualiza tus bitácoras, de lo contrario avisa a Call Center para hacer la activación. \n El formato correcto debe ser como el siguiente ejemplo: GDL21ENE9090")
                        .outerCircleColor(R.color.red_color)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(16)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.textos_general)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        .icon(droid)                     // Specify a custom drawable to draw as the target
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        //Toast.makeText(NuevaBitacora.this, "Ayuda finalizada", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void searchBitacoraInWeb(String bitacora) {

        if (ApplicationResourcesProvider.checkInternetConnection())
        {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {

                        /*runOnUiThread(new Runnable() {
                            @Override
                            public void run() { {
                                    downloadBitacoraIndividual(bitacora);
                                }
                            }
                        });*/


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                /**Muestra el dialogo de carga loading**/
                                showMyCustomDialog();

                                /********Obtiene el token de firebase y lo actualiza o inserta en la base de datos*******/
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


                                /*********Creamos los parametros del WS de getSingleBitacora*********/
                                JSONObject params = new JSONObject();
                                try {
                                    params.put("usuario", DatabaseAssistant.getUserNameFromSesiones());
                                    params.put("token_device", DatabaseAssistant.getTokenDeUsuario());
                                    params.put("isProveedor", DatabaseAssistant.getIsProveedor());
                                    params.put("isBunker", Preferences.getPreferenceIsbunker(NuevaBitacora.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0");
                                    params.put("bitacora", bitacora);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                /**********Creamos el request*************/
                                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_DOWNLOAD_BITACORA_INDIVIDUAL, params, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        /**Declaramos los arreglos de tipo JSONArray**/
                                        JSONArray bitacorasArray = new JSONArray();
                                        JSONArray bitacoraDetallesArray = new JSONArray();
                                        JSONArray bitacorasComentariosArray = new JSONArray();
                                        JSONArray bitacorasAtaurnas = new JSONArray();

                                        try {
                                            if (response.has("bitacoraDrivers")) {

                                                /***** Borramos toda la informacion de bitacoras en la tabla local para agregar los nuevos registros********/
                                                Bitacoras.deleteAll(Bitacoras.class);

                                                try {
                                                    /** Obtenenos toda la información de bitacoraDrivers para procesarla, sini embargo solo es 1 un registro **/
                                                    bitacorasArray = response.getJSONArray("bitacoraDrivers");
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
                                                        //bitacoraSeleccionada = bitacora;
                                                        bitacoraSeleccionada = bitacorasArray.getJSONObject(i).getString("name");
                                                        bitacoraGlobal = bitacorasArray.getJSONObject(i).getString("name");
                                                    }
                                                    Log.d(TAG, "onResponse: Bitacora guardada y consultada correctamente");
                                                    errorStackTraceBitacoras = false;

                                                    /** Verificamos si la respuesta (Response)  contiene la llave "bitacorasDetails" para procesarla, sin embargo solo es 1 un registro **/
                                                    if (response.has("bitacorasDetails")) {
                                                        try {
                                                            errorStackTraceBitacorasDetalles = false;

                                                            /** Obtenenos toda la información de bitacorasDetails para procesarla, sin embargo solo es 1 un registro **/
                                                            bitacoraDetallesArray = response.getJSONArray("bitacorasDetails");

                                                            for (int i = 0; i <= bitacoraDetallesArray.length() - 1; i++)
                                                            {
                                                                String bitacoraFromResponse = bitacoraDetallesArray.getJSONObject(i).getString("bitacora");
                                                                Adicional.executeQuery("DELETE FROM ADICIONAL WHERE bitacora = '" + bitacoraFromResponse + "' and sync ='1'");
                                                                String seleccionarHora = "00:00:00";
                                                                if (bitacoraDetallesArray.getJSONObject(i).getString("hora_instalacion").equals("Selecciona aqui..."))
                                                                    seleccionarHora = "00:00:00";
                                                                else
                                                                    seleccionarHora = bitacoraDetallesArray.getJSONObject(i).getString("hora_instalacion");

                                                                DatabaseAssistant.insertarInformacionAdicional(
                                                                        "" + seleccionarHora,
                                                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("ropa_entregada"),
                                                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("lugar_de_velacion"),
                                                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("tipo_servicio"),
                                                                        "",
                                                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("observaciones_instalacion"),
                                                                        "",
                                                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("observaciones_cortejo"),
                                                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("documentos_seleccion"),
                                                                        "" + bitacoraFromResponse,
                                                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("encapsulado"),
                                                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("observaciones_recoleccion"),
                                                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("observaciones_traslado"),
                                                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("embalsamado_o_arreglo"),
                                                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("laboratorio"),
                                                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("idLaboratorio")
                                                                );


                                                                if (bitacoraDetallesArray.getJSONObject(i).has("Equipos_instalacion")) {
                                                                    Equipoinstalacion.executeQuery("DELETE FROM EQUIPOINSTALACION WHERE bitacora = '" + bitacoraFromResponse + "' and sync ='1'");
                                                                    JSONArray jsonArrayEquiposInstalacion = bitacoraDetallesArray.getJSONObject(i).getJSONArray("Equipos_instalacion");
                                                                    for (int position = 0; position <= jsonArrayEquiposInstalacion.length() - 1; position++) {


                                                                        DatabaseAssistant.insertarEquipoInstalacionFromWebService(
                                                                                "" + bitacoraFromResponse,
                                                                                "" + jsonArrayEquiposInstalacion.getJSONObject(position).getString("serie"),
                                                                                "" + jsonArrayEquiposInstalacion.getJSONObject(position).getString("nombre"),
                                                                                "" + (Preferences.getPreferenceIsbunker(NuevaBitacora.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0")
                                                                        );
                                                                    }
                                                                }

                                                                if (bitacoraDetallesArray.getJSONObject(i).has("Equipos_cortejo")) {
                                                                    Equipocortejo.executeQuery("DELETE FROM EQUIPOCORTEJO WHERE bitacora = '" + bitacoraFromResponse + "' and sync ='1'");
                                                                    JSONArray jsonArrayEquiposCortejo = bitacoraDetallesArray.getJSONObject(i).getJSONArray("Equipos_cortejo");
                                                                    for (int position = 0; position <= jsonArrayEquiposCortejo.length() - 1; position++) {

                                                                        DatabaseAssistant.insertarEquipoCortejoFromWebservice(
                                                                                "" + bitacoraFromResponse,
                                                                                "" + jsonArrayEquiposCortejo.getJSONObject(position).getString("serie"),
                                                                                "" + jsonArrayEquiposCortejo.getJSONObject(position).getString("nombre"),
                                                                                "" + (Preferences.getPreferenceIsbunker(NuevaBitacora.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0")
                                                                        );
                                                                    }
                                                                }

                                                                if (bitacoraDetallesArray.getJSONObject(i).has("Documentos_extras")) {
                                                                    Documentos.executeQuery("DELETE FROM DOCUMENTOS WHERE bitacora = '" + bitacoraFromResponse + "' and sync ='1'");
                                                                    JSONArray jsonArrayDocumentosExtras = bitacoraDetallesArray.getJSONObject(i).getJSONArray("Documentos_extras");
                                                                    for (int position = 0; position <= jsonArrayDocumentosExtras.length() - 1; position++) {

                                                                        DatabaseAssistant.insertarDocumentoExtraFromWebService(
                                                                                "" + bitacoraFromResponse,
                                                                                "" + jsonArrayDocumentosExtras.getJSONObject(position).getString("nombre")
                                                                        );

                                                                    }
                                                                }
                                                            }
                                                            errorStackTraceBitacorasDetalles = false;
                                                            Log.d(TAG, "onResponse: Detalles registrados correctamente");


                                                            //*************************** COMENTARIOS EDE BITACORA **********************
                                                            if (response.has("comments")) {
                                                                try {
                                                                    errorStackTraceBitacorasComentarios = false;
                                                                    bitacorasComentariosArray = response.getJSONArray("comments");
                                                                    Comentarios.executeQuery("DELETE FROM COMENTARIOS WHERE bitacora = '" + bitacorasComentariosArray.getJSONObject(0).getString("bitacora") + "' and sync ='1'");

                                                                    for (int i = 0; i <= bitacorasComentariosArray.length() - 1; i++) {
                                                                        DatabaseAssistant.insertarComentarios(
                                                                                "" + bitacorasComentariosArray.getJSONObject(i).getString("bitacora"),
                                                                                "" + bitacorasComentariosArray.getJSONObject(i).getString("comentario"),
                                                                                "" + bitacorasComentariosArray.getJSONObject(i).getString("usuario"),
                                                                                "" + bitacorasComentariosArray.getJSONObject(i).getString("fecha_captura"),
                                                                                "1",
                                                                                DatabaseAssistant.getUserNameFromSesiones().equals(bitacorasComentariosArray.getJSONObject(i).getString("usuario")) ? "1" : "0"
                                                                        );
                                                                    }
                                                                    Log.d(TAG, "onResponse: Comentarios actualizados");

                                                                } catch (JSONException e) {
                                                                    errorStackTraceBitacorasComentarios = true;
                                                                    e.printStackTrace();
                                                                    Toast.makeText(NuevaBitacora.this, "Ocurrio un error en consulta de comentarios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                            //*************************************************************************


                                                            //*************************** CONSULTA DE ATAURNAS DESDE WS **********************
                                                            if (response.has("ataurna")) {
                                                                try {
                                                                    errorStackTraceAtaurna = false;
                                                                    bitacorasAtaurnas = response.getJSONArray("ataurna");
                                                                    Inventario.executeQuery("DELETE FROM INVENTARIO WHERE bitacora = '" + "" + bitacorasAtaurnas.getJSONObject(0).getString("bitacora") + "' and sync ='1'");

                                                                    for (int i = 0; i <= bitacorasAtaurnas.length() - 1; i++) {

                                                                        DatabaseAssistant.insertarInventario(
                                                                                "" + bitacorasAtaurnas.getJSONObject(i).getString("codigo_ataurna"),
                                                                                "" + bitacorasAtaurnas.getJSONObject(i).getString("item_ataurna"),
                                                                                "" + bitacorasAtaurnas.getJSONObject(i).getString("serie_ataurna"),
                                                                                "" + bitacorasAtaurnas.getJSONObject(i).getString("fecha_ataurna"),
                                                                                "",
                                                                                "" + bitacorasAtaurnas.getJSONObject(i).getString("bitacora"),
                                                                                "0"
                                                                        );

                                                                    }
                                                                    Log.d(TAG, "onResponse: Ataurnas actualizadas");

                                                                } catch (JSONException e) {
                                                                    errorStackTraceAtaurna = true;
                                                                    e.printStackTrace();
                                                                    Log.e(TAG, "onResponse: Ocurrio un error en Consulta de Ataurnas: " + e.getMessage());
                                                                    Toast.makeText(NuevaBitacora.this, "Ocurrio un error en consulta de ataúdes y urnas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                            //*************************************************************************





                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            errorStackTraceBitacorasDetalles = true;
                                                            Log.e(TAG, "onResponse: Error en bitacora details al descargar: " + e.getMessage());
                                                            Toast.makeText(NuevaBitacora.this, "Ocurrio un error en la búsqueda de la bitácora.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    //*******************************************************************************************************************

                                                    try {
                                                        consultarEquiposDeInstalacion(bitacoraGlobal);
                                                        consultarEquiposDeRecoleccion(bitacoraGlobal);
                                                        consultarEquiposDeTraslado(bitacoraGlobal);
                                                    }catch (Throwable e){
                                                        Log.e(TAG, "onResume: Error en consulta: " + e.getMessage());
                                                    }
                                                } catch (Throwable e) {
                                                    errorStackTraceBitacoras = true;
                                                    Log.e(TAG, "onResponse: Error al consultar y guardar la bitacora individual: " + e.getMessage());
                                                }
                                            }
                                            //**********************************************************************************************


                                            // write code here...
                                            if (DatabaseAssistant.getDataBitacora(bitacora.toUpperCase()) != null) {
                                                closeTeclado();
                                                String[] bitacompletaData = DatabaseAssistant.getDataBitacora(bitacora.toUpperCase());

                                                bitacoraSeleccionada = bitacompletaData[1];
                                                try {
                                                    choferSeleccionado = bitacompletaData[3];
                                                    etChofer.setText(bitacompletaData[3]);
                                                } catch (Throwable e) {
                                                    Log.e(TAG, "onItemClick: " + e.getMessage());
                                                    choferSeleccionado = null;
                                                    etChofer.setText("");
                                                }

                                                try {
                                                    ayudanteSeleccionado = bitacompletaData[4];
                                                    etAyudante.setText(bitacompletaData[4]);
                                                } catch (Throwable e) {
                                                    Log.e(TAG, "onItemClick: " + e.getMessage());
                                                    ayudanteSeleccionado = null;
                                                    etAyudante.setText("");
                                                }

                                                try {
                                                    carroSeleccinado = bitacompletaData[5];
                                                    etCarro.setText(bitacompletaData[5]);
                                                } catch (Throwable e) {
                                                    Log.e(TAG, "onItemClick: " + e.getMessage());
                                                    carroSeleccinado = null;
                                                    etCarro.setText("");
                                                }

                                                try {
                                                    lugarSeleccionado = bitacompletaData[6];
                                                } catch (Throwable e) {
                                                    Log.e(TAG, "onItemClick: " + e.getMessage());
                                                    lugarSeleccionado = null;
                                                }

                                                try {
                                                    destinoSeleccionado = bitacompletaData[7];
                                                } catch (Throwable e) {
                                                    Log.e(TAG, "onItemClick: " + e.getMessage());
                                                    destinoSeleccionado = null;
                                                }


                                                TextView tvAtaud = (TextView) findViewById(R.id.tvAtaud);
                                                tvAtaud.setText(bitacompletaData[14]);
                                                LinearLayout layoutAtaud = (LinearLayout) findViewById(R.id.layoutAtaud);
                                                layoutAtaud.setVisibility(View.VISIBLE);

                                                TextView tvPanteon = (TextView) findViewById(R.id.tvPanteon);
                                                tvPanteon.setText(bitacompletaData[15]);
                                                LinearLayout layoutPanteon = (LinearLayout) findViewById(R.id.layoutPanteon);
                                                layoutPanteon.setVisibility(View.VISIBLE);

                                                TextView tvCrematorio = (TextView) findViewById(R.id.tvCrematorio);
                                                tvCrematorio.setText(bitacompletaData[16]);
                                                LinearLayout layoutSevicio = (LinearLayout) findViewById(R.id.layoutSevicio);
                                                layoutSevicio.setVisibility(View.VISIBLE);

                                                etChofer.setTextColor(Color.parseColor("#969da9"));
                                                etAyudante.setTextColor(Color.parseColor("#969da9"));
                                                etCarro.setTextColor(Color.parseColor("#969da9"));


                                                if (etChofer.getText().toString().equals("") || choferSeleccionado.equals("")) {
                                                    etChofer.setTextColor(Color.RED);
                                                    etChofer.setText("**** FALTA DATO AQUÍ ****");
                                                    choferSeleccionado = null;
                                                }

                                                if (etAyudante.getText().toString().equals("") || ayudanteSeleccionado.equals("")) {
                                                    etAyudante.setTextColor(Color.RED);
                                                    etAyudante.setText("**** FALTA DATO AQUÍ ****");
                                                    ayudanteSeleccionado = null;
                                                }

                                                if (etCarro.getText().toString().equals("") || carroSeleccinado.equals("")) {
                                                    etCarro.setTextColor(Color.RED);
                                                    etCarro.setText("**** FALTA DATO AQUÍ ****");
                                                    carroSeleccinado = null;
                                                }

                                                String geofenceActual = Preferences.getGeofenceActual(NuevaBitacora.this, Preferences.PREFERENCE_GEOFENCE_ACTUAL);
                                                if (!geofenceActual.equals("")) {
                                                    ArrayList<String> geofenceActualPorUbicacion = new ArrayList<String>();
                                                    geofenceActualPorUbicacion.add(geofenceActual);
                                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, geofenceActualPorUbicacion);
                                                    spLugares.setAdapter(adapter);
                                                    spLugares.setEnabled(false);
                                                } else {

                                                    if (lugarSeleccionado.equals("**** FALTA DATO AQUÍ ****")) {
                                                        spLugares.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_style_red));
                                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                                                        spLugares.setAdapter(adapter);
                                                    } else {
                                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                                                        spLugares.setAdapter(adapter);
                                                        spLugares.setSelection(adapter.getPosition(lugarSeleccionado));
                                                    }

                                                }


                                                if (destinoSeleccionado.equals("**** FALTA DATO AQUÍ ****")) {
                                                    spDestino.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_style_red));
                                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                                                    spDestino.setAdapter(adapter);
                                                } else {
                                                    ArrayAdapter<String> adapterOpciones2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                                                    spDestino.setAdapter(adapterOpciones2);
                                                    spDestino.setSelection(adapterOpciones2.getPosition(destinoSeleccionado));
                                                }

                                                LinearLayout layoutDatos = (LinearLayout) findViewById(R.id.layoutDatos);
                                                layoutDatos.setVisibility(View.VISIBLE);

                                            } else {
                                                Toast.makeText(NuevaBitacora.this, "No se encontraron datos de la bitácora seleccionada", Toast.LENGTH_LONG).show();
                                            }


                                            dismissMyCustomDialog();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            errorStackTraceBitacoras = true;
                                            codigoErrorStackTraceBitacoras = e.getMessage();
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
                                            }
                                        }) {

                                };

                                postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);


                                /*******************************************/
                            }
                        });


                        synchronized (this) {
                            wait(2000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // write code here...
                                    if (DatabaseAssistant.getDataBitacora(bitacora.toUpperCase()) != null) {
                                        closeTeclado();
                                        String[] bitacompletaData = DatabaseAssistant.getDataBitacora(bitacora.toUpperCase());

                                        bitacoraSeleccionada = bitacompletaData[1];
                                        try {
                                            choferSeleccionado = bitacompletaData[3];
                                            etChofer.setText(bitacompletaData[3]);
                                        } catch (Throwable e) {
                                            Log.e(TAG, "onItemClick: " + e.getMessage());
                                            choferSeleccionado = null;
                                            etChofer.setText("");
                                        }

                                        try {
                                            ayudanteSeleccionado = bitacompletaData[4];
                                            etAyudante.setText(bitacompletaData[4]);
                                        } catch (Throwable e) {
                                            Log.e(TAG, "onItemClick: " + e.getMessage());
                                            ayudanteSeleccionado = null;
                                            etAyudante.setText("");
                                        }

                                        try {
                                            carroSeleccinado = bitacompletaData[5];
                                            etCarro.setText(bitacompletaData[5]);
                                        } catch (Throwable e) {
                                            Log.e(TAG, "onItemClick: " + e.getMessage());
                                            carroSeleccinado = null;
                                            etCarro.setText("");
                                        }

                                        try {
                                            lugarSeleccionado = bitacompletaData[6];
                                        } catch (Throwable e) {
                                            Log.e(TAG, "onItemClick: " + e.getMessage());
                                            lugarSeleccionado = null;
                                        }

                                        try {
                                            destinoSeleccionado = bitacompletaData[7];
                                        } catch (Throwable e) {
                                            Log.e(TAG, "onItemClick: " + e.getMessage());
                                            destinoSeleccionado = null;
                                        }


                                        TextView tvAtaud = (TextView) findViewById(R.id.tvAtaud);
                                        tvAtaud.setText(bitacompletaData[14]);
                                        LinearLayout layoutAtaud = (LinearLayout) findViewById(R.id.layoutAtaud);
                                        layoutAtaud.setVisibility(View.VISIBLE);

                                        TextView tvPanteon = (TextView) findViewById(R.id.tvPanteon);
                                        tvPanteon.setText(bitacompletaData[15]);
                                        LinearLayout layoutPanteon = (LinearLayout) findViewById(R.id.layoutPanteon);
                                        layoutPanteon.setVisibility(View.VISIBLE);

                                        TextView tvCrematorio = (TextView) findViewById(R.id.tvCrematorio);
                                        tvCrematorio.setText(bitacompletaData[16]);
                                        LinearLayout layoutSevicio = (LinearLayout) findViewById(R.id.layoutSevicio);
                                        layoutSevicio.setVisibility(View.VISIBLE);

                                        etChofer.setTextColor(Color.parseColor("#969da9"));
                                        etAyudante.setTextColor(Color.parseColor("#969da9"));
                                        etCarro.setTextColor(Color.parseColor("#969da9"));


                                        if (etChofer.getText().toString().equals("") || choferSeleccionado.equals("")) {
                                            etChofer.setTextColor(Color.RED);
                                            etChofer.setText("**** FALTA DATO AQUÍ ****");
                                            choferSeleccionado = null;
                                        }

                                        if (etAyudante.getText().toString().equals("") || ayudanteSeleccionado.equals("")) {
                                            etAyudante.setTextColor(Color.RED);
                                            etAyudante.setText("**** FALTA DATO AQUÍ ****");
                                            ayudanteSeleccionado = null;
                                        }

                                        if (etCarro.getText().toString().equals("") || carroSeleccinado.equals("")) {
                                            etCarro.setTextColor(Color.RED);
                                            etCarro.setText("**** FALTA DATO AQUÍ ****");
                                            carroSeleccinado = null;
                                        }

                                        String geofenceActual = Preferences.getGeofenceActual(NuevaBitacora.this, Preferences.PREFERENCE_GEOFENCE_ACTUAL);
                                        if (!geofenceActual.equals("")) {
                                            ArrayList<String> geofenceActualPorUbicacion = new ArrayList<String>();
                                            geofenceActualPorUbicacion.add(geofenceActual);
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, geofenceActualPorUbicacion);
                                            spLugares.setAdapter(adapter);
                                            spLugares.setEnabled(false);
                                        } else {

                                            if (lugarSeleccionado.equals("**** FALTA DATO AQUÍ ****")) {
                                                spLugares.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_style_red));
                                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                                                spLugares.setAdapter(adapter);
                                            } else {
                                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                                                spLugares.setAdapter(adapter);
                                                spLugares.setSelection(adapter.getPosition(lugarSeleccionado));
                                            }

                                        }


                                        if (destinoSeleccionado.equals("**** FALTA DATO AQUÍ ****")) {
                                            spDestino.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_style_red));
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                                            spDestino.setAdapter(adapter);
                                        } else {
                                            ArrayAdapter<String> adapterOpciones2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, DatabaseAssistant.pedirLugaresDeOrigenYDestino());
                                            spDestino.setAdapter(adapterOpciones2);
                                            spDestino.setSelection(adapterOpciones2.getPosition(destinoSeleccionado));
                                        }

                                        LinearLayout layoutDatos = (LinearLayout) findViewById(R.id.layoutDatos);
                                        layoutDatos.setVisibility(View.VISIBLE);

                                    } else {
                                        Toast.makeText(NuevaBitacora.this, "No se encontraron datos de la bitácora seleccionada", Toast.LENGTH_LONG).show();
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

        } else
            showErrorDialog("No hay conexión a internet");

    }


  /*  private void downloadBitacoraIndividual(String bitacoraParametro){

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
            params.put("isBunker", Preferences.getPreferenceIsbunker(NuevaBitacora.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0");
            params.put("bitacora", bitacoraParametro);
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
                    //****************************** BITACORA DATA ***********************************************
                    if(response.has("bitacoraDrivers"))
                    {
                        try {
                            bitacorasArray = response.getJSONArray("bitacoraDrivers");
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
                        }catch (Throwable e){
                            errorStackTraceBitacoras = true;
                            Log.e(TAG, "onResponse: Error al consultar y guardar la bitacora individual: " + e.getMessage());
                        }
                    }
                    //**********************************************************************************************



                    //****************************DETALLES***********************************
                    if(response.has("bitacorasDetails")){
                        try {
                            errorStackTraceBitacorasDetalles = false;
                            bitacoraDetallesArray = response.getJSONArray("bitacorasDetails");

                            for (int i = 0; i <= bitacoraDetallesArray.length() - 1; i++)
                            {
                                String bitacoraFromResponse = bitacoraDetallesArray.getJSONObject(i).getString("bitacora");
                                Adicional.executeQuery("DELETE FROM ADICIONAL WHERE bitacora = '" + bitacoraFromResponse + "' and sync ='1'");
                                String seleccionarHora="00:00:00";
                                if (bitacoraDetallesArray.getJSONObject(i).getString("hora_instalacion").equals("Selecciona aqui..."))
                                    seleccionarHora = "00:00:00";
                                else
                                    seleccionarHora = bitacoraDetallesArray.getJSONObject(i).getString("hora_instalacion");

                                DatabaseAssistant.insertarInformacionAdicional(
                                        "" + seleccionarHora,
                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("ropa_entregada"),
                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("lugar_de_velacion"),
                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("tipo_servicio"),
                                        "",
                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("observaciones_instalacion"),
                                        "",
                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("observaciones_cortejo"),
                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("documentos_seleccion"),
                                        "" + bitacoraFromResponse,
                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("encapsulado"),
                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("observaciones_recoleccion"),
                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("observaciones_traslado"),
                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("embalsamado_o_arreglo"),
                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("laboratorio"),
                                        "" + bitacoraDetallesArray.getJSONObject(i).getString("idLaboratorio")
                                );


                                if(bitacoraDetallesArray.getJSONObject(i).has("Equipos_instalacion")) {
                                    Equipoinstalacion.executeQuery("DELETE FROM EQUIPOINSTALACION WHERE bitacora = '" + bitacoraFromResponse + "' and sync ='1'");
                                    JSONArray jsonArrayEquiposInstalacion = bitacoraDetallesArray.getJSONObject(i).getJSONArray("Equipos_instalacion");
                                    for (int position = 0; position <= jsonArrayEquiposInstalacion.length() - 1; position++) {


                                        DatabaseAssistant.insertarEquipoInstalacionFromWebService(
                                                "" + bitacoraFromResponse,
                                                "" + jsonArrayEquiposInstalacion.getJSONObject(position).getString("serie"),
                                                "" + jsonArrayEquiposInstalacion.getJSONObject(position).getString("nombre"),
                                                "" + (Preferences.getPreferenceIsbunker(NuevaBitacora.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0")
                                        );
                                    }
                                }

                                if(bitacoraDetallesArray.getJSONObject(i).has("Equipos_cortejo")) {
                                    Equipocortejo.executeQuery("DELETE FROM EQUIPOCORTEJO WHERE bitacora = '" + bitacoraFromResponse + "' and sync ='1'");
                                    JSONArray jsonArrayEquiposCortejo = bitacoraDetallesArray.getJSONObject(i).getJSONArray("Equipos_cortejo");
                                    for (int position = 0; position <= jsonArrayEquiposCortejo.length() - 1; position++) {

                                        DatabaseAssistant.insertarEquipoCortejoFromWebservice(
                                                "" + bitacoraFromResponse,
                                                "" + jsonArrayEquiposCortejo.getJSONObject(position).getString("serie"),
                                                "" + jsonArrayEquiposCortejo.getJSONObject(position).getString("nombre"),
                                                "" + (Preferences.getPreferenceIsbunker(NuevaBitacora.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0")
                                        );
                                    }
                                }

                                if(bitacoraDetallesArray.getJSONObject(i).has("Documentos_extras")) {
                                    Documentos.executeQuery("DELETE FROM DOCUMENTOS WHERE bitacora = '" + bitacoraFromResponse + "' and sync ='1'");
                                    JSONArray jsonArrayDocumentosExtras = bitacoraDetallesArray.getJSONObject(i).getJSONArray("Documentos_extras");
                                    for (int position = 0; position <= jsonArrayDocumentosExtras.length() - 1; position++) {

                                        DatabaseAssistant.insertarDocumentoExtraFromWebService(
                                                "" + bitacoraFromResponse,
                                                "" + jsonArrayDocumentosExtras.getJSONObject(position).getString("nombre")
                                        );

                                    }
                                }
                            }
                            errorStackTraceBitacorasDetalles = false;
                            Log.d(TAG, "onResponse: Detalles registrados correctamente");
                        } catch (Exception e) {
                            e.printStackTrace();
                            errorStackTraceBitacorasDetalles = true;
                            Log.e(TAG, "onResponse: Error en bitacora details al descargar: " + e.getMessage());
                        }
                    }
                    //*******************************************************************************************************************


                    //*************************** COMENTARIOS EDE BITACORA **********************
                    if(response.has("comments")){
                        try
                        {
                            errorStackTraceBitacorasComentarios = false;
                            bitacorasComentariosArray = response.getJSONArray("comments");
                            Comentarios.executeQuery("DELETE FROM COMENTARIOS WHERE bitacora = '" +  bitacorasComentariosArray.getJSONObject(0).getString("bitacora") + "' and sync ='1'");

                            for(int i =0; i<=bitacorasComentariosArray.length()-1;i++)
                            {
                                DatabaseAssistant.insertarComentarios(
                                        "" + bitacorasComentariosArray.getJSONObject(i).getString("bitacora"),
                                        "" + bitacorasComentariosArray.getJSONObject(i).getString("comentario"),
                                        "" + bitacorasComentariosArray.getJSONObject(i).getString("usuario"),
                                        "" + bitacorasComentariosArray.getJSONObject(i).getString("fecha_captura"),
                                        "1",
                                        DatabaseAssistant.getUserNameFromSesiones().equals(bitacorasComentariosArray.getJSONObject(i).getString("usuario")) ? "1" :"0"
                                );
                            }
                            Log.d(TAG, "onResponse: Comentarios actualizados");

                        } catch (JSONException e) {
                            errorStackTraceBitacorasComentarios = true;
                            e.printStackTrace();
                        }
                    }
                    //*************************************************************************











                } catch (Exception e) {
                    e.printStackTrace();
                    errorStackTraceBitacoras = true;
                    codigoErrorStackTraceBitacoras = e.getMessage();
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
                    }
                }) {

        };

        //postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }
    */




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() != null) {

                if(equipoDeInstalacion) {

                    if(result.getContents().length()<=6) {
                        boolean guardarEquipo = false, guardar4Candelabros = false;
                        String nombreDeEquipo = "DESCONOCIDO", inicialesDeEquipo = "";

                        if (!result.getContents().contains("AA")) {

                            inicialesDeEquipo = result.getContents().substring(0, 2);
                            List<CatalogoArticulos> catalogoArticulosList = CatalogoArticulos.findWithQuery(CatalogoArticulos.class, "SELECT * FROM catalogo_Articulos WHERE letras ='" + inicialesDeEquipo + "'");
                            if (catalogoArticulosList.size() > 0) {
                                Log.d(TAG, "onActivityResult: Azael si encontre información de articulos con las letras: " + inicialesDeEquipo);
                                nombreDeEquipo = catalogoArticulosList.get(0).getNombre();
                                guardarEquipo = true;

                                if (inicialesDeEquipo.equals("CB")) {
                                    guardar4Candelabros = true;
                                }
                            } else {
                                nombreDeEquipo = "DESCONOCIDO";
                                guardarEquipo = false;
                                guardar4Candelabros = false;
                            }

                            if (!nombreDeEquipo.equals("DESCONOCIDO") && !(inicialesDeEquipo.equals("CL") && nombreDeEquipo.equals("Candelabro"))) {
                                guardarEquipo = !DatabaseAssistant.yaExisteInformacionDelArticuloDeVelacionDeInstalacion("" + result.getContents(), "" + bitacoraGlobal);
                            }

                            if (guardarEquipo) {
                                DatabaseAssistant.insertarEquipoInstalacion(
                                        "" + bitacoraGlobal,
                                        "" + result.getContents(),
                                        "" + nombreDeEquipo,
                                        "" + (Preferences.getPreferenceIsbunker(getApplicationContext(), Preferences.PREFERENCE_ISBUNKER) ? "1" : "0"),
                                        "4"
                                );

                                if (guardar4Candelabros) {
                                    for (int i = 0; i <= 3; i++) {
                                        int random = (int) Math.floor(Math.random() * 247 + 1);
                                        String randomString = String.valueOf(random);
                                        String s = randomString.length() == 2 ? "CL00" : randomString.length() == 3 ? "CL0" : randomString.length() == 1 ? "CL000" : "CL000";
                                        String serie = s + random;
                                        DatabaseAssistant.insertarEquipoInstalacion(
                                                "" + bitacoraGlobal,
                                                serie,
                                                "Candelabro",
                                                "" + (Preferences.getPreferenceIsbunker(getApplicationContext(), Preferences.PREFERENCE_ISBUNKER) ? "1" : "0"),
                                                "4"
                                        );
                                    }
                                } else
                                    Log.d(TAG, "onActivityResult: No se guardaran candelabros");

                                consultarEquiposDeInstalacion(bitacoraGlobal);
                                Toast.makeText(this, "Equipo guardado correctamente", Toast.LENGTH_SHORT).show();

                            } else {
                                showErrorDialog("Código repetido o desconocido para articulos de instalación, verifica nuevamente");
                            }


                        }
                        else
                            showErrorDialog("Código repetido o desconocido para articulos de instalación, verifica nuevamente");
                    }
                    else {
                        showErrorDialog("El código no corresponde a un artículo de velación, verifica nuevamente.");
                    }


                }else if(equipoDeTraslado){

                    if(result.getContents().length()<=6) {


                        boolean guardarEquipo = false, guardar4Candelabros = false;
                        String nombreDeEquipo = "DESCONOCIDO", inicialesDeEquipo = "", entradaOsalida = "";

                        if (!result.getContents().contains("AA")) {
                            inicialesDeEquipo = result.getContents().substring(0, 2);
                            List<CatalogoArticulos> catalogoArticulosList = CatalogoArticulos.findWithQuery(CatalogoArticulos.class, "SELECT * FROM catalogo_Articulos WHERE letras ='" + inicialesDeEquipo + "'");
                            if (catalogoArticulosList.size() > 0) {
                                Log.d(TAG, "onActivityResult: Azael si encontre información de articulos con las letras: " + inicialesDeEquipo);
                                nombreDeEquipo = catalogoArticulosList.get(0).getNombre();
                                guardarEquipo = true;

                                if (inicialesDeEquipo.equals("CB")) {
                                    guardar4Candelabros = true;
                                }
                            } else {
                                nombreDeEquipo = "DESCONOCIDO";
                                guardarEquipo = false;
                                guardar4Candelabros = false;
                            }



                            //if (!nombreDeEquipo.equals("DESCONOCIDO") && !((result.getContents().contains("CL") && nombreDeEquipo.equals("Candelabro")) || (result.getContents().contains("CB") && nombreDeEquipo.equals("Cristo y base"))))
                            //  guardarEquipo = !DatabaseAssistant.yaExisteInformacionDelArticuloDeVelacionDeTraslado("" + result.getContents(), "" + bitacora);

                            if (guardarEquipo) {
                                if (DatabaseAssistant.entradaSalidaTraslado("" + result.getContents(), "" + bitacoraGlobal).equals("1")) {
                                    showErrorDialog("El código ya tiene un registro de salida y entrada de inventario.");
                                } else {
                                    if (DatabaseAssistant.entradaSalidaTraslado("" + result.getContents(), "" + bitacoraGlobal).equals("")) {
                                        entradaOsalida = "0";
                                    } else if (DatabaseAssistant.entradaSalidaTraslado("" + result.getContents(), "" + bitacoraGlobal).equals("0")) {
                                        entradaOsalida = "1";
                                    }


                                    DatabaseAssistant.insertarEquipoTraslado(
                                            "" + bitacoraGlobal,
                                            "" + result.getContents(),
                                            nombreDeEquipo,
                                            entradaOsalida,
                                            "" + (Preferences.getPreferenceIsbunker(getApplicationContext(), Preferences.PREFERENCE_ISBUNKER) ? "1" : "0"),
                                            "4"
                                    );
                                    if (guardar4Candelabros) {
                                        for (int i = 0; i <= 3; i++) {
                                            int random = (int) Math.floor(Math.random() * 247 + 1);
                                            String randomString = String.valueOf(random);
                                            String s = randomString.length() == 2 ? "CL00" : randomString.length() == 3 ? "CL0" : randomString.length() == 1 ? "CL000" : "CL000";
                                            String serie = s + random;
                                            DatabaseAssistant.insertarEquipoTraslado(
                                                    "" + bitacoraGlobal,
                                                    serie,
                                                    "Candelabro",
                                                    entradaOsalida,
                                                    "" + (Preferences.getPreferenceIsbunker(getApplicationContext(), Preferences.PREFERENCE_ISBUNKER) ? "1" : "0"),
                                                    "4"
                                            );
                                        }
                                    } else
                                        Log.d(TAG, "onActivityResult: No se guardaran candelabros");

                                    consultarEquiposDeTraslado(bitacoraGlobal);
                                    Toast.makeText(this, "Equipo guardado correctamente", Toast.LENGTH_SHORT).show();

                                }
                            } else
                                showErrorDialog("Código repetido o desconocido para articulos de Traslado, verifica nuevamente");

                        } else
                            showErrorDialog("Código repetido o desconocido para articulos de Traslado, verifica nuevamente");
                    }
                    else {
                        showErrorDialog("El código no corresponde a un artículo de velación, verifica nuevamente.");
                    }

                }
                else if(equipoRecoleccion){
                    if(result.getContents().length()<=6) {


                        boolean guardarEquipo = false, guardar4Candelabros = false;
                        String nombreDeEquipo = "DESCONOCIDO", inicialesDeEquipo="", entradaOsalida="";

                        inicialesDeEquipo = result.getContents().substring(0, 2);
                        List<CatalogoArticulos> catalogoArticulosList = CatalogoArticulos.findWithQuery(CatalogoArticulos.class, "SELECT * FROM catalogo_Articulos WHERE letras ='"+ inicialesDeEquipo + "'");
                        if(catalogoArticulosList.size() > 0 ){
                            Log.d(TAG, "onActivityResult: Azael si encontre información de articulos con las letras: " + inicialesDeEquipo);
                            nombreDeEquipo = catalogoArticulosList.get(0).getNombre();
                            guardarEquipo = true;

                            if(inicialesDeEquipo.equals("CB")){
                                guardar4Candelabros = true;
                            }
                        }
                        else{
                            nombreDeEquipo = "DESCONOCIDO";
                            guardarEquipo = false;
                            guardar4Candelabros = false;
                        }







                        /* Esta validación sirve para no permitir agregar otro registro (entrada y salida) que no sea Candelabro o cristo base NO BORRAR*/
                        //NO BORRAR
                        //NO BORRAR
                        //NO BORRAR
                        //NO BORRAR
                        //if(!nombreDeEquipo.equals("DESCONOCIDO") && !(   ( result.getContents().contains("CL") && nombreDeEquipo.equals("Candelabro") ) || (   result.getContents().contains("CB") && nombreDeEquipo.equals("Cristo y base")   )      ))
                        //  guardarEquipo = !DatabaseAssistant.yaExisteInformacionDelArticuloDeVelacionDeRecoleccion("" + result.getContents(), "" + bitacora);


                        if (guardarEquipo) {
                            if (DatabaseAssistant.entradaSalidaRecoleccion("" + result.getContents(), "" + bitacoraGlobal).equals("1")) {
                                Toast.makeText(this, "No se guarda ningun dato porque ya tiene un registro de entrada y salida", Toast.LENGTH_LONG).show();
                                showErrorDialog("El código ya tiene un registro de salida y entrada de inventario.");
                            } else {
                                if (DatabaseAssistant.entradaSalidaRecoleccion("" + result.getContents(), "" + bitacoraGlobal).equals("")) {
                                    entradaOsalida = "0";
                                } else if (DatabaseAssistant.entradaSalidaRecoleccion("" + result.getContents(), "" + bitacoraGlobal).equals("0")) {
                                    entradaOsalida = "1";
                                }

                                DatabaseAssistant.insertarEquipoRecoleccion(
                                        "" + bitacoraGlobal,
                                        "" + result.getContents(),
                                        nombreDeEquipo,
                                        entradaOsalida,
                                        "" + (Preferences.getPreferenceIsbunker(getApplicationContext(), Preferences.PREFERENCE_ISBUNKER) ? "1" : "0"),
                                        "4"
                                );

                                if (guardar4Candelabros) {
                                    for (int i = 0; i <= 3; i++) {
                                        int random = (int) Math.floor(Math.random() * 247 + 1);
                                        String randomString = String.valueOf(random);
                                        String s = randomString.length() == 2 ? "CL00" : randomString.length() == 3 ? "CL0" : randomString.length() == 1 ? "CL000" : "CL000";
                                        String serie = s + random;
                                        DatabaseAssistant.insertarEquipoRecoleccion(
                                                "" + bitacoraGlobal,
                                                serie,
                                                "Candelabro",
                                                entradaOsalida,
                                                "" + (Preferences.getPreferenceIsbunker(getApplicationContext(), Preferences.PREFERENCE_ISBUNKER) ? "1" : "0"),
                                                "4"
                                        );
                                    }
                                } else
                                    Log.d(TAG, "onActivityResult: No se guardaran candelabros");

                                consultarEquiposDeRecoleccion(bitacoraGlobal);
                                Toast.makeText(this, "Equipo guardado correctamente", Toast.LENGTH_SHORT).show();

                            }


                        } else
                            showErrorDialog("Código repetido o desconocido para articulos de recolección, verifica nuevamente");

                    }
                    else{
                        showErrorDialog("El código no corresponde a un artículo de velación, verifica nuevamente.");
                    }

                } else {
                    Log.d(TAG, "onActivityResult: No se guarda en ningun lugar");
                    Toast.makeText(this, "No se puede guardar el código", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "No se puede escanear el código.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void consultarEquiposDeInstalacion(String bitacora) {
        RecyclerView rvDocumentos;
        GridLayoutManager gridLayoutManager;
        rvDocumentos = (RecyclerView) findViewById(R.id.rvEquiposEscaneadosInstalacion);
        rvDocumentos.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rvDocumentos.setLayoutManager(gridLayoutManager);
        List<Equipoinstalacion> modelDocumentos = new ArrayList<>();
        AdapterEquiposInstalacion adapterDocumentos = null;

        List<Equipoinstalacion> documentosList = Equipoinstalacion.findWithQuery(Equipoinstalacion.class, "SELECT * FROM EQUIPOINSTALACION WHERE bitacora = '" + bitacora + "' ORDER BY id ASC");
        if (documentosList.size() > 0) {
            rvDocumentos.setVisibility(View.VISIBLE);
            //frameSinDatos.setVisibility(View.GONE);
            modelDocumentos.clear();
            for (int i = 0; i <= documentosList.size() - 1; i++) {
                Equipoinstalacion product = new Equipoinstalacion(
                        "" + documentosList.get(i).getBitacora(),
                        "" + documentosList.get(i).getSerie(),
                        "" + documentosList.get(i).getFecha(),
                        "" + documentosList.get(i).getSync(),
                        "" + documentosList.get(i).getNombre(),
                        "",
                        "",
                        "",
                        ""
                );
                modelDocumentos.add(product);
            }

            adapterDocumentos = new AdapterEquiposInstalacion(getApplicationContext(), modelDocumentos, NuevaBitacora.this);
            rvDocumentos.setAdapter(adapterDocumentos);
        } else {
            modelDocumentos.clear();
            adapterDocumentos = null;
            rvDocumentos.setVisibility(View.GONE);
            //frameSinDatos.setVisibility(View.VISIBLE);
        }
    }

    private void consultarEquiposDeTraslado(String bitacora) {
        RecyclerView rvDocumentos;
        GridLayoutManager gridLayoutManager;
        rvDocumentos = (RecyclerView) findViewById(R.id.rvEquiposEscaneadosTraslado);
        rvDocumentos.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rvDocumentos.setLayoutManager(gridLayoutManager);
        List<EquipoTraslado> modelDocumentos = new ArrayList<>();
        AdapterEquiposTraslado adapterDocumentos = null;

        List<EquipoTraslado> documentosList = EquipoTraslado.findWithQuery(EquipoTraslado.class, "SELECT * FROM EQUIPO_TRASLADO WHERE bitacora = '" + bitacora + "' ORDER BY id ASC");
        if (documentosList.size() > 0) {
            rvDocumentos.setVisibility(View.VISIBLE);
            //frameSinDatos.setVisibility(View.GONE);
            modelDocumentos.clear();
            for (int i = 0; i <= documentosList.size() - 1; i++) {
                EquipoTraslado product = new EquipoTraslado(
                        "" + documentosList.get(i).getBitacora(),
                        "" + documentosList.get(i).getSerie(),
                        "" + documentosList.get(i).getFecha(),
                        "" + documentosList.get(i).getSync(),
                        "" + documentosList.get(i).getNombre(),
                        "",
                        "",
                        "" + documentosList.get(i).getTipo(),
                        "", ""
                );
                modelDocumentos.add(product);
            }

            adapterDocumentos = new AdapterEquiposTraslado(getApplicationContext(), modelDocumentos, NuevaBitacora.this);
            rvDocumentos.setAdapter(adapterDocumentos);
        } else {
            modelDocumentos.clear();
            adapterDocumentos = null;
            rvDocumentos.setVisibility(View.GONE);
            //frameSinDatos.setVisibility(View.VISIBLE);
        }


    }


    private void consultarEquiposDeRecoleccion(String bitacora) {
        RecyclerView rvDocumentos;
        GridLayoutManager gridLayoutManager;
        rvDocumentos = (RecyclerView) findViewById(R.id.rvEquiposEscaneadosRecoleccion);
        rvDocumentos.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rvDocumentos.setLayoutManager(gridLayoutManager);
        List<EquipoRecoleccion> modelDocumentos = new ArrayList<>();
        AdapterEquiposRecoleccion adapterDocumentos = null;

        List<EquipoRecoleccion> documentosList = EquipoRecoleccion.findWithQuery(EquipoRecoleccion.class, "SELECT * FROM EQUIPO_RECOLECCION WHERE bitacora = '" + bitacora + "' ORDER BY id ASC");
        if (documentosList.size() > 0) {
            rvDocumentos.setVisibility(View.VISIBLE);
            modelDocumentos.clear();
            for (int i = 0; i <= documentosList.size() - 1; i++) {
                EquipoRecoleccion product = new EquipoRecoleccion(
                        "" + documentosList.get(i).getBitacora(),
                        "" + documentosList.get(i).getSerie(),
                        "" + documentosList.get(i).getFecha(),
                        "" + documentosList.get(i).getSync(),
                        "" + documentosList.get(i).getNombre(),
                        "",
                        "",
                        documentosList.get(i).getTipo(),
                        "", ""
                );
                modelDocumentos.add(product);
            }
            adapterDocumentos = new AdapterEquiposRecoleccion(getApplicationContext(), modelDocumentos, NuevaBitacora.this);
            rvDocumentos.setAdapter(adapterDocumentos);
        } else {
            modelDocumentos.clear();
            adapterDocumentos = null;
            rvDocumentos.setVisibility(View.GONE);
        }
    }



    @Override
    public void onClickCancelarArticuloInstalacion(int position, String serie, String fecha, String bitacora) {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setCancelable(false);
        dialogo1.setTitle("Eliminación...");
        dialogo1.setMessage("¿ Seguro que deséas eliminar el artículo ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

                List<Equipoinstalacion> listaArticulos = Equipoinstalacion.findWithQuery(Equipoinstalacion.class, "SELECT * FROM EQUIPOINSTALACION WHERE serie = '" + serie + "' and fecha ='" + fecha + "' and bitacora='" + bitacora + "'");
                if (listaArticulos.size() > 0) {
                    Equipoinstalacion.executeQuery("DELETE FROM EQUIPOINSTALACION WHERE serie = '" + serie + "' and fecha ='" + fecha + "' and bitacora='" + bitacora + "'");
                    consultarEquiposDeInstalacion(bitacora);

                }else{
                    Log.d(TAG, "onClick: No se puede cancelar el articulo");
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

    @Override
    public void onClickCancelarArticuloRecoleccion(int position, String serie, String fecha, String bitacora) {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setCancelable(false);
        dialogo1.setTitle("Eliminación...");
        dialogo1.setMessage("¿ Seguro que deséas eliminar el artículo ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

                List<EquipoRecoleccion> listaArticulos = EquipoRecoleccion.findWithQuery(EquipoRecoleccion.class, "SELECT * FROM EQUIPO_RECOLECCION WHERE serie = '" + serie + "' and fecha ='" + fecha + "' and bitacora='" + bitacora + "'");
                if (listaArticulos.size() > 0) {
                    EquipoRecoleccion.executeQuery("DELETE FROM EQUIPO_RECOLECCION WHERE serie = '" + serie + "' and fecha ='" + fecha + "' and bitacora='" + bitacora + "'");
                    consultarEquiposDeRecoleccion(bitacora);

                }else{
                    Log.d(TAG, "onClick: No se puede cancelar el articulo");
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

    @Override
    public void onClickCancelarArticuloTraslado(int position, String serie, String fecha, String bitacora) {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setCancelable(false);
        dialogo1.setTitle("Eliminación...");
        dialogo1.setMessage("¿ Seguro que deséas eliminar el artículo ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

                List<EquipoTraslado> listaArticulos = EquipoTraslado.findWithQuery(EquipoTraslado.class, "SELECT * FROM EQUIPO_TRASLADO WHERE serie = '" + serie + "' and fecha ='" + fecha + "' and bitacora='" + bitacora + "'");
                if (listaArticulos.size() > 0) {
                    EquipoTraslado.executeQuery("DELETE FROM EQUIPO_TRASLADO WHERE serie = '" + serie + "' and fecha ='" + fecha + "' and bitacora='" + bitacora + "'");
                    consultarEquiposDeTraslado(bitacora);

                }else{
                    Log.d(TAG, "onClick: No se puede cancelar el articulo");
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





    private void eliminarArticulosDeInstalacionNoGuardados(String bitacora){
        //Equipoinstalacion.executeQuery("DELETE FROM EQUIPOINSTALACION WHERE bitacora='" + bitacora + "' and sync ='4'");
        Equipoinstalacion.executeQuery("DELETE FROM EQUIPOINSTALACION WHERE bitacora='" + bitacora + "'");
    }

    private void eliminarArticulosDeCortejoNoGuardados(String bitacora){
        Equipocortejo.executeQuery("DELETE FROM EQUIPOCORTEJO WHERE bitacora='" + bitacora + "'");
    }

    private void eliminarArticulosDeRecoleccionNoGuardados(String bitacora){
        EquipoRecoleccion.executeQuery("DELETE FROM EQUIPO_RECOLECCION WHERE bitacora='" + bitacora + "'");
    }

    private void eliminarArticulosDeTrasladoNoGuardados(String bitacora){
        EquipoTraslado.executeQuery("DELETE FROM EQUIPO_TRASLADO WHERE bitacora='" + bitacora + "'");
    }






    private void updateArticulosDeInstalacionConSync4(String bitacora){
        Equipoinstalacion.executeQuery("UPDATE EQUIPOINSTALACION set sync ='0' WHERE bitacora='" + bitacora + "' and sync ='4'");
    }

    private void updateArticulosDeCortejoConSync4(String bitacora){
        Equipocortejo.executeQuery("UPDATE EQUIPOCORTEJO set sync ='0' WHERE bitacora='" + bitacora + "' and sync ='4'");
    }

    private void updateArticulosDeRecoleccionConSync4(String bitacora){
        EquipoRecoleccion.executeQuery("UPDATE EQUIPO_RECOLECCION set sync ='0' WHERE bitacora='" + bitacora + "' and sync ='4'");
    }

    private void updateArticulosDeTrasladoConSync4(String bitacora){
        EquipoTraslado.executeQuery("UPDATE EQUIPO_TRASLADO set sync ='0' WHERE bitacora='" + bitacora + "' and sync ='4'");
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

}



class LinearLayoutThatDetectsSoftKeyboard extends LinearLayout {

    public LinearLayoutThatDetectsSoftKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public interface Listener {
        public void onSoftKeyboardShown(boolean isShowing);
    }
    private Listener listener;
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Activity activity = (Activity)getContext();
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        int screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
        int diff = (screenHeight - statusBarHeight) - height;
        if (listener != null) {
            listener.onSoftKeyboardShown(diff>128);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}



