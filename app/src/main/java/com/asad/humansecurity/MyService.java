package com.asad.humansecurity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  Notification myforgroundservice(){
        Intent intent = new Intent(this,MyService.class);

        NotificationChannel channel=new NotificationChannel("sha", "sh", NotificationManager.IMPORTANCE_HIGH);

        NotificationManager string=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        string.createNotificationChannel(channel);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"shahin")
                .setContentText("SHhain").setContentTitle("sjasjdhja").setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis());
        builder.setChannelId("sha");
        return builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10000;i++){
                    System.out.println(String.valueOf(i));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        }).start();

        startForeground(1, myforgroundservice());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
