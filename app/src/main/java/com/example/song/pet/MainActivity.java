package com.example.song.pet;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.example.song.pet.view.NoScrollViewPager;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private FirstFragment fg1;
    private NewSecondFragment fg2;
    private NewThirdFragment fg3;

    private NoScrollViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private MyFragmentPagerAdapter mAdapter;

    private ImageView first_fragment;
    private ImageView second_fragment;
    private ImageView third_fragment;

//    private ImageView image1;
//    private ImageView image2;
//    private ImageView image3;
//
//    private TextView text1;
//    private TextView text2;
//    private TextView text3;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private int Gray = 0xFF999999;
    public FragmentManager fManager;

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableNotification();
        checkWeChatNotificationRunning();

        fManager = getSupportFragmentManager();
        initViewPager();
        initViews();
        initSpAndAlarmDb();
        clearChoice();
        setTab(0);
//        if(!isEnabled()){
//            new AlertDialog.Builder(MainActivity.this).setTitle("是否开启Notification access")//设置对话框标题
//
//                    .setMessage("若想启动微信消息提醒则开启")//设置显示的内容
//
//                    .setPositiveButton("开启", new DialogInterface.OnClickListener() {//添加确定按钮
//
//
//                        @Override
//
//                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
//
//                            // TODO Auto-generated method stub
//
//                            startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
//
//                        }
//
//                    }).setNegativeButton("取消",new DialogInterface.OnClickListener() {//添加返回按钮
//
//
//
//                @Override
//
//                public void onClick(DialogInterface dialog, int which) {//响应事件
//
//                    // TODO Auto-generated method stub
//
//
//                }
//
//            }).show();//在按键响应事件中显示此对话框
//        }
        if(!Settings.canDrawOverlays(getApplicationContext())){
            Intent intent1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent1.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent1,100);
        }

        // 打开app时检测petOn是否为true
        boolean isPetOn = sharedPreferences.getBoolean("petOn", true);
        if (isPetOn) {
            Intent startFloatWindow = new Intent(this, FloatWindowService.class);
            startService(startFloatWindow);
        }

//        startFloatWindow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
//                startService(intent);
//                finish();
//            }
//        });
    }


    private void initViews() {
        mPager = findViewById(R.id.mPager);
        first_fragment = findViewById(R.id.first_fragment);
        second_fragment = findViewById(R.id.second_fragment);
        third_fragment = findViewById(R.id.third_fragment);
        mPager.setAdapter(mAdapter);

//        mPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return true;
//            }
//        });
        mPager.setOnPageChangeListener(this);
        first_fragment.setOnClickListener(this);
        second_fragment.setOnClickListener(this);
        third_fragment.setOnClickListener(this);
    }

    private void initViewPager()
    {
        fragmentsList = new ArrayList<>();
        fg1 = new FirstFragment();
//        fg2 = new SecondFragment();
        fg2 = new NewSecondFragment();
        fg3 = new NewThirdFragment();
        fragmentsList.add(fg1);
        fragmentsList.add(fg2);
        fragmentsList.add(fg3);
        mAdapter = new MyFragmentPagerAdapter(fManager,fragmentsList);
    }

    private void initSpAndAlarmDb() {
        sharedPreferences = getSharedPreferences("pet", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

//        //第一次登陆时的初始化
//        if(!sharedPreferences.contains("times")){
//            editor.putInt("times",1);
//            editor.putString("name1", "皮卡");
//            editor.putString("name2","鳄鱼");
//            editor.putBoolean("isSecondUnlock",false);
//            editor.putBoolean("isFirstOn",false);
//            editor.putBoolean("isSecondOn",false);
//            //设置界面的初始化
//            editor.putBoolean("show", true);
//            editor.putBoolean("always", true);
//            editor.putBoolean("on",false);
//            editor.putBoolean("set",true);
//            editor.putBoolean("time",false);
//            editor.commit();
//        }
        if (!sharedPreferences.contains("firstRun")) {
            editor.putBoolean("firstRun", true);
            editor.putBoolean("petOn", true);
            editor.putBoolean("autoStart", false);
            editor.putInt("petSize", PetNumbers.INITIAL_PET_VIEW_SIZE);
            editor.apply();
            LitePal.deleteDatabase("PetAlarm"); // 清空数据库

//            for (int i = 0; i < 3; i++) {
//                PetAlarmModel alarm = new PetAlarmModel();
//                alarm.setTitle("Item " + i);
//                alarm.setHour(23);
//                alarm.setMinute(59);
//                alarm.save();
//            }
        }
    }

    public void clearChoice()
    {
        first_fragment.setImageResource(R.drawable.pet_light);
        second_fragment.setImageResource(R.drawable.home_light);
        third_fragment.setImageResource(R.drawable.setting_light);
    }
    public void setTab(int num)
    {
        switch (num) {
            case R.id.first_fragment:case 0:
                first_fragment.setImageResource(R.drawable.pet_dark);
                mPager.setCurrentItem(0);
                mPager.setNoScroll(false);
                break;
            case R.id.second_fragment:case 1:
                second_fragment.setImageResource(R.drawable.home_dark);
                mPager.setCurrentItem(1);
                mPager.setNoScroll(true);
                break;
            case R.id.third_fragment:case 2:
                third_fragment.setImageResource(R.drawable.setting_dark);
                mPager.setCurrentItem(2);
                mPager.setNoScroll(false);
                break;
            default:
                break;
        }

    }


    @Override
    public void onClick(View v) {
        clearChoice();
        setTab(v.getId());
    }
    @Override
    public void onPageScrollStateChanged(int arg0) {
        if(arg0 == 2)
        {
            int i = mPager.getCurrentItem();
            clearChoice();
            setTab(i);
        }
    }
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }
    @Override
    public void onPageSelected(int arg0) {

    }

    //判断是否开启了Notification access
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void enableNotification(){
        String string = Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners");
        if (!string.contains(WeChatNotificationListenerService.class.getName())) {
            new AlertDialog.Builder(MainActivity.this).setTitle("是否开启微信提示")
                    .setMessage("启动微信消息提示功能？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                        }
                    })
                    .setNegativeButton("取消",null)
                    .show();
        }
    }

    //确认是否开启
    private void checkWeChatNotificationRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList == null || serviceList.size() == 0){
            toggleNotificationListenerService();
            return;
        }
        boolean collectorRunning = false;
        for (ActivityManager.RunningServiceInfo info : serviceList) {
            if (info.service.getClassName().equals(WeChatNotificationListenerService.class.getName()))
                collectorRunning = true;
        }
        if(collectorRunning) return;
        toggleNotificationListenerService();
    }

    private void toggleNotificationListenerService() {
        ComponentName thisComponent = new ComponentName(this,  WeChatNotificationListenerService.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
