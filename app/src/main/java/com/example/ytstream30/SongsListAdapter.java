package com.example.ytstream30;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import org.w3c.dom.Text;

import java.util.List;

public class SongsListAdapter extends BaseAdapter {

    List<Song> songs;
    LayoutInflater inflater;
    Activity act;

    SongsListAdapter(Activity act,List<Song> songs)
    {
        this.songs = songs;
        inflater = act.getLayoutInflater();
        this.act = act;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
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
            convertView = inflater.inflate(R.layout.list_view_result,null);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(act,MainActivity.class);
                    intent.putExtra("song",songs.get(position));
                    act.startActivity(intent);
                }
            });

            ImageView thumbnail = convertView.findViewById(R.id.thumb_search);

            ShapeableImageView uploader = convertView.findViewById(R.id.uploader_icon);

            TextView title_view = convertView.findViewById(R.id.title_search);
            TextView channel_name = convertView.findViewById(R.id.channel);
            TextView views = convertView.findViewById(R.id.views);
            TextView time = convertView.findViewById(R.id.time);

            Song song = songs.get(position);

            String thumb_url = song.getThumbnail_url();
            String uploader_url = song.getChannel_url();
            String title = song.getTitle();
            String channel_str = song.getChannel();
            String view_str = song.getViewCount();
            String time_str = song.getPublishedTime();

            Glide.with(thumbnail).load(thumb_url).into(thumbnail);
            Glide.with(uploader).load(uploader_url).into(uploader);

            title_view.setText(title);
            channel_name.setText(channel_str);
            views.setText(view_str);
            time.setText(time_str);
        }

        return convertView;
    }
}
