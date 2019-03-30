package edu.scse.take;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServiceNotify extends Service {

    Thread notificationThread=new Thread(new Runnable() {
        @Override
        public void run() {
            if(notificationThread.isInterrupted()){
                stopSelf();
            }
        }
    });
    @Override
    public void onCreate() {
        super.onCreate();
        notificationThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationThread.interrupt();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
