package com.example.song.pet;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    static final int NOTIFICATION_ID = 0x123;
    NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        //收到闹钟Intent,解封数据
        int hour = intent.getIntExtra("hour", 23);
        int minute = intent.getIntExtra("minute", 59);
        String title = intent.getStringExtra("title");

        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

        if (mKeyguardManager != null && mKeyguardManager.inKeyguardRestrictedInputMode()) {
            // keyguard on
            NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //【2】设置通知。PendingIntent表示延后触发，是在用户下来状态栏并点击通知时触发，触发时PendingIntent发送intent
            Intent intent1 = new Intent();
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent1, 0);
            Notification notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setTicker("闹钟提醒")
                    .setContentTitle(title)
                    .setContentText(String.format(Locale.CHINA, "%02d:%02d", hour, minute))
                    .setContentIntent(pi)
                    .build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL; //点击后删除，如果是FLAG_NO_CLEAR则不删除，FLAG_ONGOING_EVENT用于某事正在进行，例如电话，具体查看参考。
            //【3】发送通知到通知管理器。第一个参数是这个通知的唯一标识，通过这个id可以在以后cancel通知，更新通知（发送一个具有相同id的新通知）。这个id在应用中应该是唯一的。
            if (notifyMgr != null) {
                notifyMgr.notify(NOTIFICATION_ID, notification);
            }

        } else {
            //封装Intent,以便发送局部广播
            Intent msgRcv = new Intent();
            msgRcv.putExtra("hour", hour);
            msgRcv.putExtra("minute", minute);
            msgRcv.putExtra("title", title);
            //发送局部广播
            msgRcv.setAction("AlarmNotificationListenerService");
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgRcv);
        }

    }

}