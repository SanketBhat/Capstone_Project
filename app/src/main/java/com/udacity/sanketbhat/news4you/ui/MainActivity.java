package com.udacity.sanketbhat.news4you.ui;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.udacity.sanketbhat.news4you.Dependency;
import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.adapter.InfiniteScrollListener;
import com.udacity.sanketbhat.news4you.adapter.NewsListAdapter;
import com.udacity.sanketbhat.news4you.model.Article;
import com.udacity.sanketbhat.news4you.model.ArticleType;

public class MainActivity extends ArticleBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewsListAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, InfiniteScrollListener.LoadNextPageCallback, View.OnClickListener {

    public static boolean isAppAlive = false;
    private static final String EXTRA_LIST_STATE = "listState";
    private MainViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsListAdapter adapter;
    private Snackbar snackbar;
    private RecyclerView recyclerView;
    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isAppAlive = true;
        isTablet = getResources().getBoolean(R.bool.tablet_layout);

        FloatingActionButton fab = findViewById(R.id.fab);
        if (isTablet) {
            fab.setOnClickListener(this);
        } else {
            fab.hide();
        }

        setupNavigationDrawer(toolbar);
        setupRecyclerView();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getArticleList().observe(this, adapter::setArticles);

        Dependency.scheduleUpdateJob(getApplicationContext(), false);
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

    @Override
    void onEvent(String event, int articleType) {
        if (articleType == ArticleType.Type.TOP_HEAD) {
            switch (event) {
                case EVENT_LOADING:
                    swipeRefreshLayout.setRefreshing(true);
                    break;
                case EVENT_LOAD_EMPTY:
                    swipeRefreshLayout.setRefreshing(false);
                    showSnackbar("No new articles added!");
                    break;
                case EVENT_LOAD_FAILED:
                    swipeRefreshLayout.setRefreshing(false);
                    showSnackbar("Failed to get news articles");
                    break;
                case EVENT_LOAD_FINISHED:
                    swipeRefreshLayout.setRefreshing(false);
                    showSnackbar("Updated with new news articles");
                    break;
            }
        }
    }

    private void showSnackbar(String s) {
        if (snackbar == null) snackbar = Snackbar.make(recyclerView, "", Snackbar.LENGTH_SHORT);
        if (snackbar.isShownOrQueued()) snackbar.dismiss();
        snackbar.setText(s);
        snackbar.show();
    }

    private void setupRecyclerView() {
        adapter = new NewsListAdapter(null, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);


        recyclerView = findViewById(R.id.newsList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new InfiniteScrollListener(layoutManager, this));
        recyclerView.setHasFixedSize(true);
    }

    private void setupNavigationDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_top_headlines).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
            searchView.setSubmitButtonEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all_articles) {
            Intent intent = new Intent(this, AllArticlesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_categories) {
            Intent intent = new Intent(this, ArticleCategoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
    public void onRefresh() {
        viewModel.loadTopHeadlines(true);
        //It will automatically shows loading when loading even occurs
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        isAppAlive = false;
        super.onDestroy();
    }

    @Override
    public void loadNextPage() {
        viewModel.getNextTopHeadlines();
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
}
