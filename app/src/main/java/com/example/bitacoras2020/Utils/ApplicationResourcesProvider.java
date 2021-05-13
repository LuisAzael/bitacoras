 package com.example.bitacoras2020.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bitacoras2020.Activities.Login;
import com.example.bitacoras2020.Activities.NuevaBitacora;
import com.example.bitacoras2020.Database.Activos;
import com.example.bitacoras2020.Database.Adicional;
import com.example.bitacoras2020.Database.Articuloscan;
import com.example.bitacoras2020.Database.Asistencia;
import com.example.bitacoras2020.Database.Bitacoras;
import com.example.bitacoras2020.Database.Cancelados;
import com.example.bitacoras2020.Database.CatalogoArticulos;
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
import com.example.bitacoras2020.Database.Movimientos;
import com.example.bitacoras2020.Database.Sesiones;
import com.example.bitacoras2020.Database.Ubicaciones;
import com.example.bitacoras2020.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.orm.SugarDb;
import com.orm.SugarRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ApplicationResourcesProvider extends com.orm.SugarApp {
    private static final String TAG = "ApplicationResourceProvider";
    private static Context sContext;
    private static Date date;
    private static String timeLocation = "0";
    public static double latitud = 0, longitud = 0;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static Typeface bold, light, regular;

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        Preferences.setGeofenceActual(sContext, "", Preferences.PREFERENCE_GEOFENCE_ACTUAL);

        bold = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        light = Typeface.createFromAsset(getAssets(), "fonts/light.ttf");
        regular = Typeface.createFromAsset(getAssets(), "fonts/regular.ttf");


        /*{ // Preferencias de dia siguiente
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            int lastTimeStarted = settings.getInt("last_time_started", -1);
            Calendar calendar = Calendar.getInstance();
            int today = calendar.get(Calendar.DAY_OF_YEAR);

            if (today != lastTimeStarted) {
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
                    } else
                        Log.d(TAG, "onCreate: No se encuentran datos de sesiones");
                } catch (Throwable e) {
                    Log.d(TAG, "onCreate: Error " + e.getMessage());
                }
            } else {
                Log.d(TAG, "onCreate: Mismo dia, no se actualiza información");
            }
        }*/

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("FIREBASE", "Refresh Token: "+ token);

        if(token!=null)
            DatabaseAssistant.insertarToken(token);
        else
            DatabaseAssistant.insertarToken("Unknown");

        timerForInsertLocations();
        timerForSyncData();
        timerCallLocations();
        updateLocation();



        if(checkInternetConnection())
            createJsonForSync();

        Preferences.setWithinTheZone(sContext, false, Preferences.PREFERENCE_WITHIN_THE_ZONE_TO_LOGIN);
    }

    @SuppressLint("LongLogTag")
    private void downloadCatalogoArticulos()
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

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_DOWNLOAD_ARTICULOS_CATALOGO, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                JSONArray bitacorasArray = new JSONArray();

                try
                {
                    bitacorasArray = response.getJSONArray("catalogo");

                    if(bitacorasArray.length()>0){
                        CatalogoArticulos.deleteAll(CatalogoArticulos.class);
                        for (int i = 0; i <= bitacorasArray.length() - 1; i++)
                        {
                            DatabaseAssistant.insertarCatalogoArticulos(
                                    bitacorasArray.getJSONObject(i).getString("name"),
                                    bitacorasArray.getJSONObject(i).getString("name")
                            );
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onResponse: Error en descarga de catalogo de articulos: " + e.getMessage());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e(TAG, "onResponse: Error en descarga de catalogo de articulos: " + error.getMessage());
                    }
                }) {

        };
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(postRequest);
    }

    public static Context getContext() {
        return sContext;
    }


    @SuppressLint("LongLogTag")
    public static void createJsonForSync()
    {

        try{


            @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(sContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            String query="SELECT * FROM EVENTOS where estatus = '0' ORDER BY id ASC";
            List<Eventos> listaMovimientos = Eventos.findWithQuery(Eventos.class, query);
            if(listaMovimientos.size()>0)
            {
                for (int i = 0; i <= listaMovimientos.size() - 1; i++)
                {
                    try
                    {
                        JSONObject jsonEventos = new JSONObject();
                        JSONArray jsonActivos = new JSONArray();
                        jsonEventos.put("s", "registrarSalida");
                        jsonEventos.put("evento_id", listaMovimientos.get(i).getId());
                        jsonEventos.put("fecha", listaMovimientos.get(i).getFecha());
                        jsonEventos.put("hora", listaMovimientos.get(i).getHora());
                        jsonEventos.put("bitacora", listaMovimientos.get(i).getBitacora());
                        jsonEventos.put("id_chofer", DatabaseAssistant.getidFromNombreChofer(listaMovimientos.get(i).getChofer()));
                        jsonEventos.put("id_ayudante",  DatabaseAssistant.getidFromNombreChofer(listaMovimientos.get(i).getAyudante()));
                        jsonEventos.put("id_lugar",  DatabaseAssistant.getidFromNombreLugar(listaMovimientos.get(i).getLugar()));
                        jsonEventos.put("id_lugar_destino",  DatabaseAssistant.getidFromNombreLugar(listaMovimientos.get(i).getDestino()));
                        jsonEventos.put("id_vehiculo",  DatabaseAssistant.getidFromNombreCarro(listaMovimientos.get(i).getCarro()));
                        jsonEventos.put("dispositivo", listaMovimientos.get(i).getDispositivo());
                        jsonEventos.put("lat", listaMovimientos.get(i).getLatitud());
                        jsonEventos.put("lng", listaMovimientos.get(i).getLongitud());
                        jsonEventos.put("evento", listaMovimientos.get(i).getTipo());
                        jsonEventos.put("automatico", listaMovimientos.get(i).getAutomatico());


                        jsonEventos.put("nombre_chofer", listaMovimientos.get(i).getChofer());
                        jsonEventos.put("nombre_ayudante", listaMovimientos.get(i).getAyudante());
                        jsonEventos.put("nombre_lugar", listaMovimientos.get(i).getLugar());
                        jsonEventos.put("nombre_destino", listaMovimientos.get(i).getDestino());
                        jsonEventos.put("nombre_vehiculo", listaMovimientos.get(i).getCarro());
                        jsonEventos.put("token_device", DatabaseAssistant.getTokenDeUsuario());

                        jsonEventos.put("tipo_movimiento", listaMovimientos.get(i).getMovimiento());
                        jsonEventos.put("usuario", listaMovimientos.get(i).getUsuario());
                        jsonEventos.put("checkInDriver", listaMovimientos.get(i).getCheckInDriver());
                        jsonEventos.put("isProveedor", Preferences.getIsProveedor(sContext, Preferences.PREFERENCE_IS_PROVEEDOR) ? "1" : "0");

                        try{
                            List<Codigos> listaCodigos = Codigos.findWithQuery(Codigos.class, "SELECT * FROM CODIGOS where bitacora = '"+ listaMovimientos.get(i).getBitacora() +"'");
                            if(listaCodigos.size()>0)
                            {
                                for(int y=0; y<=listaCodigos.size()-1; y++) {
                                    JSONObject jsonCodigos = new JSONObject();
                                    jsonCodigos.put("evento_id_barras", listaCodigos.get(y).getId());
                                    jsonCodigos.put("name", listaCodigos.get(y).getCodigobarras());
                                    jsonCodigos.put("id", listaCodigos.get(y).getId());
                                    jsonActivos.put(jsonCodigos);
                                }
                                jsonEventos.put("activos", jsonActivos);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                        cargaDeDatosAServidor(jsonEventos);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                }
            }
            else
                Log.i(TAG, "NO hay registros de Eventos");


            //*********************************** JSON UBICACIONES ***************************************
            String queryUbicaciones="SELECT * FROM UBICACIONES";
            List<Ubicaciones> listaUbicaciones = Ubicaciones.findWithQuery(Ubicaciones.class, queryUbicaciones);
            if(listaUbicaciones.size()>0)
            {
                for (int i = 0; i <= listaUbicaciones.size() - 1; i++)
                {
                    try{
                        JSONObject jsonUbicaciones = new JSONObject();
                        jsonUbicaciones.put("evento_id", listaUbicaciones.get(i).getId());
                        jsonUbicaciones.put("id_device",  android_id);
                        jsonUbicaciones.put("lat", listaUbicaciones.get(i).getLatitud());
                        jsonUbicaciones.put("lng", listaUbicaciones.get(i).getLongitud());
                        jsonUbicaciones.put("fecha", listaUbicaciones.get(i).getFecha());
                        jsonUbicaciones.put("hora", listaUbicaciones.get(i).getHora());
                        jsonUbicaciones.put("isProveedor", Preferences.getIsProveedor(sContext, Preferences.PREFERENCE_IS_PROVEEDOR) ? "1" : "0");
                        jsonUbicaciones.put("usuario", DatabaseAssistant.getUserNameFromSesiones());
                        cargaDeUbicacionesToServer(jsonUbicaciones);

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                }
            }
            else
                Log.i(TAG, "NO hay registros de Ubicaciones");
            //***********************
            List<Movimientos> listaMovs = Movimientos.findWithQuery(Movimientos.class, "SELECT * FROM MOVIMIENTOS WHERE estatus = '0'");
            if(listaMovs.size()>0)
            {
                for (int i = 0; i <= listaMovs.size() - 1; i++)
                {
                    try{
                        JSONObject jsonMovimientoss = new JSONObject();
                        jsonMovimientoss.put("id_list", listaMovs.get(i).getId());
                        jsonMovimientoss.put("id_chofer", listaMovs.get(i).getIdchofer());
                        jsonMovimientoss.put("id_ayudante",  listaMovs.get(i).getIdayudante());
                        jsonMovimientoss.put("android_id",listaMovs.get(i).getAndroidid());
                        jsonMovimientoss.put("app_version",listaMovs.get(i).getAppversion());
                        jsonMovimientoss.put("android_model",listaMovs.get(i).getAndroidmodel());
                        jsonMovimientoss.put("imei", listaMovs.get(i).getImei());
                        jsonMovimientoss.put("fecha", listaMovs.get(i).getFecha());
                        jsonMovimientoss.put("movimiento", listaMovs.get(i).getMovimiento());
                        jsonMovimientoss.put("usuario", listaMovs.get(i).getUsuario());
                        jsonMovimientoss.put("isProveedor", Preferences.getIsProveedor(sContext, Preferences.PREFERENCE_IS_PROVEEDOR) ? "1" : "0");
                        cargaDeMovimientosToServer(jsonMovimientoss);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            else
                Log.i(TAG, "NO hay registros de Movimientos");






            //******** sesiones ******

            List<Sesiones> listaSesiones = Sesiones.findWithQuery(Sesiones.class, "SELECT * FROM SESIONES WHERE statos = '0' ORDER BY fecha ASC");
            if(listaSesiones.size()>0)
            {
                Log.d(TAG, "createJsonForSync: Comienza creación de JSON SESIONES");
                for(int i = 0; i <= listaSesiones.size()-1; i ++){
                    try {
                        JSONObject jsonParams = new JSONObject();
                        jsonParams.put("id_sync", listaSesiones.get(i).getId());
                        jsonParams.put("usuario", listaSesiones.get(i).getUsuario());
                        jsonParams.put("pass", listaSesiones.get(i).getContrasena());
                        jsonParams.put("fecha", listaSesiones.get(i).getFecha());
                        jsonParams.put("hora",  listaSesiones.get(i).getHora());
                        jsonParams.put("lat", listaSesiones.get(i).getLatitud());
                        jsonParams.put("lng", listaSesiones.get(i).getLongitud());
                        jsonParams.put("tipo", listaSesiones.get(i).getEstatus());
                        jsonParams.put("isBunker", listaSesiones.get(i).getIsBunker());
                        jsonParams.put("geoFenceActual", listaSesiones.get(i).getGeofence());
                        jsonParams.put("token_device", DatabaseAssistant.getTokenDeUsuario());
                        jsonParams.put("isProveedor", Preferences.getIsProveedor(sContext, Preferences.PREFERENCE_IS_PROVEEDOR) ? "1" : "0");
                        requestLoginOnline(jsonParams);
                        //Log.i("SESIONES", "createJsonForSync: json de sesiones: " + jsonParams.toString());
                    } catch (Exception e) {
                        Log.e("SESIONES", "createJsonForSync: Error en JSON de SESIONES: " + e.getMessage() );
                        e.printStackTrace();
                    }
                }
            }
            else
                Log.i(TAG, "NO hay registros de Sesiones");


            //*********************** Comentarios ********


            List<Comentarios> articuloscanList = Comentarios.findWithQuery(Comentarios.class, "SELECT * FROM COMENTARIOS WHERE sync='0'");
            if(articuloscanList.size()>0)
            {
                JSONArray jsonComentarioIndividual = new JSONArray();
                JSONObject jsonGeneralParaComentarios = new JSONObject();
                for(int x=0; x<=articuloscanList.size()-1; x++){
                    try {
                        JSONObject jsonComentario = new JSONObject();
                        jsonComentario.put("bitacora", articuloscanList.get(x).getBitacora());
                        jsonComentario.put("comentario", articuloscanList.get(x).getComentario());
                        jsonComentario.put("usuario", articuloscanList.get(x).getUsuario());
                        jsonComentario.put("fecha_captura", articuloscanList.get(x).getFecha());
                        jsonComentarioIndividual.put(jsonComentario);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                jsonGeneralParaComentarios.put("Comentarios_Array", jsonComentarioIndividual);
                requestSaveCommentariosToServer(jsonGeneralParaComentarios);
            }
            else
                Log.i(TAG, "NO hay registros de Comentarios");

            //****************************



            //******** sesiones ******

            List<Asistencia> asistenciaList = Asistencia.findWithQuery(Asistencia.class, "SELECT * FROM ASISTENCIA WHERE statos = '0'");
            if(asistenciaList.size()>0)
            {
                for(int i = 0; i <= asistenciaList.size()-1; i ++){
                    try {
                        JSONObject jsonParams = new JSONObject();
                        jsonParams.put("id_sync", asistenciaList.get(i).getId());
                        jsonParams.put("usuario", asistenciaList.get(i).getUsuario());
                        jsonParams.put("pass", asistenciaList.get(i).getContrasena());
                        jsonParams.put("fecha", asistenciaList.get(i).getFecha());
                        jsonParams.put("hora",  asistenciaList.get(i).getHora());
                        jsonParams.put("lat", asistenciaList.get(i).getLatitud());
                        jsonParams.put("lng", asistenciaList.get(i).getLongitud());
                        jsonParams.put("tipo", asistenciaList.get(i).getEstatus());
                        jsonParams.put("is_bunker", asistenciaList.get(i).getIsBunker());
                        jsonParams.put("geoFenceActual", asistenciaList.get(i).getGeofence());
                        jsonParams.put("isProveedor", asistenciaList.get(i).getIsProveedor());
                        jsonParams.put("horario_seleccionado", asistenciaList.get(i).getHoraSeleccionada());
                        requestdoCheckINAndCheckOut(jsonParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else
                Log.i(TAG, "NO hay registros de Sesiones");






            //*********************************** JSON Informacion adicionaladicional ***************************************
            List<Adicional> adicionalList = Adicional.findWithQuery(Adicional.class, "SELECT * FROM ADICIONAL WHERE sync ='0'");
            if(adicionalList.size()>0)
            {
                for (int i = 0; i <= adicionalList.size() - 1; i++)
                {
                    try{
                        JSONObject jsonInformacionAdicionalBitacora = new JSONObject();
                        JSONArray jsonActivos = new JSONArray();
                        JSONArray jsonEquiposInstalacion = new JSONArray();
                        JSONArray jsonEquiposCortejo = new JSONArray();
                        JSONArray jsonEquiposRecoleccion= new JSONArray();
                        JSONArray jsonEquiposTraslado= new JSONArray();
                        JSONArray jsonAtaudesAndUrnas = new JSONArray();
                        JSONArray jsonAtaudesAndUrnasEliminadas = new JSONArray();
                        jsonInformacionAdicionalBitacora.put("bitacora", adicionalList.get(i).getBitacora());
                        jsonInformacionAdicionalBitacora.put("fecha",  adicionalList.get(i).getFecha());
                        jsonInformacionAdicionalBitacora.put("hora_instalacion", adicionalList.get(i).getHoraRecoleccion());
                        jsonInformacionAdicionalBitacora.put("documentos_seleccion", adicionalList.get(i).getJsonAdicionalInfo());
                        jsonInformacionAdicionalBitacora.put("lugar_de_velacion", adicionalList.get(i).getLugarDeVelacion());
                        jsonInformacionAdicionalBitacora.put("observaciones_instalacion", adicionalList.get(i).getObservacionesInstalacion());
                        jsonInformacionAdicionalBitacora.put("observaciones_cortejo", adicionalList.get(i).getObservacionesCortejo());
                        jsonInformacionAdicionalBitacora.put("ropa_entregada", adicionalList.get(i).getRopaEntregada());
                        jsonInformacionAdicionalBitacora.put("tipo_servicio", adicionalList.get(i).getTipoDeServicio());
                        jsonInformacionAdicionalBitacora.put("encapsulado", adicionalList.get(i).getEncapsulado());
                        jsonInformacionAdicionalBitacora.put("observaciones_recoleccion", adicionalList.get(i).getObservacionesrecoleccion());
                        jsonInformacionAdicionalBitacora.put("observaciones_traslado", adicionalList.get(i).getObservacionestraslado());
                        jsonInformacionAdicionalBitacora.put("embalsamado_o_arreglo", adicionalList.get(i).getProcedimiento());
                        jsonInformacionAdicionalBitacora.put("laboratorio", adicionalList.get(i).getLaboratorio());
                        jsonInformacionAdicionalBitacora.put("idLaboratorio", DatabaseAssistant.getIDFromLaboratorioString(adicionalList.get(i).getLaboratorio()));
                        jsonInformacionAdicionalBitacora.put("isProveedor", Preferences.getIsProveedor(sContext, Preferences.PREFERENCE_IS_PROVEEDOR) ? "1" : "0");
                        jsonInformacionAdicionalBitacora.put("usuario", DatabaseAssistant.getUserNameFromSesiones());


                        try{
                            List<Documentos> documentosList = Documentos.findWithQuery(Documentos.class, "SELECT * FROM DOCUMENTOS where bitacora = '"+ adicionalList.get(i).getBitacora() +"' and sync ='0'");
                            if(documentosList.size()>0)
                            {
                                for(int y=0; y<=documentosList.size()-1; y++) {
                                    JSONObject jsonDocumentos = new JSONObject();
                                    jsonDocumentos.put("name_documento", documentosList.get(y).getDocumento());
                                    jsonDocumentos.put("fecha", documentosList.get(y).getFecha());
                                    jsonActivos.put(jsonDocumentos);
                                }
                                jsonInformacionAdicionalBitacora.put("Documentos_extras", jsonActivos);
                            }
                            else
                                Log.d(TAG, "createJsonForSync: No hay datos de documentos");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }


                        try{
                            List<Equipoinstalacion> equipoinstalacionList = Equipoinstalacion.findWithQuery(Equipoinstalacion.class, "SELECT * FROM EQUIPOINSTALACION where bitacora = '"+ adicionalList.get(i).getBitacora() +"' and sync ='0'");
                            if(equipoinstalacionList.size()>0)
                            {
                                for(int y=0; y<=equipoinstalacionList.size()-1; y++) {
                                    JSONObject jsonEquiposDeInstalacion = new JSONObject();
                                    jsonEquiposDeInstalacion.put("nombre", equipoinstalacionList.get(y).getNombre());
                                    jsonEquiposDeInstalacion.put("serie", equipoinstalacionList.get(y).getSerie());
                                    jsonEquiposDeInstalacion.put("fecha", equipoinstalacionList.get(y).getFecha());
                                    jsonEquiposDeInstalacion.put("latitud", equipoinstalacionList.get(y).getLatitud());
                                    jsonEquiposDeInstalacion.put("longitud", equipoinstalacionList.get(y).getLongitud());
                                    jsonEquiposDeInstalacion.put("isBunker", equipoinstalacionList.get(y).getIsBunker());
                                    jsonEquiposDeInstalacion.put("isProveedor", Preferences.getIsProveedor(sContext, Preferences.PREFERENCE_IS_PROVEEDOR) ? "1" : "0");
                                    jsonEquiposDeInstalacion.put("usuario", equipoinstalacionList.get(y).getUsuario());
                                    jsonEquiposInstalacion.put(jsonEquiposDeInstalacion);
                                }
                                jsonInformacionAdicionalBitacora.put("Equipos_instalacion", jsonEquiposInstalacion);
                            }
                            else
                                Log.d(TAG, "createJsonForSync: No hay datos de equipos de instalacion");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }


                        try{
                            List<Equipocortejo> equipocortejoList = Equipocortejo.findWithQuery(Equipocortejo.class, "SELECT * FROM EQUIPOCORTEJO where bitacora = '"+ adicionalList.get(i).getBitacora() +"' and sync ='0'");
                            if(equipocortejoList.size()>0)
                            {
                                for(int y=0; y<=equipocortejoList.size()-1; y++) {
                                    JSONObject jsonEquiposDeCortejo = new JSONObject();
                                    jsonEquiposDeCortejo.put("nombre", equipocortejoList.get(y).getNombre());
                                    jsonEquiposDeCortejo.put("serie", equipocortejoList.get(y).getSerie());
                                    jsonEquiposDeCortejo.put("fecha", equipocortejoList.get(y).getFecha());
                                    jsonEquiposDeCortejo.put("latitud", equipocortejoList.get(y).getLatitud());
                                    jsonEquiposDeCortejo.put("longitud", equipocortejoList.get(y).getLongitud());
                                    jsonEquiposDeCortejo.put("isBunker", equipocortejoList.get(y).getIsBunker());
                                    jsonEquiposDeCortejo.put("isProveedor", Preferences.getIsProveedor(sContext, Preferences.PREFERENCE_IS_PROVEEDOR) ? "1" : "0");
                                    jsonEquiposDeCortejo.put("usuario", equipocortejoList.get(y).getUsuario());
                                    jsonEquiposCortejo.put(jsonEquiposDeCortejo);
                                }
                                jsonInformacionAdicionalBitacora.put("Equipos_cortejo", jsonEquiposCortejo);
                            }
                            else
                                Log.d(TAG, "createJsonForSync: No hay datos de equipos de cortejo");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }


                        try{
                            List<EquipoRecoleccion> equipoRecoleccions = EquipoRecoleccion.findWithQuery(EquipoRecoleccion.class, "SELECT * FROM EQUIPO_RECOLECCION where bitacora = '"+ adicionalList.get(i).getBitacora() +"' and sync ='0'");
                            if(equipoRecoleccions.size()>0)
                            {
                                for(int y=0; y<=equipoRecoleccions.size()-1; y++) {
                                    JSONObject jsonEquiposDeRecoleccion = new JSONObject();
                                    jsonEquiposDeRecoleccion.put("nombre", equipoRecoleccions.get(y).getNombre());
                                    jsonEquiposDeRecoleccion.put("serie", equipoRecoleccions.get(y).getSerie());
                                    jsonEquiposDeRecoleccion.put("fecha", equipoRecoleccions.get(y).getFecha());
                                    jsonEquiposDeRecoleccion.put("latitud", equipoRecoleccions.get(y).getLatitud());
                                    jsonEquiposDeRecoleccion.put("longitud", equipoRecoleccions.get(y).getLongitud());
                                    jsonEquiposDeRecoleccion.put("isBunker", equipoRecoleccions.get(y).getIsBunker());
                                    jsonEquiposDeRecoleccion.put("isProveedor", Preferences.getIsProveedor(sContext, Preferences.PREFERENCE_IS_PROVEEDOR) ? "1" : "0");
                                    jsonEquiposDeRecoleccion.put("usuario", equipoRecoleccions.get(y).getUsuario());
                                    jsonEquiposRecoleccion.put(jsonEquiposDeRecoleccion);
                                }
                                jsonInformacionAdicionalBitacora.put("Equipos_recoleccion", jsonEquiposRecoleccion);
                            }
                            Log.d(TAG, "createJsonForSync: No hay datos de equipo de recoleccion");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }




                        //FALRA EQUIPOS DE TRASLADO
                        try{
                            List<EquipoTraslado> equipoTraslados = EquipoTraslado.findWithQuery(EquipoTraslado.class, "SELECT * FROM EQUIPO_TRASLADO where bitacora = '"+ adicionalList.get(i).getBitacora() +"' and sync ='0'");
                            if(equipoTraslados.size()>0)
                            {
                                for(int y=0; y<=equipoTraslados.size()-1; y++) {
                                    JSONObject jsonEquiposDeTraslado = new JSONObject();
                                    jsonEquiposDeTraslado.put("nombre", equipoTraslados.get(y).getNombre());
                                    jsonEquiposDeTraslado.put("serie", equipoTraslados.get(y).getSerie());
                                    jsonEquiposDeTraslado.put("fecha", equipoTraslados.get(y).getFecha());
                                    jsonEquiposDeTraslado.put("latitud", equipoTraslados.get(y).getLatitud());
                                    jsonEquiposDeTraslado.put("longitud", equipoTraslados.get(y).getLongitud());
                                    jsonEquiposDeTraslado.put("isBunker", equipoTraslados.get(y).getIsBunker());
                                    jsonEquiposDeTraslado.put("isProveedor", Preferences.getIsProveedor(sContext, Preferences.PREFERENCE_IS_PROVEEDOR) ? "1" : "0");
                                    jsonEquiposDeTraslado.put("usuario", equipoTraslados.get(y).getUsuario());
                                    jsonEquiposTraslado.put(jsonEquiposDeTraslado);
                                }
                                jsonInformacionAdicionalBitacora.put("Equipos_traslado", jsonEquiposTraslado);
                            }
                            Log.d(TAG, "createJsonForSync: No hay datos de equipo de traslado");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }





                        //ATAUDES************************
                        /*try{
                            String queryFromInventario = "SELECT * FROM INVENTARIO where bitacora = '"+ adicionalList.get(i).getBitacora() +"' and sync ='0'";
                            List<Inventario> inventarioList = Inventario.findWithQuery(Inventario.class, queryFromInventario);
                            if(inventarioList.size()>0)
                            {
                                for(int y=0; y<=inventarioList.size()-1; y++) {
                                    JSONObject jsonAtaurnas = new JSONObject();
                                    jsonAtaurnas.put("codigo_ataurna", inventarioList.get(y).getCodigo());
                                    jsonAtaurnas.put("item_ataurna", inventarioList.get(y).getDescripcion());
                                    jsonAtaurnas.put("serie_ataurna", inventarioList.get(y).getSerie());
                                    jsonAtaurnas.put("proveedor_ataurna", inventarioList.get(y).getProveedor());
                                    jsonAtaurnas.put("fecha_ataurna", inventarioList.get(y).getFecha());
                                    jsonAtaudesAndUrnas.put(jsonAtaurnas);
                                }
                                jsonInformacionAdicionalBitacora.put("Ataurnas", jsonAtaudesAndUrnas);
                            }
                            Log.d(TAG, "createJsonForSync: Mo hay datos de inventario");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }*/
                        //********************************


                        //Eliminados************************
                        /*try{
                            String queryFromCancelados = "SELECT * FROM CANCELADOS where bitacora = '"+ adicionalList.get(i).getBitacora() +"' and sync ='0'";
                            List<Cancelados> canceladosList = Cancelados.findWithQuery(Cancelados.class, queryFromCancelados);
                            if(canceladosList.size()>0)
                            {
                                for(int y=0; y<=canceladosList.size()-1; y++) {
                                    JSONObject jsonAtaurnasCanceladas = new JSONObject();
                                    jsonAtaurnasCanceladas.put("codigo_ataurna", canceladosList.get(y).getCodigo());
                                    jsonAtaurnasCanceladas.put("item_ataurna", canceladosList.get(y).getDescripcion());
                                    jsonAtaurnasCanceladas.put("serie_ataurna", canceladosList.get(y).getSerie());
                                    jsonAtaurnasCanceladas.put("proveedor_ataurna", canceladosList.get(y).getProveedor());
                                    jsonAtaurnasCanceladas.put("fecha_ataurna", canceladosList.get(y).getFecha());
                                    jsonAtaudesAndUrnasEliminadas.put(jsonAtaurnasCanceladas);
                                }
                                jsonInformacionAdicionalBitacora.put("Ataurnas_eliminadas", jsonAtaudesAndUrnasEliminadas);
                            }
                            else
                                Log.d(TAG, "createJsonForSync: No hay datos de Ataudes y urnas canceladas");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }*/

                        requestToSyncInfoAdicionalPorBitacora(jsonInformacionAdicionalBitacora);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                }
            }
            else
                Log.i(TAG, "NO hay registros de Ubicaciones");
            //***********************







            //*************** ATAUDES y URNAS AGREGADAS Y ELIMINADAS********************
            try{
                JSONObject jsonInformacionAdicionalBitacora = new JSONObject();
                JSONArray jsonAtaudesAndUrnas = new JSONArray();
                JSONArray jsonAtaudesAndUrnasEliminadas = new JSONArray();

                //ATAUDES************************
                try{
                    String queryFromInventario = "SELECT * FROM INVENTARIO where sync ='0'";
                    List<Inventario> inventarioList = Inventario.findWithQuery(Inventario.class, queryFromInventario);
                    if(inventarioList.size()>0)
                    {
                        for(int y=0; y<=inventarioList.size()-1; y++) {
                            JSONObject jsonAtaurnas = new JSONObject();
                            jsonAtaurnas.put("codigo_ataurna", inventarioList.get(y).getCodigo());
                            jsonAtaurnas.put("item_ataurna", inventarioList.get(y).getDescripcion());
                            jsonAtaurnas.put("serie_ataurna", inventarioList.get(y).getSerie());
                            jsonAtaurnas.put("proveedor_ataurna", inventarioList.get(y).getProveedor());
                            jsonAtaurnas.put("fecha_ataurna", inventarioList.get(y).getFecha().replace("/", "-"));
                            jsonAtaurnas.put("bitacora", inventarioList.get(y).getBitacora());
                            jsonAtaurnas.put("fecha_captura", inventarioList.get(y).getCapturado());
                            jsonAtaurnas.put("latitud", inventarioList.get(y).getLatitud());
                            jsonAtaurnas.put("longitud", inventarioList.get(y).getLongitud());
                            jsonAtaurnas.put("isProveedor", Preferences.getIsProveedor(sContext, Preferences.PREFERENCE_IS_PROVEEDOR) ? "1" : "0");
                            jsonAtaurnas.put("usuario", DatabaseAssistant.getUserNameFromSesiones());
                            jsonAtaudesAndUrnas.put(jsonAtaurnas);
                        }
                        jsonInformacionAdicionalBitacora.put("Ataurnas_registradas", jsonAtaudesAndUrnas);
                    }
                    else
                        Log.d(TAG, "onResponse: No se hay datos de ataudes y urnas");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                //********************************


                //Eliminados************************
                try{
                    String queryFromCancelados = "SELECT * FROM CANCELADOS where sync ='0'";
                    List<Cancelados> canceladosList = Cancelados.findWithQuery(Cancelados.class, queryFromCancelados);
                    if(canceladosList.size()>0)
                    {
                        for(int y=0; y<=canceladosList.size()-1; y++) {
                            JSONObject jsonAtaurnasCanceladas = new JSONObject();
                            jsonAtaurnasCanceladas.put("codigo_ataurna", canceladosList.get(y).getCodigo());
                            jsonAtaurnasCanceladas.put("item_ataurna", canceladosList.get(y).getDescripcion());
                            jsonAtaurnasCanceladas.put("serie_ataurna", canceladosList.get(y).getSerie());
                            jsonAtaurnasCanceladas.put("proveedor_ataurna", canceladosList.get(y).getProveedor());
                            jsonAtaurnasCanceladas.put("fecha_ataurna", canceladosList.get(y).getFecha().replace("/", "-"));
                            jsonAtaurnasCanceladas.put("bitacora", canceladosList.get(y).getBitacora());
                            jsonAtaurnasCanceladas.put("fecha_captura", canceladosList.get(y).getCapturado());
                            jsonAtaudesAndUrnasEliminadas.put(jsonAtaurnasCanceladas);
                        }
                        jsonInformacionAdicionalBitacora.put("Ataurnas_eliminadas", jsonAtaudesAndUrnasEliminadas);
                    }
                    else
                        Log.d(TAG, "onResponse: No se hay datos de Eliminados");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                //********************************
                if(jsonInformacionAdicionalBitacora.length()>0)
                    requestToSyncAtaurnas(jsonInformacionAdicionalBitacora);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }

            //***********************
        }catch (Throwable e){
            Log.e(TAG, "createJsonEvents: Error");
        }
    }


    public static void requestToSyncInfoAdicionalPorBitacora(JSONObject jsonParams)
    {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_SAVE_INFORMATION_ADICIONAL_POR_BITACORA, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("Success")) {
                                Adicional.executeQuery("UPDATE ADICIONAL set sync = '1' WHERE bitacora = '" + jsonParams.getString("bitacora") + "'");
                                Documentos.executeQuery("UPDATE DOCUMENTOS set sync = '1' WHERE bitacora = '" + jsonParams.getString("bitacora") + "'");
                                Equipoinstalacion.executeQuery("UPDATE EQUIPOINSTALACION set sync = '1' WHERE bitacora = '" + jsonParams.getString("bitacora") + "'");
                                Equipocortejo.executeQuery("UPDATE EQUIPOCORTEJO set sync = '1' WHERE bitacora = '" + jsonParams.getString("bitacora") + "'");
                                Equipocortejo.executeQuery("UPDATE EQUIPO_RECOLECCION set sync = '1' WHERE bitacora = '" + jsonParams.getString("bitacora") + "'");
                                Equipocortejo.executeQuery("UPDATE EQUIPO_TRASLADO set sync = '1' WHERE bitacora = '" + jsonParams.getString("bitacora") + "'");
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
        postRequest.setRetryPolicy(new DefaultRetryPolicy(80000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(sContext).addToRequestQueue(postRequest);
    }


    public static void requestToSyncAtaurnas(JSONObject jsonParams)
    {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_SAVE_ATAURNAS, jsonParams,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("Success"))
                            {
                                JSONObject jsonResult = new JSONObject(response.getString("Result"));
                                
                                if(jsonResult.length()>0) {

                                    if (jsonResult.has("Ataurnas_registradas")) {
                                        JSONArray arrayAtaurnasRegistradas = new JSONArray();
                                        arrayAtaurnasRegistradas = jsonResult.getJSONArray("Ataurnas_registradas");

                                        for (int i = 0; i <= arrayAtaurnasRegistradas.length() - 1; i++) {
                                            Inventario.executeQuery("UPDATE INVENTARIO set sync = '1' WHERE codigo = '" + arrayAtaurnasRegistradas.getJSONObject(i).getString("codigo_ataurna") + "'");
                                            Log.d(TAG, "onResponse: Inventario actualizado correctamente: " + arrayAtaurnasRegistradas.getJSONObject(i).getString("codigo_ataurna"));
                                        }
                                    }

                                    if (jsonResult.has("Ataurnas_eliminadas")) {
                                        JSONArray arrayAtaurnasEliminadas = new JSONArray();
                                        arrayAtaurnasEliminadas = jsonResult.getJSONArray("Ataurnas_eliminadas");
                                        for (int i = 0; i <= arrayAtaurnasEliminadas.length() - 1; i++) {
                                            Inventario.executeQuery("UPDATE CANCELADOS set sync = '1' WHERE codigo = '" + arrayAtaurnasEliminadas.getJSONObject(i).getString("codigo_ataurna") + "'");
                                            Log.d(TAG, "onResponse: Cancelados actualizados correctamente: " + arrayAtaurnasEliminadas.getJSONObject(i).getString("codigo_ataurna"));
                                        }
                                    }
                                }else
                                    Log.d(TAG, "onResponse: No se hay datos de ataudes y urnas");
                            } else
                                Log.d("LOCATION", response.getString("error"));

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
        postRequest.setRetryPolicy(new DefaultRetryPolicy(80000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(sContext).addToRequestQueue(postRequest);
    }


    public static void requestLoginOnline(JSONObject jsonParams)
    {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_LOGIN, jsonParams,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("Success")) {

                                if(response.has("login")){
                                    Sesiones.executeQuery("UPDATE SESIONES SET statos ='1' WHERE latitud='" + response.getJSONObject("login").getString("lat") + "' AND longitud ='"+ response.getJSONObject("login").getString("lng") + "' AND fecha ='"+ response.getJSONObject("login").getString("fecha") +"' AND hora ='"+ response.getJSONObject("login").getString("hora") +"'");
                                    Log.d("SESIONES", "onResponse: Actualizado 1 registro de sesiones.");
                                }
                                //String query ="UPDATE SESIONES set statos = '1' WHERE id = '" + jsonParams.getString("id_sync") + "' and fecha ='" + jsonParams.getString("fecha") +"' and hora = '" + jsonParams.getString("hora") +"'";
                                //Sesiones.executeQuery(query);
                            }

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
                                } catch (Throwable e) {
                                    Log.e("SESIONES", "createJsonParametersForLoginOnline: " + e.getMessage());
                                }

                            } else {
                                Log.e("SESIONES", "run: no es success");
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
        postRequest.setRetryPolicy(new DefaultRetryPolicy(80000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(sContext).addToRequestQueue(postRequest);
    }




    public static void requestdoCheckINAndCheckOut(JSONObject jsonParams)
    {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_CHECKINCHECKOUT_NEW, jsonParams,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("Success")) {
                                String query ="UPDATE ASISTENCIA set statos = '1' WHERE id = '" + jsonParams.getString("id_sync") + "' and fecha ='" + jsonParams.getString("fecha") +"' and hora = '" + jsonParams.getString("hora") +"'";
                                Asistencia.executeQuery(query);
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
        postRequest.setRetryPolicy(new DefaultRetryPolicy(80000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(sContext).addToRequestQueue(postRequest);
    }






    public static void requestSaveCommentariosToServer(JSONObject jsonParams)
    {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_SAVE_COMENTARIOS, jsonParams,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(!response.getBoolean("error"))
                            {
                                JSONArray arrayResult = new JSONArray();
                                arrayResult = response.getJSONArray("result");
                                for(int i =0; i<=arrayResult.length()-1;i++){
                                    String query ="UPDATE COMENTARIOS set sync = '1' WHERE bitacora = '" + arrayResult.getJSONObject(i).getString("bitacora") + "' and fecha ='" + arrayResult.getJSONObject(i).getString("fecha_captura") +"'";
                                    Comentarios.executeQuery(query);
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
        postRequest.setRetryPolicy(new DefaultRetryPolicy(80000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(sContext).addToRequestQueue(postRequest);
    }

    /*public static void requestSyncronizationArticlesInventory(JSONObject jsonParams)
    {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_SAVE_ARTICLES_INVENTORY, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("Success"))
                            {
                                String query ="UPDATE ARTICULOSCAN set sync = '1' WHERE id = '" + jsonParams.getString("fecha") + "'";
                                Articuloscan.executeQuery(query);
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
        postRequest.setRetryPolicy(new DefaultRetryPolicy(80000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(sContext).addToRequestQueue(postRequest);
    }*/

    public static void cargaDeDatosAServidor(JSONObject jsonParams)
    {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_URL, jsonParams, new Response.Listener<JSONObject>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(JSONObject response)
            {
                try{
                    if(response.getBoolean("Success"))
                    {
                        String query ="UPDATE EVENTOS set estatus = '1' WHERE id = '" + jsonParams.getString("evento_id") + "'";
                        Eventos.executeQuery(query);

                        if(response.has("query")) {
                            try {
                                String queryJSON = response.getString("query");
                                Log.i("ServerQuery", "from ApplicationResourcesProvider " + queryJSON);
                                if (!queryJSON.equals("") && !queryJSON.equals("null")) {
                                    SugarRecord.executeQuery(queryJSON);
                                    Log.d("SESIONES", "onResponse: Query ejecutado correctamente.");
                                    response.remove("query");
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        else {
                            Log.d("RESPUESTA", "JSON Query no contiene datos");
                        }

                    }
                    else
                        Log.w(TAG, "Carga de datos, storeBitacora No se registro");
                }catch (JSONException e){
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
        postRequest.setRetryPolicy(new DefaultRetryPolicy(80000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(sContext).addToRequestQueue(postRequest);
    }


    public static void cargaDeUbicacionesToServer(JSONObject jsonParams)
    {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.WS_LOCATION_URL,
                jsonParams, new Response.Listener<JSONObject>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(JSONObject response)
            {
                try{
                    if(response.getBoolean("Success")) {

                        String query ="DELETE FROM UBICACIONES WHERE id = '" + jsonParams.getString("evento_id") + "'";
                        Ubicaciones.executeQuery(query);
                        Log.v(TAG, "Carga de ubicaciones, storePosition registrado correctamente");
                    }
                    else
                        Log.w(TAG, "Carga de datos, No se registro");


                    if(response.has("query")) {
                        try {
                            String queryJSON = response.getString("query");
                            Log.i("ServerQuery", "from ApplicationResourcesProvider " + queryJSON);
                            if (!queryJSON.equals("") && !queryJSON.equals("null")) {
                                SugarRecord.executeQuery(queryJSON);
                                Log.d("SESIONES", "onResponse: Query ejecutado correctamente.");
                                response.remove("query");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Log.e("SESIONES", "onResponse: Error en Server query store position" + ex.getMessage() );
                        }
                    }
                    else {
                        Log.d("RESPUESTA", "JSON Query no contiene datos");
                    }



                }catch (JSONException e){
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
        postRequest.setRetryPolicy(new DefaultRetryPolicy(80000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(sContext).addToRequestQueue(postRequest);
    }




    public static void cargaDeMovimientosToServer(JSONObject jsonParams)
    {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ConstantsBitacoras.URL_SAVE_MOVEMENTS,
                jsonParams, new Response.Listener<JSONObject>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(JSONObject response)
            {
                try{
                    if(response.getString("success").length()>0)
                    {
                        String deleteMov = "DELETE FROM MOVIMIENTOS WHERE id = '" + jsonParams.getString("id_list") + "'";
                        Movimientos.executeQuery(deleteMov);

                        Log.v(TAG, "Carga de Movimientos, saveMovements registrado correctamente");
                    }
                    else
                        Log.w(TAG, "Carga de Movimientos, No se registro");
                }catch (JSONException e){
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
        postRequest.setRetryPolicy(new DefaultRetryPolicy(80000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(sContext).addToRequestQueue(postRequest);
    }

    
    public void timerCallLocations() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("LongLogTag")
            public void run() {
                timerCallLocations();
                updateLocation();
            }
        }, 60000);
    }

    public void timerForInsertLocations() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("LongLogTag")
            public void run() {
                timerForInsertLocations();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat horaFormat;
                horaFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
                DatabaseAssistant.insertarUbicaciones(
                        String.valueOf(latitud),
                        String.valueOf(longitud),
                        "" + dateFormatter.format(date = new Date()),
                        "" + horaFormat.format(cal.getTime())
                );
                Log.d(TAG, "Ubicación insertada: " + latitud + ", " + longitud);
            }
        }, 17000);
    }


    public void timerForSyncData() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                timerForSyncData();
                if(checkInternetConnection()) {
                    createJsonForSync();
                    downloadCatalogoArticulos();
                }
            }
        }, 120000);
    }


    private void updateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
        getCurrentLocation();
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    latitud = location.getLatitude();
                    longitud = location.getLongitude();
                }
            }
        });

    }

    private PendingIntent getPendingIntent(){
        Intent intent = new Intent(this, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(8000);
        locationRequest.setSmallestDisplacement(10f);
    }

    @SuppressLint("LongLogTag")
    public static String[] getCoordenadasFromApplication()
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] coordenadas = new String[3];

        try {
            coordenadas[0]= String.valueOf(latitud);
            coordenadas[1]= String.valueOf(longitud);
            coordenadas[2]= String.valueOf(dateFormatter.format(date = new Date()));
        }catch (Throwable e) {
            Log.e(TAG, "Error en Localizaciones getCoordenadasFromApplication()" + e.getMessage().toString());
        }
        return coordenadas;
    }

    @SuppressLint("LongLogTag")
    public static void updateCoordenadasLocation(Location loc){
        try {
            latitud = loc.getLatitude();
            longitud = loc.getLongitude();
            timeLocation = String.valueOf(loc.getTime());
            Log.d(TAG, "updateCoordenadasLocation: " + latitud + ", " + longitud);
        } catch (Throwable e) {
            Log.e(TAG, "--> No se obtuvo nueva ubicación" + e.getMessage());
        }
    }



    public static boolean checkInternetConnection()
    {
        ConnectivityManager con = (ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    @SuppressLint("HardwareIds")
    public static void insertarMovimiento(String driver, String ayudante, String movimiento) {
        String deviceID="", appVersion = "", androidModel = "", imei = "", fecha = "";
        deviceID = Settings.Secure.getString(sContext.getContentResolver(), Settings.Secure.ANDROID_ID);

        PackageInfo pInfo;
        try {
            pInfo = sContext.getPackageManager().getPackageInfo(sContext.getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        androidModel = Build.MODEL;

        try {
            TelephonyManager manager = (TelephonyManager) sContext.getSystemService(Context.TELEPHONY_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                if (ActivityCompat.checkSelfPermission(sContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    imei = manager.getImei();
                    return;
                } else
                    imei = manager.getDeviceId();

        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        fecha = dateFormat.format(date);
        DatabaseAssistant.insertarMovimientos(driver, ayudante, deviceID, appVersion, androidModel, imei, fecha, movimiento);
    }


}
