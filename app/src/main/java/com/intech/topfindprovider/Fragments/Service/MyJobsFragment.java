package com.intech.topfindprovider.Fragments.Service;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.intech.topfindprovider.Activities.Service.ViewRequestActivity;
import com.intech.topfindprovider.Adapters.CurrentJobsAdapter;
import com.intech.topfindprovider.Models.CurrentJobs;
import com.intech.topfindprovider.R;


public class MyJobsFragment extends Fragment {
View root;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindRef = db.collection("TopFind_Clients");
    CollectionReference CurrentJobRef = db.collection("Current_clients");
    private RecyclerView recyclerViewJobs;
    private CurrentJobsAdapter adapter;
    private FloatingActionButton editBtn;
    private int editState = 0;
    private LinearLayout editLayout,primeLayout,editButton;
    private Button BtnSaveChanges;
    private View CloseActive;

    public MyJobsFragment() {
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
       root = inflater.inflate(R.layout.fragment_my_jobs, container, false);
       mAuth = FirebaseAuth.getInstance();
        recyclerViewJobs= root.findViewById(R.id.recycler_active_jobs2);
        CloseActive = root.findViewById(R.id.CloseActive);

        CloseActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity().getSupportFragmentManager().findFragmentById(R.id.Frame_main) != null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction().remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.Frame_main)).commit();
                }
            }
        });

        return root;
    }


    private void FetchProduct() {

        Query query =
                TopFindRef.document(mAuth.getCurrentUser().getUid())
                        .collection("Current_workers")
                        .whereEqualTo("job_ID",mAuth.getCurrentUser().getUid())
                        .orderBy("timestamp", Query.Direction.DESCENDING).limit(30);
        FirestoreRecyclerOptions<CurrentJobs> transaction = new FirestoreRecyclerOptions.Builder<CurrentJobs>()
                .setQuery(query, CurrentJobs.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new CurrentJobsAdapter(transaction);
        recyclerViewJobs.setHasFixedSize(true);
        recyclerViewJobs.setNestedScrollingEnabled(false);
        LinearLayoutManager LayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewJobs.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerViewJobs.setAdapter(adapter);

        adapter.setOnItemClickListener(new CurrentJobsAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                CurrentJobs  topFindProviders = documentSnapshot.toObject(CurrentJobs.class);

                String UID = topFindProviders.getUser_ID();
                if (UID != null){
                    Intent intent = new Intent(getContext(), ViewRequestActivity.class);
                    intent.putExtra("ID",UID);
                    startActivity(intent);
                }


            }
        });





    }

}