package com.robo.popularmoviesapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RatingMoviesFragment extends Fragment {

    private final String LOG_TAG = RatingMoviesFragment.class.getSimpleName();

    private static final String SORT_RATING = "vote_average.desc";

    private String sortOrder;
    private static final String RATING_LIST = "ratingList";

    ProgressBar mProgressBar;
    MoviePosterAdapter ratingAdapter;
    ArrayList<Movie> mRatingList = new ArrayList<>();

    public static RatingMoviesFragment newInstance(String sort) {
        RatingMoviesFragment fragment = new RatingMoviesFragment();
        Bundle args = new Bundle();
        args.putString("sort", sort);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sortOrder = getArguments().getString("sort");

        if (savedInstanceState != null) {
            Log.d(LOG_TAG, "Has saved instance");
            mRatingList = savedInstanceState.getParcelableArrayList(RATING_LIST);
        } else {
            FetchRatingMovies rm = new FetchRatingMovies();
            rm.execute(SORT_RATING);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rating_movies, container, false);

        ratingAdapter = new MoviePosterAdapter(getActivity(), mRatingList);

        mProgressBar = (ProgressBar) view.findViewById(R.id.movies_progress_bar);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(ratingAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(RATING_LIST, mRatingList);
        super.onSaveInstanceState(outState);
    }

    public class FetchRatingMovies extends FetchMovieListTask {

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            //super.onPostExecute(movies);

            mRatingList = movies;
            /*for(Movie m : movies){
                Log.d(LOG_TAG, "onPostExecute: " + m.getTitle());
            }*/

            ratingAdapter.setMoviesData(mRatingList);
            ratingAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        }
    }

}
