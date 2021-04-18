package com.news.newsapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.news.newsapp.model.Articles;
import com.news.newsapp.ui.DetailedActivity;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    Context mContext;
    List<Articles> mArticles;

    public Adapter(Context context, List<Articles> articles) {
        this.mContext = context;
        this.mArticles = articles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.items,parent,false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Articles article = mArticles.get(position);
        Picasso.with(mContext).load(article.getUrlToImage()).into(holder.mImageView);
        holder.mNewsTitle.setText(article.getTitle());
        holder.mNewsSource.setText(article.getSource().getName());
        holder.mNewsDate.setText(getDate(article.getPublishedAt()));

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailedActivity.class);
                intent.putExtra("title",article.getTitle());
                intent.putExtra("source",article.getSource().getName());
                intent.putExtra("time",getDate(article.getPublishedAt()));
                intent.putExtra("desc",article.getDescription());
                intent.putExtra("imageUrl",article.getUrlToImage());
                intent.putExtra("url",article.getUrl());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mNewsTitle, mNewsSource, mNewsDate;
        ImageView mImageView;
        CardView mCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mNewsTitle = itemView.findViewById(R.id.newsTitle);
            mNewsSource = itemView.findViewById(R.id.newsSource);
            mNewsDate = itemView.findViewById(R.id.newsDate);
            mImageView = itemView.findViewById(R.id.image);
            mCardView = itemView.findViewById(R.id.cardView);
        }
    }


    public String getDate(String t){
        String newsDate = "N/A";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date = simpleDateFormat.parse(t);
            newsDate = simpleDateFormat.format(date);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return newsDate;
    }
}
