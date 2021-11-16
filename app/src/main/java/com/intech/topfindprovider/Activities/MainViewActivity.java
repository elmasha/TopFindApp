package com.intech.topfindprovider.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.intech.topfindprovider.Adapters.ProvidersAdapter;
import com.intech.topfindprovider.Models.TopFindProviders;
import com.intech.topfindprovider.Models.TopFinders;
import com.intech.topfindprovider.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainViewActivity extends AppCompatActivity {
    private long backPressedTime;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindRef = db.collection("TopFind_Provider");

    private CircleImageView profileImage;

    private LinearLayout imageView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ProvidersAdapter adapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onStart() {
        super.onStart();
        LoadDetails();
        FetchProduct();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        mAuth = FirebaseAuth.getInstance();
        profileImage = findViewById(R.id.ProfilePicture);
        swipeRefreshLayout = findViewById(R.id.SwipeRefresh);
        mRecyclerView = findViewById(R.id.recycler_provider);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
            }
        });


    }



    private void FetchProduct() {

        //       Query query = TopFindRef
//                .orderBy("timestamp", Query.Direction.DESCENDING).limit(30);
        FirestoreRecyclerOptions<TopFindProviders> transaction = new FirestoreRecyclerOptions.Builder<TopFindProviders>()
                .setQuery(TopFindRef, TopFindProviders.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new ProvidersAdapter(transaction);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager LayoutManager
                = new LinearLayoutManager(MainViewActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(LayoutManager);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProvidersAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
//                Candidates  candidates = documentSnapshot.toObject(Candidates.class);
//                String id = documentSnapshot.getId();
//                Intent toUpdate = new Intent(getContext(), UpdateCandidateActivity.class);
//                toUpdate.putExtra("ID",id);
//                startActivity(toUpdate);

            }
        });

    }

    //----Load details---//
    private String userName,email,Bureau_Name,BureauImage,userImage;
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
                    userImage = topFinders.getProfile_image();

                    if (userImage != null){
                        Picasso.with(getApplicationContext())
                                .load(userImage).placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .into(profileImage);
                    }




                }
            }
        });

    }
    //...end load details


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