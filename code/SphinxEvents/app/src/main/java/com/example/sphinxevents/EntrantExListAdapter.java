/*
 * Class Name: EntrantExListAdapter
 * Date: 2024-11-27
 *
 * Description:
 * Adapter for Entrant objects in expandable lists
 * Displays the details of entrants
 *
 */


package com.example.sphinxevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Adapter for Entrant objects in expandable lists
 * Displays the details of entrants
 */
public class EntrantExListAdapter extends ExListAdapter {

    /**
     * Constructor for EntrantExListAdapter
     * @param context  context of where this adapter is used
     * @param headers  array of headers
     * @param entrants  map of headers and an array of entrants
     */
    EntrantExListAdapter(Context context, List<String> headers, Map<String, List<Entrant>> entrants) {
        super(context, headers, entrants);
    }

    /**
     * Displays the entrants within ex-list
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

        // Obtains UI elements
        TextView entrantNameTextView = view.findViewById(R.id.entrant_name_text_view);
        TextView entrantEmailTextView = view.findViewById(R.id.entrant_data_email);
        TextView entrantPhoneTextView = view.findViewById(R.id.entrant_data_phone_number);

        // Sets details of entrant
        entrantNameTextView.setText(entrant.getName());
        entrantEmailTextView.setText(entrant.getEmail());
        entrantPhoneTextView.setText(entrant.getPhoneNumber());

        return view;
    }
}
