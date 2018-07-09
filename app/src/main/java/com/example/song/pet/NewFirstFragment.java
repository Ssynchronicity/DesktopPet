package com.example.song.pet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class NewFirstFragment extends Fragment {
    private FloatingActionButton mEditFab;
    private ImageView mCurrentPet;
    private TextView mPetName;
    private TextView mPetAppellation;
    private TextView mPetBirthday;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_first, container, false);
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
    }

    private void setListener() {

    }
}
