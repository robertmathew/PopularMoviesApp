package com.robo.popularmoviesapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robo.popularmoviesapp.Movie;
import com.robo.popularmoviesapp.R;
import com.robo.popularmoviesapp.adapters.MoviePosterAdapter;
import com.robo.popularmoviesapp.asynctask.FetchMovieListTask;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PopularMoviesFragment extends Fragment {

    private static final String POPULAR_LIST = "popularList";
    private final String TAG = PopularMoviesFragment.class.getSimpleName();
    MoviePosterAdapter popularAdapter;
    ArrayList<Movie> mPopularList = new ArrayList<>();
    private Context mContext;
    private String sortOrder;

    public PopularMoviesFragment() {
        // Required empty public constructor
    }

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
        mContext = getActivity();

        if (savedInstanceState != null) {
            mPopularList = savedInstanceState.getParcelableArrayList(POPULAR_LIST);
        } else {
            FetchPopularMovies pm = new FetchPopularMovies();
            pm.execute(sortOrder);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(POPULAR_LIST, mPopularList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View pView = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        popularAdapter = new MoviePosterAdapter(mContext, mPopularList);

        RecyclerView popRecyclerView = (RecyclerView) pView.findViewById(R.id.pop_recycler_view);
        RecyclerView.LayoutManager popLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        popRecyclerView.setLayoutManager(popLayoutManager);

        popRecyclerView.setAdapter(popularAdapter);

        return pView;
    }

    public class FetchPopularMovies extends FetchMovieListTask {

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {

            if (movies != null) {
                mPopularList.clear();
                for (Movie m : movies) {
                    mPopularList.add(m);
                }
                popularAdapter.setMoviesData(mPopularList);
                popularAdapter.notifyDataSetChanged();
            }
        }
    }

}
