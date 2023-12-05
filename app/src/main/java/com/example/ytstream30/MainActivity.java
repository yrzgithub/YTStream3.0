package com.example.ytstream30;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView title;
    ImageView thumbnail,backward,forward,pause_or_play;
    SeekBar bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title);
        thumbnail = findViewById(R.id.thumb);
        backward = findViewById(R.id.backward);
        forward = findViewById(R.id.forward);
        pause_or_play = findViewById(R.id.play);
        bar = findViewById(R.id.seek);

        load_gif(this,thumbnail,R.drawable.yt);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        if(search==null) return false;
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(!query.isEmpty()) {

                    Log.e("uruttu_query",query);
                   PlaySong song = new PlaySong(MainActivity.this,query);
                   //song.start();

                    new Thread(song).start();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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

    public static void load_gif(Context c, ImageView v,int id)
    {
        Glide.with(c).load(id).into(v);
    }

}