package com.intech.topfindprovider.Fragments.Service;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intech.topfindprovider.R;


public class FinderNotificationFragment extends Fragment {
View root;

    public FinderNotificationFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_finder_notification, container, false);

        return root;
    }
}