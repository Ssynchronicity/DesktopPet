package com.example.song.pet;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;


public class WeChatNotificationListenerService extends NotificationListenerService {
    public static final String label = "WeChatNotificationService";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        if(!sbn.getPackageName().equals("com.tencent.mm")) return;

        Bundle bundle = new Bundle();
        bundle.putString("name", sbn.getNotification().extras.getString(Notification.EXTRA_TITLE));
        bundle.putString("text", sbn.getNotification().extras.getString(Notification.EXTRA_TEXT));

        Intent intent = new Intent();
        intent.putExtra("from",WeChatNotificationListenerService.label);
        intent.putExtras(bundle);
        intent.setAction("com.example.activity.WeChatNotificationListenerService");
        sendBroadcast(intent);
    }
}

/*将下面这个东西放在要接收微信消息的地方，*/
/* private class WeChatReveiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if(intent.getStringExtra("from").equals(WeChatNotificationListenerService.label)){
            String name = bundle.getString("name");
            String text = bundle.getString("text");

            new AlertDialog.Builder(MainActivity.this) //!!!!!!!!!!!!这个 MainActivity 可能要换!!!!!!!!!!!!!
                    .setTitle("微信信息")
                    .setMessage(name + " : \n" + text)
                    .setPositiveButton("确定", null)
                    .show();
        }
    }
} */
/*创建实例

    private WeChatReveiver receiver = null;

    receiver = new WeChatReveiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction("com.example.activity.WeChatNotificationListenerService");
    MainActivity.this.registerReceiver(receiver, filter);
 */
