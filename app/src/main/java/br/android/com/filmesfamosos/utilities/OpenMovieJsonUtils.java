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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import br.android.com.filmesfamosos.Filme;

public final class OpenMovieJsonUtils {

    private static final String OWM_RESULT = "results";
    private static final String OWM_MESSAGE_CODE = "status_code";
    private static final String OWM_ID = "id";
    private static final String OWM_TITLE = "title";
    private static final String OWM_POSTER_PATH = "poster_path";
    private static final String OWM_POSTER_PATH_BACKDROP = "backdrop_path";
    private static final String OWM_TRAILER = "key";
    private static final String OWM_CONTENT = "content";
    private static final String OWM_URL_SINOPSE = "overview";
    private static final String OWM_URL_DATA_LANCAMENTO = "release_date";
    private static final String OWM_URL_MEDIA_VOTO = "vote_average";
    private static final String OWM_URL_IMAGE = "http://image.tmdb.org/t/p/w185";
    private static final String OWM_URL_TRAILER = "https://youtu.be/";

    public static List<Filme> getSimpleMovieStringsFromJson(Context context, String movieJsonStr,String trailerJsonStr, String reviewJsonStr, boolean isDetalhe) throws JSONException {

        JSONObject movieJson = new JSONObject(movieJsonStr);

        if (movieJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        List<Filme> filmes = new ArrayList<>();

        if(!isDetalhe){
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULT);
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieJsonObject = movieArray.getJSONObject(i);
                Filme filme = newFilme(movieJsonObject);
                filmes.add(filme);
            }
        }else{
            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray(OWM_RESULT);
            JSONArray reviewArray = reviewJson.getJSONArray(OWM_RESULT);

            Filme filme = newFilme(movieJson);
            filme.setUrlTrailer(getUrlTrailer(filme.getId(), trailerArray));
            filme.setReview(getUrlReview(filme.getId(), reviewArray));
            filmes.add(filme);
        }
        return filmes;
    }

    private static List<String> getUrlTrailer(int id, JSONArray trailerArray) throws JSONException {
        List<String> urlList = new ArrayList<String>();
        String keyVideo = "";
        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject movieJsonObject = trailerArray.getJSONObject(i);
            keyVideo = movieJsonObject.getString(OWM_TRAILER);
            urlList.add(OWM_URL_TRAILER+keyVideo);
        }
        return urlList;
    }

    private static List<String> getUrlReview(int id, JSONArray reviewrArray) throws JSONException {
        List<String> reviewList = new ArrayList<String>();
        String content = "";
        for (int i = 0; i < reviewrArray.length(); i++) {
            JSONObject movieJsonObject = reviewrArray.getJSONObject(i);
            content = movieJsonObject.getString(OWM_CONTENT);
            reviewList.add(content);
        }
        return reviewList;
    }

    private static Filme newFilme(JSONObject movieJsonObject) throws JSONException {
        Filme filme = new Filme();
        filme.setId(movieJsonObject.getInt(OWM_ID));
        filme.setTitulo(movieJsonObject.getString(OWM_TITLE));
        filme.setCaminhoImgPoster(OWM_URL_IMAGE+movieJsonObject.getString(OWM_POSTER_PATH));
        filme.setCaminhoImgBackDrop(OWM_URL_IMAGE+movieJsonObject.getString(OWM_POSTER_PATH_BACKDROP));

        try {
            filme.setDataLancamento( DateUtils.parse( movieJsonObject.getString(OWM_URL_DATA_LANCAMENTO), "yyyy-MM-dd" ) );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        filme.setMediaVotos(movieJsonObject.getDouble(OWM_URL_MEDIA_VOTO));
        filme.setSinopse(movieJsonObject.getString(OWM_URL_SINOPSE));

        return filme;
    }
}