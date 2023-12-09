package com.example.ytstream30;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.widget.SearchView;

import java.util.concurrent.Executor;

public class ShowSuggestions implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener,Runnable {

    SearchView search;
    AutoCompleteTextView auto;
    ArrayAdapter<String> adapter;
    Thread thread;
    DataRetriever retriever;
    Activity act;
    String[] titles;
    Handler handler = new Handler(Looper.getMainLooper());

    ShowSuggestions(Activity act, Menu menu)
    {
        search = (SearchView) menu.findItem(R.id.search).getActionView();
        auto  = search.findViewById(androidx.appcompat.R.id.search_src_text);

        this.act = act;

        auto.setHint("Search YouTube");
        auto.setDropDownBackgroundResource(R.color.white);

        adapter = new ArrayAdapter<>(act, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,new String[0]);
    }

    public Thread updateAdapter(String query)
    {
        if(!query.isEmpty())
        {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    retriever = new DataRetriever(query);

                    titles = retriever.getTitles();



                    handler.post(this);
                }
            });

            thread.start();

            return thread;
        }

        return null;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        search.clearFocus();
        search.onActionViewCollapsed();
        auto.dismissDropDown();

        Thread thread = updateAdapter(query);

        try
        {
            assert thread!=null;
            thread.join();

            // for Now
            Intent intent = new Intent(this.act,MainActivity.class);
            intent.putExtra(MainActivity.song_serializable,query);
            act.startActivity(intent);
        }

        catch (Error | Exception e) {
            Log.e("uruttu_show_suggestions",e.getMessage());
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        updateAdapter(newText);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String title = (String) parent.getItemAtPosition(position);
        auto.setText(title);
    }

    @Override
    public void run() {
        if(titles.length>0)
        {
            adapter.clear();
            adapter.addAll(titles);
        }

        auto.setAdapter(adapter);
    }
}
