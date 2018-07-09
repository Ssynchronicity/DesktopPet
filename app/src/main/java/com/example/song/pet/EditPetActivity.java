package com.example.song.pet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditPetActivity extends AppCompatActivity {

    private EditText mNameInput;
    private EditText mAppellationInput;
    private ImageView mBack;
    private ImageView mDone;
    //    private ListViewCompat mPetListView;
//    private List<PetModel> mPetList;
    private PileLayout mPetSelectSlider;

    private List<String> petModelName;

    private String currentPetOriginalName;
//    private boolean selectedBorderSeted = false;


    private class ViewHolder {
        public ImageView mPetImage;
        public String mPetOriginalName;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);
        initPetModelList();
        init();
        mNameInput.setText(getIntent().getStringExtra("name"));
        mAppellationInput.setText(getIntent().getStringExtra("appellation"));
        currentPetOriginalName = getIntent().getStringExtra("current");
        setListener();
    }

    private void initPetModelList() {
        petModelName = new ArrayList<>();
        String s = "Billd BuLaiKe ChuYin DaGe HaiMian JianSan KaXiuSi PiKaQiu RuiErSi";
        String[] names = s.split("\\s+");
        Collections.addAll(petModelName, names);
    }


    private void init() {
        mNameInput = findViewById(R.id.edit_name_input);
        mAppellationInput = findViewById(R.id.edit_appellation_input);
        mBack = findViewById(R.id.back);
        mDone = findViewById(R.id.done);
        mPetSelectSlider = findViewById(R.id.select_pet_slider);
        mPetSelectSlider.setAdapter(new PileLayout.Adapter() {
            @Override
            public int getLayoutId() {
                return R.layout.pet_select_item;
            }

            @Override
            public void bindView(View view, int position) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                if (viewHolder == null) {
                    viewHolder = new ViewHolder();
                    viewHolder.mPetImage = (ImageView) view.findViewById(R.id.pet_img);
//                    viewHolder.mPetOriginalName = petModelName.get(position);
                    view.setTag(viewHolder);
                    Log.i("Border", String.format("position: %d   name: %s", position, petModelName.get(position).toLowerCase()));
                }
                // recycled view bind new position
                viewHolder.mPetImage.setImageResource(getDrawableIdByName(petModelName.get(position).toLowerCase()));
//                if (!selectedBorderSeted && viewHolder.mPetOriginalName.toLowerCase().equals(currentPetOriginalName)) {
//                    viewHolder.mPetImage.setBackgroundResource(R.drawable.radius_corner);
//                    selectedBorderSeted = true;
////                    Log.i("Border", String.format("position: %d   name: %s", position, petModelName.get(position).toLowerCase()));
//                }
            }

            @Override
            public int getItemCount() {
                return petModelName.size();
            }


            @Override
            public void onItemClick(View view, int position) {
                // 更新UI
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                if (viewHolder == null) {
                    viewHolder = new ViewHolder();
                    viewHolder.mPetImage = (ImageView) view.findViewById(R.id.pet_img);
                    view.setTag(viewHolder);
                    Log.i("Border", String.format("position: %d   name: %s", position, petModelName.get(position).toLowerCase()));
                }
//                viewHolder.mPetImage.setBackgroundResource(R.drawable.radius_corner_blue);

                PetModel selectedPet = findPetModelByOriginName(petModelName.get(position).toLowerCase());
                currentPetOriginalName = petModelName.get(position).toLowerCase();
                mAppellationInput.setText(selectedPet.getAppellation());
                mNameInput.setText(selectedPet.getName());

            }
        });
    }

    private void setListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditPetActivity.this.finish();
            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PetModel currentPet = findPetModelByOriginName(currentPetOriginalName);
                String appellation = mAppellationInput.getText().toString();
                String name = mNameInput.getText().toString();
                if (appellation.trim().equals("")) {
                    Toast.makeText(EditPetActivity.this, "宠物还不知道怎么称呼你呢~", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (name.trim().equals("")) {
                    Toast.makeText(EditPetActivity.this, "宠物需要一个名字~", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentPet.setAppellation(appellation);
                currentPet.setName(name);
                currentPet.save();

                SharedPreferences.Editor editor = getSharedPreferences("pet", Context.MODE_PRIVATE).edit();
                editor.putString("current", currentPet.getOriginalName());   // 当前使用的宠物模型的名字，全小写字母
                editor.putString("currentAppellation", currentPet.getAppellation());  // 当前宠物对主人的称呼，默认是“主人”
                editor.commit();

                // 发送广播更改悬浮窗的宠物模型
                Intent intent = new Intent("changePetModel");
                intent.putExtra("originalName", currentPet.getOriginalName());
                LocalBroadcastManager.getInstance(EditPetActivity.this).sendBroadcast(intent);

                Toast.makeText(EditPetActivity.this, "宠物信息修改成功！", Toast.LENGTH_SHORT).show();
                EditPetActivity.this.finish();
            }
        });
    }


    private PetModel findPetModelByOriginName(String oname) {
        List<PetModel> pets = LitePal.findAll(PetModel.class);
        for (PetModel pet : pets) {
            if (pet.getOriginalName().equals(oname)) return pet;
        }
        return null;
    }

    private int getDrawableIdByName(String name) {
        return getResources().getIdentifier(name, "drawable", getApplicationInfo().packageName);
    }

}
