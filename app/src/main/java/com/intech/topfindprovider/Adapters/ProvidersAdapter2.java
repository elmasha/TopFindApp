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
import com.intech.topfindprovider.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProvidersAdapter2 extends FirestoreRecyclerAdapter<TopFindProviders, ProvidersAdapter2.ProviderViewHolder> {

    private OnItemCickListener listener;
    public List<TopFindProviders> candidatesList;
    public Context context;


    public ProvidersAdapter2(@NonNull FirestoreRecyclerOptions<TopFindProviders> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProviderViewHolder holder, int position, @NonNull TopFindProviders model) {
        holder.Name.setText(model.getUser_name());
        holder.category.setText(model.getProfession());
        holder.location.setText(model.getLocation());
//        holder.mobile.setText(model.getMobile_no());
//        holder.age.setText(model.getAge()+" yrs");
//        holder.status.setText(model.getStatus());

        if(context != null | model.getProfile_image() != null) {
            Picasso.with(context).load(model.getProfile_image()).placeholder(R.drawable.load).error(R.drawable.errorimage).into(holder.profile);
        }




    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_row,parent,false);
        this.context = parent.getContext();
        return new ProviderViewHolder(v);
    }


    ///Delete item
    public void deleteItem (int position) {
     getSnapshots().getSnapshot(position).getReference().delete();
    }

    class ProviderViewHolder extends RecyclerView.ViewHolder{
       private TextView Name, category, location,mobile,age,status;
       private CircleImageView profile;
       private View view;

        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.row_username);

            category = itemView.findViewById(R.id.row_category);
            profile = itemView.findViewById(R.id.row_image);
            location = itemView.findViewById(R.id.row_location);
//            age = itemView.findViewById(R.id.row_age);
//            status = itemView.findViewById(R.id.row_status);

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
