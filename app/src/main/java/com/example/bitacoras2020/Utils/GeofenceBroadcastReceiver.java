package com.example.bitacoras2020.Utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bitacoras2020.Activities.RequestPermissionManager;
import com.example.bitacoras2020.Database.Activos;
import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.Database.LoginZone;
import com.example.bitacoras2020.MainActivity;
import com.example.bitacoras2020.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG ="GeofenceBroadcastReceiv";
    String lugar="", tipoEvento="", lugarEndSession="";
    SimpleDateFormat dateFormat;
    Dialog dialogoSalidas;
    @SuppressLint("SimpleDateFormat")

    @Override
    public void onReceive(Context context, Intent intent) {
        dialogoSalidas = new Dialog(context);
        NotificationHelper notificationHelper = new NotificationHelper(context);
        GeofencingEvent geofencingEvent = GeofencingEvent .fromIntent(intent);
        if(geofencingEvent.hasError()){
            //Log.d(TAG, "onReceive: Error geoFencingEvent");
            return;
        }
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();

        for(Geofence geofence: geofenceList){
            //Log.d(TAG, "onReceive: " + geofence.getRequestId());
            lugar = geofence.getRequestId();
        }
        int transitionTypes = geofencingEvent.getGeofenceTransition();

        //puede ser la de check out o la que tenemos en check out service

        List<LoginZone> lista = LoginZone.findWithQuery(LoginZone.class, "SELECT * FROM LOGIN_ZONE");
        if (lista.size() > 0) {
             lugarEndSession = lista.get(0).getEnd_session_place();
        }
//Preferences.setPreferencePermissionsBoolean(RequestPermissionManager.this, true, Preferences.PREFERENCE_REQUEST_PERMISSIONS_GRANTED);
        switch (transitionTypes){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Preferences.setGeofenceActual(context, lugar, Preferences.PREFERENCE_GEOFENCE_ACTUAL);
                Log.i(TAG, "onReceive: Se guardo en GeofenceActual el lugar: " + lugar);

                if(lugar.equals("Bunker")){
                    Preferences.setPreferenceIsbunker(context, true, Preferences.PREFERENCE_ISBUNKER);
                }else
                    Preferences.setPreferenceIsbunker(context, false, Preferences.PREFERENCE_ISBUNKER);


                if(lugar.equals("LOGIN_ZONE")){
                    Preferences.setWithinTheZone(context, true, Preferences.PREFERENCE_WITHIN_THE_ZONE_TO_LOGIN);
                    notificationHelper.sendHighPriorityNotification("Actividad", "Entrada a "+ lugar);
                }
                else if(lugar.equals("CHECK_OUT_LOGIN_ZONE") || lugar.equals(lugarEndSession)){
                    Preferences.setEndLoginTheZone(context, true, Preferences.PREFERENCE_END_SESION_THE_ZONE_TO_LOGIN);
                    notificationHelper.sendHighPriorityNotification("Actividad", "Entrada a "+ lugar);
                    tipoEvento = "Llegada";
                }
                else{
                    notificationHelper.sendHighPriorityNotification("Actividad", "Entrada a "+ lugar);
                    tipoEvento = "Llegada";
                    registrarEvento("" +lugar, 2 , context);
                }
                Preferences.setSalidaAutomatica(context, false, Preferences.PREFERENCE_AUTOMATICA);
                break;

            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Preferences.setGeofenceActual(context, lugar, Preferences.PREFERENCE_GEOFENCE_ACTUAL);
                Log.i(TAG, "onReceive: Se guardo en GeofenceActual el lugar: " + lugar);

                if(lugar.equals("Bunker")){
                    Preferences.setPreferenceIsbunker(context, true, Preferences.PREFERENCE_ISBUNKER);
                }else
                    Preferences.setPreferenceIsbunker(context, false, Preferences.PREFERENCE_ISBUNKER);

                if(lugar.equals("LOGIN_ZONE")){
                    Preferences.setWithinTheZone(context, true, Preferences.PREFERENCE_WITHIN_THE_ZONE_TO_LOGIN);
                    notificationHelper.sendHighPriorityNotification("Actividad", "Dentro de "+ lugar);
                }
                else if(lugar.equals("CHECK_OUT_LOGIN_ZONE") || lugar.equals(lugarEndSession)){
                    Preferences.setEndLoginTheZone(context, true, Preferences.PREFERENCE_END_SESION_THE_ZONE_TO_LOGIN);
                    notificationHelper.sendHighPriorityNotification("Actividad", "Dentro de "+ lugar);
                }
                else{
                    notificationHelper.sendHighPriorityNotification("Actividad", "Dentro de "+ lugar);
                    tipoEvento = "Dentro";
                }

                Preferences.setSalidaAutomatica(context, false, Preferences.PREFERENCE_AUTOMATICA);
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Preferences.setGeofenceActual(context, "", Preferences.PREFERENCE_GEOFENCE_ACTUAL);
                Log.i(TAG, "onReceive: Se borro la GeofenceActual al salir de: " + lugar);

                Preferences.setPreferenceIsbunker(context, false, Preferences.PREFERENCE_ISBUNKER);
                Preferences.setSalidaAutomatica(context, true, Preferences.PREFERENCE_AUTOMATICA);

                if(lugar.equals("LOGIN_ZONE")){
                    Preferences.setWithinTheZone(context, false, Preferences.PREFERENCE_WITHIN_THE_ZONE_TO_LOGIN);
                    notificationHelper.sendHighPriorityNotification("Actividad", "Salida de "+ lugar);
                   //Preferences.setSalidaAutomatica(context, false, Preferences.PREFERENCE_AUTOMATICA);
                }
                else if(lugar.equals("CHECK_OUT_LOGIN_ZONE") || lugar.equals(lugarEndSession)){
                    Preferences.setEndLoginTheZone(context, false, Preferences.PREFERENCE_END_SESION_THE_ZONE_TO_LOGIN);
                    notificationHelper.sendHighPriorityNotification("Actividad", "Salida de "+ lugar);
                    tipoEvento = "Salida";
                    //Preferences.setSalidaAutomatica(context, false, Preferences.PREFERENCE_AUTOMATICA);
                }
                else{
                    notificationHelper.sendHighPriorityNotification("Actividad", "Salida de "+ lugar);
                    tipoEvento = "Salida";
                    registrarEvento("" +lugar, 1, context );
                    //Preferences.setSalidaAutomatica(context, true, Preferences.PREFERENCE_AUTOMATICA);
                    Log.d(TAG, "onReceive: Registro de salida en: " + lugar);
                }
                break;


        }
    }


    @SuppressLint("SimpleDateFormat")
    void registrarEvento(String lugar, int tipo, Context context) {
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            String hora = horaFormat.format(cal.getTime());
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String[] arregloCoordenadas = ApplicationResourcesProvider.getCoordenadasFromApplication();

            List<Activos> lista = Activos.findWithQuery(Activos.class, "SELECT * FROM ACTIVOS WHERE activo = '1'");
            if (lista.size() > 0) 
            {
                for (int i = 0; i <= lista.size() - 1; i++) 
                {

                    String destino = DatabaseAssistant.getLastDestinoPerBitacora(lista.get(i).getBitacora());

                    if (tipo == 1) { //Salida
                        if (!destino.equals("")) {
                            //si ya tiene destino no agregamos otra salida
                            Log.d(TAG, "registrarEvento: ***SALIDA*** No se agrega otra salida, ya tenemos una salida actualmente: Bitacora " + lista.get(i).getBitacora());
                        } else {

                            String llegada = DatabaseAssistant.getLastLlegadaPerBitacora(lista.get(i).getBitacora());

                            if(llegada.equals(lugar)) {
                                DatabaseAssistant.insertarEventos(
                                        "" + lista.get(i).getBitacora(),
                                        "" + lista.get(i).getChofer(),
                                        "" + lista.get(i).getAyudante(),
                                        "" + lista.get(i).getCarro(),
                                        "" + lugar,
                                        "" + dateFormat.format(new Date()),
                                        "" + hora,
                                        "" + lista.get(i).getDispositivo(),
                                        "" + Double.parseDouble(arregloCoordenadas[0]),
                                        "" + Double.parseDouble(arregloCoordenadas[1]),
                                        "" + lista.get(i).getEstatus(),
                                        "Salida",
                                        "" + lista.get(i).getNombre(),
                                        "" + lista.get(i).getDomicilio(),
                                        "" + lista.get(i).getTelefonos(),
                                        "" + destino,
                                        "1",
                                        "" + lista.get(i).getMovimiento()
                                );
                                Log.w(TAG, "registrarEvento: ***SALIDA*** Regitrado correctamente: Bitacora " + lista.get(i).getBitacora() );
                            }else
                                Log.d(TAG, "registrarEvento: ***SALIDA*** No Coincide la salida de la geozona con la llegada del registro anterior, no se inserta: Bitacora " + lista.get(i).getBitacora());
                        }

                    } else if (tipo == 2) //Llegada
                    {
                        if (destino.equals("")) {
                            // si el destino esta vacio, quire decir que ya tiene una llegada
                            Log.d(TAG, "registrarEvento: ***LLEGADA*** No se agrega otra llegada, ya tenemos una llegada actualmente : Bitacora " + lista.get(i).getBitacora());
                        } else {
                            
                            if(destino.equals(lugar)) {

                                DatabaseAssistant.insertarEventos(
                                        "" + lista.get(i).getBitacora(),
                                        "" + lista.get(i).getChofer(),
                                        "" + lista.get(i).getAyudante(),
                                        "" + lista.get(i).getCarro(),
                                        "" + lugar,
                                        "" + dateFormat.format(new Date()),
                                        "" + hora,
                                        "" + lista.get(i).getDispositivo(),
                                        "" + Double.parseDouble(arregloCoordenadas[0]),
                                        "" + Double.parseDouble(arregloCoordenadas[1]),
                                        "" + lista.get(i).getEstatus(),
                                        "Llegada",
                                        "" + lista.get(i).getNombre(),
                                        "" + lista.get(i).getDomicilio(),
                                        "" + lista.get(i).getTelefonos(),
                                        "",
                                        "1",
                                        "" + lista.get(i).getMovimiento()
                                );
                                Log.w(TAG, "registrarEvento: ***LLEGADA*** Regitrado correctamente: Bitacora " + lista.get(i).getBitacora() );
                            }
                            else
                                Log.d(TAG, "registrarEvento: ***LLEGADA*** El destino registrado no es igual al destino de la geozona, no se inserta: Bitacora " + lista.get(i).getBitacora());
                        }

                    }
                }//End for
            }
            else
                Log.d(TAG, "registrarEvento: No tenemos datos de bitacoras activas");



        } catch (Throwable e) {
            Log.e(TAG, "Error en click de salidas y llegadas: " + e.getMessage());
        }
    }

}