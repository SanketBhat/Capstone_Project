package com.udacity.sanketbhat.news4you.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.sanketbhat.news4you.Dependency;
import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.database.ArticleDao;
import com.udacity.sanketbhat.news4you.model.Article;

import java.util.List;

public class NewsWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new NewsWidgetRemoteViewsFactory(getApplicationContext());
    }

    static class NewsWidgetRemoteViewsFactory implements RemoteViewsFactory {

        List<Article> articles;
        Context context;
        ArticleDao articleDao;

        NewsWidgetRemoteViewsFactory(Context context) {
            articleDao = Dependency.getArticleDao(context);
            this.context = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            articles = articleDao.getHeadlines();
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (articles != null) return articles.size();
            return 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.news_widget_headline_item);
            remoteViews.setTextViewText(R.id.news_widget_headline, articles.get(position).getTitle());
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return articles.get(position).getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
