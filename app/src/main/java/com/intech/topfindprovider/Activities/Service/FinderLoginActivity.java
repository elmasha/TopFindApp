package com.intech.topfindprovider.Activities.Service;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.intech.topfindprovider.MainActivity;
import com.intech.topfindprovider.R;

import java.util.HashMap;

public class FinderLoginActivity extends AppCompatActivity {
    private TextView toRegister,toMain;

    FirebaseAuth mAuth;
    private String email,password;
    private TextInputLayout InputEmail,InputPassword;
    private Button LoginBtn;
    private LinearLayout linearLayout;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindRef = db.collection("TopFind_Clients");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_finder_login);
        mAuth = FirebaseAuth.getInstance();
        InputEmail = findViewById(R.id.email_login);
        InputPassword = findViewById(R.id.password_login);
        LoginBtn = findViewById(R.id.Btn_login);
        linearLayout = findViewById(R.id.linearLogin);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validation()){

                }else {
                    LoginUser();
                }
            }
        });




        toMain = findViewById(R.id.BackToMain2);
        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logout = new Intent(getApplicationContext(), MainActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logout);
            }
        });
        toRegister = findViewById(R.id.PRToRegister);
        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logout = new Intent(getApplicationContext(), FinderRegisterActivity.class);
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
                            ToastBack(task.getException().getMessage());
                            progressDialog.dismiss();
                        }
                    }
                });

    }


    private void UpdateDeviceToken(String uid) {

        String token_Id = FirebaseInstanceId.getInstance().getToken();

        HashMap<String,Object> updates = new HashMap<>();
        updates.put("device_token",token_Id);
        TopFindRef.document(uid).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                   ToastBack("Authentication Succeeded ");
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), MainViewActivity.class));

                }else {

                    ToastBack(task.getException().getMessage());
                    progressDialog.dismiss();

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

    private Snackbar snackbar;
    private void ToastBack(String msg){
        snackbar = Snackbar.make(linearLayout, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent logout = new Intent(getApplicationContext(), MainActivity.class);
        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logout);

    }

}