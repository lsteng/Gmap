package com.example.reson.gmap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.Date;

public class MyService extends Service {
    private Handler handler = new Handler();
    private LocationUpdate locationUpdate;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
//        handler.postDelayed(showTime, 1000);
        locationUpdate = new LocationUpdate(getApplicationContext());
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
//        handler.removeCallbacks(showTime);
        locationUpdate.stopService();
        super.onDestroy();
    }

    private Runnable showTime = new Runnable() {
        public void run() {
            //log目前時間
//            Log.i("time:", new Date().toString());
            String msg = new Date().toString();
            notifyMSG(msg);
            handler.postDelayed(this, 1000);
        }
    };

    private void notifyMSG(String text){
        final int notifyID = 1; // 通知的識別號碼
        final boolean autoCancel = true; // 點擊通知後是否要自動移除掉通知

        final int requestCode = notifyID; // PendingIntent的Request Code
//        final Intent intent = getIntent(); // 目前Activity的Intent
//        final Intent intent = getApplicationContext(); // 目前Activity的Intent

//        final int flags = PendingIntent.FLAG_CANCEL_CURRENT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
//        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent,
//                PendingIntent.FLAG_CANCEL_CURRENT // 先前無效 新的有效
//                //	  PendingIntent.FLAG_UPDATE_CURRENT // 先前的更新為最新的
//                //	  PendingIntent.FLAG_ONE_SHOT       // 不論先後 點擊後內容為最先的 且只秀一次
//                //	  PendingIntent.FLAG_ONE_SHOT
//        ); // 取得PendingIntent

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        final Notification notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("log目前時間")
                .setContentText(text)
//                .setContentIntent(pendingIntent)
                .setAutoCancel(autoCancel)
//                .build(); // 建立通知
                .getNotification();
        notificationManager.notify(notifyID, notification); // 發送通知
    }
}
