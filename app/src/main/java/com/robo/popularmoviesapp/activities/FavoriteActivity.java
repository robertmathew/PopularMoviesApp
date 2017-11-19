package com.robo.popularmoviesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.robo.popularmoviesapp.R;
import com.robo.popularmoviesapp.fragments.FavoriteDetailFragment;
import com.robo.popularmoviesapp.fragments.FavoriteFragment;

public class FavoriteActivity extends AppCompatActivity {

    private Boolean mTwoPane;
    private static final String TAG = "FavoriteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        mTwoPane = findViewById(R.id.movie_detail_container) != null;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new FavoriteFragment()).commit();

    }

    public void switchToDetail(String id, String title) {
        if (mTwoPane) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, FavoriteDetailFragment.newInstance(id, title, mTwoPane))
                    .commit();
        } else {
            Intent i = new Intent(FavoriteActivity.this, DetailActivity.class);
            i.putExtra("class", "FavoriteActivity");
            i.putExtra("id", id);
            i.putExtra("title", title);
            startActivity(i);
            Log.d(TAG, "switchToDetail: switching");
        }
    }
}
