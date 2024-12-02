/*
 * Class Name: EventsAdapter
 * Date: 2024-11-25
 *
 * Description:
 * Adapter for events in list
 * Used when admin is searching for events
 *
 */

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

/**
 *  Adapter for events in list
 *  Used when admin is searching for events
 */
public class EventsAdapter extends ArrayAdapter<Event> {

    /**
     * Constructor for EventsAdapter
     * @param context the context as to where this adapter is used
     * @param events the array of events to display
     */
    public EventsAdapter(@NonNull Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    /**
     * Displays the events within the list view
     * @param position index of the event in the list
     * @param convertView reference to the existing view if it exists
     * @param parent parent view
     * @return view of event
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_event, parent, false);
        }
        else {
            view = convertView;
        }

        // Sets display of event
        Event event = getItem(position);
        TextView eventNameTextView = view.findViewById(R.id.event_name_text_view);
        TextView eventDescriptionTextView = view.findViewById(R.id.event_description_text_view);
        TextView eventEndDateTextView = view.findViewById(R.id.event_enddate_text_view);

        eventNameTextView.setText(event.getName());
        eventDescriptionTextView.setText(event.getDescription());
        eventEndDateTextView.setText(event.getLotteryEndDate().toString());


        return view;
    }
}
