package com.example.ytstream30;

import android.media.browse.MediaBrowser;
import android.provider.MediaStore;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

public class MediaSource {

    /*

       file : playlists / name -> List<MediaSource> list;

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
