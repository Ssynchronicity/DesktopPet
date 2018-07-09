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

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private NewFirstFragment fg1;
    private NewSecondFragment fg2;
    private NewThirdFragment fg3;

    private NoScrollViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private MyFragmentPagerAdapter mAdapter;

    private ImageView first_fragment;
    private ImageView second_fragment;
    private ImageView third_fragment;


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
        initSettingAndDatabase();
        clearChoice();
        setTab(0);

        if (!Settings.canDrawOverlays(getApplicationContext())) {
            Intent intent1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent1.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent1, 100);
        }

        // 打开app时检测petOn是否为true
        boolean isPetOn = sharedPreferences.getBoolean("petOn", true);
        if (isPetOn) {
            Intent startFloatWindow = new Intent(this, FloatWindowService.class);
            startService(startFloatWindow);
        }


    }


    private void initViews() {
        mPager = findViewById(R.id.mPager);
        first_fragment = findViewById(R.id.first_fragment);
        second_fragment = findViewById(R.id.second_fragment);
        third_fragment = findViewById(R.id.third_fragment);
        mPager.setAdapter(mAdapter);

        mPager.setOnPageChangeListener(this);
        first_fragment.setOnClickListener(this);
        second_fragment.setOnClickListener(this);
        third_fragment.setOnClickListener(this);
    }

    private void initViewPager() {
        fragmentsList = new ArrayList<>();
        fg1 = new NewFirstFragment();
        fg2 = new NewSecondFragment();
        fg3 = new NewThirdFragment();
        fragmentsList.add(fg1);
        fragmentsList.add(fg2);
        fragmentsList.add(fg3);
        mAdapter = new MyFragmentPagerAdapter(fManager, fragmentsList);
    }

    private void initSettingAndDatabase() {
        sharedPreferences = getSharedPreferences("pet", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (!sharedPreferences.contains("firstRun")) {
            // LitePal.deleteDatabase("PetAlarm"); // 清空数据库
            // TODO:将可用宠物的数据加入数据库
//            new PetModel(getResources().getString(R.string.pet_name_1), "主人", "None", true, downloadUrl).save();
//            new PetModel(getResources().getString(R.string.pet_name_2), "主人", "None", true, downloadUrl).save();
//            new PetModel(getResources().getString(R.string.pet_name_3), "主人", "None", true, downloadUrl).save();
//            new PetModel(getResources().getString(R.string.pet_name_4), "主人", "None", true, downloadUrl).save();
//            new PetModel(getResources().getString(R.string.pet_name_5), "主人", "None", true, downloadUrl).save();
//            new PetModel(getResources().getString(R.string.pet_name_6), "主人", "None", true, downloadUrl).save();
//            new PetModel(getResources().getString(R.string.pet_name_7), "主人", "None", true, downloadUrl).save();
//            new PetModel(getResources().getString(R.string.pet_name_8), "主人", "None", true, downloadUrl).save();
//            new PetModel(getResources().getString(R.string.pet_name_9), "主人", "None", true, downloadUrl).save();
//            new PetModel(getResources().getString(R.string.pet_name_10), "主人", "None", true, downloadUrl).save();

            editor.putBoolean("firstRun", true);
            editor.putBoolean("petOn", true);
            editor.putBoolean("autoStart", false);
            editor.putInt("petSize", PetNumbers.INITIAL_PET_VIEW_SIZE);
            editor.putLong("currentPetId", 1);
            editor.apply();


        }
    }

    public void clearChoice() {
        first_fragment.setImageResource(R.drawable.pet_light);
        second_fragment.setImageResource(R.drawable.home_light);
        third_fragment.setImageResource(R.drawable.setting_light);
    }

    public void setTab(int num) {
        switch (num) {
            case R.id.first_fragment:
            case 0:
                first_fragment.setImageResource(R.drawable.pet_dark);
                mPager.setCurrentItem(0);
                mPager.setNoScroll(true);
                break;
            case R.id.second_fragment:
            case 1:
                second_fragment.setImageResource(R.drawable.home_dark);
                mPager.setCurrentItem(1);
                mPager.setNoScroll(true);
                break;
            case R.id.third_fragment:
            case 2:
                third_fragment.setImageResource(R.drawable.setting_dark);
                mPager.setCurrentItem(2);
                mPager.setNoScroll(true);
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
        if (arg0 == 2) {
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
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
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
    private void enableNotification() {
        String string = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!string.contains(WeChatNotificationListenerService.class.getName())) {
            new AlertDialog.Builder(MainActivity.this).setTitle("是否开启微信提示")
                    .setMessage("启动微信消息提示功能？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
    }

    //确认是否开启
    private void checkWeChatNotificationRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList == null || serviceList.size() == 0) {
            toggleNotificationListenerService();
            return;
        }
        boolean collectorRunning = false;
        for (ActivityManager.RunningServiceInfo info : serviceList) {
            if (info.service.getClassName().equals(WeChatNotificationListenerService.class.getName()))
                collectorRunning = true;
        }
        if (collectorRunning) return;
        toggleNotificationListenerService();
    }

    private void toggleNotificationListenerService() {
        ComponentName thisComponent = new ComponentName(this, WeChatNotificationListenerService.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
