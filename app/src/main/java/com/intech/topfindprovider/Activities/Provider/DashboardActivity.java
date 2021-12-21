package com.intech.topfindprovider.Activities.Provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intech.topfindprovider.BuildConfig;
import com.intech.topfindprovider.Fragments.Provider.ProviderJobsFragment;
import com.intech.topfindprovider.Fragments.Provider.ProviderNotificationFragment;
import com.intech.topfindprovider.Fragments.Provider.ProviderProfileFragment;
import com.intech.topfindprovider.Fragments.Provider.ProviderRequestFragment;
import com.intech.topfindprovider.Fragments.Provider.ReviewsFragment;
import com.intech.topfindprovider.Fragments.Service.FinderNotificationFragment;
import com.intech.topfindprovider.Fragments.Service.FinderProfileFragment;
import com.intech.topfindprovider.Fragments.Service.MyJobsFragment;
import com.intech.topfindprovider.MainActivity;
import com.intech.topfindprovider.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.function.ToIntFunction;

public class DashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private long backPressedTime;

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private TextView OpenDrawer;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindRef = db.collection("TopFind_Provider");
    CollectionReference CurrentJobRef = db.collection("Current_clients");


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
                case R.id.navigation_notification:
                    SelectedFragment = new ProviderNotificationFragment();
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
        OpenDrawer = findViewById(R.id.drawerOpen2);
        dl = (DrawerLayout) findViewById(R.id.drawer2);
//        dl.closeDrawer(GravityCompat.END);


        OpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dl.isDrawerVisible(GravityCompat.START)){
                    dl.openDrawer(GravityCompat.START);
                }else if (dl.isDrawerVisible(GravityCompat.START)){
                    dl.closeDrawer(GravityCompat.START);
                }
            }
        });
        nv = (NavigationView) findViewById(R.id.navigation_menu2);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment SelectedFragment1 = null;
                switch (item.getItemId()) {
                    case R.id.account:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                        dl.closeDrawer(GravityCompat.START);
                    }
                        getSupportFragmentManager().beginTransaction().replace(R.id.Frame_provider,
                                new ProviderProfileFragment()).commit();

                        break;
                    case R.id.myJobs:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.Frame_provider,
                                new ProviderJobsFragment()).commit();
                        break;
                    case R.id.request:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.Frame_provider,
                                new ProviderRequestFragment()).commit();
                        break;
                    case R.id.notification:

                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.Frame_provider,
                                new ProviderNotificationFragment()).commit();
                        break;
                    case R.id.share:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        //shareApp(getApplicationContext());
                        break;
                    case R.id.refer:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);}

                        break;
                    case R.id.logOut:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        Logout_Alert();
                        break;

                    default:
                        getSupportFragmentManager().beginTransaction().replace(R.id.Frame_provider,
                                SelectedFragment1).commit();
                        return true;
                }


                return true;

            }
        });



    }


    private static void shareApp(Context context) {
        final String appPackageName = BuildConfig.APPLICATION_ID;
        final String appName = context.getString(R.string.app_name);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareBodyText = "https://play.google.com/store/apps/details?id=" +
                appPackageName;
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
        context.startActivity(Intent.createChooser(shareIntent, "Share With"));
    }


    private AlertDialog dialog2;
    public void Logout_Alert() {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog2 = builder.create();
        dialog2.show();
        builder.setTitle("Log Out");
        builder.setIcon(R.drawable.logout);
        builder.setMessage("Are you sure to Log out..\n");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Log_out();

                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog2.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    void Log_out(){

        String User_ID = mAuth.getCurrentUser().getUid();

        HashMap<String,Object> store = new HashMap<>();
        store.put("device_token", FieldValue.delete());

        TopFindRef.document(User_ID).update(store).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    mAuth.signOut();
                    Intent logout = new Intent(getApplicationContext(), MainActivity.class);
                    logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(logout);
                    dialog2.dismiss();


                }else {

                    if (task.getException().getMessage().equals("NOT_FOUND: NO document to update")){
                        ToastBack("Account doesn't exist");
                    }

                    //ToastBack( task.getException().getMessage());

                }

            }
        });

    }



    private Toast backToast;
    private void ToastBack(String message){
        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
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