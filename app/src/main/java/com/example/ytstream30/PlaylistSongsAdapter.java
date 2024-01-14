package com.example.ytstream30;

import static com.example.ytstream30.MainActivity.SONG;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSongsAdapter extends BaseAdapter {

    Activity act;
    List<Song> sources = new ArrayList<>();
    static final String PLAYLIST = "playlist_name";
    static final String SONG_INDEX = "song_index";
    String playlist_name;
    PlayListManager manager;

    PlaylistSongsAdapter(Activity act,String playlist_name)
    {
        this.act = act;
        this.playlist_name = playlist_name;

        manager = new PlayListManager(act,playlist_name);
        sources.addAll(manager.getSources());
    }

    @Override
    public int getCount() {
        return sources.size();
    }

    @Override
    public Song getItem(int position) {
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

            Song source = sources.get(position);
            String title_ = source.getTitle();

            TextView title = convertView.findViewById(R.id.title);
            ImageView sourceImg = convertView.findViewById(R.id.source);
            ImageView popImage = convertView.findViewById(R.id.pop);

            title.setText(title_);

            if(source.isYt())
            {
                Glide.with(sourceImg).load(R.drawable.youtube).into(sourceImg);
            }

            popImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(act,v, Gravity.BOTTOM);
                    menu.inflate(R.menu.playlist_song_menu);

                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();

                            if(id == R.id.delete)
                            {
                                manager.deleteFromPlaylist(source);
                                sources.remove(position);
                                notifyDataSetChanged();
                            }

                            return false;
                        }
                    });

                    menu.show();
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(act,MainActivity.class);
                    intent.putExtra(SONG,source);
                    intent.putExtra(SONG_INDEX,position);
                    intent.putExtra(PLAYLIST,playlist_name);
                    act.startActivity(intent);
                }
            });
        }

        return convertView;
    }

    public void addSource(Song source)
    {
        this.sources.add(source);
        notifyDataSetChanged();
    }

    public void removeSource(Song song)
    {
        this.sources.remove(song);
        notifyDataSetChanged();
    }
}
