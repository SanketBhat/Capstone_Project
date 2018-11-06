package com.udacity.sanketbhat.news4you.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.adapter.ArticleCategoryPagerAdapter;
import com.udacity.sanketbhat.news4you.model.ArticleType;

public class ArticleCategoryActivity extends ArticleBaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Snackbar snackbar;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("News Categories");
        setSupportActionBar(toolbar);

        boolean isTablet = getResources().getBoolean(R.bool.tablet_layout);

        FloatingActionButton fab = findViewById(R.id.fab);
        if (isTablet) {
            fab.setOnClickListener(this);
        } else {
            fab.hide();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_categories).setChecked(true);

        ArticleCategoryPagerAdapter mArticleCategoryPagerAdapter = new ArticleCategoryPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mArticleCategoryPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    void onEvent(String event, int articleType) {
        ArticleCategoryPagerAdapter adapter = (ArticleCategoryPagerAdapter) mViewPager.getAdapter();
        if (adapter != null) {
            int position = -1;
            for (int i = 0; i < adapter.getCount(); i++)
                if (ArticleType.Type.types[i] == articleType) {
                    position = i;
                    break;
                }
            if (position >= 0) {
                ArticleCategoryFragment fragment = (ArticleCategoryFragment) getSupportFragmentManager()
                        .findFragmentByTag(adapter.fragmentTags[position]);
                if (fragment != null) fragment.onEvent(event, articleType);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article_category, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_top_headlines) {
            finish();
        } else if (id == R.id.nav_all_articles) {
            Intent intent = new Intent(this, AllArticlesActivity.class);
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
            ArticleCategoryPagerAdapter adapter = (ArticleCategoryPagerAdapter) mViewPager.getAdapter();
            if (adapter != null) {
                NewsDetailFragment fragment = (NewsDetailFragment) getSupportFragmentManager()
                        .findFragmentByTag(NewsDetailFragment.FRAGMENT_TAG + adapter.fragmentTags[mViewPager.getCurrentItem()]);
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

    private void showSnackbar(String s) {
        if (snackbar == null) snackbar = Snackbar.make(mViewPager, "", Snackbar.LENGTH_SHORT);
        if (snackbar.isShownOrQueued()) snackbar.dismiss();
        snackbar.setText(s);
        snackbar.show();
    }
}
