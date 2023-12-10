package com.example.ytstream30;

import java.io.File;

public class PlayListManager {

    File file;
    String name;

    PlayListManager(String playList_name)
    {
        this.name = playList_name;
    }

    PlayListManager(File file)
    {
        this.name = file.getName();
    }

    public boolean addPlayList()
    {

    }

    public boolean deletePlayList()
    {

    }

}
