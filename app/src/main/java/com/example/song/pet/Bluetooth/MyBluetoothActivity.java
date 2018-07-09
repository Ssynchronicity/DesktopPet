package com.example.song.pet.Bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.song.pet.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import android.content.SharedPreferences;

import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;


/**
 * Created by song on 2018/7/9.
 */

public class MyBluetoothActivity extends AppCompatActivity {
    //宠物状态
    private int petState;
    private SharedPreferences sharedPreferences;

    // 蓝牙资源
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // 定义蓝牙打开交互事件
    private static final int REQUEST_ENABLE_BT = 3;
    boolean is_selected_device = false;
    String selected_device;
    Handler timeOutHandler = new Handler();
    Runnable timeOutToast = new Runnable() {
        @Override
        public void run() {
            scan_status.setText("");
            Toast.makeText(MyBluetoothActivity.this, "连接超时（请连接双方在app中打开蓝牙）或该设备无宠物app", Toast.LENGTH_SHORT).show();
            selected_device = null;
            device_view.setEnabled(true);
        }
    };

    // 定义蓝牙会话
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            device_view.setEnabled(true);
                            if(mBluetoothAdapter.isDiscovering())
                                mBluetoothAdapter.cancelDiscovery();
                            deviceAdapter.clear();
                            if(is_selected_device) {
                                deviceAdapter.add(selected_device);
                                is_selected_device = false;
                                mChatService.write(("device&&&" + mBluetoothAdapter.getName()
                                        + "\n" + mBluetoothAdapter.getAddress()).getBytes());
                            }
                            Toast.makeText(MyBluetoothActivity.this, "配对成功", Toast.LENGTH_SHORT).show();
                            timeOutHandler.removeCallbacks(timeOutToast);
                            scan_status.setText("");
                            anim.setEnabled(true);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Log.v("MyDebug", "连接中...");
                            scan_status.setText("连接中...");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:

