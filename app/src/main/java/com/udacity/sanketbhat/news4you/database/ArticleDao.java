package com.udacity.sanketbhat.news4you.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.udacity.sanketbhat.news4you.model.Article;
import com.udacity.sanketbhat.news4you.model.ArticleType;

import java.util.List;

@Dao
public interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Article article);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ArticleType articleType);

    @Query("select * from articles a, article_type at  where a.id = at.id and at.type =:type order by published_at desc")
    LiveData<List<Article>> getArticles(int type);

    @Query("select * from articles order by published_at desc")
    LiveData<List<Article>> getAllArticles();

    @Query("select id from articles where title = :title and url = :url and published_at = :publishedAt")
    long getArticleId(String title, String url, String publishedAt);

    @Query("select * from article_type where id = :id")
    List<ArticleType> getArticleTypes(long id);
}
