/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.android.com.filmesfamosos.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import br.android.com.filmesfamosos.BuildConfig;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String STATIC_MOVIE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String MOVIE_BASE_URL = STATIC_MOVIE_URL;

    private static final String format = "json";
    final static String API_KEY = "api_key";
    final static String LANGUAGE = "language";
    final static String PAGE = "page";

    public static URL buildUrl(String idOrOrdem) {
        Uri builtUri = Uri.parse(STATIC_MOVIE_URL+idOrOrdem).buildUpon()
                .appendQueryParameter(API_KEY, BuildConfig.MY_MOVIE_DB_API_KEY)
                .appendQueryParameter(LANGUAGE, "pt-BR")
                .appendQueryParameter(PAGE, "1")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildUrlTrailer(String idOrOrdem) {
        Uri builtUri = Uri.parse(STATIC_MOVIE_URL+idOrOrdem+"/videos").buildUpon()
                .appendQueryParameter(API_KEY, BuildConfig.MY_MOVIE_DB_API_KEY)
                .appendQueryParameter(LANGUAGE, "pt-BR")
                .appendQueryParameter(PAGE, "1")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildUrlReview(String idOrOrdem) {
        Uri builtUri = Uri.parse(STATIC_MOVIE_URL+idOrOrdem+"/reviews").buildUpon()
                .appendQueryParameter(API_KEY, BuildConfig.MY_MOVIE_DB_API_KEY)
                .appendQueryParameter(LANGUAGE, "pt-BR")
                .appendQueryParameter(PAGE, "1")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * Builds the URL used to talk to the weather server using latitude and longitude of a
     * location.
     *
     * @param lat The latitude of the location
     * @param lon The longitude of the location
     * @return The Url to use to query the weather server.
     */
    public static URL buildUrl(Double lat, Double lon) {
        return null;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}