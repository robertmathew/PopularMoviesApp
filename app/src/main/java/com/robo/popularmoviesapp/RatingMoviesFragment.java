package com.robo.popularmoviesapp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private Context mContext;

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

    public RatingMoviesFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sortOrder = getArguments().getString("sort");
        mContext = getActivity();

        if (savedInstanceState != null) {
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

        ratingAdapter = new MoviePosterAdapter(mContext, mRatingList);

        mProgressBar = (ProgressBar) view.findViewById(R.id.movies_progress_bar);
        RecyclerView ratRecyclerView = (RecyclerView) view.findViewById(R.id.rat_recycler_view);
        RecyclerView.LayoutManager ratLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        ratRecyclerView.setLayoutManager(ratLayoutManager);
        ratRecyclerView.setAdapter(ratingAdapter);

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

            if (movies != null) {
                mRatingList.clear();
                for (Movie m : movies) {
                    mRatingList.add(m);
                    //Log.d(LOG_TAG, "Rating onPostExecute: " + m.getId() + m.getImg() + m.getTitle());
                }
                ratingAdapter.setMoviesData(mRatingList);
                ratingAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

}
