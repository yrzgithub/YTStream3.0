package com.example.ytstream30;

import static com.example.ytstream30.MainActivity.SONG;

import android.app.Activity;
import android.content.Intent;
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
    static final String PLAYLIST = "playlist_name";
    String playlist_name;

    PlaylistSongsAdapter(Activity act,String playlist_name)
    {
        this.act = act;
        this.playlist_name = playlist_name;

        PlayListManager manager = new PlayListManager(act,playlist_name);
        sources = manager.getSources();
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

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(act,MainActivity.class);
                    intent.putExtra(SONG,source.getSong());
                    intent.putExtra(PLAYLIST,playlist_name);
                    act.startActivity(intent);
                }
            });
        }

        return convertView;
    }

    public void addSource(MediaSource source)
    {
        this.sources.add(source);
        notifyDataSetChanged();
    }
}
