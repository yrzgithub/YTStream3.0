package com.example.ytstream30;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;



public class PlaySong extends MediaPlayer implements Runnable, MediaPlayer.OnPreparedListener,MediaPlayer.OnBufferingUpdateListener {

    String query;
    List<Song> songs;
    PyObject main;
    Song current_song;
    Context context;

    PlaySong(Context context,String query) // YT Stream
    {
        songs = new ArrayList<>();

        this.query = query;
        this.context = context;



        setOnPreparedListener(this);
        setOnBufferingUpdateListener(this);
        setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    PlaySong() // for local songs
    {

    }

    public void fetch()
    {
        Log.e("uruttu_media_state","Fetching..");

        List<Song> songs = main.callAttr("get_url_data",query).asList().stream().map(Song::new).collect(Collectors.toList());;
        this.songs.addAll(songs);

        Log.e("uruttu_media_state","Fetched..");
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

        Log.e("uruttu_media_state","Fetched");

        return song;
    }

    @Override
    public void run() {

        Log.e("uruttu_media","Thread Running..");

        main = Python.getInstance().getModule("main");

        fetch();
        current_song = get();

        try
        {
            String stream_url = current_song.getStream_url();

            Log.e("uruttu_url_stream",stream_url);

            setDataSource(stream_url);
            prepareAsync();
           // prepare();
            //start();
        }
        catch (Exception e)
        {
            Log.e("uruttu_error",e.getMessage());
        }
    }

    @Override
    public void setDataSource(@NonNull Context context, @NonNull Uri uri) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(context, uri);
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
    }

    @Override
    public void prepare() throws IOException, IllegalStateException {
        super.prepare();
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        super.prepareAsync();
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        release();
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
    }

    @Override
    public void setWakeMode(Context context, int mode) {
        super.setWakeMode(context, mode);
    }

    @Override
    public boolean isPlaying() {
        return super.isPlaying();
    }

    @Override
    public void seekTo(int msec) throws IllegalStateException {
        super.seekTo(msec);
    }

    @Override
    public int getCurrentPosition() {
        return super.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return super.getDuration();
    }

    @Override
    public void release() {
        super.release();
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public void setLooping(boolean looping) {
        super.setLooping(looping);
    }

    @Override
    public boolean isLooping() {
        return super.isLooping();
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        super.setOnPreparedListener(listener);
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        super.setOnCompletionListener(listener);
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        super.setOnBufferingUpdateListener(listener);
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        super.setOnSeekCompleteListener(listener);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
         start();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.e("uruttu_buffer",String.valueOf(percent));
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
