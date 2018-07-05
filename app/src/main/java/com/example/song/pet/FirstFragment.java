package com.example.song.pet;

/**
 * Created by Lxr on 2016/4/19.
 */

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import com.example.song.pet.view.PetSelect;


public class FirstFragment extends Fragment {
    ToolbarView first_toolbarview;
    //    private PetSelect first,second,third,fourth;
    private PetSelect first, second;
    private Button miku_temp;//暂时
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    // Download staff block begin //
    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            downloadBinder = (DownloadService.DownloadBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    // Download staff block end //


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);
        first_toolbarview = rootView.findViewById(R.id.first_toolbarview);
        first = rootView.findViewById(R.id.select_1);
        second = rootView.findViewById(R.id.select_2);
        miku_temp = rootView.findViewById(R.id.miku_temp);
//        third = (PetSelect)rootView.findViewById(R.id.select_3);
//        fourth = (PetSelect)rootView.findViewById(R.id.select_4);
        init();

        // Download staff block begin //

        Button startDownload = rootView.findViewById(R.id.start_download);
        Button pauseDownload = rootView.findViewById(R.id.pause_download);
        Button cancelDownload = rootView.findViewById(R.id.cancel_download);

        startDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // downloadBinder是在点击开始下载之后才被初始化的
                if (downloadBinder == null) return;
                String url = getResources().getString(R.string.res_server_root_path) + getResources().getString(R.string.download_test_res);

                downloadBinder.startDownload(url);
            }
        });

        pauseDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (downloadBinder == null) return;
                downloadBinder.pauseDownload();
            }
        });

        cancelDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (downloadBinder == null) return;
                downloadBinder.cancelDownload();
//                getActivity().unbindService(connection);  // 取消下载后解绑服务
            }
        });
        Intent downloadIntent = new Intent(getActivity(), DownloadService.class);
        getActivity().startService(downloadIntent);  // 启动服务
        getActivity().bindService(downloadIntent, connection, Context.BIND_AUTO_CREATE);  // 绑定服务
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请读写存储的权限
            //TODO: 处理申请权限被拒绝的情况，尝试在当前Fragment中override onRequestPermissionRequest但是不可以，有点迷
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }


        // Download staff block end //
        setListener();

        return rootView;
    }

    // Download staff block begin //
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(connection);  // Fragment被销毁时解绑服务
    }
    // Download staff block end //

    @Override
    public void onResume() {
        super.onResume();


        if (!sharedPreferences.getBoolean("isSecondUnlock", false)) {
            second.setPetImg(R.drawable.kong_unlock);
            second.setenabled(false);
        } else {
            second.setName(sharedPreferences.getString("name2", "鳄鱼"));
            second.setCheck(sharedPreferences.getBoolean("isSecondOn", false));
            second.setPetImg(R.drawable.kong);
        }


        first.setName(sharedPreferences.getString("name1", "皮卡"));
        first.setCheck(sharedPreferences.getBoolean("isFirstOn", false));
    }

    private void init() {
        //first_toolbarview.settoolbar_more_Visibility(View.GONE);
        first_toolbarview.settoolbar_relative_Visibility(View.GONE);

        sharedPreferences = getActivity().getSharedPreferences("pet", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

//        third.setenabled(false);
//        fourth.setenabled(false);
        first.setPetImg(R.drawable.pika);

//        third.setPetImg(R.drawable.qiao_unlock);
//        fourth.setPetImg(R.drawable.v_unlock);

    }

    private void setListener() {
        first.setEditListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("flag", "1");
                intent.putExtras(bundle);
                intent.setClass(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });


        second.setEditListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("flag", "2");
                intent.putExtras(bundle);
                intent.setClass(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    //要获取Activity中的资源，就必须等Activity创建完成以后，所以必须放在onActivityCreated()回调函数中
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        miku_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FloatWindowService.class);
                getActivity().startService(intent);
                getActivity().finish();
            }
        });

        first.setSwitchListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    second.setCheck(false);
                    if (!sharedPreferences.getBoolean("isFirstOn", false)) {
                        editor.putBoolean("isFirstOn", true);
                        editor.putBoolean("isSecondOn", false);
                        editor.commit();
                        System.out.println(sharedPreferences.getBoolean("isFirstOn", false));
                        Intent intent = new Intent(getActivity(), FloatWindowService.class);
                        getActivity().startService(intent);
                        getActivity().finish();
                    }
                } else {
                    editor.putBoolean("isFirstOn", false);
                    editor.commit();

                    Intent intent = new Intent(getActivity(), FloatWindowService.class);
                    getActivity().stopService(intent);
                }
            }
        });

        second.setSwitchListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    first.setCheck(false);
                    if (!sharedPreferences.getBoolean("isSecondOn", false)) {
                        editor.putBoolean("isFirstOn", false);
                        editor.putBoolean("isSecondOn", true);
                        editor.commit();
                        Intent intent = new Intent(getActivity(), FloatWindowService.class);
                        getActivity().startService(intent);
                        getActivity().finish();
                    }
                } else {
                    editor.putBoolean("isSecondOn", false);
                    editor.commit();
                    Intent intent = new Intent(getActivity(), FloatWindowService.class);
                    getActivity().stopService(intent);
                }
            }
        });

    }

}
