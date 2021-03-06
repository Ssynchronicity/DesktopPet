package com.example.song.pet;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;



public class StartupReceiver extends BroadcastReceiver
{
      SharedPreferences sharedPreferences ;
      SharedPreferences.Editor editor;
      @Override
      public void onReceive(Context context, Intent Intent)
      {
          sharedPreferences = context.getSharedPreferences("pet", Context.MODE_PRIVATE);
          editor = sharedPreferences.edit();
          if(!sharedPreferences.getBoolean("autoStart", false))
          {
              return;
          }
            android.content.Intent intent = new Intent(context, FloatWindowService.class);
            ComponentName componentName = new ComponentName("com.example.song.pet","FloatWindowService");
            // intent.setComponent(componentName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startForegroundService(intent);
      }
}
            /* sharedPreferences = context.getSharedPreferences("pet", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            if(sharedPreferences.getBoolean("on", true)&&(sharedPreferences.getBoolean("isSecondOn", true)||sharedPreferences.getBoolean("isFirstOn", true))){
                  android.content.Intent intent = new Intent(context, FloatWindowService.class);
                  context.startService(intent);
            }
            else{
                  //如果设置开机不启动，则把宠物的选择给设置为false
                  editor.putBoolean("isFirstOn",false);
                  editor.putBoolean("isSecondOn",false);
                  editor.commit();
            } */

