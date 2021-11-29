package com.intech.topfindprovider.Fragments.Provider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.intech.topfindprovider.Adapters.RequestAdapter;
import com.intech.topfindprovider.Models.TopFindProviders;
import com.intech.topfindprovider.Models.TopFindRequest;
import com.intech.topfindprovider.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProviderRequestFragment extends Fragment {
View root;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindRef = db.collection("TopFind_Clients");
    CollectionReference TopFindProviderRef = db.collection("TopFind_Provider");
    CollectionReference FindRequestRef = db.collection("TopFind_Request");
    CollectionReference CurrentJobRef = db.collection("Current_clients");

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
        LoadDetails();
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

    private String UID,SenderID;
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
                TopFindRequest topFindProviders = documentSnapshot.toObject(TopFindRequest.class);
                Reqemail = topFindProviders.getEmail();
                Requsername  = topFindProviders.getUser_name();
                Reqphone  = topFindProviders.getPhone();
                ReqProfileImage = topFindProviders.getProfile_image();
                ReQuestLocation = topFindProviders.getLocation();
                UID = topFindProviders.getUser_ID();
                SenderID = topFindProviders.getSender_ID();
                ReqMessage = topFindProviders.getRequest_message();
                RequestDialog();


            }
        });

    }



    private String userName,email,phone,location,userImage,narration,profession;

    private void LoadDetails() {

        TopFindProviderRef.document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()){
                    TopFindProviders topFinders = documentSnapshot.toObject(TopFindProviders.class);
                    userImage = topFinders.getProfile_image();
                    userName = topFinders.getUser_name();
                    email = topFinders.getEmail();
                    phone = topFinders.getPhone();
                    location = topFinders.getLocation();
                    narration = topFinders.getNarration();
                    profession = topFinders.getProfession();



                }
            }
        });

    }

    private AlertDialog dialog_sendRequest;
    private TextView emailInput,usernameInput,Location,ReCancel,ReProfession,Narration;
    private EditText ReText;
    private String Reqemail,Requsername,Reqphone,ReqMessage,ReqProfileImage,ReQuestLocation,RequestID,ReqProfession;
    private Button BtnConfirm;
    private CircleImageView ReQuestImage;
    private LinearLayout google,facebook;
    private ProgressBar progressBarMpesa;

    private void RequestDialog(){
        final  AlertDialog.Builder mbuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialog_my_request, null);
        mbuilder.setView(mView);
        dialog_sendRequest = mbuilder.create();
        dialog_sendRequest.show();
        emailInput = mView.findViewById(R.id.TpView_email);
        usernameInput = mView.findViewById(R.id.TpView_name);
        BtnConfirm = mView.findViewById(R.id.TpView_Accept);
        Location = mView.findViewById(R.id.TpView_location);
        ReCancel = mView.findViewById(R.id.TpView_Cancel);
        ReQuestImage = mView.findViewById(R.id.TpView_userImage);
        Narration =mView.findViewById(R.id.TpView_narration);
        ReProfession = mView.findViewById(R.id.TpView_profession);



        usernameInput.setText(Requsername);
        emailInput.setText(Reqemail);
        Location.setText(ReQuestLocation);
        ReProfession.setText(ReqProfession);
        Narration.setText(ReqMessage);
        if (ReqProfileImage != null){
            Picasso.with(getContext())
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
                SaveToCurrentJob();
            }

        });


    }

    private void SaveToCurrentJob() {
        HashMap<String,Object> store = new HashMap<>();
        store.put("User_name",Requsername);
        store.put("Email",Reqemail);
        store.put("Phone",Reqphone);
        store.put("location",ReQuestLocation);
        store.put("User_ID",UID);
        store.put("Profile_image",ReqProfileImage);
        store.put("job_ID",SenderID);
        store.put("timestamp", FieldValue.serverTimestamp());

        HashMap<String,Object> store2 = new HashMap<>();
        store2.put("User_name",userName);
        store2.put("Email",email);
        store2.put("Phone",phone);
        store2.put("location",location);
        store2.put("User_ID",mAuth.getCurrentUser().getUid());
        store2.put("Profile_image",userImage);
        store2.put("Category",profession);
        store2.put("job_ID",SenderID);
        store2.put("timestamp", FieldValue.serverTimestamp());




        TopFindRef.document(SenderID)
                .collection("Current_workers")
                .document(mAuth.getCurrentUser().getUid()).set(store2).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FindRequestRef.document(SenderID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                TopFindProviderRef.document(mAuth.getCurrentUser().getUid())
                                        .collection("Current_clients").document(SenderID).set(store)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    if (dialog_sendRequest != null)dialog_sendRequest.dismiss();
                                                    ToastBack("Request Accepted..");
                                                }else {
                                                    ToastBack(task.getException().getMessage());
                                                    if (dialog_sendRequest != null)dialog_sendRequest.dismiss();
                                                }
                                            }
                                        });
                            }else {
                                ToastBack(task.getException().getMessage());
                                if (dialog_sendRequest != null)dialog_sendRequest.dismiss();
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
        backToast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        backToast.show();
    }
}