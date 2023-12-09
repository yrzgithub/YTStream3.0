package com.example.ytstream30;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView title;
    ImageView thumbnail,backward,forward,pause_or_play;
    SeekBar bar;
    PlaySong player;
    final static String SONG = "song";
    final static String RESTORE = "restore";
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;

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
        drawer = findViewById(R.id.drawer);

        toggle = new ActionBarDrawerToggle(this,drawer,R.string.open,R.string.close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Marquee
        title.post(new Runnable() {
            @Override
            public void run() {
                title.setSelected(true);
            }
        });

        Intent intent = getIntent();

        if(intent.hasExtra(SONG))
        {
            Glide.with(thumbnail).load(R.drawable.loading).into(thumbnail);

            Song song = intent.getSerializableExtra(MainActivity.SONG,Song.class);

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
        getMenuInflater().inflate(R.menu.main_act_menu,menu);

        ShowSuggestions suggestions = new ShowSuggestions(this,menu);
        suggestions.setPlayer(player);

        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        AutoCompleteTextView auto = (AutoCompleteTextView) search.findViewById(androidx.appcompat.R.id.search_src_text);

        search.setOnQueryTextListener(suggestions);
        auto.setOnItemClickListener(suggestions);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
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
        if(v.getContext()!=null) Glide.with(v).load(url).into(v);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        outState = new Bundle();
        outState.putSerializable(RESTORE,player);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

        PlaySong player = (PlaySong) savedInstanceState.getSerializable(RESTORE);
        if(player!=null)  player.updateUI();

        super.onRestoreInstanceState(savedInstanceState);
    }
}