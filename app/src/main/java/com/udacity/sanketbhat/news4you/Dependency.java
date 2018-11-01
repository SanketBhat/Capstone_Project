package com.udacity.sanketbhat.news4you;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.udacity.sanketbhat.news4you.api.NewsAPIService;
import com.udacity.sanketbhat.news4you.database.ArticleDao;
import com.udacity.sanketbhat.news4you.database.ArticleDatabase;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Dependency {
    private static final Object LOCK = new Object();
    private static NewsAPIService apiService;
    private static ArticleDao articleDao;

    public static Repository getRepository(Context context) {
        return Repository.getInstance(context.getApplicationContext());
    }

    static NewsAPIService getAPIService() {
        if (apiService == null) {
            synchronized (LOCK) {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(30000, TimeUnit.MILLISECONDS)
                        .readTimeout(20000, TimeUnit.MILLISECONDS)
                        .retryOnConnectionFailure(true)
                        .build();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://newsapi.org/v2/")
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                apiService = retrofit.create(NewsAPIService.class);
            }
        }
        return apiService;
    }

    public static ArticleDao getArticleDao(Context context) {
        if (articleDao == null) {
            synchronized (LOCK) {
                ArticleDatabase database = Room.databaseBuilder(context.getApplicationContext(), ArticleDatabase.class, "article.db")
                        .build();
                articleDao = database.getArticleDao();
            }
        }
        return articleDao;
    }
}
