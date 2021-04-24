package com.example.bitacoras2020.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by luki2 on 18/06/2018.
 */

public class Preferences
{
    public static final String STRING_PREFERENCES="bitacoras.latino";
    public static final String PREFERENCE_REQUEST_PERMISSIONS_GRANTED="estado.button.sesion";
    public static final String PREFERENCE_CHECKIN_CHECKOUT_ASSISTANT="asistencia.checkin.checkout";
    public static final String PREFERENCE_WITHIN_THE_ZONE_TO_LOGIN="asistencia.checkin.checkout.login";
    public static final String PREFERENCE_END_SESION_THE_ZONE_TO_LOGIN="asistencia.checkin.checkout.endlogin";
    public static final String PREFERENCE_NOMBRE_CHOFER="asistencia.checkin.nombre";
    public static final String PREFERENCE_GEOFENCE_ACTUAL="geofence.actual";
    public static final String PREFERENCE_BITACORA_NOTIFICATION_PUSH="notification.push.bitacora";
    public static final String PREFERENCE_IS_PROVEEDOR="is.proveedor";
    public static final String PREFERENCE_ACTIVIDAD_INICIADA="actividad.iniciada";
    public static final String PREFERENCE_TUTORIAL_HOME="TUTORIAL_HOME";
    public static final String PREFERENCE_TUTORIAL_DETALLES="TUTORIAL_DETAILS";
    public static final String PREFERENCE_ISBUNKER="is.bunker";
    public static final String PREFERENCE_ASISTENCIA_ENTRADA="asistencia.entrada";


    public static void setPreferencesAsistenciaEntrada(Context c, boolean bandera, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putBoolean(key, bandera).apply();
    }

    public static boolean getPreferencesAsistenciaEntrada(Context c, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getBoolean(key, false  );
    }


    public static void setPreferenceIsbunker(Context c, boolean bandera, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putBoolean(key, bandera).apply();
    }

    public static boolean getPreferenceIsbunker(Context c, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getBoolean(key, false  );
    }


    public static void setPreferencePermissionsBoolean(Context c, boolean bandera, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putBoolean(key, bandera).apply();
    }

    public static boolean getPreferencePermissionsBoolean(Context c, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getBoolean(key, false  );
    }

    public static void setPreferenceTutorialHome(Context c, boolean bandera, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putBoolean(key, bandera).apply();
    }

    public static boolean getPreferenceTutorialHome(Context c, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getBoolean(key, false  );
    }


    public static void setPreferenceTutorialDetails(Context c, boolean bandera, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putBoolean(key, bandera).apply();
    }

    public static boolean getPreferenceTutorialDetails(Context c, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getBoolean(key, false  );
    }




    public static void setPreferenceCheckinCheckoutAssistant(Context c, boolean check, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putBoolean(key, check).apply();
    }

    public static boolean getPreferenceCheckinCheckoutAssistant(Context c, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getBoolean(key, false  );
    }

    public static void setWithinTheZone(Context c, boolean bandera, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putBoolean(key, bandera).apply();
    }

    public static boolean getWithinTheZone(Context c, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getBoolean(key, false  );
    }

    public static void setEndLoginTheZone(Context c, boolean bandera, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putBoolean(key, bandera).apply();
    }

    public static boolean getEndLoginTheZone(Context c, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getBoolean(key, false  );
    }

    public static void setNombreChoferInLogin(Context c, String nombre, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putString(key, nombre).apply();
    }

    public static String getNombreChoferInLogin(Context c, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getString(key, ""  );
    }




    public static void setGeofenceActual(Context c, String nombre, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putString(key, nombre).apply();
    }

    public static String getGeofenceActual(Context c, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getString(key, ""  );
    }


    public static void setBitacoraNotificationPush(Context c, String nombre, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putString(key, nombre).apply();
    }

    public static String getBitacoraNotificationPush(Context c, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getString(key, ""  );
    }

    public static void setIsProveedor(Context c, boolean bandera, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putBoolean(key, bandera).apply();
    }

    public static boolean getIsProveedor(Context c, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getBoolean(key, false  );
    }

    public static void setActividadYaIniciada(Context c, boolean bandera, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        preferences.edit().putBoolean(key, bandera).apply();
    }

    public static boolean getActividadYaIniciada(Context c, String key)
    {
        SharedPreferences preferences= c.getSharedPreferences(STRING_PREFERENCES, c.MODE_PRIVATE);
        return preferences.getBoolean(key, false  );
    }
}
