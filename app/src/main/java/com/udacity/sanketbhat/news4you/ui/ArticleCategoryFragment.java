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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

    //Extra states to handle config changes
    private static final String EXTRA_NEWS_LIST_STATE = "newsListState";
    private static final String EXTRA_CONTAINER_ID = "containerId";
    private static final String EXTRA_REFRESHING_STATE = "refreshingState";

    //Other instance variables
    private NewsListAdapter adapter;
    private MainViewModel viewModel;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int type = -1;
    private boolean refreshing = false;
    private Snackbar snackbar;
    private boolean visible = false;

    //Id for the view that will be replaced by detail fragment in tablet layouts
    private int containerId = (int) (Math.random() * Integer.MAX_VALUE);

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
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setUserVisibleHint(false);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_ARTICLE_TYPE);
        }
        if (state != null) {
            containerId = state.getInt(EXTRA_CONTAINER_ID);
            refreshing = state.getBoolean(EXTRA_REFRESHING_STATE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle state) {
        View rootView = inflater.inflate(R.layout.content_main, container, false);

        View v = rootView.findViewById(R.id.main_detail_container);
        if (v != null) v.setId(containerId);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(refreshing);

        setupRecyclerView(rootView);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getArticlesByCategory(type).observe(this, adapter::setArticles);

        AdView adView = rootView.findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(request);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        RecyclerView.LayoutManager layoutManager;
        if (recyclerView != null && (layoutManager = recyclerView.getLayoutManager()) != null)
            outState.putParcelable(EXTRA_NEWS_LIST_STATE, layoutManager.onSaveInstanceState());
        outState.putInt(EXTRA_CONTAINER_ID, containerId);
        outState.putBoolean(EXTRA_REFRESHING_STATE, refreshing);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null && savedInstanceState != null) {
            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(EXTRA_NEWS_LIST_STATE));
        }
    }

    private void setupRecyclerView(View rootView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new NewsListAdapter(null, this);

        recyclerView = rootView.findViewById(R.id.newsList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new InfiniteScrollListener(layoutManager, this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(@NonNull Article article, @NonNull ImageView imageView) {
        if (getContext() != null) {
            boolean isTablet = getContext().getResources().getBoolean(R.bool.tablet_layout);
            if (isTablet) {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(containerId, NewsDetailFragment.getInstance(article), NewsDetailFragment.FRAGMENT_TAG + getTag())
                            .commit();
                }
            } else {
                NewsDetailActivity.launch(getContext(), article, getActivity(), imageView);
            }
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
                    showSnackbar(getString(R.string.text_article_update_empty));
                    break;

                case ArticleBaseActivity.EVENT_LOAD_FINISHED:
                    setRefreshing(false);
                    showSnackbar(getString(R.string.text_article_update_success));
                    break;

                case ArticleBaseActivity.EVENT_LOAD_FAILED:
                    setRefreshing(false);
                    showSnackbar(getString(R.string.text_article_update_fail));
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
