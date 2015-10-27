package com.robo.popularmoviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MoviesListFragment())
                .commit();

    }

    public void switchToDetail(Intent intent){
        startActivity(intent);
    }
}
