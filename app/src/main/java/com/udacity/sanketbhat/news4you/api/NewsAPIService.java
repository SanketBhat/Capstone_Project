package com.udacity.sanketbhat.news4you.api;

import com.udacity.sanketbhat.news4you.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsAPIService {
    @GET("top-headlines")
    Call<NewsResponse> getTopHeadlines(@Query("country") String countryCode, @Query("page") int page, @Query("apiKey") String apiKey);

    @GET("top-headlines")
    Call<NewsResponse> getArticlesByCategory(@Query("country") String countryCode, @Query("page") int page, @Query("apiKey") String apiKey, @Query("category") String category);

    @GET("everything")
    Call<NewsResponse> getEverything(@Query("q") String query, @Query("apiKey") String apiKey);
}
