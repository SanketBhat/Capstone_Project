package com.udacity.sanketbhat.news4you;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.udacity.sanketbhat.news4you.api.NewsAPIService;
import com.udacity.sanketbhat.news4you.database.ArticleDao;
import com.udacity.sanketbhat.news4you.database.ArticleDatabase;
import com.udacity.sanketbhat.news4you.database.ArticleMaintenanceDao;
import com.udacity.sanketbhat.news4you.service.MaintenanceService;
import com.udacity.sanketbhat.news4you.service.NewsUpdateService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Dependency {
    private static final Object LOCK = new Object();
    private static NewsAPIService apiService;
    private static ArticleDatabase database;
    private static ArticleDao articleDao;
    private static boolean scheduled = false;

    public static Repository getRepository(Context context) {
        return Repository.getInstance(context.getApplicationContext());
    }

    public static NewsAPIService getAPIService() {
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
            if (database == null) database = getDatabase(context);
            articleDao = database.getArticleDao();
        }
        return articleDao;
    }

    public static ArticleMaintenanceDao getMaintenanceDao(Context context) {
        if (database == null) database = getDatabase(context);
        return database.getMaintenanceDao();
    }

    private static ArticleDatabase getDatabase(Context context) {
        if (database == null) {
            synchronized (LOCK) {
                database = Room.databaseBuilder(context.getApplicationContext(), ArticleDatabase.class, "article.db")
                        .build();
            }
        }
        return database;
    }

    public static void scheduleUpdateJob(Context context) {
        if (!scheduled) {
            FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

            Job updateJob = jobDispatcher.newJobBuilder()
                    .setService(NewsUpdateService.class)
                    .setTag("temporary-tag")
                    .setRecurring(true)
                    .setLifetime(Lifetime.FOREVER)
                    .setTrigger(Trigger.executionWindow(0, 60))
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .build();
            jobDispatcher.mustSchedule(updateJob);

            Job maintenanceJob = jobDispatcher.newJobBuilder()
                    .setService(MaintenanceService.class)
                    .setTag("temporary-tag1")
                    .setRecurring(true)
                    .setLifetime(Lifetime.FOREVER)
                    .setTrigger(Trigger.executionWindow(43200000, 86400000))
                    .setReplaceCurrent(false)
                    .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                    .build();
            jobDispatcher.mustSchedule(maintenanceJob);

            scheduled = true;

        }
    }
}
