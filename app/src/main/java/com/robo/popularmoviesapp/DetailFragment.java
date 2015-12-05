package com.robo.popularmoviesapp;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class DetailFragment extends Fragment {

    String id, title;
    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    ImageView imgPoster;
    ImageView imgBackdrop;
    TextView tvRating, tvRelease, tvPlot;
    private ArrayList<Movie> trailerList = new ArrayList<>();
    MovieTrailerAdapter movieTrailerAdapter;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getActivity().getIntent().getStringExtra("id");
        title = getActivity().getIntent().getStringExtra("title");

        //AsyncTask to load the backdrop
        MovieImgTask btask = new MovieImgTask();
        btask.execute(id);

        //AsyncTask to load the movie info
        MovieInfoTask task = new MovieInfoTask();
        task.execute(id);

        //AsyncTask to load trailer
        MovieTrailerTask tTask = new MovieTrailerTask();
        tTask.execute(id);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(title);

        imgBackdrop = (ImageView) view.findViewById(R.id.backdrop);

        imgPoster = (ImageView) view.findViewById(R.id.posterImage);
        tvRating = (TextView) view.findViewById(R.id.tvRating);
        tvRelease = (TextView) view.findViewById(R.id.tvRelease);
        tvPlot = (TextView) view.findViewById(R.id.tvPlot);

        //Trailer
        RecyclerView trailerRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_trailer);
        trailerRecyclerView.setHasFixedSize(true);
        movieTrailerAdapter = new MovieTrailerAdapter(getActivity(), trailerList);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        trailerRecyclerView.setLayoutManager(linearLayoutManager);
        trailerRecyclerView.setAdapter(movieTrailerAdapter);

        return view;
    }

    private class MovieInfoTask extends FetchMovieInfoTask {

        @Override
        protected void onPostExecute(Movie movieInfo) {
            if (movieInfo != null) {
                //Loading poster
                final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
                final String SIZE_PATH = "w342";
                String IMG_PATH = movieInfo.getImg();
                Context context = imgPoster.getContext();
                Picasso.with(context).load(POSTER_BASE_URL + SIZE_PATH + IMG_PATH).into(imgPoster);

                String ratingValue = getResources()
                        .getString(R.string.rating_value, (movieInfo.getRating()));
                tvRating.setText(ratingValue);
                tvRelease.setText(Utility.loadDate(movieInfo.getReleaseDate()));
                tvPlot.setText(movieInfo.getPlot());
            }
        }
    }

    private class MovieImgTask extends FetchMovieImgTask {

        @Override
        protected void onPostExecute(String img) {
            if (img != null) {
                //Loading poster
                final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
                final String SIZE_PATH = "original";

                Context context = imgBackdrop.getContext();
                Picasso.with(context).load(POSTER_BASE_URL + SIZE_PATH + img).into(imgBackdrop);
            }
        }
    }

    private class MovieTrailerTask extends FetchMovieVideoTask {

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null) {
                trailerList.clear();
                for (Movie m : movies) {
                    trailerList.add(m);
                }
                movieTrailerAdapter.notifyDataSetChanged();
            }
        }
    }
}
