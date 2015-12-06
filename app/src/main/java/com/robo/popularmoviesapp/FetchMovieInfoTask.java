package com.robo.popularmoviesapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by robo on 30/11/15.
 */
public class FetchMovieInfoTask extends AsyncTask<String, Void, Movie> {

    private static final String TAG = "FetchMovieInfoTask";

    private Movie getMoviesDataFromJson(String moviesJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_BACKDROP = "backdrop_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_VOTE_COUNT = "vote_count";


        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        String backdrop = moviesJson.getString(TMDB_BACKDROP);
        String plot = moviesJson.getString(TMDB_OVERVIEW);
        String poster = moviesJson.getString(TMDB_POSTER);
        String releaseDate = moviesJson.getString(TMDB_RELEASE_DATE);
        String rating = moviesJson.getString(TMDB_VOTE_AVERAGE);
        String voteCount = moviesJson.getString(TMDB_VOTE_COUNT);

        return new Movie(backdrop, plot, poster, releaseDate, rating, voteCount);
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
            Log.e(TAG, "Error ", e);
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
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getMoviesDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}
