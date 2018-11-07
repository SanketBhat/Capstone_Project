package com.udacity.sanketbhat.news4you;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

public class News4You extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, getString(R.string.GOOGLE_ADS_ID));
        Dependency.getAPIService();
        Dependency.getArticleDao(this);
    }
}
