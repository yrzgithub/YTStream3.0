package com.example.ytstream30;

import android.app.Activity;
import android.media.browse.MediaBrowser;
import android.util.Log;

import com.google.android.exoplayer2.MediaItem;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayListManager {

    String name;
    DataStorage<List<Song>> storage;


    PlayListManager(Activity act)
    {
        this.storage = new DataStorage<>(act);
    }


    PlayListManager(Activity act, String playList_name)
    {
        this(act);
        this.name = playList_name;
    }

    PlayListManager(Activity act,File file)
    {
        this(act);
        this.name = file.getName();
    }

    public boolean createPlayList(@NotNull List<Song> items)
    {
        return storage.writeObject(name,new ArrayList<>(items));
    }

    public boolean createPlayList(String playlist_name,List<Song> items)
    {
        return storage.writeObject(playlist_name,items);
    }

    public boolean createPlayList(Song source)
    {
        List<Song> sources = new ArrayList<>();
        sources.add(source);
        return storage.writeObject(name,sources);
    }

    public boolean createPlayList(@NotNull Song... sources)
    {
        return createPlayList(Arrays.asList(sources));
    }

    public boolean deletePlayList()
    {
        return storage.delete(name);
    }

    public boolean addToPlayList(@NotNull Song source)
    {
        List<Song> sources = storage.readObject(name);
        if(sources.stream().noneMatch((song)->song.getSourceString().equals(source.getSourceString())))
        {
            sources.add(source);
        }

        return storage.writeObject(name,sources);
    }

    public boolean containsPlaylist(String playlist)
    {
        return getPlaylistNames().contains(playlist);
    }

    public void editPlaylistName(String new_name)
    {


        File file = new File(storage.getPlaylist_dir(),this.name);
        file.renameTo(new File(storage.getPlaylist_dir(),new_name));
    }

    public boolean deleteFromPlaylist(Song song_)
    {
        List<Song> sources = storage.readObject(name);
        sources.remove(song_);

        return storage.writeObject(name,sources);
    }

    public List<String> getPlaylistNames()
    {
        File file = storage.getPlaylist_dir();
        return Arrays.stream(file.list()).collect(Collectors.toList());
    }

    public List<Song> getSources()
    {
        return storage.readObject(name);
    }

    public List<MediaItem> getMediaItems()
    {
        return getSources().stream().map(Song::getSource).collect(Collectors.toList());
    }
}
