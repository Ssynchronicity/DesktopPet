package com.example.song.pet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

public class FloatWindowView extends RelativeLayout {
    public static int viewWidth;
    public static int viewHeight;
    private static int statusBarHeight;
    private WindowManager windowManager;
    private WindowManager.LayoutParams mParams;
    private float xInScreen;
    private float yInScreen;
    private float xDownInScreen;
    private float yDownInScreen;
    private float xInView;
    private float yInView;
    private ImageView animView;
    private TextView titleView;
    private TextView contextView;
    private View view;
    private View messageView;
    private int NowAnimNumber = 0;
    private boolean isMessageVisible;

    public FloatWindowView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window, this);
        view = findViewById(R.id.float_window_id);
        messageView = findViewById(R.id.layouttt);
        titleView = findViewById(R.id.textview1);
        contextView = findViewById(R.id.textview2);
        animView = findViewById(R.id.animview);
        setViewSizeFromConfig(context);
        viewWidth = animView.getLayoutParams().width;
        viewHeight = animView.getLayoutParams().height;

        LocalBroadcastManager.getInstance(context).registerReceiver(onWechat, new IntentFilter("WeChatNotificationListenerService"));
        LocalBroadcastManager.getInstance(context).registerReceiver(onAlarm, new IntentFilter("AlarmNotificationListenerService"));
        LocalBroadcastManager.getInstance(context).registerReceiver(onPetSizeChanged, new IntentFilter("PetSizeChangeListener"));

        isMessageVisible = false;
    }

    private void setViewSizeFromConfig(Context context) {
        float scale = context.getSharedPreferences("pet", Context.MODE_PRIVATE).getInt("petSize", PetNumbers.INITIAL_PET_VIEW_SIZE) / (float) PetNumbers.INITIAL_PET_VIEW_SIZE;
        scaleViewSize(animView, scale);
        scaleViewSize(messageView, scale);
        scaleViewSize(view, scale);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                    /*动画0：老老实实地站着
                    动画1：走起来!
                    动画2：坐下来在周围看一圈然后无聊的傻fufu的坐着
                    动画3：举起消息框！
                    动画4：爬墙
                    动画5：趴地
                    */
                    if (NowAnimNumber == 0) {
                        if (viewWidth == view.getLayoutParams().width) {
                            viewWidth = animView.getLayoutParams().width;
                            viewHeight = animView.getLayoutParams().height;
                            messageView.setVisibility(View.INVISIBLE);
                            MyWindowManager.updateWindow(getContext());
                        }
                        AnimationDrawable drawable = (AnimationDrawable) ContextCompat.getDrawable(getContext(), R.drawable.walkanim);
                        animView.setBackground(drawable);
                        drawable.start();
                        NowAnimNumber = 1;
                    } else if (NowAnimNumber == 1) {
                        if (viewWidth == view.getLayoutParams().width) {
                            viewWidth = animView.getLayoutParams().width;
                            viewHeight = animView.getLayoutParams().height;
                            messageView.setVisibility(View.INVISIBLE);
                            MyWindowManager.updateWindow(getContext());
                        }
                        AnimationDrawable drawable = (AnimationDrawable) ContextCompat.getDrawable(getContext(), R.drawable.sitanim);
                        animView.setBackground(drawable);
                        drawable.start();
                        NowAnimNumber = 2;
                    } else if (NowAnimNumber == 2) {
                        if (viewWidth == animView.getLayoutParams().width) {
                            viewWidth = view.getLayoutParams().width;
                            viewHeight = view.getLayoutParams().height;
                            messageView.setVisibility(View.VISIBLE);
                            isMessageVisible = true;
                            MyWindowManager.updateWindow(getContext());
                        }
                        AnimationDrawable drawable = (AnimationDrawable) ContextCompat.getDrawable(getContext(), R.drawable.messageanim);
                        animView.setBackground(drawable);
                        drawable.start();
                        NowAnimNumber = 3;
                    } else if (NowAnimNumber == 3) {
                        if (viewWidth == view.getLayoutParams().width) {
                            viewWidth = animView.getLayoutParams().width;
                            viewHeight = animView.getLayoutParams().height;
                            messageView.setVisibility(View.INVISIBLE);
                            MyWindowManager.updateWindow(getContext());
                        }
                        AnimationDrawable drawable = (AnimationDrawable) ContextCompat.getDrawable(getContext(), R.drawable.paqiang);
                        animView.setBackground(drawable);
                        drawable.start();
                        NowAnimNumber = 4;
                    } else if (NowAnimNumber == 4) {
                        if (viewWidth == view.getLayoutParams().width) {
                            viewWidth = animView.getLayoutParams().width;
                            viewHeight = animView.getLayoutParams().height;
                            messageView.setVisibility(View.INVISIBLE);
                            MyWindowManager.updateWindow(getContext());
                        }
                        AnimationDrawable drawable = (AnimationDrawable) ContextCompat.getDrawable(getContext(), R.drawable.padi);
                        animView.setBackground(drawable);
                        drawable.start();
                        NowAnimNumber = 5;
                    } else {
                        if (viewWidth == view.getLayoutParams().width) {
                            viewWidth = animView.getLayoutParams().width;
                            viewHeight = animView.getLayoutParams().height;
                            messageView.setVisibility(View.INVISIBLE);
                            MyWindowManager.updateWindow(getContext());
                        }
                        animView.setBackgroundResource(R.drawable.shime1);
                        NowAnimNumber = 0;
                    }

                }
                break;
            default:
                break;
        }
        return true;
    }

    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    private BroadcastReceiver onWechat = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("WeChatNotificationListenerService")) {

                if (intent.getStringExtra("from").equals(WeChatNotificationListenerService.label)) {
                    String name = intent.getStringExtra("name");
                    String text = intent.getStringExtra("text");

                    String content = "";
                    if (name != null && text != null) {
                        content = name + ":" + text;
                    } else if (name != null) {
                        content = name;
                    } else if (text != null) {
                        content = text;
                    }

                    if (viewWidth == animView.getLayoutParams().width) {
                        viewWidth = view.getLayoutParams().width;
                        viewHeight = view.getLayoutParams().height;
                        messageView.setVisibility(View.VISIBLE);
                        MyWindowManager.updateWindow(getContext());
                    }
                    titleView.setText("主人主人，有新的微信消息啦");//关于宠物怎么称呼用户，需要读取设置页面的信息
                    contextView.setText(content);
                    AnimationDrawable drawable = (AnimationDrawable) ContextCompat.getDrawable(getContext(), R.drawable.messageanim);
                    animView.setBackground(drawable);
                    drawable.start();
                    NowAnimNumber = 0;
                }
            }
        }
    };

    private BroadcastReceiver onAlarm = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("AlarmNotificationListenerService")) {
                String task = intent.getStringExtra("task");
                String date = intent.getStringExtra("date");
                String time = intent.getStringExtra("time");
                String addition = intent.getStringExtra("addition");

                String content = time;
                if (task != null) {
                    content = task + "\n" + content;
                }
                if (addition != null) {
                    content += "\n" + addition;
                }

                if (viewWidth == animView.getLayoutParams().width) {
                    viewWidth = view.getLayoutParams().width;
                    viewHeight = view.getLayoutParams().height;
                    messageView.setVisibility(View.VISIBLE);
                    MyWindowManager.updateWindow(getContext());
                }
                titleView.setText("主人主人，闹钟响啦");//关于宠物怎么称呼用户，需要读取设置页面的信息
                contextView.setText(content);
                AnimationDrawable drawable = (AnimationDrawable) ContextCompat.getDrawable(getContext(), R.drawable.messageanim);
                animView.setBackground(drawable);
                drawable.start();
                NowAnimNumber = 0;
            }
        }
    };

    private BroadcastReceiver onPetSizeChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("PetSizeChangeListener")) {
                float scale = intent.getIntExtra("changedSize", PetNumbers.INITIAL_PET_VIEW_SIZE) / (float) PetNumbers.INITIAL_PET_VIEW_SIZE;
                scaleViewSize(animView, scale);
                scaleViewSize(messageView, scale);
                scaleViewSize(view, scale);
                if (!isMessageVisible) {
                    viewHeight = animView.getLayoutParams().height;
                    viewWidth = animView.getLayoutParams().width;
                } else {
                    viewHeight = view.getLayoutParams().height;
                    viewWidth = view.getLayoutParams().width;
                }
                MyWindowManager.updateWindow(getContext());
            }
        }
    };

    private void scaleViewSize(View view, float scale) {
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scale * PetNumbers.INITIAL_PET_VIEW_SIZE, getResources().getDisplayMetrics());
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scale * PetNumbers.INITIAL_PET_VIEW_SIZE, getResources().getDisplayMetrics());

        view.setLayoutParams(params);
    }
}

