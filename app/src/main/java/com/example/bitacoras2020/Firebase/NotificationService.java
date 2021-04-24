package com.example.bitacoras2020.Firebase;

import android.annotation.SuppressLint;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.application.isradeleon.notify.Notify;
import com.example.bitacoras2020.Activities.NuevaBitacora;
import com.example.bitacoras2020.Database.DatabaseAssistant;
import com.example.bitacoras2020.MainActivity;
import com.example.bitacoras2020.R;
import com.example.bitacoras2020.Utils.NotificationHelper;
import com.example.bitacoras2020.Utils.Preferences;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NotificationService extends FirebaseMessagingService {

    String TAG="NOTIFY";
    boolean notificacionGuardada = false;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        notificacionGuardada = false;
        String bitacora="", click_action="";
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getNotification() != null) {
                try {
                    if(remoteMessage.getData().get("message") != null) {
                        JSONObject json = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("message")));
                        if(json.has("bitacora")) {
                            bitacora = json.getString("bitacora");
                        }

                        if(json.has("click_action")) {
                            click_action = json.getString("click_action");
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    bitacora = "";
                    click_action ="";
                }
                verNotificacion(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), bitacora, click_action);
            }
        }
    }

    public void verNotificacion(String titulo, String cuerpo, String bitacora, String click_action)
    {
        Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        long[] pattern = { 0, 100, 400, 100, 400, 100, 400, 100, 400, 100, 400, 100, 400, 100, 400, 100, 400, 100, 400, 100, 400, 100, 400, 100, 400, 100, 400, 100, 400};
        vibrator.vibrate(pattern , -1);
        vibrator.vibrate(15000);

        if(click_action.equals("NOTIFICACIONES_LIST")) {
            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
            notificationHelper.showNotificationsActivity(titulo, cuerpo);
            }
        else
        {
            if(!bitacora.equals("")) {
                try {
                    Notify.build(getApplicationContext())
                            .setTitle(titulo)
                            .setContent(cuerpo)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setColor(R.color.red_color)
                            .setImportance(Notify.NotifyImportance.MAX)
                            .enableVibration(true)
                            .setVibrationPattern(pattern)
                            .setLargeIcon(R.drawable.ic_launcher)
                            .largeCircularIcon()
                            .setPicture("http://35.167.149.196/eprobensoTEST/servicio.png")
                            .show();
                }catch (Throwable e){
                    Log.e(TAG, "verNotificacion: " + e.getMessage());
                }

                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                notificationHelper.showNotificationFromPush(titulo, cuerpo, bitacora);
                Preferences.setBitacoraNotificationPush(getApplicationContext(), bitacora, Preferences.PREFERENCE_BITACORA_NOTIFICATION_PUSH);
            }
            else
            {
                try {
                    Notify.build(getApplicationContext())
                            .setTitle(titulo)
                            .setContent(cuerpo)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setImportance(Notify.NotifyImportance.MAX)
                            .enableVibration(true)
                            .setVibrationPattern(pattern)
                            .setLargeIcon(R.drawable.ic_launcher)
                            .largeCircularIcon()
                            .show();
                }catch (Throwable e){
                    Log.e(TAG, "verNotificacion: " + e.getMessage());
                }
            }
        }



        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(!notificacionGuardada) {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    DatabaseAssistant.insertarNotificacion(
                            "" + titulo,
                            "" + cuerpo,
                            "" + click_action,
                            "" + bitacora,
                            "" + dateFormat.format(new Date())
                    );
                    notificacionGuardada = true;
                }
            }
        });

    }

}
