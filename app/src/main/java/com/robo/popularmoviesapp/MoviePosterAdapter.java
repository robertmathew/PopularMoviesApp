package com.robo.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;


public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.ViewHolder> {

    private final String LOG_TAG = MoviePosterAdapter.class.getSimpleName();

    Context mContext;
    private List<Movie> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MoviePosterAdapter(Context context, List<Movie> myDataset) {
        mContext = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MoviePosterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_poster, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Movie mMovie = mDataset.get(position);

        //Creating URL for image
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE_PATH = "w185";
        String IMG_PATH = mMovie.getImg();

        //Loading image using Glide
        Glide.with(holder.mImageView.getContext())
                .load(POSTER_BASE_URL + SIZE_PATH + IMG_PATH)
                .placeholder(R.color.grid_placeholder_bg)
                .into(holder.mImageView);

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, DetailActivity.class);
                i.putExtra("id", mMovie.getId());
                i.putExtra("title", mMovie.getTitle());

                if (mContext instanceof MainActivity) {
                    MainActivity main= (MainActivity) mContext;
                    main.switchToDetail(i);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
