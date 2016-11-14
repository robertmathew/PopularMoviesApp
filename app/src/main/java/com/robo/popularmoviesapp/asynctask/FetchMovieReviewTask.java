package com.robo.popularmoviesapp.asynctask;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.robo.popularmoviesapp.BuildConfig;
import com.robo.popularmoviesapp.Review;

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
 * Created by robo on 7/12/15.
 */
public class FetchMovieReviewTask extends AsyncTask<String, Void, ArrayList<Review>> {

    private static final String TAG = "FetchMovieReviewTask";

    ArrayList<Review> reviewList = new ArrayList<>();

    private ArrayList<Review> getMoviesVideoFromJson(String moviesJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String RESULT = "results";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";


        JSONObject reviewListJson = new JSONObject(moviesJsonStr);
        JSONArray singleReview = reviewListJson.getJSONArray(RESULT);
        for (int i = 0; i < singleReview.length(); i++) {
            JSONObject jVideo = singleReview.getJSONObject(i);
            String author = jVideo.getString(TMDB_AUTHOR);
            String content = jVideo.getString(TMDB_CONTENT);
            Review review = new Review(author, content);
            reviewList.add(review);
        }

        return reviewList;
    }

    @Override
    protected ArrayList<Review> doInBackground(String... params) {
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
                    "http://api.themoviedb.org/3/movie";
            final String REVIEW_PATH = "reviews";
            final String API_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendPath(REVIEW_PATH)
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
            return getMoviesVideoFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}
