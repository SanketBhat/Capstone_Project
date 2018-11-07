package com.udacity.sanketbhat.news4you.ui;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.adapter.InfiniteScrollListener;
import com.udacity.sanketbhat.news4you.adapter.NewsListAdapter;
import com.udacity.sanketbhat.news4you.model.Article;

public class ArticleSearchActivity extends AppCompatActivity implements NewsListAdapter.OnItemClickListener, InfiniteScrollListener.LoadNextPageCallback, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String EXTRA_LIST_STATE = "listState";
    private SearchViewModel viewModel;
    private RecyclerView recyclerView;
    private Snackbar snackbar;
    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isTablet = getResources().getBoolean(R.bool.tablet_layout);
        FloatingActionButton fab = findViewById(R.id.fab);
        if (isTablet) {
            fab.setOnClickListener(this);
        } else {
            fab.hide();
        }

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        NewsListAdapter adapter = setupRecyclerView();

        viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        viewModel.getSearchResults().observe(this, adapter::setArticles);

        if (getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_SEARCH)) {
            handleSearch();
        }

        AdView adView = findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(request);
    }

    @NonNull
    private NewsListAdapter setupRecyclerView() {
        recyclerView = findViewById(R.id.newsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        NewsListAdapter adapter = new NewsListAdapter(null, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new InfiniteScrollListener(layoutManager, this));
        return adapter;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            outState.putParcelable(EXTRA_LIST_STATE, layoutManager.onSaveInstanceState());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LinearLayoutManager layoutManager;
        if (recyclerView != null && (layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager()) != null) {
            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(EXTRA_LIST_STATE));
        }
    }

    private void handleSearch() {
        if (getIntent().hasExtra(SearchManager.QUERY)) {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            performSearch(query);
        } else {
            Toast.makeText(this, R.string.text_empty_search_string, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void performSearch(String query) {
        query = query.replaceAll("[^A-Za-z0-9]", "");
        viewModel.loadResult(query);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleSearch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setIconified(false);
        }
        return true;
    }

    @Override
    public void onItemClick(Article article, ImageView imageView) {
        if (isTablet) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_detail_container, NewsDetailFragment.getInstance(article), NewsDetailFragment.FRAGMENT_TAG)
                    .commit();
        } else {
            NewsDetailActivity.launch(this, article, this, imageView);
        }
    }

    @Override
    public void loadNextPage() {
        viewModel.loadNextPage();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            NewsDetailFragment fragment = (NewsDetailFragment) getSupportFragmentManager().findFragmentByTag(NewsDetailFragment.FRAGMENT_TAG);
            if (fragment != null && fragment.getArticle() != null) {
                Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                        .setType(getString(R.string.share_text_mime_type))
                        .setText(getString(R.string.article_share_template, fragment.getArticle().getUrl()))
                        .setChooserTitle(R.string.share_intent_chooser_title)
                        .getIntent();

                if (getPackageManager().resolveActivity(shareIntent, 0) != null) {
                    startActivity(shareIntent);
                } else {
                    showSnackbar(getString(R.string.share_article_no_app_to_share));
                }
            } else {
                showSnackbar(getString(R.string.share_article_no_article_selected));
            }
        }
    }

    private void showSnackbar(String s) {
        if (snackbar == null) snackbar = Snackbar.make(recyclerView, "", Snackbar.LENGTH_SHORT);
        if (snackbar.isShownOrQueued()) snackbar.dismiss();
        snackbar.setText(s);
        snackbar.show();
    }
}
