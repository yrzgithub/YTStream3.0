package com.example.ytstream30;

import static com.example.ytstream30.PlaylistSongsAdapter.PLAYLIST;
import static com.example.ytstream30.Song.LOCAL;
import static com.example.ytstream30.Song.SONG_TYPE;
import static com.google.common.io.Resources.getResource;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import java.io.IOException;
import java.security.Permission;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements Player.Listener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, Runnable {

    TextView title,start,end;
    ImageView thumbnail,backward,forward,pause_or_play;
    SeekBar seek;
    final static String SONG = "song";
    final static String RESTORE = "restore";
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    String query;
    static ExoPlayer player;
    Runnable seek_runnable;
    DataRetriever retriever;
    MenuItem add_menu;
    LinearLayout playlist,local,search,downloads,settings,update_or_about,developer_contact;
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

        forward.setOnClickListener(this);
        backward.setOnClickListener(this);
        pause_or_play.setOnClickListener(this);

        toggle = new ActionBarDrawerToggle(this,drawer,R.string.open,R.string.close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Marquee
        marquee(this,title);

        seek.setOnSeekBarChangeListener(this);

        // Drawer UI

        playlist = findViewById(R.id.player_playlist);
        local = findViewById(R.id.local);
        search = findViewById(R.id.search_list);
        downloads = findViewById(R.id.downloads);
        settings = findViewById(R.id.settings);
        update_or_about = findViewById(R.id.update_or_about);
        developer_contact = findViewById(R.id.developer_contact);

        playlist.setOnClickListener(this);
        local.setOnClickListener(this);
        search.setOnClickListener(this);
        downloads.setOnClickListener(this);
        settings.setOnClickListener(this);
        update_or_about.setOnClickListener(this);
        developer_contact.setOnClickListener(this);

        player = getPlayer();

        Intent intent = getIntent();

        stopSong();

        if(intent.hasExtra(PLAYLIST)) // playlist = true
        {
            String playlist_name = intent.getStringExtra(PLAYLIST);

            PlayListManager manager= new PlayListManager(this,playlist_name);

            player = getPlayer();
            player.setMediaItems(manager.getMediaItems());
            player.addListener(this);
        }

        if(intent.hasExtra(SONG))
        {
            Glide.with(thumbnail).load(R.drawable.loading).into(thumbnail);

            Song song = intent.getSerializableExtra(MainActivity.SONG,Song.class);
            Song.setCurrentSong(song);
            startSong();
        }
        else
        {
            if(Song.getCurrentSong()==null)  load_gif(thumbnail,R.drawable.yt);
            else updateUI();
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
        Song song = Song.getCurrentSong();

        String stream_url = song.getStream_url();
        String title = song.getTitle();
        String duration = song.getDuration_str();

        String thumbnail_url = song.getThumbnail_url();

        this.end.setText(duration);
        this.title.setText(title);

        if(song.isYt())
        {
            MainActivity.load_gif(thumbnail,thumbnail_url);
        }
        else
        {
            MainActivity.load_gif(thumbnail,R.drawable.local_song_playing);
        }

        if(add_menu!=null) add_menu.setEnabled(true);

        pause_or_play.setImageResource(R.drawable.pause);

        updateSeek((int) song.getDuration());

        play(song);
    }

    public void play(Song song)
    {
        player = getPlayer();

        MediaItem item = song.getSource();
        player.setMediaItem(item);
        player.prepare();
        player.play();
    }

    public void updateSeek(int duration)
    {
        seek.setMax(duration);

        seek_runnable = new Runnable() {
            @Override
            public void run() {

                int buffered_position = Math.round(player.getBufferedPosition() / 1000);
                int current_position = Math.round(player.getCurrentPosition() / 1000);

                seek.setProgress(current_position);
                seek.setSecondaryProgress(buffered_position);

                String start = String.format("%d:%02d",current_position/60,current_position%60);

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
        Song.setCurrentSong(null);
        this.query = query;
        player.stop();
    }

    public void setNext(Song song)
    {
        Song.setCurrentSong(song);
        this.query = null;
        player.stop();
    }

    @Override
    public void run() {

        Song current = Song.getCurrentSong();

        if(query!=null)
        {
            retriever = new DataRetriever(this.query);
            retriever.fetch();
            Song song = retriever.get();
            Song.setCurrentSong(song);
        }
        else if(current.isYt())
        {
            retriever = new DataRetriever();
            String stream_rl = retriever.getStreamUrl(current);
            current.setStream_url(stream_rl);
            current.download();
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

        add_menu = menu.findItem(R.id.add);
        add_menu.setEnabled(Song.getCurrentSong()!=null && !Song.getCurrentSong().isYt());

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
            Intent intent = new Intent(this, PlaylistAct.class);
            intent.putExtra(SONG,Song.getCurrentSong());
            startActivity(intent);
        }
        else if(id == R.id.download)
        {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == playlist.getId())
        {
            startActivity(new Intent(MainActivity.this,PlaylistAct.class));
        }
        else if (id == local.getId())
        {
            Intent intent = new Intent(MainActivity.this, SearchResultsAct.class);
            intent.putExtra(SONG_TYPE,LOCAL);
            startActivity(intent);
        }
        else if (id == search.getId())
        {
            getOnBackPressedDispatcher().onBackPressed();
        }
        else if (id == downloads.getId())
        {

        }
        else if (id == settings.getId())
        {

        }
        else if (id == update_or_about.getId())
        {

        }
        else if (id == forward.getId())
        {
            forward();
        }
        else if (id == backward.getId())
        {
            backward();
        }
        else if (id == pause_or_play.getId())
        {
            play_or_pause();
        }

    }

    public static void load_gif(ImageView v,int id)
    {
        Glide.with(v).load(id).into(v);
    }

    public static void load_gif(ImageView v,String url)
    {
        if(v.getContext()!=null) Glide.with(v).load(url).into(v);
    }

    public static void marquee(Context context, TextView view)
    {
        view.post(new Runnable() {
            @Override
            public void run() {
                String title_ = view.getText().toString();
                int len = title_.length();

                int sizeInPx = (int) view.getTextSize() * len;
                int widthInPx = view.getWidth();

                float threshold = 1.5F;

                if(sizeInPx * threshold > widthInPx)  view.setSelected(true);
            }
        });
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