/*
 * Adapter for listviews that hold events
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

public class EventsAdapter extends ArrayAdapter<Event> {

    public EventsAdapter(@NonNull Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }
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
