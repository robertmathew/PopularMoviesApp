package com.robo.popularmoviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.robo.popularmoviesapp.Movie;
import com.robo.popularmoviesapp.R;
import com.robo.popularmoviesapp.activities.FavoriteActivity;
import com.robo.popularmoviesapp.activities.MainActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;


public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.ViewHolder> {

    private final String TAG = MoviePosterAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<Movie> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MoviePosterAdapter(Context context, ArrayList<Movie> myDataset) {
        this.mContext = context;
        this.mDataset = myDataset;
    }

    public void setMoviesData(ArrayList<Movie> moviesData) {
        mDataset = moviesData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MoviePosterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_poster, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Movie mMovie = mDataset.get(position);
        //Log.d(LOG_TAG, "onBindViewHolder: " + mMovie.getTitle());

        //Creating URL for image
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE_PATH = "w342";
        String IMG_PATH = mMovie.getImg();

        //Loading image using Picasso
        Picasso.with(mContext)
                .load(POSTER_BASE_URL + SIZE_PATH + IMG_PATH)
                .error(R.drawable.no_poster)
                .into(holder.mImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        //Title
        holder.mTextView.setText(mMovie.getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof MainActivity) {
                    MainActivity main = (MainActivity) mContext;
                    main.switchToDetail(mMovie.getId(), mMovie.getTitle());
                }
                if (mContext instanceof FavoriteActivity) {
                    FavoriteActivity favorite = (FavoriteActivity) mContext;
                    favorite.switchToDetail(mMovie.getId(), mMovie.getTitle());
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView mImageView;
        TextView mTextView;
        MaterialProgressBar mProgressBar;

        ViewHolder(View view) {
            super(view);
            this.mView = view;
            this.mImageView = view.findViewById(R.id.imageView);
            this.mTextView = view.findViewById(R.id.title);
            this.mProgressBar = view.findViewById(R.id.materialProgress);
        }
    }
}
