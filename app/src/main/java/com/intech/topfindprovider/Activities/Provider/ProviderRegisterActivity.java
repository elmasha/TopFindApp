package com.intech.topfindprovider.Activities.Provider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.intech.topfindprovider.Activities.Service.MainViewActivity;
import com.intech.topfindprovider.Interface.RetrofitInterface;
import com.intech.topfindprovider.MainActivity;
import com.intech.topfindprovider.Models.Category;
import com.intech.topfindprovider.Models.ResponseStk;
import com.intech.topfindprovider.Models.StkQuery;
import com.intech.topfindprovider.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;

public class ProviderRegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private FloatingActionButton add_profile;
    private CircleImageView imageView;

    private Uri ImageUri;
    private UploadTask uploadTask;
    FirebaseStorage storage;
    StorageReference storageReference;
    int PERMISSION_ALL = 20003;
    private Bitmap compressedImageBitmap;
    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindRef = db.collection("TopFind_Provider");
    CollectionReference TopFindCategory = db.collection("Category");

    private TextView toLogin,toMain;
    private TextInputLayout Username,Email,Profession,Narration,Password,RPassword,Phone,Location;
    private Button Btn_register;
    private Spinner Experience,PaymentMethod;
    private String username,email,password,rpassword,phone,location,profession,experience,payment,narration;

    public  String Product_Id,CheckoutRequestID,ResponseCode,ResultCode,ResponseDescription,ResultDesc;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "https://secure-atoll-77019.herokuapp.com/";

    private static final long START_TIME_IN_MILLIS_COUNT = 27000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS_COUNT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth =FirebaseAuth.getInstance();
        toLogin = findViewById(R.id.ToLogin);
        Username = findViewById(R.id.first_name);
        Email = findViewById(R.id.email_address);
        Password = findViewById(R.id.password);
        Profession = findViewById(R.id.Profession);
        Narration = findViewById(R.id.Narration);
        Experience = findViewById(R.id.Experience);
        PaymentMethod = findViewById(R.id.PaymentMethod);
        RPassword = findViewById(R.id.retry_password);
        Phone = findViewById(R.id.phone_no);
        Location = findViewById(R.id.location);
        Btn_register = findViewById(R.id.Btn_register);
        imageView = findViewById(R.id.profileImage);
        add_profile = findViewById(R.id.add_Photo);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        add_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                    ActivityCompat.requestPermissions(ProviderRegisterActivity.this, PERMISSIONS, PERMISSION_ALL);
                }else {
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                            .setMinCropResultSize(512,512)
                            .setAspectRatio(1,1)
                            .start(ProviderRegisterActivity.this);
                }
            }
        });

        Btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!validation()){

                }else {
                    profession = Profession.getEditText().getText().toString();
                    EmailPassRegistration();
                }
            }
        });

        toMain = findViewById(R.id.BackToMain);
        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProviderLoginActivity.class));
            }
        });

    }




    private ProgressDialog progressStk;
    private void stk(){
        String Amount = "1";
        PesaNO = mpesaNo.getText().toString().trim();
        String PhoneNumber = PesaNO.substring(1);
        String ID =  TopFindRef.document().getId();
        HashMap<String,Object> stk_Push = new HashMap<>();
//        stk_Push.put("User_name",FuserName);
//        stk_Push.put("Email",Femail);
//        stk_Push.put("Phone",Fnumber);
//        stk_Push.put("location",Flocation);
//        stk_Push.put("User_ID",requestID);
//        stk_Push.put("Request_ID",ID);
//        stk_Push.put("Request_message",reqMessage);
//        stk_Push.put("Sender_ID",mAuth.getCurrentUser().getUid());
//        stk_Push.put("timestamp", FieldValue.serverTimestamp());
//        stk_Push.put("Profile_image",FuserImage);
//        stk_Push.put("Phone_no","254"+PhoneNumber);
//        stk_Push.put("Amount",Amount);


        Call<ResponseStk> callStk = retrofitInterface.stk_push(stk_Push);

        callStk.enqueue(new Callback<ResponseStk>() {
            @Override
            public void onResponse(Call<ResponseStk> call, Response<ResponseStk> response) {

                if (response.code() == 200) {
                    newtime();
                    progressStk = new ProgressDialog(ProviderRegisterActivity.this);
                    progressStk.setCancelable(false);
                    progressStk.setMessage("Processing payment...");
                    progressStk.show();
                    if (dialog_mpesa != null)dialog_mpesa.dismiss();
                    noMpesa.setVisibility(View.VISIBLE);
                    BtnConfirm.setVisibility(View.VISIBLE);
                    progressBarMpesa.setVisibility(View.INVISIBLE);
                    ResponseStk responseStk = response.body();
                    String responeDesc = responseStk.getCustomerMessage();
                    ResponseCode = responseStk.getResponseCode();
                    CheckoutRequestID = responseStk.getCheckoutRequestID();
                    String errorMessage = responseStk.getErrorMessage();
                    String errorCode = responseStk.getErrorCode();
                    Log.i("TAG", "CheckoutRequestID: " + response.body());

                    //Toast.makeText(getContext(), responeDesc , Toast.LENGTH_LONG).show();

                    if (responeDesc != null){
                        if (responeDesc.equals("Success. Request accepted for processing")){
                            ToastBack(responeDesc);
                        }else {

                        }
                    }else {

                        if (errorMessage.equals("No ICCID found on NMS")){
                            ToastBack("Please provide a valid mpesa number.");
                            progressStk.dismiss();
                        }
                        ToastBack(errorMessage);
                        progressStk.dismiss();
                    }


                } else if (response.code() == 404) {
                    ResponseStk errorResponse = response.body();
                    ToastBack(errorResponse.getErrorMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseStk> call, Throwable t) {

                // Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }

    private void newtime(){
        new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (CheckoutRequestID != null){
                    StkQuery(CheckoutRequestID);

                }else {

                    if (progressStk != null)progressStk.dismiss();
                    else if (dialog_mpesa != null)dialog_mpesa.dismiss();
                    //Toast.makeText(getContext(), "StkPush Request timeout...", Toast.LENGTH_LONG).show();
                    ToastBack("StkPush Request timeout...");
                    // progressStk.dismiss();
                }
            }
        }.start();
    }

    private void StkQuery(String checkoutRequestID){

        Map<String ,String > stk_Query = new HashMap<>();
        stk_Query.put("checkoutRequestId",checkoutRequestID);
        Call<StkQuery> callQuery = retrofitInterface.stk_Query(stk_Query);

        callQuery.enqueue(new Callback<StkQuery>() {
            @Override
            public void onResponse(Call<StkQuery> call, Response<StkQuery> response) {

                if (response != null){
                    if (response.code()== 200){

                        StkQuery stkQuery1 = response.body();
                        Toast.makeText(getApplicationContext(), ""+ stkQuery1.getResultDesc(), Toast.LENGTH_SHORT).show();
                        Log.i("TAG", "onResponse:"+response.body());
                        String body = stkQuery1.getResultDesc();
                        ResponseDescription = stkQuery1.getResponseDescription();
                        ResultCode = stkQuery1.getResultCode();
                        progressStk.dismiss();
                        pauseTimer();
                        resetTimer();

                        if (ResultCode.equals("0")){
                            new SweetAlertDialog(ProviderRegisterActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Registration successfully..")
                                    .show();
                            EmailPassRegistration();


                        }else if (ResultCode.equals("1032")){
                            new SweetAlertDialog(ProviderRegisterActivity.this,SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("This payment was cancelled")
                                    .setConfirmText("Close")
                                    .show();



                        }else if (ResultCode.equals("1031")){

                            new SweetAlertDialog(ProviderRegisterActivity.this,SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("This payment was cancelled")
                                    .setConfirmText("Close")
                                    .show();



                        }else if (ResultCode.equals("2001")) {
                            new SweetAlertDialog(ProviderRegisterActivity.this,SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Sorry you entered a wrong pin. Try again")
                                    .setConfirmText("Okay")
                                    .show();






                        }else if (ResultCode.equals("1")) {
                            new SweetAlertDialog(ProviderRegisterActivity.this,SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("You current balance is insufficient.")
                                    .setConfirmText("Close")
                                    .show();

                        }


                    }else if (response.code()==404){
                        StkQuery errorResponse = response.body();
                        ToastBack(errorResponse.getErrorMessage());
                    }
                }


            }

            @Override
            public void onFailure(Call<StkQuery> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Now"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                progressStk.dismiss();

            }
        });


    }


    private AlertDialog dialog_mpesa;
    private EditText mpesaNo;
    private String PesaNO;
    private Button BtnConfirm;
    private TextView noMpesa,mpesaText;
    private ProgressBar progressBarMpesa;
    private int phoneState = 0;
    private void MpesaDialog(){
        final  AlertDialog.Builder mbuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_mpesa, null);
        mbuilder.setView(mView);
        mpesaNo = mView.findViewById(R.id.MpesaPhone);
        BtnConfirm = mView.findViewById(R.id.verify_MpesaNo);
        progressBarMpesa = mView.findViewById(R.id.progress_MpesaNo);
        mpesaText = mView.findViewById(R.id.TextMpesa);
        noMpesa = mView.findViewById(R.id.no);
        DoubleBounce doubleBounce = new DoubleBounce();
        progressBarMpesa.setIndeterminateDrawable(doubleBounce);

        mpesaText.setText("Are you sure this "+phone+" is your Mpesa number?");
        mpesaNo.setText(phone);


        noMpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneState == 0){
                    mpesaText.setText("Please enter is your Mpesa number");
                    mpesaNo.setVisibility(View.VISIBLE);
                    phoneState =1;
                    noMpesa.setText("Close");
                }else if (phoneState == 1){
                    phoneState = 0;
                    if (dialog_mpesa != null) dialog_mpesa.dismiss();
                    noMpesa.setText("No");
                }

            }
        });

        BtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (phone.isEmpty()){
                    ToastBack("Please provide your Mpesa Number");
                }else {
                    stk();
                    startTimer();
                    noMpesa.setVisibility(View.INVISIBLE);
                    BtnConfirm.setVisibility(View.INVISIBLE);
                    progressBarMpesa.setVisibility(View.VISIBLE);
                }

            }
        });
        dialog_mpesa = mbuilder.create();
        dialog_mpesa.show();

    }



    //----Stk Timer------
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;

                if (CheckoutRequestID != null){
                    StkQuery(CheckoutRequestID);

                }else {

                    //if (progressStk != null)progressStk.dismiss();
                    if (dialog_mpesa != null)dialog_mpesa.dismiss();
                    //Toast.makeText(getContext(), "StkPush Request timeout...", Toast.LENGTH_LONG).show();
                    ToastBack("StkPush Request timeout...");
                    // progressStk.dismiss();
                }

            }
        }.start();
        mTimerRunning = true;

    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;

    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS_COUNT;
        updateCountDownText();

    }
    ///____end stk timer.

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }



    private boolean infoState = false;
    void LoadSelectedCandidate(){
        TopFindCategory.whereEqualTo("category",profession)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {


                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Category  category = document.toObject(Category.class);
                        if (category.getCategory() != null){
                            infoState = true;
                            ToastBack(category.getCategory());

                            if (profession.equals(category.getCategory())){
                                infoState = true;
                                checkCat(infoState);
                            }else if (category == null){
                                infoState = false;
                                checkCat(infoState);
                            }

                        }else {

                        }
                        ToastBack(category.getCategory());
                    }
                }else {
                    ToastBack(task.getException().getMessage());
                }
            }
        });
    }

    private void checkCat(boolean infoState2) {

        if (infoState2 == true){
            Store_Image_and_Details(profession);
        }else if (infoState2 == false){
            New_Store_Image_and_Details(profession);

        }

    }


    ProgressDialog progressDialog;
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
                           LoadSelectedCandidate();
                        }else {
                            showSnackBackOffline(getApplicationContext(),task.getException().getMessage());
                            progressDialog.dismiss();
                        }
                    }
                });


    }

    private void Store_Image_and_Details(String profession) {


        if (ImageUri != null){

            File newimage = new File(ImageUri.getPath());
            username = Username.getEditText().getText().toString();
            email = Email.getEditText().getText().toString();
            password = Password.getEditText().getText().toString();
            rpassword = RPassword.getEditText().getText().toString();
            phone = Phone.getEditText().getText().toString();
            location = Location.getEditText().getText().toString();
            experience = Experience.getSelectedItem().toString();
            payment = PaymentMethod.getSelectedItem().toString();
            narration = Narration.getEditText().getText().toString();




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


            final StorageReference ref = storageReference.child("Users/thumbs" + UUID.randomUUID().toString());
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
                    store.put("Experience",experience);
                    store.put("Payment",payment);
                    store.put("Narration",narration);
                    store.put("Profession",profession);
                    store.put("device_token",token_Id);
                    store.put("ratings",0);
                    store.put("User_ID",mAuth.getCurrentUser().getUid());
                    store.put("Profile_image",profileImage);
                    store.put("date_registered", FieldValue.serverTimestamp());

                    String DOC_id = TopFindCategory.document().getId();

                    HashMap<String,Object> storeCategory = new HashMap<>();
                    storeCategory.put("category",profession);
                    storeCategory.put("category_ID",DOC_id);

                    TopFindRef.document(mAuth.getCurrentUser().getUid())
                            .set(store).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Intent logout = new Intent(getApplicationContext(), DashboardActivity.class);
                                showSnackBarOnline(getBaseContext(),"Registration was successful.");
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

                }
            });

        }else {

            ToastBack("No image selected");

        }




    }


    private void New_Store_Image_and_Details(String profession) {


        if (ImageUri != null){

            File newimage = new File(ImageUri.getPath());
            username = Username.getEditText().getText().toString();
            email = Email.getEditText().getText().toString();
            password = Password.getEditText().getText().toString();
            rpassword = RPassword.getEditText().getText().toString();
            phone = Phone.getEditText().getText().toString();
            location = Location.getEditText().getText().toString();
            experience = Experience.getSelectedItem().toString();
            payment = PaymentMethod.getSelectedItem().toString();
            narration = Narration.getEditText().getText().toString();




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


            final StorageReference ref = storageReference.child("Users/thumbs" + UUID.randomUUID().toString());
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
                    store.put("Experience",experience);
                    store.put("Payment",payment);
                    store.put("Narration",narration);
                    store.put("Profession",profession);
                    store.put("device_token",token_Id);
                    store.put("ratings",0);
                    store.put("User_ID",mAuth.getCurrentUser().getUid());
                    store.put("Profile_image",profileImage);
                    store.put("date_registered", FieldValue.serverTimestamp());

                    String DOC_id = TopFindCategory.document().getId();

                    HashMap<String,Object> storeCategory = new HashMap<>();
                    storeCategory.put("category",profession);
                    storeCategory.put("category_ID",DOC_id);



                    TopFindCategory.document(DOC_id).set(storeCategory)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        TopFindRef.document(mAuth.getCurrentUser().getUid())
                                                .set(store).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                                                if (task.isSuccessful()){
                                                    Intent logout = new Intent(getApplicationContext(), DashboardActivity.class);
                                                    showSnackBarOnline(getBaseContext(),"Registration was successful.");
                                                    startActivity(logout);
                                                }else {

                                                    showSnackBackOffline(getBaseContext(),task.getException().getMessage());
                                                    progressDialog.dismiss();

                                                }
                                            }
                                        });


                                    }else {

                                        showSnackBackOffline(getBaseContext(),task.getException().getMessage());
                                    }
                                }
                            });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                    showSnackBackOffline(getBaseContext(),e.getMessage());

                }
            });

        }else {

            ToastBack("No image selected");

        }




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
                    if (ImageUri != null){
                        imageView.setImageURI(ImageUri);
                        imageView.setVisibility(View.VISIBLE);
                        add_profile.setVisibility(View.GONE);
                    }else {

                    }

                    break;
            }
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
        experience = Experience.getSelectedItem().toString();
        payment = PaymentMethod.getSelectedItem().toString();
        narration = Narration.getEditText().getText().toString();
        profession = Profession.getEditText().getText().toString();


        if (username.isEmpty()){
            Username.setError("Provide your full name");
            return false;
        }else if (location.isEmpty()){
            Location.setError("Provide your location.");
            return false;
        }else if (email.isEmpty()){
            Email.setError("Provide your email.");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Please enter a Valid email");
            return false;
        } else if (phone.isEmpty()){
            Phone.setError("Provide your phone number.");
            return false;
        }else if (profession.isEmpty()){
            Profession.setError("Provide profession");
            return false;
        }else if (narration.isEmpty()){
            Narration.setError("Provide narration");
            return false;
        }  else if (experience.equals("Select experience")){
            ToastBack("Provide experience");
            return false;
        }else if (payment.equals("Select Payment Method")){
            ToastBack("Provide payment method");
            return false;
        }
        else if (password.isEmpty()){
            Password.setError("Provide password");
            return false;
        }else if (rpassword.isEmpty()){
            RPassword.setError("Please retry password.");
            return false;
        }else if (!password.equals(rpassword)){
            RPassword.setError("Password do no match");
            return false;
        }else if (ImageUri == null){
            ToastBack("No image was selected.");
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

    private Toast backToast;
    private void ToastBack(String message){
        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        backToast.show();
    }

}