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
public class PopularMoviesFragment extends Fragment {

    private final String LOG_TAG = PopularMoviesFragment.class.getSimpleName();

    private static final String SORT_POPULAR = "popularity.desc";

    private String sortOrder;
    private static final String POPULAR_LIST = "popularList";

    ProgressBar mProgressBar;
    MoviePosterAdapter mAdapter;
    ArrayList<Movie> mPopularList = new ArrayList<>();

    public static PopularMoviesFragment newInstance(String sort) {
        PopularMoviesFragment fragment = new PopularMoviesFragment();
        Bundle args = new Bundle();
        args.putString("sort", sort);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sortOrder = getArguments().getString("sort");
        Log.d(LOG_TAG, "onCreate: " + sortOrder);

        if (savedInstanceState != null) {
            Log.d(LOG_TAG, "Has savedInstance");
            mPopularList = savedInstanceState.getParcelableArrayList(POPULAR_LIST);
        } else {
            FetchPopularMovies pm = new FetchPopularMovies();
            pm.execute(SORT_POPULAR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        mAdapter = new MoviePosterAdapter(getActivity(), mPopularList);

        mProgressBar = (ProgressBar) view.findViewById(R.id.movies_progress_bar);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(POPULAR_LIST, mPopularList);
        super.onSaveInstanceState(outState);
    }

    public class FetchPopularMovies extends FetchMovieListTask {

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            //super.onPostExecute(movies);

            mPopularList = movies;
            mAdapter.setMoviesData(mPopularList);
            mAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        }
    }

}
