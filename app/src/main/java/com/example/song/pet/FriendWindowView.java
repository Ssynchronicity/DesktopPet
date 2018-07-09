package com.example.song.pet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.LocalBroadcastManager;
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

import java.lang.reflect.Field;

public class FriendWindowView extends RelativeLayout {
    public static int friendWidth;
    public static int friendHeight;
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
    public FriendWindowView(Context context) {
        super(context);
        size = 125;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.friend_window,this);
        view = findViewById(R.id.ffloat_window_id);
        messageview = findViewById(R.id.flayouttt);
        titleview = (TextView)findViewById(R.id.ftextview1);
        contextview = (TextView)findViewById(R.id.ftextview2);
        animview = (ImageView)findViewById(R.id.fanimview);
        friendWidth = animview.getLayoutParams().width;
        friendHeight = animview.getLayoutParams().height;
        InitAnim();
        MessageVis = friendWidth == view.getLayoutParams().width;
        PetName = "初音";


        LocalBroadcastManager.getInstance(context).registerReceiver(onBluetooth, new IntentFilter("com.bluetooth.visit"));
        LocalBroadcastManager.getInstance(context).registerReceiver(onBluetoothGo, new IntentFilter("com.bluetooth.back"));
    }
    private void ChangeMessageVisbility(boolean i) {
        int newx = mParams.x;
        int newy = mParams.y;
        if (MessageVis && !i) {
            friendWidth = animview.getLayoutParams().width;
            friendHeight = animview.getLayoutParams().height;
            messageview.setVisibility(View.INVISIBLE);
            MyWindowManager.updateWindow(getContext());
            mParams.x = newx + view.getLayoutParams().width-animview.getLayoutParams().width;
            mParams.y = newy + view.getLayoutParams().height-animview.getLayoutParams().height;
            windowManager.updateViewLayout(this, mParams);
        }
        else if (!MessageVis && i) {
            friendWidth = view.getLayoutParams().width;
            friendHeight = view.getLayoutParams().height;
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
        walkDrawable = new AnimationDrawable();
        walkDrawable.addFrame(getResources().getDrawable(R.drawable.shime1),200);
        walkDrawable.addFrame(getResources().getDrawable(R.drawable.shime2),200);
        walkDrawable.addFrame(getResources().getDrawable(R.drawable.shime1),200);
        walkDrawable.addFrame(getResources().getDrawable(R.drawable.shime3),200);
        walkDrawable.setOneShot(false);
        //2 坐
        sitDrawable = new AnimationDrawable();
        sitDrawable.addFrame(getResources().getDrawable(R.drawable.shime15),400);
        sitDrawable.addFrame(getResources().getDrawable(R.drawable.shime16),400);
        sitDrawable.addFrame(getResources().getDrawable(R.drawable.shime17),400);
        sitDrawable.addFrame(getResources().getDrawable(R.drawable.shime11),400);
        sitDrawable.setOneShot(true);
        //3 举消息框
        messageDrawable = new AnimationDrawable();
        messageDrawable.addFrame(getResources().getDrawable(R.drawable.shime34),200);
        messageDrawable.addFrame(getResources().getDrawable(R.drawable.shime35),200);
        messageDrawable.addFrame(getResources().getDrawable(R.drawable.shime34),200);
        messageDrawable.addFrame(getResources().getDrawable(R.drawable.shime36),200);
        messageDrawable.setOneShot(false);
        //4 爬墙
        paqiangDrawable = new AnimationDrawable();
        paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.shime12),200);
        paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.shime13),200);
        paqiangDrawable.addFrame(getResources().getDrawable(R.drawable.shime14),200);
        paqiangDrawable.setOneShot(false);
        //5 爬地
        padiDrawable = new AnimationDrawable();
        padiDrawable.addFrame(getResources().getDrawable(R.drawable.shime20),500);
        padiDrawable.addFrame(getResources().getDrawable(R.drawable.shime21),500);
        padiDrawable.setOneShot(false);
        //6 坐着晃腿
        sit2Drawable = new AnimationDrawable();
        sit2Drawable.addFrame(getResources().getDrawable(R.drawable.shime31),200);
        sit2Drawable.addFrame(getResources().getDrawable(R.drawable.shime32),200);
        sit2Drawable.addFrame(getResources().getDrawable(R.drawable.shime33),200);
        sit2Drawable.setOneShot(false);
        //7 不同的分裂
        differDrawable = new AnimationDrawable();
        differDrawable.addFrame(getResources().getDrawable(R.drawable.shime38),500);
        differDrawable.addFrame(getResources().getDrawable(R.drawable.shime39),150);
        differDrawable.addFrame(getResources().getDrawable(R.drawable.shime40),150);
        differDrawable.addFrame(getResources().getDrawable(R.drawable.shime41),300);
        differDrawable.setOneShot(true);
        //8 相同的分裂
        sameDrawable = new AnimationDrawable();
        sameDrawable.addFrame(getResources().getDrawable(R.drawable.shime42),200);
        sameDrawable.addFrame(getResources().getDrawable(R.drawable.shime43),200);
        sameDrawable.addFrame(getResources().getDrawable(R.drawable.shime44),200);
        sameDrawable.addFrame(getResources().getDrawable(R.drawable.shime45),200);
        sameDrawable.addFrame(getResources().getDrawable(R.drawable.shime46),200);
        sameDrawable.setOneShot(true);
        //9 先跳再举牌子
        wechatDrawable = new AnimationDrawable();
        wechatDrawable.addFrame(getResources().getDrawable(R.drawable.shime18),400);
        wechatDrawable.addFrame(getResources().getDrawable(R.drawable.shime19),500);
        wechatDrawable.addFrame(getResources().getDrawable(R.drawable.shime34),200);
        wechatDrawable.addFrame(getResources().getDrawable(R.drawable.shime35),200);
        wechatDrawable.addFrame(getResources().getDrawable(R.drawable.shime34),200);
        wechatDrawable.addFrame(getResources().getDrawable(R.drawable.shime36),200);
        wechatDrawable.addFrame(getResources().getDrawable(R.drawable.shime34),200);
        wechatDrawable.addFrame(getResources().getDrawable(R.drawable.shime35),200);
        wechatDrawable.addFrame(getResources().getDrawable(R.drawable.shime34),200);
        wechatDrawable.addFrame(getResources().getDrawable(R.drawable.shime36),200);
        wechatDrawable.setOneShot(true);

    }
    public static float dip2px(float dipValue)
    {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return  (dipValue * scale + 0.5f);
    }
    public void ChangeFrameAnim(int i) {
        switch(NowAnimNumber) {
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
            case 8:
                LayoutParams params = (LayoutParams) animview.getLayoutParams();
                params.width =(int)dip2px(size);
                animview.setLayoutParams(params);
                friendWidth = animview.getLayoutParams().width;
                MyWindowManager.updateWindow(getContext());
                sameDrawable.stop();
                break;
            case 9:
                wechatDrawable.stop();
                break;
            default:
                break;
        }
        switch(i) {
            case 0:
                animview.setBackgroundResource(R.drawable.shime1);
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
            case 8:
                LayoutParams params = (LayoutParams) animview.getLayoutParams();
                params.width =(int)dip2px(size*(float)(165.0/125.0));
                animview.setLayoutParams(params);
                friendWidth = animview.getLayoutParams().width;
                MyWindowManager.updateWindow(getContext());
                animview.setBackground(sameDrawable);
                sameDrawable.start();

                break;
            case 9:
                animview.setBackground(wechatDrawable);
                wechatDrawable.start();
                break;
            case 10:
                animview.setBackgroundResource(R.drawable.shime34);
                break;
            case 11:
                animview.setBackgroundResource(R.drawable.shime4);
                break;
            case 12:
                animview.setBackgroundResource(R.drawable.shime22);
                break;
            default:
                break;
        }
        NowAnimNumber = i;
    }
    public void ChangeAttrAnim(String i, String title, String content) {
        if (NowAttrAnim.equals(i)&& !i.equals("quiet")) {
            titleview.setText(title);
            contextview.setText(content);
        }
        else {
            switch (NowAttrAnim) {
                case "alarm":
                    ChangeMessageVisbility(false);
                    break;
                case "wechat" :
                    LayoutParams parm = new LayoutParams((int)dip2px(size*(float)(202.0/125.0)),(int)dip2px(size*(float)(100.0/125.0)));
                    parm.addRule(RelativeLayout.ALIGN_PARENT_START);
                    parm.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    parm.setMargins(0,(int)dip2px(size*(float)(62.0/125.0)),0,0);
                    messageview.setLayoutParams(parm);
                    LayoutParams parm2 = new LayoutParams((int)dip2px(size),(int)dip2px(size));
                    parm2.addRule(RelativeLayout.ALIGN_PARENT_END);
                    parm2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    parm2.setMargins(0,0,0,0);
                    animview.setLayoutParams(parm2);
                    int newx = mParams.x;
                    ChangeMessageVisbility(false);
                    mParams.x = newx;
                    windowManager.updateViewLayout(this, mParams);
                    break;
                case "bluebooth" :
                    break;
                case "quiet":
                    quietAnim.cancel();
                    break;
                case "comeon":
                    break;
                case "goout":
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
                    ObjectAnimator object_rotate = ObjectAnimator.ofFloat(view,"rotationY",0,30,60,90,120,150,180);
                    object_rotate.setDuration(500);
                    object_rotate.start();
                    float startX = mParams.x;
                    float endX = MyWindowManager.screenWidth - animview.getLayoutParams().width;
                    final float startY = mParams.y;
                    final float endY = (MyWindowManager.screenHeight- getStatusBarHeight())/2;
                    final float startX1 = MyWindowManager.screenWidth - animview.getLayoutParams().width;
                    final float endX1 = dip2px(size*(float)(50.0/125.0));
                    ValueAnimator runAnim = ValueAnimator.ofObject(new animEvaluator(), startX, endX);
                    runAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mParams.x = (int) (float) animation.getAnimatedValue();
                            windowManager.updateViewLayout(FriendWindowView.this, mParams);
                        }
                    });
                    runAnim.setStartDelay(500);
                    runAnim.setDuration(1000);
                    runAnim.setInterpolator(new LinearInterpolator());
                    runAnim.start();
                    runAnim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation, boolean isReverse) {
                            ObjectAnimator object_rotate1 = ObjectAnimator.ofFloat(view,"rotationY",180,150,120,90,60,30,0);
                            object_rotate1.setDuration(500);
                            object_rotate1.start();
                            ValueAnimator runAnim1 = ValueAnimator.ofObject(new animEvaluator(),startY,endY);
                            runAnim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    mParams.y = (int) (float) animation.getAnimatedValue();
                                    windowManager.updateViewLayout(FriendWindowView.this, mParams);
                                }
                            });
                            runAnim1.setStartDelay(500);
                            runAnim1.setDuration(1000);
                            runAnim1.start();
                            runAnim1.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation, boolean isReverse) {
                                    if(startY < endY) ChangeFrameAnim(11);
                                    else ChangeFrameAnim(12);
                                }
                                @Override
                                public void onAnimationEnd(Animator animation, boolean isReverse) {
                                    ChangeFrameAnim(9);
                                    ChangeMessageVisbility(true);
                                    ValueAnimator runAnim2 = ValueAnimator.ofObject(new animEvaluator(),startX1,endX1);
                                    runAnim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            mParams.x = (int) (float) animation.getAnimatedValue();
                                            windowManager.updateViewLayout(FriendWindowView.this, mParams);
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
                    LayoutParams parm = new LayoutParams((int)dip2px(size*(float)(202.0/125.0)),(int)dip2px(size*(float)(100.0/125.0)));
                    parm.addRule(RelativeLayout.ALIGN_PARENT_START);
                    parm.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    messageview.setLayoutParams(parm);
                    ChangeMessageVisbility(true);
                    ChangeFrameAnim(4);
                    TranslateAnimation animAlarm1 = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF,0,
                            Animation.RELATIVE_TO_SELF,0-animview.getLayoutParams().height/2);
                    animAlarm1.setDuration(1000);
                    animAlarm1.setInterpolator(new LinearInterpolator());
                    animview.startAnimation(animAlarm1);
                    animAlarm1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) { }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            LayoutParams parm1 = new LayoutParams((int)dip2px(size),(int)dip2px(size));
                            parm1.addRule(RelativeLayout.ALIGN_PARENT_END);
                            parm1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                            parm1.setMargins(0,0,(int)dip2px(size*(float)(30.0/125.0)),0);
                            animview.setLayoutParams(parm1);
                            ChangeFrameAnim(5);
                            TranslateAnimation animAlarm2= new TranslateAnimation(
                                    Animation.RELATIVE_TO_SELF,0-dip2px(size*(float)(110.0/125.0)),
                                    Animation.RELATIVE_TO_SELF,0);
                            animAlarm2.setDuration(5000);
                            animAlarm2.setInterpolator(new LinearInterpolator());
                            animview.startAnimation(animAlarm2);
                            animAlarm2.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) { }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    LayoutParams parm2 = new LayoutParams((int)dip2px(size),(int)dip2px(size));
                                    parm2.addRule(RelativeLayout.ALIGN_PARENT_START);
                                    parm2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                    parm2.setMargins(0,(int)dip2px(size*(float)(17.0/125.0)),0,0);
                                    animview.setLayoutParams(parm2);
                                    ChangeFrameAnim(6);
                                    isWechat=false;
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) { }
                            });
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) { }
                    });
                    break;
                case "quiet":
                    ChangeMessageVisbility(false);
                    ChangeFrameAnim(2);
                    if(mParams.x < MyWindowManager.screenWidth-mParams.x) {
                        float startX2 = mParams.x;
                        float endX2 = 0;
                        quietAnim = ValueAnimator.ofObject(new animEvaluator(), startX2, endX2);
                        quietAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                mParams.x = (int) (float) animation.getAnimatedValue();
                                windowManager.updateViewLayout(FriendWindowView.this, mParams);
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
                                animview.setBackgroundResource(R.drawable.shime9);
                            }
                        });
                    }
                    else {
                        float startX2 = mParams.x;
                        float endX2 = MyWindowManager.screenWidth-animview.getLayoutParams().width;
                        quietAnim = ValueAnimator.ofObject(new animEvaluator(), startX2, endX2);
                        quietAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                mParams.x = (int) (float) animation.getAnimatedValue();
                                windowManager.updateViewLayout(FriendWindowView.this, mParams);
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
                                animview.setBackgroundResource(R.drawable.shime10);
                            }
                        });
                    }
                    break;
                case "comeon":
                    ChangeMessageVisbility(false);
                    break;
                case "goout":
                    ChangeMessageVisbility(false);
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
                float movex = (xInScreen - xInView -mParams.x)/MyWindowManager.screenWidth;
                if (movex<0) {
                    animview.setBackgroundResource(R.drawable.shime8);
                }
                else {
                    animview.setBackgroundResource(R.drawable.shime7);
                }
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                ChangeAttrAnim("quiet","","");
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

    private BroadcastReceiver onBluetooth = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("com.bluetooth.selfvisit")) {
                PetName = intent.getStringExtra("VisitPetName");
                ChangeAttrAnim("bluebooth", "", "");
            }
        }
    };
    private BroadcastReceiver onBluetoothGo = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("com.bluetooth.selfback")) {
                PetName = intent.getStringExtra("BackPetName");
                ChangeAttrAnim("bluetoothgo", "", "");
            }
        }
    };
}