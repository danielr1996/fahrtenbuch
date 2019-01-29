package de.danielr1996.fahrtenbuch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ForegroundService extends Service {


    @Override
    public void onCreate() {
        Log.i(ForegroundService.class.getName(), "onCreate");
        NotificationChannel channel = new NotificationChannel("tracking_active", "Tracking Aktiv", NotificationManager.IMPORTANCE_LOW);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "tracking_active")
                .setContentTitle("Fahrtenbuch")
                .setContentText("Tracking aktiv")
                .setSmallIcon(R.drawable.ic_baseline_map_24px)
                .setAutoCancel(true);
        startForeground(12345678, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(ForegroundService.class.getName(), "onDestroy");
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(ForegroundService.class.getName(), "onStartCommand");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}