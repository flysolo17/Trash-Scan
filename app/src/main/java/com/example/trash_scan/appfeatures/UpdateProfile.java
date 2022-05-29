package com.example.trash_scan.appfeatures;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
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
import com.example.trash_scan.databinding.FragmentUpdateProfileBinding;
import com.example.trash_scan.firebase.models.User;
import com.example.trash_scan.registration.RegistrationActivity;
import com.example.trash_scan.viewmodels.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;


public class UpdateProfile extends Fragment {
    private FragmentUpdateProfileBinding binding;
    private UserViewModel userViewModel;
    private User user = null;
    private Uri imageURI = null;
    private ActivityResultLauncher<Intent> galleryLauncher = null;
    private ProgressDialog progressDialog;
    private StorageReference storageReference = null;
    private StorageTask storageTask = null;
    private FirebaseFirestore firestore;
    private void init() {
        storageReference = FirebaseStorage.getInstance().getReference("profiles");
        firestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(requireActivity());
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUpdateProfileBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            this.user = user;
            bindViews(user);
            imageURI =  Uri.parse(user.getUserProfile());
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
           try {
               if (data != null && data.getData() != null) {
                    imageURI = data.getData();
                    binding.userProfile.setImageURI(imageURI);

               }
           } catch (Exception e){
               Toast.makeText(binding.getRoot().getContext(), "Error", Toast.LENGTH_SHORT).show();
           }
        });
        binding.buttonGallery.setOnClickListener(v -> {
            Intent galleryIntent = new
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryIntent);
        });
        binding.buttonBack.setOnClickListener(view1 -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_updateProfile_to_nav_profile);
        });
        binding.buttonSaveChanges.setOnClickListener(view12 -> {
            String firstname = binding.inputFirstname.getText().toString();
            String lastname = binding.inputLastname.getText().toString();
            String address = binding.inputAddres.getText().toString();
            String phone = binding.inputPhone.getText().toString();
            if (firstname.isEmpty()) {
                binding.inputFirstname.setError("enter firstname");
            }
            else if (lastname.isEmpty()) {
                binding.inputFirstname.setError("enter lastname");
            }

            else if (address.isEmpty()) {
                binding.inputFirstname.setError("enter address");
            }
            else if (phone.isEmpty()) {
                binding.inputFirstname.setError("enter phone");
            } else {
                if (!user.getUserProfile().equals(imageURI.toString())) {
                    uploadProfile(imageURI,firstname,lastname,address,phone);
                } else {
                    User user = new User(this.user.getUserID(),imageURI.toString(),firstname,lastname,address,this.user.getUserEmail(),phone,this.user.getUserType());
                    updateUser(user);
                }
            }
        });
    }

    private void bindViews(User user) {
        if (!user.getUserProfile().isEmpty()) {
            Picasso.get().load(user.getUserProfile()).into(binding.userProfile);
        }
        binding.inputFirstname.setText(user.getUserFirstName());
        binding.inputLastname.setText(user.getUserLastName());
        binding.inputAddres.setText(user.getUserAddress());
        binding.inputPhone.setText(user.getUserPhoneNumber());
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //TODO: upload file in firestore and storage
    private void uploadProfile(Uri uri ,String firstname ,String lastname,String address,String phone){
        progressDialog.isLoading();
        if (uri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
            storageTask = fileReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                fileReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    User user = new User(this.user.getUserID(),uri1.toString(),firstname,lastname,address,this.user.getUserEmail(),phone,this.user.getUserType());
                    progressDialog.stopLoading();
                    updateUser(user);
                });
            });
        }
    }
    private void updateUser(User user){
        progressDialog.isLoading();
        firestore.collection(User.TABLE_NAME).document(user.getUserID())
                .set(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.stopLoading();
                        Toast.makeText(binding.getRoot().getContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_updateProfile_to_nav_profile);
                    } else  {
                        progressDialog.stopLoading();
                        Toast.makeText(binding.getRoot().getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}