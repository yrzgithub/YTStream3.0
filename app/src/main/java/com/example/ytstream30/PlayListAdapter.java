package com.example.ytstream30;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class PlayListAdapter extends BaseAdapter {

    List<String> playlist_names;
    Activity activity;

    PlayListAdapter(Activity activity,List<String> names)
    {
        playlist_names = names;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return playlist_names.size();
    }

    @Override
    public String getItem(int position) {
        return playlist_names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null)
        {
            convertView = activity.getLayoutInflater().inflate(R.layout.custom_playlist,null);

            String name = playlist_names.get(position);

            TextView title = convertView.findViewById(R.id.name);
            title.setText(name);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity,PlayListSongsList.class);
                    intent.putExtra(PlayListSongsList.SELECTED_LIST,name);
                    activity.startActivity(intent);
                }
            });
        }

        return convertView;
    }

    public void addPlaylist(String name)
    {
        playlist_names.add(name);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
