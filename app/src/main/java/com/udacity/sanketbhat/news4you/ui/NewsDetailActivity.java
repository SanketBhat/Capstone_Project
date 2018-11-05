package com.udacity.sanketbhat.news4you.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.model.Article;

public class NewsDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_ARTICLE = "article";
    private Article article;
    private Snackbar snackbar;

    public static void launch(@NonNull Context context, @NonNull Article article,
                              @Nullable Activity activity, @Nullable ImageView imageView) {

        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.EXTRA_ARTICLE, article);

        //If possible make shared element transition
        if (activity != null && imageView != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView,
                    context.getString(R.string.image_transition_name));

            context.startActivity(intent, options.toBundle());
            return;
        }

        context.startActivity(intent);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(EXTRA_ARTICLE)) {
            article = getIntent().getParcelableExtra(EXTRA_ARTICLE);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.news_detail_activity_container, NewsDetailFragment.getInstance(article), NewsDetailFragment.FRAGMENT_TAG)
                    .commit();
        }

        if (article != null) {
            ImageView imageView = findViewById(R.id.image);
            Picasso.with(this)
                    .load(article.getUrlToImage())
                    .fit()
                    .centerCrop()
                    .into(imageView);

            findViewById(R.id.fab).setOnClickListener(this);

            setTitle(article.getTitle());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            NewsDetailFragment fragment = (NewsDetailFragment) getSupportFragmentManager().findFragmentByTag(NewsDetailFragment.FRAGMENT_TAG);
            if (fragment != null && fragment.getArticle() != null) {
                Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                        .setType("text/plain")
                        .setText(getString(R.string.article_share_template, fragment.getArticle().getUrl()))
                        .setChooserTitle(R.string.share_intent_chooser_title)
                        .getIntent();

                if (getPackageManager().resolveActivity(shareIntent, 0) != null) {
                    startActivity(shareIntent);
                } else {
                    showSnackbar("No app available to share");
                }
            } else {
                showSnackbar("Select an article to share");
            }
        }
    }

    private void showSnackbar(String s) {
        if (snackbar == null)
            snackbar = Snackbar.make(findViewById(R.id.fab), "", Snackbar.LENGTH_SHORT);
        if (snackbar.isShownOrQueued()) snackbar.dismiss();
        snackbar.setText(s);
        snackbar.show();
    }
}
