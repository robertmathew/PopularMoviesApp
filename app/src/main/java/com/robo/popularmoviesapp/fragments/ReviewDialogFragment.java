package com.robo.popularmoviesapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robo.popularmoviesapp.R;
import com.robo.popularmoviesapp.Review;
import com.robo.popularmoviesapp.adapters.ReviewAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewDialogFragment extends Fragment {

    ArrayList<Review> reviewArrayList = new ArrayList<>();

    public ReviewDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reviewArrayList = getArguments().getParcelableArrayList("review");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.review_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        ReviewAdapter reviewAdapter = new ReviewAdapter(reviewArrayList);
        mRecyclerView.setAdapter(reviewAdapter);
        return view;
    }

}
