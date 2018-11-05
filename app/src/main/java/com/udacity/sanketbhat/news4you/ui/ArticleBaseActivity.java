package com.udacity.sanketbhat.news4you.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

public abstract class ArticleBaseActivity extends AppCompatActivity {
    public static final String EVENT_LOADING = "com.udacity.sanketbhat.news4you.EVENT_LOADING";
    public static final String EVENT_LOAD_FINISHED = "com.udacity.sanketbhat.news4you.EVENT_LOAD_FINISHED";
    public static final String EVENT_LOAD_FAILED = "com.udacity.sanketbhat.news4you.EVENT_LOAD_FAILED";
    public static final String EVENT_LOAD_EMPTY = "com.udacity.sanketbhat.news4you.EVENT_LOAD_EMPTY";
    public static final String EXTRA_EVENT_ARTICLE_TYPE = "extraEventArticleType";
    private static final int invalidType = -1;

    private BroadcastReceiver eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(EXTRA_EVENT_ARTICLE_TYPE)) {
                    onEvent(intent.getAction(), intent.getIntExtra(EXTRA_EVENT_ARTICLE_TYPE, invalidType));
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EVENT_LOAD_EMPTY);
        intentFilter.addAction(EVENT_LOAD_FAILED);
        intentFilter.addAction(EVENT_LOAD_FINISHED);
        intentFilter.addAction(EVENT_LOADING);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(eventListener, intentFilter);
    }

    abstract void onEvent(String event, int articleType);

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(eventListener);
        super.onDestroy();
    }
}
