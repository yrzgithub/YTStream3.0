package com.example.ytstream30;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView title;
    ImageView thumbnail,backward,forward,pause_or_play;
    SeekBar bar;
    PlaySong player;
    final static String song_serializable = "song";


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title);
        thumbnail = findViewById(R.id.thumb);
        backward = findViewById(R.id.backward);
        forward = findViewById(R.id.forward_btn);
        pause_or_play = findViewById(R.id.play);
        bar = findViewById(R.id.seek);

        // Marquee
        title.post(new Runnable() {
            @Override
            public void run() {
                title.setSelected(true);
            }
        });

        Intent intent = getIntent();

        if(intent.hasExtra("song"))
        {
            Glide.with(thumbnail).load(R.drawable.loading).into(thumbnail);

            Song song = intent.getSerializableExtra(MainActivity.song_serializable,Song.class);

            player = new PlaySong(MainActivity.this,song);
            player.start();
        }
        else
        {
            load_gif(thumbnail,R.drawable.yt);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        // start

        ShowSuggestions suggestions = new ShowSuggestions(this,menu);

        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        AutoCompleteTextView auto = (AutoCompleteTextView) search.findViewById(androidx.appcompat.R.id.search_src_text);

        search.setOnQueryTextListener((SearchView.OnQueryTextListener) suggestions);
        auto.setOnItemClickListener(suggestions);

        // end


    /*    auto.setHint("Search YouTube");
        auto.setDropDownBackgroundResource(R.color.white);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,new String[0]);
        auto.setAdapter(adapter);

        auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = (String) parent.getItemAtPosition(position);
                auto.setText(title);
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if(!query.isEmpty()) {

                    search.clearFocus();
                    search.onActionViewCollapsed();
                    auto.dismissDropDown();

                    Intent intent = new Intent(MainActivity.this,SearchResultsAct.class);
                    intent.putExtra("query",query);

                    if(player!=null)
                    {
                        player.destroyPlayer();
                       // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }

                  //  load_gif(thumbnail,R.drawable.loading);

                 if(player!=null)
                    {
                        player.destroyPlayer();
                    }

                    player = new PlaySong(MainActivity.this,query);
                    player.start();

                  //  Glide.with(thumbnail).load(R.drawable.yt).into(thumbnail);

                    startActivity(intent);
                }

                return false;
            } */

        /*

            @Override
            public boolean onQueryTextChange(String newText) {

                Executor thread = Executors.newSingleThreadExecutor();
                thread.execute(new Runnable() {
                    @Override
                    public void run() {
                        DataRetriever retriever = new DataRetriever(newText);

                        String[] titles = retriever.getTitles();

                        Log.e("uruttu_titles", Arrays.toString(titles));

                        if(titles.length>0)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.clear();
                                    adapter.addAll(titles);
                                    //if(!auto.isPopupShowing()) auto.showDropDown();
                                }
                            });
                        }
                    }
                });

                return false;
            }
        }); */


        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {

        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    public static void load_gif(ImageView v,int id)
    {
        Glide.with(v).load(id).into(v);
    }

    public static void load_gif(ImageView v,String url)
    {
        Glide.with(v).load(url).into(v);
    }

    @Override
    protected void onDestroy() {
        player.destroyPlayer();
        super.onDestroy();
    }
}