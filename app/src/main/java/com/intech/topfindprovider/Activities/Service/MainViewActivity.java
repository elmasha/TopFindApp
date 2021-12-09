package com.intech.topfindprovider.Activities.Service;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.intech.topfindprovider.Adapters.CategoryAdapter;
import com.intech.topfindprovider.Adapters.CountyAdapter;
import com.intech.topfindprovider.Adapters.ProvidersAdapter;
import com.intech.topfindprovider.Fragments.Service.FinderNotificationFragment;
import com.intech.topfindprovider.Fragments.Service.FinderProfileFragment;
import com.intech.topfindprovider.Fragments.Service.MyJobsFragment;
import com.intech.topfindprovider.Interface.RetrofitInterface;
import com.intech.topfindprovider.MainActivity;
import com.intech.topfindprovider.Models.Category;
import com.intech.topfindprovider.Models.Counties;
import com.intech.topfindprovider.Models.ResponseStk;
import com.intech.topfindprovider.Models.StkQuery;
import com.intech.topfindprovider.Models.TopFindProviders;
import com.intech.topfindprovider.Models.TopFinders;
import com.intech.topfindprovider.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainViewActivity extends AppCompatActivity {
    private long backPressedTime;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindRef = db.collection("TopFind_Clients");
    CollectionReference TopFindProRef = db.collection("TopFind_Provider");
    CollectionReference FindRequestRef = db.collection("TopFind_Request");
    CollectionReference CategoryRef = db.collection("Category");
    CollectionReference CountyRef = db.collection("County");

    private CircleImageView profileImage;


    private SwipeRefreshLayout swipeRefreshLayout;
    private FrameLayout catLayout,countyLayout;
    private LinearLayout linearLayoutSearch,linearLayoutFilter,ClearAll;
    private ProvidersAdapter adapter;
    private CategoryAdapter categoryAdapter;
    private CountyAdapter countyAdapter;
    private RecyclerView mRecyclerView, mRecyclerViewCategory,recyclerViewPost,recyclerCounty;
    private TextView chooseCat,chooseCounty,SearchCategory,OpenDrawer,closeFilter;
    private String Category = "";
    private String  County = "";
    private FloatingActionButton postJob;
    private Button SearchCat;
    private RelativeLayout relativeLayout;
    private ImageView imageViewNotify;

    private long Rating;


    private int dropDownState = 0;
    private int NotifyState = 0;


    public  String Product_Id,CheckoutRequestID,ResponseCode,ResultCode,ResponseDescription,ResultDesc;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "https://secure-atoll-77019.herokuapp.com/";

    private static final long START_TIME_IN_MILLIS_COUNT = 27000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS_COUNT;


    @Override
    protected void onStart() {
        super.onStart();
        FetchProduct();
        FetchCategory();
        FetchCounty();
        LoadDetails();
        if (County.isEmpty() | Category.isEmpty()){
            SearchCat.setBackgroundResource(R.drawable.btn_round_grey);
            SearchCat.setTextColor(Color.parseColor("#808080"));
        }
    }



    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private int FilterState = 0;
    private int countyState = 0;
    private RadioGroup radioGroup;
    private long RatingSearch = 0;
    private String SelectedExp;
    private Spinner experienceSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        mAuth = FirebaseAuth.getInstance();
        profileImage = findViewById(R.id.ProfilePicture);
        swipeRefreshLayout = findViewById(R.id.SwipeRefresh);
        mRecyclerView = findViewById(R.id.recycler_provider);
        mRecyclerViewCategory = findViewById(R.id.recycler_category);
        chooseCat = findViewById(R.id.choose_category);
        catLayout = findViewById(R.id.FrameCategory);
        SearchCat = findViewById(R.id.SearchCategory);
        SearchCategory = findViewById(R.id.searchCat);
        linearLayoutSearch = findViewById(R.id.SearchLayout);
        relativeLayout = findViewById(R.id.relative);
        recyclerCounty = findViewById(R.id.recycler_county);
        imageViewNotify = findViewById(R.id.Notification);
        OpenDrawer = findViewById(R.id.drawerOpen);
        closeFilter = findViewById(R.id.CloseCat);
        ClearAll = findViewById(R.id.ClearAll);
        chooseCounty = findViewById(R.id.choose_county);
        countyLayout = findViewById(R.id.onCountyLayout);
        radioGroup = findViewById(R.id.RadioGroupRating);
        experienceSelect = findViewById(R.id.SelectExperience);
        linearLayoutFilter = findViewById(R.id.Filter);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);


        experienceSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    SelectedExp = experienceSelect.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.one:
                       RatingSearch = 1;
                        break;
                    case R.id.two:
                        RatingSearch = 2;
                        break;
                    case R.id.three:
                        RatingSearch = 3;
                        break;
                    case R.id.four:
                        RatingSearch = 4;
                        break;
                    case R.id.five:
                        RatingSearch = 5;
                        break;
                }
            }
        });


        catLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Empty Onclick...
            }
        });

        chooseCounty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countyState == 0){
                    countyLayout.setVisibility(View.VISIBLE);
                    countyState = 1;
                }else if (countyState == 1){
                    countyLayout.setVisibility(View.GONE);
                    countyState=0;
                }

            }
        });



        ClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Category = "";
                County = "";
                RatingSearch = 0;
                SelectedExp = "";
                FetchCategory();
                FetchCounty();
                FetchProduct();
                SearchCat.setBackgroundResource(R.drawable.btn_round_grey);
                SearchCat.setTextColor(Color.parseColor("#808080"));
            }
        });

        closeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FilterState == 1){
                    catLayout.setVisibility(View.GONE);
                    FilterState =0;
                    SearchCat.setBackgroundResource(R.drawable.btn_round_grey);
                    SearchCat.setTextColor(Color.parseColor("#808080"));
                }
            }
        });

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




        linearLayoutFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FilterState == 0){
                    catLayout.setVisibility(View.VISIBLE);
                    closeFilter.setVisibility(View.VISIBLE);
                    FilterState =1;
                }else if (FilterState ==1){
                    catLayout.setVisibility(View.GONE);
                    closeFilter.setVisibility(View.GONE);
                    FilterState=0;
                }
            }
        });


        dl = (DrawerLayout) findViewById(R.id.drawer);
