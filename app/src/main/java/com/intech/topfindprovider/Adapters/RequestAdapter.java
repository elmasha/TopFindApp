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
import com.intech.topfindprovider.Models.TopFindProviders;
import com.intech.topfindprovider.Models.TopFindRequest;
import com.intech.topfindprovider.R;
import com.intech.topfindprovider.TimeAgo;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends FirestoreRecyclerAdapter<TopFindRequest, RequestAdapter.ProviderViewHolder> {

    private OnItemCickListener listener;
    public List<TopFindProviders> candidatesList;
    public Context context;


    public RequestAdapter(@NonNull FirestoreRecyclerOptions<TopFindRequest> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProviderViewHolder holder, int position, @NonNull TopFindRequest model) {
        holder.Name.setText(model.getUser_name());
        holder.email.setText(model.getEmail());
        holder.location.setText(model.getLocation());
        holder.time.setText(TimeAgo.getTimeAgo(model.getTimestamp().getTime()));


        if(context != null | model.getProfile_image() != null) {
            Picasso.with(context).load(model.getProfile_image())
                    .placeholder(R.drawable.load).error(R.drawable.errorimage).into(holder.profile);
        }


    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_row,parent,false);
        this.context = parent.getContext();
        return new ProviderViewHolder(v);
    }


    ///Delete item
    public void deleteItem (int position) {
     getSnapshots().getSnapshot(position).getReference().delete();
    }

    class ProviderViewHolder extends RecyclerView.ViewHolder{
       private TextView Name,email, location,time;
       private CircleImageView profile;
       private View view;

        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.request_name);

            email = itemView.findViewById(R.id.request_email);
            profile = itemView.findViewById(R.id.request_image);
            time = itemView.findViewById(R.id.request_time);
            location = itemView.findViewById(R.id.request_location);

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