                    break;
                case Constants.MESSAGE_READ:
                    // ****************按下蓝牙交互按钮后，在这个地方获得对方发送的数据**************************
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if(readMessage.contains("&&&")) {
                        String[] tmp_s = readMessage.split("&&&");
                        String tag = tmp_s[0];
                        String data = tmp_s[1];
                        if (tag.equals("device")) {
                            deviceAdapter.clear();
                            deviceAdapter.add(data);
                            selected_device = data;
                        }else if(tag.equals("scan_click")){
                            selected_device = null;
                            deviceAdapter.clear();
                            anim.setEnabled(false);
                        }
                    }else {
                        String action = readMessage.substring(0, 2);
                        String petInfo = readMessage.substring(2);
                        String[] array = petInfo.split("&&&", 2);
                        String petSkin = array[0];
                        String petName = array[1];
                        switch (action) {
                            case "拜访":
                                //告诉对方拜访成功
                                mChatService.write("拜成".getBytes());
                                //告诉悬浮窗需要显示对方宠物
                                Intent visitIntent = new Intent();
                                visitIntent.putExtra("VisitPetSkin", petSkin);
                                visitIntent.putExtra("VisitPetName", petName);
                                visitIntent.setAction("com.bluetooth.visit");
                                LocalBroadcastManager.getInstance(MyBluetoothActivity.this).sendBroadcast(visitIntent);

                                final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MyBluetoothActivity.this);
                                alertDialog1.setTitle("收到拜访消息提示");
                                alertDialog1.setMessage("宠物"+petName+"过来玩啦~");
                                alertDialog1.setPositiveButton("确定", null);
                                alertDialog1.setIcon(R.drawable.paw);

                                final AlertDialog dlg1 = alertDialog1.create();
                                dlg1.show();

                                final Timer t1 = new Timer();
                                t1.schedule(new TimerTask() {
                                    public void run() {
                                        dlg1.dismiss(); // when the task active then close the dialog
                                        t1.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                                    }
                                }, 2000);
                                break;
                            case "召回":
                                //告诉对方召回成功
                                mChatService.write("召成".getBytes());
                                //告诉悬浮窗需要移除对方宠物
                                Intent backIntent = new Intent();
                                backIntent.putExtra("VisitPetSkin", petSkin);
                                backIntent.putExtra("VisitPetName", petName);
                                backIntent.setAction("com.bluetooth.back");
                                LocalBroadcastManager.getInstance(MyBluetoothActivity.this).sendBroadcast(backIntent);

                                final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(MyBluetoothActivity.this);
                                alertDialog2.setTitle("收到召回消息提示");
                                alertDialog2.setMessage("宠物"+petName+"要走啦~");
                                alertDialog2.setPositiveButton("确定", null);
                                alertDialog2.setIcon(R.drawable.paw);

                                final AlertDialog dlg2 = alertDialog2.create();
                                dlg2.show();

                                final Timer t2 = new Timer();
                                t2.schedule(new TimerTask() {
                                    public void run() {
                                        dlg2.dismiss(); // when the task active then close the dialog
                                        t2.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                                    }
                                }, 2000);
                                break;
                            case "拜成":
                                if(petState == Constants.LEAVING) {
                                    petState = Constants.ARRIVED;
                                    //加载页面完成
                                    //发一个广播移除自己的宠物
                                    String MyPetName = sharedPreferences.getString("currentPet", "");
                                    Intent visitSuccessintent = new Intent();
                                    visitSuccessintent.setAction("com.bluetooth.selfback");
                                    LocalBroadcastManager.getInstance(MyBluetoothActivity.this).sendBroadcast(visitSuccessintent);

                                    final AlertDialog.Builder alertDialog3 = new AlertDialog.Builder(MyBluetoothActivity.this);
                                    alertDialog3.setTitle("拜访成功消息提示");
                                    alertDialog3.setMessage("您的宠物"+MyPetName+"出去玩啦~");
                                    alertDialog3.setPositiveButton("确定", null);
                                    alertDialog3.setIcon(R.drawable.paw);

                                    final AlertDialog dlg3 = alertDialog3.create();
                                    dlg3.show();

                                    final Timer t3 = new Timer();
                                    t3.schedule(new TimerTask() {
                                        public void run() {
                                            dlg3.dismiss(); // when the task active then close the dialog
                                            t3.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                                        }
                                    }, 2000);

                                }
                                break;
                            case "召成":
                                if(petState == Constants.GOBACK) {
                                    petState = Constants.HOME;
                                    //召回页面完成
                                    //发一个广播显示自己的宠物
                                    String MyPetName = sharedPreferences.getString("currentPet", "");
                                    Intent backSuccessintent = new Intent();
                                    backSuccessintent.setAction("com.bluetooth.selfvisit");
                                    LocalBroadcastManager.getInstance(MyBluetoothActivity.this).sendBroadcast(backSuccessintent);

                                    final AlertDialog.Builder alertDialog4 = new AlertDialog.Builder(MyBluetoothActivity.this);
                                    alertDialog4.setTitle("召回成功消息提示");
                                    alertDialog4.setMessage("您的宠物"+MyPetName+"已经回来啦~");
                                    alertDialog4.setPositiveButton("确定", null);
                                    alertDialog4.setIcon(R.drawable.paw);

                                    final AlertDialog dlg4 = alertDialog4.create();
                                    dlg4.show();

                                    final Timer t4 = new Timer();
                                    t4.schedule(new TimerTask() {
                                        public void run() {
                                            dlg4.dismiss(); // when the task active then close the dialog
                                            t4.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                                        }
                                    }, 2000);
                                }
                                break;
                        }
                    }
                    break;
            }
        }
    };

    // 处理搜索蓝牙设备事务
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.v("MyDebug", "蓝牙搜索返回一些事件啦");
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceAdapter.add(device.getName() + "\n" + device.getAddress());
                Log.v("MyDebug", device.getName() + "\n" + device.getAddress());
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                scan_status.setText("");
            }
        }
    };

    // 视图资源
    Switch open;
    Button scan, anim;
    ListView device_view;
    TextView scan_status;

    // 数据资源
    ArrayAdapter<String> deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mybluetooth_activity);

        // 开启搜索蓝牙的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // Only ask for these permissions on runtime when running Android 6.0 or higher
            switch (ContextCompat.checkSelfPermission(getBaseContext(), ACCESS_COARSE_LOCATION)) {
                case PackageManager.PERMISSION_DENIED:
                    ((TextView) new AlertDialog.Builder(this)
                            .setTitle("开启搜索其他手机的权限")
                            .setMessage("您的手机未允许该app搜索蓝牙设备，点击开启")
                            .setNeutralButton("开启搜索", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (ContextCompat.checkSelfPermission(getBaseContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(MyBluetoothActivity.this,
                                                new String[]{ACCESS_COARSE_LOCATION},
                                                1);
                                    }
                                }
                            })
                            .show()
                            .findViewById(android.R.id.message))
                            .setMovementMethod(LinkMovementMethod.getInstance());       // Make the link clickable. Needs to be called after show(), in order to generate hyperlinks
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    break;
//                    Intent intent = new Intent(ACCESS_COARSE_LOCATION);
            }
        }
        // 初始化视图元素
        open = (Switch) findViewById(R.id.bluetooth_open);
        scan = (Button) findViewById(R.id.bluetooth_scan);
        anim = (Button) findViewById(R.id.bluetooth_anim);
        device_view = (ListView) findViewById(R.id.bluetooth_device);
        scan_status = (TextView) findViewById(R.id.bluetooth_scan_status);
        //初始化宠物状态和名字
        petState = Constants.HOME;
        sharedPreferences = (MyBluetoothActivity.this).getSharedPreferences("pet", Context.MODE_PRIVATE);
        // 初始化蓝牙
        initBluetoothConfig();
        // 初始化数据以及注册扫描事件
        initDeviceListAndRegister();
        // ***************************初始化蓝牙交互按钮**************************
        final ArrayAdapter<String> anim_options = new ArrayAdapter<String>(this, R.layout.device_name);
        anim_options.add("拜访");
        anim_options.add("召回");
        anim.setEnabled(false);
        anim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected_device == null){
                    Toast.makeText(MyBluetoothActivity.this, "设备未连接, 请点击上面蓝牙列表连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                DialogPlus dialog = DialogPlus.newDialog(MyBluetoothActivity.this)
                        .setAdapter(anim_options)
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                // 在这里获取每个动作
                                TextView tv = (TextView) view;
                                String message = tv.getText().toString();
                                switch (message) {
                                    case "拜访":
                                        if(petState == Constants.HOME) {
                                            //还未加载一个loading界面
                                            petState = Constants.LEAVING;
                                            // 下面这个函数将会把消息发到另外一个蓝牙设备中, 如果需要可以在执行函数前对字符串进行处理
                                            // 这里需要读取自己宠物名字
                                            String MyPetName = sharedPreferences.getString("currentName", "");
                                            String MyPetSkin = sharedPreferences.getString("current", "");
                                            mChatService.write((message + MyPetSkin + "&&&" + MyPetName).getBytes());
                                        }
                                        break;
                                    case "召回":
                                        if(petState == Constants.ARRIVED) {
                                            //还未加载一个loading界面
                                            petState = Constants.GOBACK;
                                            // 下面这个函数将会把消息发到另外一个蓝牙设备中, 如果需要可以在执行函数前对字符串进行处理
                                            // 这里需要读取自己宠物的名字
                                            String MyPetName = sharedPreferences.getString("currentName", "");
                                            String MyPetSkin = sharedPreferences.getString("current", "");
                                            mChatService.write((message + MyPetSkin + "&&&" + MyPetName).getBytes());
                                        }
                                        break;
                                }
                            }
                        })
                        .create();
                dialog.show();
            }
        });
    }

    private void initDeviceListAndRegister(){
        // 设置展示设备
        deviceAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        device_view.setAdapter(deviceAdapter);
        // 注册点击事件
        device_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBluetoothAdapter.cancelDiscovery();
                device_view.setEnabled(false);
                TextView textView = (TextView) view;
                String text_tmp = textView.getText().toString();
                if(selected_device != null && text_tmp.equals(selected_device)){
                    Toast.makeText(MyBluetoothActivity.this, "设备已连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                is_selected_device = true;
                selected_device = text_tmp;
                String[] addresses = textView.getText().toString().split("\n");
                Log.v("MyDebug", "name and address==>>" + addresses[0] + "==>>" + addresses[1]);
                String address = addresses[1];
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                mChatService.connect(device, true);
                timeOutHandler.postDelayed(timeOutToast, 10000);
            }
        });
        // 注册蓝牙搜索事件
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // 给按钮注册搜索事件
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChatService != null){
                    mChatService.write("scan_click&&&scan_click".getBytes());
                    selected_device = null;
                }
                deviceAdapter.clear();
                anim.setEnabled(false);
                if(mBluetoothAdapter.isDiscovering()){
                    mBluetoothAdapter.cancelDiscovery();
                }
                if(!mBluetoothAdapter.isEnabled()){
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }else {
                    deviceAdapter.clear();
                    scan_status.setText("搜索中...");
                    mBluetoothAdapter.startDiscovery();
                }
            }
        });
    }

    private void initBluetoothConfig(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 如果设备不支持蓝牙
        if (mBluetoothAdapter == null) {
            final Toast disable_toast = Toast.makeText(MyBluetoothActivity.this, "您的设备不支持蓝牙", Toast.LENGTH_SHORT);
            disable_toast.show();
            open.setChecked(false);
            scan.setEnabled(false);
            anim.setEnabled(false);
            open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        open.setChecked(false);
                        disable_toast.show();
                    }
                }
            });
            return;
        }
        // 设置Swith开关能打开蓝牙
        open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                anim.setEnabled(false);
                if(isChecked){
                    if(!mBluetoothAdapter.isEnabled()){
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    }else{
                        mChatService = new BluetoothChatService(MyBluetoothActivity.this, mHandler);
                        mChatService.start();
                    }
                }
                if(!isChecked && mBluetoothAdapter.isEnabled()){
                    deviceAdapter.clear();
                    mBluetoothAdapter.disable();
                }
            }
        });
        // 如果设备已经打开蓝牙了
        if(mBluetoothAdapter.isEnabled()){
            open.setChecked(true);
            scan_status.setText("搜索中...");
            mBluetoothAdapter.startDiscovery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(this, "取消操作", Toast.LENGTH_SHORT).show();
            return;
        }else {
            switch (requestCode) {
                case REQUEST_ENABLE_BT: // 开启蓝牙
                    mChatService = new BluetoothChatService(this, mHandler);
                    mChatService.start();
                    deviceAdapter.clear();
                    scan_status.setText("搜索中...");
                    mBluetoothAdapter.startDiscovery();
                    break;
            }
        }
    }

    // 下面是管理声明周期的函数
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mChatService != null)
            mChatService.stop();
        if(mBluetoothAdapter != null){
            mBluetoothAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

}
