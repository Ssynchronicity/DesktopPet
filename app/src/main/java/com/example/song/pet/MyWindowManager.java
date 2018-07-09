package com.example.song.pet;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;

public class MyWindowManager {
    private static FloatWindowView floatWindow;
    private static FriendWindowView friendWindow;
    private static WindowManager.LayoutParams WindowParams;
    private static WindowManager.LayoutParams FriendParams;
    private static WindowManager mWindowManager;
    private static ActivityManager mActivityManager;
    public  static  int screenWidth;
    public  static  int screenHeight;
    public static void createWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
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
        if (friendWindow != null) {
            windowManager.removeView(friendWindow);
            FriendParams.width = FriendWindowView.friendWidth;
            FriendParams.height = FriendWindowView.friendHeight;
            friendWindow.setParams(FriendParams);
            windowManager.addView(friendWindow, FriendParams);
        }
    }

    public static void addWindow(Context context, int x, int y, String PetName, String PetSkin) {
        WindowManager windowManager = getWindowManager(context);
        if (friendWindow == null) {
            friendWindow = new FriendWindowView(context);
            if (FriendParams == null) {
                FriendParams= new WindowManager.LayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    FriendParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    FriendParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                FriendParams.format = PixelFormat.RGBA_8888;
                FriendParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                FriendParams.gravity = Gravity.LEFT | Gravity.TOP;
                FriendParams.x = x;
                FriendParams.y = y;
                FriendParams.width = FriendWindowView.friendWidth;
                FriendParams.height = FriendWindowView.friendHeight;
            }
            friendWindow.setParams(FriendParams);
            friendWindow.PetSkin = PetSkin;
            friendWindow.PetName = PetName;
            windowManager.addView(friendWindow, FriendParams);
        }
    }
    public static void removeWindow(Context context) {
        if (floatWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(floatWindow);
            floatWindow = null;
        }
    }
    public static void removeFriend(Context context) {
        if (friendWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(friendWindow);
            friendWindow = null;
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
    public static FloatWindowView getFloatWindow() {
        return floatWindow;
    }
    private static long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(mi);
        return mi.availMem;
    }
    private static ActivityManager getActivityManager(Context context) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }



}
