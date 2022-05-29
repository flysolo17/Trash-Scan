package com.example.trash_scan.junkshop;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.trash_scan.ProgressDialog;
import com.example.trash_scan.R;
import com.example.trash_scan.databinding.FragmentAddRecycableBinding;
import com.example.trash_scan.firebase.models.Recycables;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.viewmodels.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;


public class AddRecycableFragment extends Fragment {
    private FragmentAddRecycableBinding binding;

    private Uri imageURI = null;
    private ActivityResultLauncher<Intent> galleryLauncher = null;
    private ProgressDialog progressDialog;
    private StorageReference storageReference = null;
    private StorageTask storageTask = null;
    private FirebaseFirestore firestore;
    private void init() {
        storageReference = FirebaseStorage.getInstance().getReference("recycables/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        firestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(requireActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddRecycableBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        binding.buttonBack.setOnClickListener(view1 -> Navigation.findNavController(binding.getRoot()).navigate(R.id.action_addRecycableFragment_to_nav_home));
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
            try {
                if (data != null && data.getData() != null) {
                    imageURI = data.getData();
                    binding.imageRecycable.setImageURI(imageURI);

                }
            } catch (Exception e){
                Toast.makeText(binding.getRoot().getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonGetImageFromGallery.setOnClickListener(v -> {
            Intent galleryIntent = new
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryIntent);
        });
        binding.buttonAddItem.setOnClickListener(view12 -> {
            String itemName = binding.inputItemName.getText().toString();
            String itemPrice = binding.inputPrice.getText().toString();
            String information = binding.inputInformation.getText().toString();
            if (imageURI == null) {
                Toast.makeText(binding.getRoot().getContext(),"Put an image",Toast.LENGTH_SHORT).show();
            }
            else if (itemName.isEmpty()) {
                binding.inputItemName.setError("enter firstname");
            }
            else if (itemPrice.isEmpty()) {
                binding.inputItemName.setError("enter price");
            }
            else if (information.isEmpty()) {
                binding.inputInformation.setError("enter lastname");
            }
            else {
                String id = firestore.collection(User.TABLE_NAME).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("Recycables").document().getId();
                uploadRecycable(imageURI,id,itemName,information,Integer.parseInt(itemPrice));
            }
        });

    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //TODO: upload file in firestore and storage
    private void uploadRecycable(Uri uri ,String id,String itemname ,String information,int price){
        progressDialog.isLoading();
        if (uri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
            storageTask = fileReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                fileReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                  progressDialog.stopLoading();

                  Recycables recycables = new Recycables(id,uri1.toString(),itemname,information,price);
                  addRecycable(FirebaseAuth.getInstance().getCurrentUser().getUid(),recycables);
                });
            });
        }
    }
    private void addRecycable(String myID,Recycables recycables){
        progressDialog.isLoading();
        firestore.collection(User.TABLE_NAME).document(myID)
                .collection("Recycables")
                .document(recycables.getRecycableID())
                .set(recycables)
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       Toast.makeText(binding.getRoot().getContext(),"Success",Toast.LENGTH_SHORT).show();
                       Navigation.findNavController(binding.getRoot()).navigate(R.id.action_addRecycableFragment_to_nav_home);
                   } else {
                       Toast.makeText(binding.getRoot().getContext(),"Failed",Toast.LENGTH_SHORT).show();
                   }
                   progressDialog.stopLoading();
                });
    }
}