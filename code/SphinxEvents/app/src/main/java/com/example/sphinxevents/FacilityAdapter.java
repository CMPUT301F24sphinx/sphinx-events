/*
 * Class Name: FacilityAdapter
 * Date: 2024-10-25
 *
 * Description:
 * Adapter for facilities in list
 * Used when admin searches for facilities
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

import com.example.sphinxevents.Facility;

import java.util.ArrayList;

/**
 * Adapter for facilities in list
 * Used when admin searches for facilities
 */
public class FacilityAdapter extends ArrayAdapter<Facility> {

    /**
     * Constructor for FacilityAdapter
     * @param context the context as to where this adapter is used
     * @param facilities the array of facilities to display
     */
    public FacilityAdapter(@NonNull Context context, ArrayList<Facility> facilities) {
        super(context, 0, facilities);
    }

    /**
     * Displays the facilities within the list view
     * @param position index of the facility in the list
     * @param convertView reference to the existing view if it exists
     * @param parent parent view
     * @return view of facility
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_facility, parent, false);
        }
        else {
            view = convertView;
        }

        // Sets display of facility
        Facility facility = getItem(position);
        TextView facilityNameTextView = view.findViewById(R.id.facility_name_text_view);
        TextView facilityPhoneNumberTextView = view.findViewById(R.id.facility_phone_number_text_view);

        facilityNameTextView.setText(facility.getName());
        facilityPhoneNumberTextView.setText(facility.getPhoneNumber());

        return view;
    }
}
