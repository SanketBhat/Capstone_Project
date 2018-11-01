package com.udacity.sanketbhat.news4you.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.udacity.sanketbhat.news4you.Dependency;
import com.udacity.sanketbhat.news4you.Repository;
import com.udacity.sanketbhat.news4you.database.ArticleDao;
import com.udacity.sanketbhat.news4you.model.Article;
import com.udacity.sanketbhat.news4you.model.ArticleType;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final Repository repository;
    private final ArticleDao articleDao;
    private final LiveData<List<Article>> topHeadlines;

    public MainViewModel(Application application) {
        super(application);
        articleDao = Dependency.getArticleDao(getApplication().getApplicationContext());
        repository = Dependency.getRepository(getApplication().getApplicationContext());
        topHeadlines = articleDao.getArticles(ArticleType.Type.TOP_HEAD);
    }


    LiveData<List<Article>> getArticleList() {
        loadTopHeadlines(false);
        return articleDao.getArticles(ArticleType.Type.TOP_HEAD);
    }

    LiveData<List<Article>> getArticlesByCategory(int type) {
        loadArticlesByCategory(type, false);
        return articleDao.getArticles(type);
    }

    LiveData<List<Article>> getAllArticles() {
        return articleDao.getAllArticles();
    }

    public void loadTopHeadlines(boolean onDemand) {
        repository.getTopHeadlines(onDemand);
    }

    public void loadArticlesByCategory(int type, boolean onDemand) {
        repository.getArticlesByCategory(type, onDemand);
    }
}
