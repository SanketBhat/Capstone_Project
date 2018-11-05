package com.udacity.sanketbhat.news4you.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.adapter.InfiniteScrollListener;
import com.udacity.sanketbhat.news4you.adapter.NewsListAdapter;
import com.udacity.sanketbhat.news4you.model.Article;

/**
 * A fragment containing a single category of news articles
 */
public class ArticleCategoryFragment extends Fragment implements NewsListAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, InfiniteScrollListener.LoadNextPageCallback {

    //The fragment argument representing the article type for this fragment
    private static final String ARG_ARTICLE_TYPE = "article_type";

    //Other instance variables
    private NewsListAdapter adapter;
    private MainViewModel viewModel;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int type = -1;
    private boolean refreshing = false;
    private Snackbar snackbar;
    private boolean visible = false;

    public ArticleCategoryFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ArticleCategoryFragment newInstance(int articleType) {
        ArticleCategoryFragment fragment = new ArticleCategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ARTICLE_TYPE, articleType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUserVisibleHint(false);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_ARTICLE_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_category, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(refreshing);

        setupRecyclerView(rootView);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getArticlesByCategory(type).observe(this, adapter::setArticles);

        return rootView;
    }


    private void setupRecyclerView(View rootView) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new NewsListAdapter(null, this);

        recyclerView = rootView.findViewById(R.id.article_category_list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new InfiniteScrollListener(layoutManager, this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(@NonNull Article article, @NonNull ImageView imageView) {
        if (getContext() != null) {
            NewsDetailActivity.launch(getContext(), article, getActivity(), imageView);
        }
    }

    @Override
    public void onRefresh() {
        viewModel.loadArticlesByCategory(type, true);
    }

    @Override
    public void loadNextPage() {
        viewModel.getNextArticlesByCategory(type);
    }

    public void onEvent(String event, int articleType) {
        if (articleType == type) {
            switch (event) {
                case ArticleBaseActivity.EVENT_LOADING:
                    setRefreshing(true);
                    break;

                case ArticleBaseActivity.EVENT_LOAD_EMPTY:
                    setRefreshing(false);
                    showSnackbar("No new news articles");
                    break;

                case ArticleBaseActivity.EVENT_LOAD_FINISHED:
                    setRefreshing(false);
                    showSnackbar("Updated with new articles");
                    break;

                case ArticleBaseActivity.EVENT_LOAD_FAILED:
                    setRefreshing(false);
                    showSnackbar("Failed to load new articles");
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        visible = isVisibleToUser;
    }

    private void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(refreshing);
    }

    private void showSnackbar(String msg) {
        if (visible) {
            if (snackbar == null) snackbar = Snackbar.make(recyclerView, "", Snackbar.LENGTH_SHORT);
            if (snackbar.isShownOrQueued()) snackbar.dismiss();
            snackbar.setText(msg);
            snackbar.show();
        }
    }
}
