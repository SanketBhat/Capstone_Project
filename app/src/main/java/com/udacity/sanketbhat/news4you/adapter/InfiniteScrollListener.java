package com.udacity.sanketbhat.news4you.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

//RecyclerView's scroll listener helps to load next page if user is at the end of current page
public class InfiniteScrollListener extends RecyclerView.OnScrollListener {
    private LoadNextPageCallback loadNextPageCallback;
    private LinearLayoutManager layoutManager;

    public InfiniteScrollListener(LinearLayoutManager layoutManager, LoadNextPageCallback callback) {
        this.layoutManager = layoutManager;
        loadNextPageCallback = callback;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        if (layoutManager.getChildCount() + layoutManager.findLastVisibleItemPosition() >= layoutManager.getItemCount()) {
            if (loadNextPageCallback != null) loadNextPageCallback.loadNextPage();
        }
    }

    public interface LoadNextPageCallback {
        void loadNextPage();
    }
}
