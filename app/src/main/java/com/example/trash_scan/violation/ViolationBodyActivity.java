package com.example.trash_scan.violation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trash_scan.R;
import com.example.trash_scan.appfeatures.Violation;

public class ViolationBodyActivity extends AppCompatActivity {
    private TextView text_title,text_body;
    private ImageView button_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_body);
        initViews();
        Intent intent = getIntent();
        text_title.setText(intent.getStringExtra("title"));
        text_body.setText(intent.getStringExtra("body"));

        button_back.setOnClickListener(v -> new Intent(ViolationBodyActivity.this, Violation.class));
    }
    private void initViews(){
        text_title = findViewById(R.id.text_title);
        text_body = findViewById(R.id.text_body);
        button_back = findViewById(R.id.button_back);
    }
}