package com.robo.popularmoviesapp;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DetailFragment extends Fragment {

    String id, title;
    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    ImageView imgPoster;
    TextView tvRating, tvRelease, tvPlot;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getActivity().getIntent().getStringExtra("id");
        title = getActivity().getIntent().getStringExtra("title");

        //AsyncTask to load the movie info
        MovieInfoTask task = new MovieInfoTask();
        task.execute(id);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        imgPoster = (ImageView) view.findViewById(R.id.posterImage);
        tvRating = (TextView) view.findViewById(R.id.tvRating);
        tvRelease = (TextView) view.findViewById(R.id.tvRelease);
        tvPlot = (TextView) view.findViewById(R.id.tvPlot);


        return view;
    }

    private class MovieInfoTask extends AsyncTask<String, Void, Movie> {

        private final String LOG_TAG = MovieInfoTask.class.getSimpleName();

        private Movie getMoviesDataFromJson(String moviesJsonStr) throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_POSTER = "poster_path";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_VOTE_COUNT = "vote_count";


            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            String plot = moviesJson.getString(TMDB_OVERVIEW);
            String poster = moviesJson.getString(TMDB_POSTER);
            String releaseDate = moviesJson.getString(TMDB_RELEASE_DATE);
            String rating = moviesJson.getString(TMDB_VOTE_AVERAGE);
            String voteCount = moviesJson.getString(TMDB_VOTE_COUNT);

            return new Movie(plot, poster, releaseDate, rating, voteCount);
        }

        protected Movie doInBackground(String... params) {

            String api = BuildConfig.TMDB_API_KEY;

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                // Construct the URL for the TMDB query
                // Possible parameters are avaiable at TMDB's movie API page
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";
                final String API_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendPath(params[0])
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
        protected void onPostExecute(Movie movieInfo) {
            if (movieInfo != null) {
                //Loading poster
                final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
                final String SIZE_PATH = "w185";
                String IMG_PATH = movieInfo.getImg();
                Context context = imgPoster.getContext();
                Glide.with(context).load(POSTER_BASE_URL + SIZE_PATH + IMG_PATH).into(imgPoster);

                tvRating.setText(movieInfo.getRating());
                tvRelease.setText("Release: " + movieInfo.getReleaseDate());
                tvPlot.setText(movieInfo.getPlot());
            }
        }
    }
}
