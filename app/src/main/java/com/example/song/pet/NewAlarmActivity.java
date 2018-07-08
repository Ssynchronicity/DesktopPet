package com.example.song.pet;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.TimeZone;

public class NewAlarmActivity extends AppCompatActivity {
    private EditText mAlarmTitleInput;
    private FloatingActionButton mSetDoneBtn;
    private ImageView mBackBtn;
    private TimePicker mTimePicker;
    private long currentAlarmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        init();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mTimePicker.setHour(bundle.getInt("hour"));
            mTimePicker.setMinute(bundle.getInt("minute"));
            mAlarmTitleInput.setText(bundle.getString("title"));
            currentAlarmId = bundle.getLong("id");
        } else {
            Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
            mTimePicker.setMinute(now.get(Calendar.MINUTE));
            mTimePicker.setHour(now.get(Calendar.HOUR_OF_DAY));
        }
        setListener(bundle != null);
    }


    private void init() {
        mAlarmTitleInput = findViewById(R.id.alarm_title_input);
        mSetDoneBtn = findViewById(R.id.done);
        mBackBtn = findViewById(R.id.add_alarm_back);
        mTimePicker = findViewById(R.id.time_picker);
    }

    private void setListener(final boolean isEditMode) {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewAlarmActivity.this.finish();
            }
        });

        mSetDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mAlarmTitleInput.getText().toString().trim();
                if (title.equals("")) title = "提醒事项";


                // 储存到数据库
                PetAlarmModel alarm;
                // 新建闹钟
                if (!isEditMode) {
                    alarm = new PetAlarmModel();
                }
                // 编辑现有的闹钟
                else {
                    alarm = LitePal.find(PetAlarmModel.class, currentAlarmId);
                }

                alarm.setTitle(title);
                alarm.setHour(mTimePicker.getHour());  // getHour()返回的是24小时制下的小时
                alarm.setMinute(mTimePicker.getMinute());
                alarm.setPid((int) (System.currentTimeMillis() % Integer.MAX_VALUE));  // 将pid设置为系统的当前毫秒数 % max_int，保证唯一性
                alarm.save();

                // 注册闹钟事件
                if (!AlarmEventHelper.registerAlarm(NewAlarmActivity.this, alarm)) {
                    Toast.makeText(NewAlarmActivity.this, "闹钟设置失败！", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(NewAlarmActivity.this, "闹钟设置成功！", Toast.LENGTH_SHORT).show();

                NewAlarmActivity.this.finish();
            }
        });
    }
}
