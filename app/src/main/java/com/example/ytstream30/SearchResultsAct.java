package com.example.ytstream30;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SearchResultsAct extends AppCompatActivity {

    ListView list;
    ImageView loading;
    final static String QUERY_PATH = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        SwipeRefreshLayout swipe = findViewById(R.id.swipe);
        loading = findViewById(R.id.loading);
        list = findViewById(R.id.vid_list);

        Glide.with(loading).load(R.drawable.loading_pink_list).into(loading);

        Intent intent = getIntent();
        retrieve(intent);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieve(intent);
                swipe.setRefreshing(false);
            }
        });
    }

    public void retrieve(Intent intent)
    {
        String type = intent.getStringExtra(Song.SONG_TYPE);

        if(type.equals(Song.YT))
        {
            String query = intent.getStringExtra(QUERY_PATH);

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

                            YTSongsListAdapter adapter = new YTSongsListAdapter(SearchResultsAct.this,songs);
                            list.setAdapter(adapter);
                        }
                    });
                }
            });
        }

        else
        {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_launcher_foreground,null))
                        .setMessage("Do You want to Enable permissions")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",getPackageName(),null)));
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SearchResultsAct.super.getOnBackPressedDispatcher().onBackPressed();
                            }
                        })
                        .show();
            }



        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_act_menu,menu);

        ShowSuggestions suggestions  = new ShowSuggestions(this,menu);

        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        AutoCompleteTextView auto = search.findViewById(androidx.appcompat.R.id.search_src_text);

        auto.setOnItemClickListener(suggestions);
        search.setOnQueryTextListener(suggestions);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}