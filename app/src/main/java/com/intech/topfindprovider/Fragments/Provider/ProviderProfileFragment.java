package com.intech.topfindprovider.Fragments.Provider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
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
import com.intech.topfindprovider.Activities.Service.MainViewActivity;
import com.intech.topfindprovider.Adapters.CurrentJobsAdapter;
import com.intech.topfindprovider.Adapters.ProvidersAdapter;
import com.intech.topfindprovider.MainActivity;
import com.intech.topfindprovider.Models.CurrentJobs;
import com.intech.topfindprovider.Models.TopFindProviders;
import com.intech.topfindprovider.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProviderProfileFragment extends Fragment {
private View root;
    private TextView UserName,Email,Phone,Location,logout,Narration,Profession,Experience,closeEdit;
    private EditText EditUserName,EditEmail,EditPhone,EditLocation,EditNarration,EditProfession,EditExperience;
    private CircleImageView ProfileImage;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindRef = db.collection("TopFind_Provider");
    CollectionReference CurrentJobRef = db.collection("Current_clients");
    private RecyclerView recyclerViewJobs;
    private RatingBar ratingBar;
    private CurrentJobsAdapter adapter;
    private FloatingActionButton editProfile;
    private LinearLayout linearLayoutProfile,layoutEditButton;
    private Button BtnSaveChanges;
    private int EdtState = 0;
    @Override
    public void onStart() {
        super.onStart();
        LoadDetails();
        FetchProduct();
    }

    public ProviderProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_provider_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        UserName = root.findViewById(R.id.Tp_name);
        Email = root.findViewById(R.id.Tp_email);
        Phone = root.findViewById(R.id.Tp_phone);
        Location = root.findViewById(R.id.Tp_location);
        ProfileImage = root.findViewById(R.id.Tp_userImage);
        Narration = root.findViewById(R.id.Tp_narration);
        logout = root.findViewById(R.id.LogOut);
        Profession =root.findViewById(R.id.Tp_profession);
        Experience = root.findViewById(R.id.Tp_experince);
        ratingBar = root.findViewById(R.id.ratingBarProfile);
        recyclerViewJobs= root.findViewById(R.id.recycler_current_jobs);
        editProfile =  root.findViewById(R.id.edit_Tf_profile2);
        linearLayoutProfile = root.findViewById(R.id.EditView2);

        EditEmail = root.findViewById(R.id.edit_Tf_email2);
        EditUserName = root.findViewById(R.id.edit_Tf_name2);
        EditLocation = root.findViewById(R.id.edit_Tf_location2);
        EditPhone = root.findViewById(R.id.edit_Tf_phone2);
        EditNarration =root.findViewById(R.id.edit_Tf_narration2);
        EditProfession = root.findViewById(R.id.edit_Tf_profession2);

        BtnSaveChanges = root.findViewById(R.id.edit_Tf_saveChanges2);
        layoutEditButton = root.findViewById(R.id.editButton2);
        closeEdit = root.findViewById(R.id.closeEdit2);


        closeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EdtState == 1){
                    linearLayoutProfile.setVisibility(View.VISIBLE);
                    layoutEditButton.setVisibility(View.GONE);
                    EdtState = 0;
                }else if (EdtState ==0 ){
                    linearLayoutProfile.setVisibility(View.GONE);
                    layoutEditButton.setVisibility(View.VISIBLE);
                    EdtState = 1;
                }
            }
        });

        BtnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validation()){
                }else {
                    SaveChanges();
                }
            }
        });


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EdtState == 0){
                    linearLayoutProfile.setVisibility(View.VISIBLE);
                    layoutEditButton.setVisibility(View.GONE);
                    EdtState = 1;
                }else if (EdtState ==1 ){
                    linearLayoutProfile.setVisibility(View.GONE);
                    layoutEditButton.setVisibility(View.VISIBLE);
                    EdtState = 0;
                }
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout_Alert();
            }
        });

        LoadDetails();
        return root;
    }


    private void SaveChanges() {

        HashMap<String,Object> store = new HashMap<>();
        store.put("User_name",userName);
        store.put("Email",email);
        store.put("Phone",phone);
        store.put("location",location);
        store.put("Narration",narration);
        store.put("Profession",profession);


        TopFindRef.document(mAuth.getCurrentUser().getUid()).update(store).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    showSnackBarOnline(getContext(),"Saved changes..");
                    if (EdtState == 0){
                        linearLayoutProfile.setVisibility(View.VISIBLE);
                        layoutEditButton.setVisibility(View.GONE);
                        EdtState = 1;
                    }else if (EdtState ==1 ){
                        linearLayoutProfile.setVisibility(View.GONE);
                        layoutEditButton.setVisibility(View.VISIBLE);
                        EdtState = 0;
                    }

                }else {

                    showSnackBackOffline(getContext(),task.getException().getMessage());


                }
            }
        });
    }



    //----InterNet Connection----
    public void showSnackBackOffline(Context context, String msg) {
        Snackbar.with(getContext(),null).type(Type.ERROR).message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.CENTER).show();
    }

    public void showSnackBarOnline(Context context,String msg) {

        Snackbar.with(getContext(), null).type(Type.SUCCESS).message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.CENTER).show();

    }

    private void FetchProduct() {

            Query query =  TopFindRef.document(mAuth.getCurrentUser().getUid())
                    .collection("Current_clients")
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
            recyclerViewJobs.setLayoutManager(LayoutManager);
            recyclerViewJobs.setAdapter(adapter);

            adapter.setOnItemClickListener(new CurrentJobsAdapter.OnItemCickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {


                }
            });



    }


    private boolean validation(){
        userName = EditUserName.getText().toString();
        email = EditEmail.getText().toString();
        phone = EditPhone.getText().toString();
        location = EditLocation.getText().toString();
        narration = EditNarration.getText().toString();
        profession = EditProfession.getText().toString();


        if (userName.isEmpty()){
            EditUserName.setError("Provide your full name");
            return false;
        }
        else if (email.isEmpty()){
            EditEmail.setError("Provide your email.");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EditEmail.setError("Please enter a Valid email");
            return false;
        }else if (narration.isEmpty()){
            EditNarration.setError("Provide your narration.");
            return false;
        }else if (profession.isEmpty()){
            EditProfession.setError("Provide your profession.");
            return false;
        }
        else if (phone.isEmpty()){
            EditPhone.setError("Provide your phone number.");
            return false;
        }else if (location.isEmpty()){
            EditLocation.setError("Provide your location.");
            return false;
        }
        else{

            return true;
        }

    }


    private AlertDialog dialog2;
    public void Logout_Alert() {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                    Intent logout = new Intent(getContext(), MainActivity.class);
                    logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(logout);
                    dialog2.dismiss();

                }else {

                    ToastBack( task.getException().getMessage());

                }

            }
        });

    }


    private String userName,email,phone,location,userImage,narration,profession,experience;
    private long rates;

    private void LoadDetails() {

        TopFindRef.document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                    experience = topFinders.getExperience();
                    rates = topFinders.getRatings();

                    EditUserName.setText(userName);
                    EditEmail.setText(email);
                    EditLocation.setText(location);
                    EditNarration.setText(narration);
                    EditPhone.setText(phone);
                    EditProfession.setText(profession);


                    UserName.setText(userName);
                    Email.setText(email);
                    Location.setText(location);
                    Phone.setText(phone);
                    Narration.setText(narration);
                    Profession.setText(profession);
                    Experience.setText(experience);
                    ratingBar.setRating(Float.parseFloat(rates+""));

                    if (userImage != null){
                        Picasso.with(getContext())
                                .load(userImage).placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .into(ProfileImage);
                    }




                }
            }
        });

    }
    //...end load details


    private Toast backToast;
    private void ToastBack(String message){
        backToast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        backToast.show();
    }
}