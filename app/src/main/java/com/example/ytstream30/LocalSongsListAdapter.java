package com.example.ytstream30;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class LocalSongsListAdapter extends BaseAdapter {

    List<Song> songs = new ArrayList<>();

    LocalSongsListAdapter(List<Song> songs)
    {
        this.songs.addAll(songs);
    }

    @Override
    public int getCount() {
        return this.songs.size();
    }

    @Override
    public Song getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null)
        {

        }

        return convertView;
    }
}
