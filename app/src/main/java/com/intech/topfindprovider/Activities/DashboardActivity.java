package com.intech.topfindprovider.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.intech.topfindprovider.Activities.Service.FinderProfileActivity;
import com.intech.topfindprovider.Fragments.Provider.ProviderProfileFragment;
import com.intech.topfindprovider.Fragments.Provider.ProviderRequestFragment;
import com.intech.topfindprovider.Fragments.Provider.ReviewsFragment;
import com.intech.topfindprovider.R;

public class DashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private long backPressedTime;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment SelectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    SelectedFragment = new ProviderProfileFragment();
                    break;
                case R.id.navigation_request:
                    SelectedFragment = new ProviderRequestFragment();
                    break;
                case R.id.navigation_review:
                    SelectedFragment = new ReviewsFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.Frame_provider,
                    SelectedFragment).commit();
            return true;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.Frame_provider,new
                ProviderProfileFragment()).commit();
        mAuth = FirebaseAuth.getInstance();


    }




    private Toast backToast;
    private void ToastBack(String message){


        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        View view = backToast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(Color.parseColor("#062D6E"), PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be editted
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.parseColor("#2BB66A"));
        backToast.show();
    }
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2500 > System.currentTimeMillis()) {
            backToast.cancel();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            super.onBackPressed();
            finish();
            return;
        } else {

            ToastBack("Double tap to exit");

        }
        backPressedTime = System.currentTimeMillis();
    }
}