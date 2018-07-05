package com.example.song.pet;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

@SuppressLint("OverrideAbstract")
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

public class WeChatNotificationListenerService extends NotificationListenerService {
    public static final String label = "WeChatNotificationService";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        if(!sbn.getPackageName().equals("com.tencent.mm")) return;

        Bundle bundle = new Bundle();
        bundle.putString("name", sbn.getNotification().extras.getString(Notification.EXTRA_TITLE));
        bundle.putString("text", sbn.getNotification().extras.getString(Notification.EXTRA_TEXT));

        Intent intent = new Intent();
        intent.putExtra("from",WeChatNotificationListenerService.label);
        intent.putExtras(bundle);
        intent.setAction("WeChatNotificationListenerService");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,START_STICKY,startId);
    }
}
