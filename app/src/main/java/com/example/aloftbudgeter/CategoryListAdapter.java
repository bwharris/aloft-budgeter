package com.example.aloftbudgeter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class CategoryListAdapter extends ArrayAdapter<Category> {


    private final Activity activity;
    private final List<Category> listItems;

    public CategoryListAdapter(Activity activity, List<Category> listItems) {
        super(activity, R.layout.list_item, listItems);
        this.activity = activity;
        this.listItems = listItems;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = activity.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item, null, true);
        ((TextView)rowView.findViewById(R.id.item_name)).setText(listItems.get(position).getName());

        return rowView;
    }
}
