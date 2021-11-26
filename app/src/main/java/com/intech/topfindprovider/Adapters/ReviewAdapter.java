package com.intech.topfindprovider.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.intech.topfindprovider.Models.CurrentJobs;
import com.intech.topfindprovider.Models.Review;
import com.intech.topfindprovider.R;
import com.intech.topfindprovider.TimeAgo;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends FirestoreRecyclerAdapter<Review, ReviewAdapter.ReviewViewHolder> {

    private OnItemCickListener listener;
    public List<CurrentJobs> candidatesList;
    public Context context;


    public ReviewAdapter(@NonNull FirestoreRecyclerOptions<Review> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReviewViewHolder holder, int position, @NonNull Review model) {
        holder.Name.setText(model.getUser_name());
        holder.time.setText(TimeAgo.getTimeAgo(model.getTimestamp().getTime()));
        holder.review.setText(model.getReview());
        holder.ratingBar.setRating(Float.parseFloat(model.getRatings()+""));

        if(context != null | model.getUser_image() != null) {
            Picasso.with(context).load(model.getUser_image())
                    .placeholder(R.drawable.user).error(R.drawable.user).into(holder.profile);
        }

    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_row,parent,false);
        this.context = parent.getContext();
        return new ReviewViewHolder(v);
    }



    ///Delete item
    public void deleteItem (int position) {
     getSnapshots().getSnapshot(position).getReference().delete();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{
       private TextView Name,time,review;
       private CircleImageView profile;
       private RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.review_row_name);
            profile = itemView.findViewById(R.id.review_row_image);
            review = itemView.findViewById(R.id.review_row_comment);
            time = itemView.findViewById(R.id.review_row_date);
            ratingBar = itemView.findViewById(R.id.ratingBarReview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });



        }
    }


    public interface  OnItemCickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemCickListener listener){

        this.listener = listener;

    }




}
