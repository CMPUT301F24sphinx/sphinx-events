
package com.example.sphinxevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class EventExListAdapter extends ExListAdapter {

    EventExListAdapter(Context context, List<String> headers, Map<String, List<Event>> events) {
        super(context, headers, events);
    }


    /**
     * Displays the events within ex-list
     *
     * @param groupPosition index of group
     * @param childPosition index of event within group
     * @param isExpanded boolean of whether list is expanded
     * @param convertView reference to existing view if exists
     * @param parent parent view
     * @return view of event
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view;
        Event event = (Event) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.ex_list_event, parent, false);
        }
        else {
            view = convertView;
        }

        TextView eventTextView = view.findViewById(R.id.event_name_text_view);
        eventTextView.setText(event.getName());

        return view;
    }
}
