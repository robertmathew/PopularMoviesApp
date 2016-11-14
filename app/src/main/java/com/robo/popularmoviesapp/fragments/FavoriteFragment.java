package com.robo.popularmoviesapp.fragments;


import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robo.popularmoviesapp.Movie;
import com.robo.popularmoviesapp.R;
import com.robo.popularmoviesapp.adapters.MoviePosterAdapter;
import com.robo.popularmoviesapp.data.MovieContract;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "FavoriteFragment";
    private static final int CURSOR_LOADER_ID = 0;
    ArrayList<Movie> favoriteList = new ArrayList<>();
    Context mContext;
    String[] projections = {MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_URL};
    private MoviePosterAdapter favoriteAdapter;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // initialize loader
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.favorite_recycler_view);
        RecyclerView.LayoutManager favLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(favLayoutManager);
        favoriteAdapter = new MoviePosterAdapter(mContext, favoriteList);
        recyclerView.setAdapter(favoriteAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                projections,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            Log.d(TAG, "No data in database");
        } else {
            //Log.v(TAG, DatabaseUtils.dumpCursorToString(data));
            while (data.moveToNext()) {
                String id = String.valueOf(data.getInt(data.getColumnIndex(MovieContract.MovieEntry._ID)));
                String title = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                String poster = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL));
                //Log.d(TAG, "ID: " + id);
                //Log.d(TAG, "Title: " + title);
                //Log.d(TAG, "Poster URL: " + poster);
                Movie m = new Movie(id, poster, title);
                favoriteList.add(m);
            }
            data.close();
        }
        favoriteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
