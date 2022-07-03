package com.example.trash_scan.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trash_scan.R;
import com.example.trash_scan.databinding.FragmentInfographicsBinding;


public class InfographicsFragment extends DialogFragment {


    private static final String ARG_PARAM1 = "param1";
    private FragmentInfographicsBinding binding;

    // TODO: Rename and change types of parameters
    private String mParam1;



    public static InfographicsFragment newInstance(String param1) {
        InfographicsFragment fragment = new InfographicsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentInfographicsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
/*
    Card Board
    Glass Bottle 4
    Junk Food Wrapper 1
    Plastic Bottle 6
    Plastic Fork 2
    Plastic Container 1
    Plastic Spoon 3
    Cans 4
*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonBack.setOnClickListener(v -> {
            dismiss();
        });
        switch (mParam1) {
            case "Card Board":
                binding.cardBoard.setVisibility(View.VISIBLE);
                break;
            case "Glass Bottle":
                binding.glass1.setVisibility(View.VISIBLE);
                binding.glass2.setVisibility(View.VISIBLE);
                binding.glass3.setVisibility(View.VISIBLE);
                binding.glass4.setVisibility(View.VISIBLE);
                break;
            case "Junk Food Wrapper":
                binding.junkfood.setVisibility(View.VISIBLE);
                break;
            case "Plastic Bottle":
                binding.plasticBottle1.setVisibility(View.VISIBLE);
                binding.plasticBottle2.setVisibility(View.VISIBLE);
                binding.plasticBottle3.setVisibility(View.VISIBLE);
                binding.plasticBottle4.setVisibility(View.VISIBLE);
                binding.plasticBottle5.setVisibility(View.VISIBLE);
                binding.plasticBottle6.setVisibility(View.VISIBLE);
                break;
            case "Plastic Fork":
                binding.plasticFork.setVisibility(View.VISIBLE);
                binding.plasticFork1.setVisibility(View.VISIBLE);
                break;
            case "Plastic Container":
                binding.plasticGallons.setVisibility(View.VISIBLE);
                break;
            case "Plastic Spoon":
                binding.spoon1.setVisibility(View.VISIBLE);
                binding.spoon2.setVisibility(View.VISIBLE);
                binding.spoon3.setVisibility(View.VISIBLE);
                break;
            case "Cans":
                binding.can1.setVisibility(View.VISIBLE);
                binding.can2.setVisibility(View.VISIBLE);
                binding.can3.setVisibility(View.VISIBLE);
                binding.can4.setVisibility(View.VISIBLE);
                break;
            default:
                binding.cardBoard.setVisibility(View.GONE);
                binding.glass1.setVisibility(View.GONE);
                binding.glass2.setVisibility(View.GONE);
                binding.glass3.setVisibility(View.GONE);
                binding.glass4.setVisibility(View.GONE);
                binding.junkfood.setVisibility(View.GONE);
                binding.plasticBottle1.setVisibility(View.GONE);
                binding.plasticBottle2.setVisibility(View.GONE);
                binding.plasticBottle3.setVisibility(View.GONE);
                binding.plasticBottle4.setVisibility(View.GONE);
                binding.plasticBottle5.setVisibility(View.GONE);
                binding.plasticBottle6.setVisibility(View.GONE);
                binding.plasticFork.setVisibility(View.GONE);
                binding.plasticFork1.setVisibility(View.GONE);
                binding.plasticGallons.setVisibility(View.GONE);
                binding.spoon1.setVisibility(View.GONE);
                binding.spoon2.setVisibility(View.GONE);
                binding.spoon3.setVisibility(View.GONE);
                binding.can1.setVisibility(View.GONE);
                binding.can2.setVisibility(View.GONE);
                binding.can3.setVisibility(View.GONE);
                binding.can4.setVisibility(View.GONE);
                break;
        }
    }
}