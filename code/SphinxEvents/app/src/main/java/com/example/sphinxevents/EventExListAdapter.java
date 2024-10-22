
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

    @Override
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        View view;
        Event event = (Event) getChild(i, i1);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.ex_list_event, null);
        }
        else {
            view = convertView;
        }

        TextView eventTextView = view.findViewById(R.id.event_name_text_view);
        eventTextView.setText(event.getName());

        return view;
    }
}
