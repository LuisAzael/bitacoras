package com.example.bitacoras2020;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bitacoras2020.Activities.BitacoraDetalle;
import com.example.bitacoras2020.Activities.Login;
import com.example.bitacoras2020.Activities.NotificacionesActivity;
import com.example.bitacoras2020.Activities.NuevaBitacora;
import com.example.bitacoras2020.Activities.RequestPermissionManager;
import com.example.bitacoras2020.Activities.Scanner;
import com.example.bitacoras2020.Activities.ScannerArticulos;
import com.example.bitacoras2020.Activities.SplashScreen;
import com.example.bitacoras2020.Adapters.AdapterBitacorasActivas;
import com.example.bitacoras2020.Callbacks.RegistrarSalidaCallback;
import com.example.bitacoras2020.Callbacks.RequerirEventoDeSalidaCallback;
import com.example.bitacoras2020.Callbacks.ShowDetailsBitacoraCallback;
import com.example.bitacoras2020.Callbacks.TerminarBitacoraCallback;
import com.example.bitacoras2020.Database.Activos;
import com.example.bitacoras2020.Database.Adicional;
import com.example.bitacoras2020.Database.Bitacoras;
import com.example.bitacoras2020.Database.Carros;
import com.example.bitacoras2020.Database.CatalogoArticulos;
import com.example.bitacoras2020.Database.Choferes;
import com.example.bitacoras2020.Database.Codigos;
import com.example.bitacoras2020.Database.Comentarios;
import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.Database.Documentos;
import com.example.bitacoras2020.Database.EquipoRecoleccion;
import com.example.bitacoras2020.Database.EquipoTraslado;
import com.example.bitacoras2020.Database.Equipocortejo;
import com.example.bitacoras2020.Database.Equipoinstalacion;
import com.example.bitacoras2020.Database.Eventos;
import com.example.bitacoras2020.Database.Inventario;
import com.example.bitacoras2020.Database.Laboratorios;
import com.example.bitacoras2020.Database.LoginZone;
import com.example.bitacoras2020.Database.Lugares;
import com.example.bitacoras2020.Models.ModelBitacorasActivas;
import com.example.bitacoras2020.Utils.ApplicationResourcesProvider;
import com.example.bitacoras2020.Utils.BaseActivity;
import com.example.bitacoras2020.Utils.Constants;
import com.example.bitacoras2020.Utils.ConstantsBitacoras;
import com.example.bitacoras2020.Utils.ForegroundService;
import com.example.bitacoras2020.Utils.GeoFenceHelper;
import com.example.bitacoras2020.Utils.Preferences;
import com.example.bitacoras2020.Utils.VolleySingleton;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.karan.churi.PermissionManager.PermissionManager;
import com.orm.SugarRecord;
import com.polyak.iconswitch.IconSwitch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.AutoTransition;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import android.os.CancellationSignal;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphFloatingActionButton;
import soup.neumorphism.NeumorphImageButton;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, TerminarBitacoraCallback, ShowDetailsBitacoraCallback, RequerirEventoDeSalidaCallback,
        RegistrarSalidaCallback {

    private PermissionManager permissionManager;
    private static String TAG = "MainActivity";
    NestedScrollView layoutGeneral;
    //FrameLayout layoutCargando;
    SimpleDateFormat dateFormat;
    Dialog dialogoSalidas;
    BottomSheetDialog dialogoFingerPrint;
    public TextView tvnumeroBitacora, tvChofer, tvAyudante, tvVehiculo, tvSecondName, tvAddress, tvPhone, textoSinBitacora, tvContador;
    CardView cardBitacoraActiva, expandable_data;
    ImageView btExpandir;
    NeumorphImageButton btDescargarTodo;
    IconSwitch btCerrarAsistencia;
    CoordinatorLayout rootLayout;
    NeumorphFloatingActionButton fabb;
    boolean errorStackTraceBitacoras = false;
    boolean errorStackTraceChoferes = false;
    boolean errorStackTraceCarrosas = false;
    boolean errorStackTracePlaces = false;
    String codigoErrorStackTraceBitacoras = "", bitacoraDesdeNotifiacion = "";
    String codigoErrorStackTraceChoferes = "";
    String codigoErrorStackTraceCarrosas = "";
    String codigoErrorStackTracePlaces = "", fechaEstatica="";
    TextView etBitacoraLayout;
    BottomSheetDialog mBottomSheetDialog;

    //** MAPAS **//
    ArrayList<Geofence> geofenceList = new ArrayList<Geofence>();
    private static final int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private GoogleMap mMap;

    GeofencingClient geofencingClient;
    GeoFenceHelper geoFenceHelper;
    Handler handler = new Handler();

    /**** fingerprint
     */
    TextView mHeadingLabel;
    ImageView mFingerprintImage;
    TextView mParaLabel;
    FingerprintManager fingerprintManager;
    KeyguardManager keyguardManager;
    KeyStore keyStore;
    Cipher cipher;
    String KEY_NAME = "AndroidKey";

    //---Actualizacion
    private boolean updatePostponed = false;
    private Timer timerUpdate;
    private Boolean downloadingApp = false;
    ProgressDialog pDialog;
    PendingIntent pendingIntent = null;
    AdapterBitacorasActivas adapterBitacorasActivas = null;
    FrameLayout frameLayout;
    boolean goToBitacoraDetalle = false, goToNuevaBitacora = false;
    LottieAnimationView lottie_animation;
    Button btAceptar;
    MediaPlayer mediaPlayer;
    String bitacora = "", isProveedor= "0";
    TextView tvCheckVersionApp, tvNada;
    int status = 0;
    boolean requesterCanceled = false;
    String descripcionPeticion ="";
    NeumorphCardView cardBtacoras;

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_main);
        btCerrarAsistencia = (IconSwitch) findViewById(R.id.btCerrarAsistencia);
        if (Preferences.getPreferenceCheckinCheckoutAssistant(MainActivity.this, Preferences.PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT))
            btCerrarAsistencia.setChecked(IconSwitch.Checked.RIGHT);

        tvNada =(TextView) findViewById(R.id.tvNada);
        cardBtacoras =(NeumorphCardView) findViewById(R.id.cardBtacoras);


        TextView tvTextoNombre =(TextView) findViewById(R.id.tvTextoNombre);
        tvTextoNombre.setTypeface(ApplicationResourcesProvider.bold);

        TextView tvUltimo =(TextView) findViewById(R.id.tvUltimo);
        tvUltimo.setTypeface(ApplicationResourcesProvider.bold);

        TextView tvBitacorasActivas =(TextView) findViewById(R.id.tvBitacorasActivas);
        tvBitacorasActivas.setTypeface(ApplicationResourcesProvider.bold);

        TextView tvNotifi =(TextView) findViewById(R.id.tvNotifi);
        tvNotifi.setTypeface(ApplicationResourcesProvider.regular);

        TextView tvSincro =(TextView) findViewById(R.id.tvSincro);
        tvSincro.setTypeface(ApplicationResourcesProvider.regular);

        TextView tvCheckVersionApp =(TextView) findViewById(R.id.tvCheckVersionApp);
        tvCheckVersionApp.setTypeface(ApplicationResourcesProvider.light);

        TextView tvDomicilio =(TextView) findViewById(R.id.tvDomicilio);
        tvDomicilio.setTypeface(ApplicationResourcesProvider.regular);

        TextView textoSinBitacora =(TextView) findViewById(R.id.textoSinBitacora);
        textoSinBitacora.setTypeface(ApplicationResourcesProvider.regular);


        NeumorphButton tvRegistroDeAsistencia =(NeumorphButton) findViewById(R.id.tvRegistroDeAsistencia);
        tvRegistroDeAsistencia.setTypeface(ApplicationResourcesProvider.regular);


        tvRegistroDeAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean actualmenteEsEntrada=false;

                if(Preferences.getPreferencesAsistenciaEntrada(MainActivity.this, Preferences.PREFERENCE_ASISTENCIA_ENTRADA)) {
                    actualmenteEsEntrada = true;
                }else {
                    actualmenteEsEntrada = false;
                }

                showBottomLayoutForHorariosDeAsistencia(v, actualmenteEsEntrada);
            }
        });



        if (!Preferences.getPreferenceTutorialHome(MainActivity.this, Preferences.PREFERENCE_TUTORIAL_HOME)) {
            startsTutorial();
        }


        TextView tvVerTutorial = (TextView) findViewById(R.id.tvVerTutorial);
        tvVerTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarDeNuevoElTutorial();
                layoutGeneral.smoothScrollTo(0, 0);
            }
        });



        //final Display display = getWindowManager().getDefaultDisplay();
        /*final Rect droidTarget = new Rect(0, 0, 40, 40);
        droidTarget.offset(20, 60);

        final TapTargetSequence sequence = new TapTargetSequence(this)
                .targets(
                        TapTarget.forBounds(droidTarget, "Oh look!", "You can point to any part of the screen. You also can't cancel this one!")
                                .cancelable(false)
                                .icon(droid)
                                .id(4)
                )
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        //((TextView) findViewById(R.id.educated)).setText("Congratulations! You're educated now!");
                        Toast.makeText(MainActivity.this, "Tutorial finalizado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        Log.d("TapTargetView", "Clicked on " + lastTarget.id());
                        Toast.makeText(MainActivity.this, "Cicked on: " + lastTarget.id(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        final androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                                .setTitle("Uh oh")
                                .setMessage("You canceled the sequence")
                                .setPositiveButton("Oops", null).show();
                        TapTargetView.showFor(dialog,
                                TapTarget.forView(dialog.getButton(DialogInterface.BUTTON_POSITIVE), "Uh oh!", "You canceled the sequence at step " + lastTarget.id())
                                        .cancelable(false)
                                        .tintTarget(false), new TapTargetView.Listener() {
                                    @Override
                                    public void onTargetClick(TapTargetView view) {
                                        super.onTargetClick(view);
                                        dialog.dismiss();
                                    }
                                });
                    }
                });

        final SpannableString spannedDesc = new SpannableString("Aquí podrás actualizar tus bitácoras");
        //spannedDesc.setSpan(new UnderlineSpan(), spannedDesc.length() - "TapTargetView".length(), spannedDesc.length(), 0);
        TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.btDescargarTodo), "Bienvenido a eBita!", spannedDesc)
                .cancelable(false)
                .drawShadow(true)
                .titleTextDimen(R.dimen.title_text_size)
                .tintTarget(false), new TapTargetView.Listener() {
            @Override
            public void onTargetClick(TapTargetView view) {
                super.onTargetClick(view);
                sequence.start();
            }

            @Override
            public void onOuterCircleClick(TapTargetView view) {
                super.onOuterCircleClick(view);
                Toast.makeText(view.getContext(), "You clicked the outer circle!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                Log.d("TapTargetViewSample", "You dismissed me :(");
            }
        });*/





        android.transition.Fade fade = new android.transition.Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        //pendingIntent = geoFenceHelper.getPendingIntent();
        permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fechaEstatica = dateFormat.format(new Date());

        //dialogoError = new Dialog(this);
        dialogoSalidas = new Dialog(MainActivity.this);
        dialogoFingerPrint = new BottomSheetDialog(MainActivity.this);
        layoutGeneral = (NestedScrollView) findViewById(R.id.layoutGeneral);
        etBitacoraLayout = (TextView) findViewById(R.id.etBitacoraLayout);
        lottie_animation = (LottieAnimationView) findViewById(R.id.lottie_animation);
        btAceptar = (Button) findViewById(R.id.btAceptar);
        tvnumeroBitacora = (TextView) findViewById(R.id.tvNumeroBitacora);
        tvChofer = (TextView) findViewById(R.id.tvChofer);
        tvContador = (TextView) findViewById(R.id.tvContador);
        tvAyudante = (TextView) findViewById(R.id.tvAyudante);
        tvVehiculo = (TextView) findViewById(R.id.tvVehiculo);
        tvSecondName = (TextView) findViewById(R.id.tvSecondName);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvPhone = (TextView) findViewById(R.id.tvPhones);
        textoSinBitacora = (TextView) findViewById(R.id.textoSinBitacora);
        cardBitacoraActiva = (CardView) findViewById(R.id.cardBitacoraActiva);
        expandable_data = (CardView) findViewById(R.id.expandable_data);
        btExpandir = (ImageView) findViewById(R.id.btExpandir);
        btDescargarTodo = (NeumorphImageButton) findViewById(R.id.btDescargarTodo);

        tvCheckVersionApp = (TextView) findViewById(R.id.tvCheckVersionApp);
        fabb = (NeumorphFloatingActionButton) findViewById(R.id.fab);
        startService();
        textoSinBitacora.setText(DatabaseAssistant.getLastestTimeSesion());


        tvCheckVersionApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestForCheckUpdateApp();
            }
        });

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                if (extras.containsKey("isProveedor"))
                    isProveedor = extras.getString("isProveedor");
                else
                    isProveedor = "0";
            }catch (Throwable e){
                Log.e(TAG, "onCreate: isProveedor: " + e.getMessage() );
                Log.e(TAG, "onCreate: isProveedor: " + e.getLocalizedMessage() );
            }
        }


        try {
            if (Preferences.getIsProveedor(MainActivity.this, Preferences.PREFERENCE_IS_PROVEEDOR)) {
                isProveedor = "1";
                FirebaseMessaging.getInstance().subscribeToTopic("Proveedor")
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
            } else {
                isProveedor = "0";
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
            }
        }catch (Throwable e){
            Log.e(TAG, "onCreate: " + e.getMessage());
        }







        //fingerprintCheck();

        /*SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int lastTimeStarted = settings.getInt("last_time_started", -1);
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);

        if (today != lastTimeStarted) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("last_time_started", today);
            editor.commit();

        }
        else{
            Log.d(TAG, "onCreate: Mismo dia, no se actualiza información");
        }*/



        if (DatabaseAssistant.cerrarSesionEnUnLugarEspecifico()) {
            String[] coordinates = DatabaseAssistant.getCoordinatesToDoGeofenceAndCloseSesion();
            LatLng coordenadasToDoGeofenceCheckOut = new LatLng(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
            addGeofenceToCloseLogin(coordenadasToDoGeofenceCheckOut);
        }


        CardView cardScannerArticulos =(CardView) findViewById(R.id.cardScannerArticulos);
        cardScannerArticulos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScannerArticulos.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        Button btCancelarPeticion =(Button) findViewById(R.id.btCancelarPeticion);
        btCancelarPeticion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = -1;
                requesterCanceled = true;
                hideLoadingDialog();
            }
        });

        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                } catch (Throwable e) {
                    Log.e(TAG, "onClick: " + e.getMessage());
                }

                Preferences.setBitacoraNotificationPush(getApplicationContext(), "", Preferences.PREFERENCE_BITACORA_NOTIFICATION_PUSH);
                frameLayout.setVisibility(View.GONE);
                layoutGeneral.setAlpha(1.0f);
                fabb.setVisibility(View.VISIBLE);
                etBitacoraLayout.setText("-------");

                if(!bitacoraDesdeNotifiacion.equals("")) {
                    Intent intent = new Intent(getApplicationContext(), NuevaBitacora.class);
                    intent.putExtra("bitacora_from_push_notification", bitacoraDesdeNotifiacion);
                    startActivity(intent);
                }


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


        @SuppressLint("CutPasteId") NeumorphFloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNuevaBitacora = true;
                Intent intent = new Intent(getApplicationContext(), NuevaBitacora.class);
                intent.putExtra("tipo", "1");
                startActivity(intent);
            }
        });


        cardBitacoraActiva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandable_data.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(cardBitacoraActiva, new AutoTransition());
                    expandable_data.setVisibility(View.VISIBLE);
                    cardBitacoraActiva.setCardElevation(20.0f);
                    btExpandir.setBackgroundResource(R.drawable.ic_arrow_up);
                } else {
                    TransitionManager.beginDelayedTransition(cardBitacoraActiva, new AutoTransition());
                    expandable_data.setVisibility(View.GONE);
                    cardBitacoraActiva.setCardElevation(8.0f);
                    btExpandir.setBackgroundResource(R.drawable.ic_arrow_down);

                }
            }
        });

        btExpandir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (expandable_data.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(cardBitacoraActiva, new AutoTransition());
                    expandable_data.setVisibility(View.VISIBLE);
                    cardBitacoraActiva.setCardElevation(20.0f);
                    btExpandir.setBackgroundResource(R.drawable.ic_arrow_up);
                } else {
                    TransitionManager.beginDelayedTransition(cardBitacoraActiva, new AutoTransition());
                    expandable_data.setVisibility(View.GONE);
                    cardBitacoraActiva.setCardElevation(8.0f);
                    btExpandir.setBackgroundResource(R.drawable.ic_arrow_down);

                }
            }
        });


        NeumorphCardView cardEnviar = findViewById(R.id.cardEnviar);
        cardEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ApplicationResourcesProvider.checkInternetConnection()) {
                    try {
                        sincronizarDatos();
                    }catch (Throwable e){
                        Log.e(TAG, "onClick: Error en sincronizar infirmacion desd MainActivity" + e.getMessage());
                        Toast.makeText(MainActivity.this, "Ocurio un error con la sincronización, vuelve a intentarlo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                } else
                    showErrorDialog("No hay conexión a internet", "");
            }
        });

        NeumorphCardView cardScanner = findViewById(R.id.cardScanner);
        cardScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NotificacionesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        timerUpdateLocationVehicle();
        timerUpdateNotifications();
        Preferences.setActividadYaIniciada(getApplicationContext(), true, Preferences.PREFERENCE_ACTIVIDAD_INICIADA);
    }

    private void startsTutorial() {
        final Drawable droid = ContextCompat.getDrawable(   this, R.drawable.ic_sync);
        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.btDescargarTodo), "Hola bienvenido!", "Aquí podrás actualizar tus bitácoras.")
                        .outerCircleColor(R.color.purple_200)      // Specify a color for the outer circle
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
                        checkInAndCheckOutTutorial();
                    }
                });
    }


    private void checkInAndCheckOutTutorial() {
        final Drawable droid = ContextCompat.getDrawable(this, R.drawable.ic_power_tutorial);
        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.btCerrarAsistencia), "Cerrar sesión", "Aquí podrás apagar la aplicación y hacer tu checkout de salida.")
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
                        asistenciaTutorial();
                    }
                });
    }




    private void asistenciaTutorial() {
        //final Drawable droid = ContextCompat.getDrawable(this, R.drawable.ic_asistencia);
        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.tvRegistroDeAsistencia), "Nuevo registro de asistencia", "Aquí podras checar tu asistencia y salida por horarios.")
                        .outerCircleColor(R.color.textos_principal)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(16)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.white)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                        //.icon(droid)                     // Specify a custom drawable to draw as the target
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        final Display display = getWindowManager().getDefaultDisplay();
                        try {
                            //display.getWidth() / 2, display.getHeight() / 2
                            layoutGeneral.smoothScrollTo(0, display.getHeight() + 1000 );
                        }catch (Throwable e){
                            Log.e(TAG, "onTargetClick: " + e.getMessage() );
                            layoutGeneral.smoothScrollTo(0, display.getHeight() );
                        }
                        nuevaBitacoraTutorial();
                    }
                });
    }

    private void nuevaBitacoraTutorial() {
        final Drawable droid = ContextCompat.getDrawable(this, R.drawable.ic_add_tutorial);
        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.fab), "Nueva bitácora", "Aquí podrás agregar una nueva bitácora.")
                        .outerCircleColor(R.color.teal_200)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.black)      // Specify the color of the title text
                        .descriptionTextSize(16)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.textos_principal)  // Specify the color of the description text
                        .textColor(R.color.textos_principal)            // Specify a color for both the title and description text
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
                        final Display display = getWindowManager().getDefaultDisplay();
                        try {
                            //display.getWidth() / 2, display.getHeight() / 2
                            layoutGeneral.smoothScrollTo(0, display.getHeight() + 1000 );
                        }catch (Throwable e){
                            Log.e(TAG, "onTargetClick: " + e.getMessage() );
                            layoutGeneral.smoothScrollTo(0, display.getHeight() );
                        }
                        sincronizarTutorial();
                    }
                });
    }

    private void sincronizarTutorial() {
        final Drawable droid = ContextCompat.getDrawable(this, R.drawable.ic_sync_tutorial);
        TapTargetView.showFor(this,
                TapTarget.forView(findViewById(R.id.cardEnviar), "Sincronización", "Presiona aquí cada que realices una bitácora, salida o llegada.")
                        .outerCircleColor(R.color.purple_500)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white_card_color)   // Specify a color for the target circle
                        .titleTextSize(25)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(16)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.white_card_color)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        .icon(droid)                     // Specify a custom drawable to draw as the target
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        Preferences.setPreferenceTutorialHome(MainActivity.this, true, Preferences.PREFERENCE_TUTORIAL_HOME);
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        Preferences.setPreferenceTutorialHome(MainActivity.this, true, Preferences.PREFERENCE_TUTORIAL_HOME);
                    }
                });
    }

    private void consultarBitacorasActivas() {
        RecyclerView rvBitacorasActivas;
        GridLayoutManager gridLayoutManager;
        rvBitacorasActivas = (RecyclerView) findViewById(R.id.rvBitacorasActivas);
        rvBitacorasActivas.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rvBitacorasActivas.setLayoutManager(gridLayoutManager);
        List<ModelBitacorasActivas> modelBitacorasActivas = new ArrayList<>();


        List<Eventos> categorias = Eventos.findWithQuery(Eventos.class, "SELECT * FROM ACTIVOS where activo ='1'");
        if (categorias.size() > 0) {
            rvBitacorasActivas.setVisibility(View.VISIBLE);
            TextView tvBitacorasActivas = (TextView) findViewById(R.id.tvBitacorasActivas);
            tvBitacorasActivas.setText("Bitácoras activas");
            tvBitacorasActivas.setTextColor(Color.parseColor("#1b1b1b"));
            tvNada.setVisibility(View.GONE);
            cardBtacoras.setVisibility(View.VISIBLE);

            for (int i = 0; i <= categorias.size() - 1; i++) {
                ModelBitacorasActivas product = new ModelBitacorasActivas(
                        "" + categorias.get(i).getBitacora(),
                        "" + DatabaseAssistant.getOrigenBitacora(categorias.get(i).getBitacora()),
                        "" + DatabaseAssistant.getDestinoBitacora(categorias.get(i).getBitacora()),
                        "" + DatabaseAssistant.getLatitudDestino(categorias.get(i).getBitacora()),
                        "" + DatabaseAssistant.getLongitudDestino(categorias.get(i).getBitacora()),
                        "" + DatabaseAssistant.getDomicilioDestinoFromBitacora(categorias.get(i).getBitacora()),
                        "" + categorias.get(i).getChofer(),
                        "" + categorias.get(i).getAyudante(),
                        "" + categorias.get(i).getCarro(),
                        "" + categorias.get(i).getDomicilio(),
                        "" + categorias.get(i).getTelefonos(),
                        "" + DatabaseAssistant.getCodigoDeBarrasBitacora(categorias.get(i).getBitacora()),
                        "" + categorias.get(i).getTipo(),
                        "" + DatabaseAssistant.getTimeLastEvent(categorias.get(i).getBitacora())
                );
                modelBitacorasActivas.add(product);
            }

            adapterBitacorasActivas = new AdapterBitacorasActivas(
                    MainActivity.this,
                    modelBitacorasActivas,
                    MainActivity.this,
                    MainActivity.this,
                    MainActivity.this,
                    MainActivity.this
            );
            rvBitacorasActivas.setAdapter(adapterBitacorasActivas);
        } else {
            Log.d(TAG, "consultarBitacorasActivas: No tenemos bitacoras acivas por el momento");
            modelBitacorasActivas.clear();
            adapterBitacorasActivas = null;
            rvBitacorasActivas.setVisibility(View.GONE);
            TextView tvBitacorasActivas = (TextView) findViewById(R.id.tvBitacorasActivas);
            tvBitacorasActivas.setText("No tienes bitácoras activas");
            tvBitacorasActivas.setTextColor(Color.parseColor("#9a0007"));
            tvNada.setVisibility(View.VISIBLE);
            cardBtacoras.setVisibility(View.GONE);
        }


    }


    public void timerUpdateLocationVehicle() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                timerUpdateLocationVehicle();
                try {
                    if (mMap != null) {
                        String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();
                        LatLng myLocationActual = new LatLng(Double.parseDouble(arregloCoordenadas[0]), Double.parseDouble(arregloCoordenadas[1]));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocationActual, 16));
                        Log.d("MAPA_UPDATE", myLocationActual.toString());
                    }
                } catch (Throwable e) {
                    Log.d("MAPA_UPDATE", e.getMessage());
                }
            }
        }, 5000);
    }


    public void timerUpdateNotifications() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                timerUpdateNotifications();
                String bitacoraFromPushNotifiation = Preferences.getBitacoraNotificationPush(MainActivity.this, Preferences.PREFERENCE_BITACORA_NOTIFICATION_PUSH);
                if (!bitacoraFromPushNotifiation.equals("")) {
                    if (frameLayout.getVisibility() != View.VISIBLE) {
                        etBitacoraLayout.setText(bitacoraFromPushNotifiation);
                        bitacoraDesdeNotifiacion = bitacoraFromPushNotifiation;
                        frameLayout.setVisibility(View.VISIBLE);
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notification_3);
                        mediaPlayer.start();
                        mediaPlayer.setLooping(true);

                        lottie_animation.setAnimation("car.json");
                        lottie_animation.loop(true);
                        lottie_animation.playAnimation();

                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_left);
                        frameLayout.startAnimation(animation);

                        layoutGeneral.setAlpha(0.1f);
                        fabb.setVisibility(View.GONE);
                    }
                } else
                    frameLayout.setVisibility(View.GONE);
            }
        }, 2000);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {

            String jsonLugares = new Gson().toJson(Lugares.listAll(Lugares.class));
            try {
                JSONArray json = new JSONArray(jsonLugares);
                for (int i = 0; i <= json.length() - 1; i++) {

                    if (Double.parseDouble(json.getJSONObject(i).getString("latitud")) != 0 || Double.parseDouble(json.getJSONObject(i).getString("longitud")) != 0) {
                        LatLng place = new LatLng(Double.parseDouble(json.getJSONObject(i).getString("latitud")), Double.parseDouble(json.getJSONObject(i).getString("longitud")));
                        enableUserLocation();


                        if (Build.VERSION.SDK_INT >= 29) {
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                enableUserLocation();
                                playProcessLocationAndMarkers(place, json.getJSONObject(i).getString("nombre"),
                                        Integer.parseInt(json.getJSONObject(i).getString("perimetro")));


                            } else {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.
                                            ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                                    enableUserLocation();
                                    playProcessLocationAndMarkers(place, json.getJSONObject(i).getString("nombre"),
                                            Integer.parseInt(json.getJSONObject(i).getString("perimetro")));
                                } else {
                                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.
                                            ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                                }
                            }
                        } else {
                            enableUserLocation();
                            playProcessLocationAndMarkers(place, json.getJSONObject(i).getString("nombre"),
                                    Integer.parseInt(json.getJSONObject(i).getString("perimetro")));

                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Throwable e) {
            Log.e(TAG, "onMapReady: " + e.getMessage());
        }


    }


    private void cargarDatos() {
        if (ApplicationResourcesProvider.checkInternetConnection()) {
        } else
            showErrorDialog("No hay conexión a internet", "");
    }



    private void lanzarDeNuevoElTutorial() {
        showMyCustomDialog();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Preferences.setPreferenceTutorialHome(MainActivity.this, false, Preferences.PREFERENCE_TUTORIAL_HOME);
                            Preferences.setPreferenceTutorialDetails(MainActivity.this, false, Preferences.PREFERENCE_TUTORIAL_DETALLES);
                            startsTutorial();
                        }
                    });

                    synchronized (this) {
                        wait(3000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissMyCustomDialog();
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Ocurrio un error, intenta de nuevo", Toast.LENGTH_SHORT).show();
                }
            }

            ;
        };
        thread.start();
    }

    private void downloadBitacoras()
    {
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d("FIREBASE", "Refresh Token: " + token);
            if (token != null)
                DatabaseAssistant.insertarToken(token);
            else
                DatabaseAssistant.insertarToken("Unknown");
        }catch (Throwable e){
            Log.e(TAG, "downloadBitacoras: " + e.getMessage());
        }

        JSONObject params = new JSONObject();
        try {
            params.put("usuario", DatabaseAssistant.getUserNameFromSesiones() );
            params.put("token_device", DatabaseAssistant.getTokenDeUsuario());
            params.put("isProveedor", DatabaseAssistant.getIsProveedor());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_DOWNLOAD_BITACORAS_COMPLETAS, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
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

                    //verificar las bitacoras activas y actualizar el status a 1 para no usarlas de nuevo
                    List<Activos> activosList = Activos.findWithQuery(Activos.class, "SELECT * FROM ACTIVOS WHERE activo = '1'");
                    if(activosList.size()>0) {
                        for(int i = 0; i <= activosList.size()-1; i++){
                            DatabaseAssistant.updateBitacoraParaNoVolverAUtilizar(activosList.get(i).getBitacora());
                            Log.w(TAG, "Bitacora actualizada a sync 1: " + activosList.get(i).getBitacora());
                        }
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

        // postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 1, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }


    /*private void downloadCarrosas() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsBitacoras.WS_VEHICLES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Carros.deleteAll(Carros.class);
                JSONArray jsonArrayCarrosas = new JSONArray();
                try {
                    JSONObject json = new JSONObject(response);
                    jsonArrayCarrosas = json.getJSONArray("vehicles");

                    errorStackTraceCarrosas = false;
                    for (int i = 0; i <= jsonArrayCarrosas.length() - 1; i++) {
                        DatabaseAssistant.insertarCarrosas(
                                "" + jsonArrayCarrosas.getJSONObject(i).getString("name"),
                                "" + jsonArrayCarrosas.getJSONObject(i).getString("status"),
                                "" + jsonArrayCarrosas.getJSONObject(i).getString("id")
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorStackTraceCarrosas = true;
                    codigoErrorStackTraceCarrosas = e.getMessage();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                errorStackTraceCarrosas = true;
                codigoErrorStackTraceCarrosas = error.getMessage();
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }*/

    private void downloadCarrosas()
    {
        JSONObject params = new JSONObject();
        try {
            params.put("usuario", DatabaseAssistant.getUserNameFromSesiones() );
            params.put("token_device", DatabaseAssistant.getTokenDeUsuario());
            params.put("isProveedor", DatabaseAssistant.getIsProveedor());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_VEHICLES_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                Carros.deleteAll(Carros.class);
                JSONArray jsonArrayCarrosas = new JSONArray();
                try {
                    jsonArrayCarrosas = response.getJSONArray("vehicles");

                    errorStackTraceCarrosas = false;
                    for (int i = 0; i <= jsonArrayCarrosas.length() - 1; i++) {
                        DatabaseAssistant.insertarCarrosas(
                                "" + jsonArrayCarrosas.getJSONObject(i).getString("name"),
                                "" + jsonArrayCarrosas.getJSONObject(i).getString("status"),
                                "" + jsonArrayCarrosas.getJSONObject(i).getString("id")
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorStackTraceCarrosas = true;
                    codigoErrorStackTraceCarrosas = e.getMessage();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        errorStackTraceCarrosas = true;
                        codigoErrorStackTraceCarrosas = error.getMessage();
                    }
                }) {
        };
        //postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 1, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }

    public void getComentariosFromWeb()
    {
        JSONObject jsonParams = null;
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_GET_COMENTARIOS, jsonParams,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            if(!response.getBoolean("error"))
                            {
                                JSONArray arrayResult = new JSONArray();
                                arrayResult = response.getJSONArray("result");
                                if(arrayResult.length()>0){
                                    Comentarios.deleteAll(Comentarios.class);
                                }
                                for(int i =0; i<=arrayResult.length()-1;i++){
                                    //Guarda aqui los comentarios
                                    DatabaseAssistant.insertarComentarios(
                                            "" + arrayResult.getJSONObject(i).getString("bitacora"),
                                            "" + arrayResult.getJSONObject(i).getString("comentario"),
                                            "" + arrayResult.getJSONObject(i).getString("usuario"),
                                            "" + arrayResult.getJSONObject(i).getString("fecha_captura"),
                                            "1",
                                            DatabaseAssistant.getUserNameFromSesiones().equals(arrayResult.getJSONObject(i).getString("usuario")) ? "1" :"0"
                                    );
                                }
                                Log.d(TAG, "onResponse: Comentarios actualizados");
                            }

                        } catch (JSONException e) {
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
        //postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 1, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }

    private void downloadBitacorasDetails()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantsBitacoras.WS_BITACORA_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONArray jsonArrayResult = new JSONArray();
                try {
                    JSONObject json = new JSONObject(response);
                    jsonArrayResult = json.getJSONArray("result");

                    for (int i = 0; i <= jsonArrayResult.length() - 1; i++) {

                        String bitacoraFromResponse = jsonArrayResult.getJSONObject(i).getString("bitacora");
                        Adicional.executeQuery("DELETE FROM ADICIONAL WHERE bitacora = '" + bitacoraFromResponse + "' and sync ='1'");

                        String seleccionarHora="00:00:00";
                        if (jsonArrayResult.getJSONObject(i).getString("hora_instalacion").equals("Selecciona aqui..."))
                            seleccionarHora = "00:00:00";
                        else
                            seleccionarHora = jsonArrayResult.getJSONObject(i).getString("hora_instalacion");

                        DatabaseAssistant.insertarInformacionAdicional(
                                "" + seleccionarHora,
                                "" + jsonArrayResult.getJSONObject(i).getString("ropa_entregada"),
                                "" + jsonArrayResult.getJSONObject(i).getString("lugar_de_velacion"),
                                "" + jsonArrayResult.getJSONObject(i).getString("tipo_servicio"),
                                "",
                                "" + jsonArrayResult.getJSONObject(i).getString("observaciones_instalacion"),
                                "",
                                "" + jsonArrayResult.getJSONObject(i).getString("observaciones_cortejo"),
                                "" + jsonArrayResult.getJSONObject(i).getString("documentos_seleccion"),
                                "" + bitacoraFromResponse,
                                "" + jsonArrayResult.getJSONObject(i).getString("encapsulado"),
                                "" + jsonArrayResult.getJSONObject(i).getString("observaciones_recoleccion"),
                                "" + jsonArrayResult.getJSONObject(i).getString("observaciones_traslado"),
                                "" + jsonArrayResult.getJSONObject(i).getString("embalsamado_o_arreglo"),
                                "" + jsonArrayResult.getJSONObject(i).getString("laboratorio"),
                                "" + jsonArrayResult.getJSONObject(i).getString("idLaboratorio")
                        );


                        if(jsonArrayResult.getJSONObject(i).has("Equipos_instalacion")) {
                            Equipoinstalacion.executeQuery("DELETE FROM EQUIPOINSTALACION WHERE bitacora = '" + bitacoraFromResponse + "' and sync ='1'");
                            JSONArray jsonArrayEquiposInstalacion = jsonArrayResult.getJSONObject(i).getJSONArray("Equipos_instalacion");
                            for (int position = 0; position <= jsonArrayEquiposInstalacion.length() - 1; position++) {


                                DatabaseAssistant.insertarEquipoInstalacionFromWebService(
                                        "" + bitacoraFromResponse,
                                        "" + jsonArrayEquiposInstalacion.getJSONObject(position).getString("serie"),
                                        "" + jsonArrayEquiposInstalacion.getJSONObject(position).getString("nombre"),
                                        "" + (Preferences.getPreferenceIsbunker(MainActivity.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0")
                                );
                            }
                        }

                        if(jsonArrayResult.getJSONObject(i).has("Equipos_cortejo")) {
                            Equipocortejo.executeQuery("DELETE FROM EQUIPOCORTEJO WHERE bitacora = '" + bitacoraFromResponse + "' and sync ='1'");
                            JSONArray jsonArrayEquiposCortejo = jsonArrayResult.getJSONObject(i).getJSONArray("Equipos_cortejo");
                            for (int position = 0; position <= jsonArrayEquiposCortejo.length() - 1; position++) {

                                DatabaseAssistant.insertarEquipoCortejoFromWebservice(
                                        "" + bitacoraFromResponse,
                                        "" + jsonArrayEquiposCortejo.getJSONObject(position).getString("serie"),
                                        "" + jsonArrayEquiposCortejo.getJSONObject(position).getString("nombre"),
                                        "" + (Preferences.getPreferenceIsbunker(MainActivity.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0")
                                );
                            }
                        }

                        if(jsonArrayResult.getJSONObject(i).has("Documentos_extras")) {
                            Documentos.executeQuery("DELETE FROM DOCUMENTOS WHERE bitacora = '" + bitacoraFromResponse + "' and sync ='1'");
                            JSONArray jsonArrayDocumentosExtras = jsonArrayResult.getJSONObject(i).getJSONArray("Documentos_extras");
                            for (int position = 0; position <= jsonArrayDocumentosExtras.length() - 1; position++) {

                                DatabaseAssistant.insertarDocumentoExtraFromWebService(
                                        "" + bitacoraFromResponse,
                                        "" + jsonArrayDocumentosExtras.getJSONObject(position).getString("nombre")
                                );

                            }
                        }



                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorStackTracePlaces = true;
                    codigoErrorStackTracePlaces = e.getMessage();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                errorStackTracePlaces = true;
                codigoErrorStackTracePlaces = error.getMessage();
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
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
                    errorStackTracePlaces = false;
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
                    errorStackTracePlaces = true;
                    codigoErrorStackTracePlaces = e.getMessage();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                errorStackTracePlaces = true;
                codigoErrorStackTracePlaces = error.getMessage();
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
                    errorStackTracePlaces = false;
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
                    errorStackTracePlaces = true;
                    codigoErrorStackTracePlaces = e.getMessage();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        errorStackTracePlaces = true;
                        codigoErrorStackTracePlaces = error.getMessage();
                    }
                }) {
        };
        //postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 1, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
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
                    errorStackTraceChoferes = false;

                    for (int i = 0; i <= jsonArrayChoferes.length() - 1; i++) {
                        DatabaseAssistant.insertarChoferes(
                                "" + jsonArrayChoferes.getJSONObject(i).getString("name"),
                                "" + jsonArrayChoferes.getJSONObject(i).getString("status"),
                                "" + jsonArrayChoferes.getJSONObject(i).getString("id")
                        );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    errorStackTraceChoferes = true;
                    codigoErrorStackTraceChoferes = e.getMessage();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        errorStackTraceChoferes = true;
                        codigoErrorStackTraceChoferes = error.getMessage();
                    }
                }) {
        };
        //postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 1, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }


    public void showErrorDialog(final String codeError, String bitacora) {
        final NeumorphButton btNo, btSi;
        TextView tvCodeError, tvBitacora;
        EditText etDescripcionPeticion;
        Dialog dialogoError = new Dialog(MainActivity.this);
        dialogoError.setContentView(R.layout.layout_error);
        dialogoError.setCancelable(true);
        etDescripcionPeticion = (EditText) dialogoError.findViewById(R.id.etDescripcionPeticion);
        btNo = (NeumorphButton) dialogoError.findViewById(R.id.btNo);
        btSi = (NeumorphButton) dialogoError.findViewById(R.id.btSi);
        tvCodeError = (TextView) dialogoError.findViewById(R.id.tvCodeError);
        tvBitacora = (TextView) dialogoError.findViewById(R.id.tvBitacora);
        tvCodeError.setText(codeError);



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
                } else if (codeError.equals("Parece que los articulos de velación no estan completos, necesitas autorización para terminar la bitácora.")){
                    dialogoError.dismiss();
                    descripcionPeticion = etDescripcionPeticion.getText().toString();
                    doRequestForEndBinnacle();
                    requesterCanceled = false;
                    status = 0;
                }
                else {
                    if (codeError.equals("¿Cerrar sesión?")) {

                        if (DatabaseAssistant.cerrarSesionEnUnLugarEspecifico()) {

                            if (Preferences.getEndLoginTheZone(MainActivity.this, Preferences.PREFERENCE_END_SESION_THE_ZONE_TO_LOGIN)) {

                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                                Calendar cal = Calendar.getInstance();

                                Preferences.setPreferenceCheckinCheckoutAssistant(MainActivity.this, false, Preferences.PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT);
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
                                            isProveedor,
                                            Preferences.getGeofenceActual(getApplicationContext(), Preferences.PREFERENCE_GEOFENCE_ACTUAL),
                                            "" + DatabaseAssistant.getLastIsFuneraria()
                                    );

                                    dialogoError.dismiss();
                                    Toast.makeText(getApplicationContext(), "Hasta luego", Toast.LENGTH_SHORT).show();
                                    finishAffinity();
                                } else
                                    Toast.makeText(MainActivity.this, "Ocurrio un error al cerrar tu sesión, intenta nuevamente", Toast.LENGTH_SHORT).show();
                            } else
                                showErrorDialog("No puedes hacer tu CHECK_OUT en esta zona, necesitas estar en la zona indicada por tu coordinador", "");
                        } else {
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                            Calendar cal = Calendar.getInstance();
                            Preferences.setPreferenceCheckinCheckoutAssistant(MainActivity.this, false, Preferences.PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT);
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
                                        isProveedor,
                                        Preferences.getGeofenceActual(getApplicationContext(), Preferences.PREFERENCE_GEOFENCE_ACTUAL),
                                        "" + DatabaseAssistant.getLastIsFuneraria()
                                );
                                dialogoError.dismiss();
                                Toast.makeText(getApplicationContext(), "Hasta luego", Toast.LENGTH_SHORT).show();

                                finishAffinity();

                            } else
                                Toast.makeText(MainActivity.this, "Ocurrio un error al cerrar tu sesión, intenta nuevamente", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        goToNuevaBitacora = true;
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

    void registrarBitacoraTerminada(String bitacora) {
        try {
            Calendar cal = Calendar.getInstance();
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

                /*LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup) findViewById(R.id.relativeLayout1));
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view);
                toast.show();*/


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


                consultarBitacorasActivas();
                //dialogoSalidas.dismiss();
            }
        } catch (Throwable e) {
            Log.e(TAG, "registrarBitacoraTerminada: " + e.getMessage());
        }
    }

    public void showSalidaLlegada(int tipo, boolean pedirDestino, String bitacora, String destinoAnterior, boolean actualizarDestino) {
        TextView etTitulo, etSalidaLlegada, etDestino, etBitacora;
        Spinner spLugares, spDestino;
        NeumorphButton btCancelar, btGuardar;

        dialogoSalidas.setContentView(R.layout.layout_entrada_salida);
        dialogoSalidas.setCancelable(false);
        etTitulo = (TextView) dialogoSalidas.findViewById(R.id.etTitulo);
        etSalidaLlegada = (TextView) dialogoSalidas.findViewById(R.id.etSalidaLlegada);
        etDestino = (TextView) dialogoSalidas.findViewById(R.id.etDestino);
        spLugares = (Spinner) dialogoSalidas.findViewById(R.id.spLugares);
        spDestino = (Spinner) dialogoSalidas.findViewById(R.id.spDestino);
        btGuardar = (NeumorphButton) dialogoSalidas.findViewById(R.id.btGuardar);
        btCancelar = (NeumorphButton) dialogoSalidas.findViewById(R.id.btCancelar);
        etBitacora = (TextView) dialogoSalidas.findViewById(R.id.etBitacora);
        etBitacora.setText(bitacora);

        try {
            ArrayAdapter<String> adapterColonias = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, DatabaseAssistant.getLugares());

            if (pedirDestino) {
                spDestino.setAdapter(adapterColonias);
                spLugares.setVisibility(View.GONE);
                etSalidaLlegada.setVisibility(View.GONE);
                spDestino.setVisibility(View.VISIBLE);
                etDestino.setVisibility(View.VISIBLE);
            } else
                spLugares.setAdapter(adapterColonias);
        }catch (Throwable e){
            Log.e(TAG, "showSalidaLlegada: " + e.getMessage());
        }


        if (tipo == 1) {
            etTitulo.setText("Nueva Salida");
            etSalidaLlegada.setText("Lugar de salida");
        } else if (tipo == 2) {
            etTitulo.setText("Nueva Llegada");
            etSalidaLlegada.setText("Lugar de llegada");
        }


        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialogoSalidas.dismiss();
                    dialogoSalidas.cancel();
                }catch (Throwable e){
                    Log.e(TAG, "onClick: btCancelar error");
                }

            }
        });

        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!spDestino.getSelectedItem().toString().equals(destinoAnterior))
                {
                    if(actualizarDestino){

                        DatabaseAssistant.updateDestinoDeBitacoraPorSalidaAutomatica(bitacora, spDestino.getSelectedItem().toString());

                        String[] datosBitacoraActiva = DatabaseAssistant.getDatosDeBitacoraActiva(bitacora);
                        ApplicationResourcesProvider.insertarMovimiento(datosBitacoraActiva[1], datosBitacoraActiva[2], "ACTUALIZACION DE DESTINO DE SALIDA AUTOMATICA: " + datosBitacoraActiva[0]);

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
                        consultarBitacorasActivas();
                        dialogoSalidas.dismiss();
                    }else {
                        try {
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
                            String hora = horaFormat.format(cal.getTime());
                            String[] datosBitacoraActiva = DatabaseAssistant.getDatosDeBitacoraActiva(bitacora);
                            String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();

                            if (datosBitacoraActiva.length > 0) {
                                DatabaseAssistant.insertarEventos(
                                        "" + datosBitacoraActiva[0],
                                        "" + datosBitacoraActiva[1],
                                        "" + datosBitacoraActiva[2],
                                        "" + datosBitacoraActiva[3],
                                        pedirDestino ? datosBitacoraActiva[11] : spLugares.getSelectedItem().toString(),
                                        "" + dateFormat.format(new Date()),
                                        "" + hora,
                                        "" + datosBitacoraActiva[4],
                                        "" + Double.parseDouble(arregloCoordenadas[0]),
                                        "" + Double.parseDouble(arregloCoordenadas[1]),
                                        "" + datosBitacoraActiva[5],
                                        "" + (tipo == 1 ? "Salida" : tipo == 2 ? "Llegada" : ""),
                                        "" + datosBitacoraActiva[6],
                                        "" + datosBitacoraActiva[7],
                                        "" + datosBitacoraActiva[8],
                                        pedirDestino ? spDestino.getSelectedItem().toString() : datosBitacoraActiva[10],
                                        "0",
                                        "" + datosBitacoraActiva[12]
                                );

                                ApplicationResourcesProvider.insertarMovimiento(datosBitacoraActiva[1], datosBitacoraActiva[2], "REGISTRO DE SALIDA: " + datosBitacoraActiva[0]);

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
                                consultarBitacorasActivas();
                                dialogoSalidas.dismiss();


                            }

                        } catch (Throwable e) {
                            Log.e(TAG, "Error en click de salidas y llegadas: " + e.getMessage());
                        }
                    }
                } else
                    showErrorDialog("Salida invalida, tienes que seleccionar el destino diferente a tu llegada.", "");
            }
        });


        dialogoSalidas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogoSalidas.show();

    }


    void registrarLlegadaSinPedirDestino(String lugar, int tipo, String bitacora) {
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            String hora = horaFormat.format(cal.getTime());
            String[] datosBitacoraActiva = DatabaseAssistant.getDatosDeBitacoraActiva(bitacora);
            String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();

            if (datosBitacoraActiva.length > 0) {
                DatabaseAssistant.insertarEventos(
                        "" + datosBitacoraActiva[0],
                        "" + datosBitacoraActiva[1],
                        "" + datosBitacoraActiva[2],
                        "" + datosBitacoraActiva[3],
                        "" + lugar,
                        "" + dateFormat.format(new Date()),
                        "" + hora,
                        "" + datosBitacoraActiva[4],
                        "" + Double.parseDouble(arregloCoordenadas[0]),
                        "" + Double.parseDouble(arregloCoordenadas[1]),
                        "" + datosBitacoraActiva[5],
                        "" + (tipo == 1 ? "Salida" : tipo == 2 ? "Llegada" : ""),
                        "" + datosBitacoraActiva[6],
                        "" + datosBitacoraActiva[7],
                        "" + datosBitacoraActiva[8],
                        "",
                        "0",
                        "" + datosBitacoraActiva[12]
                );

                ApplicationResourcesProvider.insertarMovimiento(datosBitacoraActiva[1], datosBitacoraActiva[2], "REGISTRO DE LLEGADA: " + datosBitacoraActiva[0]);
                //Toast.makeText(MainActivity.this, (tipo == 1 ? "Salida" : tipo == 2 ? "Llegada" : "") + " guardada correctamente", Toast.LENGTH_SHORT).show();

               /* LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup) findViewById(R.id.relativeLayout1));

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view);
                toast.show();*/

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

                consultarBitacorasActivas();
            }

        } catch (Throwable e) {
            Log.e(TAG, "Error en click de salidas y llegadas: " + e.getMessage());
        }
    }


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getApplicationContext(), "resultado", Toast.LENGTH_SHORT).show();
    }*/


    @Override
    public void onPause() {
        super.onPause();

        handler.removeCallbacks(null);
        handler.removeMessages(0);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onResume() {
        super.onResume();

        Equipoinstalacion.executeQuery("DELETE FROM EQUIPOINSTALACION WHERE sync ='4'");
        Equipocortejo.executeQuery("DELETE FROM EQUIPOCORTEJO WHERE sync ='4'");
        EquipoRecoleccion.executeQuery("DELETE FROM EQUIPO_RECOLECCION WHERE sync ='4'");
        EquipoTraslado.executeQuery("DELETE FROM EQUIPO_TRASLADO WHERE sync ='4'");
        consultarBitacorasActivas();

        goToBitacoraDetalle = false;
        goToNuevaBitacora = false;
        try {
            if (handler != null) {
                timerUpdateBitacorasActivas();
            }
        } catch (Throwable e) {
            Log.e(TAG, "onResume: Error " + e.getMessage());
        }

    }


    void actualizarDatos() {
        if (DatabaseAssistant.laBitacoraEstaActiva("auxiliar")) {
            String[] datosBitacoraActiva = DatabaseAssistant.getDatosDeBitacoraActiva("auxiliar");
            if (datosBitacoraActiva.length > 0) {
                tvnumeroBitacora.setText(datosBitacoraActiva[0]);
                tvChofer.setText(datosBitacoraActiva[1]);
                tvAyudante.setText(datosBitacoraActiva[2]);
                tvSecondName.setText(datosBitacoraActiva[6]);
                tvVehiculo.setText(datosBitacoraActiva[3]);
                tvAddress.setText(datosBitacoraActiva[7]);
                tvPhone.setText(datosBitacoraActiva[8]);
                //cardBitacoraActiva.setVisibility(View.VISIBLE);
                //textoSinBitacora.setText("Tienes una bitácora activa");

                /*LinearLayout layoutLastPlace = findViewById(R.id.layoutLastPlace);
                try {
                    TextView tvUltimoLugar = findViewById(R.id.tvUltimoLugar);
                    tvUltimoLugar.setText(DatabaseAssistant.getLastPlaceBitacora(datosBitacoraActiva[0]));
                    tvUltimoLugar.setVisibility(View.VISIBLE);
                    layoutLastPlace.setVisibility(View.VISIBLE);
                } catch (Throwable e) {
                    Log.e(TAG, e.getMessage());
                    layoutLastPlace.setVisibility(View.GONE);
                }*/


                //Poner aqui los codigos de barras que tienen registrados por bitacora
                LinearLayout layoutCodigosDeBarras = findViewById(R.id.layoutCodigosDeBarras);
                List<Codigos> lista = Codigos.findWithQuery(Codigos.class, "SELECT * FROM CODIGOS ORDER BY id DESC");
                if (lista.size() > 0) {
                    layoutCodigosDeBarras.setVisibility(View.VISIBLE);
                    TextView tvCodigos = findViewById(R.id.tvCodigos);
                    String texto = "";
                    for (int i = 0; i <= lista.size() - 1; i++) {
                        texto = texto + lista.get(i).getCodigobarras() + "\n";
                    }
                    tvCodigos.setText(texto);
                } else
                    layoutCodigosDeBarras.setVisibility(View.GONE);


                Snackbar.make(fabb, "Datos actualizados correctamente", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else {
                //textoSinBitacora.setText("Parece que no tienes ningúna bitácora activa actualmente");
                cardBitacoraActiva.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "No tienes bitácoras activas por actualizar", Toast.LENGTH_LONG).show();
            //textoSinBitacora.setText("Parece que no tienes ningúna bitácora activa actualmente");
            cardBitacoraActiva.setVisibility(View.GONE);
        }
    }


    //** MAPAS **//
    void playProcessLocationAndMarkers(LatLng geofencePoint, String namePlace, int radius) {
        addMarker(geofencePoint, namePlace);
        addCircle(geofencePoint, radius);
        addGeofence(geofencePoint, radius, namePlace);
    }

    void addMarker(LatLng latLng, String namePlace) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        markerOptions.title(namePlace);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
    }

    void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(100);
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
        circleOptions.fillColor(Color.argb(64, 255, 0, 0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }


    void addGeofence(LatLng latLng, float radius, String idGeoZona) {
        geofencingClient = LocationServices.getGeofencingClient(this);
        geoFenceHelper = new GeoFenceHelper(this);

        Geofence geofence = geoFenceHelper.getGeoFence(
                idGeoZona,
                latLng,
                radius,
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

    void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Tenemos el permiso
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                //No teenemos el permiso
            }
        }


        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Tenemos el permiso
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Toast.makeText(getApplicationContext(), "Permiso de localización activado", Toast.LENGTH_SHORT).show();
            } else {
                //No teenemos el permiso
                Toast.makeText(getApplicationContext(), "Se necesita el permiso de localización", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
        handler.removeMessages(0);
        stopService();
        try {
            geofencingClient.removeGeofences(pendingIntent);
        }catch (Throwable e){
            Log.e(TAG, "onDestroy: Error en removeGeofences" );
        }
        Preferences.setWithinTheZone(getApplicationContext(), false, Preferences.PREFERENCE_WITHIN_THE_ZONE_TO_LOGIN);
        Preferences.setEndLoginTheZone(getApplicationContext(), false, Preferences.PREFERENCE_END_SESION_THE_ZONE_TO_LOGIN);

    }

    /*@Override
    public void onStop() {
        super.onStop();
        Toast.makeText(getApplicationContext(), "onStop", Toast.LENGTH_SHORT).show();
        handler.removeCallbacks(null);
        handler.removeMessages(0);
        if(!goToBitacoraDetalle || !goToNuevaBitacora) {
            geofencingClient.removeGeofences(pendingIntent);
            Preferences.setWithinTheZone(getApplicationContext(), false, Preferences.PREFERENCE_WITHIN_THE_ZONE_TO_LOGIN);
            Preferences.setEndLoginTheZone(getApplicationContext(), false, Preferences.PREFERENCE_END_SESION_THE_ZONE_TO_LOGIN);
        }
    }*/

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


    //*********************************** MAPAS******************************
    private void showMyCustomDialog() {
        final FrameLayout flLoading = (FrameLayout) findViewById(R.id.layoutCargando);
        flLoading.setVisibility(View.VISIBLE);
    }

    private void dismissMyCustomDialog() {
        final FrameLayout flLoading = (FrameLayout) findViewById(R.id.layoutCargando);
        flLoading.setVisibility(View.GONE);
    }

    public void timerUpdateBitacorasActivas() {
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    timerUpdateBitacorasActivas();
                    consultarBitacorasActivas();

                    if (DatabaseAssistant.getTotalDeEventosPorSincronizar() > 0) {
                        tvContador.setVisibility(View.VISIBLE);
                        tvContador.setText(String.valueOf(DatabaseAssistant.getTotalDeEventosPorSincronizar()));
                    } else {
                        tvContador.setVisibility(View.GONE);
                    }


                } catch (Throwable e) {
                    Log.d(TAG, "run: Error on TimerUpdate");
                }
            }
        }, 7000);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return false;
    }

    //FINGERPRINT DISABLED
    /*  FINGERPRINT DISABLED
        public void fingerprintCheck () {


        dialogoFingerPrint.setContentView(R.layout.fingerprint_autentication_layout);
        dialogoFingerPrint.setCancelable(false);

        mHeadingLabel = (TextView) dialogoFingerPrint.findViewById(R.id.headingLabel);
        mFingerprintImage = (ImageView) dialogoFingerPrint.findViewById(R.id.fingerprintImage);
        mParaLabel = (TextView) dialogoFingerPrint.findViewById(R.id.paraLabel);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (!fingerprintManager.isHardwareDetected()) {
                mParaLabel.setText("Este dispositivo no cuenta con scanner de huellas");
            } else if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                mParaLabel.setText("No tienes permisos para usar el escanner de huella");
            } else if (!keyguardManager.isKeyguardSecure()) {
                mParaLabel.setText("Agrega la huella en configuración");
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                mParaLabel.setText("Debes agregar al menos 1 huella");
            } else {
                mParaLabel.setText("Por favor coloca tu huella para marcar tu asistencia");
                generateKey();
                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler fingerprintHandler = new FingerprintHandler(getApplicationContext());
                    fingerprintHandler.startAuth(fingerprintManager, cryptoObject);
                    dialogoFingerPrint.dismiss();
                }

            }

        }

        dialogoFingerPrint.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogoFingerPrint.show();

    }
        @TargetApi(Build.VERSION_CODES.M)
        private void generateKey () {

        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();

        } catch (KeyStoreException | IOException | CertificateException
                | NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | NoSuchProviderException e) {

            e.printStackTrace();

        }

    }
        @TargetApi(Build.VERSION_CODES.M)
        public boolean cipherInit () {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }


        try {

            keyStore.load(null);

            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);

            cipher.init(Cipher.ENCRYPT_MODE, key);

            return true;

        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }

    }
    */

    @Override
    public void onRequeriedEventoSalida(int position, String bitacora) {
        try {
            if (!dialogoSalidas.isShowing()) {

            /*LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.custom_layout_pedir_destino_de_bitacora, (ViewGroup)findViewById(R.id.relativeLayout1));
            TextView tvBitacoraFaltanteDeDestino = view.findViewById(R.id.tvBitacoraFaltanteDeDestino);
            tvBitacoraFaltanteDeDestino.setText(bitacora);
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(view);
            toast.show();*/

                //Validar si es automatico, para que pida la salida

                //Callback desde Adapter

                Log.d("GeofenceBroadcastReceiv", "onRequeriedEventoSalida: Entro a salida automatica, requiere que se ingrese una salida.");
                try {
                    String[] evento = DatabaseAssistant.getUltimoEvento(bitacora);
                    String destino = evento[1];

                    if (!destino.equals("") || !destino.isEmpty())
                        //registrarLlegadaSinPedirDestino("" + destinoOriginal, 2, bitacora);
                        Log.d(TAG, "onRequeriedEventoSalida: Registra la llegada sin pedir destino");
                    else
                        showSalidaLlegada(1, true, bitacora, evento[0], true);
                }catch (Throwable e){
                    Log.e(TAG, "onRequeriedEventoSalida: Error " + e.getMessage());
                }
            }
        }catch (Throwable e){
            Log.e(TAG, "onRequeriedEventoSalida: " + e.getMessage());
        }

    }

    @Override
    public void onClickRegistrarSalida(int position, String bitacora, String destinoOriginal) {
        //Callback desde Adapter

        String[] evento = DatabaseAssistant.getUltimoEvento(bitacora);
        String destino = evento[1];

        if (!destino.equals("") || !destino.isEmpty())
            registrarLlegadaSinPedirDestino("" + destinoOriginal, 2, bitacora);
        else
            showSalidaLlegada(1, true, bitacora, evento[0], false);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

        private static final String TAG = "FINGERPRINT";
        private Context context;

        public FingerprintHandler(Context context) {
            this.context = context;
        }

        public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
            CancellationSignal cancellationSignal = new CancellationSignal();
            fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            this.update("Error de Autenticación: " + errString, false);
            mParaLabel.setTextColor(ContextCompat.getColor(context, R.color.red_color));
            mFingerprintImage.setImageResource(R.drawable.ic_fingerprint_red);
        }

        @Override
        public void onAuthenticationFailed() {
            this.update("Autenticación fallida.", false);
            mParaLabel.setTextColor(ContextCompat.getColor(context, R.color.red_color));
            mFingerprintImage.setImageResource(R.drawable.ic_fingerprint_red);
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            this.update("Ocurrio un error: " + helpString, false);
            mParaLabel.setTextColor(ContextCompat.getColor(context, R.color.red_color));
            mFingerprintImage.setImageResource(R.drawable.ic_fingerprint_red);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            this.update("Autenticación satisfactoria.", true);
        }

        private void update(String s, boolean b) {
            try {
                mParaLabel.setText(s);
                if (!b) {
                    mParaLabel.setTextColor(ContextCompat.getColor(context, R.color.purple_500));
                } else {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                mParaLabel.setTextColor(ContextCompat.getColor(context, R.color.teal_700));
                                mFingerprintImage.setImageResource(R.drawable.action_done);
                                synchronized (this) {
                                    wait(1500);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (dialogoFingerPrint.isShowing())
                                                dialogoFingerPrint.dismiss();
                                        }
                                    });

                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        ;
                    };
                    thread.start();
                }

            } catch (Throwable e) {
                Log.e(TAG, "update: Error fingerprint");
            }


        }
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
    public void onClickShowDetails(int position, String bitacora) {
        goToBitacoraDetalle = true;
        Intent intent = new Intent(getApplicationContext(), BitacoraDetalle.class);
        intent.putExtra("bitacora", bitacora);
        startActivity(intent);

    }

    @Override
    public void onClickTerminarBitacora(int position, String bitacora) {
        //showErrorDialog("Estas a punto de terminar la bitácora\n¿Estás de acuerdo?", bitacora);
        this.bitacora = bitacora;

        if(DatabaseAssistant.articulosDeVelacionYCortejoEstanCompletos(bitacora)
                && DatabaseAssistant.articulosDeRecoleccionEstanCompletos(bitacora)
                && DatabaseAssistant.articulosDeTrasladoEstanCompletos(bitacora)) {
            showErrorDialog("Estas a punto de terminar la bitácora\n¿Estás de acuerdo?", bitacora);
        }
        else
            showErrorDialog("Parece que los articulos de velación no estan completos, necesitas autorización para terminar la bitácora.", bitacora);

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

                                if(checkUpdate == 1) {
                                    if (checkUpdate == 1 && !updatePostponed)
                                        checkVersion();
                                    else if (checkUpdate == 2)
                                        forceUpdate();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "No hay actualizaciones disponibles", Toast.LENGTH_LONG).show();
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

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

                                    BroadcastReceiver onComplete = new BroadcastReceiver()
                                    {
                                        @Override
                                        public void onReceive(Context context, Intent intent)
                                        {
                                            if (file.exists())
                                            {
                                                Uri apkUri = Uri.fromFile(file);
                                                Intent update = new Intent();
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                                {
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
                                                finish();

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
                    else
                        Toast.makeText(getApplicationContext(), "Ya tienes la última versión. Felicidades", Toast.LENGTH_LONG).show();
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
                pDialog = new ProgressDialog(MainActivity.this);
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


    @Override
    protected void onStart() {
        super.onStart();

        CatalogoArticulos.deleteAll(CatalogoArticulos.class);
        DatabaseAssistant.insertarCatalogoArticulos("Cafetera", "CF");
        DatabaseAssistant.insertarCatalogoArticulos("Candelabro", "CL");
        DatabaseAssistant.insertarCatalogoArticulos("Cristo y base", "CB");
        DatabaseAssistant.insertarCatalogoArticulos("Pedestal", "PD");
        DatabaseAssistant.insertarCatalogoArticulos("Biombo", "BM");
        DatabaseAssistant.insertarCatalogoArticulos("Ataúd de traslado", "AT");
        DatabaseAssistant.insertarCatalogoArticulos("Carrito pedestal", "CP");
        DatabaseAssistant.insertarCatalogoArticulos("Carrito infantil", "CI");
        DatabaseAssistant.insertarCatalogoArticulos("Candelero infantil", "CN");
        DatabaseAssistant.insertarCatalogoArticulos("Pedestal infantil", "PI");
        DatabaseAssistant.insertarCatalogoArticulos("Kit de Cafetería", "KC");
        DatabaseAssistant.insertarCatalogoArticulos("Camilla rueda", "CU");
        DatabaseAssistant.insertarCatalogoArticulos("Camilla de rescate", "CR");
        DatabaseAssistant.insertarCatalogoArticulos("Ataúd de recolección", "AA");


        /*try {
            String queryJSON ="UPDATE ASISTENCIA asist inner join (select * from SESIONES where usuario != '' ORDER BY id desc limit 1) as ses on asist.usuario = ses.usuario SET asist.usuario = ses.usuario where asist.usuario = ''";
            Log.i("ServerQuery", "from ApplicationResourcesProvider " + queryJSON);
            if (!queryJSON.equals("") && !queryJSON.equals("null")) {
                SugarRecord.executeQuery(queryJSON);
                Log.d("SESIONES", "onResponse: Query ejecutado correctamente.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/



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
                                        MainActivity.this,
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


        //downloadNotifications();
    }


    public void showBottomLayoutForHorariosDeAsistencia(View view, boolean actualmenteEsEntrada) {
        try {
            String tipoDEntrada="";
            RadioButton bt7am, bt8am, bt9am, bt19, bt20, bt7amm, bt199, bt19a9;
            NeumorphButton btGuardarAsistencia;
            TextView tvMensaje, tvOrigen, tvDestino;
            mBottomSheetDialog = new BottomSheetDialog(MainActivity.this);
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
                            horaSeleccionada = "00:00:00";
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



                    if(finalTipoDEntrada.equals("3") && horaSeleccionada.equals("00:00:00") || horaSeleccionada == null){
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
                                "" + (Preferences.getPreferenceIsbunker(MainActivity.this, Preferences.PREFERENCE_ISBUNKER) ? "1" : "0"),
                                "" + DatabaseAssistant.getIsProveedor(),
                                Preferences.getGeofenceActual(getApplicationContext(), Preferences.PREFERENCE_GEOFENCE_ACTUAL),
                                "" + horaSeleccionada,
                                "" + horaEntrada,
                                ""+ horaSalida
                        );

                        if(finalTipoDEntrada.equals("3")){
                            Preferences.setPreferencesAsistenciaEntrada(MainActivity.this, true, Preferences.PREFERENCE_ASISTENCIA_ENTRADA);

                        }else {
                            Preferences.setPreferencesAsistenciaEntrada(MainActivity.this, false, Preferences.PREFERENCE_ASISTENCIA_ENTRADA);
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


    private void sincronizarDatos() {
        if (ApplicationResourcesProvider.checkInternetConnection()) {
            showMyCustomDialog();
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ApplicationResourcesProvider.createJsonForSync();
                            }
                        });

                        synchronized (this) {
                            wait(3000);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissMyCustomDialog();
                                    Toast.makeText(getApplicationContext(), "Sincronizado correctamente", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Ocurrio un error, intenta de nuevo", Toast.LENGTH_SHORT).show();
                    }
                }

                ;
            };
            thread.start();
        } else
            showErrorDialog("No hay conexión a internet", "");
    }



    private void loadRequests() {

        //***************************** descarga de carrosas
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
                                            Toast.makeText(MainActivity.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            errorStackTracePlaces = true;
                                            codigoErrorStackTracePlaces = e.getMessage();
                                            dismissMyCustomDialog();
                                            Toast.makeText(MainActivity.this, "Ocurrio un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(MainActivity.this, "Ocurrio un error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(MainActivity.this, "Ocurrio un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(MainActivity.this, "Ocurrio un error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MainActivity.this, "Ocurrio un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, "Ocurrio un error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 1, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);

        //********************** FIN DESCARGA DE CARROSAS*********************************














        // pasar aqui todos los request normales, para hacer la validacion del loading
    }


    void doRequestForEndBinnacle()
    {
        try {
            status = 0;
            showLoadingDialog();
            new Thread(() -> {
                while (status == 0)
                {
                    Log.d("TIMER", "doRequestForEndBinnacle: Iteracion de ciclo para saber si la bitacora fue autorizada a terminar");
                    if (ApplicationResourcesProvider.checkInternetConnection() && getApplicationContext() != null) {
                        runOnUiThread(this::getStatusBinaccle);
                    } else {
                        runOnUiThread(() -> {
                            if (getApplicationContext() != null) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Verifica tu conexión a internet", Toast.LENGTH_SHORT);toast.show();
                                hideLoadingDialog();
                            }
                        });
                        runOnUiThread(this::showLoadingDialog);
                    }
                    Log.d("TIMER", "doRequestForEndBinnacle: Esperando 5 segundos...");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        hideLoadingDialog();
                        Toast.makeText(this, "Error: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("TIMER", "doRequestForEndBinnacle: Error: " + e.getMessage());
                    }
                }
                Log.d("TIMER" ,"while(status == 0) -> status = " + status);
            }).start();
        }catch (Throwable e){
            Log.e("TIMER", "registrarBitacoraTerminada: " + e.getMessage());
            hideLoadingDialog();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void hideLoadingDialog(){
        RelativeLayout frameLoading = (RelativeLayout) findViewById(R.id.frameLoading);
        frameLoading.setVisibility(View.GONE);
    }

    void showLoadingDialog(){
        RelativeLayout frameLoading = (RelativeLayout) findViewById(R.id.frameLoading);
        frameLoading.setVisibility(View.VISIBLE);
    }



    private void getStatusBinaccle() {
        JSONObject json = new JSONObject();

        try {
            json.put("bitacora", bitacora);
            json.put("descripcion", descripcionPeticion);
            json.put("usuario_peticion", DatabaseAssistant.getUserNameFromSesiones());
            json.put("fecha_peticion", fechaEstatica);
            requestStatusBinaccle(json);
        } catch (Exception e) {
            Log.d("TIMER", "getStatusBinaccle: error en creacion de json para status de bitacora");
            hideLoadingDialog();
            Toast.makeText(this, "Ocurrio un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void requestStatusBinaccle(JSONObject jsonParams)
    {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_CHECK_STATUS_BINNACLE, jsonParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("TIMER", "onResponse: " + ConstantsBitacoras.WS_CHECK_STATUS_BINNACLE);
                manage_GetStatusBinnacle(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        status = -1;
                        Log.e("TIMER", "onErrorResponse: Error: " + error.getMessage());
                        hideLoadingDialog();
                        Toast.makeText(getApplicationContext(), "Ocurrio un error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }

    public void manage_GetStatusBinnacle(JSONObject json)
    {
        if (status != -1 && !requesterCanceled)
        {
            try
            {
                status = 0;
                status = json.getInt("status");

                if (status == 0) {
                    //continue;
                    Log.d("TIMER", "manage_GetStatusBinnacle: Continua con el ciclo de bitacora.");
                }

                //accepted
                else if (status == 1) {
                    Toast.makeText(ApplicationResourcesProvider.getContext(), "Bitácora aceptada.", Toast.LENGTH_LONG).show();
                    registrarBitacoraTerminada(json.getString("bitacora"));
                    hideLoadingDialog();
                }

                //rechazado
                else if (status == 2) {
                    Toast.makeText(ApplicationResourcesProvider.getContext(), "Bitácora rechazada.", Toast.LENGTH_LONG).show();
                    hideLoadingDialog();
                }
                else {
                    status = -1;
                    hideLoadingDialog();
                    Toast.makeText(this, "La bitácora no fue aceptada.", Toast.LENGTH_SHORT).show();
                    requesterCanceled = true;
                }

            } catch (JSONException e) {
                status = -1;
                e.printStackTrace();
                Log.e("TIMER", "manage_GetStatusBinnacle: Error en obtener el dato de estatus: " + e.getMessage());
                hideLoadingDialog();
                Toast.makeText(this, "Ocurrio un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else
            Log.d("TIMER", "manage_GetStatusBinnacle: el estatus es -1");

    }

}