package com.intech.topfindprovider.Activities.Service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.intech.topfindprovider.MainActivity;
import com.intech.topfindprovider.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;

public class FinderRegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindRef = db.collection("TopFind_Clients");

    private FloatingActionButton add_profile_finder;
    private CircleImageView imageView;

    private Uri ImageUri;
    private UploadTask uploadTask;
    FirebaseStorage storage;
    StorageReference storageReference;
    int PERMISSION_ALL = 20003;
    private Bitmap compressedImageBitmap;
    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};

    private TextView toLogin,toMain;
    private TextInputLayout Username,Email,Password,RPassword,Phone,Location;
    private Button Btn_register2;
    private String username,email,password,rpassword,phone,location;
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finder_register);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        toLogin = findViewById(R.id.PToLogin);
        Username = findViewById(R.id.Pfirst_name);
        Email = findViewById(R.id.Pemail_address);
        Password = findViewById(R.id.Ppassword);
        RPassword = findViewById(R.id.Pretry_password);
        Phone = findViewById(R.id.Pphone_no);
        Location = findViewById(R.id.Plocation);
        Btn_register2 = findViewById(R.id.FBtn_register);
        imageView = findViewById(R.id.PprofileImage);
        add_profile_finder = findViewById(R.id.Padd_Photo);
        linearLayout = findViewById(R.id.finderLinear);




        add_profile_finder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                    ActivityCompat.requestPermissions(FinderRegisterActivity.this, PERMISSIONS, PERMISSION_ALL);
                }else {
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                            .setMinCropResultSize(512,512)
                            .setAspectRatio(1,1)
                            .start(FinderRegisterActivity.this);
                }
            }
        });

        Btn_register2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validation()){

                }else {

                    EmailPassRegistration();

                }
            }
        });

        toMain = findViewById(R.id.PBackToMain);
        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),FinderLoginActivity.class));
            }
        });

    }




    private void EmailPassRegistration() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait uploading details...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        email = Email.getEditText().getText().toString();
        password = Password.getEditText().getText().toString();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Store_Image_and_Details(mAuth.getCurrentUser().getUid());
                        }else {

                            showSnackBackOffline(getBaseContext(),task.getException().getMessage());
                            progressDialog.dismiss();
                        }
                    }
                });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    //data.getData returns the content URI for the selected Image
                    ImageUri = result.getUri();
                    imageView.setImageURI(ImageUri);
                    imageView.setVisibility(View.VISIBLE);
                    break;
            }
    }

    private ProgressDialog progressDialog;
    private void Store_Image_and_Details(String UID) {


            File newimage = new File(ImageUri.getPath());
            username = Username.getEditText().getText().toString();
            email = Email.getEditText().getText().toString();
            password = Password.getEditText().getText().toString();
            rpassword = RPassword.getEditText().getText().toString();
            phone = Phone.getEditText().getText().toString();
            location = Location.getEditText().getText().toString();

            try {
                Compressor compressor = new Compressor(this);
                compressor.setMaxHeight(200);
                compressor.setMaxWidth(200);
                compressor.setQuality(10);
                compressedImageBitmap = compressor.compressToBitmap(newimage);
            } catch (IOException e) {
                e.printStackTrace();
            }


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            final byte[] data = baos.toByteArray();


            final StorageReference ref = storageReference.child("UserImage/thumbs" + UUID.randomUUID().toString());
            uploadTask = ref.putBytes(data);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    String profileImage = downloadUri.toString();

                    String token_Id = FirebaseInstanceId.getInstance().getToken();
                    HashMap<String,Object> store = new HashMap<>();
                    store.put("User_name",username);
                    store.put("Email",email);
                    store.put("Phone",phone);
                    store.put("location",location);
                    store.put("device_token",token_Id);
                    store.put("User_ID",UID);
                    store.put("Profile_image",profileImage);



                    TopFindRef.document(UID).set(store).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Intent logout = new Intent(getApplicationContext(), MainViewActivity.class);
                                startActivity(logout);

                            }else {


                                showSnackBackOffline(getBaseContext(),task.getException().getMessage());
                                progressDialog.dismiss();

                            }
                        }
                    });



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                    showSnackBackOffline(getBaseContext(),e.getMessage());
                    progressDialog.dismiss();

                }
            });




    }



    //----InterNet Connection----
    public void showSnackBackOffline(Context context, String msg) {
        com.chootdev.csnackbar.Snackbar.with(this,null).type(Type.ERROR).message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.CENTER).show();
    }

    public void showSnackBarOnline(Context context,String msg) {

        Snackbar.with(this, null).type(Type.SUCCESS).message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.CENTER).show();

    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {


                    return false;
                }

            }
        }
        return true;
    }


    private boolean validation(){
        username = Username.getEditText().getText().toString();
        email = Email.getEditText().getText().toString();
        password = Password.getEditText().getText().toString();
        rpassword = RPassword.getEditText().getText().toString();
        phone = Phone.getEditText().getText().toString();
        location = Location.getEditText().getText().toString();


        if (username.isEmpty()){
            Username.setError("Provide your full name");
            return false;
        }else if (email.isEmpty()){
            Email.setError("Provide your email.");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Please enter a Valid email");
            return false;
        }else if (password.isEmpty()){
            Password.setError("Provide password");
            return false;
        }else if (rpassword.isEmpty()){
            RPassword.setError("Please retry password.");
            return false;
        }else if (!password.equals(rpassword)){
            RPassword.setError("Password do no match");
            return false;
        }else if (ImageUri==null){
            showSnackBackOffline(getBaseContext(),"Provide profile photo");
            return false;
        }
        else if (phone.isEmpty()){
            Phone.setError("Provide your phone number.");
            return false;
        }else if (location.isEmpty()){
            Location.setError("Provide your location.");
            return false;
        }
        else{

            return true;
        }

    }
}