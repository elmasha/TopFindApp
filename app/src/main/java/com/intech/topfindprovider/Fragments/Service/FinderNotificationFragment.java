package com.intech.topfindprovider.Fragments.Service;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.intech.topfindprovider.Adapters.NotificationAdapter;
import com.intech.topfindprovider.Models.Notification;
import com.intech.topfindprovider.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class FinderNotificationFragment extends Fragment {
View root;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindRef = db.collection("TopFind_Clients");
    CollectionReference FindRequestRef = db.collection("TopFind_Request");

    private CircleImageView profileImage;

    private LinearLayout imageView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private NotificationAdapter adapter;
    private RecyclerView mRecyclerView;

    private TextView CloseNotify;

    public FinderNotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        FetchNotification();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_finder_notification, container, false);
        mAuth = FirebaseAuth.getInstance();
        CloseNotify = root.findViewById(R.id.CloseNotify);
        mRecyclerView = root.findViewById(R.id.recycler_finder_notify);

        CloseNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity().getSupportFragmentManager().findFragmentById(R.id.Frame_main) != null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .remove(getActivity()
                                    .getSupportFragmentManager().findFragmentById(R.id.Frame_main)).commit();
                }
            }
        });

        FetchNotification();
        return root;
    }

    private void FetchNotification() {
        Query query =  TopFindRef.document(mAuth.getCurrentUser().getUid())
                .collection("Notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);
        FirestoreRecyclerOptions<Notification> transaction = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query, Notification.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new NotificationAdapter(transaction);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager LayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(LayoutManager);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new NotificationAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

            }
        });

    }
}