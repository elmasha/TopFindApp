package com.intech.topfindprovider.Activities.Provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.intech.topfindprovider.Activities.Service.MainViewActivity;
import com.intech.topfindprovider.MainActivity;
import com.intech.topfindprovider.R;

import java.util.HashMap;

public class ProviderLoginActivity extends AppCompatActivity {
    private TextView toRegister,toMain;

    FirebaseAuth mAuth;
    private String email,password;
    private TextInputLayout InputEmail,InputPassword;
    private Button LoginBtn;
    private LinearLayout relativeLayout;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindRef = db.collection("TopFind_Provider");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        InputEmail = findViewById(R.id.PRemail_login);
        InputPassword = findViewById(R.id.PRpassword_login);
        LoginBtn = findViewById(R.id.PRBtn_login);
        relativeLayout = findViewById(R.id.linearLayout);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validation()){

                }else {
                    LoginUser();
                }
            }
        });

        toMain = findViewById(R.id.BackToMain22);

        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logout = new Intent(getApplicationContext(), MainActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logout);
            }
        });

        toRegister = findViewById(R.id.ToRegister);

        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logout = new Intent(getApplicationContext(), ProviderRegisterActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logout);
            }
        });


    }

    private ProgressDialog progressDialog;
    private void LoginUser() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        password = InputPassword.getEditText().getText().toString();
        email = InputEmail.getEditText().getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            UpdateDeviceToken(mAuth.getCurrentUser().getUid());
                        }else {
                            showSnackBackOffline(getBaseContext(),task.getException().getMessage());
                            progressDialog.dismiss();

                        }
                    }
                });

    }


    private void UpdateDeviceToken(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference TopFindRef = db.collection("TopFind_Provider");

        String token_Id = FirebaseInstanceId.getInstance().getToken();

        HashMap<String,Object> updates = new HashMap<>();
        updates.put("device_token",token_Id);
        TopFindRef.document(uid).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    showSnackBarOnline(getBaseContext(),"Login was successful");
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));

                }else {

                    showSnackBackOffline(getBaseContext(),task.getException().getMessage());
                    progressDialog.dismiss();
                    if (task.getException().getMessage().equals("NOT_FOUND: NO document to update")){
                        showSnackBackOffline(getBaseContext(),"Account doesn't exist");
                    }

                }
            }
        });


    }


    private boolean validation(){
        password = InputPassword.getEditText().getText().toString();
        email = InputEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()){
            InputEmail.setError("Provide your Email");
            return false;
        }else if (password.isEmpty()){
            InputPassword.setError("Provide a Password");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            InputEmail.setError("Please enter a Valid email");
            return false;
        }
        else{

            return true;
        }

    }



    //----InterNet Connection----
    public void showSnackBackOffline(Context context, String msg) {
       Snackbar.with(this,null).type(Type.ERROR).message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.CENTER).show();
    }

    public void showSnackBarOnline(Context context,String msg) {

        Snackbar.with(this,null).type(Type.SUCCESS).message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.CENTER).show();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent logout = new Intent(getApplicationContext(), MainActivity.class);
        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logout);
    }
}