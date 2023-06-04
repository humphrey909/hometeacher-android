package com.example.hometeacher;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.hometeacher.shared.Session;

import java.util.ArrayList;

public class RestartService extends Service {
    com.example.hometeacher.shared.GlobalClass GlobalClass; //글로벌 클래스

    Intent serviceintent;
    //public static Socket socket;
    //public SocketService mService;
    public boolean mBound = false;

    Session oSession; //자동로그인을 위한 db
    ArrayList<ArrayList<String>> Sessionlist;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GlobalClass = (com.example.hometeacher.shared.GlobalClass)getApplication();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = builder.build();
        startForeground(9, notification);

        serviceintent =  new Intent(getApplicationContext(), SocketService.class);
        startService(serviceintent);
        //oSession = new Session(this);
        //자동 로그인 하기
        //Sessionlist = oSession.Getoneinfo("0");
        //ArrayList<ArrayList<String>> Sessionlist = oSession.Getoneinfo("0");



        //startService(serviceintent);
        stopForeground(true);
        stopSelf();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
