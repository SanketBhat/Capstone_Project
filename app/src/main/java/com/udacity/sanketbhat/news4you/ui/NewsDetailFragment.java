package com.udacity.sanketbhat.news4you.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.model.Article;
import com.udacity.sanketbhat.news4you.utils.DateAndTimeUtils;

public class NewsDetailFragment extends Fragment {

    public static final String FRAGMENT_TAG = "detailFragment";
    private Article article;

    public NewsDetailFragment() {

    }

    static NewsDetailFragment getInstance(Article article) {
        Bundle arg = new Bundle();
        arg.putParcelable(NewsDetailActivity.EXTRA_ARTICLE, article);
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            article = getArguments().getParcelable(NewsDetailActivity.EXTRA_ARTICLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_news_detail, container, false);

        if (article != null && getContext() != null) {
            TextView title = rootView.findViewById(R.id.newsTitle);
            TextView author = rootView.findViewById(R.id.newsAuthor);
            TextView published = rootView.findViewById(R.id.publishedAt);
            TextView desc = rootView.findViewById(R.id.description);
            TextView content = rootView.findViewById(R.id.newsContent);
            TextView url = rootView.findViewById(R.id.newsURL);
            ImageView imageView = rootView.findViewById(R.id.imageTablet);

            if (imageView != null) {
                Picasso.with(getContext())
                        .load(article.getUrlToImage())
                        .placeholder(R.drawable.news_image_placeholder)
                        .error(R.drawable.news_image_placeholder)
                        .into(imageView);
            }

            url.setText(article.getUrl());
            url.setOnClickListener(v -> {
                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
                intentBuilder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                CustomTabsIntent customTabsIntent = intentBuilder.build();
                customTabsIntent.launchUrl(getContext(), Uri.parse(article.getUrl()));
            });
            title.setText(article.getTitle());
            author.setText(article.getAuthor());
            String dateString = DateAndTimeUtils.getDateDisplayString(article.getPublishedAt());
            published.setText(dateString);
            desc.setText(article.getDescription());
            content.setText(article.getContent());
        }
        return rootView;
    }

    public Article getArticle() {
        return article;
    }
}
