package com.udacity.sanketbhat.news4you.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.model.Article;

public class NewsDetailActivity extends AppCompatActivity {

    private Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra("article")) {
            article = getIntent().getParcelableExtra("article");
        }

        if (article != null) {

            setTitle(article.getTitle());

            ImageView imageView = findViewById(R.id.image);
            Picasso.with(this)
                    .load(article.getUrlToImage())
                    .fit()
                    .centerCrop()
                    .into(imageView);

            findViewById(R.id.fab).setOnClickListener(v -> {
            });

            TextView title = findViewById(R.id.newsTitle);
            TextView author = findViewById(R.id.newsAuthor);
            TextView published = findViewById(R.id.publishedAt);
            TextView desc = findViewById(R.id.description);
            TextView content = findViewById(R.id.newsContent);
            TextView url = findViewById(R.id.newsURL);

            url.setText(article.getUrl());
            url.setOnClickListener(v -> {
                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
                intentBuilder.setToolbarColor(ContextCompat.getColor(NewsDetailActivity.this, R.color.colorPrimary));
                CustomTabsIntent customTabsIntent = intentBuilder.build();
                customTabsIntent.launchUrl(NewsDetailActivity.this, Uri.parse(article.getUrl()));
            });
            title.setText(article.getTitle());
            author.setText(article.getAuthor());
            published.setText(article.getPublishedAt());
            desc.setText(article.getDescription());
            content.setText(article.getContent());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
