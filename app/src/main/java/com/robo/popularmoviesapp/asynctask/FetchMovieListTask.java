package com.robo.popularmoviesapp.asynctask;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.robo.popularmoviesapp.BuildConfig;
import com.robo.popularmoviesapp.Movie;

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

/**
 * Created by robo on 20/11/15.
 */
public class FetchMovieListTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private static final String SORT_POPULAR = "popularity.desc";
    private static final String SORT_RATING = "vote_average.desc";
    private final String LOG_TAG = FetchMovieListTask.class.getSimpleName();
    ArrayList<Movie> resultStrs = new ArrayList<>();

    private ArrayList<Movie> getMoviesDataFromJson(String moviesJsonStr) throws JSONException {

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

            Movie movieInfo = new Movie(id, img, title);
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


        try {
            // Construct the URL for the TMDB query
            // Possible parameters are avaiable at TMDB's movie API page
            final String MOVIES_BASE_URL =
                    "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_PARAM = "api_key";
            final String VOTE_MIN = "vote_count.gte";
            final String VOTE_MIN_VALUE = "100";
            String sort = params[0];

            Uri builtUri = null;
            switch (sort) {
                case SORT_POPULAR:
                    builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                            .appendQueryParameter(SORT_PARAM, params[0])
                            .appendQueryParameter(API_PARAM, api)
                            .build();
                    break;
                case SORT_RATING:
                    builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                            .appendQueryParameter(SORT_PARAM, params[0])
                            .appendQueryParameter(API_PARAM, api)
                            .appendQueryParameter(VOTE_MIN, VOTE_MIN_VALUE)
                            .build();
                    break;
            }
            //Log.d(LOG_TAG, "doInBackground: " + builtUri.toString());

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
}
