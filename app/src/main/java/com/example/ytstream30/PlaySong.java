package com.example.ytstream30;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.google.android.exoplayer2.BasePlayer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;

import java.io.IOException;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



class PlaySong extends Thread implements SeekBar.OnSeekBarChangeListener,Player.Listener
{

    DataRetriever retriever;
    String query;
    TextView title,start,end;
    Activity act;
    ExoPlayer player;
    ImageView thumbnail;
    Runnable seek_runnable;
    SeekBar seek;
    ImageButton backward,forward,pause_or_play;
    Song song;
    Handler handler = new Handler();

    PlaySong(Activity activity,String query)
    {
        this.query = query;
        this.act = activity;

        // UI

        thumbnail = activity.findViewById(R.id.thumb);
        title = act.findViewById(R.id.title);
        seek = activity.findViewById(R.id.seek);
        start = activity.findViewById(R.id.start);
        end = activity.findViewById(R.id.end);
        backward = activity.findViewById(R.id.backward);
        forward = activity.findViewById(R.id.forward_btn);
        pause_or_play = activity.findViewById(R.id.play);

        seek.setOnSeekBarChangeListener(this);

        player = new ExoPlayer.Builder(activity).build();

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
    }

    PlaySong(Activity act,Song song)
    {
        this(act,(String) null);
        this.song = song;
    }

    @Override
    public void run() {
        super.run();

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
                updateUI(song);
            }
        });
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        switch (playbackState)
        {
            case Player.STATE_BUFFERING:
                Glide.with(pause_or_play).load(R.drawable.loading_pink_list).into(pause_or_play);
                break;
        }
        Player.Listener.super.onPlaybackStateChanged(playbackState);
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

    public void updateUI(Song song)
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

        MediaItem item = MediaItem.fromUri(stream_url);
        player.setMediaItem(item);
        player.prepare();
        player.play();

        pause_or_play.setImageResource(R.drawable.pause);

        updateSeek();

        Glide.with(thumbnail).load(Uri.parse(thumbnail_url)).into(thumbnail);
    }

    public void destroyPlayer()
    {
        player.seekTo(0);
        handler.removeCallbacks(seek_runnable);
        player.pause();
        player.stop();
        player.release();
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

                PlaySong.this.start.setText(start);

                if(player.getPlaybackState()!=Player.STATE_ENDED) handler.postDelayed(this,1000);
            }
        };

        handler.post(seek_runnable);
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
}