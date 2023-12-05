package com.example.ytstream30;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



class PlaySong extends Thread
{

    DataRetriever retriever;
    String query;
    Activity act;
    ExoPlayer player;
    ImageView thumbnail;

    PlaySong(Activity activity,String query)
    {
        this.query = query;
        this.act = activity;

        // UI

        this.thumbnail = activity.findViewById(R.id.thumb);

        player = new ExoPlayer.Builder(activity).build();
    }

    @Override
    public void run() {
        super.run();

        retriever = new DataRetriever(this.query);
        retriever.fetch();
        Song song = retriever.get();

        String stream_url = song.getStream_url();
        String thumbnail_url = song.getThumbnail_url();

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                MainActivity.load_gif(thumbnail,thumbnail_url);

                MediaItem item = MediaItem.fromUri(stream_url);
                player.setMediaItem(item);
                player.prepare();
                player.play();

                Glide.with(thumbnail).load(Uri.parse(thumbnail_url)).into(thumbnail);
            }
        });
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

    public List<Song> fetch()
    {
        Log.e("uruttu_media_state","Fetching..");

        List<Song> songs = main.callAttr("get_url_data",query).asList().stream().map(Song::new).collect(Collectors.toList());;
        this.songs.addAll(songs);

        Log.e("uruttu_media_state","Fetched..");

        return this.songs;
    }

    public Song get()
    {
        Log.e("uruttu_media_state","Fetching Stream url..");

        Song song = null;

        if(songs.size()>0) {
            song = this.songs.get(0);
            String stream_url = main.callAttr("get_stream_url",song.getYt_url()).toString();
            song.setStream_url(stream_url);
        }

        Log.e("uruttu_media_state","Stream url Fetched..");

        return song;
    }
}



class Song
{
    String yt_url,stream_url,thumbnail_url,title,error,publishedTime,channel,viewCount;
    float duration;

    Song()
    {

    }

    Song(PyObject videoMap)
    {
        Log.e("uruttu_video_data",videoMap.toString());

        Map<PyObject,PyObject> videoData = videoMap.asMap();

        title = videoData.getOrDefault(PyObject.fromJava("title"),null).toJava(String.class);
        yt_url = videoData.getOrDefault(PyObject.fromJava("url"),null).toJava(String.class);
//        publishedTime = videoData.getOrDefault(PyObject.fromJava("publishedTime"),null).toJava(String.class);
        //   duration = Float.parseFloat(videoData.getOrDefault(PyObject.fromJava("duration"),null).toJava(String.class));
        viewCount = videoData.getOrDefault(PyObject.fromJava("viewCount"),null).toJava(String.class);
        thumbnail_url = videoData.getOrDefault(PyObject.fromJava("thumbnail"),null).toJava(String.class);
        channel = videoData.getOrDefault(PyObject.fromJava("channel"),null).toJava(String.class);
        //  error = videoData.getOrDefault(PyObject.fromJava("error"),null).toJava(String.class);
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