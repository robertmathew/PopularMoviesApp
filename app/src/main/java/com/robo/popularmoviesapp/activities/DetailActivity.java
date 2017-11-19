package com.robo.popularmoviesapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.robo.popularmoviesapp.R;
import com.robo.popularmoviesapp.fragments.DetailFragment;
import com.robo.popularmoviesapp.fragments.FavoriteDetailFragment;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Getting id and title
        String classname = getIntent().getStringExtra("class");
        String id = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");

        if (classname.equals("MainActivity")) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, DetailFragment.newInstance(id, title, false))
                    .commit();
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, FavoriteDetailFragment.newInstance(id, title, false))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }


}
