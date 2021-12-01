package com.intech.topfindprovider.Fragments.Service;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intech.topfindprovider.R;


public class FinderNotificationFragment extends Fragment {
View root;
    private View CloseNotify;

    public FinderNotificationFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_finder_notification, container, false);
        CloseNotify = root.findViewById(R.id.CloseNotify);

        CloseNotify.setOnClickListener(new View.OnClickListener() {
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
}