package com.example.ytstream30;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

public class DownloadAct extends AppCompatActivity {

    ListView list;
    ImageView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        list = findViewById(R.id.downloads);
        list.setAdapter(new DownloadsAdapter(this));
    }
}