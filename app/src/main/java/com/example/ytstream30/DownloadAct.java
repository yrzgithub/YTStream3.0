package com.example.ytstream30;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

public class DownloadAct extends AppCompatActivity {

    ListView list;
    ImageView image;
    ImageView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        list = findViewById(R.id.downloads);
        image = findViewById(R.id.none);

        DownloadsAdapter adapter = new DownloadsAdapter(this);

        if(adapter.getCount()>0)
        {
            show();
        }
        else
        {
            hide();
        }

        list.setAdapter(adapter);

    }

    public void show()
    {
        list.setVisibility(View.VISIBLE);
        image.setVisibility(View.GONE);
    }

    public void hide()
    {
        list.setVisibility(View.GONE);
        image.setVisibility(View.VISIBLE);
    }

}