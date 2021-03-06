package com.andrew.bergfeld.snackbar.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ExampleAdapter extends ArrayAdapter<Object> {

    private Context mContext;

    public ExampleAdapter(Context context) {
        super(context, -1, new ArrayList<Object>());
        mContext = context;
    }

    @Override
    public int getCount() {
        return 100;
    }

    @Override
    public Object getItem(int position) {
        return new Object();
    }

    @Override
    public long getItemId(int position) {
        return 100;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.two_line_list_item, parent, false);
        }

        //ToDo - ViewHolding
        TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
        TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);

        text1.setText("List Item: " + position);
        text2.setText("SubText: " + position);

        return convertView;
    }

}
