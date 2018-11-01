package com.udacity.sanketbhat.news4you.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.udacity.sanketbhat.news4you.Dependency;
import com.udacity.sanketbhat.news4you.model.Article;

import java.util.List;

public class AllArticlesViewModel extends AndroidViewModel {

    private final LiveData<List<Article>> allArticles;

    public AllArticlesViewModel(@NonNull Application application) {
        super(application);
        allArticles = Dependency.getArticleDao(getApplication().getApplicationContext()).getAllArticles();
    }

    LiveData<List<Article>> getAllArticles() {
        return allArticles;
    }
}
