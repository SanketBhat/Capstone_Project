package com.udacity.sanketbhat.news4you.ui;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.adapter.NewsListAdapter;
import com.udacity.sanketbhat.news4you.model.Article;

public class AllArticlesActivity extends AppCompatActivity implements NewsListAdapter.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String EXTRA_LIST_STATE = "listState";
    private RecyclerView recyclerView;
    private boolean isTablet;
    private Snackbar snackbar;
    private NewsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_articles);
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
        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));

        setupNavigationDrawer(toolbar);
        setupRecyclerView();

        AllArticlesViewModel viewModel = ViewModelProviders.of(this).get(AllArticlesViewModel.class);
        viewModel.getAllArticles().observe(this, adapter::setArticles);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.newsList);
        adapter = new NewsListAdapter(null, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
        navigationView.getMenu().findItem(R.id.nav_all_articles).setChecked(true);
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
    public void onItemClick(Article article, ImageView imageView) {
        if (isTablet) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_detail_container, NewsDetailFragment.getInstance(article), NewsDetailFragment.FRAGMENT_TAG)
                    .commit();
        } else {
            NewsDetailActivity.launch(this, article, this, imageView);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_top_headlines) {
            finish();
        } else if (id == R.id.nav_categories) {
            Intent intent = new Intent(this, ArticleCategoryActivity.class);
            startActivity(intent);
            finish();
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
