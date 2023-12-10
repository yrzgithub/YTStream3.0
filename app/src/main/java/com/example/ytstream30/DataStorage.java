package com.example.ytstream30;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class DataStorage {

    File file;

    DataStorage(Activity activity)
    {
        file = activity.getFilesDir();
    }

    public void createDir() {
        if(file.exists())
        {
            try
            {
                Log.e("uruttu_file","Files created" + file.getAbsolutePath());
            }
            catch (Exception e)
            {
                Log.e("uruttu_file","Files created" + e.getMessage());
            }

        }
    }

}
