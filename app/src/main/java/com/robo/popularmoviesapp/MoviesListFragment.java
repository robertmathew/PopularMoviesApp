package com.robo.popularmoviesapp;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MoviesListFragment extends Fragment {

    public static final String PREFS_NAME = "SortPrefsFile";
    RecyclerView.Adapter mAdapter;
    ArrayList<Movie> resultStrs = new ArrayList<>();
    ArrayList<Movie> parcelList;
    String sortMovies;
    SharedPreferences settings;
    String sort;

    public MoviesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        sort = settings.getString("sort", "popular");
        if (sort.equals("popular")) {
            sortMovies = "popularity.desc";
        } else {
            sortMovies = "vote_average.desc";
        }

        if (savedInstanceState == null || !savedInstanceState.containsKey("key")) {
            if (isNetworkAvailable()) {
                new MoviesListTask().execute(sortMovies);
            } else {
                Toast.makeText(getActivity(),"Check network connectivity", Toast.LENGTH_LONG).show();
            }
            /*parcelList = new ArrayList<>();

            for(int i = 0; i < resultStrs.size(); i++) {
                m = resultStrs.get(i);
                parcelList.add(new Movie(m.getId(), m.getImg(), m.getTitle()));
            }*/
        } else {
            resultStrs = savedInstanceState.getParcelableArrayList("key");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies_list, container, false);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MoviePosterAdapter(getActivity(), resultStrs);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        SharedPreferences.Editor editor = settings.edit();
        if (id == R.id.popular) {
            sort = "popular";
            sortMovies = "popularity.desc";
            if (isNetworkAvailable()) {
                new MoviesListTask().execute(sortMovies);
            }

            editor.putString("sort", sort);
            // Commit the edits!
            editor.commit();
            return true;
        }
        if (id == R.id.rating) {
            sort = "rating";
            sortMovies = "vote_average.desc";
            if (isNetworkAvailable()) {
                new MoviesListTask().execute(sortMovies);
            }

            editor.putString("sort", sort);
            // Commit the edits!
            editor.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", resultStrs);
        super.onSaveInstanceState(outState);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class MoviesListTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = MoviesListTask.class.getSimpleName();

        private ArrayList<Movie> getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
            List<String> idList = new ArrayList<>();
            List<String> imgList = new ArrayList<>();
            List<String> titleList = new ArrayList<>();
            idList.clear();
            imgList.clear();
            titleList.clear();
            resultStrs.clear();

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_LIST = "results";
            final String TMDB_ID = "id";
            final String TMDB_IMG = "poster_path";
            final String TMDB_TITLE = "title";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray movieList = moviesJson.getJSONArray(TMDB_LIST);
            for (int i = 0; i < movieList.length(); i++) {
                JSONObject singleMovie = movieList.getJSONObject(i);
                String id = singleMovie.getString(TMDB_ID);
                String img = singleMovie.getString(TMDB_IMG);
                String title = singleMovie.getString(TMDB_TITLE);
                idList.add(id);
                imgList.add(img);
                titleList.add(title);
                Movie movieInfo = new Movie(idList.get(i), imgList.get(i), titleList.get(i));
                resultStrs.add(movieInfo);
            }
            return resultStrs;
        }

        protected ArrayList<Movie> doInBackground(String... params) {

            String api = BuildConfig.TMDB_API_KEY;

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            sort = settings.getString("sort", "popular");
            if (sort.equals("popular")) {
                sortMovies = "popularity.desc";
            } else {
                sortMovies = "vote_average.desc";
            }

            try {
                // Construct the URL for the TMDB query
                // Possible parameters are avaiable at TMDB's movie API page
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortMovies)
                        .appendQueryParameter(API_PARAM, api)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            if (result != null) {
                resultStrs = result;
                mAdapter.notifyDataSetChanged();
            }
        }
    }

}
