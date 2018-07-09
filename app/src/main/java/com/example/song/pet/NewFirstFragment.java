package com.example.song.pet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.List;

public class NewFirstFragment extends Fragment {
    private FloatingActionButton mEditFab;
    private ImageView mCurrentPet;
    private TextView mPetName;
    private TextView mPetAppellation;
    private TextView mPetBirthday;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private PetModel currentPet;

    @SuppressLint("CommitPrefEdits")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_first, container, false);
        sharedPreferences = getActivity().getSharedPreferences("pet", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        currentPet = findPetModelByOriginName(sharedPreferences.getString("current", "chuyin"));
        init(rootView);
        setListener();
        return rootView;
    }

    private void init(View rootView) {
        mEditFab = rootView.findViewById(R.id.edit_fab);
        mCurrentPet = rootView.findViewById(R.id.currentPetDisplay);
        mPetName = rootView.findViewById(R.id.name);
        mPetAppellation = rootView.findViewById(R.id.appellation);
        mPetBirthday = rootView.findViewById(R.id.birthday);
        updatePetInfo();
    }

    private void setListener() {
        mEditFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditPetActivity.class);
                intent.putExtra("current", currentPet.getOriginalName().toLowerCase());
                intent.putExtra("name", currentPet.getName());
                intent.putExtra("appellation", currentPet.getAppellation());
                startActivity(intent);
            }
        });
    }


    // 通过名称来查找Drawable资源ID，不需要带后缀
    private int getDrawableIdByName(String name) {
        return getResources().getIdentifier(name, "drawable", getActivity().getApplicationInfo().packageName);
    }

    private PetModel findPetModelByOriginName(String oname) {
        List<PetModel> pets = LitePal.findAll(PetModel.class);
        for (PetModel pet : pets) {
            if (pet.getOriginalName().equals(oname)) return pet;
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePetInfo();
    }

    private void updatePetInfo() {
        currentPet = findPetModelByOriginName(sharedPreferences.getString("current", "chuyin"));
        mCurrentPet.setImageResource(getDrawableIdByName(currentPet.getOriginalName().toLowerCase()));
        mPetName.setText(currentPet.getName());
        mPetAppellation.setText(currentPet.getAppellation());
        mPetBirthday.setText(currentPet.getBirthday());
    }
}
