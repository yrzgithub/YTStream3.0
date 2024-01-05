package com.example.ytstream30;

import android.app.Activity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PlaylistPlayer extends Thread {

    PlayListManager manager;
    ExoPlayer player = MainActivity.player;
    List<Song> songs;
    int index = 0;

    PlaylistPlayer(Activity act,String name,int index)
    {
        manager = new PlayListManager(act,name);
        songs = manager.getSources();
        this.index = index;
    }

    @Override
    public void run() {
        DataRetriever retriever = new DataRetriever();
        for(Song song : songs)
        {
            retriever.putStreamUrl(song);
        }
        player.setMediaItems(songs.stream().map(Song::getSource).collect(Collectors.toList()));
        super.run();
    }

    public Song next()
    {
        if(index == songs.size()) index = 0;

        Song song = songs.get(index);
        ++index;
        return song;
    }

    public void setListener(Player.Listener listener)
    {
        player.addListener(listener);
    }
}
