package com.example.ytstream30;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

public class PlaylistAct extends AppCompatActivity {

    ListView playlist;
    PlayListAdapter adapter;
    PlayListManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        manager = new PlayListManager(this);
        adapter = new PlayListAdapter(PlaylistAct.this,manager.getPlaylistNames());

        playlist = findViewById(R.id.playlists_list);
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