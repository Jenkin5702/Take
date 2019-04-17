package edu.scse.take;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MyLocationData;

public class TakeService extends Service {
    private final IBinder mBinder = new LocalBinder();
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result= (String) msg.obj;
            if(result.equals("YES")){
                sendNoti();
            }
            Log.e("result",result);
        }
    };
    Locations locWhenJoin;
    Thread messageThread=new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                locWhenJoin=DataLoader.locations;
                DataLoader.join(locWhenJoin);
                while(!Thread.interrupted()){
//                    double randomDouble = Math.random();
//                    ThreadRandomServiceDemo.UpdateGUI(randomDouble);
                    Message msg=new Message();
                    msg.obj=DataLoader.newMessageOccurs();
                    handler.sendMessage(msg);
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                DataLoader.leave(locWhenJoin);
            }

        }
    });
    private static final int NOTIFICATION_ID = 1001;
    private void sendNoti() {
        //1、NotificationManager
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        /** 2、Builder->Notification
         *  必要属性有三项
         *  小图标，通过 setSmallIcon() 方法设置
         *  标题，通过 setContentTitle() 方法设置
         *  内容，通过 setContentText() 方法设置*/
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentInfo("Content info")
                .setContentText("你收到了一条委托")//设置通知内容
                .setContentTitle("Take")//设置通知标题
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_notifications_red_24dp)//不能缺少的一个属性
//                .setSubText("Subtext")
//                .setTicker("滚动消息......")
//                .setWhen(System.currentTimeMillis())
        ;//设置通知时间，默认为系统发出通知的时间，通常不用设置

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("001","my_channel",NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            manager.createNotificationChannel(channel);
            builder.setChannelId("001");
        }

        Notification n = builder.build();
        n.flags=Notification.FLAG_ONLY_ALERT_ONCE;
        //3、manager.notify()
        manager.notify(NOTIFICATION_ID,n);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        sendNoti();
        Toast.makeText(this, "已开始接收委托消息", Toast.LENGTH_SHORT).show();
        if (!messageThread.isAlive()){
            messageThread.start();
        }
        DataLoader.receiving=true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "已停止接收委托消息", Toast.LENGTH_SHORT).show();
        messageThread.interrupt();
        DataLoader.receiving=false;
    }

    public class LocalBinder extends Binder {
        TakeService getService() {
            return TakeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }


    public long Add(long a, long b) {
        return a + b;
    }



}
