package com.udacity.sanketbhat.news4you.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.adapter.InfiniteScrollListener;
import com.udacity.sanketbhat.news4you.adapter.NewsListAdapter;
import com.udacity.sanketbhat.news4you.model.Article;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArticleCategoryFragment extends Fragment implements NewsListAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, InfiniteScrollListener.LoadNextPageCallback {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "ArticleCategoryFragment";
    private NewsListAdapter adapter;
    private MainViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int type;

    public ArticleCategoryFragment() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ArticleCategoryFragment newInstance(int sectionNumber) {
        ArticleCategoryFragment fragment = new ArticleCategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(ARG_SECTION_NUMBER);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_category, container, false);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        setupRecyclerView(rootView);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getArticlesByCategory(type).observe(this, adapter::setArticles);

        return rootView;
    }


    private void setupRecyclerView(View rootView) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new NewsListAdapter(null, this);

        RecyclerView recyclerView = rootView.findViewById(R.id.article_category_list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new InfiniteScrollListener(layoutManager, this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(Article article, ImageView imageView) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), imageView, getContext().getString(R.string.image_transition_name));
        Intent intent = new Intent(getContext(), NewsDetailActivity.class);
        intent.putExtra("article", article);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onRefresh() {
        viewModel.loadArticlesByCategory(type, true);
        swipeRefreshLayout.setRefreshing(true);
        Handler handler = new Handler();
        handler.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 3000);
    }

    @Override
    public void loadNextPage() {
        viewModel.getNextArticlesByCategory(type);
        Log.e(TAG, "loadNextPage: requesting next page");
    }
}
