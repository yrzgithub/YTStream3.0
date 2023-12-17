package com.example.ytstream30;

import android.media.browse.MediaBrowser;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.analytics.PlayerId;
import com.google.android.exoplayer2.drm.DrmSessionEventListener;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;

import java.io.IOException;
import java.io.Serializable;

public class MediaSource implements Serializable {

    /*

       file : playlists / name -> List<MediaSource> list;

       // Use with stream url

     */

    boolean yt;
    Song song;
    String local_path;

    MediaSource(Song song)
    {
        this.song = song;
        yt = true;
    }

    MediaSource(String path)
    {
        local_path = path;
        yt = false;
    }

    public MediaItem getSource()
    {
        if(yt)
        {
            return MediaItem.fromUri(song.stream_url);
        }
        else
        {
            return MediaItem.fromUri(local_path);
        }
    }

    public boolean isYt() {
        return yt;
    }

    public void setYt(boolean yt) {
        this.yt = yt;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public String getLocal_path() {
        return local_path;
    }

    public void setLocal_path(String local_path) {
        this.local_path = local_path;
    }
}
