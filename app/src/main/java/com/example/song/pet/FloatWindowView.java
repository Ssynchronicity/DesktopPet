package com.example.song.pet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.litepal.LitePal;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class FloatWindowView extends RelativeLayout {
    public static int viewWidth;
    public static int viewHeight;
    private static int statusBarHeight;
    private WindowManager windowManager;
    private WindowManager.LayoutParams mParams;
    private float xInScreen;//记录当前手指位置在屏幕上的横坐标值
    private float yInScreen;//记录当前手指位置在屏幕上的纵坐标值
    private float xDownInScreen;//记录手指按下时在屏幕上的横坐标的值
    private float yDownInScreen;//记录手指按下时在屏幕上的纵坐标的值
    private float xInView;//记录手指按下时在小悬浮窗的View上的横坐标的值
    private float yInView;//记录手指按下时在小悬浮窗的View上的纵坐标的值
    private ImageView animview;
    private TextView titleview;
    private TextView contextview;
    private View view;
    private View messageview;
    private int NowAnimNumber = 0;
    private String NowAttrAnim = "";
    private AnimationDrawable walkDrawable;
    private AnimationDrawable sitDrawable;
    private AnimationDrawable messageDrawable;
    private AnimationDrawable paqiangDrawable;
    private AnimationDrawable padiDrawable;
    private AnimationDrawable sit2Drawable;
    private AnimationDrawable differDrawable;
    private AnimationDrawable sameDrawable;
    private AnimationDrawable wechatDrawable;
    private boolean MessageVis;
    private String PetName;
    private int size;
    private ValueAnimator quietAnim;
    private boolean isWechat;
    private boolean isAlarm;
    private String PetSkin;
    private String PetString;
    private AnimManager animManager = new AnimManager();
    private SharedPreferences sharedPreferences;
    public FloatWindowView(Context context) {
        super(context);
        size = 125;
        sharedPreferences = context.getSharedPreferences("pet", Context.MODE_PRIVATE);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window, this);
        view = findViewById(R.id.float_window_id);
        messageview = findViewById(R.id.layouttt);
        titleview = (TextView) findViewById(R.id.textview1);
        contextview = (TextView) findViewById(R.id.textview2);
        animview = (ImageView) findViewById(R.id.animview);
//        PetSkin = "chuyin";
        PetSkin = sharedPreferences.getString("current", "chuyin");
        PetString = sharedPreferences.getString("currentAppellation", "主人");
        switch (PetSkin) {
            case "billd":
                animview.setBackgroundResource(R.drawable.billd1);
                break;
            case "bulaike":
                animview.setBackgroundResource(R.drawable.bulaike1);
                break;
            case "ruiersi":
                animview.setBackgroundResource(R.drawable.ruiersi1);
                break;
            case "dage":
                animview.setBackgroundResource(R.drawable.dage1);
                break;
            case "haimian":
                animview.setBackgroundResource(R.drawable.haimian1);
                break;
            case "jiansan":
                animview.setBackgroundResource(R.drawable.jiansan1);
                break;
            case "kaxiusi":
                animview.setBackgroundResource(R.drawable.kaxiusi1);
                break;
            case "pikaqiu":
                animview.setBackgroundResource(R.drawable.pikaqiu1);
                break;
            default:
                animview.setBackgroundResource(R.drawable.chuyin1);
                break;
        }
        viewWidth = animview.getLayoutParams().width;
        viewHeight = animview.getLayoutParams().height;
        InitAnim();
        MessageVis = viewWidth == view.getLayoutParams().width;
        PetName = sharedPreferences.getString("currentName", "初音");
        LocalBroadcastManager.getInstance(context).registerReceiver(onWechat, new IntentFilter("WeChatNotificationListenerService"));
        LocalBroadcastManager.getInstance(context).registerReceiver(onAlarm, new IntentFilter("AlarmNotificationListenerService"));
        LocalBroadcastManager.getInstance(context).registerReceiver(onPetSizeChanged, new IntentFilter("PetSizeChangeListener"));
        LocalBroadcastManager.getInstance(context).registerReceiver(ChangeName, new IntentFilter("ChangeNameListenerService"));
        LocalBroadcastManager.getInstance(context).registerReceiver(onBluetooth, new IntentFilter("com.bluetooth.visit"));
        LocalBroadcastManager.getInstance(context).registerReceiver(onBluetoothGo, new IntentFilter("com.bluetooth.back"));
        LocalBroadcastManager.getInstance(context).registerReceiver(onChangePet, new IntentFilter("changePetModel"));
    }

    private void ChangeMessageVisbility(boolean i) {
        int newx = mParams.x;
        int newy = mParams.y;
        if (MessageVis && !i) {
            viewWidth = animview.getLayoutParams().width;
            viewHeight = animview.getLayoutParams().height;
            messageview.setVisibility(View.INVISIBLE);
            MyWindowManager.updateWindow(getContext());
            mParams.x = newx + view.getLayoutParams().width - animview.getLayoutParams().width;
            mParams.y = newy + view.getLayoutParams().height - animview.getLayoutParams().height;
            windowManager.updateViewLayout(this, mParams);
        } else if (!MessageVis && i) {
            viewWidth = view.getLayoutParams().width;
            viewHeight = view.getLayoutParams().height;
            messageview.setVisibility(View.VISIBLE);
            MyWindowManager.updateWindow(getContext());
            mParams.x = newx - view.getLayoutParams().width + animview.getLayoutParams().width;
            mParams.y = newy - view.getLayoutParams().height + animview.getLayoutParams().height;
            windowManager.updateViewLayout(this, mParams);
        }
        MessageVis = i;
    }

    private void InitAnim() {
        //1 走
        walkDrawable = animManager.walk(PetSkin);
        //2 坐
        sitDrawable = animManager.sit(PetSkin);
        //3 举消息框
        messageDrawable = animManager.message(PetSkin);
        //4 爬墙
        paqiangDrawable = animManager.paqiang(PetSkin);
        //5 爬地
        padiDrawable = animManager.padi(PetSkin);
        //6 坐着晃腿
        sit2Drawable = animManager.sit2(PetSkin);
        //7 不同的分裂
        differDrawable = animManager.differ(PetSkin);
        //9 先跳再举牌子
        wechatDrawable = animManager.wechat(PetSkin);

    }

    public static float dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }

    public void ChangeFrameAnim(int i) {
        // 如果被移除了，就不再执行逐帧动画
        if (MyWindowManager.getFloatWindow() == null) return;
        switch (NowAnimNumber) {
            case 1:
                walkDrawable.stop();
                break;
            case 2:
                sitDrawable.stop();
                break;
            case 3:
                messageDrawable.stop();
                break;
            case 4:
                paqiangDrawable.stop();
                break;
            case 5:
                padiDrawable.stop();
                break;
            case 6:
                sit2Drawable.stop();
                break;
            case 7:
                differDrawable.stop();
                break;

            case 9:
                wechatDrawable.stop();
                break;
            default:
                break;
        }
        switch (i) {
            case 0:
                switch (PetSkin) {
                    case "billd":
                        animview.setBackgroundResource(R.drawable.billd1);
                        break;
                    case "bulaike":
                        animview.setBackgroundResource(R.drawable.bulaike1);
                        break;
                    case "ruiersi":
                        animview.setBackgroundResource(R.drawable.ruiersi1);
                        break;
                    case "dage":
                        animview.setBackgroundResource(R.drawable.dage1);
                        break;
                    case "haimian":
                        animview.setBackgroundResource(R.drawable.haimian1);
                        break;
                    case "jiansan":
                        animview.setBackgroundResource(R.drawable.jiansan1);
                        break;
                    case "kaxiusi":
                        animview.setBackgroundResource(R.drawable.kaxiusi1);
                        break;
                    case "pikaqiu":
                        animview.setBackgroundResource(R.drawable.pikaqiu1);
                        break;
                    default:
                        animview.setBackgroundResource(R.drawable.chuyin1);
                        break;
                }
                break;
            case 1:
                animview.setBackground(walkDrawable);
                walkDrawable.start();
                break;
            case 2:
                animview.setBackground(sitDrawable);
                sitDrawable.start();
                break;
            case 3:
                animview.setBackground(messageDrawable);
                messageDrawable.start();
                break;
            case 4:
                animview.setBackground(paqiangDrawable);
                paqiangDrawable.start();
                break;
            case 5:
                animview.setBackground(padiDrawable);
                padiDrawable.start();
                break;
            case 6:
                animview.setBackground(sit2Drawable);
                sit2Drawable.start();
                break;
            case 7:
                animview.setBackground(differDrawable);
                differDrawable.start();
                break;

            case 9:
                animview.setBackground(wechatDrawable);
                wechatDrawable.start();
                break;
            case 10:
                switch (PetSkin) {
                    case "billd":
                        animview.setBackgroundResource(R.drawable.billd34);
                        break;
                    case "bulaike":
                        animview.setBackgroundResource(R.drawable.bulaike34);
                        break;
                    case "ruiersi":
                        animview.setBackgroundResource(R.drawable.ruiersi34);
                        break;
                    case "dage":
                        animview.setBackgroundResource(R.drawable.dage34);
                        break;
                    case "haimian":
                        animview.setBackgroundResource(R.drawable.haimian34);
                        break;
                    case "jiansan":
                        animview.setBackgroundResource(R.drawable.jiansan34);
                        break;
                    case "kaxiusi":
                        animview.setBackgroundResource(R.drawable.kaxiusi34);
                        break;
                    case "pikaqiu":
                        animview.setBackgroundResource(R.drawable.pikaqiu34);
                        break;
                    default:
                        animview.setBackgroundResource(R.drawable.chuyin34);
                        break;
                }
                break;
            case 11:
                switch (PetSkin) {
                    case "billd":
                        animview.setBackgroundResource(R.drawable.billd4);
                        break;
                    case "bulaike":
                        animview.setBackgroundResource(R.drawable.bulaike4);
                        break;
                    case "ruiersi":
                        animview.setBackgroundResource(R.drawable.ruiersi4);
                        break;
                    case "dage":
                        animview.setBackgroundResource(R.drawable.dage4);
                        break;
                    case "haimian":
                        animview.setBackgroundResource(R.drawable.haimian4);
                        break;
                    case "jiansan":
                        animview.setBackgroundResource(R.drawable.jiansan4);
                        break;
                    case "kaxiusi":
                        animview.setBackgroundResource(R.drawable.kaxiusi4);
                        break;
                    case "pikaqiu":
                        animview.setBackgroundResource(R.drawable.pikaqiu4);
                        break;
                    default:
                        animview.setBackgroundResource(R.drawable.chuyin4);
                        break;
                }
                break;
            case 12:
                switch (PetSkin) {
                    case "billd":
                        animview.setBackgroundResource(R.drawable.billd22);
                        break;
                    case "bulaike":
                        animview.setBackgroundResource(R.drawable.bulaike22);
                        break;
                    case "ruiersi":
                        animview.setBackgroundResource(R.drawable.ruiersi22);
                        break;
                    case "dage":
                        animview.setBackgroundResource(R.drawable.dage22);
                        break;
                    case "haimian":
                        animview.setBackgroundResource(R.drawable.haimian22);
                        break;
                    case "jiansan":
                        animview.setBackgroundResource(R.drawable.jiansan22);
                        break;
                    case "kaxiusi":
                        animview.setBackgroundResource(R.drawable.kaxiusi22);
                        break;
                    case "pikaqiu":
                        animview.setBackgroundResource(R.drawable.pikaqiu22);
                        break;
                    default:
                        animview.setBackgroundResource(R.drawable.chuyin22);
                        break;
                }
                break;
            default:
                break;
        }
        NowAnimNumber = i;
    }

    public void ChangeAttrAnim(String i, final String title, final String content) {
        if (MyWindowManager.getFloatWindow() == null) return;
        if (NowAttrAnim.equals(i) && !i.equals("quiet")) {
            titleview.setText(title);
            contextview.setText(content);
        } else {
            switch (NowAttrAnim) {
                case "alarm":
                    ChangeMessageVisbility(false);
                    break;
                case "wechat":
                    RelativeLayout.LayoutParams parm = new LayoutParams((int) dip2px(size * (float) (202.0 / 125.0)), (int) dip2px(size * (float) (100.0 / 125.0)));
                    parm.addRule(RelativeLayout.ALIGN_PARENT_START);
                    parm.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    parm.setMargins(0, (int) dip2px(size * (float) (62.0 / 125.0)), 0, 0);
                    messageview.setLayoutParams(parm);
                    RelativeLayout.LayoutParams parm2 = new LayoutParams((int) dip2px(size), (int) dip2px(size));
                    parm2.addRule(RelativeLayout.ALIGN_PARENT_END);
                    parm2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    parm2.setMargins(0, 0, 0, 0);
                    animview.setLayoutParams(parm2);
                    int newx = mParams.x;
                    ChangeMessageVisbility(false);
                    mParams.x = newx;
                    windowManager.updateViewLayout(this, mParams);
                    break;
                case "bluebooth":
                    break;
                case "quiet":
                    quietAnim.cancel();
                    break;
                case "bluetoothgo":
                    break;
                default:
                    break;
            }

            switch (i) {
                case "alarm":
                    isAlarm = true;
                    ChangeMessageVisbility(false);
                    titleview.setText(title);
                    contextview.setText(content);
                    ChangeFrameAnim(1);
                    ObjectAnimator object_rotate = ObjectAnimator.ofFloat(view, "rotationY", 0, 30, 60, 90, 120, 150, 180);
                    object_rotate.setDuration(500);
                    object_rotate.start();
                    float startX = mParams.x;
                    float endX = MyWindowManager.screenWidth - animview.getLayoutParams().width;
                    final float startY = mParams.y;
                    final float endY = (MyWindowManager.screenHeight - getStatusBarHeight()) / 2;
                    final float startX1 = MyWindowManager.screenWidth - animview.getLayoutParams().width;
                    final float endX1 = dip2px(size * (float) (50.0 / 125.0));
                    final long currentViewId = MyWindowManager.getCurrentViewId();
                    ValueAnimator runAnim = ValueAnimator.ofObject(new animEvaluator(), startX, endX);
                    runAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            if (MyWindowManager.getFloatWindow() == null) {
                                animation.cancel();
                                return;
                            }
                            if (!MyWindowManager.isViewChanged(currentViewId)) return;
                            mParams.x = (int) (float) animation.getAnimatedValue();
                            windowManager.updateViewLayout(FloatWindowView.this, mParams);
                        }
                    });
                    runAnim.setStartDelay(500);
                    runAnim.setDuration(1000);
                    runAnim.setInterpolator(new LinearInterpolator());
                    runAnim.start();
                    runAnim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation, boolean isReverse) {
                            ObjectAnimator object_rotate1 = ObjectAnimator.ofFloat(view, "rotationY", 180, 150, 120, 90, 60, 30, 0);
                            object_rotate1.setDuration(500);
                            object_rotate1.start();
                            final long currentViewId = MyWindowManager.getCurrentViewId();
                            ValueAnimator runAnim1 = ValueAnimator.ofObject(new animEvaluator(), startY, endY);
                            runAnim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    if (MyWindowManager.getFloatWindow() == null) {
                                        animation.cancel();
                                        return;
                                    }
                                    if (!MyWindowManager.isViewChanged(currentViewId)) return;
                                    mParams.y = (int) (float) animation.getAnimatedValue();
                                    windowManager.updateViewLayout(FloatWindowView.this, mParams);
                                }
                            });
                            runAnim1.setStartDelay(500);
                            runAnim1.setDuration(1000);
                            runAnim1.start();
                            runAnim1.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation, boolean isReverse) {
                                    if (startY < endY) ChangeFrameAnim(11);
                                    else ChangeFrameAnim(12);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation, boolean isReverse) {
                                    ChangeFrameAnim(9);
                                    ChangeMessageVisbility(true);
                                    final long currentViewId = MyWindowManager.getCurrentViewId();
                                    ValueAnimator runAnim2 = ValueAnimator.ofObject(new animEvaluator(), startX1, endX1);
                                    runAnim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            if (MyWindowManager.getFloatWindow() == null) {
                                                animation.cancel();
                                                return;
                                            }
                                            if (!MyWindowManager.isViewChanged(currentViewId))
                                                return;
                                            mParams.x = (int) (float) animation.getAnimatedValue();
                                            windowManager.updateViewLayout(FloatWindowView.this, mParams);
                                        }
                                    });
                                    runAnim2.setStartDelay(400);
                                    runAnim2.setDuration(1600);
                                    runAnim2.setInterpolator(new LinearInterpolator());
                                    runAnim2.start();
                                    runAnim2.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation, boolean isReverse) {
                                            ChangeFrameAnim(10);
                                            isAlarm = false;
                                        }
                                    });
                                }
                            });
                        }
                    });
                    break;
                case "wechat":
                    isWechat = true;
                    titleview.setText(title);
                    contextview.setText(content);
                    RelativeLayout.LayoutParams parm = new LayoutParams((int) dip2px(size * (float) (202.0 / 125.0)), (int) dip2px(size * (float) (100.0 / 125.0)));
                    parm.addRule(RelativeLayout.ALIGN_PARENT_START);
                    parm.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    messageview.setLayoutParams(parm);
                    ChangeMessageVisbility(true);
                    ChangeFrameAnim(4);
                    TranslateAnimation animAlarm1 = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0 - animview.getLayoutParams().height / 2);
                    animAlarm1.setDuration(1000);
                    animAlarm1.setInterpolator(new LinearInterpolator());
                    animview.startAnimation(animAlarm1);
                    animAlarm1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            RelativeLayout.LayoutParams parm1 = new LayoutParams((int) dip2px(size), (int) dip2px(size));
                            parm1.addRule(RelativeLayout.ALIGN_PARENT_END);
                            parm1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                            parm1.setMargins(0, 0, (int) dip2px(size * (float) (30.0 / 125.0)), 0);
                            animview.setLayoutParams(parm1);
                            ChangeFrameAnim(5);
                            TranslateAnimation animAlarm2 = new TranslateAnimation(
                                    Animation.RELATIVE_TO_SELF, 0 - dip2px(size * (float) (110.0 / 125.0)),
                                    Animation.RELATIVE_TO_SELF, 0);
                            animAlarm2.setDuration(5000);
                            animAlarm2.setInterpolator(new LinearInterpolator());
                            animview.startAnimation(animAlarm2);
                            animAlarm2.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    Log.i("ValueAnim", "Start called");
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    RelativeLayout.LayoutParams parm2 = new LayoutParams((int) dip2px(size), (int) dip2px(size));
                                    parm2.addRule(RelativeLayout.ALIGN_PARENT_START);
                                    parm2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                    parm2.setMargins(0, (int) dip2px(size * (float) (17.0 / 125.0)), 0, 0);
                                    animview.setLayoutParams(parm2);
                                    ChangeFrameAnim(6);
                                    isWechat = false;
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    break;
                case "bluebooth":
                    ChangeMessageVisbility(false);
                    ChangeFrameAnim(7);
                    final int friendx = mParams.x;
                    final int friendy = mParams.y;
                    float startx = mParams.x;
                    float endx = mParams.x + animview.getLayoutParams().width;
                    final long currentViewId4 = MyWindowManager.getCurrentViewId();
                    ValueAnimator runAnim1 = ValueAnimator.ofObject(new animEvaluator(), startx, endx);
                    runAnim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            if (MyWindowManager.getFloatWindow() == null) {
                                animation.cancel();
                                return;
                            }
                            if (!MyWindowManager.isViewChanged(currentViewId4)) return;
                            mParams.x = (int) (float) animation.getAnimatedValue();
                            windowManager.updateViewLayout(FloatWindowView.this, mParams);
                        }
                    });
                    runAnim1.setStartDelay(1100);
                    runAnim1.setDuration(100);
                    runAnim1.start();
                    runAnim1.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation, boolean isReverse) {
                            MyWindowManager.addWindow(getContext(), friendx, friendy, content, title);
                            ChangeFrameAnim(0);
                        }
                    });

                    break;
                case "quiet":
                    ChangeMessageVisbility(false);
                    ChangeFrameAnim(2);
                    if (mParams.x < MyWindowManager.screenWidth - mParams.x) {
                        float startX2 = mParams.x;
                        float endX2 = 0;
                        final long currentViewId3 = MyWindowManager.getCurrentViewId();
                        quietAnim = ValueAnimator.ofObject(new animEvaluator(), startX2, endX2);
                        quietAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                if (MyWindowManager.getFloatWindow() == null) {
                                    animation.cancel();
                                    return;
                                }
                                // 返回false表明view被重置过
                                if (!MyWindowManager.isViewChanged(currentViewId3)) return;
                                mParams.x = (int) (float) animation.getAnimatedValue();
                                windowManager.updateViewLayout(MyWindowManager.getFloatWindow(), mParams);
                            }
                        });
                        quietAnim.setStartDelay(5000);
                        quietAnim.setDuration(1000);
                        quietAnim.setInterpolator(new LinearInterpolator());
                        quietAnim.start();
                        quietAnim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation, boolean isReverse) {
                                ChangeFrameAnim(1);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation, boolean isReverse) {
                                switch (PetSkin) {
                                    case "billd":
                                        animview.setBackgroundResource(R.drawable.billd9);
                                        break;
                                    case "bulaike":
                                        animview.setBackgroundResource(R.drawable.bulaike9);
                                        break;
                                    case "ruiersi":
                                        animview.setBackgroundResource(R.drawable.ruiersi9);
                                        break;
                                    case "dage":
                                        animview.setBackgroundResource(R.drawable.dage9);
                                        break;
                                    case "haimian":
                                        animview.setBackgroundResource(R.drawable.haimian9);
                                        break;
                                    case "jiansan":
                                        animview.setBackgroundResource(R.drawable.jiansan9);
                                        break;
                                    case "kaxiusi":
                                        animview.setBackgroundResource(R.drawable.kaxiusi9);
                                        break;
                                    case "pikaqiu":
                                        animview.setBackgroundResource(R.drawable.pikaqiu9);
                                        break;
                                    default:
                                        animview.setBackgroundResource(R.drawable.chuyin9);
                                        break;
                                }
                            }
                        });
                    } else {
                        float startX2 = mParams.x;
                        float endX2 = MyWindowManager.screenWidth - animview.getLayoutParams().width;
                        final long currentViewId2 = MyWindowManager.getCurrentViewId();
                        quietAnim = ValueAnimator.ofObject(new animEvaluator(), startX2, endX2);
                        quietAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                if (MyWindowManager.getFloatWindow() == null) {
                                    animation.cancel();
                                    return;
                                }
                                if (!MyWindowManager.isViewChanged(currentViewId2)) return;
                                mParams.x = (int) (float) animation.getAnimatedValue();
//                                windowManager.updateViewLayout(FloatWindowView.this, mParams);
                                windowManager.updateViewLayout(MyWindowManager.getFloatWindow(), mParams);
                            }
                        });
                        quietAnim.setStartDelay(5000);
                        quietAnim.setDuration(1000);
                        quietAnim.setInterpolator(new LinearInterpolator());
                        quietAnim.start();
                        quietAnim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation, boolean isReverse) {
                                ChangeFrameAnim(1);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation, boolean isReverse) {
                                switch (PetSkin) {
                                    case "billd":
                                        animview.setBackgroundResource(R.drawable.billd10);
                                        break;
                                    case "bulaike":
                                        animview.setBackgroundResource(R.drawable.bulaike10);
                                        break;
                                    case "ruiersi":
                                        animview.setBackgroundResource(R.drawable.ruiersi10);
                                        break;
                                    case "dage":
                                        animview.setBackgroundResource(R.drawable.dage10);
                                        break;
                                    case "haimian":
                                        animview.setBackgroundResource(R.drawable.haimian10);
                                        break;
                                    case "jiansan":
                                        animview.setBackgroundResource(R.drawable.jiansan10);
                                        break;
                                    case "kaxiusi":
                                        animview.setBackgroundResource(R.drawable.kaxiusi10);
                                        break;
                                    case "pikaqiu":
                                        animview.setBackgroundResource(R.drawable.pikaqiu10);
                                        break;
                                    default:
                                        animview.setBackgroundResource(R.drawable.chuyin10);
                                        break;
                                }
                            }
                        });
                    }
                    break;
                case "bluetoothgo":
                    switch (PetSkin) {
                        case "billd":
                            animview.setBackgroundResource(R.drawable.billd37);
                            break;
                        case "bulaike":
                            animview.setBackgroundResource(R.drawable.bulaike37);
                            break;
                        case "ruiersi":
                            animview.setBackgroundResource(R.drawable.ruiersi37);
                            break;
                        case "dage":
                            animview.setBackgroundResource(R.drawable.dage37);
                            break;
                        case "haimian":
                            animview.setBackgroundResource(R.drawable.haimian37);
                            break;
                        case "jiansan":
                            animview.setBackgroundResource(R.drawable.jiansan37);
                            break;
                        case "kaxiusi":
                            animview.setBackgroundResource(R.drawable.kaxiusi37);
                            break;
                        case "pikaqiu":
                            animview.setBackgroundResource(R.drawable.pikaqiu37);
                            break;
                        default:
                            animview.setBackgroundResource(R.drawable.chuyin37);
                            break;
                    }
                    MyWindowManager.removeFriend(getContext());
                    break;
                default:
                    break;
            }
            NowAttrAnim = i;
        }

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
                float movex = (xInScreen - xInView - mParams.x) / MyWindowManager.screenWidth;
                if (movex < 0) {
                    switch (PetSkin) {
                        case "billd":
                            animview.setBackgroundResource(R.drawable.billd8);
                            break;
                        case "bulaike":
                            animview.setBackgroundResource(R.drawable.bulaike8);
                            break;
                        case "ruiersi":
                            animview.setBackgroundResource(R.drawable.ruiersi8);
                            break;
                        case "dage":
                            animview.setBackgroundResource(R.drawable.dage8);
                            break;
                        case "haimian":
                            animview.setBackgroundResource(R.drawable.haimian8);
                            break;
                        case "jiansan":
                            animview.setBackgroundResource(R.drawable.jiansan8);
                            break;
                        case "kaxiusi":
                            animview.setBackgroundResource(R.drawable.kaxiusi8);
                            break;
                        case "pikaqiu":
                            animview.setBackgroundResource(R.drawable.pikaqiu8);
                            break;
                        default:
                            animview.setBackgroundResource(R.drawable.chuyin8);
                            break;
                    }
                } else {
                    switch (PetSkin) {
                        case "billd":
                            animview.setBackgroundResource(R.drawable.billd7);
                            break;
                        case "bulaike":
                            animview.setBackgroundResource(R.drawable.bulaike7);
                            break;
                        case "ruiersi":
                            animview.setBackgroundResource(R.drawable.ruiersi7);
                            break;
                        case "dage":
                            animview.setBackgroundResource(R.drawable.dage7);
                            break;
                        case "haimian":
                            animview.setBackgroundResource(R.drawable.haimian7);
                            break;
                        case "jiansan":
                            animview.setBackgroundResource(R.drawable.jiansan7);
                            break;
                        case "kaxiusi":
                            animview.setBackgroundResource(R.drawable.kaxiusi7);
                            break;
                        case "pikaqiu":
                            animview.setBackgroundResource(R.drawable.pikaqiu7);
                            break;
                        default:
                            animview.setBackgroundResource(R.drawable.chuyin7);
                            break;
                    }
                }
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                isWechat = false;
                isAlarm = false;
                ChangeAttrAnim("quiet", "", "");
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
                if (!isAlarm)
                    ChangeAttrAnim("wechat", PetName + "：" + PetString + ",有新的微信消息啦", content);

            }
        }
    };

    private BroadcastReceiver onAlarm = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("AlarmNotificationListenerService")) {
                int hour = intent.getIntExtra("hour", 23);
                int minute = intent.getIntExtra("minute", 59);
                String title = intent.getStringExtra("title");
                String content = String.format(Locale.CHINA, "%02d:%02d", hour, minute);
                content += title.equals("提醒事项") ? "提醒~" : title;
                if (!isWechat)
                    ChangeAttrAnim("alarm", PetName + "：" + PetString + ",闹钟响啦", content);
            }
        }
    };

    private BroadcastReceiver onPetSizeChanged = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("PetSizeChangeListener")) {
                int size0 = intent.getIntExtra("changedSize", size);
                float beishu = (float) size0 / size;
                LayoutParams params = (LayoutParams) animview.getLayoutParams();
                params.width = (int) (animview.getLayoutParams().width * beishu);
                params.height = (int) (animview.getLayoutParams().height * beishu);
                animview.setLayoutParams(params);
                LayoutParams params1 = (LayoutParams) messageview.getLayoutParams();
                params1.width = (int) (messageview.getLayoutParams().width * beishu);
                params1.height = (int) (messageview.getLayoutParams().height * beishu);
                messageview.setLayoutParams(params1);
                RelativeLayout.LayoutParams parm = new LayoutParams((int) dip2px(size0 * (float) (202.0 / 125.0)), (int) dip2px(size0 * (float) (100.0 / 125.0)));
                parm.addRule(RelativeLayout.ALIGN_PARENT_START);
                parm.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                parm.setMargins(0, (int) dip2px(size0 * (float) (62.0 / 125.0)), 0, 0);
                messageview.setLayoutParams(parm);
                LayoutParams params2 = (LayoutParams) view.getLayoutParams();
                params2.width = (int) (view.getLayoutParams().width * beishu);
                params2.height = (int) (view.getLayoutParams().height * beishu);
                view.setLayoutParams(params2);
                size = size0;
                if (!MessageVis) {
                    viewWidth = animview.getLayoutParams().width;
                    viewHeight = animview.getLayoutParams().height;
                } else {
                    viewWidth = view.getLayoutParams().width;
                    viewHeight = view.getLayoutParams().height;
                }
                MyWindowManager.updateWindow(getContext());
                size = size0;
            }
        }
    };
    private BroadcastReceiver ChangeName = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("ChangeNameListenerService")) {
                PetName = intent.getStringExtra("name");
            }
        }
    };
    private BroadcastReceiver onBluetooth = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("com.bluetooth.visit")) {
                String skin = intent.getStringExtra("VisitPetSkin");
                //String name = intent.getStringExtra("VisitPetName");
                ChangeAttrAnim("bluebooth", skin, "小傻子");
                //ChangeAttrAnim("wechat",skin,name);

            }
        }
    };
    private BroadcastReceiver onBluetoothGo = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("com.bluetooth.back")) {
                ChangeAttrAnim("bluetoothgo", "", "");
            }
        }
    };

    private BroadcastReceiver onChangePet = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("changePetModel")) {
                PetModel currentPet = findPetModelByOriginName(intent.getStringExtra("originalName"));

                // TODO: 更换宠物资源，并刷新FloatWindowView
                // 宠物拼音名可通过
                //currentPet.getOriginalName(); // 获取宠物拼音名
                //currentPet.getName();  // 获取主人给宠物设置的名称
                //currentPet.getAppellation(); // 获取宠物对主人的称呼
                PetSkin = currentPet.getOriginalName();
                PetName = currentPet.getName();
                PetString = currentPet.getAppellation();
                InitAnim();
                ChangeFrameAnim(NowAnimNumber);
            }
        }
    };


    // 通过宠物的拼音名来查找数据库中的宠物信息，返回一个PetModel
    private PetModel findPetModelByOriginName(String oname) {
        List<PetModel> pets = LitePal.findAll(PetModel.class);
        for (PetModel pet : pets) {
            if (pet.getOriginalName().equals(oname)) return pet;
        }
        return null;
    }

    public class AnimManager {
        public AnimationDrawable walk(String petname) {
            AnimationDrawable walkDrawable = new AnimationDrawable();
            switch (petname) {
                case "billd":
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.billd1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.billd2), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.billd1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.billd3), 200);
                    walkDrawable.setOneShot(false);
                    break;
                case "bulaike":
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike2), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike3), 200);
                    walkDrawable.setOneShot(false);
                    break;
                case "ruiersi":
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi2), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi3), 200);
                    walkDrawable.setOneShot(false);
                    break;
                case "dage":
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.dage1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.dage2), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.dage1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.dage3), 200);
                    walkDrawable.setOneShot(false);
                    break;
                case "haimian":
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.haimian1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.haimian2), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.haimian1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.haimian3), 200);
                    walkDrawable.setOneShot(false);
                    break;
                case "jiansan":
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan2), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan3), 200);
                    walkDrawable.setOneShot(false);
                    break;
                case "kaxiusi":
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi2), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi3), 200);
                    walkDrawable.setOneShot(false);
                    break;
                case "pikaqiu":
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu2), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu3), 200);
                    walkDrawable.setOneShot(false);
                    break;
                default:
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin2), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin1), 200);
                    walkDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin3), 200);
                    walkDrawable.setOneShot(false);
                    break;
            }
            return walkDrawable;
        }

        public AnimationDrawable sit(String petname) {
            AnimationDrawable sitDrawable = new AnimationDrawable();
            switch (petname) {
                case "billd":
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.billd15), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.billd16), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.billd17), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.billd11), 400);
                    sitDrawable.setOneShot(true);
                    break;
                case "bulaike":
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike15), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike16), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike17), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike11), 400);
                    sitDrawable.setOneShot(true);
                    break;
                case "ruiersi":
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi15), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi16), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi17), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi11), 400);
                    sitDrawable.setOneShot(true);
                    break;
                case "dage":
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.dage15), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.dage16), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.dage17), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.dage11), 400);
                    sitDrawable.setOneShot(true);
                    break;
                case "haimian":
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.haimian15), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.haimian16), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.haimian17), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.haimian11), 400);
                    sitDrawable.setOneShot(true);
                    break;
                case "jiansan":
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan15), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan16), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan17), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan11), 400);
                    sitDrawable.setOneShot(true);
                    break;
                case "kaxiusi":
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi15), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi16), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi17), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi11), 400);
                    sitDrawable.setOneShot(true);
                    break;
                case "pikaqiu":
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu15), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu16), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu17), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu11), 400);
                    sitDrawable.setOneShot(true);
                    break;
                default:
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin15), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin16), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin17), 400);
                    sitDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin11), 400);
                    sitDrawable.setOneShot(true);
                    break;
            }
            return sitDrawable;
        }

        public AnimationDrawable message(String petname) {
            AnimationDrawable messageDrawable = new AnimationDrawable();
            switch (petname) {
                case "billd":
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.billd34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.billd35), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.billd34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.billd36), 200);
                    messageDrawable.setOneShot(false);
                    break;
                case "bulaike":
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike35), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike36), 200);
                    messageDrawable.setOneShot(false);
                    break;
                case "ruiersi":
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi35), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi36), 200);
                    messageDrawable.setOneShot(false);
                    break;
                case "dage":
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.dage34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.dage35), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.dage34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.dage36), 200);
                    messageDrawable.setOneShot(false);
                    break;
                case "haimian":
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.haimian34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.haimian35), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.haimian34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.haimian36), 200);
                    messageDrawable.setOneShot(false);
                    break;
                case "jiansan":
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan35), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan36), 200);
                    messageDrawable.setOneShot(false);
                    break;
                case "kaxiusi":
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi35), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi36), 200);
                    messageDrawable.setOneShot(false);
                    break;
                case "pikaqiu":
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu35), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu36), 200);
                    messageDrawable.setOneShot(false);
                    break;
                default:
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin35), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin34), 200);
                    messageDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin36), 200);
                    messageDrawable.setOneShot(false);
                    break;
            }
            return messageDrawable;
        }

        public AnimationDrawable paqiang(String petname) {
            AnimationDrawable paqiangDrawable = new AnimationDrawable();
            switch (petname) {
                case "billd":
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.billd12), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.billd13), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.billd14), 200);
                    paqiangDrawable.setOneShot(false);
                    break;
                case "bulaike":
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike12), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike13), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike14), 200);
                    paqiangDrawable.setOneShot(false);
                    break;
                case "ruiersi":
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi12), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi13), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi14), 200);
                    paqiangDrawable.setOneShot(false);
                    break;
                case "dage":
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.dage12), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.dage13), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.dage14), 200);
                    paqiangDrawable.setOneShot(false);
                    break;
                case "haimian":
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.haimian12), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.haimian13), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.haimian14), 200);
                    paqiangDrawable.setOneShot(false);
                    break;
                case "jiansan":
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan12), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan13), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan14), 200);
                    paqiangDrawable.setOneShot(false);
                    break;
                case "kaxiusi":
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi12), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi13), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi14), 200);
                    paqiangDrawable.setOneShot(false);
                    break;
                case "pikaqiu":
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu12), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu13), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu14), 200);
                    paqiangDrawable.setOneShot(false);
                    break;
                default:
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin12), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin13), 200);
                    paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin14), 200);
                    paqiangDrawable.setOneShot(false);
                    break;
            }
            return paqiangDrawable;
        }

        public AnimationDrawable padi(String petname) {
            AnimationDrawable padiDrawable = new AnimationDrawable();
            switch (petname) {
                case "billd":
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.billd20), 500);
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.billd21), 500);
                    padiDrawable.setOneShot(false);
                    break;
                case "bulaike":
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike20), 500);
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike21), 500);
                    padiDrawable.setOneShot(false);
                    break;
                case "ruiersi":
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi20), 500);
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi21), 500);
                    padiDrawable.setOneShot(false);
                    break;
                case "dage":
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.dage20), 500);
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.dage21), 500);
                    padiDrawable.setOneShot(false);
                    break;
                case "haimian":
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.haimian20), 500);
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.haimian21), 500);
                    padiDrawable.setOneShot(false);
                    break;
                case "jiansan":
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan20), 500);
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan21), 500);
                    padiDrawable.setOneShot(false);
                    break;
                case "kaxiusi":
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi20), 500);
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi21), 500);
                    padiDrawable.setOneShot(false);
                    break;
                case "pikaqiu":
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu20), 500);
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu21), 500);
                    padiDrawable.setOneShot(false);
                    break;
                default:
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin20), 500);
                    padiDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin21), 500);
                    padiDrawable.setOneShot(false);
                    break;
            }
            return padiDrawable;
        }

        public AnimationDrawable sit2(String petname) {
            AnimationDrawable sit2Drawable = new AnimationDrawable();
            switch (petname) {
                case "billd":
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.billd31), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.billd32), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.billd33), 200);
                    sit2Drawable.setOneShot(false);
                    break;
                case "bulaike":
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.bulaike31), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.bulaike32), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.bulaike33), 200);
                    sit2Drawable.setOneShot(false);
                    break;
                case "ruiersi":
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.ruiersi31), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.ruiersi32), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.ruiersi33), 200);
                    sit2Drawable.setOneShot(false);
                    break;
                case "dage":
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.dage31), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.dage32), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.dage33), 200);
                    sit2Drawable.setOneShot(false);
                    break;
                case "haimian":
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.haimian31), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.haimian32), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.haimian33), 200);
                    sit2Drawable.setOneShot(false);
                    break;
                case "jiansan":
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.jiansan31), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.jiansan32), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.jiansan33), 200);
                    sit2Drawable.setOneShot(false);
                    break;
                case "kaxiusi":
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi31), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi32), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi33), 200);
                    sit2Drawable.setOneShot(false);
                    break;
                case "pikaqiu":
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu31), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu32), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu33), 200);
                    sit2Drawable.setOneShot(false);
                    break;
                default:
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.chuyin31), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.chuyin32), 200);
                    sit2Drawable.addFrame(getResources().getDrawable(R.drawable.chuyin33), 200);
                    sit2Drawable.setOneShot(false);
                    break;
            }
            return sit2Drawable;
        }

        public AnimationDrawable differ(String petname) {
            AnimationDrawable differDrawable = new AnimationDrawable();
            switch (petname) {
                case "billd":
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.billd38), 500);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.billd39), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.billd40), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.billd41), 300);
                    differDrawable.setOneShot(true);
                    break;
                case "bulaike":
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike38), 500);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike39), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike40), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike41), 300);
                    differDrawable.setOneShot(true);
                    break;
                case "ruiersi":
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi38), 500);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi39), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi40), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi41), 300);
                    differDrawable.setOneShot(true);
                    break;
                case "dage":
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.dage38), 500);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.dage39), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.dage40), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.dage41), 300);
                    differDrawable.setOneShot(true);
                    break;
                case "haimian":
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.haimian38), 500);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.haimian39), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.haimian40), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.haimian41), 300);
                    differDrawable.setOneShot(true);
                    break;
                case "jiansan":
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan38), 500);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan39), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan40), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan41), 300);
                    differDrawable.setOneShot(true);
                    break;
                case "kaxiusi":
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi38), 500);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi39), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi40), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi41), 300);
                    differDrawable.setOneShot(true);
                    break;
                case "pikaqiu":
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu38), 500);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu39), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu40), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu41), 300);
                    differDrawable.setOneShot(true);
                    break;
                default:
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin38), 500);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin39), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin40), 150);
                    differDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin41), 300);
                    differDrawable.setOneShot(true);
                    break;
            }
            return differDrawable;
        }

        public AnimationDrawable wechat(String petname) {
            AnimationDrawable wechatDrawable = new AnimationDrawable();
            switch (petname) {
                case "billd":
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.billd18), 400);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.billd19), 500);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.billd34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.billd35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.billd34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.billd36), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.billd34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.billd35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.billd34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.billd36), 200);
                    wechatDrawable.setOneShot(true);
                    break;
                case "bulaike":
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike18), 400);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike19), 500);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike36), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.bulaike36), 200);
                    wechatDrawable.setOneShot(true);
                    break;
                case "ruiersi":
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi18), 400);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi19), 500);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi36), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.ruiersi36), 200);
                    wechatDrawable.setOneShot(true);
                    break;
                case "dage":
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.dage18), 400);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.dage19), 500);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.dage34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.dage35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.dage34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.dage36), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.dage34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.dage35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.dage34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.dage36), 200);
                    wechatDrawable.setOneShot(true);
                    break;
                case "haimian":
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.haimian18), 400);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.haimian19), 500);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.haimian34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.haimian35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.haimian34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.haimian36), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.haimian34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.haimian35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.haimian34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.haimian36), 200);
                    wechatDrawable.setOneShot(true);
                    break;
                case "jiansan":
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan18), 400);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan19), 500);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan36), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.jiansan36), 200);
                    wechatDrawable.setOneShot(true);
                    break;
                case "kaxiusi":
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi18), 400);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi19), 500);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi36), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.kaxiusi36), 200);
                    wechatDrawable.setOneShot(true);
                    break;
                case "pikaqiu":
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu18), 400);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu19), 500);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu36), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.pikaqiu36), 200);
                    wechatDrawable.setOneShot(true);
                    break;
                default:
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin18), 400);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin19), 500);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin36), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin35), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin34), 200);
                    wechatDrawable.addFrame(getResources().getDrawable(R.drawable.chuyin36), 200);
                    wechatDrawable.setOneShot(true);
                    break;
            }
            return wechatDrawable;
        }
    }
}