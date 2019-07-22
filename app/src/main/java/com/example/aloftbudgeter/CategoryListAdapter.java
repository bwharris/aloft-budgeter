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
    final Integer[] mainActivityViews = new Integer[] {
            R.id.list_edit,
            R.id.list_diff
        };

    public CategoryListAdapter(Activity activity, List<Category> listItems) {
        super(activity, R.layout.list_item, listItems);
        this.activity = activity;
        this.listItems = listItems;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        View rowView = activity.getLayoutInflater().inflate(R.layout.list_item, null, true);
        ((TextView)rowView.findViewById(R.id.list_name)).setText(listItems.get(position).getName());

        if(parent.getId() != R.id.main_categories){
            for(Integer i: mainActivityViews){ rowView.findViewById(i).setVisibility(View.GONE); }
            rowView.findViewById(R.id.list_name).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        else{
            ((TextView)rowView.findViewById(R.id.list_name)).setText(
                    ((TextView)rowView.findViewById(R.id.list_name)).getText().toString() + ":"
                );
            ((TextView)rowView.findViewById(R.id.list_diff)).setText(String.valueOf(
                    listItems.get(position).getBudgetItemSum(false)
                            - listItems.get(position).getBudgetItemSum(true)
                ));
        }

        return rowView;
    }
}
