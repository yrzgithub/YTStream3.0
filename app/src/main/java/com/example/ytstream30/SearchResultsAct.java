package com.example.ytstream30;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SearchResultsAct extends AppCompatActivity {

    ListView list;
    ImageView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        SwipeRefreshLayout swipe = findViewById(R.id.swipe);
        loading = findViewById(R.id.loading);
        list = findViewById(R.id.vid_list);

        Glide.with(loading).load(R.drawable.loading_pink_list).into(loading);

        Intent intent = getIntent();
        String query = intent.getStringExtra("query");

        retrieve(query);

       swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieve(query);
            }
        });
    }

    public void retrieve(String query)
    {
        Handler handler = new Handler();

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                DataRetriever retriever = new DataRetriever(query);
                List<Song> songs =  retriever.fetch();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loading.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);

                        SongsListAdapter adapter = new SongsListAdapter(SearchResultsAct.this,songs);
                        list.setAdapter(adapter);
                    }
                });
            }
        });
    }
}