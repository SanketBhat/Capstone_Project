package com.udacity.sanketbhat.news4you.ui;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;

import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.adapter.InfiniteScrollListener;
import com.udacity.sanketbhat.news4you.adapter.NewsListAdapter;
import com.udacity.sanketbhat.news4you.model.Article;

public class ArticleSearchActivity extends AppCompatActivity implements NewsListAdapter.OnItemClickListener, InfiniteScrollListener.LoadNextPageCallback, SwipeRefreshLayout.OnRefreshListener {

    private SearchViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView recyclerView = findViewById(R.id.newsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        NewsListAdapter adapter = new NewsListAdapter(null, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new InfiniteScrollListener(layoutManager, this));

        viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        viewModel.getSearchResults().observe(this, adapter::setArticles);

        if (getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_SEARCH)) {
            handleSearch();
        }
    }

    private void handleSearch() {
        if (getIntent().hasExtra(SearchManager.QUERY)) {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            performSearch(query);
        } else {
            Toast.makeText(this, "No search string provided", Toast.LENGTH_SHORT).show();
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
        menu.findItem(R.id.action_search).expandActionView();
        return true;
    }

    @Override
    public void onItemClick(Article article, ImageView imageView) {
        NewsDetailActivity.launch(this, article, this, imageView);
    }

    @Override
    public void loadNextPage() {
        viewModel.loadNextPage();
    }

    @Override
    public void onRefresh() {

    }
}
