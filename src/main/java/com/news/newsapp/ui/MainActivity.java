package com.news.newsapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.news.newsapp.Adapter;
import com.news.newsapp.ApiClient;
import com.news.newsapp.model.Articles;
import com.news.newsapp.model.Headlines;
import com.news.newsapp.R;
import com.news.newsapp.Utils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Adapter mAdapter;
    List<Articles> mArticles = new ArrayList<>();
    LinearLayoutManager mLinearLayoutManager;
    int mVisibleItemsCount = 0, mTotalItemsCount = 0, mScrolledItemsCount = 0;
    boolean mIsScrolling = false;
    int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);
        mRecyclerView = findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new Adapter(MainActivity.this, mArticles);
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                mArticles.clear();
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

                    if (mIsScrolling && (mVisibleItemsCount + mScrolledItemsCount == mTotalItemsCount)) {
                        mPage++;
                        retrieveJson();
                        mIsScrolling = false;
                    }
                }
            }
        });

        // Calling for first time, so no page increment
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo == null) {
            // Offline handling, load previously saved json data
            Toast.makeText(MainActivity.this,
                    getResources().getString(R.string.check_internet),
                    Toast.LENGTH_SHORT).show();
        } else {
            retrieveJson();
        }
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
                    mSwipeRefreshLayout.setRefreshing(false);
                    if(response.body().getArticles().size() == 0){
                        mPage--;
                        Toast.makeText(MainActivity.this,
                                getResources().getString(R.string.thats_all),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mArticles.addAll(response.body().getArticles());
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                mPage--;
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this,
                        getResources().getString(R.string.check_internet),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Save latest mArticles data in db to load in offline mode
    }
}
