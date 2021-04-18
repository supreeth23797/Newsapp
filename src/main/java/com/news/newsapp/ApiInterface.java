package com.news.newsapp;

import com.news.newsapp.model.Headlines;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("top-headlines")
    Call<Headlines> getTopHeadlines(
            @Query("apiKey") String apiKey,
            @Query("country") String country,
            @Query("pageSize") int pageSize,
            @Query("page") int page
    );
}
