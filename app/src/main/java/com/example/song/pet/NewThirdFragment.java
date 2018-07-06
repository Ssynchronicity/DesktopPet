package com.example.song.pet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;


public class NewThirdFragment extends Fragment {
    private SwitchCompat isPetOnSwitch, isAutoStartSwitch;
    private boolean isPetOn, isAutoStart;
    private int currentPetSize;
    private ImageView petSizePreview;
    private IndicatorSeekBar changePetSizeSeekBar;
    private RelativeLayout bluetoothSetting;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private LocalBroadcastManager localBroadcastManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_third_new, container, false);
        init(rootView);
        setListener();
        return rootView;
    }

    @SuppressLint("CommitPrefEdits")
    private void init(View rootView) {
        // find all views
        isPetOnSwitch = (SwitchCompat) rootView.findViewById(R.id.isPetOnSwitch);
        isAutoStartSwitch = (SwitchCompat) rootView.findViewById(R.id.isAutoStartSwitch);
        changePetSizeSeekBar = (IndicatorSeekBar) rootView.findViewById(R.id.changePetSizeSeekBar);
        petSizePreview = (ImageView) rootView.findViewById(R.id.petSizePreview);
        bluetoothSetting = (RelativeLayout) rootView.findViewById(R.id.bluetoothSetting);

        preferences = getActivity().getSharedPreferences("pet", Context.MODE_PRIVATE);
        editor = preferences.edit();
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());

        isPetOn = preferences.getBoolean("petOn", true);
        isAutoStart = preferences.getBoolean("autoStart", false);
        currentPetSize = preferences.getInt("petSize", PetNumbers.INITIAL_PET_VIEW_SIZE);
        changePetSizeSeekBar.setProgress(currentPetSize);
        setPetSizePreviewHeight(currentPetSize);

        isPetOnSwitch.setChecked(isPetOn);
        isAutoStartSwitch.setChecked(isAutoStart);
    }

    private void setListener() {
        isPetOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("petOn", b);
                editor.commit();
                Intent handleFloatWindowService = new Intent(getActivity(), FloatWindowService.class);
                if (b) {
                    getActivity().startService(handleFloatWindowService);
                } else {
                    getActivity().stopService(handleFloatWindowService);
                }
            }
        });

        isAutoStartSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("autoStart", b);
                editor.commit();
                // TODO: Handle auto start staff
            }
        });

        bluetoothSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Start bluetooth activity
                Log.i("Click", "Bluetooth entry is clicked.");
            }
        });

        changePetSizeSeekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                setPetSizePreviewHeight(seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                editor.putInt("petSize", seekBar.getProgress());
                editor.commit();

                Intent intent = new Intent("PetSizeChangeListener");
                intent.putExtra("changedSize", seekBar.getProgress());
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    private void setPetSizePreviewHeight(int heightInDp) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) petSizePreview.getLayoutParams();
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());
        petSizePreview.setLayoutParams(params);
    }
}
