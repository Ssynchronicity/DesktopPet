package com.example.song.pet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;
import java.util.TimeZone;

import static android.content.Context.ALARM_SERVICE;

public class AlarmEventHelper {

    /**
     * 设定一个新闹钟
     *
     * @param context 所在的context
     * @param alarm   闹钟
     * @return true if alarm is set successfully, otherwise false
     */
    public static boolean registerAlarm(Context context, PetAlarmModel alarm) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 检查是否已经超过当前时间，超过则加一天
        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        Bundle alarmData = new Bundle();
        alarmData.putInt("hour", alarm.getHour());
        alarmData.putInt("minute", alarm.getMinute());
        alarmData.putString("title", alarm.getTitle());
        intent.putExtras(alarmData);

        //创建PendingIntent对象
        PendingIntent pi = PendingIntent.getBroadcast(context, alarm.getPid(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //获取系统闹钟服务
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        //开启闹钟
        if (am != null) {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
            return true;
        }

        return false;
    }

    /**
     * 取消已经设定的闹钟
     *
     * @param context 所在的context
     * @param pid     pendingIntentId
     * @return true if alarm is canceled successfully, otherwise false
     */
    public static boolean cancelAlarm(Context context, int pid) {
        //无携带数据的Intent对象
        Intent intent = new Intent(context, AlarmReceiver.class);

        //创建PendingIntent对象
        PendingIntent pi = PendingIntent.getBroadcast(context, pid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //获取系统闹钟服务
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        //删除指定pi对应的闹钟时间
        if (am != null) {
            am.cancel(pi);
            return true;
        }
        return false;

    }
}
