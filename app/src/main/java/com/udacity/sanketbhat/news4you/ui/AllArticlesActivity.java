package com.udacity.sanketbhat.news4you.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.adapter.NewsListAdapter;
import com.udacity.sanketbhat.news4you.model.Article;

public class AllArticlesActivity extends AppCompatActivity implements NewsListAdapter.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_articles);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("All Articles");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_all_articles).setChecked(true);


        RecyclerView recyclerView = findViewById(R.id.allArticleList);
        NewsListAdapter adapter = new NewsListAdapter(null, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        AllArticlesViewModel viewModel = ViewModelProviders.of(this).get(AllArticlesViewModel.class);
        viewModel.getAllArticles().observe(this, adapter::setArticles);
    }

    @Override
    public void onItemClick(Article article, ImageView imageView) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imageView, getString(R.string.image_transition_name));
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra("article", article);
        startActivity(intent, options.toBundle());
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

        } else if (id == R.id.nav_about) {

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
