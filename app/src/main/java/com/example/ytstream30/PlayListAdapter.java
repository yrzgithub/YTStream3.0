package com.example.ytstream30;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class PlayListAdapter extends BaseAdapter {

    List<String> playlist_names;
    Activity activity;
    boolean add;
    Song song;

    PlayListAdapter(Activity activity,List<String> names)
    {
        playlist_names = names;
        this.activity = activity;
    }

    PlayListAdapter(Activity act,List<String> names,Song sng)
    {
        this(act,names);
        this.add = true;
        this.song = sng;
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

            if(add)
            {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String playlist =  playlist_names.get(position);
                        PlayListManager manager = new PlayListManager(activity,playlist);
                        manager.addToPlayList(song);

                        Toast.makeText(activity,"Added",Toast.LENGTH_LONG).show();
                    }
                });
            }
            else
            {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(activity, PlayListSongsListAct.class);
                        intent.putExtra(PlayListSongsListAct.SELECTED_LIST,name);
                        activity.startActivity(intent);

                    }
                });
            }
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
