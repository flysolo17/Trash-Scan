package com.example.trash_scan.registration;
import android.os.Bundle;
import com.example.trash_scan.databinding.ActivityRegistrationBinding;
import androidx.appcompat.app.AppCompatActivity;
public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }


}