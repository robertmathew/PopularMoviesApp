package com.robo.popularmoviesapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by robo on 20/11/15.
 */
public class Utility {

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String loadDate(String date) {
        // date from API is formatted YYYY-MM-DD

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(5, 7)));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(8, 10)));

        // For this app version I want only Locale.US name of month
        return (calendar.get(Calendar.DAY_OF_MONTH)
                + " " + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)
                + " " + calendar.get(Calendar.YEAR));

    }
}
