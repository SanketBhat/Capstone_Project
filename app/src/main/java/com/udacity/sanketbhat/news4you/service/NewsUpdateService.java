package com.udacity.sanketbhat.news4you.service;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.udacity.sanketbhat.news4you.Dependency;
import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.Repository;
import com.udacity.sanketbhat.news4you.api.NewsAPIService;
import com.udacity.sanketbhat.news4you.model.Article;
import com.udacity.sanketbhat.news4you.model.ArticleType;
import com.udacity.sanketbhat.news4you.model.NewsResponse;
import com.udacity.sanketbhat.news4you.ui.MainActivity;
import com.udacity.sanketbhat.news4you.ui.NewArticleNotification;

import java.io.IOException;
import java.util.List;

public class NewsUpdateService extends JobService {

    private static AsyncTask<Void, Void, Boolean> updateCheckTask;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(JobParameters job) {
        updateCheckTask = new AsyncTask<Void, Void, Boolean>() {
            //Return answer for the question whether updating data is done?
            @Override
            protected Boolean doInBackground(Void... voids) {
                if (!MainActivity.isAppAlive) {
                    NewsAPIService apiService = Dependency.getAPIService();
                    Repository repository = Dependency.getRepository(getApplicationContext());

                    try {
                        NewsResponse response = apiService.getTopHeadlines("in", 1, getString(R.string.NEWS_API_KEY))
                                .execute()
                                .body();
                        if (response != null && response.getArticles() != null) {
                            List<Article> insertedArticles = repository.insertAll(response.getArticles(), ArticleType.Type.TOP_HEAD);
                            if (insertedArticles != null && insertedArticles.size() > 0) {
                                NewArticleNotification.notify(getApplicationContext(), insertedArticles,
                                        insertedArticles.size());

                            }
                            return true;
                        } else {
                            return false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        //TODO: Add analytics and report
                        return false;
                    }
                } else {
                    //App is in foreground; No need to update data in background
                    return true;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                //If updating done(aBoolean = true) then no need to reschedule
                jobFinished(job, !aBoolean);
            }
        };

        updateCheckTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (updateCheckTask != null) {
            updateCheckTask.cancel(true);
            updateCheckTask = null;
            return true;
        }
        return false;
    }
}
