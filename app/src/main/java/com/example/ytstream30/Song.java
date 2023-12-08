package com.example.ytstream30;

import com.chaquo.python.PyObject;

import java.io.Serializable;
import java.util.Map;

public class Song implements Serializable
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
