package com.intech.topfindprovider.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.intech.topfindprovider.R;

public class ViewRequestActivity extends AppCompatActivity {
    private String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);
    if (getIntent() != null){
        ID = getIntent().getStringExtra("ID");
    }


    }
}