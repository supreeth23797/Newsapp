package com.news.newsapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.news.newsapp.R;
import com.squareup.picasso.Picasso;

public class DetailedActivity extends AppCompatActivity {
    TextView mNewsTitle, mNewsSource, mNewsTime, mNewsDesc, mReadMore;
    ImageView mImage, mActionBack;
    String mUrl, mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        mNewsTitle = findViewById(R.id.newsTitle);
        mNewsSource = findViewById(R.id.newsSource);
        mNewsTime = findViewById(R.id.newsDate);
        mNewsDesc = findViewById(R.id.newsDesc);
        mImage = findViewById(R.id.image);
        mActionBack = findViewById(R.id.actionBack);
        mReadMore = findViewById(R.id.readMore);

        Intent intent = getIntent();
        mImageUrl = intent.getStringExtra("imageUrl");
        mUrl = intent.getStringExtra("url");
        mNewsTitle.setText(intent.getStringExtra("title"));
        mNewsSource.setText(intent.getStringExtra("source"));
        mNewsTime.setText(intent.getStringExtra("time"));
        mNewsDesc.setText(intent.getStringExtra("desc"));
        Picasso.with(DetailedActivity.this).load(mImageUrl).into(mImage);

        mActionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailedActivity.this, DetailedWebViewActivity.class);
                intent.putExtra("url", mUrl);
                startActivity(intent);
            }
        });
    }
}
