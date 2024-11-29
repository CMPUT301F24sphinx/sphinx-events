/*
 * Adapter for expandable lists that holds events
 */

package com.example.sphinxevents;


import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.sphinxevents.ExListAdapter;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Adapter for expandable lists that holds events
 */
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

        int layoutId;

        // Change the layoutId based on the groupPosition
        switch (groupPosition) {
            case 0:
                layoutId = R.layout.ex_list_event;  // Use a different layout for group 0
                break;
            case 1:
                layoutId = R.layout.ex_list_event;  // Use a different layout for group 1
                break;
            default:
                layoutId = R.layout.ex_list_created_event;  // Default layout if groupPosition doesn't match specific cases
                break;
        }


        if (convertView == null || (int) convertView.getId() != layoutId) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutId, parent, false);
        }
        else {
            view = convertView;
        }

        // Sets XML elements that are common in all type of events
        // TODO: Set other common elements
        TextView eventTextView = view.findViewById(R.id.event_name_text_view);
        eventTextView.setText(event.getName());


        //TODO: Set unique pending and joined event layout elements

        // Set unique created event layout elements
        if (groupPosition == 2) {
            // Sets display of number of entrants who joined lottery and the entrant limit
            TextView numberOfEntrantsTextView = view.findViewById(R.id.number_of_entrants_text_view);
            TextView limitOfEntrantsTextView = view.findViewById(R.id.limit_of_entrants_text_view);
            numberOfEntrantsTextView.setText(context.getString(R.string.number_of_entrants, event.getEventEntrants().size()));
            if (event.getEntrantLimit() == 0) {
                limitOfEntrantsTextView.setVisibility(View.GONE);
            }
            else {
                limitOfEntrantsTextView.setText(context.getString(R.string.entrant_limit, event.getEntrantLimit()));
            }

            setLotteryTime(view, event);  // Sets the time remaining until lottery
        }

        return view;
    }

    /**
     * Sets the lottery time display for the event
     * Sees if the lottery has been drawn
     * If yes, display proper message
     * Else display time remaining until lottery
     *
     * @param view the view of the event
     * @param event the Event object associated with the view
     */
    private void setLotteryTime(View view, Event event) {
        // Obtains relevant XML elements
        LinearLayout lotteryTimerLinearLayout = view.findViewById(R.id.lottery_timer_linear_layout);
        TextView lotteryTimeRemainingTextView = view.findViewById(R.id.lottery_time_remaining_text_view);
        ImageView clockImage = view.findViewById(R.id.clock_image_view);


        // TODO: Fix this logic in case things were changed in the Event class
        // Sets lottery time remaining to proper display
        if (event.wasLotteryDrawn()) {
            lotteryTimeRemainingTextView.setText(R.string.lottery_drawn);
            clockImage.setVisibility(View.GONE);
            lotteryTimerLinearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.green_rounded_border));
        }
        else {
            Date lotteryEndDate = event.getLotteryEndDate();
            Date currentDate = new Date();
            if (lotteryEndDate.compareTo(currentDate) <= 0) {  // Waiting for organizer to initiate lottery
                lotteryTimeRemainingTextView.setText(R.string.awaiting_lottery);
                clockImage.setVisibility(View.GONE);
            }
            else {  // Still time remaining until lottery
                long differenceInDays = (lotteryEndDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24);
                lotteryTimeRemainingTextView.setText(context.getString(R.string.days_until_lottery, differenceInDays));
            }
        }
    }
}
