package com.example.ytstream30;

import static com.example.ytstream30.MainActivity.SONG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

public class PlaylistAct extends AppCompatActivity {

    ListView playlist;
    PlayListAdapter adapter;
    PlayListManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        playlist = findViewById(R.id.playlists_list);

        manager = new PlayListManager(this);

        List<String> playlist_names = manager.getPlaylistNames();

        Intent intent = getIntent();

        if(intent.hasExtra(SONG))
        {
            Song sng = (Song) intent.getSerializableExtra(SONG);
            adapter = new PlayListAdapter(PlaylistAct.this,playlist_names,sng);
        }
        else
        {
            adapter = new PlayListAdapter(PlaylistAct.this,playlist_names);
        }

        playlist.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playlist,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.add)
        {
            EditText name = new EditText(this);

            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("Enter the playlist name")
                    .setView(name)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String playlist_name = name.getText().toString();
                            PlayListManager manager = new PlayListManager(PlaylistAct.this,playlist_name);
                            manager.createPlayList();

                            adapter.addPlaylist(playlist_name);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }
}