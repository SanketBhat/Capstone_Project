package com.udacity.sanketbhat.news4you.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.udacity.sanketbhat.news4you.model.ArticleType;
import com.udacity.sanketbhat.news4you.ui.ArticleCategoryFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ArticleCategoryPagerAdapter extends FragmentPagerAdapter {
    public ArticleCategoryPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return ArticleType.Type.getName(ArticleType.Type.types[position]);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return ArticleCategoryFragment.newInstance(ArticleType.Type.types[position]);
    }

    @Override
    public int getCount() {
        // Show 6 total pages.
        return ArticleType.Type.types.length;
    }
}
