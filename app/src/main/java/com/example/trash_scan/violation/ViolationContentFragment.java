package com.example.trash_scan.violation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.trash_scan.R;
import com.example.trash_scan.databinding.FragmentViolationContentBinding;


public class ViolationContentFragment extends DialogFragment {
    private static final String ARG_POSITION = "position";
    private static final String ARG_TITLE= "title";
    private int mPosition;
    private String mTitle;

    private FragmentViolationContentBinding binding;


    public static ViolationContentFragment newInstance(int position,String title) {
        ViolationContentFragment fragment = new ViolationContentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_POSITION);
            mTitle = getArguments().getString(ARG_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentViolationContentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonBack.setOnClickListener(v -> {
           dismiss();
        });
        binding.textTitle.setText(mTitle);
        switch (mPosition) {
            case 0:
                addImage(R.drawable.violation);
                break;
            case 1:
                addImage(R.drawable.violation);
                addImage(R.drawable.violation);
            break;
            default:
                break;
        }
    }

    //add violation infographics dynamically
    private void addImage(int image){
        View view = LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.row_image,binding.getRoot(),false);
        ImageView imageViolation  = view.findViewById(R.id.imageViolation);
        imageViolation.setBackgroundResource(image);
        binding.layoutViolationPictures.addView(view);
    }
}