package com.example.bitacoras2020.Utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Azael Jimenez  on 13/11/2020.
 */

public class VolleySingleton {

    private static com.example.bitacoras2020.Utils.VolleySingleton intanciaVolley;
    private RequestQueue request;
    private static Context contexto;

    private VolleySingleton(Context context) {
        contexto = context;
        request = getRequestQueue();
    }

    public static synchronized com.example.bitacoras2020.Utils.VolleySingleton getIntanciaVolley(Context context) {
        if (intanciaVolley == null) {
            intanciaVolley = new com.example.bitacoras2020.Utils.VolleySingleton(context);
        }

        return intanciaVolley;
    }

    public RequestQueue getRequestQueue() {
        if (request == null) {
            request = Volley.newRequestQueue(contexto.getApplicationContext());
        }

        return request;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

}
