package com.robo.popularmoviesapp;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailFragment extends Fragment {

    String id, title;
    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    ImageView imgPoster;
    ImageView imgBackdrop;
    TextView tvRating, tvRelease, tvPlot;

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

        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        imgPoster = (ImageView) view.findViewById(R.id.posterImage);
        tvRating = (TextView) view.findViewById(R.id.tvRating);
        tvRelease = (TextView) view.findViewById(R.id.tvRelease);
        tvPlot = (TextView) view.findViewById(R.id.tvPlot);


        return view;
    }

    private class MovieInfoTask extends FetchMovieInfoTask {

        @Override
        protected void onPostExecute(Movie movieInfo) {
            if (movieInfo != null) {
                //Loading poster
                final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
                final String SIZE_PATH = "w185";
                String IMG_PATH = movieInfo.getImg();
                Context context = imgPoster.getContext();
                Picasso.with(context).load(POSTER_BASE_URL + SIZE_PATH + IMG_PATH).into(imgPoster);

                tvRating.setText(movieInfo.getRating());
                tvRelease.setText("Release: " + movieInfo.getReleaseDate());
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
}
