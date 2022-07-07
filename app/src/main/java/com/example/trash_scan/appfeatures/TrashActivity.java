package com.example.trash_scan.appfeatures;



import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;

import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trash_scan.R;
import com.example.trash_scan.adapter.JunkShopsWasteAdapter;
import com.example.trash_scan.adapter.JunkshopOwnerAdapter;
import com.example.trash_scan.databinding.ActivityTrashBinding;
import com.example.trash_scan.databinding.FragmentMarketPlaceBinding;
import com.example.trash_scan.dialogs.ViewWaste;
import com.example.trash_scan.firebase.models.Points;
import com.example.trash_scan.firebase.models.Recycables;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.fragments.InfographicsFragment;
import com.example.trash_scan.fragments.MarketPlaceFragment;
import com.example.trash_scan.ml.ModelUnquant;
import com.example.trash_scan.viewmodels.RecycableViewModel;
import com.example.trash_scan.viewmodels.UserViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class TrashActivity extends Fragment implements JunkshopOwnerAdapter.OnJunkShopClick, JunkShopsWasteAdapter.OnWasteClick {
    private ActivityTrashBinding binding;
    private ActivityResultLauncher<Intent> cameraLuancher;
    private ActivityResultLauncher<String> permissionLauncher;
    private List<String> labels;
    private FirebaseFirestore firestore;
    private UserViewModel userViewModel;
    int imageSize = 224;
    private List<Recycables> recycablesList;
    private Boolean cameraPermissionGranted = false;
    private JunkShopsWasteAdapter adapter;
    private JunkshopOwnerAdapter junkshopOwnerAdapter;
    private RecycableViewModel recycableViewModel;
    private String scannedResult = "";
    private List<Points> pointsList;
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
        pointsList = new ArrayList<>();
        recycableViewModel = new ViewModelProvider(requireActivity()).get(RecycableViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        firestore = FirebaseFirestore.getInstance();
        recycablesList = new ArrayList<>();
        binding.recyclerviewJunkShopOwners.setLayoutManager(new LinearLayoutManager(view.getContext()));
        junkshopOwnerAdapter = new JunkshopOwnerAdapter(view.getContext(),getAllJunkShopOwner(), this);
        binding.recyclerviewJunkShopOwners.setAdapter(junkshopOwnerAdapter);
        binding.recyclerviewWaste.setLayoutManager(new GridLayoutManager(view.getContext(),2));
        cameraLuancher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getData()  != null) {
                Bitmap bitmap =(Bitmap) result.getData().getExtras().get("data");
                int dimension = Math.min(bitmap.getWidth(),bitmap.getHeight());
                bitmap = ThumbnailUtils.extractThumbnail(bitmap,dimension,dimension);
                bitmap = Bitmap.createScaledBitmap(bitmap,imageSize,imageSize,false);
                if (bitmap != null) {
                    binding.imageView.setImageBitmap(bitmap);
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
        String filename = "labels.txt";

        try {
            labels = FileUtil.loadLabels(view.getContext(),filename);

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

        binding.textMoreInfo.setOnClickListener(v -> {
            if (!scannedResult.isEmpty()) {
                InfographicsFragment infographicsFragment = InfographicsFragment.newInstance(scannedResult);
                if (!infographicsFragment.isAdded()) {
                    infographicsFragment.show(getChildFragmentManager(),"Infographics");
                }
            }

        });
        binding.textGotoMarkerPlace.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_trashActivity_to_marketPlaceFragment2);
        });
    }

    private FirestoreRecyclerOptions<User> getAllJunkShopOwner() {
        Query query = firestore.collection(User.TABLE_NAME).whereEqualTo("userType","junk shop owner");
        FirestoreRecyclerOptions<User> build = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();
        return build;
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
                String result = labels.get(maxPos);
                printResult(result);
                Points points = new Points(result,maxConfidence);
                getEarnedPointsToday(points);
            }else {
                binding.textWasteName.setText("I didn't catch that");
            }
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void printResult(String result) {
        String[] newResult = result.split(",");
        if (newResult.length == 4) {
            binding.textWasteName.setText(newResult[0]);
            binding.textWasteType.setText(newResult[1]);
            binding.textRecycable.setText(newResult[2]);
            binding.textMarket.setText(newResult[3]);
            scannedResult = newResult[0];
            String markertable = newResult[3].toLowerCase();
            getJunkshopRecyclables(newResult[0]);
            if (markertable.equals("sellable")){
                bindMoreInfoAndMarketPlace(true);
            } else {
                bindMoreInfoAndMarketPlace(false);
            }
        } else  {
            binding.textWasteName.setText("I didn't catch that");
        }
    }

    private void getJunkshopRecyclables(String name){
        recycablesList.clear();
        firestore.collection(Recycables.TABLE_NAME)
                .whereEqualTo(Recycables.RECYCLABLE_NAME,name)
                .addSnapshotListener((value, error) -> {
                    if (error  != null) {
                        Log.d(".TrashActivity",error.getMessage());
                    }
                    if (value != null) {
                        for (QueryDocumentSnapshot snapshot : value){
                            if (snapshot != null) {
                                Recycables recycables = snapshot.toObject(Recycables.class);
                                recycablesList.add(recycables);
                            }
                        }
                        adapter = new JunkShopsWasteAdapter(binding.getRoot().getContext(),recycablesList,this);
                        binding.recyclerviewWaste.setAdapter(adapter);
                        if (recycablesList.size() == 0) {
                            binding.textCantFind.setText("Can't find " + name);
                            binding.textCantFind.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
    private void bindMoreInfoAndMarketPlace(Boolean isSellable){
        if (isSellable) {
            binding.layoutMarketPlace.setVisibility(View.VISIBLE);
            binding.layoutJunkShopOwners.setVisibility(View.VISIBLE);
            binding.textMoreInfo.setVisibility(View.VISIBLE);
        } else  {
            binding.layoutMarketPlace.setVisibility(View.GONE);
            binding.layoutJunkShopOwners.setVisibility(View.GONE);
            binding.textMoreInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onJunkShopOwnerClick(int position) {
        userViewModel.setUser(junkshopOwnerAdapter.getItem(position));
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_trashActivity_to_messagingFragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        junkshopOwnerAdapter.startListening();
    }

    @Override
    public void onViewWasteInfo(int position) {
        recycableViewModel.setRecycables(recycablesList.get(position));
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_trashActivity_to_viewWaste);
    }
    private void showCongratsDialog(float points) {
        View view = LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.view_congrats,binding.getRoot(),false);
        TextView textPoints = view.findViewById(R.id.textPoints);
        textPoints.setText("You earn " + points + " points!");
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(binding.getRoot().getContext());
        builder.setView(view)
                .setPositiveButton("Close", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
    private void getEarnedPointsToday(Points currentPoints) {
        pointsList.clear();
        firestore.collection(User.TABLE_NAME)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection(Points.TABLE_NAME)
                .whereGreaterThan(Points.TIMESTAMP,startOfDay(System.currentTimeMillis()))
                .whereLessThan(Points.TIMESTAMP,endOfDay(System.currentTimeMillis()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documents: task.getResult()) {
                            Points points = documents.toObject(Points.class);
                            pointsList.add(points);
                        }
                        displayReward(computePointsToday(pointsList),currentPoints);
                    }
                });
    }
    private float computePointsToday(List<Points> pointsList) {
        float total = 0;
        for (Points points  : pointsList) {
            total += points.getPoints();
        }
        return total;
    }
    private void displayReward(float totalPoints , Points points) {
        if (totalPoints < 5.0) {
            addPoints(points);
        } else {
            new MaterialAlertDialogBuilder(binding.getRoot().getContext())
                    .setTitle("Reward limit")
                    .setMessage("You reach the reward limit for today comeback again tomorrow!")
                    .setPositiveButton("Close", (dialog, which) -> {
                        dialog.dismiss();
                    }).show();
        }
    }
    private void addPoints(Points points) {
        firestore.collection(User.TABLE_NAME)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Points")
                .add(points)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showCongratsDialog(points.getPoints());
                    }
                });
    }
    public static long startOfDay(long time) {
        Calendar cal = Calendar.getInstance();
        Date date = new Date(time);
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0); //set hours to zero
        cal.set(Calendar.MINUTE, 0); // set minutes to zero
        cal.set(Calendar.SECOND, 1); //set seconds to zero
        return cal.getTimeInMillis();
    }
    public static long endOfDay(long time) {
        Calendar cal = Calendar.getInstance();
        Date date = new Date(time);
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23); //set hours to zero
        cal.set(Calendar.MINUTE, 59); // set minutes to zero
        cal.set(Calendar.SECOND, 59); //set seconds to zero
        return cal.getTimeInMillis();
    }



}