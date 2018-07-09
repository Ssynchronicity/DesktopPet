package com.example.song.pet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class EditPetActivity extends AppCompatActivity {
//    class PetItemAdapter extends ArrayAdapter<PetModel> {
//        private int resourceId;
//
//        class MyViewHolder {
//            TextView petName;
//            ImageView petImage;
//            AnimDownloadProgressButton petResDownloadBtn;
//        }
//
//        PetItemAdapter(@NonNull Context context, int resource, @NonNull List<PetModel> objects) {
//            super(context, resource, objects);
//            this.resourceId = resource;
//        }
//
//        @NonNull
//        @Override
//        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            final PetModel pet = getItem(position);
//            View view;
//            final MyViewHolder viewHolder;
//            if (convertView == null) {
//                view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
//                viewHolder = new MyViewHolder();
//                viewHolder.petName = view.findViewById(R.id.list_item_time);
//                viewHolder.petImage = view.findViewById(R.id.list_item_title);
//                viewHolder.petResDownloadBtn = view.findViewById(R.id.list_item_edit);
//                view.setTag(viewHolder);
//            } else {
//                view = convertView;
//                viewHolder = (MyViewHolder) view.getTag();
//            }
//
//
//
//            return view;
//        }
//
//
//    }

    private EditText mNameInput;
    private EditText mAppellationInput;
    private ImageView mBack;
    private ImageView mDone;
    //    private ListViewCompat mPetListView;
//    private List<PetModel> mPetList;
    private PileLayout mPetSelectSlider;

    private List<String> petModelName;


    private class ViewHolder {
        public ImageView mPetImage;
        public TextView mPetName;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);
        Bundle petData = this.getIntent().getExtras();
        initPetModelList();
        init();
        setListener();
    }

    private void initPetModelList() {
        String s = "Billd BuLaiKe ChuYin DaGe HaiMian JianSan KaXiuSi PiKaQiu RuiErSi";
        String[] names = s.split("\\s+");
        Collections.addAll(petModelName, names);
    }


    private void init() {


        mNameInput = findViewById(R.id.edit_name_input);
        mAppellationInput = findViewById(R.id.edit_appellation_input);
        mBack = findViewById(R.id.back);
        mDone = findViewById(R.id.done);
//        mPetListView = findViewById(R.id.pet_select_list);
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
                    viewHolder.mPetName = view.findViewById(R.id.pet_name);
                    view.setTag(viewHolder);
                }
                // recycled view bind new position
                viewHolder.mPetImage.setImageResource(getDrawableIdByName(petModelName.get(position) + ".png"));
                viewHolder.mPetName.setText(petModelName.get(position));
            }

            @Override
            public int getItemCount() {
                return petModelName.size();
            }

            @Override
            public void displaying(int position) {
                // right displaying the left biggest itemView's position
            }

            @Override
            public void onItemClick(View view, int position) {
                // on item click
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
    }

    public int getDrawableIdByName(String name) {
        return getResources().getIdentifier(name, "drawable", getApplicationInfo().packageName);
    }

}
