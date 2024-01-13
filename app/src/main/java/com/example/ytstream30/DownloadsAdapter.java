package com.example.ytstream30;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DownloadsAdapter extends BaseAdapter {

    static List<Song> songs = new ArrayList<>();
    Activity activity;

    DownloadsAdapter(Activity activity)
    {
        this.activity = activity;
    }

    public static void addSong(Song song)
    {
        songs.add(song);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Song getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            convertView = activity.getLayoutInflater().inflate(R.layout.custom_download_list,null);

            TextView title = convertView.findViewById(R.id.download_title);

            TextView progressPercentage = convertView.findViewById(R.id.progress_txt);
            ProgressBar progressBar = convertView.findViewById(R.id.progress);

            Song song = songs.get(position);

            String title_ = song.getTitle();
            title.setText(title_);

            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    int progressPercentage_ =  song.getProgressPercent();
                    progressPercentage.setText(progressPercentage_ + "%");

                    progressBar.setProgress(progressPercentage_,true);

                    if(!song.isDownloading())
                    {
                        return;
                    }

                    if(song.isError())
                    {
                        progressPercentage.setTextColor(Color.RED);
                        progressPercentage.setText("Download Failed");
                    }

                    handler.postDelayed(this,1000);
                }
            });
        }

        return convertView;
    }
}
