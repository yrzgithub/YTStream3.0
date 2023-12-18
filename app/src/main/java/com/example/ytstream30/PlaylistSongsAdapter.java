package com.example.ytstream30;

import android.app.Activity;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class PlaylistSongsAdapter extends BaseAdapter {

    Activity act;
    List<MediaSource> sources;

    PlaylistSongsAdapter(Activity act, List<MediaSource> sources)
    {
        this.act = act;
        this.sources = sources;
    }

    @Override
    public int getCount() {
        return sources.size();
    }

    @Override
    public MediaSource getItem(int position) {
        return sources.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null)
        {
            convertView = act.getLayoutInflater().inflate(R.layout.custom_playlist_song,null);

            MediaSource source = sources.get(position);
            String title_ = source.getTitle();

            TextView title = convertView.findViewById(R.id.title);
            title.setText(title_);
        }

        return convertView;
    }

    public void addSource(MediaSource source)
    {

    }
}
