package com.udacity.sanketbhat.news4you.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

@Dao
public interface ArticleMaintenanceDao {
    @Query("delete from articles where id not in(" +
            "select a.id from articles a, article_type at " +
            "where at.id = a.id and at.type = :type " +
            "order by published_at desc " +
            "limit 20" +
            ")")
    void deleteOldArticles(int type);
}
