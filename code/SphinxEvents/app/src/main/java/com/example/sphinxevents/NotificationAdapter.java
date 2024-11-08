package com.example.sphinxevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NotificationAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> notificationMessages;
    LayoutInflater inflter;

    public NotificationAdapter(Context applicationContext, ArrayList<String> notificationMessages) {
        this.context = context;
        this.notificationMessages = notificationMessages;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return notificationMessages != null ? notificationMessages.size() : 0;
//        return notificationMessages.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.notification, null);
        TextView country = (TextView)           view.findViewById(R.id.textView);
        country.setText(notificationMessages.get(i));
        return view;
    }
}
