package com.example.sphinxevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Used in displaying the data of event entrants by an organizer
 */
public class EntrantExListAdapter extends ExListAdapter {

    EntrantExListAdapter(Context context, List<String> headers, Map<String, List<Entrant>> entrants) {
        super(context, headers, entrants);
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
        Entrant entrant = (Entrant) getChild(groupPosition, childPosition);

        int layoutId = R.layout.ex_list_entrant;

        if (convertView == null || (int) convertView.getId() != layoutId) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutId, parent, false);
        }
        else {
            view = convertView;
        }

        TextView entrantNameTextView = view.findViewById(R.id.entrant_name_text_view);
        TextView entrantEmailTextView = view.findViewById(R.id.entrant_data_email);
        TextView entrantPhoneTextView = view.findViewById(R.id.entrant_data_phone_number);

        entrantNameTextView.setText(entrant.getName());
        entrantEmailTextView.setText(entrant.getEmail());
        entrantPhoneTextView.setText(entrant.getPhoneNumber());

        return view;
    }
}
