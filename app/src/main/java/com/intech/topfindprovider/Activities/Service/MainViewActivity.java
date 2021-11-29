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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Type;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
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
import com.intech.topfindprovider.Adapters.ProvidersAdapter;
import com.intech.topfindprovider.Fragments.Service.FinderNotificationFragment;
import com.intech.topfindprovider.MainActivity;
import com.intech.topfindprovider.Models.Category;
import com.intech.topfindprovider.Models.TopFindProviders;
import com.intech.topfindprovider.Models.TopFinders;
import com.intech.topfindprovider.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainViewActivity extends AppCompatActivity {
    private long backPressedTime;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindRef = db.collection("TopFind_Clients");
    CollectionReference TopFindProRef = db.collection("TopFind_Provider");
    CollectionReference FindRequestRef = db.collection("TopFind_Request");
    CollectionReference CategoryRef = db.collection("Category");

    private CircleImageView profileImage;


    private SwipeRefreshLayout swipeRefreshLayout;
    private FrameLayout catLayout;
    private LinearLayout linearLayoutSearch;
    private ProvidersAdapter adapter;
    private CategoryAdapter categoryAdapter;
    private RecyclerView mRecyclerView,mRecyclerView2,recyclerViewPost;
    private TextView chooseCat,SearchCategory;
    private String Category = "";
    private FloatingActionButton SearchCat,postJob;
    private RelativeLayout relativeLayout;
    private ImageView imageViewNotify;


    private int dropDownState = 0;
    private int NotifyState = 0;
    @Override
    protected void onStart() {
        super.onStart();
        FetchProduct();
        FetchCategory();
        LoadDetails();
    }



    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        mAuth = FirebaseAuth.getInstance();
        profileImage = findViewById(R.id.ProfilePicture);
        swipeRefreshLayout = findViewById(R.id.SwipeRefresh);
        mRecyclerView = findViewById(R.id.recycler_provider);
        mRecyclerView2 = findViewById(R.id.recycler_category);
        chooseCat = findViewById(R.id.choose_category);
        catLayout = findViewById(R.id.FrameCategory);
        SearchCat = findViewById(R.id.SearchCategory);
        SearchCategory = findViewById(R.id.searchCat);
        linearLayoutSearch = findViewById(R.id.SearchLayout);
        relativeLayout = findViewById(R.id.relative);
        imageViewNotify = findViewById(R.id.Notification);


        dl = (DrawerLayout) findViewById(R.id.drawerMenu);
//        dl.closeDrawer(GravityCompat.END);


        nv = (NavigationView) findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.account:
                        Toast.makeText(MainViewActivity.this, "My Account", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.myJobs:
                        Toast.makeText(MainViewActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.mycart:
                        Toast.makeText(MainViewActivity.this, "My Cart", Toast.LENGTH_SHORT).show();
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
                if (Category != null){
                    FetchProduct();
                }

            }
        });





        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Category = "";
                chooseCat.setText("Select category");
                FetchCategory();
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
                                SendRequest(inputText,topFindProviders.getUser_ID());
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
    private Button BtnConfirm;
    private CircleImageView ReQuestImage;
    private LinearLayout google,facebook;
    private ProgressBar progressBarMpesa;

    private void RequestDialog(){
        final  AlertDialog.Builder mbuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_send_request, null);
        mbuilder.setView(mView);
        dialog_sendRequest = mbuilder.create();
        dialog_sendRequest.show();
        emailInput = mView.findViewById(R.id.EmailRequest);
        usernameInput = mView.findViewById(R.id.NameRequest);
        BtnConfirm = mView.findViewById(R.id.SendRequest);
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

        BtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReqMessage = ReText.getText().toString().trim();
                if (ReqMessage.isEmpty()){
                    ToastBack("Enter a message");
                }else {
                    SendRequest(ReqMessage,RequestID);
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
        recyclerViewPost.setLayoutManager(LayoutManager);
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
                    // FetchProduct();
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
        mRecyclerView2.setHasFixedSize(true);
        mRecyclerView2.setNestedScrollingEnabled(false);
        LinearLayoutManager LayoutManager
                = new LinearLayoutManager(MainViewActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView2.setLayoutManager(LayoutManager);
        mRecyclerView2.setAdapter(categoryAdapter);
        categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Category category = documentSnapshot.toObject(Category.class);
                Category = category.getCategory();
                if (Category != null){
                    FetchProduct();
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
        }else if (Category != null){


            Query query = TopFindProRef.whereEqualTo("Profession",Category)
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
                    if (dialog_sendRequest != null)dialog_sendRequest.dismiss();
                    if (dialog_postJob != null)dialog_postJob.dismiss();

                    snackbar = Snackbar.make(relativeLayout, "Request sent successful", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("NOTIFY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Notify(id);
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
        notify.put("title","New Request");
        notify.put("description","You have received new Request from "+FuserName);
        notify.put("to",id);
        notify.put("from",mAuth.getCurrentUser().getUid());
        notify.put("timestamp",FieldValue.serverTimestamp());

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