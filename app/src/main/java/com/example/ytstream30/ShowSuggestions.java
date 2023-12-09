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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class ShowSuggestions implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    SearchView search;
    AutoCompleteTextView auto;
    Activity act;

    ShowSuggestions(Activity act, Menu menu) {
        search = (SearchView) menu.findItem(R.id.search).getActionView();
        auto = search.findViewById(androidx.appcompat.R.id.search_src_text);

        this.act = act;

        auto.setHint("Search YouTube");
        auto.setDropDownBackgroundResource(R.color.white);
        auto.setThreshold(1);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        search.clearFocus();
        search.onActionViewCollapsed();
        auto.dismissDropDown();

        Intent intent = new Intent(this.act, SearchResultsAct.class);
        intent.putExtra(SearchResultsAct.search_query, query);
        act.startActivity(intent);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText.isEmpty()) return false;

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

                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        auto.setAdapter(new ArrayAdapter<String>(act,androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,titles));
                        if(!auto.isPopupShowing()) auto.showDropDown();
                    }
                });
            }
        }).start();

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String title = (String) parent.getItemAtPosition(position);
        auto.setText(title);
    }
}
