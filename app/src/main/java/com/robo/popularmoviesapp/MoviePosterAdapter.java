package com.robo.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.ViewHolder> {

    private final String TAG = MoviePosterAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<Movie> mDataset;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public ImageView mImageView;
        public TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            this.mView = view;
            this.mImageView = (ImageView) view.findViewById(R.id.imageView);
            this.mTextView = (TextView) view.findViewById(R.id.title);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MoviePosterAdapter(Context context, ArrayList<Movie> myDataset) {
        this.mContext = context;
        this.mDataset = myDataset;
    }

    public void setMoviesData(ArrayList<Movie> moviesData){
        mDataset = moviesData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MoviePosterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_poster, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Movie mMovie = mDataset.get(position);
        //Log.d(LOG_TAG, "onBindViewHolder: " + mMovie.getTitle());

        //Creating URL for image
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE_PATH = "w185";
        String IMG_PATH = mMovie.getImg();

        //Loading image using Picasso
        Picasso.with(mContext)
                .load(POSTER_BASE_URL + SIZE_PATH + IMG_PATH)
                .placeholder( R.drawable.progress_animation )
                .into(holder.mImageView);

        //Title
        holder.mTextView.setText(mMovie.getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
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
