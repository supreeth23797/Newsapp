package com.news.newsapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://newsapi.org/v2/";
    private static ApiClient mApiClient;
    private static Retrofit mRetrofit;

    private ApiClient(){
        mRetrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static synchronized ApiClient getInstance(){
        if (mApiClient == null){
            mApiClient = new ApiClient();
        }
        return mApiClient;
    }

    public ApiInterface getApi(){
        return mRetrofit.create(ApiInterface.class);
    }
}
