package com.example.ytstream30;

import static com.example.ytstream30.PlaylistSongsAdapter.PLAYLIST;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements Player.Listener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, Runnable {

    TextView title,start,end;
    ImageView thumbnail,backward,forward,pause_or_play;
    SeekBar seek;
    final static String SONG = "song";
    final static String RESTORE = "restore";
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Song song;
    String query;
    static ExoPlayer player;
    Runnable seek_runnable;
    DataRetriever retriever;
    Handler handler = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thumbnail = findViewById(R.id.thumb);
        title = findViewById(R.id.title);
        seek = findViewById(R.id.seek);
        start = findViewById(R.id.start);
        end = findViewById(R.id.end);
        backward = findViewById(R.id.backward);
        forward = findViewById(R.id.forward_btn);
        pause_or_play = findViewById(R.id.play);
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

        seek.setOnSeekBarChangeListener(this);

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forward();
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backward();
            }
        });

        pause_or_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_or_pause();
            }
        });

        // Drawer UI

        LinearLayout playlist = findViewById(R.id.player_playlist);
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PlaylistAct.class));
            }
        });

        Intent intent = getIntent();

        stopSong();

        if(intent.hasExtra(PLAYLIST)) // playlist = true
        {
            String playlist_name = intent.getStringExtra(PLAYLIST);

            PlayListManager manager= new PlayListManager(this,playlist_name);

            Log.e("sanjay_play", manager.getMediaItems().toString());

            player = getPlayer();
            player.setMediaItems(manager.getMediaItems());
            player.addListener(this);
            //player.setRepeatMode();
        }

        if(intent.hasExtra(SONG))
        {
            Glide.with(thumbnail).load(R.drawable.loading).into(thumbnail);

            song = intent.getSerializableExtra(MainActivity.SONG,Song.class);
            startSong();
        }
        else
        {
            load_gif(thumbnail,R.drawable.yt);
        }
    }

    public void startSong()
    {
        Thread sng = new Thread(this);
        sng.start();
    }

    public void forward()
    {
        player.seekTo(player.getCurrentPosition()+ 5000);
    }

    public void backward()
    {
        player.seekTo(player.getCurrentPosition() - 5000);
    }

    public void play_or_pause()
    {
        if(player.isPlaying())
        {
            player.pause();
            pause_or_play.setImageResource(R.drawable.play);
        }
        else
        {
            player.play();
            pause_or_play.setImageResource(R.drawable.pause);
        }
    }

    public ExoPlayer getPlayer()
    {
        if(player==null)
        {
            player = new ExoPlayer.Builder(this).build();
        }
        return player;
    }

    public void stopSong()
    {
        if(player!=null)
        {
            player.pause();
            player.stop();
        }
    }

    public void updateUI()
    {
        seek.setMax((int) song.getDuration());

        String stream_url = song.getStream_url();
        String thumbnail_url = song.getThumbnail_url();
        String title = song.getTitle();
        String duration = song.getDuration_str();

        Log.e("uruttu_duration_str",duration);

        this.end.setText(duration);

        MainActivity.load_gif(thumbnail,thumbnail_url);
        this.title.setText(title);

        player = getPlayer();

        MediaItem item = MediaItem.fromUri(stream_url);
        player.setMediaItem(item);
        player.prepare();
        player.play();

        pause_or_play.setImageResource(R.drawable.pause);

        updateSeek();

        Glide.with(thumbnail).load(Uri.parse(thumbnail_url)).into(thumbnail);
    }

    public void updateSeek()
    {
        seek_runnable = new Runnable() {
            @Override
            public void run() {

                int buffered_position = Math.round(player.getBufferedPosition() / 1000);
                int current_position = Math.round(player.getCurrentPosition() / 1000);

                seek.setProgress(current_position);
                seek.setSecondaryProgress(buffered_position);

                String start = String.format("%2d.%02d",current_position/60,current_position%60);

                MainActivity.this.start.setText(start);

                if(player.getPlaybackState()!=Player.STATE_ENDED) handler.postDelayed(this,1000);
            }
        };

        handler.post(seek_runnable);
    }

    public static void destroyPlayer()
    {
        player.seekTo(0);
        player.pause();
        player.stop();
        player.release();
    }

    public void setNext(String query)
    {
        this.query = query;
        this.song = null;
        player.stop();
    }

    public void setNext(Song song)
    {
        this.song = song;
        this.query = null;
        player.stop();
    }

    @Override
    public void run() {

        if(query!=null)
        {
            retriever = new DataRetriever(this.query);
            retriever.fetch();
            song = retriever.get();
        }
        else
        {
            retriever = new DataRetriever();
            String stream_rl = retriever.getStreamUrl(song);
            song.setStream_url(stream_rl);
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });
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

        int id = item.getItemId();

        if(id==R.id.add)
        {
            if(song==null)
            {
                Toast.makeText(this,"Song not found",Toast.LENGTH_SHORT).show();
                return false;
            }

            Intent intent = new Intent(this, PlaylistAct.class);
            intent.putExtra(SONG,song);
            startActivity(intent);
        }

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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser)
        {
            if(!handler.hasCallbacks(seek_runnable))
            {
                handler.post(seek_runnable);
            }
            player.seekTo(progress*1000);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(player.isPlaying()) player.pause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(!player.isPlaying()) player.play();
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        if(playbackState==Player.STATE_ENDED) player.seekToNextMediaItem();
        Player.Listener.super.onPlaybackStateChanged(playbackState);
    }
}