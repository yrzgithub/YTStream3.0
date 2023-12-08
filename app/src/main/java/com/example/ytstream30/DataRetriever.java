package com.example.ytstream30;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.util.ArrayList;
import java.util.List;
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
