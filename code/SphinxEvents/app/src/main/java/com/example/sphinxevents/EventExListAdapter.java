/*
 * Adapter for expandable lists that holds events
 */

package com.example.sphinxevents;


import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
                layoutId = R.layout.ex_list_joined_event;
                break;
            case 1:
                layoutId = R.layout.ex_list_pending_event;
                break;
            default:
                layoutId = R.layout.ex_list_created_event;
                break;
        }


        if (convertView == null || !convertView.getTag().equals(layoutId)) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutId, parent, false);
            view.setTag(layoutId);
        }
        else {
            view = convertView;
        }

        TextView eventNameTextView = view.findViewById(R.id.event_name_text_view);
        TextView eventDescriptionTextView = view.findViewById(R.id.event_description_text_view);
        eventNameTextView.setText(event.getName());
        eventDescriptionTextView.setText(event.getDescription());

        setLotteryIndicator(view, event);  // Sets the time remaining until lottery

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
    private void setLotteryIndicator(View view, Event event) {
        // Obtains relevant XML elements
        LinearLayout lotteryTimerLinearLayout = view.findViewById(R.id.lottery_timer_linear_layout);
        TextView lotteryTimeRemainingTextView = view.findViewById(R.id.lottery_time_remaining_text_view);
        ImageView clockImage = view.findViewById(R.id.clock_image_view);

        Date lotteryEndDate = event.getLotteryEndDate();
        Date currentDate = new Date();
        if (event.canLotteryBeDrawn() || event.getLotteryWasDrawn()) {  // Waiting for organizer to initiate lottery
            clockImage.setVisibility(View.GONE);
            lotteryTimeRemainingTextView.setText(R.string.lottery_ongoing);
        }
        else {  // Still time remaining until lottery
            long differenceInDays = (lotteryEndDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24);
            lotteryTimeRemainingTextView.setText(context.getString(R.string.days_until_lottery, differenceInDays));
        }
    }
}