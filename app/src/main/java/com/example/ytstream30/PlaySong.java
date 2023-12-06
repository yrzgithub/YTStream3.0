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


class DataRetriever
{

    List<Song> songs;
    PyObject main = Python.getInstance().getModule("main");
    String query;

    DataRetriever(String query)
    {
        songs = new ArrayList<>();
        this.query = query;
    }

    DataRetriever()
    {

    }

    public List<Song> fetch()
    {
       // Log.e("uruttu_media_state","Fetching..");

        List<Song> songs = main.callAttr("get_url_data",query).asList().stream().map(Song::new).collect(Collectors.toList());;
        this.songs.addAll(songs);

       // Log.e("uruttu_media_state","Fetched..");

        return this.songs;
    }

    public Song get()
    {
      //  Log.e("uruttu_media_state","Fetching Stream url..");

        Song song = null;

        if(songs.size()>0) {
            song = this.songs.get(0);
            String stream_url = main.callAttr("get_stream_url",song.getYt_url()).toString();
            song.setStream_url(stream_url);
        }

       // Log.e("uruttu_media_state","Stream url Fetched..");

        return song;
    }

    public String getStreamUrl(Song song)
    {
        return main.callAttr("get_stream_url",song.getYt_url()).toString();
    }

    public List<String> getTitlesList()
    {
        List<Song> songs = fetch();
        return songs.stream().map(Song::getTitle).collect(Collectors.toList());
    }

    public String[] getTitles()
    {
        List<Song> songs = fetch();
        return songs.stream().map(Song::getTitle).toArray(String[]::new);
    }
}



class Song implements Serializable
{
    String yt_url,stream_url,thumbnail_url,title,error,publishedTime,channel,viewCount,duration_str,channel_url;
    float duration = 1;

    Song()
    {

    }

    Song(PyObject videoMap)
    {
       // Log.e("uruttu_video_data",videoMap.toString());

        Map<PyObject,PyObject> videoData = videoMap.asMap();

        title = videoData.getOrDefault(PyObject.fromJava("title"),null).toJava(String.class);
        yt_url = videoData.getOrDefault(PyObject.fromJava("url"),null).toJava(String.class);
        thumbnail_url = videoData.getOrDefault(PyObject.fromJava("thumbnail"),null).toJava(String.class);
        channel = videoData.getOrDefault(PyObject.fromJava("channel"),null).toJava(String.class);
        channel_url = videoData.getOrDefault(PyObject.fromJava("channel_url"),null).toJava(String.class);


        try
        {
            publishedTime = videoData.getOrDefault(PyObject.fromJava("publishedTime"),null).toJava(String.class);
        }
        catch (NullPointerException e)
        {
            publishedTime = null;
        }

        try {
            viewCount = videoData.getOrDefault(PyObject.fromJava("viewCount"),null).toJava(String.class);
        }
        catch (NullPointerException e)
        {
            viewCount = null;
        }

        try {
            duration_str = videoData.getOrDefault(PyObject.fromJava("duration"),null).toJava(String.class);
            duration = durationConvert(duration_str);
        }

        catch (NullPointerException e)
        {
            duration_str = null;
            duration = 1;
        }

    }

    public Song(String yt_url, String stream_url, String thumbnail_url, String title, String error, String publishedTime, String channel, String viewCount, float duration) {
        this.yt_url = yt_url;
        this.stream_url = stream_url;
        this.thumbnail_url = thumbnail_url;
        this.title = title;
        this.error = error;
        this.publishedTime = publishedTime;
        this.channel = channel;
        this.viewCount = viewCount;
        this.duration = duration;
    }

    public String getChannel_url() {
        return channel_url;
    }

    public void setChannel_url(String channel_url) {
        this.channel_url = channel_url;
    }

    public String getDuration_str() {
        return duration_str;
    }

    public void setDuration_str(String duration_str) {
        this.duration_str = duration_str;
    }

    public float durationConvert(String str)
    {
        String[] duration_string = str.split(":");
        int duration_string_length = duration_string.length;

        float duration = 0;

        for(int index=0;index<duration_string_length;++index)
        {
            duration += Math.pow(60,duration_string_length-index-1) * Float.parseFloat(duration_string[index]);
            //Log.e("uruttu__duration", String.valueOf(Math.pow(60,index)));
        }

        return duration;
    }

    public String getYt_url() {
        return yt_url;
    }

    public void setYt_url(String yt_url) {
        this.yt_url = yt_url;
    }

    public String getStream_url() {
        return stream_url;
    }

    public void setStream_url(String stream_url) {
        this.stream_url = stream_url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPublishedTime() {
        return publishedTime;
    }

    public void setPublishedTime(String publishedTime) {
        this.publishedTime = publishedTime;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }
}