//        dl.closeDrawer(GravityCompat.END);


        nv = (NavigationView) findViewById(R.id.navigation_menu);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.account:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                        dl.closeDrawer(GravityCompat.START);
                       }
                        getSupportFragmentManager().beginTransaction().replace(R.id.Frame_main,
                                new FinderProfileFragment()).commit();
                        break;
                    case R.id.myJobs:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.Frame_main,
                                new MyJobsFragment()).commit();
                        break;
                    case R.id.notification:

                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        if (NotifyState == 0){
                            getSupportFragmentManager().beginTransaction().replace(R.id.Frame_main,
                                    new FinderNotificationFragment()).commit();
                            NotifyState = 1;
                        }else if (NotifyState == 1){
                            NotifyState = 0;
                            if(getSupportFragmentManager().findFragmentById(R.id.Frame_main) != null) {
                                getSupportFragmentManager()
                                        .beginTransaction().
                                        remove(getSupportFragmentManager().findFragmentById(R.id.Frame_main)).commit();
                            }

                        }
                        break;
                    case R.id.share:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        Toast.makeText(MainViewActivity.this, "My Cart", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.refer:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        Toast.makeText(MainViewActivity.this, "My Cart", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.logOut:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        Logout_Alert();
                        break;

                    default:
                        return true;
                }


                return true;

            }
        });


        imageViewNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NotifyState == 0){
                    getSupportFragmentManager().beginTransaction().replace(R.id.Frame_main,
                            new FinderNotificationFragment()).commit();
                    NotifyState = 1;
                }else if (NotifyState == 1){
                    NotifyState = 0;
                    if(getSupportFragmentManager().findFragmentById(R.id.Frame_main) != null) {
                        getSupportFragmentManager()
                                .beginTransaction().
                                remove(getSupportFragmentManager().findFragmentById(R.id.Frame_main)).commit();
                    }

                }

            }
        });

        postJob = findViewById(R.id.PostJob);


        postJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostJobDialog();
            }
        });


        SearchCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Category != null | County != null){
                    FetchProduct();
                    catLayout.setVisibility(View.GONE);
                }

            }
        });





        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Category = "";
                County = "";
                RatingSearch = 0;
                SelectedExp = "";
                chooseCat.setText("Select category");
                SearchCat.setBackgroundResource(R.drawable.btn_round_grey);
                SearchCat.setTextColor(Color.parseColor("#808080"));
                FetchCategory();
                FetchCounty();
                FetchProduct();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean
                        isOpen = dl.isDrawerOpen(GravityCompat.END);
                dl.openDrawer(GravityCompat.START);

                //startActivity(new Intent(getApplicationContext(), FinderProfileActivity.class));
            }
        });

        FetchProduct();

    }






    private ProgressDialog progressStk;
    private void stk(String requestID,String reqMessage){
            String Amount = "1";
            PesaNO = mpesaNo.getText().toString().trim();
            String PhoneNumber = PesaNO.substring(1);
            String ID =  FindRequestRef.document().getId();
            HashMap<String,Object> stk_Push = new HashMap<>();
            stk_Push.put("User_name",FuserName);
            stk_Push.put("Email",Femail);
            stk_Push.put("Phone",Fnumber);
            stk_Push.put("location",Flocation);
            stk_Push.put("User_ID",requestID);
            stk_Push.put("Request_ID",ID);
            stk_Push.put("Request_message",reqMessage);
            stk_Push.put("Sender_ID",mAuth.getCurrentUser().getUid());
            stk_Push.put("timestamp", FieldValue.serverTimestamp());
            stk_Push.put("Profile_image",FuserImage);
            stk_Push.put("Phone_no","254"+PhoneNumber);
            stk_Push.put("Amount",Amount);


            Call<ResponseStk> callStk = retrofitInterface.stk_push(stk_Push);

            callStk.enqueue(new Callback<ResponseStk>() {
                @Override
                public void onResponse(Call<ResponseStk> call, Response<ResponseStk> response) {

                    if (response.code() == 200) {
                        newtime();
                        progressStk = new ProgressDialog(MainViewActivity.this);
                        progressStk.setCancelable(false);
                        progressStk.setMessage("Processing payment...");
                        progressStk.show();
                        if (dialog_sendRequest != null)dialog_sendRequest.dismiss();
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
                    else if (dialog_sendRequest != null)dialog_sendRequest.dismiss();
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
                            new SweetAlertDialog(MainViewActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Order sent successfully..")
                                    .show();
                            progressBar.setVisibility(View.INVISIBLE);
                            dialog_sendRequest.dismiss();


                        }else if (ResultCode.equals("1032")){
                            new SweetAlertDialog(MainViewActivity.this,SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("This payment was cancelled")
                                    .setConfirmText("Close")
                                    .show();



                        }else if (ResultCode.equals("1031")){

                            new SweetAlertDialog(MainViewActivity.this,SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("This payment was cancelled")
                                    .setConfirmText("Close")
                                    .show();



                        }else if (ResultCode.equals("2001")) {
                            new SweetAlertDialog(MainViewActivity.this,SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Sorry you entered a wrong pin. Try again")
                                    .setConfirmText("Okay")
                                    .show();






                        }else if (ResultCode.equals("1")) {
                            new SweetAlertDialog(MainViewActivity.this,SweetAlertDialog.WARNING_TYPE)
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
    private void MpesaDialog(String reqMessage,String requestID){
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

        mpesaText.setText("Are you sure this "+Fnumber+" is your Mpesa number?");
        mpesaNo.setText(Fnumber);


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

                if (dialog_sendRequest != null) dialog_sendRequest.dismiss();
                stk(reqMessage,requestID);
                startTimer();
                noMpesa.setVisibility(View.INVISIBLE);
                BtnConfirm.setVisibility(View.INVISIBLE);
                progressBarMpesa.setVisibility(View.VISIBLE);
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
                     if (dialog_sendRequest != null)dialog_sendRequest.dismiss();
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



    private android.app.AlertDialog dialog2;
    public void Logout_Alert() {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        dialog2 = builder.create();
        dialog2.show();
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

                    ToastBack( task.getException().getMessage());

                }

            }
        });

    }


    private AlertDialog dialog_postJob;
    private TextView  PostCancel,catOfChoice, selectedCat;
    private EditText textWrite;
    private String inputText,pickedCat;
    private Button BtnPost;
    private ProgressBar progressBar;
    private void PostJobDialog(){
        final  AlertDialog.Builder mbuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_post_job, null);
        mbuilder.setView(mView);
        dialog_postJob = mbuilder.create();
        dialog_postJob.show();
        textWrite = mView.findViewById(R.id.input_post_job);
        BtnPost = mView.findViewById(R.id.Btn_postJob);
        PostCancel = mView.findViewById(R.id.CancelJob);
        recyclerViewPost = mView.findViewById(R.id.cat_post);
        catOfChoice = mView.findViewById(R.id.cat_of_choice);
        chooseCat = mView.findViewById(R.id.choose_cat);



        FetchCategory1();


        if (pickedCat != null){
            catOfChoice.setText(pickedCat);
            catOfChoice.setVisibility(View.VISIBLE);
        }






        PostCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog_postJob != null)dialog_postJob.dismiss();

            }
        });

        BtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputText = textWrite.getText().toString();
                if (inputText.isEmpty()){
                    ToastBack("Message is empty");
                }else {
                    postJob(pickedCat);
                }
            }
        });


    }

    private void postJob(String pickedCat){

        TopFindProRef.whereEqualTo("Profession",pickedCat)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                TopFindProviders topFindProviders = document.toObject(TopFindProviders.class);
                                if (topFindProviders != null){
                                    MpesaDialog(inputText,topFindProviders.getUser_ID());
                                }


                            }
                        } else {

                        }
                    }
                });
    }



    private AlertDialog dialog_sendRequest;
    private TextView emailInput,usernameInput,Location,ReCancel,ReProfession;
    private EditText ReText;
    private String Reqemail,Requsername,Reqphone,ReqMessage,ReqProfileImage,ReQuestLocation,RequestID,ReqProfession;
    private Button BtnConfirm1;
    private CircleImageView ReQuestImage;
    private LinearLayout google,facebook;
    private void RequestDialog(){
        final  AlertDialog.Builder mbuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_send_request, null);
        mbuilder.setView(mView);
        dialog_sendRequest = mbuilder.create();
        dialog_sendRequest.show();
        emailInput = mView.findViewById(R.id.EmailRequest);
        usernameInput = mView.findViewById(R.id.NameRequest);
        BtnConfirm1 = mView.findViewById(R.id.SendRequest);
        Location = mView.findViewById(R.id.LocationRequest);
        ReCancel = mView.findViewById(R.id.CancelRequest);
        ReQuestImage = mView.findViewById(R.id.RequestImage);
        ReProfession = mView.findViewById(R.id.ProfessionRequest);
        ReText = mView.findViewById(R.id.input_post_request);



        ReText.setHint("Write a message to "+ Requsername +"..");



        usernameInput.setText(Requsername);
        emailInput.setText(Reqemail);
        Location.setText(ReQuestLocation);
        ReProfession.setText(ReqProfession);
        if (ReqProfileImage != null){
            Picasso.with(getApplicationContext())
                    .load(ReqProfileImage).placeholder(R.drawable.user)
                    .error(R.drawable.errorimage)
                    .into(ReQuestImage);
        }



        ReCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog_sendRequest != null)dialog_sendRequest.dismiss();

            }
        });

        BtnConfirm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReqMessage = ReText.getText().toString().trim();
                if (ReqMessage.isEmpty()){
                    ToastBack("Enter a message");
                }else {
                    if (dialog_sendRequest != null)dialog_sendRequest.dismiss();
                    MpesaDialog(RequestID,ReqMessage);
                }

                }

        });


    }

    private void FetchCategory1() {

        //       Query query = TopFindRef
//                .orderBy("timestamp", Query.Direction.DESCENDING).limit(30);
        FirestoreRecyclerOptions<Category> transaction = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(CategoryRef, Category.class)
                .setLifecycleOwner(this)
                .build();
        categoryAdapter = new CategoryAdapter(transaction);
        recyclerViewPost.setHasFixedSize(true);
        recyclerViewPost.setNestedScrollingEnabled(false);
        LinearLayoutManager LayoutManager
                = new LinearLayoutManager(MainViewActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPost.setLayoutManager(new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.HORIZONTAL));
        recyclerViewPost.setAdapter(categoryAdapter);
        categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Category category = documentSnapshot.toObject(Category.class);
                pickedCat = category.getCategory();
                if (pickedCat != null){
                    chooseCat.setVisibility(View.GONE);
                    if (pickedCat != null){
                        catOfChoice.setVisibility(View.VISIBLE);
                        catOfChoice.setText(pickedCat);
                    }
                    if (County != null | Category != null){
                        SearchCat.setBackgroundResource(R.drawable.btn_round_blue);
                        SearchCat.setTextColor(Color.parseColor("#2BB66A"));
                    }
                    // FetchProduct();
                }

            }
        });

    }


    private void FetchCounty() {
        Query query = CountyRef.orderBy("county",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Counties> transaction = new FirestoreRecyclerOptions.Builder<Counties>()
                .setQuery(query, Counties.class)
                .setLifecycleOwner(this)
                .build();
        countyAdapter = new CountyAdapter(transaction);
        recyclerCounty.setHasFixedSize(true);
        recyclerCounty.setNestedScrollingEnabled(false);
        LinearLayoutManager LayoutManager
                = new LinearLayoutManager(MainViewActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerCounty.setLayoutManager(new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.HORIZONTAL));
        recyclerCounty.setAdapter(countyAdapter);
        countyAdapter.setOnItemClickListener(new CountyAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Counties counties = documentSnapshot.toObject(Counties.class);
                County = counties.getCounty();
                if (County != null | Category != null){
                    SearchCat.setBackgroundResource(R.drawable.btn_round_blue);
                    SearchCat.setTextColor(Color.parseColor("#2BB66A"));
                }
            }
        });

    }


    private void FetchCategory() {

        FirestoreRecyclerOptions<Category> transaction = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(CategoryRef, Category.class)
                .setLifecycleOwner(this)
                .build();
        categoryAdapter = new CategoryAdapter(transaction);
        mRecyclerViewCategory.setHasFixedSize(true);
        mRecyclerViewCategory.setNestedScrollingEnabled(false);
        LinearLayoutManager LayoutManager
                = new LinearLayoutManager(MainViewActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewCategory.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.HORIZONTAL));
        mRecyclerViewCategory.setAdapter(categoryAdapter);
        categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Category category = documentSnapshot.toObject(Category.class);
                Category = category.getCategory();
                if (County != null | Category != null){
                    SearchCat.setBackgroundResource(R.drawable.btn_round_blue);
                    SearchCat.setTextColor(Color.parseColor("#2BB66A"));
                }
            }
        });

    }


    private void FetchProduct() {
        if (Category.equals("")){

            Query query = TopFindProRef
                    .orderBy("date_registered", Query.Direction.DESCENDING).limit(30);
            FirestoreRecyclerOptions<TopFindProviders> transaction = new FirestoreRecyclerOptions.Builder<TopFindProviders>()
                    .setQuery(query, TopFindProviders.class)
                    .setLifecycleOwner(this)
                    .build();
            adapter = new ProvidersAdapter(transaction);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setNestedScrollingEnabled(false);
            LinearLayoutManager LayoutManager
                    = new LinearLayoutManager(MainViewActivity.this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
            mRecyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new ProvidersAdapter.OnItemCickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    TopFindProviders topFindProviders = documentSnapshot.toObject(TopFindProviders.class);
                    Requsername = topFindProviders.getUser_name();
                    Reqemail = topFindProviders.getEmail();
                    ReQuestLocation = topFindProviders.getLocation();
                    ReqProfileImage = topFindProviders.getProfile_image();
                    Reqphone = topFindProviders.getPhone();
                    RequestID = topFindProviders.getUser_ID();
                    ReqProfession = topFindProviders.getProfession();
                    RequestDialog();

                }
            });
        }else if (Category != null |County != null){

            Query query = TopFindProRef.whereEqualTo("Profession",Category)
                    .whereEqualTo("location",County)
                    .whereEqualTo("ratings",RatingSearch)
                    .whereEqualTo("Experience",SelectedExp)
                    .orderBy("date_registered", Query.Direction.DESCENDING).limit(30);
            FirestoreRecyclerOptions<TopFindProviders> transaction = new FirestoreRecyclerOptions.Builder<TopFindProviders>()
                    .setQuery(query, TopFindProviders.class)
                    .setLifecycleOwner(this)
                    .build();
            adapter = new ProvidersAdapter(transaction);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setNestedScrollingEnabled(false);
            LinearLayoutManager LayoutManager
                    = new LinearLayoutManager(MainViewActivity.this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
            mRecyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new ProvidersAdapter.OnItemCickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    TopFindProviders topFindProviders = documentSnapshot.toObject(TopFindProviders.class);
                    Requsername = topFindProviders.getUser_name();
                    Reqemail = topFindProviders.getEmail();
                    ReQuestLocation = topFindProviders.getLocation();
                    ReqProfileImage = topFindProviders.getProfile_image();
                    Reqphone = topFindProviders.getPhone();
                    RequestID = topFindProviders.getUser_ID();
                    ReqProfession = topFindProviders.getProfession();
                    RequestDialog();

                }
            });

        }else {
            ToastBack("Select all categories ");
        }




    }

    //----Load details---//
    private String FuserName,Femail,Flocation,Fprofession,Fnumber,FuserImage;
    private long noOfCandidates;
    private void LoadDetails() {

        TopFindRef.document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()){
                    TopFinders topFinders = documentSnapshot.toObject(TopFinders.class);
                    FuserImage = topFinders.getProfile_image();
                    Femail = topFinders.getEmail();
                    Flocation = topFinders.getLocation();
                    Fnumber = topFinders.getPhone();
                    FuserName = topFinders.getUser_name();


                    if (FuserImage != null){
                        Picasso.with(getApplicationContext())
                                .load(FuserImage).placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .into(profileImage);
                    }




                }else {

                    ToastBack(e.getMessage());
                }
            }
        });

    }
    //...end load details

    String IID;
    private Snackbar snackbar;
    private void SendRequest(String msg ,String id){
        String ID =  FindRequestRef.document().getId();
        HashMap<String,Object> request = new HashMap<>();
        request.put("User_name",FuserName);
        request.put("Email",Femail);
        request.put("Phone",Fnumber);
        request.put("location",Flocation);
        request.put("User_ID",id);
        request.put("Request_ID",ID);
        request.put("Request_message",msg);
        request.put("Sender_ID",mAuth.getCurrentUser().getUid());
        request.put("timestamp", FieldValue.serverTimestamp());
        request.put("Profile_image",FuserImage);



        FindRequestRef.document(mAuth.getCurrentUser().getUid()).set(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Notify(id);
                    if (dialog_sendRequest != null)dialog_sendRequest.dismiss();
                    if (dialog_postJob != null)dialog_postJob.dismiss();

                    snackbar = Snackbar.make(relativeLayout, "Request sent successful", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("NOTIFY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           // Notify(id);
                        }
                    });

                    snackbar.show();




                }else {
                   // ToastBack(task.getException().getMessage());
                    if (dialog_sendRequest != null)dialog_sendRequest.dismiss();
                    if (dialog_postJob != null)dialog_postJob.dismiss();
                }
            }
        });




    }


    private void Notify(String id){
        HashMap<String ,Object> notify = new HashMap<>();
        notify.put("title","New job request");
        notify.put("description","You have received new request from "+FuserName);
        notify.put("to",id);
        notify.put("from",mAuth.getCurrentUser().getUid());
        notify.put("timestamp",FieldValue.serverTimestamp());

        HashMap<String ,Object> notify2 = new HashMap<>();
        notify2.put("title","New job request");
        notify2.put("description","You have sent a job request to "+Requsername);
        notify2.put("to",id);
        notify2.put("from",mAuth.getCurrentUser().getUid());
        notify2.put("timestamp",FieldValue.serverTimestamp());


        TopFindRef.document(mAuth.getCurrentUser().getUid()).collection("Notifications")
                .document().set(notify2)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    TopFindProRef.document(id).collection("Notifications")
                            .document().set(notify).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                snackbar.dismiss();
                            }else {
                                ToastBack(task.getException().getMessage());
                                if (dialog_sendRequest != null)dialog_sendRequest.dismiss();
                                if (dialog_postJob != null)dialog_postJob.dismiss();
                            }
                        }
                    });

                }else {

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

            if(getSupportFragmentManager().findFragmentById(R.id.Frame_main) != null) {
                getSupportFragmentManager()
                        .beginTransaction().
                        remove(getSupportFragmentManager().findFragmentById(R.id.Frame_main)).commit();
            }

        }
        backPressedTime = System.currentTimeMillis();
    }
}