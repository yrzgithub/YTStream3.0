package com.example.ytstream30;

import android.app.Activity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayListManager {

    File file;
    String name;
    DataStorage<List<MediaSource>> storage;


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

    public boolean createPlayList(@NotNull List<MediaSource> items)
    {
        return storage.writeObject(name,new ArrayList<>(items));
    }

    public boolean createPlayList(MediaSource source)
    {
        List<MediaSource> sources = new ArrayList<>();
        sources.add(source);
        return storage.writeObject(name,sources);
    }

    public boolean createPlayList(@NotNull MediaSource... sources)
    {
        return createPlayList(Arrays.asList(sources));
    }

    public boolean deletePlayList()
    {
        return storage.delete(name);
    }

    public boolean addToPlayList(MediaSource source)
    {
        List<MediaSource> sources = storage.readObject(name);
        sources.add(source);
        return storage.writeObject(name,sources);
    }

    public boolean deleteFromPlaylist(Song song_)
    {
        List<MediaSource> sources = storage.readObject(name);
        sources.remove(new MediaSource(song_));
        return storage.writeObject(name,sources);
    }

    public List<List<MediaSource>> getPlaylists()
    {
        File file = storage.getPlaylist_dir();
        File[] playlists = file.listFiles();

        return Arrays.stream(playlists).map(source->storage.readObject(source)).collect(Collectors.toList());
    }

    public List<String> getPlaylistNames()
    {
        File file = storage.getPlaylist_dir();
        return Arrays.stream(file.list()).collect(Collectors.toList());
    }
}
