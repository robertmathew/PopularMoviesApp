package com.robo.popularmoviesapp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PopularMoviesFragment extends Fragment {

    private final String TAG = PopularMoviesFragment.class.getSimpleName();

    private static final String POPULAR_LIST = "popularList";

    private Context mContext;
    private String sortOrder;


    MoviePosterAdapter popularAdapter;
    ArrayList<Movie> mPopularList = new ArrayList<>();

    public static PopularMoviesFragment newInstance(String sort) {
        PopularMoviesFragment fragment = new PopularMoviesFragment();
        Bundle args = new Bundle();
        args.putString("sort", sort);
        fragment.setArguments(args);
        return fragment;
    }

    public PopularMoviesFragment(){
        // Required empty public constructor
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
