package com.example.ytstream30;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

public class PlayListSongsListAct extends AppCompatActivity {

    ListView list;
    PlayListManager manager;

    static final String SELECTED_LIST = "SELECTED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_songs_list);

        Intent intent = getIntent();

        String playlist_name = intent.getStringExtra(SELECTED_LIST);

        manager = new PlayListManager(this,playlist_name);
        List<MediaSource> sources =  manager.getSources();

        PlaylistSongsAdapter adapter = new PlaylistSongsAdapter(this,sources);

        list = findViewById(R.id.playlists_list);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playlist_song,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.add)
        {

        }
        return super.onOptionsItemSelected(item);
    }
}