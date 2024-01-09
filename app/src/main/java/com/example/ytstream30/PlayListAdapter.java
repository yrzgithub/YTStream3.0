package com.example.ytstream30;

import static android.view.View.GONE;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
            title.setVisibility(View.VISIBLE);
            title.setText(name);

            EditText edit = convertView.findViewById(R.id.edit);
            edit.setText(name);
            edit.setVisibility(GONE);

            edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_DONE)
                    {
                        String new_name = edit.getText().toString();

                        edit.setVisibility(GONE);

                        title.setText(new_name);
                        title.setVisibility(View.VISIBLE);

                        PlayListManager manager = new PlayListManager(activity,name);
                        manager.editPlaylistName(new_name);

                        playlist_names.remove(position);
                        playlist_names.add(position,new_name);
                        notifyDataSetChanged();
                    }

                    return false;
                }
            });

            ImageView pop = convertView.findViewById(R.id.pop);
            pop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(v.getContext(),v,Gravity.BOTTOM);
                    menu.inflate(R.menu.playlist_popmenu);

                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            menu.dismiss();

                            PlayListManager manager = new PlayListManager(activity,name);

                            if(id == R.id.delete)
                            {
                                manager.deletePlayList();
                                removePlaylist(name);
                            }

                            else if (id == R.id.edit)
                            {
                                title.setVisibility(GONE);
                                edit.setVisibility(View.VISIBLE);
                            }

                            return true;
                        }
                    });

                    menu.show();
                }
            });

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

    public void removePlaylist(String name)
    {
        playlist_names.remove(name);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
