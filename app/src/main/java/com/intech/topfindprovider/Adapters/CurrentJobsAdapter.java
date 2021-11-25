package com.intech.topfindprovider.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.intech.topfindprovider.Models.CurrentJobs;
import com.intech.topfindprovider.R;
import com.intech.topfindprovider.TimeAgo;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentJobsAdapter extends FirestoreRecyclerAdapter<CurrentJobs, CurrentJobsAdapter.CurrentJobsViewHolder> {

    private OnItemCickListener listener;
    public List<CurrentJobs> candidatesList;
    public Context context;


    public CurrentJobsAdapter(@NonNull FirestoreRecyclerOptions<CurrentJobs> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CurrentJobsViewHolder holder, int position, @NonNull CurrentJobs model) {
        holder.Name.setText(model.getUser_name());
        holder.location.setText(model.getLocation());
        holder.category.setText(model.getCategory());

        if(context != null | model.getProfile_image() != null) {
            Picasso.with(context).load(model.getProfile_image())
                    .placeholder(R.drawable.load).error(R.drawable.errorimage).into(holder.profile);
        }

    }

    @NonNull
    @Override
    public CurrentJobsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_row,parent,false);
        this.context = parent.getContext();
        return new CurrentJobsViewHolder(v);
    }


    ///Delete item
    public void deleteItem (int position) {
     getSnapshots().getSnapshot(position).getReference().delete();
    }

    class CurrentJobsViewHolder extends RecyclerView.ViewHolder{
       private TextView Name, location,category;
       private CircleImageView profile;
       private View view;

        public CurrentJobsViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.current_row_username);
            profile = itemView.findViewById(R.id.current_row_image);
            location = itemView.findViewById(R.id.current_row_location);
            category = itemView.findViewById(R.id.current_row_category);

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
