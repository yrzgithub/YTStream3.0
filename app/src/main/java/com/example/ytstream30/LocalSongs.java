package com.example.ytstream30;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.loader.ResourcesProvider;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import androidx.core.content.res.ResourcesCompat;

import com.google.common.io.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalSongs {

    List<Song> songs = new ArrayList<>();

    LocalSongs()
    {

    }

    public List<Song> fetch(final Context context)
    {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DURATION,};
        String selection = MediaStore.Audio.Media.IS_DOWNLOAD + "!=0";

        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(uri,projection,selection,null,null);

        if(cursor!=null && cursor.moveToFirst())
        {

            int title_index = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int data_index = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int duration_index = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int artist_index = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do
            {
                String title = cursor.getString(title_index);
                String path = cursor.getString(data_index);
                String artist = cursor.getString(artist_index);
                float duration = cursor.getFloat(duration_index);

                if(artist.equals("<unknown>")) artist = "";

                Song song = new Song(path);
                song.setTitle(title);
                song.setDuration(duration);
                song.setChannel(artist);

                songs.add(song);

            } while(cursor.moveToNext());

            cursor.close();
        }

        return songs;
    }
}
