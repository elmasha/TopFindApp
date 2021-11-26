package com.intech.topfindprovider.Activities.Service;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
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
import com.hsalf.smilerating.SmileRating;
import com.hsalf.smileyrating.SmileyRating;
import com.intech.topfindprovider.Models.TopFindProviders;
import com.intech.topfindprovider.Models.TopFinders;
import com.intech.topfindprovider.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewRequestActivity extends AppCompatActivity {
    private String ID;
    private TextView UserName,Email,Phone,Location,logout,Narration,Profession,Experience;
    private CircleImageView ProfileImage;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TopFindProviderRef = db.collection("TopFind_Provider");
    CollectionReference TopFindRef = db.collection("TopFind_Clients");

    CollectionReference CurrentJobRef = db.collection("Current_clients");
    private FloatingActionButton Rate,Delete,Call,Sms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);
    if (getIntent() != null){
        ID = getIntent().getStringExtra("ID");

    }

        mAuth = FirebaseAuth.getInstance();
        UserName = findViewById(R.id.TpView_name);
        Email = findViewById(R.id.TpView_email);
        Phone = findViewById(R.id.TpView_phone);
        Location = findViewById(R.id.TpView_location);
        ProfileImage = findViewById(R.id.TpView_userImage);
        Narration = findViewById(R.id.TpView_narration);
        Profession = findViewById(R.id.TpView_profession);
        Experience = findViewById(R.id.TpView_expereince);
        Rate = findViewById(R.id.RateProvider);
        Delete = findViewById(R.id.DeleteProvider);
        Call = findViewById(R.id.CallProvider);
        Sms = findViewById(R.id.SmsProvider);


        Sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RateDialogue();
            }
        });
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        LoadDetails();
        LoadDetails2();
    }

    private AlertDialog dialog_rate;
    private SmileyRating smileyRating;
    private Button BtnSubmitRates;
    private EditText inputReview;
    private String review;
    private  int rating;

    private void RateDialogue() {
        final  AlertDialog.Builder mbuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_rate, null);
        mbuilder.setView(mView);
        dialog_rate = mbuilder.create();

        smileyRating = mView.findViewById(R.id.ratingBarProvider);
        inputReview = mView.findViewById(R.id.review_input);
        BtnSubmitRates = mView.findViewById(R.id.Rating_done);
        inputReview.setHint("Write something about " +userName+"...");

        smileyRating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                // You can compare it with rating Type
                if (SmileyRating.Type.GREAT == type) {

                }
                // You can get the user rating too
                // rating will between 1 to 5
                 rating = type.getRating();
            }
        });

        BtnSubmitRates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                review = inputReview.getText().toString().trim();
                if (review.isEmpty() | rating == 0){
                    showSnackBackOffline(getApplicationContext(),"Review field is empty");
                }else {
                    SubmitRatings(review);
                }

            }
        });

        dialog_rate.show();
    }

    private void SubmitRatings(String  review) {

        HashMap<String,Object> myReviews = new HashMap<>();
        myReviews.put("User_name",userName2);
        myReviews.put("User_image",userImage2);
        myReviews.put("Review",review);
        myReviews.put("Ratings",rating);
        myReviews.put("timestamp", FieldValue.serverTimestamp());


        TopFindProviderRef.document(ID)
                .collection("Review & ratings").document()
                .set(myReviews).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    showSnackBarOnline(getBaseContext(),"Rate & review submitted successful");
                    if (dialog_rate != null)dialog_rate.dismiss();
                }else {
                    showSnackBackOffline(getBaseContext(),task.getException().getMessage());
                }
            }
        });

    }

    //----InterNet Connection----
    public void showSnackBackOffline(Context context, String msg) {
        Snackbar.with(this,null).type(Type.ERROR).message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.CENTER).show();
    }

    public void showSnackBarOnline(Context context,String msg) {

        Snackbar.with(this, null).type(Type.SUCCESS).message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.CENTER).show();

    }


    private String userName2,email2,phone2,location2,userImage2;

    private void LoadDetails2() {

        TopFindRef.document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()){
                    TopFinders topFinders = documentSnapshot.toObject(TopFinders.class);
                    userImage2 = topFinders.getProfile_image();
                    userName2 = topFinders.getUser_name();
                    email2 = topFinders.getEmail();
                    phone2 = topFinders.getPhone();
                    location2 = topFinders.getLocation();


                }
            }
        });

    }
    //...end load details



    private String userName,email,phone,location,userImage,narration,profession,experience;

    private void LoadDetails() {

        TopFindProviderRef.document(ID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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


                    UserName.setText(userName);
                    Email.setText(email);
                    Location.setText(location);
                    Phone.setText(phone);
                    Narration.setText(narration);
                    Profession.setText(profession);
                    Experience.setText(experience);

                    if (userImage != null){
                        Picasso.with(getApplicationContext())
                                .load(userImage).placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .into(ProfileImage);
                    }




                }
            }
        });

    }
    //...end load details
}