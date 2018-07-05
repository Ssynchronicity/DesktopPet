package com.example.song.pet;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MyWindowManager {
    private static FloatWindowView floatWindow;
    private static WindowManager.LayoutParams WindowParams;
    private static WindowManager mWindowManager;
    private static ActivityManager mActivityManager;
    public static void createWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        if (floatWindow == null) {
            floatWindow = new FloatWindowView(context);
            if (WindowParams == null) {
                WindowParams = new WindowManager.LayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    WindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                WindowParams.format = PixelFormat.RGBA_8888;
                WindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                WindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                WindowParams.width = FloatWindowView.viewWidth;
                WindowParams.height = FloatWindowView.viewHeight;
                WindowParams.x = screenWidth;
                WindowParams.y = screenHeight / 2;
            }
            floatWindow.setParams(WindowParams);
            windowManager.addView(floatWindow, WindowParams);
        }
    }
    public static void updateWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        windowManager.removeView(floatWindow);
        WindowParams.width = FloatWindowView.viewWidth;
        WindowParams.height = FloatWindowView.viewHeight;
        floatWindow.setParams(WindowParams);
        windowManager.addView(floatWindow, WindowParams);
    }
    public static void removeWindow(Context context) {
        if (floatWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(floatWindow);
            floatWindow = null;
        }
    }
    public static void updateUsedPercent(Context context) {
        if (floatWindow != null) {
            //TextView percentView = (TextView) floatWindow.findViewById(R.id.textview1);
            //percentView.setText(getUsedPercentValue(context));
        }
    }
    public static boolean isWindowShowing() {
        return floatWindow != null;
    }
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }
    private static ActivityManager getActivityManager(Context context) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }
    private static String getUsedPercentValue(Context context) {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
            long availableSize = getAvailableMemory(context) / 1024;
            int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
            return percent + "%";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "悬浮窗";
    }
    private static long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(mi);
        return mi.availMem;
    }

    public static FloatWindowView getFloatWindow() {
        return floatWindow;
    }
}
