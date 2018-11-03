package com.udacity.sanketbhat.news4you;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.udacity.sanketbhat.news4you.api.NewsAPIService;
import com.udacity.sanketbhat.news4you.database.ArticleDao;
import com.udacity.sanketbhat.news4you.model.Article;
import com.udacity.sanketbhat.news4you.model.ArticleType;
import com.udacity.sanketbhat.news4you.model.NewsResponse;
import com.udacity.sanketbhat.news4you.widget.NewsWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private static final Object LOCK = new Object();
    private static final int PAGE_SIZE = 20;
    private static final String TOTAL_PAGES_TEMPLATE = "total_pages_";
    private static final String CURRENT_PAGE_TEMPLATE = "current_page_";
    private static final String LAST_REFRESH_TEMPLATE = "last_refresh_";
    private static final String LOADING_PAGE_TEMPLATE = "loading_";
    private static final long AUTO_REFRESH_INTERVAL = 120000;// Two minutes(in milliseconds)
    private static final String TAG = "Repository";
    @SuppressLint("StaticFieldLeak") //It is OK to use Application Context
    private static Repository mInstance;
    //Executor used for database operations
    private final Executor executor;
    private ArticleDao articleDao;
    private NewsAPIService apiService;
    private Bundle extras;
    private Context context;

    private Repository(Context context) {
        this.articleDao = Dependency.getArticleDao(context);
        apiService = Dependency.getAPIService();
        this.context = context;
        executor = Executors.newSingleThreadExecutor();
        extras = new Bundle();
    }

    static Repository getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LOCK) {
                mInstance = new Repository(context.getApplicationContext());
            }
        }
        return mInstance;
    }

    public void getTopHeadlines(boolean onDemand) {
        int type = ArticleType.Type.TOP_HEAD;
        if ((onDemand || isAfterInterval(type)) && isNotLoading(type)) {
            getTopHeadlines(1);
        }

    }

    private boolean isNotLoading(int type) {
        return !extras.getBoolean(LOADING_PAGE_TEMPLATE + type, false);
    }

    private boolean isAfterInterval(int type) {
        long lastRefreshed = extras.getLong(LAST_REFRESH_TEMPLATE + type, 0);
        return (System.currentTimeMillis() - lastRefreshed) > AUTO_REFRESH_INTERVAL;
    }

    public void getNextTopHeadlines() {
        if (isNotLoading(ArticleType.Type.TOP_HEAD)) {
            int nextPage = getNextPageNumber(ArticleType.Type.TOP_HEAD);
            if (nextPage != -1) getTopHeadlines(nextPage);
        }
    }

    private int getNextPageNumber(int type) {
        int totalPages = extras.getInt(TOTAL_PAGES_TEMPLATE + type, 1);
        int currentPage = extras.getInt(CURRENT_PAGE_TEMPLATE + type, 1);

        if (currentPage < totalPages) {
            return ++currentPage;
        } else {
            return -1;
        }
    }

    private void getTopHeadlines(int pageNumber) {
        Log.d(TAG, "getTopHeadlines: Requesting page: " + pageNumber + " type= " + ArticleType.Type.getName(ArticleType.Type.TOP_HEAD));

        extras.putBoolean(LOADING_PAGE_TEMPLATE + ArticleType.Type.TOP_HEAD, true);
        apiService.getTopHeadlines("in", pageNumber, context.getString(R.string.NEWS_API_KEY))
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            NewsResponse newsResponse = response.body();

                            storeTotalPages(newsResponse.getTotalResults(), ArticleType.Type.TOP_HEAD);
                            storeCurrentPage(call.request().url().toString(), ArticleType.Type.TOP_HEAD);

                            if (newsResponse.getArticles() != null && newsResponse.getStatus().equalsIgnoreCase("ok")) {
                                Toast.makeText(context, "Got fresh data from the server", Toast.LENGTH_SHORT).show();
                                insertAllAsync(newsResponse.getArticles(), ArticleType.Type.TOP_HEAD);
                            } else {
                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void storeCurrentPage(String s, int type) {
        String[] queries = s.substring(s.lastIndexOf("/")).split("&");
        for (String query : queries) {
            if (query.contains("page")) {
                int page = Integer.valueOf(query.split("=")[1].trim());
                extras.putInt(CURRENT_PAGE_TEMPLATE + type, page);
                if (page == 1) {
                    extras.putLong(LAST_REFRESH_TEMPLATE + type, System.currentTimeMillis());
                }
                break;
            }
        }
    }

    public void getArticlesByCategory(int type, boolean onDemand) {
        if ((onDemand || isAfterInterval(type)) && isNotLoading(type)) {
            getArticlesByCategory(type, 1);
        }
    }

    public void getNextArticleByCategory(int type) {
        if (isNotLoading(type)) {
            int nextPage = getNextPageNumber(type);
            Log.e(TAG, "getNextArticleByCategory: not loading next page yet; next page number: " + nextPage);
            if (nextPage != -1)
                getArticlesByCategory(type, nextPage);
        }
    }

    private void getArticlesByCategory(int type, int pageNumber) {
        Log.d(TAG, "getArticlesByCategory: Requesting page: " + pageNumber + " type= " + ArticleType.Type.getName(type));

        extras.putBoolean(LOADING_PAGE_TEMPLATE + type, true);
        apiService.getArticlesByCategory("in", pageNumber, context.getString(R.string.NEWS_API_KEY), ArticleType.Type.getName(type))
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            NewsResponse newsResponse = response.body();

                            storeTotalPages(newsResponse.getTotalResults(), type);
                            storeCurrentPage(call.request().url().toString(), type);

                            if (newsResponse.getArticles() != null && newsResponse.getStatus().equalsIgnoreCase("ok")) {
                                insertAllAsync(newsResponse.getArticles(), type);
                            }
                            return;
                        }
                        Toast.makeText(context, "Error when loading article type: " + ArticleType.Type.getName(type), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                        Toast.makeText(context, "Error when loading article type: " + ArticleType.Type.getName(type), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    /*public List<ArticleType> getArticleTypes(long id) {
        return articleDao.getArticleTypes(id);
    }*/

    private void storeTotalPages(int responseCount, int type) {
        int totalPages;
        if (responseCount < 20)
            totalPages = 1;
        else
            totalPages = responseCount / PAGE_SIZE;

        extras.putInt(TOTAL_PAGES_TEMPLATE + type, totalPages);
    }

    private void insertAllAsync(Article[] articles, int type) {
        executor.execute(() -> insertAll(articles, type));
    }

    public List<Article> insertAll(Article[] articles, int type) {
        ArrayList<Article> insertedArticles = new ArrayList<>();
        int insertCount = 0;
        for (Article article : articles) {
            try {
                long id = articleDao.insert(article);
                if (id == -1) {
                    id = articleDao.getArticleId(article.getTitle(), article.getUrl(), article.getPublishedAt());
                    Log.e("Retrieved id", "" + id);
                } else {
                    insertCount++;
                    article.setId((int) id);
                    insertedArticles.add(article);
                }
                ArticleType articleType = new ArticleType();
                articleType.setId((int) id);
                articleType.setType(type);
                articleDao.insert(articleType);
            } catch (Exception e) {
                e.printStackTrace();
                //TODO: Report using google analytics
            }
        }
        if (insertCount > 0 && type == ArticleType.Type.TOP_HEAD) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, NewsWidget.class));
            if (ids.length > 0) {
                Intent intent = new Intent(context, NewsWidget.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                context.sendBroadcast(intent);
            }
            extras.putBoolean(LOADING_PAGE_TEMPLATE + type, false);
            return insertedArticles;
        }
        extras.putBoolean(LOADING_PAGE_TEMPLATE + type, false);
        return null;
    }

}
