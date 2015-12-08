package com.robo.popularmoviesapp.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robo.popularmoviesapp.Movie;
import com.robo.popularmoviesapp.R;
import com.robo.popularmoviesapp.Review;
import com.robo.popularmoviesapp.Utility;
import com.robo.popularmoviesapp.activities.ReviewActivity;
import com.robo.popularmoviesapp.adapters.MovieTrailerAdapter;
import com.robo.popularmoviesapp.asynctask.FetchMovieInfoTask;
import com.robo.popularmoviesapp.asynctask.FetchMovieReviewTask;
import com.robo.popularmoviesapp.asynctask.FetchMovieVideoTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class DetailFragment extends Fragment {

    //Poster and backdrop URL
    private final static String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String MOVIE_SHARE_HASHTAG = " #PopularMoviesApp";
    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private final String POSTER_SIZE_PATH = "w342";
    private final String BACKDROP_SIZE_PATH = "w780";
    ImageView imgPoster, imgBackdrop;
    TextView tvRating, tvRelease, tvPlot;
    TextView tvUsername, tvContent;
    Button btnMoreReview;
    LinearLayout reviewLayout;
    MovieTrailerAdapter movieTrailerAdapter;
    LinearLayout trailerLayout;
    private ShareActionProvider mShareActionProvider;
    private String id, title;
    private String mBackdrop, mPoster, mRating, mReleaseDate, mPlot;
    private ArrayList<Movie> trailerList = new ArrayList<>();
    private ArrayList<Review> reviewList = new ArrayList<>();

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getActivity().getIntent().getStringExtra("id");
        title = getActivity().getIntent().getStringExtra("title");

        if (savedInstanceState != null) {
            id = savedInstanceState.getString(getString(R.string.key_id));
            title = savedInstanceState.getString(getString(R.string.key_title));
            mBackdrop = savedInstanceState.getString(getString(R.string.key_backdrop));
            mPoster = savedInstanceState.getString(getString(R.string.key_poster));
            mRating = savedInstanceState.getString(getString(R.string.key_rating));
            mReleaseDate = savedInstanceState.getString(getString(R.string.key_release_date));
            mPlot = savedInstanceState.getString(getString(R.string.key_plot));
            trailerList = savedInstanceState.getParcelableArrayList(getString(R.string.key_trailer));
            reviewList = savedInstanceState.getParcelableArrayList(getString(R.string.key_review));
        } else {
            //AsyncTask to load the movie info
            MovieInfoTask task = new MovieInfoTask();
            task.execute(id);

            //AsyncTask to load trailer
            MovieTrailerTask tTask = new MovieTrailerTask();
            tTask.execute(id);

            //Asynctask to load review
            MovieReviewTask reviewTask = new MovieReviewTask();
            reviewTask.execute(id);
        }
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

        reviewLayout = (LinearLayout) view.findViewById(R.id.linearUserReview);
        tvUsername = (TextView) view.findViewById(R.id.review_author_name_view);
        tvContent = (TextView) view.findViewById(R.id.review_content_view);
        btnMoreReview = (Button) view.findViewById(R.id.detail_reviews_show_more_button);

        btnMoreReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReviewActivity.class);
                intent.putParcelableArrayListExtra("reviews", reviewList);
                startActivity(intent);
            }
        });

        //Trailer
        trailerLayout = (LinearLayout) view.findViewById(R.id.linearTrailer);
        RecyclerView trailerRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_trailer);
        trailerRecyclerView.setHasFixedSize(true);
        movieTrailerAdapter = new MovieTrailerAdapter(getActivity(), trailerList);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        trailerRecyclerView.setLayoutManager(linearLayoutManager);
        trailerRecyclerView.setAdapter(movieTrailerAdapter);

        if (savedInstanceState != null) {
            setMovieInfo(mBackdrop, mPoster, mRating, mReleaseDate, mPlot);
            setMovieReview(reviewList);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(getString(R.string.key_id), id);
        outState.putString(getString(R.string.key_title), title);
        outState.putString(getString(R.string.key_backdrop), mBackdrop);
        outState.putString(getString(R.string.key_poster), mPoster);
        outState.putString(getString(R.string.key_rating), mRating);
        outState.putString(getString(R.string.key_release_date), mReleaseDate);
        outState.putString(getString(R.string.key_plot), mPlot);
        outState.putParcelableArrayList(getString(R.string.key_trailer), trailerList);
        outState.putParcelableArrayList(getString(R.string.key_review), reviewList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mShareActionProvider.setShareIntent(createShareForecastIntent());
    }

    private Intent createShareForecastIntent() {
        String link = null;
        if (trailerList.size() != 0) {
            Movie m = trailerList.get(0);
            link = "Trailer: http://www.youtube.com/watch?v=" + m.getKey();
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, title + link + MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }

    public void setMovieInfo(String backdrop, String posterPath, String rating,
                             String releaseDate, String plot) {
        //Backdrop
        Picasso.with(imgBackdrop.getContext())
                .load(POSTER_BASE_URL + BACKDROP_SIZE_PATH + backdrop)
                .into(imgBackdrop);

        //Poster
        Picasso.with(imgPoster.getContext())
                .load(POSTER_BASE_URL + POSTER_SIZE_PATH + posterPath)
                .priority(Picasso.Priority.HIGH).into(imgPoster);

        //Rating
        String ratingValue = getResources()
                .getString(R.string.rating_value, rating);
        tvRating.setText(ratingValue);

        //Release date
        tvRelease.setText(Utility.loadDate(releaseDate));

        //Plot
        tvPlot.setText(plot);
    }

    public void setMovieReview(ArrayList<Review> reviews) {
        if (reviews.size() != 0) {
            Review r = reviews.get(0);
            tvUsername.setText(r.getAuthor());
            tvContent.setText(r.getContent());
        } else {
            reviewLayout.setVisibility(View.GONE);
        }
    }

    private class MovieInfoTask extends FetchMovieInfoTask {

        @Override
        protected void onPostExecute(Movie movieInfo) {
            if (movieInfo != null) {
                mBackdrop = movieInfo.getBackdrop();
                mPoster = movieInfo.getImg();
                mRating = movieInfo.getRating();
                mReleaseDate = movieInfo.getReleaseDate();
                mPlot = movieInfo.getPlot();

                setMovieInfo(mBackdrop, mPoster, mRating, mReleaseDate, mPlot);
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
                if (trailerList.size() != 0) {
                    movieTrailerAdapter.notifyDataSetChanged();
                } else {
                    trailerLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    private class MovieReviewTask extends FetchMovieReviewTask {

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {
            if (reviews != null) {
                reviewList.clear();
                for (Review review : reviews) {
                    reviewList.add(review);
                }
                setMovieReview(reviewList);
            }
        }
    }
}
