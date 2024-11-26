package com.example.sphinxevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NotificationsAdapter extends ArrayAdapter<Notification> {

    /**
     * Constructor for the class
     * @param context Context of the app activity
     * @param notifications List of notifications to display
     */
    public NotificationsAdapter(Context context, ArrayList<Notification> notifications) {
        // Pass the context and notifications list to the parent class
        super(context, 0, notifications);
    }

    /**
     * Provides a view for an individual item in the ListView.
     *
     * @param i the position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent the parent that this view will be attached to.
     * @return The view corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int i, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.notification_content, parent, false);
        } else {
            view = convertView;
        }

        Notification notification = getItem(i);

        TextView mainHeader = view.findViewById(R.id.notification_main_textview);
        TextView subHeader = view.findViewById(R.id.notification_preview_textview);

        mainHeader.setText(notification.getEventName());

        // Get the message and trim to 20 characters, adding ellipses if necessary
        String message = notification.getMessage();
        if (message != null && message.length() > 20) {
            String truncatedMessage = message.substring(0, 20);
            subHeader.setText(view.getContext().getString(R.string.notification_msg_preview, truncatedMessage));
        } else {
            subHeader.setText(message); // Use the full message if it's shorter than 20 characters
        }

        return view;
    }
}

