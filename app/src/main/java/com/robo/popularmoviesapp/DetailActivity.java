package com.robo.popularmoviesapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private String id, title;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Getting id and title
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");

        //Calling AsyncTask
        MovieImgTask movieImgTask = new MovieImgTask();
        movieImgTask.execute(id);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(title);


        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DetailFragment())
                .commit();

        imageView = (ImageView) findViewById(R.id.backdrop);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class MovieImgTask extends FetchMovieImgTask {

        @Override
        protected void onPostExecute(String img) {
            if (img != null) {
                //Loading poster
                final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
                final String SIZE_PATH = "original";

                Context context = imageView.getContext();
                Picasso.with(context).load(POSTER_BASE_URL + SIZE_PATH + img).into(imageView);
            }
        }
    }
}
