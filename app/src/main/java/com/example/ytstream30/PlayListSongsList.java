package com.example.ytstream30;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

public class PlayListSongsList extends AppCompatActivity {

    ListView list;
    PlayListManager manager;

    static final String SELECTED_LIST = "selected_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_songs_list);

        Intent intent = new Intent();
        String playlist_name = intent.getStringExtra(SELECTED_LIST);

        manager = new PlayListManager(this,playlist_name);
        List<MediaSource> sources =  manager.getSources();

        PlaylistSongsAdapter adapter = new PlaylistSongsAdapter(this,sources);
        list = findViewById(R.id.playlists_list);

    }
}