package com.news.newsapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.news.newsapp.Adapter;
import com.news.newsapp.ApiClient;
import com.news.newsapp.model.Articles;
import com.news.newsapp.model.Headlines;
import com.news.newsapp.R;
import com.news.newsapp.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    LinearLayout mNoInternet;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Adapter mAdapter;
    List<Articles> mArticles;
    LinearLayoutManager mLinearLayoutManager;
    int mVisibleItemsCount = 0, mTotalItemsCount = 0, mScrolledItemsCount = 0;
    boolean mIsScrolling = false;
    boolean mLoadContent = false;
    int mPage = 1;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mPreferenceEditor;
    ConnectivityManager mConnectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);
        mRecyclerView = findViewById(R.id.recyclerView);
        mNoInternet = findViewById(R.id.noInternet);

        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mArticles = new ArrayList<>();

        mSharedPreferences = getPreferences(MODE_PRIVATE);
        mPreferenceEditor = mSharedPreferences.edit();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                if(netInfo!=null && mArticles != null){
                    mArticles.clear();
                }
                mPage = 1;
                retrieveJson();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    mVisibleItemsCount = mLinearLayoutManager.getChildCount();
                    mTotalItemsCount = mLinearLayoutManager.getItemCount();
                    mScrolledItemsCount = mLinearLayoutManager.findFirstVisibleItemPosition();
                    if (mIsScrolling && (mVisibleItemsCount + mScrolledItemsCount == mTotalItemsCount && mLoadContent)) {
                        mLoadContent = false;
                        mPage++;
                        retrieveJson();
                        mIsScrolling = false;
                    }
                }
            }
        });

        // Calling for first time, so no page increment
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        if (netInfo == null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Articles>>(){}.getType();
            mArticles = gson.fromJson(mSharedPreferences.getString("articles", ""), type);
            mPage = mSharedPreferences.getInt("page", 1);
            if(isNoArticlesToShow()){
                mNoInternet.setVisibility(View.VISIBLE);
                return;
            }
        } else {
            retrieveJson();
        }
        mAdapter = new Adapter(MainActivity.this, mArticles);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void retrieveJson() {
        mSwipeRefreshLayout.setRefreshing(true);
        Call<Headlines> call;
        call = ApiClient.getInstance()
                .getApi()
                .getTopHeadlines( Utils.API_KEY, Utils.COUNTRY, Utils.PAGE_SIZE, mPage);
        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines> call, Response<Headlines> response) {
                if (response.isSuccessful() &&
                        response.body().getArticles() != null) {
                    mNoInternet.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    if(response.body().getArticles().size() == 0){
                        mPage--;
                        mLoadContent = false;
                        Toast.makeText(MainActivity.this,
                                getResources().getString(R.string.thats_all),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Articles> tempList = new ArrayList<Articles>();
                    tempList.addAll(response.body().getArticles());
                    // temporary list to give shuffle effect on refresh ONLY for new articles fetched
                    // and not for whole articles list as an article may repeat when scrolled
                    Collections.shuffle(tempList);
                    mArticles.addAll(tempList);
                    mAdapter.notifyDataSetChanged();
                    mLoadContent = true;
                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                mPage--;
                mLoadContent = false;
                mSwipeRefreshLayout.setRefreshing(false);
                if(isNoArticlesToShow()){
                    mPage = 1;
                    mNoInternet.setVisibility(View.VISIBLE);
                    return;
                }
                Toast.makeText(MainActivity.this,
                        getResources().getString(R.string.check_internet),
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    private boolean isNoArticlesToShow(){
        if(mArticles!=null && mArticles.size()!=0){
            return false;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Gson gson = new Gson();
        mPreferenceEditor
                .putString("articles", gson.toJson(mArticles))
                .putInt("page", mPage)
                .commit();
    }

}
