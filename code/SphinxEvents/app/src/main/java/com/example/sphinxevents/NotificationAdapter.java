package com.example.sphinxevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NotificationAdapter extends BaseAdapter {

    Context context; // The context of Activity
    ArrayList<String> notificationMessages; // List of strings of messages user has
    ArrayList<String> senders; // The name of the events/organizers sending the message
    LayoutInflater inflter;

    /**
     * Constructor for the class
     * @param applicationContext // context of app activity
     * @param notificationMessages // List of strings of message text
     * @param senders // list of strings of names of sender events
     */
    public NotificationAdapter(Context applicationContext, ArrayList<String> notificationMessages, ArrayList<String> senders) {
        this.context = context;
        this.notificationMessages = notificationMessages;
        this.senders = senders;
        inflter = (LayoutInflater.from(applicationContext));
    }

    /**
     * Getter for the count of messages list
     * @return number of messages, 0 if no messages found
     */
    @Override
    public int getCount() {
        return notificationMessages != null ? notificationMessages.size() : 0;
    }

    /**
     * Getter for adapter item
     * @param i index
     * @return null
     */
    @Override
    public Object getItem(int i) {
        return null;
    }

    /**
     * getter got adapter item id
     * @param i index
     * @return 0
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Get the view of ith item and set the textviews for the notification
     * @param i index
     * @param view view
     * @param viewGroup viewgroup
     * @return constructed view with sender name and message
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.notification, null);
        TextView sender = (TextView) view.findViewById(R.id.notification_sender_testview);
        TextView message = (TextView) view.findViewById(R.id.notification_message_testview);
        message.setText(notificationMessages.get(i));
        sender.setText(senders.get(i));

        return view;
    }
}
