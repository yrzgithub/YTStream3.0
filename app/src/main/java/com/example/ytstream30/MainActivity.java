package com.example.ytstream30;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView title;
    ImageView thumbnail,backward,forward,pause_or_play;
    SeekBar bar;
    PlaySong player;


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

        load_gif(thumbnail,R.drawable.yt);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();

        AutoCompleteTextView auto = (AutoCompleteTextView) search.findViewById(androidx.appcompat.R.id.search_src_text);
        auto.setHint("Search YouTube");
        auto.setDropDownBackgroundResource(R.color.white);
        auto.setThreshold(1);
        auto.showDropDown();

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

                    search.onActionViewCollapsed();

                    load_gif(thumbnail,R.drawable.loading);

                    if(player!=null)
                    {
                        player.destroyPlayer();
                    }

                    player = new PlaySong(MainActivity.this,query);
                    player.start();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        DataRetriever retriever = new DataRetriever(newText);

                        String[] titles = retriever.fetch();

                        Log.e("uruttu_titles", Arrays.toString(titles));

                        auto.post(new Runnable() {
                            @Override
                            public void run() {
                                auto.setAdapter(new ArrayAdapter<>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,titles));
                            }
                        });
                    }
                }).start();

                return false;
            }
        });


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