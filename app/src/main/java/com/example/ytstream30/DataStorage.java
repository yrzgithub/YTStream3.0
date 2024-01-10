package com.example.ytstream30;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.android.exoplayer2.source.MediaSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataStorage<T> {

    File app_directory, playlist_dir;

    DataStorage(Activity activity)
    {
        app_directory = new File(activity.getFilesDir(),"AppData");
        playlist_dir = new File(activity.getFilesDir(),"Playlists");
        createDir(app_directory);
        createDir(playlist_dir);
    }

    public boolean writeObject(String name,T object) {
        File output_file = new File(playlist_dir,name);

        try
        {
            FileOutputStream inputFileStream = new FileOutputStream(output_file);
            ObjectOutputStream objectStream = new ObjectOutputStream(inputFileStream);
            objectStream.writeObject(object);

            inputFileStream.close();
            objectStream.close();
        }
        catch (Exception e)
        {
            Log.e("uruttu_object",e.getMessage());
            return false;
        }

        return true;
    }

    public T readObject(File file)
    {

        Log.e("uruttu_file",String.valueOf(file.exists()));

        try
        {
            assert file.exists();

            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            T object = (T) objectInputStream.readObject();

            fileInputStream.close();
            objectInputStream.close();

            return object;
        }
        catch (Exception e)
        {

            return null;
        }
    }

    public T readObject(String name)
    {
        File file = new File(playlist_dir,name);
        return readObject(file);
    }

    public File getApp_directory() {
        return app_directory;
    }

    public void setApp_directory(File app_directory) {
        this.app_directory = app_directory;
    }

    public File getPlaylist_dir() {
        return playlist_dir;
    }

    public void setPlaylist_dir(File playlist_dir) {
        this.playlist_dir = playlist_dir;
    }

    public void createDir(File dir) {

        if(dir.exists())
        {
            Log.e("uruttu_dir", dir.getAbsolutePath());
            return;
        }

        boolean dir_created = dir.mkdirs();

        if(dir_created)
        {
            Log.e("uruttu_dir","Dir Created");
        }
        else
        {
            Log.e("uruttu_dir","Dir not Created");
        }
    }

    public boolean delete(String name)
    {
        File file = new File(playlist_dir,name);
        return file.delete();
    }
}
