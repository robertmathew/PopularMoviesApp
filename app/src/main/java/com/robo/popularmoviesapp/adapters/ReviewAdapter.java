package com.robo.popularmoviesapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.robo.popularmoviesapp.R;
import com.robo.popularmoviesapp.Review;

import java.util.ArrayList;

/**
 * Created by robo on 7/12/15.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private ArrayList<Review> mDataset = new ArrayList<>();

    public ReviewAdapter(ArrayList<Review> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review r = mDataset.get(position);
        holder.tvAuthor.setText(r.getAuthor());
        holder.tvContent.setMaxLines(Integer.MAX_VALUE);
        holder.tvContent.setText(r.getContent());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor;
        TextView tvContent;

        ViewHolder(View v) {
            super(v);
            tvAuthor = v.findViewById(R.id.review_author_name_view);
            tvContent = v.findViewById(R.id.review_content_view);
        }
    }
}
