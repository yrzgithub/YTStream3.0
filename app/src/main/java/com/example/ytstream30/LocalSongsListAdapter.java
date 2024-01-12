package com.example.ytstream30;

import static com.example.ytstream30.MainActivity.SONG;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LocalSongsListAdapter extends BaseAdapter {

    List<Song> songs = new ArrayList<>();
    Context context;

    LocalSongsListAdapter(Context context)
    {
        this.context = context;

        LocalSongs local = new LocalSongs();
        songs.addAll(local.fetch(context));
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
            convertView = LayoutInflater.from(context).inflate(R.layout.local_songs_adapter,null);

            Song song = songs.get(position);

            String title_ = song.getTitle();
            String artist_ = song.getChannel();

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,MainActivity.class);
                    intent.putExtra(SONG,song);
                    context.startActivity(intent);
                }
            });

            TextView title = convertView.findViewById(R.id.title_local_songs);
            TextView artist = convertView.findViewById(R.id.artist);

            title.setText(title_);
            artist.setText(artist_);
        }

        return convertView;
    }
}
