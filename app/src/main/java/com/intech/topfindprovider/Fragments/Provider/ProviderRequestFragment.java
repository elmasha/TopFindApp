package com.intech.topfindprovider.Fragments.Provider;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.intech.topfindprovider.Activities.MainViewActivity;
import com.intech.topfindprovider.Activities.ProviderRegisterActivity;
import com.intech.topfindprovider.Adapters.ProvidersAdapter;
import com.intech.topfindprovider.Adapters.RequestAdapter;
import com.intech.topfindprovider.Models.TopFindProviders;
import com.intech.topfindprovider.Models.TopFindRequest;
import com.intech.topfindprovider.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProviderRequestFragment extends Fragment {
View root;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindRef = db.collection("TopFind_Provider");
    CollectionReference FindRequestRef = db.collection("TopFind_Request");

    private CircleImageView profileImage;

    private LinearLayout imageView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RequestAdapter adapter;
    private RecyclerView mRecyclerView;
    public ProviderRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        FetchProduct();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_provider_request, container, false);


        mAuth = FirebaseAuth.getInstance();
        profileImage = root.findViewById(R.id.ProfilePicture);
        swipeRefreshLayout = root.findViewById(R.id.SwipeRefresh_request);
        mRecyclerView = root.findViewById(R.id.recycler_request);
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



        return root;
    }

    private void FetchProduct() {

        Query query = FindRequestRef.whereEqualTo("User_ID",mAuth.getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(30);
        FirestoreRecyclerOptions<TopFindRequest> transaction = new FirestoreRecyclerOptions.Builder<TopFindRequest>()
                .setQuery(query, TopFindRequest.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new RequestAdapter (transaction);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager LayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(LayoutManager);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RequestAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                TopFindProviders topFindProviders = documentSnapshot.toObject(TopFindProviders.class);

            }
        });

    }
}