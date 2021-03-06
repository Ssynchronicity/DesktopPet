package com.example.song.pet;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FloatWindowService extends Service{
    private Handler handler = new Handler();
    private Timer timer;
    private BroadcastReceiver onStopService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("StopFloatWindowService")) {
                stopSelf();
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1,new Notification());
        LocalBroadcastManager.getInstance(this).registerReceiver(onStopService, new IntentFilter("StopFloatWindowService"));

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("FWService", "Started");

        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(),0,500);
        }
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyWindowManager.removeWindow(getApplicationContext());
        Log.i("FWService", "Stopped");
        timer.cancel();
        timer = null;
    }

    class RefreshTask extends TimerTask {

        @Override
        public void run() {
            if (!MyWindowManager.isWindowShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.createWindow(getApplicationContext());
                    }
                });
            }
            // 当前界面是桌面，且有悬浮窗显示，则更新内存数据。
            else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //MyWindowManager.updateUsedPercent(getApplicationContext());
                    }
                });
            }
        }

    }
    /**
     * 判断当前界面是否是桌面
     */
    private boolean isHome() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return getHomes().contains(rti.get(0).topActivity.getPackageName());
    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }
}
