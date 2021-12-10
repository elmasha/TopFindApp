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
import android.widget.RatingBar;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.intech.topfindprovider.Adapters.CurrentJobsAdapter;
import com.intech.topfindprovider.Adapters.CurrentJobsAdapter2;
import com.intech.topfindprovider.Models.CurrentJobs;
import com.intech.topfindprovider.R;


public class ProviderJobsFragment extends Fragment {
View root;
    private RecyclerView recyclerViewJobs;
    private RatingBar ratingBar;
    private CurrentJobsAdapter2 adapter;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindRef = db.collection("TopFind_Provider");
    CollectionReference CurrentJobRef = db.collection("Current_clients");
    private SwipeRefreshLayout swipeRefreshLayout;

    public ProviderJobsFragment() {
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
        mAuth = FirebaseAuth.getInstance();
        root = inflater.inflate(R.layout.fragment_provider_jobs, container, false);
        recyclerViewJobs = root.findViewById(R.id.recycler_p_jobs);
        swipeRefreshLayout = root.findViewById(R.id.SwipeRefresh_p_jobs);
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

        Query query =  TopFindRef.document(mAuth.getCurrentUser().getUid())
                .collection("Current_clients")
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(30);
        FirestoreRecyclerOptions<CurrentJobs> transaction = new FirestoreRecyclerOptions.Builder<CurrentJobs>()
                .setQuery(query, CurrentJobs.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new CurrentJobsAdapter2(transaction);
        recyclerViewJobs.setHasFixedSize(true);
        recyclerViewJobs.setNestedScrollingEnabled(false);
        LinearLayoutManager LayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewJobs.setLayoutManager(LayoutManager);
        recyclerViewJobs.setAdapter(adapter);

        adapter.setOnItemClickListener(new CurrentJobsAdapter2.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {


            }
        });



    }

}