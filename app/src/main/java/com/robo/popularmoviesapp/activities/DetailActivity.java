package com.robo.popularmoviesapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.robo.popularmoviesapp.R;
import com.robo.popularmoviesapp.fragments.DetailFragment;

public class DetailActivity extends AppCompatActivity {

    private String id, title;
    private Boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Getting id and title
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        twoPane = false;

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, DetailFragment.newInstance(id, title, twoPane))
                .commit();
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
