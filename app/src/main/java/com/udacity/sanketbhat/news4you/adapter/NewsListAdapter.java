package com.udacity.sanketbhat.news4you.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.sanketbhat.news4you.R;
import com.udacity.sanketbhat.news4you.model.Article;

import java.util.List;

//Common recyclerView adapter for news articles used throughout app
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsListViewHolder> {

    final private OnItemClickListener itemClickListener;
    private List<Article> articles;

    public NewsListAdapter(List<Article> articles, OnItemClickListener clickListener) {
        this.articles = articles;
        this.itemClickListener = clickListener;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_list_item, viewGroup, false);
        return new NewsListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull NewsListViewHolder newsListViewHolder, int position) {
        newsListViewHolder.bind(articles.get(position));
    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.size();
    }

    @Override
    public void onViewRecycled(@NonNull NewsListViewHolder holder) {
        holder.image.setImageDrawable(null);
        super.onViewRecycled(holder);
    }

    public interface OnItemClickListener {
        void onItemClick(Article article, ImageView imageView);
    }

    class NewsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView title;
        final TextView subTitle;
        final ImageView image;

        NewsListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.newsTitle);
            image = itemView.findViewById(R.id.newsThumbnail);
            subTitle = itemView.findViewById(R.id.newsSubtitle);
            itemView.setOnClickListener(this);
        }


        void bind(Article article) {

            title.setText(article.getTitle());

            if (article.getDescription() != null && !article.getDescription().equals(""))
                subTitle.setText(article.getDescription());
            else
                subTitle.setText(article.getUrl());

            if (article.getUrlToImage() != null && !article.getUrlToImage().equals("")) {
                Picasso.with(itemView.getContext())
                        .load(article.getUrlToImage())
                        .placeholder(R.drawable.news_image_placeholder)
                        .error(R.drawable.news_image_placeholder)
                        .fit()
                        .centerCrop()
                        .into(image);
            }
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null)
                itemClickListener.onItemClick(articles.get(getAdapterPosition()), image);
        }
    }
}
