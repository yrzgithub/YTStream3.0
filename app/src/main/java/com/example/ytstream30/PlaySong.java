package com.example.ytstream30;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.google.android.exoplayer2.MediaItem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        float start = System.currentTimeMillis();

        try
        {
            songs.addAll(main.callAttr("get_url_data",query).asList().stream().map(Song::new).collect(Collectors.toList()));
        }
        catch(Error | Exception e)
        {
            Log.e("uruttu_playsong_fetch",e.getMessage());
        }

        float end = System.currentTimeMillis();

        Log.e("uruttu_fetch_time_fetch",end + " " + start);

        return this.songs;
    }

    public Song get()
    {

        float start = System.currentTimeMillis();

        Song song = null;

        if(songs.size()>0) {
            song = this.songs.get(0);
            String stream_url = getStreamUrl(song);
            song.setStream_url(stream_url);
        }

        float end = System.currentTimeMillis();

        Log.e("uruttu_fetch_time_get",String.valueOf((end-start)/1000));

        return song;
    }

    public String getStreamUrl(Song song)
    {
        String stream_url = null;

        float start = System.currentTimeMillis();

        stream_url =  main.callAttr("get_stream_url",song.getYt_url()).toString();

        float end = System.currentTimeMillis();

        // Log.e("uruttu_fetch_stream_url",String.valueOf(end-start));

        Log.e("uruttu_fetch_time_fetch",end + " " + start);

        return stream_url;
    }

    public Song putStreamUrl(Song song)
    {
        song.setStream_url(getStreamUrl(song));
        return song;
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



class Song extends Thread implements Serializable
{
    String yt_url,stream_url,thumbnail_url,title,error,publishedTime,channel,viewCount,duration_str,channel_url;
    float duration = 1;
    boolean yt = true;
    String local_path;
    static Song current_song;
    int progressPercent = 0;
    boolean err = false,download_started = false;
    final static String SONG_TYPE = "song_type";
    final static String LOCAL = "local";
    final static String YT = "yt";

    // Channel == Artist

    Song()
    {

    }

    Song(String local_path)
    {
        this.local_path = local_path;
        yt = false;
    }

    Song(PyObject videoMap)
    {
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

        yt = true;
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

        yt = true;
    }

    public MediaItem getSource()
    {
        if(yt)
        {
            return MediaItem.fromUri(stream_url);
        }
        else
        {
            return MediaItem.fromUri(local_path);
        }
    }

    @Override
    public void run() {
        startDownload();
        super.run();
    }

    public void download(Context context)
    {
        if(download_started || isDownloading())
        {
            return;
        }

        Toast.makeText(context,"Downloading",Toast.LENGTH_SHORT).show();
        DownloadsAdapter.addSong(this);
        start();
    }

    public boolean isDownloadable()
    {
        return stream_url!=null;
    }

    private void startDownload()
    {
        try
        {
            assert isYt();
            assert stream_url!=null;

            download_started = true;

            // Input

            URL url = new URL(stream_url);
            URLConnection connection = url.openConnection();
            InputStream ipStream = connection.getInputStream();
            BufferedInputStream bufferStream = new BufferedInputStream(ipStream);

            // Output

            String file_name = title + " - YTStream.mp3";
            file_name = file_name.replace("|","");
            String downloadDirPath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DOWNLOADS;

            File file = new File(downloadDirPath,file_name);
            FileOutputStream fOutputStream = new FileOutputStream(file);

            // write

            final int BUFFER_LENGTH = 1024;  // 1 MB

            int total_length = connection.getContentLength();
            byte buffer[] = new byte[BUFFER_LENGTH];

            int readLength = 0;
            int totalReadLength = 0;

            while((readLength = bufferStream.read(buffer)) != -1)            // EOF = -1
            {

                fOutputStream.write(buffer);
                totalReadLength += readLength;
                progressPercent = Math.round((totalReadLength * 100)/total_length);

                Log.e("download_progress",String.valueOf(progressPercent));
            }

            bufferStream.close();
            ipStream.close();
            fOutputStream.close();
        }

        catch (Error | Exception e)
        {
            err = true;
            Log.e("download_progress",e.getMessage());
        }
    }

    public boolean isError()
    {
        return err;
    }

    public int getProgressPercent()
    {
        return progressPercent;
    }

    public boolean isDownloading()
    {
        return download_started && progressPercent<100;
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    public static Song getCurrentSong()
    {
        return current_song;
    }

    public static void setCurrentSong(Song current)
    {
        current_song = current;
    }

    public String getSourceString()
    {
        return yt?getStream_url():getLocal_path();
    }

    public String getTitle()
    {
        if(yt)
        {
            return title;
        }
        else
        {
            File file = new File(local_path);
            return file.getName();
        }
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
        }

        return duration;
    }

    public boolean isYt() {
        return yt;
    }

    public void setYt(boolean yt) {
        this.yt = yt;
    }

    public String getLocal_path() {
        return local_path;
    }

    public void setLocal_path(String local_path) {
        this.local_path = local_path;
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
        if(!isYt())
        {
            duration /= 1000;
            duration_str = String.format("%d:%02d",(int) Math.round(duration/60),(int) Math.round(duration%60));
        }
        this.duration = duration;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if(!(obj instanceof Song)) return false;

        Song song = (Song) obj;

        return song.getYt_url().equals(getYt_url());
    }
}