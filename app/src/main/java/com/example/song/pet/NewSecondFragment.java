package com.example.song.pet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.song.pet.view.SwipeListLayout;

import org.litepal.LitePal;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class NewSecondFragment extends Fragment {
    private FloatingActionButton mAddAlarm;
    private ListViewCompat mAlarmListView;
    private AlarmItemAdapter mAlarmItemAdapter;
    private List<PetAlarmModel> mAlarmList;

    class AlarmItemAdapter extends ArrayAdapter<PetAlarmModel> {
        private int resourceId;
        private Set<SwipeListLayout> sets = new HashSet<>();

        class MyViewHolder {
            TextView alarmTime;
            TextView alarmTitle;
            ImageView alarmEdit;
            TextView alarmDelete;
            SwipeListLayout swipeLayout;
        }

        AlarmItemAdapter(@NonNull Context context, int resource, @NonNull List<PetAlarmModel> objects) {
            super(context, resource, objects);
            this.resourceId = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final PetAlarmModel alarm = getItem(position);
            View view;
            final MyViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
                viewHolder = new MyViewHolder();
                viewHolder.alarmTime = view.findViewById(R.id.list_item_time);
                viewHolder.alarmTitle = view.findViewById(R.id.list_item_title);
                viewHolder.alarmEdit = view.findViewById(R.id.list_item_edit);
                viewHolder.alarmDelete = view.findViewById(R.id.list_item_delete);
                viewHolder.swipeLayout = view.findViewById(R.id.alarm_list_item);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (MyViewHolder) view.getTag();
            }

            viewHolder.alarmTime.setText(String.format(Locale.CHINA, "%02d:%02d", alarm.getHour(), alarm.getMinute()));
            viewHolder.alarmTitle.setText(alarm.getTitle());
            viewHolder.swipeLayout.setOnSwipeStatusListener(new MyOnSlipStatusListener(viewHolder.swipeLayout));

            viewHolder.alarmEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 先取消闹钟
                    if (!AlarmEventHelper.cancelAlarm(view.getContext(), alarm.getPid())) {
                        Toast.makeText(view.getContext(), "闹钟删除失败！", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 将数据传入新建闹钟的Activity
                    Intent editAlarmIntent = new Intent(view.getContext(), NewAlarmActivity.class);
                    Bundle alarmValue = new Bundle();
                    alarmValue.putString("title", alarm.getTitle().equals("提醒事项") ? "" : alarm.getTitle());
                    alarmValue.putInt("hour", alarm.getHour());
                    alarmValue.putInt("minute", alarm.getMinute());
                    alarmValue.putLong("id", alarm.getId());
                    editAlarmIntent.putExtras(alarmValue);
                    view.getContext().startActivity(editAlarmIntent);
                }
            });

            viewHolder.alarmDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.swipeLayout.setStatus(SwipeListLayout.Status.Close, true);


                    // 从ListView的数据源中删除该对象
                    mAlarmList.remove(position);

                    // 从数据库中删除该对象
                    LitePal.delete(PetAlarmModel.class, alarm.getId());

                    // 取消对应的闹钟事件
                    if (AlarmEventHelper.cancelAlarm(view.getContext(), alarm.getPid())) {
                        // 删除数据后刷新ListView
                        notifyDataSetChanged();
                        Toast.makeText(view.getContext(), "闹钟删除成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(view.getContext(), "闹钟删除失败！", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return view;
        }


        class MyOnSlipStatusListener implements SwipeListLayout.OnSwipeStatusListener {
            private SwipeListLayout slipListLayout;

            MyOnSlipStatusListener(SwipeListLayout slipListLayout) {
                this.slipListLayout = slipListLayout;
            }

            @Override
            public void onStatusChanged(SwipeListLayout.Status status) {
                if (status == SwipeListLayout.Status.Open) {
                    //若有其他的item的状态为Open，则Close，然后移除
                    if (sets.size() > 0) {
                        for (SwipeListLayout s : sets) {
                            s.setStatus(SwipeListLayout.Status.Close, true);
                            sets.remove(s);
                        }
                    }
                    sets.add(slipListLayout);
                } else {
                    if (sets.contains(slipListLayout))
                        sets.remove(slipListLayout);
                }
            }

            @Override
            public void onStartCloseAnimation() {

            }

            @Override
            public void onStartOpenAnimation() {

            }

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_second_new, container, false);
        initViews(rootView);
        setListener();
        initAlarmListFromDatabase();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initAlarmListFromDatabase();
    }

    private void initViews(View rootView) {
        mAddAlarm = rootView.findViewById(R.id.add_alarm);
        mAlarmListView = rootView.findViewById(R.id.alarm_list);
    }

    private void setListener() {
        mAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewAlarmActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initAlarmListFromDatabase() {
        mAlarmList = LitePal.findAll(PetAlarmModel.class);
        mAlarmItemAdapter = new AlarmItemAdapter(getActivity(), R.layout.alarm_list_item, mAlarmList);
        mAlarmListView.setAdapter(mAlarmItemAdapter);
    }
}
