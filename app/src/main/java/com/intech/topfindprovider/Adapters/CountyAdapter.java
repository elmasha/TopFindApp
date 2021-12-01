package com.intech.topfindprovider.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.intech.topfindprovider.Models.Counties;
import com.intech.topfindprovider.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CountyAdapter extends FirestoreRecyclerAdapter<Counties, CountyAdapter.CountyViewHolder> {

    private OnItemCickListener listener;
    public Context context;


    public CountyAdapter(@NonNull FirestoreRecyclerOptions<Counties> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CountyViewHolder holder, int position, @NonNull Counties model) {
        holder.Name.setText(model.getCounty());
        holder.county.setText(model.getNo()+"");


    }

    @NonNull
    @Override
    public CountyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.county_row,parent,false);
        this.context = parent.getContext();
        return new CountyViewHolder(v);
    }


    ///Delete item
    public void deleteItem (int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class CountyViewHolder extends RecyclerView.ViewHolder{
        private TextView Name, county, ward,mobile,age;
        private CircleImageView profile;
        private View viewOn;

        public CountyViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.county);
            county = itemView.findViewById(R.id.county_no);
            viewOn = itemView.findViewById(R.id.onView2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.setBackgroundResource(R.drawable.border_green);
                    viewOn.setVisibility(View.VISIBLE);
                    Name.setTextColor(Color.parseColor("#2BB66A"));
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
