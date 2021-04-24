package com.example.bitacoras2020.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.bitacoras2020.R;


public class ForegroundService extends Service
{
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("BITACORAS", "foregound service running");

        try {
            String input = intent.getStringExtra("inputExtra");
            if (input.equals(Constants.ACTION.STARTFOREGROUND_ACTION))
            {
                createNotificationChannel();

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Latinoamericana Recinto Funeral")
                        .setContentText("eBita")
                        .setSmallIcon(R.drawable.ic_ok)
                        .build();

                startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

            } else if (input.equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
                stopForeground(true);
                stopSelf();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return START_STICKY;
    }

    private void createNotificationChannel() {
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel serviceChannel = new NotificationChannel(
                        CHANNEL_ID,
                        "Foreground Service Channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                );

                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(serviceChannel);
            }
        }catch (Throwable e)
        {
            Log.i("NOTI_SOCIAL", "createNotificationChannel()");
        }
    }

    private void createNotificcationChannel() {
        /**Apartir de Android 8 no se podran mandar mas de 1 notificacion del mismo tipo
         * Para mas información acerca de notificaciones Android 8 y posteriores visita
         * https://developer.android.com/training/notify-user/build-notification
         */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("PABS_NOTIFICATIONS", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}