package com.example.trash_scan.appfeatures;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trash_scan.databinding.ActivityTrashBinding;

import com.example.trash_scan.ml.Model1;
import com.example.trash_scan.ml.ModelUnquant;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class TrashActivity extends Fragment {
    private ActivityTrashBinding binding;
    private ActivityResultLauncher<Intent> cameraLuancher;
    private ActivityResultLauncher<String> permissionLauncher;
    private List<String> labels1;
    private List<String> labels2;
    int imageSize = 224;
    private Bitmap bitmap;
    private Boolean cameraPermissionGranted = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityTrashBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraLuancher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getData()  != null) {
                Bitmap bitmap =(Bitmap) result.getData().getExtras().get("data");
                if (bitmap != null) {
                    binding.imageView.setImageBitmap(bitmap);
                    classifyIfBioOrNonBio(bitmap);
                    classifyIfRecycableOrNot(bitmap);
                }
            }
        });
        permissionLauncher =  registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                cameraPermissionGranted = true;
                launchCamera();
            } else {
                Toast.makeText(binding.getRoot().getContext(), "You cannot use camera", Toast.LENGTH_SHORT).show();
            }
        });
        String filename = "labels1.txt";
        String filename2 = "labels.txt";
        try {
            labels1 = FileUtil.loadLabels(view.getContext(),filename);
            labels2 = FileUtil.loadLabels(view.getContext(),filename2);

        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.button.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("Focus the object on your camera.\n" +
                    "No blurred pictures.")
                    .setPositiveButton("Continue", (dialog, which) -> {
                        if (cameraPermissionGranted){
                            launchCamera();
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA);
                        }
                    }).show();
        });
        binding.recycableLinkText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void launchCamera() {

        Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                cameraLuancher.launch(intent);
            }
        } else {
            cameraLuancher.launch(intent);
        }
    }
    private void classifyIfRecycableOrNot(Bitmap bitmap) {
        try {
            ModelUnquant model = ModelUnquant.newInstance(binding.getRoot().getContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());


            int[] intValues = new int[imageSize * imageSize];
            bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());


            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            if (maxConfidence * 100 > 60f){
                String result = labels2.get(maxPos);
                binding.textIsRecycable.setText(result);
                if (result.equals("Recycable")){
                    binding.recycableLinkText.setVisibility(View.VISIBLE);
                } else {
                    binding.recycableLinkText.setVisibility(View.GONE);
                }

            }else {
                binding.textIsRecycable.setText("I didn't catch that");
            }

            StringBuilder s = new StringBuilder();
            for (int i = 0; i < labels2.size(); i++) {
                s.append(String.format("%s: %.1f%%\n", labels2.get(i), confidences[i] * 100));
            }
            binding.textRecycableConfindences.setText(s.toString());
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void classifyIfBioOrNonBio(Bitmap bitmap) {
        try {
            Model1 model1 =  Model1.newInstance(binding.getRoot().getContext());
            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());


            int[] intValues = new int[imageSize * imageSize];
            bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());


            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model1.Outputs outputs = model1.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            if (maxConfidence * 100 > 60f){
               String result = labels1.get(maxPos);
                binding.textWasteType.setText(result);
            }else {
               binding.textWasteType.setText("I didn't catch that");
            }

            String s = "";
            for (int i = 0; i < labels1.size(); i++) {
                s += String.format("%s: %.1f%%\n", labels1.get(i), confidences[i] * 100);
            }
            binding.textWasteTypeConfidences.setText(s);
            // Releases model resources if no longer used.
            model1.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}