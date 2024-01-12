package com.example.ytstream30;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LocalSongs {

    List<Song> songs = new ArrayList<>();

    LocalSongs()
    {

    }

    public void fetch(final Context context)
    {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DURATION,};
        String selection = MediaStore.Audio.Media.IS_DOWNLOAD + "!=0";

        Cursor cursor = context.getContentResolver().query(uri,projection,selection,null,null);

        if(cursor!=null && cursor.moveToFirst())
        {

            int title_index = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int data_index = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int duration_index = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do
            {
                String title = cursor.getString(title_index);
                String path = cursor.getString(data_index);
                float duration = cursor.getFloat(duration_index);

                Log.e("uruttu_title",title);

                Song song = new Song(path);
                song.setTitle(title);
                song.setDuration(duration);

                songs.add(song);

            } while(cursor.moveToNext());

            cursor.close();
        }
    }
}