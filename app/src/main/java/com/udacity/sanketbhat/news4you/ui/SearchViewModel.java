package com.udacity.sanketbhat.news4you.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.udacity.sanketbhat.news4you.Dependency;
import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.api.NewsAPIService;
import com.udacity.sanketbhat.news4you.model.Article;
import com.udacity.sanketbhat.news4you.model.NewsResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends AndroidViewModel {
    private NewsAPIService apiService;
    private MutableLiveData<List<Article>> searchResults;
    private String query;
    private boolean loading;
    private int totalPages, currentPage;

    SearchViewModel(Application application) {
        super(application);
        apiService = Dependency.getAPIService();
        searchResults = new MutableLiveData<>();
        searchResults.setValue(new ArrayList<>());
    }

    MutableLiveData<List<Article>> getSearchResults() {
        return searchResults;
    }

    void loadResult(String queryString) {
        if (!loading) {
            query = queryString;
            loading = true;
            apiService.getEverything(queryString, 1, getApplication().getApplicationContext().getString(R.string.NEWS_API_KEY))
                    .enqueue(new Callback<NewsResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                            NewsResponse newsResponse = response.body();
                            if (newsResponse != null && response.isSuccessful() && newsResponse.getArticles() != null) {
                                int responseCount = newsResponse.getTotalResults();
                                storeTotalResults(responseCount);
                                storeCurrentPage(call.request().url().toString());

                                List<Article> articles = Arrays.asList(newsResponse.getArticles());
                                searchResults.setValue(articles);
                            }
                            loading = false;
                        }

                        @Override
                        public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                            //TODO: Handle this case
                            loading = false;
                        }
                    });
        }
    }

    private void storeCurrentPage(String s) {
        String[] queries = s.substring(s.lastIndexOf("/")).split("&");
        for (String query : queries) {
            if (query.contains("page")) {
                currentPage = Integer.valueOf(query.split("=")[1].trim());
                break;
            }
        }
    }

    void loadNextPage() {
        if (!loading && currentPage < totalPages) {
            loading = true;
            apiService.getEverything(query, currentPage + 1, getApplication().getApplicationContext().getString(R.string.NEWS_API_KEY))
                    .enqueue(new Callback<NewsResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                            NewsResponse newsResponse = response.body();
                            if (newsResponse != null && response.isSuccessful() && newsResponse.getArticles() != null) {
                                List<Article> articles = Arrays.asList(newsResponse.getArticles());
                                ArrayList<Article> existingArticles = null;
                                storeCurrentPage(call.request().url().toString());
                                if (searchResults.getValue() != null) {
                                    existingArticles = new ArrayList<>(searchResults.getValue());
                                    existingArticles.addAll(articles);
                                }
                                searchResults.setValue(existingArticles);
                            }
                            loading = false;
                        }

                        @Override
                        public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                            //TODO: Handle this case
                            loading = false;
                        }
                    });
        }

    }

    private void storeTotalResults(int responseCount) {
        if (responseCount < 20)
            totalPages = 1;
        else
            totalPages = responseCount / 20;
    }

}
