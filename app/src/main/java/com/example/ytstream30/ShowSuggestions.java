package com.example.ytstream30;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.widget.SearchView;

import com.google.android.exoplayer2.ExoPlayer;

import java.util.List;

public class ShowSuggestions implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    SearchView search;
    AutoCompleteTextView auto;
    Activity act;
    ExoPlayer player;
    SearchAdapter adapter;

    ShowSuggestions(Activity act, Menu menu) {
        search = (SearchView) menu.findItem(R.id.search).getActionView();
        auto = search.findViewById(androidx.appcompat.R.id.search_src_text);

        this.act = act;

        adapter = new SearchAdapter(act);

        auto.setHint("Search YouTube");
        auto.setDropDownBackgroundResource(R.color.white);
        auto.setThreshold(1);
        auto.setAdapter(adapter);
    }

    public void setPlayer(ExoPlayer player)
    {
        this.player = player;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        search.clearFocus();
        search.onActionViewCollapsed();
        auto.dismissDropDown();

        //if(this.player!=null) MainActivity.destroyPlayer();

        Intent intent = new Intent(this.act, SearchResultsAct.class);
        intent.putExtra(SearchResultsAct.SEARCH_QUERY, query);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        act.startActivity(intent);
        // act.finish();

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText.isEmpty()) return false;

        Log.e("uruttu_titles","Fetching titles");

        new Thread(new Runnable() {
            @Override
            public void run() {

                DataRetriever retriever = new DataRetriever(newText);
                List<String> titles = retriever.getTitlesList();

                Log.e("uruttu_titles", newText + ":" + titles.toString());

                if(titles.size()==0)
                {
                    return;
                }

                if(act.isDestroyed()) return;

                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addAll(titles);
                    }
                });
            }
        }).start();

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String title = (String) parent.getItemAtPosition(position);
        auto.setText(title);

        onQueryTextSubmit(title);
    }
}
