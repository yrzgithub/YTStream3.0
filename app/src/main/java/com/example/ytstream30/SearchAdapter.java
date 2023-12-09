package com.example.ytstream30;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchAdapter extends BaseAdapter implements Filterable {

    List<String> suggestions = Collections.synchronizedList(new ArrayList<>());
    Activity act;

    SearchAdapter(Activity act,List<String> titles)
    {
        this(act);
        addAll(titles);
    }

    SearchAdapter(Activity act)
    {
        this.act = act;
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public String getItem(int position) {
        return suggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView suggestion;

        if(convertView==null)
        {
            suggestion = new TextView(act);
            suggestion.setText(suggestions.get(position));
        }
        else
        {
            suggestion = (TextView) convertView;
        }

        return suggestion;
    }

    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                results.values = (List<String>) suggestions;
                results.count = suggestions.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results.count>0)
                {
                    suggestions.clear();
                    suggestions.addAll((List<String>) results.values);
                }
            }
        };
    }

    public void addAll(List<String> titles)
    {
        suggestions.clear();
        suggestions.addAll(titles);
        if(titles.size()>0) notifyDataSetChanged();
    }
}
